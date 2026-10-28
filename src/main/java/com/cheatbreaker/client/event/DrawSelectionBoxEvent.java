package com.cheatbreaker.client.event;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;

public class DrawSelectionBoxEvent extends CancellableEvent {
    public Block block;
    public WorldClient worldClient;
    public int blockX;
    public int blockY;
    public int blockZ;
    public double doubleValue;
    public double doubleValue2;
    public double doubleValue3;
    public double doubleValue4;

    public int getBlockY() {
        return this.blockY;
    }

    public double getDoubleValue3() {
        return this.doubleValue3;
    }

    public Block getBlock() {
        return this.block;
    }

    public int getBlockX() {
        return this.blockX;
    }

    public WorldClient getWorldClient() {
        return this.worldClient;
    }

    public double getDoubleValue() {
        return this.doubleValue;
    }

    public DrawSelectionBoxEvent(Block block, WorldClient worldClient, int n, int n2, int n3, double d, double d2, double d3, double d4) {
        this.block = block;
        this.worldClient = worldClient;
        this.blockX = n;
        this.blockY = n2;
        this.blockZ = n3;
        this.doubleValue = d;
        this.doubleValue2 = d2;
        this.doubleValue3 = d3;
        this.doubleValue4 = d4;
    }

    public double getDoubleValue4() {
        return this.doubleValue4;
    }

    public int getBlockZ() {
        return this.blockZ;
    }

    public double getDoubleValue2() {
        return this.doubleValue2;
    }
}
