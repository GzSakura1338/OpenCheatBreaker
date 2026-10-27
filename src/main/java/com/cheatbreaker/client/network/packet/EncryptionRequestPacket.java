package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class EncryptionRequestPacket extends AbstractPacket {
   public byte[] publicKey;
   public byte[] verifyToken;

   public byte[] getVerifyToken() {
      return this.verifyToken;
   }

   public EncryptionRequestPacket(byte[] bytes, byte[] bytes2) {
      this.publicKey = bytes;
      this.verifyToken = bytes2;
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.publicKey = this.readByteArray(packetBuffer);
      this.verifyToken = this.readByteArray(packetBuffer);
   }

   public EncryptionRequestPacket() {
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      this.writeByteArray(packetBuffer, this.publicKey);
      this.writeByteArray(packetBuffer, this.verifyToken);
   }

   public byte[] getPublicKey() {
      return this.publicKey;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handleEncryptionRequest(this);
   }
}
