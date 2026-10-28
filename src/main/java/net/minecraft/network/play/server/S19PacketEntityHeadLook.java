package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S19PacketEntityHeadLook extends Packet {
    public int entity;
    public byte headYaw;

    public S19PacketEntityHeadLook() {
    }

    public S19PacketEntityHeadLook(Entity entity, byte b) {
        this.entity = entity.getEntityId();
        this.headYaw = b;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.entity = data.readInt();
        this.headYaw = data.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.entity);
        data.writeByte(this.headYaw);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityHeadLook(this);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, rot=%d", this.entity, this.headYaw);
    }

    public Entity getEntity(World world) {
        return world.getEntityByID(this.entity);
    }

    public byte getHeadYaw() {
        return this.headYaw;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
