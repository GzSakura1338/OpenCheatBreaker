package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0BPacketEntityAction extends Packet {
    public int entityId;
    public int intValue;
    public int mountJumpHeight;

    public C0BPacketEntityAction() {
    }

    public C0BPacketEntityAction(Entity entity, int n) {
        this(entity, n, 0);
    }

    public C0BPacketEntityAction(Entity entity, int n, int n2) {
        this.entityId = entity.getEntityId();
        this.intValue = n;
        this.mountJumpHeight = n2;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.entityId = data.readInt();
        this.intValue = data.readByte();
        this.mountJumpHeight = data.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.entityId);
        data.writeByte(this.intValue);
        data.writeInt(this.mountJumpHeight);
    }

    public void processPacket(INetHandlerPlayServer handler) throws IOException {
        handler.processEntityAction(this);
    }

    public int getIntValue() {
        return this.intValue;
    }

    public int getMountJumpHeight() {
        return this.mountJumpHeight;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
