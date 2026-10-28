package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0CPacketInput extends Packet {
    public float strafeSpeed;
    public float forwardSpeed;
    public boolean jumping;
    public boolean sneaking;

    public C0CPacketInput() {
    }

    public C0CPacketInput(float f, float f2, boolean flag, boolean flag2) {
        this.strafeSpeed = f;
        this.forwardSpeed = f2;
        this.jumping = flag;
        this.sneaking = flag2;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.strafeSpeed = data.readFloat();
        this.forwardSpeed = data.readFloat();
        this.jumping = data.readBoolean();
        this.sneaking = data.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeFloat(this.strafeSpeed);
        data.writeFloat(this.forwardSpeed);
        data.writeBoolean(this.jumping);
        data.writeBoolean(this.sneaking);
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processInput(this);
    }

    public float getStrafeSpeed() {
        return this.strafeSpeed;
    }

    public float getForwardSpeed() {
        return this.forwardSpeed;
    }

    public boolean isJumping() {
        return this.jumping;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
