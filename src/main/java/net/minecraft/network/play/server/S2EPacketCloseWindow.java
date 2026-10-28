package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2EPacketCloseWindow extends Packet {
    public int syncId;

    public S2EPacketCloseWindow() {
    }

    public S2EPacketCloseWindow(int n) {
        this.syncId = n;
    }

    public void processPacket(INetHandlerPlayClient handler) throws IOException {
        handler.handleCloseWindow(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.syncId = data.readUnsignedByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.syncId);
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
