package net.minecraft.network.login.client;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.util.CryptManager;

public class C01PacketEncryptionResponse extends Packet {
    public byte[] encryptedSecretKey = new byte[0];
    public byte[] encryptedNonce = new byte[0];

    public C01PacketEncryptionResponse() {
    }

    public C01PacketEncryptionResponse(SecretKey secretKey, PublicKey publicKey, byte[] bytes) {
        this.encryptedSecretKey = CryptManager.encryptData(publicKey, secretKey.getEncoded());
        this.encryptedNonce = CryptManager.encryptData(publicKey, bytes);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.encryptedSecretKey = readBlob(data);
        this.encryptedNonce = readBlob(data);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        writeBlob(data, this.encryptedSecretKey);
        writeBlob(data, this.encryptedNonce);
    }

    public void processPacket(INetHandlerLoginServer handler) {
        handler.processEncryptionResponse(this);
    }

    public SecretKey decryptSecretKey(PrivateKey key) {
        return CryptManager.decryptSharedKey(key, this.encryptedSecretKey);
    }

    public byte[] decryptNonce(PrivateKey privateKey) {
        return privateKey == null ? this.encryptedNonce : CryptManager.decryptData(privateKey, this.encryptedNonce);
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerLoginServer)netHandler);
    }
}
