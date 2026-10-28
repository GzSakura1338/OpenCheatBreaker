package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S21PacketChunkData.Extracted;
import net.minecraft.world.chunk.Chunk;

public class S26PacketMapChunkBulk extends Packet {
    public int[] xPositions;
    public int[] zPositions;
    public int[] intArray;
    public int[] intArray2;
    public byte[][] byteMatrix;
    public boolean notNether;
    public byte[] byteArray;
    public int intValue;
    public static byte[] byteArray2 = new byte[0];

    public S26PacketMapChunkBulk() {
    }

    public S26PacketMapChunkBulk(List list) {
        int size = list.size();
        this.xPositions = new int[size];
        this.zPositions = new int[size];
        this.intArray = new int[size];
        this.intArray2 = new int[size];
        this.byteMatrix = new byte[size][];
        this.notNether = !list.isEmpty() && !((Chunk)list.get(0)).worldObj.provider.hasNoSky;
        int n = 0;

        for (int i = 0; i < size; i++) {
            Chunk chunk = (Chunk)list.get(i);
            Extracted createExtracted = S21PacketChunkData.createExtracted(chunk, true, 65535);
            if (byteArray2.length < n + createExtracted.byteArray.length) {
                byte[] bytes = new byte[n + createExtracted.byteArray.length];
                System.arraycopy(byteArray2, 0, bytes, 0, byteArray2.length);
                byteArray2 = bytes;
            }

            System.arraycopy(createExtracted.byteArray, 0, byteArray2, n, createExtracted.byteArray.length);
            n += createExtracted.byteArray.length;
            this.xPositions[i] = chunk.xPosition;
            this.zPositions[i] = chunk.zPosition;
            this.intArray[i] = createExtracted.intValue;
            this.intArray2[i] = createExtracted.intValue2;
            this.byteMatrix[i] = createExtracted.byteArray;
        }

        Deflater deflater = new Deflater(-1);

        try {
            deflater.setInput(byteArray2, 0, n);
            deflater.finish();
            this.byteArray = new byte[n];
            this.intValue = deflater.deflate(this.byteArray);
        } finally {
            deflater.end();
        }
    }

    public static int constantInt() {
        return 5;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        short readShort = data.readShort();
        this.intValue = data.readInt();
        this.notNether = data.readBoolean();
        this.xPositions = new int[readShort];
        this.zPositions = new int[readShort];
        this.intArray = new int[readShort];
        this.intArray2 = new int[readShort];
        this.byteMatrix = new byte[readShort][];
        if (byteArray2.length < this.intValue) {
            byteArray2 = new byte[this.intValue];
        }

        data.readBytes(byteArray2, 0, this.intValue);
        byte[] bytes = new byte[S21PacketChunkData.constantInt() * readShort];
        Inflater inflater = new Inflater();
        inflater.setInput(byteArray2, 0, this.intValue);

        try {
            inflater.inflate(bytes);
        } catch (DataFormatException e) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        int n = 0;

        for (int i = 0; i < readShort; i++) {
            this.xPositions[i] = data.readInt();
            this.zPositions[i] = data.readInt();
            this.intArray[i] = data.readShort();
            this.intArray2[i] = data.readShort();
            int n2 = 0;
            int n3 = 0;

            for (int j = 0; j < 16; j++) {
                n2 += this.intArray[i] >> j & 1;
                n3 += this.intArray2[i] >> j & 1;
            }

            int n4 = 8192 * n2 + 256;
            n4 += 2048 * n3;
            if (this.notNether) {
                n4 += 2048 * n2;
            }

            this.byteMatrix[i] = new byte[n4];
            System.arraycopy(bytes, n, this.byteMatrix[i], 0, n4);
            n += n4;
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeShort(this.xPositions.length);
        data.writeInt(this.intValue);
        data.writeBoolean(this.notNether);
        data.writeBytes(this.byteArray, 0, this.intValue);

        for (int i = 0; i < this.xPositions.length; i++) {
            data.writeInt(this.xPositions[i]);
            data.writeInt(this.zPositions[i]);
            data.writeShort((short)(this.intArray[i] & 65535));
            data.writeShort((short)(this.intArray2[i] & 65535));
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleMapChunkBulk(this);
    }

    public int getXPos(int n) {
        return this.xPositions[n];
    }

    public int getZPos(int n) {
        return this.zPositions[n];
    }

    public int getXPosLength() {
        return this.xPositions.length;
    }

    public byte[] getDataBytes(int n) {
        return this.byteMatrix[n];
    }

    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.xPositions.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            sb.append(
                String.format(
                    "{x=%d, z=%d, sections=%d, adds=%d, data=%d}",
                    this.xPositions[i],
                    this.zPositions[i],
                    this.intArray[i],
                    this.intArray2[i],
                    this.byteMatrix[i].length
                )
            );
        }

        return String.format("size=%d, chunks=%d[%s]", this.intValue, this.xPositions.length, sb);
    }

    public int[] getIntArray() {
        return this.intArray;
    }

    public int[] getIntArray2() {
        return this.intArray2;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
