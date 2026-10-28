package net.minecraft.util;

public class ChunkCoordinates implements Comparable {
    public int posX;
    public int posY;
    public int posZ;

    public ChunkCoordinates() {
    }

    public ChunkCoordinates(int x, int y, int z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public ChunkCoordinates(ChunkCoordinates coords) {
        this.posX = coords.posX;
        this.posY = coords.posY;
        this.posZ = coords.posZ;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChunkCoordinates)) {
            return false;
        }

        ChunkCoordinates chunkCoordinates = (ChunkCoordinates)obj;
        return this.posX == chunkCoordinates.posX && this.posY == chunkCoordinates.posY && this.posZ == chunkCoordinates.posZ;
    }

    @Override
    public int hashCode() {
        return this.posX + this.posZ << 8 + this.posY << 16;
    }

    public int compareTo(ChunkCoordinates chunkCoordinates) {
        return this.posY == chunkCoordinates.posY ? (this.posZ == chunkCoordinates.posZ ? this.posX - chunkCoordinates.posX : this.posZ - chunkCoordinates.posZ) : this.posY - chunkCoordinates.posY;
    }

    public void set(int x, int y, int z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public float getDistanceSquared(int x, int y, int z) {
        float f = this.posX - x;
        float f2 = this.posY - y;
        float f3 = this.posZ - z;
        return f * f + f2 * f2 + f3 * f3;
    }

    public float getDistanceSquaredToChunkCoordinates(ChunkCoordinates other) {
        return this.getDistanceSquared(other.posX, other.posY, other.posZ);
    }

    @Override
    public String toString() {
        return "Pos{x=" + this.posX + ", y=" + this.posY + ", z=" + this.posZ + '}';
    }

    @Override
    public int compareTo(Object obj) {
        return this.compareTo((ChunkCoordinates)obj);
    }
}
