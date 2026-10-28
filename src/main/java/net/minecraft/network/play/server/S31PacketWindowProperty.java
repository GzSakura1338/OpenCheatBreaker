package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S31PacketWindowProperty extends Packet {
    public int syncId;
    public int propertyId;
    public int value;

    public S31PacketWindowProperty() {
    }

    public S31PacketWindowProperty(int n, int n2, int n3) {
        this.syncId = n;
        this.propertyId = n2;
        this.value = n3;
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleWindowProperty(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.syncId = data.readUnsignedByte();
        this.propertyId = data.readShort();
        this.value = data.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.syncId);
        data.writeShort(this.propertyId);
        data.writeShort(this.value);
    }

    public int getSyncId() {
        return this.syncId;
    }

    public int getPropertyId() {
        return this.propertyId;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
