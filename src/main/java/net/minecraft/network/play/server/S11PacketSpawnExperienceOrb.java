package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S11PacketSpawnExperienceOrb extends Packet {
    public int id;
    public int x;
    public int y;
    public int z;
    public int experience;

    public S11PacketSpawnExperienceOrb() {
    }

    public S11PacketSpawnExperienceOrb(EntityXPOrb entityXPOrb) {
        this.id = entityXPOrb.getEntityId();
        this.x = MathHelper.floor_double(entityXPOrb.posX * 32.0);
        this.y = MathHelper.floor_double(entityXPOrb.posY * 32.0);
        this.z = MathHelper.floor_double(entityXPOrb.posZ * 32.0);
        this.experience = entityXPOrb.getXpValue();
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readVarIntFromBuffer();
        this.x = data.readInt();
        this.y = data.readInt();
        this.z = data.readInt();
        this.experience = data.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeVarIntToBuffer(this.id);
        data.writeInt(this.x);
        data.writeInt(this.y);
        data.writeInt(this.z);
        data.writeShort(this.experience);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnExperienceOrb(this);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, value=%d, x=%.2f, y=%.2f, z=%.2f", this.id, this.experience, this.x / 32.0F, this.y / 32.0F, this.z / 32.0F);
    }

    public int getId() {
        return this.id;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getExperience() {
        return this.experience;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
