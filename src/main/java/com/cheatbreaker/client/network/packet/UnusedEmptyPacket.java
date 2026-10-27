package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;

public class UnusedEmptyPacket extends AbstractPacket {
   @Override
   public void handle(PacketHandler packetHandler) {
      ((ServerPacketHandler)packetHandler).handleUnusedEmpty(this);
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
   }
}
