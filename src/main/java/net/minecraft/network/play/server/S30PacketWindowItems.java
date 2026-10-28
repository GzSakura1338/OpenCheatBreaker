package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S30PacketWindowItems extends Packet {
    public int screenId;
    public ItemStack[] stacks;

    public S30PacketWindowItems() {
    }

    public S30PacketWindowItems(int n, List list) {
        this.screenId = n;
        this.stacks = new ItemStack[list.size()];

        for (int i = 0; i < this.stacks.length; i++) {
            ItemStack itemStack = (ItemStack)list.get(i);
            this.stacks[i] = itemStack == null ? null : itemStack.copy();
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.screenId = data.readUnsignedByte();
        short readShort = data.readShort();
        this.stacks = new ItemStack[readShort];

        for (int i = 0; i < readShort; i++) {
            this.stacks[i] = data.readItemStackFromBuffer();
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.screenId);
        data.writeShort(this.stacks.length);

        for (ItemStack itemStack : this.stacks) {
            data.writeItemStackToBuffer(itemStack);
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleWindowItems(this);
    }

    public int getScreenId() {
        return this.screenId;
    }

    public ItemStack[] getSlotStacks() {
        return this.stacks;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
