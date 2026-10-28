package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S24PacketBlockAction extends Packet {
    public int x;
    public int y;
    public int z;
    public int type;
    public int data;
    public Block block;

    public S24PacketBlockAction() {
    }

    public S24PacketBlockAction(int n, int n2, int n3, Block block, int n4, int n5) {
        this.x = n;
        this.y = n2;
        this.z = n3;
        this.type = n4;
        this.data = n5;
        this.block = block;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.x = data.readInt();
        this.y = data.readShort();
        this.z = data.readInt();
        this.type = data.readUnsignedByte();
        this.data = data.readUnsignedByte();
        this.block = Block.getBlockById(data.readVarIntFromBuffer() & 4095);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.x);
        data.writeShort(this.y);
        data.writeInt(this.z);
        data.writeByte(this.type);
        data.writeByte(this.data);
        data.writeVarIntToBuffer(Block.getIdFromBlock(this.block) & 4095);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleBlockAction(this);
    }

    public Block getBlockType() {
        return this.block;
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

    public int getData1() {
        return this.type;
    }

    public int getData2() {
        return this.data;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
