package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S18PacketEntityTeleport extends Packet {
    public int id;
    public int x;
    public int y;
    public int z;
    public byte yaw;
    public byte pitch;

    public S18PacketEntityTeleport() {
    }

    public S18PacketEntityTeleport(Entity entity) {
        this.id = entity.getEntityId();
        this.x = MathHelper.floor_double(entity.posX * 32.0);
        this.y = MathHelper.floor_double(entity.posY * 32.0);
        this.z = MathHelper.floor_double(entity.posZ * 32.0);
        this.yaw = (byte)(entity.rotationYaw * 256.0F / 360.0F);
        this.pitch = (byte)(entity.rotationPitch * 256.0F / 360.0F);
    }

    public S18PacketEntityTeleport(int n, int n2, int n3, int n4, byte b, byte b2) {
        this.id = n;
        this.x = n2;
        this.y = n3;
        this.z = n4;
        this.yaw = b;
        this.pitch = b2;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readInt();
        this.x = data.readInt();
        this.y = data.readInt();
        this.z = data.readInt();
        this.yaw = data.readByte();
        this.pitch = data.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.id);
        data.writeInt(this.x);
        data.writeInt(this.y);
        data.writeInt(this.z);
        data.writeByte(this.yaw);
        data.writeByte(this.pitch);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityTeleport(this);
    }

    public int getId() {
        return this.id;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
