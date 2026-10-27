package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   public byte[] buffer = new byte[8192];
   public int treshold;
   public Deflater deflater;

   public NettyCompressionEncoder(int treshold) {
      this.treshold = treshold;
      this.deflater = new Deflater();
   }

   public void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
      int readableBytes = byteBuf.readableBytes();
      PacketBuffer packetBuffer = new PacketBuffer(byteBuf2);
      if (readableBytes < this.treshold) {
         packetBuffer.writeVarIntToBuffer(0);
         packetBuffer.writeBytes(byteBuf);
      } else {
         byte[] bytes = new byte[readableBytes];
         byteBuf.readBytes(bytes);
         packetBuffer.writeVarIntToBuffer(bytes.length);
         this.deflater.setInput(bytes, 0, readableBytes);
         this.deflater.finish();

         while (!this.deflater.finished()) {
            int deflate = this.deflater.deflate(this.buffer);
            packetBuffer.writeBytes(this.buffer, 0, deflate);
         }

         this.deflater.reset();
      }
   }

   public void setCompressionTreshold(int treshold) {
      this.treshold = treshold;
   }
}
