package net.optifine;

import java.util.Comparator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;

public class ChunkPosComparator implements Comparator<ChunkCoordIntPair> {
   public int chunkPosX;
   public int chunkPosZ;
   public double yawRad;
   public double pitchNorm;

   public ChunkPosComparator(int n, int n2, double d, double d2) {
      this.chunkPosX = n;
      this.chunkPosZ = n2;
      this.yawRad = d;
      this.pitchNorm = 1.0 - MathHelper.clamp_double(Math.abs(d2) / (Math.PI / 2), 0.0, 1.0);
   }

   public int compare(ChunkCoordIntPair chunkCoordIntPair, ChunkCoordIntPair chunkCoordIntPair2) {
      int distSq = this.getDistSq(chunkCoordIntPair);
      int distSq2 = this.getDistSq(chunkCoordIntPair2);
      return distSq - distSq2;
   }

   public int getDistSq(ChunkCoordIntPair chunkCoordIntPair) {
      int n = chunkCoordIntPair.chunkXPos - this.chunkPosX;
      int n2 = chunkCoordIntPair.chunkZPos - this.chunkPosZ;
      int n3 = n * n + n2 * n2;
      double atan2 = MathHelper.atan2(n2, n);
      double abs = Math.abs(atan2 - this.yawRad);
      if (abs > Math.PI) {
         abs = (Math.PI * 2) - abs;
      }

      return (int)(n3 * 1000.0 * this.pitchNorm * abs * abs);
   }
}
