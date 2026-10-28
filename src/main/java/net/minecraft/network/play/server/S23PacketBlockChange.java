package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S23PacketBlockChange extends Packet {
    public int x;
    public int y;
    public int z;
    public Block block;
    public int data;

    public S23PacketBlockChange() {
    }

    public S23PacketBlockChange(int n, int n2, int n3, World world) {
        this.x = n;
        this.y = n2;
        this.z = n3;
        this.block = world.getBlock(n, n2, n3);
        this.data = world.getBlockMetadata(n, n2, n3);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.x = data.readInt();
        this.y = data.readUnsignedByte();
        this.z = data.readInt();
        this.block = Block.getBlockById(data.readVarIntFromBuffer());
        this.data = data.readUnsignedByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.x);
        data.writeByte(this.y);
        data.writeInt(this.z);
        data.writeVarIntToBuffer(Block.getIdFromBlock(this.block));
        data.writeByte(this.data);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleBlockChange(this);
    }

    @Override
    public String serialize() {
        return String.format("type=%d, data=%d, x=%d, y=%d, z=%d", Block.getIdFromBlock(this.block), this.data, this.x, this.y, this.z);
    }

    public Block getBlock() {
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

    public int getBlockData() {
        return this.data;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
