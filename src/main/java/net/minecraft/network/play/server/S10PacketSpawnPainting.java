package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S10PacketSpawnPainting extends Packet {
    public int id;
    public int intValue;
    public int intValue2;
    public int intValue3;
    public int intValue4;
    public String name;

    public S10PacketSpawnPainting() {
    }

    public S10PacketSpawnPainting(EntityPainting entityPainting) {
        this.id = entityPainting.getEntityId();
        this.intValue = entityPainting.intValue;
        this.intValue2 = entityPainting.intValue2;
        this.intValue3 = entityPainting.intValue3;
        this.intValue4 = entityPainting.hangingDirection;
        this.name = entityPainting.art.title;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readVarIntFromBuffer();
        this.name = data.readStringFromBuffer(EnumArt.maxArtTitleLength);
        this.intValue = data.readInt();
        this.intValue2 = data.readInt();
        this.intValue3 = data.readInt();
        this.intValue4 = data.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeVarIntToBuffer(this.id);
        data.writeStringToBuffer(this.name);
        data.writeInt(this.intValue);
        data.writeInt(this.intValue2);
        data.writeInt(this.intValue3);
        data.writeInt(this.intValue4);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnPainting(this);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, type=%s, x=%d, y=%d, z=%d", this.id, this.name, this.intValue, this.intValue2, this.intValue3);
    }

    public int getId() {
        return this.id;
    }

    public int getIntValue() {
        return this.intValue;
    }

    public int getIntValue2() {
        return this.intValue2;
    }

    public int getIntValue3() {
        return this.intValue3;
    }

    public int getIntValue4() {
        return this.intValue4;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
