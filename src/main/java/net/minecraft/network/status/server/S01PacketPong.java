package net.minecraft.network.status.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusClient;

public class S01PacketPong extends Packet {
    public long clientTime;

    public S01PacketPong() {
    }

    public S01PacketPong(long time) {
        this.clientTime = time;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.clientTime = data.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeLong(this.clientTime);
    }

    public void processPacket(INetHandlerStatusClient handler) {
        handler.handlePong(this);
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
        this.processPacket((INetHandlerStatusClient)netHandler);
    }
}
