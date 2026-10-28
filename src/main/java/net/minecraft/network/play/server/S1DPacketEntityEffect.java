package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

public class S1DPacketEntityEffect extends Packet {
    public int entityId;
    public byte effectId;
    public byte amplifier;
    public short duration;

    public S1DPacketEntityEffect() {
    }

    public S1DPacketEntityEffect(int n, PotionEffect potionEffect) {
        this.entityId = n;
        this.effectId = (byte)(potionEffect.getPotionID() & 0xFF);
        this.amplifier = (byte)(potionEffect.getAmplifier() & 0xFF);
        if (potionEffect.getDuration() > 32767) {
            this.duration = 32767;
        } else {
            this.duration = (short)potionEffect.getDuration();
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.entityId = data.readInt();
        this.effectId = data.readByte();
        this.amplifier = data.readByte();
        this.duration = data.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.entityId);
        data.writeByte(this.effectId);
        data.writeByte(this.amplifier);
        data.writeShort(this.duration);
    }

    public boolean isPermanent() {
        return this.duration == 32767;
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityEffect(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getEffectId() {
        return this.effectId;
    }

    public byte getAmplifier() {
        return this.amplifier;
    }

    public short getDuration() {
        return this.duration;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
