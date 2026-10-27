package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;

public class SetFriendRequestsEnabledPacket extends AbstractPacket {
   public boolean enabled;

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.getByteBuf().writeBoolean(this.enabled);
   }

   public SetFriendRequestsEnabledPacket() {
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((ServerPacketHandler)packetHandler).handleSetFriendRequestsEnabled(this);
   }

   public SetFriendRequestsEnabledPacket(boolean flag) {
      this.enabled = flag;
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.enabled = packetBuffer.getByteBuf().readBoolean();
   }

   public boolean isEnabled() {
      return this.enabled;
   }
}
