package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2DPacketOpenWindow extends Packet {
    public int windowId;
    public int inventoryType;
    public String windowTitle;
    public int slotCount;
    public boolean useProvidedTitle;
    public int entityId;

    public S2DPacketOpenWindow() {
    }

    public S2DPacketOpenWindow(int n, int n2, String str, int n3, boolean flag) {
        this.windowId = n;
        this.inventoryType = n2;
        this.windowTitle = str;
        this.slotCount = n3;
        this.useProvidedTitle = flag;
    }

    public S2DPacketOpenWindow(int n, int n2, String str, int n3, boolean flag, int n4) {
        this(n, n2, str, n3, flag);
        this.entityId = n4;
    }

    public void processPacket(INetHandlerPlayClient handler) throws IOException {
        handler.handleOpenWindow(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.windowId = data.readUnsignedByte();
        this.inventoryType = data.readUnsignedByte();
        this.windowTitle = data.readStringFromBuffer(32);
        this.slotCount = data.readUnsignedByte();
        this.useProvidedTitle = data.readBoolean();
        if (this.inventoryType == 11) {
            this.entityId = data.readInt();
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.windowId);
        data.writeByte(this.inventoryType);
        data.writeStringToBuffer(this.windowTitle);
        data.writeByte(this.slotCount);
        data.writeBoolean(this.useProvidedTitle);
        if (this.inventoryType == 11) {
            data.writeInt(this.entityId);
        }
    }

    public int getId() {
        return this.windowId;
    }

    public int getInventoryType() {
        return this.inventoryType;
    }

    public String getName() {
        return this.windowTitle;
    }

    public int getSlotCount() {
        return this.slotCount;
    }

    public boolean hasSlots() {
        return this.useProvidedTitle;
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
