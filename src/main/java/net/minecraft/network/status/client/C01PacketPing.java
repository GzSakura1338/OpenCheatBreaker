package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C01PacketPing extends Packet {
    public long clientTime;

    public C01PacketPing() {
    }

    public C01PacketPing(long l) {
        this.clientTime = l;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.clientTime = data.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeLong(this.clientTime);
    }

    public void processPacket(INetHandlerStatusServer handler) {
        handler.processPing(this);
    }

    @Override
    public boolean hasPriority() {
        return true;
    }

    public long getClientTime() {
        return this.clientTime;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerStatusServer)netHandler);
    }
}
