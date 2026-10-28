package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C03PacketPlayer extends Packet {
    public double x;
    public double y;
    public double z;
    public double stance;
    public float yaw;
    public float pitch;
    public boolean onGround;
    public boolean changePosition;
    public boolean rotating;

    public C03PacketPlayer() {
    }

    public C03PacketPlayer(boolean flag) {
        this.onGround = flag;
    }

    public void processPacket(INetHandlerPlayServer handler) throws IOException {
        handler.processPlayer(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.onGround = data.readUnsignedByte() != 0;
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.onGround ? 1 : 0);
    }

    public double getPositionX() {
        return this.x;
    }

    public double getPositionY() {
        return this.y;
    }

    public double getPositionZ() {
        return this.z;
    }

    public double getStance() {
        return this.stance;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean isPositionChanged() {
        return this.changePosition;
    }

    public boolean getRotating() {
        return this.rotating;
    }

    public void setPositionChanged(boolean flag) {
        this.changePosition = flag;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }

    public static class C04PacketPlayerPosition extends C03PacketPlayer {
        public C04PacketPlayerPosition() {
            this.changePosition = true;
        }

        public C04PacketPlayerPosition(double d, double d2, double d3, double d4, boolean flag) {
            this.x = d;
            this.y = d2;
            this.stance = d3;
            this.z = d4;
            this.onGround = flag;
            this.changePosition = true;
        }

        @Override
        public void readPacketData(PacketBuffer data) throws IOException {
            this.x = data.readDouble();
            this.y = data.readDouble();
            this.stance = data.readDouble();
            this.z = data.readDouble();
            super.readPacketData(data);
        }

        @Override
        public void writePacketData(PacketBuffer data) throws IOException {
            data.writeDouble(this.x);
            data.writeDouble(this.y);
            data.writeDouble(this.stance);
            data.writeDouble(this.z);
            super.writePacketData(data);
        }

        @Override
        public void processPacket(INetHandler netHandler) throws IOException {
            super.processPacket((INetHandlerPlayServer)netHandler);
        }
    }

    public static class C05PacketPlayerLook extends C03PacketPlayer {
        public C05PacketPlayerLook() {
            this.rotating = true;
        }

        public C05PacketPlayerLook(float f, float f2, boolean flag) {
            this.yaw = f;
            this.pitch = f2;
            this.onGround = flag;
            this.rotating = true;
        }

        @Override
        public void readPacketData(PacketBuffer data) throws IOException {
            this.yaw = data.readFloat();
            this.pitch = data.readFloat();
            super.readPacketData(data);
        }

        @Override
        public void writePacketData(PacketBuffer data) throws IOException {
            data.writeFloat(this.yaw);
            data.writeFloat(this.pitch);
            super.writePacketData(data);
        }

        @Override
        public void processPacket(INetHandler netHandler) throws IOException {
            super.processPacket((INetHandlerPlayServer)netHandler);
        }
    }

    public static class C06PacketPlayerPosLook extends C03PacketPlayer {
        public C06PacketPlayerPosLook() {
            this.changePosition = true;
            this.rotating = true;
        }

        public C06PacketPlayerPosLook(double d, double d2, double d3, double d4, float f, float f2, boolean flag) {
            this.x = d;
            this.y = d2;
            this.stance = d3;
            this.z = d4;
            this.yaw = f;
            this.pitch = f2;
            this.onGround = flag;
            this.rotating = true;
            this.changePosition = true;
        }

        @Override
        public void readPacketData(PacketBuffer data) throws IOException {
            this.x = data.readDouble();
            this.y = data.readDouble();
            this.stance = data.readDouble();
            this.z = data.readDouble();
            this.yaw = data.readFloat();
            this.pitch = data.readFloat();
            super.readPacketData(data);
        }

        @Override
        public void writePacketData(PacketBuffer data) throws IOException {
            data.writeDouble(this.x);
            data.writeDouble(this.y);
            data.writeDouble(this.stance);
            data.writeDouble(this.z);
            data.writeFloat(this.yaw);
            data.writeFloat(this.pitch);
            super.writePacketData(data);
        }

        @Override
        public void processPacket(INetHandler netHandler) throws IOException {
            super.processPacket((INetHandlerPlayServer)netHandler);
        }
    }
}
