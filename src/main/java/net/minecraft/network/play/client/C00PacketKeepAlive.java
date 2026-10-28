package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C00PacketKeepAlive extends Packet {
    public int key;

    public C00PacketKeepAlive() {
    }

    public C00PacketKeepAlive(int n) {
        this.key = n;
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processKeepAlive(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.key = data.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.key);
    }

    @Override
    public boolean hasPriority() {
        return true;
    }

    public int getKey() {
        return this.key;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
