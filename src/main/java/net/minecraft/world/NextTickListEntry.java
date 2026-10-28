package net.minecraft.world;

import net.minecraft.block.Block;

public class NextTickListEntry implements Comparable {
    public static long nextTickEntryID;
    public long tickEntryID;
    public int xCoord;
    public int yCoord;
    public int zCoord;
    public long scheduledTime;
    public int priority;
    public Block block;

    public NextTickListEntry(int n, int n2, int n3, Block block) {
        this.tickEntryID = nextTickEntryID++;
        this.xCoord = n;
        this.yCoord = n2;
        this.zCoord = n3;
        this.block = block;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        }

        NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
        return this.xCoord == nextTickListEntry.xCoord && this.yCoord == nextTickListEntry.yCoord && this.zCoord == nextTickListEntry.zCoord && Block.isEqualTo(this.block, nextTickListEntry.block);
    }

    @Override
    public int hashCode() {
        return (this.xCoord * 1024 * 1024 + this.zCoord * 1024 + this.yCoord) * 256;
    }

    public NextTickListEntry setScheduledTime(long l) {
        this.scheduledTime = l;
        return this;
    }

    public void setPriority(int n) {
        this.priority = n;
    }

    public int compareTo(NextTickListEntry nextTickListEntry) {
        return this.scheduledTime < nextTickListEntry.scheduledTime
            ? -1
            : (
                this.scheduledTime > nextTickListEntry.scheduledTime
                    ? 1
                    : (
                        this.priority != nextTickListEntry.priority
                            ? this.priority - nextTickListEntry.priority
                            : (this.tickEntryID < nextTickListEntry.tickEntryID ? -1 : (this.tickEntryID > nextTickListEntry.tickEntryID ? 1 : 0))
                    )
            );
    }

    @Override
    public String toString() {
        return Block.getIdFromBlock(this.block)
            + ": ("
            + this.xCoord
            + ", "
            + this.yCoord
            + ", "
            + this.zCoord
            + "), "
            + this.scheduledTime
            + ", "
            + this.priority
            + ", "
            + this.tickEntryID;
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public int compareTo(Object obj) {
        return this.compareTo((NextTickListEntry)obj);
    }
}
