package net.minecraft.network.login.server;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.CryptManager;

public class S01PacketEncryptionRequest extends Packet {
    public String hashedServerId;
    public PublicKey publicKey;
    public byte[] nonce;

    public S01PacketEncryptionRequest() {
    }

    public S01PacketEncryptionRequest(String serverId, PublicKey key, byte[] bytes) {
        this.hashedServerId = serverId;
        this.publicKey = key;
        this.nonce = bytes;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.hashedServerId = data.readStringFromBuffer(20);
        this.publicKey = CryptManager.decodePublicKey(readBlob(data));
        this.nonce = readBlob(data);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.hashedServerId);
        writeBlob(data, this.publicKey.getEncoded());
        writeBlob(data, this.nonce);
    }

    public void processPacket(INetHandlerLoginClient handler) {
        handler.handleEncryptionRequest(this);
    }

    public String getServerId() {
        return this.hashedServerId;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public byte[] getNonce() {
        return this.nonce;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerLoginClient)netHandler);
    }
}
