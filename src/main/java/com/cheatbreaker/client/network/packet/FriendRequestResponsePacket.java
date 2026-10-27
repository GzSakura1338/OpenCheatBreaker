package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class FriendRequestResponsePacket extends AbstractPacket {
   public boolean accepted;
   public String playerId;

   public String getPlayerId() {
      return this.playerId;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      packetHandler.handleFriendRequestResponse(this);
   }

   public FriendRequestResponsePacket(boolean flag, String str) {
      this.accepted = flag;
      this.playerId = str;
   }

   public FriendRequestResponsePacket() {
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.getByteBuf().writeBoolean(this.accepted);
      packetBuffer.writeString(this.playerId);
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.accepted = packetBuffer.getByteBuf().readBoolean();
      this.playerId = packetBuffer.readString();
   }

   public boolean isAccepted() {
      return this.accepted;
   }
}
