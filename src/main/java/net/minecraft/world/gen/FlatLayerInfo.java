package net.minecraft.world.gen;

import net.minecraft.block.Block;

public class FlatLayerInfo {
    public int layerCount = 1;
    public Block block;
    public int layerFillBlockMeta;
    public int layerMinimumY;

    public FlatLayerInfo(int n, Block block) {
        this.layerCount = n;
        this.block = block;
    }

    public FlatLayerInfo(int n, Block block, int n2) {
        this(n, block);
        this.layerFillBlockMeta = n2;
    }

    public int getLayerCount() {
        return this.layerCount;
    }

    public Block getBlock() {
        return this.block;
    }

    public int getFillBlockMeta() {
        return this.layerFillBlockMeta;
    }

    public int getMinY() {
        return this.layerMinimumY;
    }

    public void setMinY(int n) {
        this.layerMinimumY = n;
    }

    @Override
    public String toString() {
        String str = Integer.toString(Block.getIdFromBlock(this.block));
        if (this.layerCount > 1) {
            str = this.layerCount + "x" + str;
        }

        if (this.layerFillBlockMeta > 0) {
            str = str + ":" + this.layerFillBlockMeta;
        }

        return str;
    }
}
