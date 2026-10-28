package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0APacketAnimation extends Packet {
    public int id;
    public int type;

    public C0APacketAnimation() {
    }

    public C0APacketAnimation(Entity entity, int n) {
        this.id = entity.getEntityId();
        this.type = n;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readInt();
        this.type = data.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.id);
        data.writeByte(this.type);
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processAnimation(this);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, type=%d", this.id, this.type);
    }

    public int getType() {
        return this.type;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
