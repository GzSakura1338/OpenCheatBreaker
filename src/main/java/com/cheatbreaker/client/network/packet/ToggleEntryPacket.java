package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class ToggleEntryPacket extends AbstractPacket {
   public String key;
   public boolean add;

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.key);
      packetBuffer.getByteBuf().writeBoolean(this.add);
   }

   public ToggleEntryPacket(String str, boolean flag) {
      this.key = str;
      this.add = flag;
   }

   public String getKey() {
      return this.key;
   }

   public boolean isAdd() {
      return this.add;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      packetHandler.handleToggleEntry(this);
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.key = packetBuffer.readString();
      this.add = packetBuffer.getByteBuf().readBoolean();
   }

   public ToggleEntryPacket() {
   }
}
