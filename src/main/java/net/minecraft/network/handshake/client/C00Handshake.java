package net.minecraft.network.handshake.client;

import java.io.IOException;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;

public class C00Handshake extends Packet {
    public int protocolVersion;
    public String ip;
    public int port;
    public EnumConnectionState requestedState;

    public C00Handshake() {
    }

    public C00Handshake(int n, String str, int n2, EnumConnectionState enumConnectionState) {
        this.protocolVersion = n;
        this.ip = str;
        this.port = n2;
        this.requestedState = enumConnectionState;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.protocolVersion = data.readVarIntFromBuffer();
        this.ip = data.readStringFromBuffer(255);
        this.port = data.readUnsignedShort();
        this.requestedState = EnumConnectionState.getById(data.readVarIntFromBuffer());
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeVarIntToBuffer(this.protocolVersion);
        data.writeStringToBuffer(this.ip);
        data.writeShort(this.port);
        data.writeVarIntToBuffer(this.requestedState.getId());
    }

    public void processPacket(INetHandlerHandshakeServer handler) {
        handler.processHandshake(this);
    }

    @Override
    public boolean hasPriority() {
        return true;
    }

    public EnumConnectionState getRequestedState() {
        return this.requestedState;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerHandshakeServer)netHandler);
    }
}
