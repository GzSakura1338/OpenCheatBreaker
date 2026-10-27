package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;

public class ChallengeResponsePacket extends AbstractPacket {
   public byte[] encryptedToken;

   @Override
   public void handle(PacketHandler packetHandler) {
      ((ServerPacketHandler)packetHandler).handleChallengeResponse(this);
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      this.writeByteArray(packetBuffer, this.encryptedToken);
   }

   public ChallengeResponsePacket(byte[] bytes) {
      this.encryptedToken = bytes;
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.encryptedToken = this.readByteArray(packetBuffer);
   }

   public byte[] getEncryptedToken() {
      return this.encryptedToken;
   }

   public ChallengeResponsePacket() {
   }
}
