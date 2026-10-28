package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;

public class EncryptionResponsePacket extends AbstractPacket {
    public byte[] encryptedSharedKey;
    public byte[] encryptedVerifyToken;

    public EncryptionResponsePacket() {
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        this.writeByteArray(packetBuffer, this.encryptedSharedKey);
        this.writeByteArray(packetBuffer, this.encryptedVerifyToken);
    }

    public EncryptionResponsePacket(byte[] bytes, byte[] bytes2) {
        this.encryptedSharedKey = bytes;
        this.encryptedVerifyToken = bytes2;
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.encryptedSharedKey = this.readByteArray(packetBuffer);
        this.encryptedVerifyToken = this.readByteArray(packetBuffer);
    }

    public byte[] getEncryptedVerifyToken() {
        return this.encryptedVerifyToken;
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((ServerPacketHandler)packetHandler).handleEncryptionResponse(this);
    }

    public byte[] getEncryptedSharedKey() {
        return this.encryptedSharedKey;
    }
}
