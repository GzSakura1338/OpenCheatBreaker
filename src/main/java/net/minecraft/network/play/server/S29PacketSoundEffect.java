package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.Validate;

public class S29PacketSoundEffect extends Packet {
    public int fixedY = Integer.MAX_VALUE;
    public String name;
    public int fixedX;
    public int fixedZ;
    public float volume;
    public int pitch;

    public S29PacketSoundEffect() {
    }

    public S29PacketSoundEffect(String str, double d, double d2, double d3, float f, float f2) {
        Validate.notNull(str, "name", new Object[0]);
        this.name = str;
        this.fixedX = (int)(d * 8.0);
        this.fixedY = (int)(d2 * 8.0);
        this.fixedZ = (int)(d3 * 8.0);
        this.volume = f;
        this.pitch = (int)(f2 * 63.0F);
        if (this.pitch < 0) {
            this.pitch = 0;
        }

        if (this.pitch > 255) {
            this.pitch = 255;
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.name = data.readStringFromBuffer(256);
        this.fixedX = data.readInt();
        this.fixedY = data.readInt();
        this.fixedZ = data.readInt();
        this.volume = data.readFloat();
        this.pitch = data.readUnsignedByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.name);
        data.writeInt(this.fixedX);
        data.writeInt(this.fixedY);
        data.writeInt(this.fixedZ);
        data.writeFloat(this.volume);
        data.writeByte(this.pitch);
    }

    public String getSound() {
        return this.name;
    }

    public double getX() {
        return this.fixedX / 8.0F;
    }

    public double getY() {
        return this.fixedY / 8.0F;
    }

    public double getZ() {
        return this.fixedZ / 8.0F;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch / 63.0F;
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSoundEffect(this);
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
