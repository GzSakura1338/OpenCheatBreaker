package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S08PacketPlayerPosLook extends Packet {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean flag;

    public S08PacketPlayerPosLook() {
    }

    public S08PacketPlayerPosLook(double d, double d2, double d3, float f, float f2, boolean flag) {
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.yaw = f;
        this.pitch = f2;
        this.flag = flag;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.x = data.readDouble();
        this.y = data.readDouble();
        this.z = data.readDouble();
        this.yaw = data.readFloat();
        this.pitch = data.readFloat();
        this.flag = data.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeDouble(this.x);
        data.writeDouble(this.y);
        data.writeDouble(this.z);
        data.writeFloat(this.yaw);
        data.writeFloat(this.pitch);
        data.writeBoolean(this.flag);
    }

    public void processPacket(INetHandlerPlayClient handler) throws IOException {
        handler.handlePlayerPosLook(this);
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

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isFlag() {
        return this.flag;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
