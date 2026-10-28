package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0EPacketSpawnObject extends Packet {
    public int id;
    public int x;
    public int y;
    public int z;
    public int pitch;
    public int yaw;
    public int type;
    public int dataId;
    public int velocityX;
    public int velocityY;
    public int velocityZ;

    public S0EPacketSpawnObject() {
    }

    public S0EPacketSpawnObject(Entity entity, int n) {
        this(entity, n, 0);
    }

    public S0EPacketSpawnObject(Entity entity, int n, int n2) {
        this.id = entity.getEntityId();
        this.x = MathHelper.floor_double(entity.posX * 32.0);
        this.y = MathHelper.floor_double(entity.posY * 32.0);
        this.z = MathHelper.floor_double(entity.posZ * 32.0);
        this.pitch = MathHelper.floor_float(entity.rotationPitch * 256.0F / 360.0F);
        this.yaw = MathHelper.floor_float(entity.rotationYaw * 256.0F / 360.0F);
        this.type = n;
        this.dataId = n2;
        if (n2 > 0) {
            double motionX = entity.motionX;
            double motionY = entity.motionY;
            double motionZ = entity.motionZ;
            double d = 3.9;
            if (motionX < -d) {
                motionX = -d;
            }

            if (motionY < -d) {
                motionY = -d;
            }

            if (motionZ < -d) {
                motionZ = -d;
            }

            if (motionX > d) {
                motionX = d;
            }

            if (motionY > d) {
                motionY = d;
            }

            if (motionZ > d) {
                motionZ = d;
            }

            this.velocityX = (int)(motionX * 8000.0);
            this.velocityY = (int)(motionY * 8000.0);
            this.velocityZ = (int)(motionZ * 8000.0);
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readVarIntFromBuffer();
        this.type = data.readByte();
        this.x = data.readInt();
        this.y = data.readInt();
        this.z = data.readInt();
        this.pitch = data.readByte();
        this.yaw = data.readByte();
        this.dataId = data.readInt();
        if (this.dataId > 0) {
            this.velocityX = data.readShort();
            this.velocityY = data.readShort();
            this.velocityZ = data.readShort();
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeVarIntToBuffer(this.id);
        data.writeByte(this.type);
        data.writeInt(this.x);
        data.writeInt(this.y);
        data.writeInt(this.z);
        data.writeByte(this.pitch);
        data.writeByte(this.yaw);
        data.writeInt(this.dataId);
        if (this.dataId > 0) {
            data.writeShort(this.velocityX);
            data.writeShort(this.velocityY);
            data.writeShort(this.velocityZ);
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnObject(this);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f", this.id, this.type, this.x / 32.0F, this.y / 32.0F, this.z / 32.0F);
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

    public int getVelocityX() {
        return this.velocityX;
    }

    public int getVelocityY() {
        return this.velocityY;
    }

    public int getVelocityZ() {
        return this.velocityZ;
    }

    public int getPitch() {
        return this.pitch;
    }

    public int getYaw() {
        return this.yaw;
    }

    public int getEntityData() {
        return this.type;
    }

    public int getDataId() {
        return this.dataId;
    }

    public void setX(int n) {
        this.x = n;
    }

    public void setY(int n) {
        this.y = n;
    }

    public void setZ(int n) {
        this.z = n;
    }

    public void setVelocityX(int n) {
        this.velocityX = n;
    }

    public void setVelocityY(int n) {
        this.velocityY = n;
    }

    public void setVelocityZ(int n) {
        this.velocityZ = n;
    }

    public void setDataId(int n) {
        this.dataId = n;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
