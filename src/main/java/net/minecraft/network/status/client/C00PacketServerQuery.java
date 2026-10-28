package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C00PacketServerQuery extends Packet {
    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
    }

    public void processPacket(INetHandlerStatusServer handler) {
        handler.processServerQuery(this);
    }

    @Override
    public boolean hasPriority() {
        return true;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerStatusServer)netHandler);
    }
}
