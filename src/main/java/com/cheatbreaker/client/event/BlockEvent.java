package com.cheatbreaker.client.event;

import net.minecraft.block.Block;

public class BlockEvent extends CancellableEvent {
    public Block block;

    public BlockEvent(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }
}
