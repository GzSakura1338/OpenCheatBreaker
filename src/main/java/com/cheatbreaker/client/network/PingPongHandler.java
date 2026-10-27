package com.cheatbreaker.client.network;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public class PingPongHandler extends ChannelInboundHandlerAdapter {
   public Minecraft mc = Minecraft.getMinecraft();
   public long longValue = System.nanoTime() - 30000000000L;
   public long longValue2 = 0L;
   public ChannelDecryptionHandler channelDecryptionHandler;

   public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) throws IOException, Exception {
      S3FPacketCustomPayload s3FPacketCustomPayload;
      if (obj instanceof S3FPacketCustomPayload && (s3FPacketCustomPayload = (S3FPacketCustomPayload)obj).getChannelName().equals("CB|PING")) {
         this.longValue = System.nanoTime();
         long l = new DataInputStream(new ByteArrayInputStream(s3FPacketCustomPayload.getBufferData().readByteArray())).readInt() & 4294967295L;
         if (l != this.channelDecryptionHandler.getLongValue3()) {
            throw new IOException("CheatBreaker Protocol Error -2a\n(Try updating your client)");
         }

         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

         try {
            dataOutputStream.write(s3FPacketCustomPayload.getBufferData().readByteArray());
            if ((this.longValue2++ & 7L) == 0L) {
               dataOutputStream.writeLong(this.longValue2);
               dataOutputStream.writeUTF(this.mc.getCurrentServerData().serverIP);
               dataOutputStream.writeUTF(this.mc.getSession().getPlayerID());
               dataOutputStream.writeUTF(this.mc.entityRenderer.getClass().getName());
            }
         } catch (IOException e) {
         }

         net.minecraft.network.PacketBuffer packetBuffer = new net.minecraft.network.PacketBuffer(Unpooled.buffer());
         packetBuffer.writeBytes(byteArrayOutputStream.toByteArray());
         channelHandlerContext.channel().eventLoop().execute(() -> channelHandlerContext.channel().writeAndFlush(new C17PacketCustomPayload("CB|PONG", packetBuffer)));
      }

      if (System.nanoTime() - this.longValue > 45000000000L) {
         throw new IOException("CheatBreaker Protocol Error -2b\n(Try updating your client)");
      }

      super.channelRead(channelHandlerContext, obj);
   }

   public PingPongHandler(ChannelDecryptionHandler channelDecryptionHandler) {
      this.channelDecryptionHandler = channelDecryptionHandler;
   }
}
