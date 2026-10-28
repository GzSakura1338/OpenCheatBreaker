package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage {
    public int yBase;
    public int blockRefCount;
    public int tickRefCount;
    public byte[] blockLSBArray;
    public NibbleArray blockMSBArray;
    public NibbleArray blockMetadataArray;
    public NibbleArray blocklightArray;
    public NibbleArray skylightArray;

    public ExtendedBlockStorage(int n, boolean flag) {
        this.yBase = n;
        this.blockLSBArray = new byte[4096];
        this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.blocklightArray = new NibbleArray(this.blockLSBArray.length, 4);
        if (flag) {
            this.skylightArray = new NibbleArray(this.blockLSBArray.length, 4);
        }
    }

    public Block getBlockByExtId(int n, int n2, int n3) {
        int n4 = this.blockLSBArray[n2 << 8 | n3 << 4 | n] & 255;
        if (this.blockMSBArray != null) {
            n4 |= this.blockMSBArray.get(n, n2, n3) << 8;
        }

        return Block.getBlockById(n4);
    }

    public void setExtBlockID(int n, int n2, int n3, Block block) {
        int n4 = this.blockLSBArray[n2 << 8 | n3 << 4 | n] & 255;
        if (this.blockMSBArray != null) {
            n4 |= this.blockMSBArray.get(n, n2, n3) << 8;
        }

        Block blockById = Block.getBlockById(n4);
        if (blockById != Blocks.air) {
            this.blockRefCount--;
            if (blockById.getTickRandomly()) {
                this.tickRefCount--;
            }
        }

        if (block != Blocks.air) {
            this.blockRefCount++;
            if (block.getTickRandomly()) {
                this.tickRefCount++;
            }
        }

        int idFromBlock = Block.getIdFromBlock(block);
        this.blockLSBArray[n2 << 8 | n3 << 4 | n] = (byte)(idFromBlock & 0xFF);
        if (idFromBlock > 255) {
            if (this.blockMSBArray == null) {
                this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
            }

            this.blockMSBArray.set(n, n2, n3, (idFromBlock & 3840) >> 8);
        } else if (this.blockMSBArray != null) {
            this.blockMSBArray.set(n, n2, n3, 0);
        }
    }

    public int getExtBlockMetadata(int n, int n2, int n3) {
        return this.blockMetadataArray.get(n, n2, n3);
    }

    public void setExtBlockMetadata(int n, int n2, int n3, int n4) {
        this.blockMetadataArray.set(n, n2, n3, n4);
    }

    public boolean isEmpty() {
        return this.blockRefCount == 0;
    }

    public boolean getNeedsRandomTick() {
        return this.tickRefCount > 0;
    }

    public int getYLocation() {
        return this.yBase;
    }

    public void setExtSkylightValue(int n, int n2, int n3, int n4) {
        this.skylightArray.set(n, n2, n3, n4);
    }

    public int getExtSkylightValue(int n, int n2, int n3) {
        return this.skylightArray.get(n, n2, n3);
    }

    public void setExtBlocklightValue(int n, int n2, int n3, int n4) {
        this.blocklightArray.set(n, n2, n3, n4);
    }

    public int getExtBlocklightValue(int n, int n2, int n3) {
        return this.blocklightArray.get(n, n2, n3);
    }

    public void removeInvalidBlocks() {
        this.blockRefCount = 0;
        this.tickRefCount = 0;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    Block blockByExtId = this.getBlockByExtId(i, j, k);
                    if (blockByExtId != Blocks.air) {
                        this.blockRefCount++;
                        if (blockByExtId.getTickRandomly()) {
                            this.tickRefCount++;
                        }
                    }
                }
            }
        }
    }

    public byte[] getBlockLSBArray() {
        return this.blockLSBArray;
    }

    public void clearMSBArray() {
        this.blockMSBArray = null;
    }

    public NibbleArray getBlockMSBArray() {
        return this.blockMSBArray;
    }

    public NibbleArray getMetadataArray() {
        return this.blockMetadataArray;
    }

    public NibbleArray getBlocklightArray() {
        return this.blocklightArray;
    }

    public NibbleArray getSkylightArray() {
        return this.skylightArray;
    }

    public void setBlockLSBArray(byte[] bytes) {
        this.blockLSBArray = bytes;
    }

    public void setBlockMSBArray(NibbleArray nibbleArray) {
        this.blockMSBArray = nibbleArray;
    }

    public void setBlockMetadataArray(NibbleArray nibbleArray) {
        this.blockMetadataArray = nibbleArray;
    }

    public void setBlocklightArray(NibbleArray nibbleArray) {
        this.blocklightArray = nibbleArray;
    }

    public void setSkylightArray(NibbleArray nibbleArray) {
        this.skylightArray = nibbleArray;
    }

    public NibbleArray createBlockMSBArray() {
        this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
        return this.blockMSBArray;
    }
}
