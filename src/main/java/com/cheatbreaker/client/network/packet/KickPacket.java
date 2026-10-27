package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class KickPacket extends AbstractPacket {
   public String reason;

   public KickPacket() {
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.reason = packetBuffer.readString();
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.reason);
   }

   public String getReason() {
      return this.reason;
   }

   public KickPacket(String str) {
      this.reason = str;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handleKick(this);
   }
}
