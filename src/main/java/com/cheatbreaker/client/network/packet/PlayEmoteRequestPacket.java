package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class PlayEmoteRequestPacket extends AbstractPacket {
   public int emoteId;

   public PlayEmoteRequestPacket() {
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.emoteId = packetBuffer.getByteBuf().readInt();
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.getByteBuf().writeInt(this.emoteId);
   }

   public int getEmoteId() {
      return this.emoteId;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      packetHandler.handlePlayEmoteRequest(this);
   }

   public PlayEmoteRequestPacket(int n) {
      this.emoteId = n;
   }
}
