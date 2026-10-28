package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C07PacketPlayerDigging extends Packet {
    public int status;
    public int diggedBlockX;
    public int diggedBlockY;
    public int diggedBlockZ;
    public int diggingBlockFace;

    public C07PacketPlayerDigging() {
    }

    public C07PacketPlayerDigging(int n, int n2, int n3, int n4, int n5) {
        this.status = n;
        this.diggedBlockX = n2;
        this.diggedBlockY = n3;
        this.diggedBlockZ = n4;
        this.diggingBlockFace = n5;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.status = data.readUnsignedByte();
        this.diggedBlockX = data.readInt();
        this.diggedBlockY = data.readUnsignedByte();
        this.diggedBlockZ = data.readInt();
        this.diggingBlockFace = data.readUnsignedByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.status);
        data.writeInt(this.diggedBlockX);
        data.writeByte(this.diggedBlockY);
        data.writeInt(this.diggedBlockZ);
        data.writeByte(this.diggingBlockFace);
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processPlayerDigging(this);
    }

    public int getDiggedBlockX() {
        return this.diggedBlockX;
    }

    public int getDiggedBlockY() {
        return this.diggedBlockY;
    }

    public int getDiggedBlockZ() {
        return this.diggedBlockZ;
    }

    public int getDiggingBlockFace() {
        return this.diggingBlockFace;
    }

    public int getDiggedBlockStatus() {
        return this.status;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
