package net.minecraft.world;

import net.minecraft.util.BlockPos;

public class ChunkCoordIntPair {
   public int cachedHashCode = 0;
   public int chunkXPos;
   public int chunkZPos;

   public ChunkCoordIntPair(int x, int z) {
      this.chunkXPos = x;
      this.chunkZPos = z;
   }

   public static long chunkXZ2Int(int x, int z) {
      return x & 4294967295L | (z & 4294967295L) << 32;
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

   public int getXStart() {
      return this.chunkXPos << 4;
   }

   public int getZStart() {
      return this.chunkZPos << 4;
   }

   public int getXEnd() {
      return (this.chunkXPos << 4) + 15;
   }

   public int getZEnd() {
      return (this.chunkZPos << 4) + 15;
   }

   public BlockPos getBlock(int x, int y, int z) {
      return new BlockPos((this.chunkXPos << 4) + x, y, (this.chunkZPos << 4) + z);
   }

   public BlockPos getCenterBlock(int y) {
      return new BlockPos(this.getCenterXPos(), y, this.getCenterZPosition());
   }

   @Override
   public String toString() {
      return "[" + this.chunkXPos + ", " + this.chunkZPos + "]";
   }
}
