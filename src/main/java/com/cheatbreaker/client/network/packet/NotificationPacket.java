package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class NotificationPacket extends AbstractPacket {
   public String title;
   public String message;

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.title);
      packetBuffer.writeString(this.message);
   }

   public NotificationPacket() {
   }

   public NotificationPacket(String str, String str2) {
      this.title = str;
      this.message = str2;
   }

   public String getMessage() {
      return this.message;
   }

   public String getTitle() {
      return this.title;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handleNotification(this);
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.title = packetBuffer.readString();
      this.message = packetBuffer.readString();
   }
}
