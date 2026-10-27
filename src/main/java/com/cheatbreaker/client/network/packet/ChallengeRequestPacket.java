package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class ChallengeRequestPacket extends AbstractPacket {
   public byte[] publicKey;

   public byte[] getPublicKey() {
      return this.publicKey;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handleChallengeRequest(this);
   }

   public ChallengeRequestPacket() {
   }

   public ChallengeRequestPacket(byte[] bytes) {
      this.publicKey = bytes;
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      this.writeByteArray(packetBuffer, this.publicKey);
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.publicKey = this.readByteArray(packetBuffer);
   }
}
