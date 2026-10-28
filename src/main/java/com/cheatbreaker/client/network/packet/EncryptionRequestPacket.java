package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class EncryptionRequestPacket extends AbstractPacket {
    public byte[] publicKey;
    public byte[] verifyToken;

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.publicKey = this.readByteArray(packetBuffer);
        this.verifyToken = this.readByteArray(packetBuffer);
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((WebsocketPacketHandler)packetHandler).handleEncryptionRequest(this);
    }

    public EncryptionRequestPacket(byte[] bytes, byte[] bytes2) {
        this.publicKey = bytes;
        this.verifyToken = bytes2;
    }

    public EncryptionRequestPacket() {
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        this.writeByteArray(packetBuffer, this.publicKey);
        this.writeByteArray(packetBuffer, this.verifyToken);
    }
}
