package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S19PacketEntityStatus extends Packet {
    public int id;
    public byte status;

    public S19PacketEntityStatus() {
    }

    public S19PacketEntityStatus(Entity entity, byte b) {
        this.id = entity.getEntityId();
        this.status = b;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readInt();
        this.status = data.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.id);
        data.writeByte(this.status);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityStatus(this);
    }

    public Entity getEntity(World world) {
        return world.getEntityByID(this.id);
    }

    public byte getStatus() {
        return this.status;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
