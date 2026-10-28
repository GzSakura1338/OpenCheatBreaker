package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1BPacketEntityAttach extends Packet {
    public int attachedId;
    public int id;
    public int holdingId;

    public S1BPacketEntityAttach() {
    }

    public S1BPacketEntityAttach(int n, Entity entity, Entity entity2) {
        this.attachedId = n;
        this.id = entity.getEntityId();
        this.holdingId = entity2 != null ? entity2.getEntityId() : -1;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readInt();
        this.holdingId = data.readInt();
        this.attachedId = data.readUnsignedByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.id);
        data.writeInt(this.holdingId);
        data.writeByte(this.attachedId);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityAttach(this);
    }

    public int getAttachedEntityId() {
        return this.attachedId;
    }

    public int getId() {
        return this.id;
    }

    public int getHoldingEntityId() {
        return this.holdingId;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
