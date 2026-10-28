package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2APacketParticles extends Packet {
    public String text;
    public float x;
    public float y;
    public float z;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public float speed;
    public int count;

    public S2APacketParticles() {
    }

    public S2APacketParticles(String str, float f, float f2, float f3, float f4, float f5, float f6, float f7, int n) {
        this.text = str;
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.offsetX = f4;
        this.offsetY = f5;
        this.offsetZ = f6;
        this.speed = f7;
        this.count = n;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.text = data.readStringFromBuffer(64);
        this.x = data.readFloat();
        this.y = data.readFloat();
        this.z = data.readFloat();
        this.offsetX = data.readFloat();
        this.offsetY = data.readFloat();
        this.offsetZ = data.readFloat();
        this.speed = data.readFloat();
        this.count = data.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.text);
        data.writeFloat(this.x);
        data.writeFloat(this.y);
        data.writeFloat(this.z);
        data.writeFloat(this.offsetX);
        data.writeFloat(this.offsetY);
        data.writeFloat(this.offsetZ);
        data.writeFloat(this.speed);
        data.writeInt(this.count);
    }

    public String getText() {
        return this.text;
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

    public float getOffsetX() {
        return this.offsetX;
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    public float getOffsetZ() {
        return this.offsetZ;
    }

    public float getSpeed() {
        return this.speed;
    }

    public int getCount() {
        return this.count;
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleParticles(this);
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
