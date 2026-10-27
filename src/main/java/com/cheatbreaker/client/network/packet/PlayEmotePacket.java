package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;
import java.util.UUID;

public class PlayEmotePacket extends AbstractPacket {
   public UUID playerId;
   public int emoteId;

   public PlayEmotePacket(UUID uuid, int n) {
      this.playerId = uuid;
      this.emoteId = n;
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeUuid(this.playerId);
      packetBuffer.getByteBuf().writeInt(this.emoteId);
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public int getEmoteId() {
      return this.emoteId;
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.playerId = packetBuffer.readUuid();
      this.emoteId = packetBuffer.getByteBuf().readInt();
   }

   public PlayEmotePacket() {
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handlePlayEmote(this);
   }
}
