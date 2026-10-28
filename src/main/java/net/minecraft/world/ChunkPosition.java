package net.minecraft.world;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ChunkPosition {
    public int chunkPosX;
    public int chunkPosY;
    public int chunkPosZ;

    public ChunkPosition(int n, int n2, int n3) {
        this.chunkPosX = n;
        this.chunkPosY = n2;
        this.chunkPosZ = n3;
    }

    public ChunkPosition(Vec3 vec3) {
        this(MathHelper.floor_double(vec3.xCoord), MathHelper.floor_double(vec3.yCoord), MathHelper.floor_double(vec3.zCoord));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChunkPosition)) {
            return false;
        }

        ChunkPosition chunkPosition = (ChunkPosition)obj;
        return chunkPosition.chunkPosX == this.chunkPosX && chunkPosition.chunkPosY == this.chunkPosY && chunkPosition.chunkPosZ == this.chunkPosZ;
    }

    @Override
    public int hashCode() {
        return this.chunkPosX * 8976890 + this.chunkPosY * 981131 + this.chunkPosZ;
    }
}
