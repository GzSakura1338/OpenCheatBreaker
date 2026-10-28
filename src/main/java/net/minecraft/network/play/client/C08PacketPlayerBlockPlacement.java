package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C08PacketPlayerBlockPlacement extends Packet {
    public int placedBlockX;
    public int placedBlockY;
    public int placedBlockZ;
    public int placedBlockDirection;
    public ItemStack stack;
    public float facingX;
    public float facingY;
    public float facingZ;

    public C08PacketPlayerBlockPlacement() {
    }

    public C08PacketPlayerBlockPlacement(int n, int n2, int n3, int n4, ItemStack itemStack, float f, float f2, float f3) {
        this.placedBlockX = n;
        this.placedBlockY = n2;
        this.placedBlockZ = n3;
        this.placedBlockDirection = n4;
        this.stack = itemStack != null ? itemStack.copy() : null;
        this.facingX = f;
        this.facingY = f2;
        this.facingZ = f3;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.placedBlockX = data.readInt();
        this.placedBlockY = data.readUnsignedByte();
        this.placedBlockZ = data.readInt();
        this.placedBlockDirection = data.readUnsignedByte();
        this.stack = data.readItemStackFromBuffer();
        this.facingX = data.readUnsignedByte() / 16.0F;
        this.facingY = data.readUnsignedByte() / 16.0F;
        this.facingZ = data.readUnsignedByte() / 16.0F;
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.placedBlockX);
        data.writeByte(this.placedBlockY);
        data.writeInt(this.placedBlockZ);
        data.writeByte(this.placedBlockDirection);
        data.writeItemStackToBuffer(this.stack);
        data.writeByte((int)(this.facingX * 16.0F));
        data.writeByte((int)(this.facingY * 16.0F));
        data.writeByte((int)(this.facingZ * 16.0F));
    }

    public void processPacket(INetHandlerPlayServer handler) throws IOException {
        handler.processPlayerBlockPlacement(this);
    }

    public int getPlacedBlockX() {
        return this.placedBlockX;
    }

    public int getPlacedBlockY() {
        return this.placedBlockY;
    }

    public int getPlacedBlockZ() {
        return this.placedBlockZ;
    }

    public int getPlacedBlockDirection() {
        return this.placedBlockDirection;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }

    public float getFacingZ() {
        return this.facingZ;
    }

    public float getFacingY() {
        return this.facingY;
    }

    public float getFacingX() {
        return this.facingX;
    }
}
