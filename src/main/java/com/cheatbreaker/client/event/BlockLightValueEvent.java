package com.cheatbreaker.client.event;

public class BlockLightValueEvent extends Event {
    public int blockId;
    public int lightValue;

    public int getLightValue() {
        return this.lightValue;
    }

    public int getBlockId() {
        return this.blockId;
    }

    public BlockLightValueEvent(int n, int n2) {
        this.blockId = n;
        this.lightValue = n2;
    }

    public void setLightValue(int n) {
        this.lightValue = n;
    }
}
