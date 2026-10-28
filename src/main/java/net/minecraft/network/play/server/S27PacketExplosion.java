package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

public class S27PacketExplosion extends Packet {
    public double x;
    public double y;
    public double z;
    public float radius;
    public List affectedBlocks;
    public float playerVelocityX;
    public float playerVelocityY;
    public float playerVelocityZ;

    public S27PacketExplosion() {
    }

    public S27PacketExplosion(double d, double d2, double d3, float f, List list, Vec3 vec3) {
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.radius = f;
        this.affectedBlocks = new ArrayList(list);
        if (vec3 != null) {
            this.playerVelocityX = (float)vec3.xCoord;
            this.playerVelocityY = (float)vec3.yCoord;
            this.playerVelocityZ = (float)vec3.zCoord;
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.x = data.readFloat();
        this.y = data.readFloat();
        this.z = data.readFloat();
        this.radius = data.readFloat();
        int readInt = data.readInt();
        this.affectedBlocks = new ArrayList(readInt);
        int n = (int)this.x;
        int n2 = (int)this.y;
        int n3 = (int)this.z;

        for (int i = 0; i < readInt; i++) {
            int n4 = data.readByte() + n;
            int n5 = data.readByte() + n2;
            int n6 = data.readByte() + n3;
            this.affectedBlocks.add(new ChunkPosition(n4, n5, n6));
        }

        this.playerVelocityX = data.readFloat();
        this.playerVelocityY = data.readFloat();
        this.playerVelocityZ = data.readFloat();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeFloat((float)this.x);
        data.writeFloat((float)this.y);
        data.writeFloat((float)this.z);
        data.writeFloat(this.radius);
        data.writeInt(this.affectedBlocks.size());
        int n = (int)this.x;
        int n2 = (int)this.y;
        int n3 = (int)this.z;

        for (ChunkPosition chunkPosition : (Iterable<ChunkPosition>)this.affectedBlocks) {
            int n4 = chunkPosition.chunkPosX - n;
            int n5 = chunkPosition.chunkPosY - n2;
            int n6 = chunkPosition.chunkPosZ - n3;
            data.writeByte(n4);
            data.writeByte(n5);
            data.writeByte(n6);
        }

        data.writeFloat(this.playerVelocityX);
        data.writeFloat(this.playerVelocityY);
        data.writeFloat(this.playerVelocityZ);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleExplosion(this);
    }

    public float getPlayerVelocityX() {
        return this.playerVelocityX;
    }

    public float getPlayerVelocityY() {
        return this.playerVelocityY;
    }

    public float getPlayerVelocityZ() {
        return this.playerVelocityZ;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getRadius() {
        return this.radius;
    }

    public List getAffectedBlocks() {
        return this.affectedBlocks;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
