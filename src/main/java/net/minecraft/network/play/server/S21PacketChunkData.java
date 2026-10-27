package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class S21PacketChunkData implements Packet<INetHandlerPlayClient> {
   public int chunkX;
   public int chunkZ;
   public boolean isFullChunk;
   public S21PacketChunkData.Extracted extractedData;

   public S21PacketChunkData() {
   }

   public S21PacketChunkData(Chunk chunkIn, boolean flag, int n) {
      this.chunkX = chunkIn.xPosition;
      this.chunkZ = chunkIn.zPosition;
      this.isFullChunk = flag;
      this.extractedData = getExtractedData(chunkIn, flag, !chunkIn.getWorld().provider.getHasNoSky(), n);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.chunkX = buf.readInt();
      this.chunkZ = buf.readInt();
      this.isFullChunk = buf.readBoolean();
      this.extractedData = new S21PacketChunkData.Extracted();
      this.extractedData.dataSize = buf.readShort();
      this.extractedData.data = buf.readByteArray();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.chunkX);
      buf.writeInt(this.chunkZ);
      buf.writeBoolean(this.isFullChunk);
      buf.writeShort((short)(this.extractedData.dataSize & 65535));
      buf.writeByteArray(this.extractedData.data);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleChunkData(this);
   }

   public byte[] getExtractedDataBytes() {
      return this.extractedData.data;
   }

   public static int computeInt(int n, boolean flag, boolean flag2) {
      int n2 = n * 2 * 16 * 16 * 16;
      int n3 = n * 16 * 16 * 16 / 2;
      int n4 = flag ? n * 16 * 16 * 16 / 2 : 0;
      int n5 = flag2 ? 256 : 0;
      return n2 + n3 + n4 + n5;
   }

   public static S21PacketChunkData.Extracted getExtractedData(Chunk chunk, boolean flag, boolean flag2, int n) {
      ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
      S21PacketChunkData.Extracted extracted = new S21PacketChunkData.Extracted();
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < blockStorageArray.length; i++) {
         ExtendedBlockStorage extendedBlockStorage = blockStorageArray[i];
         if (extendedBlockStorage != null && (!flag || !extendedBlockStorage.isEmpty()) && (n & 1 << i) != 0) {
            extracted.dataSize |= 1 << i;
            newArrayList.add(extendedBlockStorage);
         }
      }

      extracted.data = new byte[computeInt(Integer.bitCount(extracted.dataSize), flag2, flag)];
      int n2 = 0;

      for (ExtendedBlockStorage extendedBlockStorage : (Iterable<ExtendedBlockStorage>)newArrayList) {
         char[] data = extendedBlockStorage.getData();

         for (char c : data) {
            extracted.data[n2++] = (byte)(c & 0xFF);
            extracted.data[n2++] = (byte)(c >> '\b' & 0xFF);
         }
      }

      for (ExtendedBlockStorage extendedBlockStorage : (Iterable<ExtendedBlockStorage>)newArrayList) {
         n2 = arraycopy(extendedBlockStorage.getBlocklightArray().getData(), extracted.data, n2);
      }

      if (flag2) {
         for (ExtendedBlockStorage extendedBlockStorage : (Iterable<ExtendedBlockStorage>)newArrayList) {
            n2 = arraycopy(extendedBlockStorage.getSkylightArray().getData(), extracted.data, n2);
         }
      }

      if (flag) {
         arraycopy(chunk.getBiomeArray(), extracted.data, n2);
      }

      return extracted;
   }

   public static int arraycopy(byte[] bytes, byte[] bytes2, int n) {
      System.arraycopy(bytes, 0, bytes2, n, bytes.length);
      return n + bytes.length;
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public int getExtractedSize() {
      return this.extractedData.dataSize;
   }

   public boolean shouldLoad() {
      return this.isFullChunk;
   }

   public static class Extracted {
      public byte[] data;
      public int dataSize;
   }
}
