package com.cheatbreaker.client.event;

public class BlockRenderPassEvent extends CancellableEvent {
    public int blockId;

    public BlockRenderPassEvent(int n) {
        this.blockId = n;
    }

    public int getBlockId() {
        return this.blockId;
    }
}
