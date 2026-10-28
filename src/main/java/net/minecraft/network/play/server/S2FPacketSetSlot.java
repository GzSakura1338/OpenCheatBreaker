package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2FPacketSetSlot extends Packet {
    public int syncId;
    public int slot;
    public ItemStack stack;

    public S2FPacketSetSlot() {
    }

    public S2FPacketSetSlot(int n, int n2, ItemStack itemStack) {
        this.syncId = n;
        this.slot = n2;
        this.stack = itemStack == null ? null : itemStack.copy();
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSetSlot(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.syncId = data.readByte();
        this.slot = data.readShort();
        this.stack = data.readItemStackFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.syncId);
        data.writeShort(this.slot);
        data.writeItemStackToBuffer(this.stack);
    }

    public int getSyncId() {
        return this.syncId;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItemStack() {
        return this.stack;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
