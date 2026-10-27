package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload implements Packet<INetHandlerPlayServer> {
   public String channel;
   public PacketBuffer data;

   public C17PacketCustomPayload() {
   }

   public C17PacketCustomPayload(String channelIn, PacketBuffer dataIn) {
      this.channel = channelIn;
      this.data = dataIn;
      if (dataIn.writerIndex() > 32767) {
         throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.channel = buf.readStringFromBuffer(20);
      int readableBytes = buf.readableBytes();
      if (readableBytes >= 0 && readableBytes <= 32767) {
         this.data = new PacketBuffer(buf.readBytes(readableBytes));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.channel);
      buf.writeBytes(this.data);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processVanilla250Packet(this);
   }

   public String getChannelName() {
      return this.channel;
   }

   public PacketBuffer getBufferData() {
      return this.data;
   }
}
