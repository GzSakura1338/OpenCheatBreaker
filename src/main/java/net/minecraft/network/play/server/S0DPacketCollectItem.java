package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S0DPacketCollectItem extends Packet {
    public int chunkX;
    public int chunkZ;

    public S0DPacketCollectItem() {
    }

    public S0DPacketCollectItem(int n, int n2) {
        this.chunkX = n;
        this.chunkZ = n2;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.chunkX = data.readInt();
        this.chunkZ = data.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.chunkX);
        data.writeInt(this.chunkZ);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleCollectItem(this);
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
