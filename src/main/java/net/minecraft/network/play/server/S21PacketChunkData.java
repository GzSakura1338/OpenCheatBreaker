package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class S21PacketChunkData extends Packet {
    public int chunkX;
    public int chunkZ;
    public boolean isFullChunk;
    public int intValue;
    public int sections;
    public byte[] byteArray;
    public byte[] byteArray2;
    public int intValue2;
    public static byte[] byteArray3 = new byte[196864];

    public S21PacketChunkData() {
    }

    public S21PacketChunkData(Chunk chunk, boolean flag, int n) {
        this.chunkX = chunk.xPosition;
        this.chunkZ = chunk.zPosition;
        this.isFullChunk = flag;
        S21PacketChunkData.Extracted createExtracted2 = createExtracted(chunk, flag, n);
        Deflater deflater = new Deflater(-1);
        this.intValue = createExtracted2.intValue2;
        this.sections = createExtracted2.intValue;

        try {
            this.byteArray = createExtracted2.byteArray;
            deflater.setInput(createExtracted2.byteArray, 0, createExtracted2.byteArray.length);
            deflater.finish();
            this.byteArray2 = new byte[createExtracted2.byteArray.length];
            this.intValue2 = deflater.deflate(this.byteArray2);
        } finally {
            deflater.end();
        }
    }

    public static int constantInt() {
        return 196864;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.chunkX = data.readInt();
        this.chunkZ = data.readInt();
        this.isFullChunk = data.readBoolean();
        this.sections = data.readShort();
        this.intValue = data.readShort();
        this.intValue2 = data.readInt();
        if (byteArray3.length < this.intValue2) {
            byteArray3 = new byte[this.intValue2];
        }

        data.readBytes(byteArray3, 0, this.intValue2);
        int n = 0;

        for (int i = 0; i < 16; i++) {
            n += this.sections >> i & 1;
        }

        int n2 = 12288 * n;
        if (this.isFullChunk) {
            n2 += 256;
        }

        this.byteArray = new byte[n2];
        Inflater inflater = new Inflater();
        inflater.setInput(byteArray3, 0, this.intValue2);

        try {
            inflater.inflate(this.byteArray);
        } catch (DataFormatException e) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.chunkX);
        data.writeInt(this.chunkZ);
        data.writeBoolean(this.isFullChunk);
        data.writeShort((short)(this.sections & 65535));
        data.writeShort((short)(this.intValue & 65535));
        data.writeInt(this.intValue2);
        data.writeBytes(this.byteArray2, 0, this.intValue2);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleChunkData(this);
    }

    @Override
    public String serialize() {
        return String.format(
            "x=%d, z=%d, full=%b, sects=%d, add=%d, size=%d", this.chunkX, this.chunkZ, this.isFullChunk, this.sections, this.intValue, this.intValue2
        );
    }

    public byte[] getByteArray() {
        return this.byteArray;
    }

    public static S21PacketChunkData.Extracted createExtracted(Chunk chunk, boolean flag, int n) {
        int n2 = 0;
        ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
        int n3 = 0;
        S21PacketChunkData.Extracted extracted = new S21PacketChunkData.Extracted();
        byte[] byteArray32 = byteArray3;
        if (flag) {
            chunk.sendUpdates = true;
        }

        for (int i = 0; i < blockStorageArray.length; i++) {
            if (blockStorageArray[i] != null && (!flag || !blockStorageArray[i].isEmpty()) && (n & 1 << i) != 0) {
                extracted.intValue |= 1 << i;
                if (blockStorageArray[i].getBlockMSBArray() != null) {
                    extracted.intValue2 |= 1 << i;
                    n3++;
                }
            }
        }

        for (int i = 0; i < blockStorageArray.length; i++) {
            if (blockStorageArray[i] != null && (!flag || !blockStorageArray[i].isEmpty()) && (n & 1 << i) != 0) {
                byte[] blockLSBArray = blockStorageArray[i].getBlockLSBArray();
                System.arraycopy(blockLSBArray, 0, byteArray32, n2, blockLSBArray.length);
                n2 += blockLSBArray.length;
            }
        }

        for (int i = 0; i < blockStorageArray.length; i++) {
            if (blockStorageArray[i] != null && (!flag || !blockStorageArray[i].isEmpty()) && (n & 1 << i) != 0) {
                NibbleArray metadataArray = blockStorageArray[i].getMetadataArray();
                System.arraycopy(metadataArray.data, 0, byteArray32, n2, metadataArray.data.length);
                n2 += metadataArray.data.length;
            }
        }

        for (int i = 0; i < blockStorageArray.length; i++) {
            if (blockStorageArray[i] != null && (!flag || !blockStorageArray[i].isEmpty()) && (n & 1 << i) != 0) {
                NibbleArray blocklightArray = blockStorageArray[i].getBlocklightArray();
                System.arraycopy(blocklightArray.data, 0, byteArray32, n2, blocklightArray.data.length);
                n2 += blocklightArray.data.length;
            }
        }

        if (!chunk.worldObj.provider.hasNoSky) {
            for (int i = 0; i < blockStorageArray.length; i++) {
                if (blockStorageArray[i] != null && (!flag || !blockStorageArray[i].isEmpty()) && (n & 1 << i) != 0) {
                    NibbleArray skylightArray = blockStorageArray[i].getSkylightArray();
                    System.arraycopy(skylightArray.data, 0, byteArray32, n2, skylightArray.data.length);
                    n2 += skylightArray.data.length;
                }
            }
        }

        if (n3 > 0) {
            for (int i = 0; i < blockStorageArray.length; i++) {
                if (blockStorageArray[i] != null && (!flag || !blockStorageArray[i].isEmpty()) && blockStorageArray[i].getBlockMSBArray() != null && (n & 1 << i) != 0) {
                    NibbleArray blockMSBArray = blockStorageArray[i].getBlockMSBArray();
                    System.arraycopy(blockMSBArray.data, 0, byteArray32, n2, blockMSBArray.data.length);
                    n2 += blockMSBArray.data.length;
                }
            }
        }

        if (flag) {
            byte[] biomeArray = chunk.getBiomeArray();
            System.arraycopy(biomeArray, 0, byteArray32, n2, biomeArray.length);
            n2 += biomeArray.length;
        }

        extracted.byteArray = new byte[n2];
        System.arraycopy(byteArray32, 0, extracted.byteArray, 0, n2);
        return extracted;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public int getSections() {
        return this.sections;
    }

    public int getIntValue() {
        return this.intValue;
    }

    public boolean shouldLoad() {
        return this.isFullChunk;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }

    public static class Extracted {
        public byte[] byteArray;
        public int intValue;
        public int intValue2;
    }
}
