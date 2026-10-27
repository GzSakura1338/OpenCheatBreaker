package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;

public class PacketBuffer extends ByteBuf {
   public ByteBuf buf;

   public PacketBuffer(ByteBuf wrapped) {
      this.buf = wrapped;
   }

   public static int getVarIntSize(int input) {
      for (int i = 1; i < 5; i++) {
         if ((input & -1 << i * 7) == 0) {
            return i;
         }
      }

      return 5;
   }

   public void writeByteArray(byte[] array) {
      this.writeVarIntToBuffer(array.length);
      this.writeBytes(array);
   }

   public byte[] readByteArray() {
      byte[] bytes = new byte[this.readVarIntFromBuffer()];
      this.readBytes(bytes);
      return bytes;
   }

   public BlockPos readBlockPos() {
      return BlockPos.fromLong(this.readLong());
   }

   public void writeBlockPos(BlockPos pos) {
      this.writeLong(pos.toLong());
   }

   public IChatComponent readChatComponent() throws IOException {
      return Serializer.jsonToComponent(this.readStringFromBuffer(32767));
   }

   public void writeChatComponent(IChatComponent component) throws IOException {
      this.writeString(Serializer.componentToJson(component));
   }

   public <T extends Enum<T>> T readEnumValue(Class<T> enumClass) {
      return (T)enumClass.getEnumConstants()[this.readVarIntFromBuffer()];
   }

   public void writeEnumValue(Enum<?> value) {
      this.writeVarIntToBuffer(value.ordinal());
   }

