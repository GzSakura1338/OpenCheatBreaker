package net.minecraft.network;

import java.io.IOException;

public interface Packet<T extends INetHandler> {
   void readPacketData(PacketBuffer packetBuffer) throws IOException;

   void writePacketData(PacketBuffer packetBuffer) throws IOException;

   void processPacket(T value);
}
