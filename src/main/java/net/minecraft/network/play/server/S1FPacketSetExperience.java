package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1FPacketSetExperience extends Packet {
    public float health;
    public int food;
    public int xpLevel;

    public S1FPacketSetExperience() {
    }

    public S1FPacketSetExperience(float f, int n, int n2) {
        this.health = f;
        this.food = n;
        this.xpLevel = n2;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.health = data.readFloat();
        this.xpLevel = data.readShort();
        this.food = data.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeFloat(this.health);
        data.writeShort(this.xpLevel);
        data.writeShort(this.food);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSetExperience(this);
    }

    public float getBarProgress() {
        return this.health;
    }

    public int getExperienceLevel() {
        return this.food;
    }

    public int getExperience() {
        return this.xpLevel;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