   public int readVarIntFromBuffer() {
      int n = 0;
      int n2 = 0;

      byte b;
      do {
         b = this.readByte();
         n |= (b & 127) << n2++ * 7;
         if (n2 > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while ((b & 128) == 128);

      return n;
   }

   public long readVarLong() {
      long l = 0L;
      int n = 0;

      byte b;
      do {
         b = this.readByte();
         l |= (long)(b & 127) << n++ * 7;
         if (n > 10) {
            throw new RuntimeException("VarLong too big");
         }
      } while ((b & 128) == 128);

      return l;
   }

   public void writeUuid(UUID uuid) {
      this.writeLong(uuid.getMostSignificantBits());
      this.writeLong(uuid.getLeastSignificantBits());
   }

   public UUID readUuid() {
      return new UUID(this.readLong(), this.readLong());
   }

   public void writeVarIntToBuffer(int input) {
      while ((input & -128) != 0) {
         this.writeByte(input & 127 | 128);
         input >>>= 7;
      }

      this.writeByte(input);
   }

   public void writeVarLong(long value) {
      while ((value & -128L) != 0L) {
         this.writeByte((int)(value & 127L) | 128);
         value >>>= 7;
      }

      this.writeByte((int)value);
   }

   public void writeNBTTagCompoundToBuffer(NBTTagCompound nbt) {
      if (nbt == null) {
         this.writeByte(0);
      } else {
         try {
            CompressedStreamTools.write(nbt, new ByteBufOutputStream(this));
         } catch (IOException e) {
            throw new EncoderException(e);
         }
      }
   }

   public NBTTagCompound readNBTTagCompoundFromBuffer() throws IOException {
      int readerIndex = this.readerIndex();
      byte readByte = this.readByte();
      if (readByte == 0) {
         return null;
      }

      this.readerIndex(readerIndex);
      return CompressedStreamTools.read(new ByteBufInputStream(this), new NBTSizeTracker(2097152L));
   }

   public void writeItemStackToBuffer(ItemStack stack) {
      if (stack == null) {
         this.writeShort(-1);
      } else {
         this.writeShort(Item.getIdFromItem(stack.getItem()));
         this.writeByte(stack.stackSize);
         this.writeShort(stack.getMetadata());
         NBTTagCompound nbttagCompound = null;
         if (stack.getItem().isDamageable() || stack.getItem().getShareTag()) {
            nbttagCompound = stack.getTagCompound();
         }

         this.writeNBTTagCompoundToBuffer(nbttagCompound);
      }
   }

   public ItemStack readItemStackFromBuffer() throws IOException {
      ItemStack itemStack = null;
      short readShort = this.readShort();
      if (readShort >= 0) {
         byte readByte = this.readByte();
         short readShort2 = this.readShort();
         itemStack = new ItemStack(Item.getItemById(readShort), readByte, readShort2);
         itemStack.setTagCompound(this.readNBTTagCompoundFromBuffer());
      }

      return itemStack;
   }

   public String readStringFromBuffer(int maxLength) {
      int computeInt2 = computeInt(maxLength);
      int readVarIntFromBuffer = this.readVarIntFromBuffer();
      if (readVarIntFromBuffer > computeInt2) {
         throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + readVarIntFromBuffer + " > " + computeInt2 + ")");
      } else if (readVarIntFromBuffer < 0) {
         throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         String str = this.toString(this.readerIndex(), readVarIntFromBuffer, StandardCharsets.UTF_8);
         this.readerIndex(this.readerIndex() + readVarIntFromBuffer);
         if (str.length() > maxLength) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + str.length() + " > " + maxLength + ")");
         } else {
            return str;
         }
      }
   }

   public PacketBuffer writeString(String string) {
      if (string.length() > 32767) {
         throw new EncoderException("String too big (was " + string.length() + " characters, max " + 32767 + ")");
      }

      byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
      int computeInt2 = computeInt(32767);
      if (bytes.length > computeInt2) {
         throw new EncoderException("String too big (was " + bytes.length + " bytes encoded, max " + computeInt2 + ")");
      }

      this.writeVarIntToBuffer(bytes.length);
      this.writeBytes(bytes);
      return this;
   }

   public int capacity() {
      return this.buf.capacity();
   }

   public ByteBuf capacity(int n) {
      return this.buf.capacity(n);
   }

   public int maxCapacity() {
      return this.buf.maxCapacity();
   }

   public ByteBufAllocator alloc() {
      return this.buf.alloc();
   }

   public ByteOrder order() {
      return this.buf.order();
   }

   public ByteBuf order(ByteOrder byteOrder) {
      return this.buf.order(byteOrder);
   }

   public ByteBuf unwrap() {
      return this.buf.unwrap();
   }

   public boolean isDirect() {
      return this.buf.isDirect();
   }

   public int readerIndex() {
      return this.buf.readerIndex();
   }

   public ByteBuf readerIndex(int n) {
      return this.buf.readerIndex(n);
   }

   public int writerIndex() {
      return this.buf.writerIndex();
   }

   public ByteBuf writerIndex(int n) {
      return this.buf.writerIndex(n);
   }

   public ByteBuf setIndex(int n, int n2) {
      return this.buf.setIndex(n, n2);
   }

   public int readableBytes() {
      return this.buf.readableBytes();
   }

   public int writableBytes() {
      return this.buf.writableBytes();
   }

   public int maxWritableBytes() {
      return this.buf.maxWritableBytes();
   }

   public boolean isReadable() {
      return this.buf.isReadable();
   }

   public boolean isReadable(int n) {
      return this.buf.isReadable(n);
   }

   public boolean isWritable() {
      return this.buf.isWritable();
   }

   public boolean isWritable(int n) {
      return this.buf.isWritable(n);
   }

   public ByteBuf clear() {
      return this.buf.clear();
   }

   public ByteBuf markReaderIndex() {
      return this.buf.markReaderIndex();
   }

   public ByteBuf resetReaderIndex() {
      return this.buf.resetReaderIndex();
   }

   public ByteBuf markWriterIndex() {
      return this.buf.markWriterIndex();
   }

   public ByteBuf resetWriterIndex() {
      return this.buf.resetWriterIndex();
   }

   public ByteBuf discardReadBytes() {
      return this.buf.discardReadBytes();
   }

   public ByteBuf discardSomeReadBytes() {
      return this.buf.discardSomeReadBytes();
   }

   public ByteBuf ensureWritable(int n) {
      return this.buf.ensureWritable(n);
   }

   public int ensureWritable(int n, boolean flag) {
      return this.buf.ensureWritable(n, flag);
   }

   public boolean getBoolean(int n) {
      return this.buf.getBoolean(n);
   }

   public byte getByte(int n) {
      return this.buf.getByte(n);
   }

   public short getUnsignedByte(int n) {
      return this.buf.getUnsignedByte(n);
   }

   public short getShort(int n) {
      return this.buf.getShort(n);
   }

   public int getUnsignedShort(int n) {
      return this.buf.getUnsignedShort(n);
   }

   public int getMedium(int n) {
      return this.buf.getMedium(n);
   }

   public int getUnsignedMedium(int n) {
      return this.buf.getUnsignedMedium(n);
   }

   public int getInt(int n) {
      return this.buf.getInt(n);
   }

   public long getUnsignedInt(int n) {
      return this.buf.getUnsignedInt(n);
   }

   public long getLong(int n) {
      return this.buf.getLong(n);
   }

   public char getChar(int n) {
      return this.buf.getChar(n);
   }

   public float getFloat(int n) {
      return this.buf.getFloat(n);
   }

   public double getDouble(int n) {
      return this.buf.getDouble(n);
   }

   public ByteBuf getBytes(int n, ByteBuf byteBuf) {
      return this.buf.getBytes(n, byteBuf);
   }

   public ByteBuf getBytes(int n, ByteBuf byteBuf, int n2) {
      return this.buf.getBytes(n, byteBuf, n2);
   }

   public ByteBuf getBytes(int n, ByteBuf byteBuf, int n2, int n3) {
      return this.buf.getBytes(n, byteBuf, n2, n3);
   }

   public ByteBuf getBytes(int n, byte[] bytes) {
      return this.buf.getBytes(n, bytes);
   }

   public ByteBuf getBytes(int n, byte[] bytes, int n2, int n3) {
      return this.buf.getBytes(n, bytes, n2, n3);
   }

   public ByteBuf getBytes(int n, ByteBuffer byteBuffer) {
      return this.buf.getBytes(n, byteBuffer);
   }

   public ByteBuf getBytes(int n, OutputStream outputStream, int n2) throws IOException {
      return this.buf.getBytes(n, outputStream, n2);
   }

   public int getBytes(int n, GatheringByteChannel gatheringByteChannel, int n2) throws IOException {
      return this.buf.getBytes(n, gatheringByteChannel, n2);
   }

   public ByteBuf setBoolean(int n, boolean flag) {
      return this.buf.setBoolean(n, flag);
   }

   public ByteBuf setByte(int n, int n2) {
      return this.buf.setByte(n, n2);
   }

   public ByteBuf setShort(int n, int n2) {
      return this.buf.setShort(n, n2);
   }

   public ByteBuf setMedium(int n, int n2) {
      return this.buf.setMedium(n, n2);
   }

   public ByteBuf setInt(int n, int n2) {
      return this.buf.setInt(n, n2);
   }

   public ByteBuf setLong(int n, long l) {
      return this.buf.setLong(n, l);
   }

   public ByteBuf setChar(int n, int n2) {
      return this.buf.setChar(n, n2);
   }

   public ByteBuf setFloat(int n, float f) {
      return this.buf.setFloat(n, f);
   }

   public ByteBuf setDouble(int n, double d) {
      return this.buf.setDouble(n, d);
   }

   public ByteBuf setBytes(int n, ByteBuf byteBuf) {
      return this.buf.setBytes(n, byteBuf);
   }

   public ByteBuf setBytes(int n, ByteBuf byteBuf, int n2) {
      return this.buf.setBytes(n, byteBuf, n2);
   }

   public ByteBuf setBytes(int n, ByteBuf byteBuf, int n2, int n3) {
      return this.buf.setBytes(n, byteBuf, n2, n3);
   }

   public ByteBuf setBytes(int n, byte[] bytes) {
      return this.buf.setBytes(n, bytes);
   }

   public ByteBuf setBytes(int n, byte[] bytes, int n2, int n3) {
      return this.buf.setBytes(n, bytes, n2, n3);
   }

   public ByteBuf setBytes(int n, ByteBuffer byteBuffer) {
      return this.buf.setBytes(n, byteBuffer);
   }

   public int setBytes(int n, InputStream inputStream, int n2) throws IOException {
      return this.buf.setBytes(n, inputStream, n2);
   }

   public int setBytes(int n, ScatteringByteChannel scatteringByteChannel, int n2) throws IOException {
      return this.buf.setBytes(n, scatteringByteChannel, n2);
   }

   public ByteBuf setZero(int n, int n2) {
      return this.buf.setZero(n, n2);
   }

   public boolean readBoolean() {
      return this.buf.readBoolean();
   }

   public byte readByte() {
      return this.buf.readByte();
   }

   public short readUnsignedByte() {
      return this.buf.readUnsignedByte();
   }

   public short readShort() {
      return this.buf.readShort();
   }

   public int readUnsignedShort() {
      return this.buf.readUnsignedShort();
   }

   public int readMedium() {
      return this.buf.readMedium();
   }

   public int readUnsignedMedium() {
      return this.buf.readUnsignedMedium();
   }

   public int readInt() {
      return this.buf.readInt();
   }

   public long readUnsignedInt() {
      return this.buf.readUnsignedInt();
   }

   public long readLong() {
      return this.buf.readLong();
   }

   public char readChar() {
      return this.buf.readChar();
   }

   public float readFloat() {
      return this.buf.readFloat();
   }

   public double readDouble() {
      return this.buf.readDouble();
   }

   public ByteBuf readBytes(int n) {
      return this.buf.readBytes(n);
   }

   public ByteBuf readSlice(int n) {
      return this.buf.readSlice(n);
   }

   public ByteBuf readBytes(ByteBuf byteBuf) {
      return this.buf.readBytes(byteBuf);
   }

   public ByteBuf readBytes(ByteBuf byteBuf, int n) {
      return this.buf.readBytes(byteBuf, n);
   }

   public ByteBuf readBytes(ByteBuf byteBuf, int n, int n2) {
      return this.buf.readBytes(byteBuf, n, n2);
   }

   public ByteBuf readBytes(byte[] bytes) {
      return this.buf.readBytes(bytes);
   }

   public ByteBuf readBytes(byte[] bytes, int n, int n2) {
      return this.buf.readBytes(bytes, n, n2);
   }

   public ByteBuf readBytes(ByteBuffer byteBuffer) {
      return this.buf.readBytes(byteBuffer);
   }

   public ByteBuf readBytes(OutputStream outputStream, int n) throws IOException {
      return this.buf.readBytes(outputStream, n);
   }

   public int readBytes(GatheringByteChannel gatheringByteChannel, int n) throws IOException {
      return this.buf.readBytes(gatheringByteChannel, n);
   }

   public ByteBuf skipBytes(int n) {
      return this.buf.skipBytes(n);
   }

   public ByteBuf writeBoolean(boolean flag) {
      return this.buf.writeBoolean(flag);
   }

   public ByteBuf writeByte(int n) {
      return this.buf.writeByte(n);
   }

   public ByteBuf writeShort(int n) {
      return this.buf.writeShort(n);
   }

   public ByteBuf writeMedium(int n) {
      return this.buf.writeMedium(n);
   }

   public ByteBuf writeInt(int n) {
      return this.buf.writeInt(n);
   }

   public ByteBuf writeLong(long l) {
      return this.buf.writeLong(l);
   }

   public ByteBuf writeChar(int n) {
      return this.buf.writeChar(n);
   }

   public ByteBuf writeFloat(float f) {
      return this.buf.writeFloat(f);
   }

   public ByteBuf writeDouble(double d) {
      return this.buf.writeDouble(d);
   }

   public ByteBuf writeBytes(ByteBuf byteBuf) {
      return this.buf.writeBytes(byteBuf);
   }

   public ByteBuf writeBytes(ByteBuf byteBuf, int n) {
      return this.buf.writeBytes(byteBuf, n);
   }

   public ByteBuf writeBytes(ByteBuf byteBuf, int n, int n2) {
      return this.buf.writeBytes(byteBuf, n, n2);
   }

   public ByteBuf writeBytes(byte[] bytes) {
      return this.buf.writeBytes(bytes);
   }

   public ByteBuf writeBytes(byte[] bytes, int n, int n2) {
      return this.buf.writeBytes(bytes, n, n2);
   }

   public ByteBuf writeBytes(ByteBuffer byteBuffer) {
      return this.buf.writeBytes(byteBuffer);
   }

   public int writeBytes(InputStream inputStream, int n) throws IOException {
      return this.buf.writeBytes(inputStream, n);
   }

   public int writeBytes(ScatteringByteChannel scatteringByteChannel, int n) throws IOException {
      return this.buf.writeBytes(scatteringByteChannel, n);
   }

   public ByteBuf writeZero(int n) {
      return this.buf.writeZero(n);
   }

   public int indexOf(int n, int n2, byte b) {
      return this.buf.indexOf(n, n2, b);
   }

   public int bytesBefore(byte b) {
      return this.buf.bytesBefore(b);
   }

   public int bytesBefore(int n, byte b) {
      return this.buf.bytesBefore(n, b);
   }

   public int bytesBefore(int n, int n2, byte b) {
      return this.buf.bytesBefore(n, n2, b);
   }

   public ByteBuf copy() {
      return this.buf.copy();
   }

   public ByteBuf copy(int n, int n2) {
      return this.buf.copy(n, n2);
   }

   public ByteBuf slice() {
      return this.buf.slice();
   }

   public ByteBuf slice(int n, int n2) {
      return this.buf.slice(n, n2);
   }

   public ByteBuf duplicate() {
      return this.buf.duplicate();
   }

   public int nioBufferCount() {
      return this.buf.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      return this.buf.nioBuffer();
   }

   public ByteBuffer nioBuffer(int n, int n2) {
      return this.buf.nioBuffer(n, n2);
   }

   public ByteBuffer internalNioBuffer(int n, int n2) {
      return this.buf.internalNioBuffer(n, n2);
   }

   public ByteBuffer[] nioBuffers() {
      return this.buf.nioBuffers();
   }

   public ByteBuffer[] nioBuffers(int n, int n2) {
      return this.buf.nioBuffers(n, n2);
   }

   public boolean hasArray() {
      return this.buf.hasArray();
   }

   public byte[] array() {
      return this.buf.array();
   }

   public int arrayOffset() {
      return this.buf.arrayOffset();
   }

   public boolean hasMemoryAddress() {
      return this.buf.hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.buf.memoryAddress();
   }

   public String toString(Charset charset) {
      return this.buf.toString(charset);
   }

   public String toString(int n, int n2, Charset charset) {
      return this.buf.toString(n, n2, charset);
   }

   public int hashCode() {
      return this.buf.hashCode();
   }

   public boolean equals(Object obj) {
      return this.buf.equals(obj);
   }

   public int compareTo(ByteBuf byteBuf) {
      return this.buf.compareTo(byteBuf);
   }

   public String toString() {
      return this.buf.toString();
   }

   public ByteBuf retain(int n) {
      return this.buf.retain(n);
   }

   public ByteBuf retain() {
      return this.buf.retain();
   }

   public int refCnt() {
      return this.buf.refCnt();
   }

   public boolean release() {
      return this.buf.release();
   }

   public boolean release(int n) {
      return this.buf.release(n);
   }

   public int writeBytes(FileChannel fileChannel, long l, int n) throws IOException {
      return this.buf.writeBytes(fileChannel, l, n);
   }

   public CharSequence getCharSequence(int n, int n2, Charset charset) {
      return this.buf.getCharSequence(n, n2, charset);
   }

   public ByteBuf asReadOnly() {
      return this.buf.asReadOnly();
   }

   public int readIntLE() {
      return this.buf.readIntLE();
   }

   public long getLongLE(int n) {
      return this.buf.getLongLE(n);
   }

   public ByteBuf readRetainedSlice(int n) {
      return this.buf.readRetainedSlice(n);
   }

   public long readUnsignedIntLE() {
      return this.buf.readUnsignedIntLE();
   }

   public int readBytes(FileChannel fileChannel, long l, int n) throws IOException {
      return this.buf.readBytes(fileChannel, l, n);
   }

   public ByteBuf writeFloatLE(float f) {
      return this.buf.writeFloatLE(f);
   }

   public ByteBuf touch() {
      return this.buf.touch();
   }

   public ByteBuf setLongLE(int n, long l) {
      return this.buf.setLongLE(n, l);
   }

   public short getShortLE(int n) {
      return this.buf.getShortLE(n);
   }

   public ByteBuf touch(Object obj) {
      return this.buf.touch(obj);
   }

   public boolean isReadOnly() {
      return this.buf.isReadOnly();
   }

   public int getUnsignedMediumLE(int n) {
      return this.buf.getUnsignedMediumLE(n);
   }

   public ByteBuf writeMediumLE(int n) {
      return this.buf.writeMediumLE(n);
   }

   public int setCharSequence(int n, CharSequence charSequence, Charset charset) {
      return this.buf.setCharSequence(n, charSequence, charset);
   }

   public ByteBuf setIntLE(int n, int n2) {
      return this.buf.setIntLE(n, n2);
   }

   public double readDoubleLE() {
      return this.buf.readDoubleLE();
   }

   public ByteBuf writeDoubleLE(double d) {
      return this.buf.writeDoubleLE(d);
   }

   public int maxFastWritableBytes() {
      return this.buf.maxFastWritableBytes();
   }

   public int getMediumLE(int n) {
      return this.buf.getMediumLE(n);
   }

   public int forEachByte(int n, int n2, ByteProcessor byteProcessor) {
      return this.buf.forEachByte(n, n2, byteProcessor);
   }

   public int readMediumLE() {
      return this.buf.readMediumLE();
   }

   public int forEachByteDesc(ByteProcessor byteProcessor) {
      return this.buf.forEachByteDesc(byteProcessor);
   }

   public static int computeInt(int n) {
      return n * 3;
   }

   public int forEachByte(ByteProcessor byteProcessor) {
      return this.buf.forEachByte(byteProcessor);
   }

   public ByteBuf retainedSlice() {
      return this.buf.retainedSlice();
   }

   public int getIntLE(int n) {
      return this.buf.getIntLE(n);
   }

   public int readUnsignedShortLE() {
      return this.buf.readUnsignedShortLE();
   }

   public ByteBuf writeLongLE(long l) {
      return this.buf.writeLongLE(l);
   }

   public boolean isContiguous() {
      return this.buf.isContiguous();
   }

   public ByteBuf writeShortLE(int n) {
      return this.buf.writeShortLE(n);
   }

   public ByteBuf asByteBuf() {
      return this.buf.asByteBuf();
   }

   public ByteBuf retainedSlice(int n, int n2) {
      return this.buf.retainedSlice(n, n2);
   }

   public int setBytes(int n, FileChannel fileChannel, long l, int n2) throws IOException {
      return this.buf.setBytes(n, fileChannel, l, n2);
   }

   public int forEachByteDesc(int n, int n2, ByteProcessor byteProcessor) {
      return this.buf.forEachByteDesc(n, n2, byteProcessor);
   }

   public ByteBuf setShortLE(int n, int n2) {
      return this.buf.setShortLE(n, n2);
   }

   public long readLongLE() {
      return this.buf.readLongLE();
   }

   public double getDoubleLE(int n) {
      return this.buf.getDoubleLE(n);
   }

   public int writeCharSequence(CharSequence charSequence, Charset charset) {
      return this.buf.writeCharSequence(charSequence, charset);
   }

   public int getUnsignedShortLE(int n) {
      return this.buf.getUnsignedShortLE(n);
   }

   public ByteBuf writeIntLE(int n) {
      return this.buf.writeIntLE(n);
   }

   public ByteBuf setFloatLE(int n, float f) {
      return this.buf.setFloatLE(n, f);
   }

   public ByteBuf retainedDuplicate() {
      return this.buf.retainedDuplicate();
   }

   public float readFloatLE() {
      return this.buf.readFloatLE();
   }

   public int readUnsignedMediumLE() {
      return this.buf.readUnsignedMediumLE();
   }

   public ByteBuf setDoubleLE(int n, double d) {
      return this.buf.setDoubleLE(n, d);
   }

   public float getFloatLE(int n) {
      return this.buf.getFloatLE(n);
   }

   public short readShortLE() {
      return this.buf.readShortLE();
   }

   public int getBytes(int n, FileChannel fileChannel, long l, int n2) throws IOException {
      return this.buf.getBytes(n, fileChannel, l, n2);
   }

   public long getUnsignedIntLE(int n) {
      return this.buf.getUnsignedIntLE(n);
   }

   public CharSequence readCharSequence(int n, Charset charset) {
      return this.buf.readCharSequence(n, charset);
   }

   public ByteBuf setMediumLE(int n, int n2) {
      return this.buf.setMediumLE(n, n2);
   }
}
