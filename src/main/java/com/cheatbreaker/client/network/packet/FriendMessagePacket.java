package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class FriendMessagePacket extends AbstractPacket {
   public String playerId;
   public String message;

   @Override
   public void handle(PacketHandler packetHandler) {
      packetHandler.handleFriendMessage(this);
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.playerId = packetBuffer.readString();
      this.message = packetBuffer.readString();
   }

   public FriendMessagePacket() {
   }

   public String getPlayerId() {
      return this.playerId;
   }

   public FriendMessagePacket(String str, String str2) {
      this.playerId = str;
      this.message = str2;
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.playerId);
      packetBuffer.writeString(this.message);
   }

   public String getMessage() {
      return this.message;
   }
}
