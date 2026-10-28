package net.optifine;

import net.minecraft.client.renderer.GLAllocation;

public class WrDisplayListBlock {
    public int start = -1;
    public int used = -1;
    public int end = -1;
    public static final int BLOCK_LENGTH = 16384;

    public WrDisplayListBlock() {
        this.start = GLAllocation.generateDisplayLists(16384);
        this.used = this.start;
        this.end = this.start + 16384;
    }

    public boolean canAllocate(int n) {
        return this.used + n < this.end;
    }

    public int allocate(int n) {
        if (!this.canAllocate(n)) {
            return -1;
        }

        int used = this.used;
        this.used += n;
        return used;
    }

    public void reset() {
        this.used = this.start;
    }

    public void deleteDisplayLists() {
        GLAllocation.deleteDisplayLists(this.start);
    }

    public int getUsed() {
        return this.used;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }
}
