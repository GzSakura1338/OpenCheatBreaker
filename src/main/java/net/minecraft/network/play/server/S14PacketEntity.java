package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S14PacketEntity extends Packet {
    public int id;
    public byte x;
    public byte y;
    public byte z;
    public byte yaw;
    public byte pitch;
    public boolean rotate;

    public S14PacketEntity() {
    }

    public S14PacketEntity(int n) {
        this.id = n;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.id);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityMovement(this);
    }

    @Override
    public String serialize() {
        return String.format("id=%d", this.id);
    }

    @Override
    public String toString() {
        return "Entity_" + super.toString();
    }

    public Entity getEntity(World world) {
        return world.getEntityByID(this.id);
    }

    public byte getX() {
        return this.x;
    }

    public byte getY() {
        return this.y;
    }

    public byte getZ() {
        return this.z;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public boolean shouldRotate() {
        return this.rotate;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }

    public static class S15PacketEntityRelMove extends S14PacketEntity {
        public S15PacketEntityRelMove() {
        }

        public S15PacketEntityRelMove(int n, byte b, byte b2, byte b3) {
            super(n);
            this.x = b;
            this.y = b2;
            this.z = b3;
        }

        @Override
        public void readPacketData(PacketBuffer data) throws IOException {
            super.readPacketData(data);
            this.x = data.readByte();
            this.y = data.readByte();
            this.z = data.readByte();
        }

        @Override
        public void writePacketData(PacketBuffer data) throws IOException {
            super.writePacketData(data);
            data.writeByte(this.x);
            data.writeByte(this.y);
            data.writeByte(this.z);
        }

        @Override
        public String serialize() {
            return super.serialize() + String.format(", xa=%d, ya=%d, za=%d", this.x, this.y, this.z);
        }

        @Override
        public void processPacket(INetHandler netHandler) {
            super.processPacket((INetHandlerPlayClient)netHandler);
        }
    }

    public static class S16PacketEntityLook extends S14PacketEntity {
        public S16PacketEntityLook() {
            this.rotate = true;
        }

        public S16PacketEntityLook(int n, byte b, byte b2) {
            super(n);
            this.yaw = b;
            this.pitch = b2;
            this.rotate = true;
        }

        @Override
        public void readPacketData(PacketBuffer data) throws IOException {
            super.readPacketData(data);
            this.yaw = data.readByte();
            this.pitch = data.readByte();
        }

        @Override
        public void writePacketData(PacketBuffer data) throws IOException {
            super.writePacketData(data);
            data.writeByte(this.yaw);
            data.writeByte(this.pitch);
        }

        @Override
        public String serialize() {
            return super.serialize() + String.format(", yRot=%d, xRot=%d", this.yaw, this.pitch);
        }

        @Override
        public void processPacket(INetHandler netHandler) {
            super.processPacket((INetHandlerPlayClient)netHandler);
        }
    }

    public static class S17PacketEntityLookMove extends S14PacketEntity {
        public S17PacketEntityLookMove() {
            this.rotate = true;
        }

        public S17PacketEntityLookMove(int n, byte b, byte b2, byte b3, byte b4, byte b5) {
            super(n);
            this.x = b;
            this.y = b2;
            this.z = b3;
            this.yaw = b4;
            this.pitch = b5;
            this.rotate = true;
        }

        @Override
        public void readPacketData(PacketBuffer data) throws IOException {
            super.readPacketData(data);
            this.x = data.readByte();
            this.y = data.readByte();
            this.z = data.readByte();
            this.yaw = data.readByte();
            this.pitch = data.readByte();
        }

        @Override
        public void writePacketData(PacketBuffer data) throws IOException {
            super.writePacketData(data);
            data.writeByte(this.x);
            data.writeByte(this.y);
            data.writeByte(this.z);
            data.writeByte(this.yaw);
            data.writeByte(this.pitch);
        }

        @Override
        public String serialize() {
            return super.serialize() + String.format(", xa=%d, ya=%d, za=%d, yRot=%d, xRot=%d", this.x, this.y, this.z, this.yaw, this.pitch);
        }

        @Override
        public void processPacket(INetHandler netHandler) {
            super.processPacket((INetHandlerPlayClient)netHandler);
        }
    }
}
