package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class RemoveBlockedServerPacket extends AbstractPacket {
   public String serverId;

   public RemoveBlockedServerPacket() {
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.serverId = packetBuffer.readString();
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.serverId);
   }

   public String getServerId() {
      return this.serverId;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handleRemoveBlockedServer(this);
   }

   public RemoveBlockedServerPacket(String str) {
      this.serverId = str;
   }
}
