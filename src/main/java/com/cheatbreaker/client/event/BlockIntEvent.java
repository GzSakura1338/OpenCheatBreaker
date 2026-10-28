package com.cheatbreaker.client.event;

import net.minecraft.block.Block;

public class BlockIntEvent extends CancellableEvent {
    public Block block;
    public int intValue1;
    public int intValue2;
    public int intValue3;

    public BlockIntEvent(Block block, int n, int n2, int n3) {
        this.block = block;
        this.intValue1 = n;
        this.intValue2 = n2;
        this.intValue3 = n3;
    }

    public int getIntValue1() {
        return this.intValue1;
    }

    public Block getBlock() {
        return this.block;
    }

    public int getIntValue2() {
        return this.intValue2;
    }

    public int getIntValue3() {
        return this.intValue3;
    }
}
