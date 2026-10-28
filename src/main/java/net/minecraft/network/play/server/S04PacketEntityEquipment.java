package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S04PacketEntityEquipment extends Packet {
    public int id;
    public int slot;
    public ItemStack stack;

    public S04PacketEntityEquipment() {
    }

    public S04PacketEntityEquipment(int n, int n2, ItemStack itemStack) {
        this.id = n;
        this.slot = n2;
        this.stack = itemStack == null ? null : itemStack.copy();
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readInt();
        this.slot = data.readShort();
        this.stack = data.readItemStackFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.id);
        data.writeShort(this.slot);
        data.writeItemStackToBuffer(this.stack);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityEquipment(this);
    }

    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public String serialize() {
        return String.format("entity=%d, slot=%d, item=%s", this.id, this.slot, this.stack);
    }

    public int getId() {
        return this.id;
    }

    public int getSlot() {
        return this.slot;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
