package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class FriendServerPacket extends AbstractPacket {
   public String playerId;
   public String server;

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.playerId);
      packetBuffer.writeString(this.server);
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.playerId = packetBuffer.readString();
      this.server = packetBuffer.readString();
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      packetHandler.handleFriendServer(this);
   }

   public String getServer() {
      return this.server;
   }

   public FriendServerPacket() {
   }

   public FriendServerPacket(String str, String str2) {
      this.playerId = str;
      this.server = str2;
   }

   public String getPlayerId() {
      return this.playerId;
   }
}
