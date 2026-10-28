package net.minecraft.world;

public class ChunkCoordIntPair {
    public int cachedHashCode = 0;
    public int chunkXPos;
    public int chunkZPos;

    public ChunkCoordIntPair(int n, int n2) {
        this.chunkXPos = n;
        this.chunkZPos = n2;
    }

    public static long chunkXZ2Int(int n, int n2) {
        return n & 4294967295L | (n2 & 4294967295L) << 32;
    }

    @Override
    public int hashCode() {
        if (this.cachedHashCode == 0) {
            int n = 1664525 * this.chunkXPos + 1013904223;
            int n2 = 1664525 * (this.chunkZPos ^ -559038737) + 1013904223;
            this.cachedHashCode = n ^ n2;
        }

        return this.cachedHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ChunkCoordIntPair)) {
            return false;
        }

        ChunkCoordIntPair chunkCoordIntPair = (ChunkCoordIntPair)obj;
        return this.chunkXPos == chunkCoordIntPair.chunkXPos && this.chunkZPos == chunkCoordIntPair.chunkZPos;
    }

    public int getCenterXPos() {
        return (this.chunkXPos << 4) + 8;
    }

    public int getCenterZPosition() {
        return (this.chunkZPos << 4) + 8;
    }

    public ChunkPosition getCenter(int n) {
        return new ChunkPosition(this.getCenterXPos(), n, this.getCenterZPosition());
    }

    @Override
    public String toString() {
        return "[" + this.chunkXPos + ", " + this.chunkZPos + "]";
    }
}
