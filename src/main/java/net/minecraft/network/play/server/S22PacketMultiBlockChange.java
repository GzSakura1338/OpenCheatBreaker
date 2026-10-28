package net.minecraft.network.play.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class S22PacketMultiBlockChange extends Packet {
    public static Logger logger = LogManager.getLogger();
    public ChunkCoordIntPair pos;
    public int intValue;
    public byte[] byteArray;

    public S22PacketMultiBlockChange() {
    }

    public S22PacketMultiBlockChange(int n, short[] shorts, Chunk chunk) {
        this.pos = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);
        this.intValue = n;
        int n2 = 4 * n;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(n2);
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            for (int i = 0; i < n; i++) {
                int n3 = shorts[i] >> 12 & 15;
                int n4 = shorts[i] >> 8 & 15;
                int n5 = shorts[i] & 255;
                dataOutputStream.writeShort(shorts[i]);
                dataOutputStream.writeShort((short)((Block.getIdFromBlock(chunk.getBlock(n3, n5, n4)) & 4095) << 4 | chunk.getBlockMetadata(n3, n5, n4) & 15));
            }

            this.byteArray = byteArrayOutputStream.toByteArray();
            if (this.byteArray.length != n2) {
                throw new RuntimeException("Expected length " + n2 + " doesn't match received length " + this.byteArray.length);
            }
        } catch (IOException e) {
            logger.error("Couldn't create bulk block update packet", e);
            this.byteArray = null;
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.pos = new ChunkCoordIntPair(data.readInt(), data.readInt());
        this.intValue = data.readShort() & '\uffff';
        int readInt = data.readInt();
        if (readInt > 0) {
            this.byteArray = new byte[readInt];
            data.readBytes(this.byteArray);
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.pos.chunkXPos);
        data.writeInt(this.pos.chunkZPos);
        data.writeShort((short)this.intValue);
        if (this.byteArray != null) {
            data.writeInt(this.byteArray.length);
            data.writeBytes(this.byteArray);
        } else {
            data.writeInt(0);
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleMultiBlockChange(this);
    }

    @Override
    public String serialize() {
        return String.format("xc=%d, zc=%d, count=%d", this.pos.chunkXPos, this.pos.chunkZPos, this.intValue);
    }

    public ChunkCoordIntPair getPos() {
        return this.pos;
    }

    public byte[] getByteArray() {
        return this.byteArray;
    }

    public int getIntValue() {
        return this.intValue;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
