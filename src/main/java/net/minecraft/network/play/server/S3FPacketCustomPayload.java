package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3FPacketCustomPayload implements Packet<INetHandlerPlayClient> {
   public String channel;
   public PacketBuffer data;

   public S3FPacketCustomPayload() {
   }

   public S3FPacketCustomPayload(String channelName, PacketBuffer dataIn) {
      this.channel = channelName;
      this.data = dataIn;
      if (dataIn.writerIndex() > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.channel = buf.readStringFromBuffer(20);
      int readableBytes = buf.readableBytes();
      if (readableBytes >= 0 && readableBytes <= 1048576) {
         this.data = new PacketBuffer(buf.readBytes(readableBytes));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.channel);
      buf.writeBytes(this.data);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleCustomPayload(this);
   }

   public String getChannelName() {
      return this.channel;
   }

   public PacketBuffer getBufferData() {
      return this.data;
   }
}
