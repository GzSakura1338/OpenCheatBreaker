package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class ShutdownPacket extends AbstractPacket {
   @Override
   public void write(PacketBuffer packetBuffer) {
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handleShutdown(this);
   }
}
