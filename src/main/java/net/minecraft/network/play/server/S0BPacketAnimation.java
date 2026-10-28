package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S0BPacketAnimation extends Packet {
    public int entityId;
    public int type;

    public S0BPacketAnimation() {
    }

    public S0BPacketAnimation(Entity ent, int animationType) {
        this.entityId = ent.getEntityId();
        this.type = animationType;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.entityId = data.readVarIntFromBuffer();
        this.type = data.readUnsignedByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeVarIntToBuffer(this.entityId);
        data.writeByte(this.type);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleAnimation(this);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, type=%d", this.entityId, this.type);
    }

    public int getId() {
        return this.entityId;
    }

    public int getAnimationId() {
        return this.type;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
