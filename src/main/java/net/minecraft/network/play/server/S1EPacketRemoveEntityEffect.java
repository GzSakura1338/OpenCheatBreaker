package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

public class S1EPacketRemoveEntityEffect extends Packet {
    public int entityId;
    public int effectId;

    public S1EPacketRemoveEntityEffect() {
    }

    public S1EPacketRemoveEntityEffect(int n, PotionEffect potionEffect) {
        this.entityId = n;
        this.effectId = potionEffect.getPotionID();
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.entityId = data.readInt();
        this.effectId = data.readUnsignedByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.entityId);
        data.writeByte(this.effectId);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleRemoveEntityEffect(this);
    }

    public int getId() {
        return this.entityId;
    }

    public int getEffectType() {
        return this.effectId;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
