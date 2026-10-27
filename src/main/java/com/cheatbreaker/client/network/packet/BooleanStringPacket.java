package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class BooleanStringPacket extends AbstractPacket {
   public boolean flag;
   public String text;

   public BooleanStringPacket(boolean flag, String str) {
      this.flag = flag;
      this.text = str;
   }

   public String getText() {
      return this.text;
   }

   public boolean isFlag() {
      return this.flag;
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.flag = packetBuffer.getByteBuf().readBoolean();
      this.text = packetBuffer.readString();
   }

   public BooleanStringPacket() {
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handleBooleanString(this);
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.getByteBuf().writeBoolean(this.flag);
      packetBuffer.writeString(this.text);
   }
}
