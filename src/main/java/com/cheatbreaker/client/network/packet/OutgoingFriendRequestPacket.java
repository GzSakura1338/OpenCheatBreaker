package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class OutgoingFriendRequestPacket extends AbstractPacket {
   public String playerId;
   public String playerName;
   public boolean flag;

   public OutgoingFriendRequestPacket() {
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handleOutgoingFriendRequest(this);
   }

   public OutgoingFriendRequestPacket(String str, String str2, boolean flag) {
      this.playerId = str;
      this.playerName = str2;
      this.flag = flag;
   }

   public String getPlayerId() {
      return this.playerId;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.playerId);
      packetBuffer.writeString(this.playerName);
      packetBuffer.getByteBuf().writeBoolean(this.flag);
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.playerId = packetBuffer.readString();
      this.playerName = packetBuffer.readString();
      this.flag = packetBuffer.getByteBuf().readBoolean();
   }

   public boolean isFlag() {
      return this.flag;
   }
}
