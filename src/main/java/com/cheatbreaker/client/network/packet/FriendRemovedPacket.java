package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class FriendRemovedPacket extends AbstractPacket {
   public String playerId;

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.playerId = packetBuffer.readString();
   }

   public FriendRemovedPacket() {
   }

   public String getPlayerId() {
      return this.playerId;
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.playerId);
   }

   public FriendRemovedPacket(String str) {
      this.playerId = str;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      packetHandler.handleFriendRemoved(this);
   }
}
