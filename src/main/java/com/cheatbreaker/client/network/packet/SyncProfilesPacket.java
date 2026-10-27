package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class SyncProfilesPacket extends AbstractPacket {
   @Override
   public void read(PacketBuffer packetBuffer) {
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handleSyncProfiles(this);
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
   }
}
