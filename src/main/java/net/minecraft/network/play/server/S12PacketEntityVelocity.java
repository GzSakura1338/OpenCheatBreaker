package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S12PacketEntityVelocity extends Packet {
    public int id;
    public int velocityX;
    public int velocityY;
    public int velocityZ;

    public S12PacketEntityVelocity() {
    }

    public S12PacketEntityVelocity(Entity entity) {
        this(entity.getEntityId(), entity.motionX, entity.motionY, entity.motionZ);
    }

    public S12PacketEntityVelocity(int n, double d, double d2, double d3) {
        this.id = n;
        double d4 = 3.9;
        if (d < -d4) {
            d = -d4;
        }

        if (d2 < -d4) {
            d2 = -d4;
        }

        if (d3 < -d4) {
            d3 = -d4;
        }

        if (d > d4) {
            d = d4;
        }

        if (d2 > d4) {
            d2 = d4;
        }

        if (d3 > d4) {
            d3 = d4;
        }

        this.velocityX = (int)(d * 8000.0);
        this.velocityY = (int)(d2 * 8000.0);
        this.velocityZ = (int)(d3 * 8000.0);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readInt();
        this.velocityX = data.readShort();
        this.velocityY = data.readShort();
        this.velocityZ = data.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.id);
        data.writeShort(this.velocityX);
        data.writeShort(this.velocityY);
        data.writeShort(this.velocityZ);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityVelocity(this);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, x=%.2f, y=%.2f, z=%.2f", this.id, this.velocityX / 8000.0F, this.velocityY / 8000.0F, this.velocityZ / 8000.0F);
    }

    public int getId() {
        return this.id;
    }

    public int getVelocityX() {
        return this.velocityX;
    }

    public int getVelocityY() {
        return this.velocityY;
    }

    public int getVelocityZ() {
        return this.velocityZ;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
