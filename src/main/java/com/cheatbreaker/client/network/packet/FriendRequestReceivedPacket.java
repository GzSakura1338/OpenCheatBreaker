package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class FriendRequestReceivedPacket extends AbstractPacket {
   public String playerId;
   public String playerName;

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.playerId);
      packetBuffer.writeString(this.playerName);
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.playerId = packetBuffer.readString();
      this.playerName = packetBuffer.readString();
   }

   public FriendRequestReceivedPacket() {
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      packetHandler.handleFriendRequestReceived(this);
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public String getPlayerId() {
      return this.playerId;
   }

   public FriendRequestReceivedPacket(String str, String str2) {
      this.playerId = str;
      this.playerName = str2;
   }
}
