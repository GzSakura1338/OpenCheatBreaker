package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0FPacketSpawnMob extends Packet {
    public int id;
    public int entityTypeId;
    public int x;
    public int y;
    public int z;
    public byte yaw;
    public byte pitch;
    public byte headYaw;
    public int velocityX;
    public int velocityY;
    public int velocityZ;
    public DataWatcher tracker;
    public List entries;

    public S0FPacketSpawnMob() {
    }

    public S0FPacketSpawnMob(EntityLivingBase entityLivingBase) {
        this.id = entityLivingBase.getEntityId();
        this.entityTypeId = (byte)EntityList.getEntityID(entityLivingBase);
        this.x = entityLivingBase.myEntitySize.multiplyBy32AndRound(entityLivingBase.posX);
        this.y = MathHelper.floor_double(entityLivingBase.posY * 32.0);
        this.z = entityLivingBase.myEntitySize.multiplyBy32AndRound(entityLivingBase.posZ);
        this.yaw = (byte)(entityLivingBase.rotationYaw * 256.0F / 360.0F);
        this.pitch = (byte)(entityLivingBase.rotationPitch * 256.0F / 360.0F);
        this.headYaw = (byte)(entityLivingBase.rotationYawHead * 256.0F / 360.0F);
        double d = 3.9;
        double motionX = entityLivingBase.motionX;
        double motionY = entityLivingBase.motionY;
        double motionZ = entityLivingBase.motionZ;
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
        this.tracker = entityLivingBase.getDataWatcher();
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readVarIntFromBuffer();
        this.entityTypeId = data.readByte() & 255;
        this.x = data.readInt();
        this.y = data.readInt();
        this.z = data.readInt();
        this.yaw = data.readByte();
        this.pitch = data.readByte();
        this.headYaw = data.readByte();
        this.velocityX = data.readShort();
        this.velocityY = data.readShort();
        this.velocityZ = data.readShort();
        this.entries = DataWatcher.readWatchedListFromPacketBuffer(data);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeVarIntToBuffer(this.id);
        data.writeByte(this.entityTypeId & 0xFF);
        data.writeInt(this.x);
        data.writeInt(this.y);
        data.writeInt(this.z);
        data.writeByte(this.yaw);
        data.writeByte(this.pitch);
        data.writeByte(this.headYaw);
        data.writeShort(this.velocityX);
        data.writeShort(this.velocityY);
        data.writeShort(this.velocityZ);
        this.tracker.write(data);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnMob(this);
    }

    public List getEntries() {
        if (this.entries == null) {
            this.entries = this.tracker.getAllWatched();
        }

        return this.entries;
    }

    @Override
    public String serialize() {
        return String.format(
            "id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f, xd=%.2f, yd=%.2f, zd=%.2f",
            this.id,
            this.entityTypeId,
            this.x / 32.0F,
            this.y / 32.0F,
            this.z / 32.0F,
            this.velocityX / 8000.0F,
            this.velocityY / 8000.0F,
            this.velocityZ / 8000.0F
        );
    }

    public int getId() {
        return this.id;
    }

    public int getEntityTypeId() {
        return this.entityTypeId;
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

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public byte getHeadYaw() {
        return this.headYaw;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
