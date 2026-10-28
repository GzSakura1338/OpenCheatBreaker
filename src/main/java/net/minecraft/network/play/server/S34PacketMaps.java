package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S34PacketMaps extends Packet {
    public int mapId;
    public byte[] colors;

    public S34PacketMaps() {
    }

    public S34PacketMaps(int n, byte[] bytes) {
        this.mapId = n;
        this.colors = bytes;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.mapId = data.readVarIntFromBuffer();
        this.colors = new byte[data.readUnsignedShort()];
        data.readBytes(this.colors);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeVarIntToBuffer(this.mapId);
        data.writeShort(this.colors.length);
        data.writeBytes(this.colors);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleMaps(this);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, length=%d", this.mapId, this.colors.length);
    }

    public int getMapId() {
        return this.mapId;
    }

    public byte[] getData() {
        return this.colors;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
