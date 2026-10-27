package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;

public class ViolationReportPacket extends AbstractPacket {
   public int code;
   public String details;

   @Override
   public void handle(PacketHandler packetHandler) {
      ((ServerPacketHandler)packetHandler).handleViolationReport(this);
   }

   public int getCode() {
      return this.code;
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.code = packetBuffer.getByteBuf().readInt();
      this.details = packetBuffer.readString();
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.getByteBuf().writeInt(this.code);
      packetBuffer.writeString(this.details);
   }

   public ViolationReportPacket(int n, String str) {
      this.code = n;
      this.details = str;
   }

   public String getDetails() {
      return this.details;
   }

   public ViolationReportPacket() {
   }
}
