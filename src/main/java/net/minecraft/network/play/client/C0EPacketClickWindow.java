package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0EPacketClickWindow extends Packet {
    public int windowId;
    public int slotId;
    public int usedButton;
    public ItemStack clickedItem;
    public short actionNumber;
    public int mode;

    public C0EPacketClickWindow() {
    }

    public C0EPacketClickWindow(int n, int n2, int n3, int n4, ItemStack itemStack, short s) {
        this.windowId = n;
        this.slotId = n2;
        this.usedButton = n3;
        this.clickedItem = itemStack != null ? itemStack.copy() : null;
        this.actionNumber = s;
        this.mode = n4;
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processClickWindow(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.windowId = data.readByte();
        this.slotId = data.readShort();
        this.usedButton = data.readByte();
        this.actionNumber = data.readShort();
        this.mode = data.readByte();
        this.clickedItem = data.readItemStackFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.windowId);
        data.writeShort(this.slotId);
        data.writeByte(this.usedButton);
        data.writeShort(this.actionNumber);
        data.writeByte(this.mode);
        data.writeItemStackToBuffer(this.clickedItem);
    }

    @Override
    public String serialize() {
        return this.clickedItem != null
            ? String.format(
                "id=%d, slot=%d, button=%d, type=%d, itemid=%d, itemcount=%d, itemaux=%d",
                this.windowId,
                this.slotId,
                this.usedButton,
                this.mode,
                Item.getIdFromItem(this.clickedItem.getItem()),
                this.clickedItem.stackSize,
                this.clickedItem.getMetadata()
            )
            : String.format("id=%d, slot=%d, button=%d, type=%d, itemid=-1", this.windowId, this.slotId, this.usedButton, this.mode);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getSlotId() {
        return this.slotId;
    }

    public int getUsedButton() {
        return this.usedButton;
    }

    public short getActionNumber() {
        return this.actionNumber;
    }

    public ItemStack getClickedItem() {
        return this.clickedItem;
    }

    public int getMode() {
        return this.mode;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
