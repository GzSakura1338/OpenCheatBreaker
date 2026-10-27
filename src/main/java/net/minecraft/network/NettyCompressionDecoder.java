package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class NettyCompressionDecoder extends ByteToMessageDecoder {
   public int treshold;
   public Inflater inflater;

   public NettyCompressionDecoder(int treshold) {
      this.treshold = treshold;
      this.inflater = new Inflater();
   }

   public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception, DataFormatException {
      if (byteBuf.readableBytes() != 0) {
         PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
         int readVarIntFromBuffer = packetBuffer.readVarIntFromBuffer();
         if (readVarIntFromBuffer == 0) {
            list.add(packetBuffer.readBytes(packetBuffer.readableBytes()));
         } else {
            if (readVarIntFromBuffer < this.treshold) {
               throw new DecoderException("Badly compressed packet - size of " + readVarIntFromBuffer + " is below server threshold of " + this.treshold);
            }

            if (readVarIntFromBuffer > 2097152) {
               throw new DecoderException("Badly compressed packet - size of " + readVarIntFromBuffer + " is larger than protocol maximum of " + 2097152);
            }

            byte[] bytes = new byte[packetBuffer.readableBytes()];
            packetBuffer.readBytes(bytes);
            this.inflater.setInput(bytes);
            byte[] bytes2 = new byte[readVarIntFromBuffer];
            this.inflater.inflate(bytes2);
            list.add(Unpooled.wrappedBuffer(bytes2));
            this.inflater.reset();
         }
      }
   }

   public void setCompressionTreshold(int treshold) {
      this.treshold = treshold;
   }
}
