package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint {
   public int index = -1;
   public int xCoord;
   public int yCoord;
   public int zCoord;
   public int hash;
   public float totalPathDistance;
   public float distanceToNext;
   public float distanceToTarget;
   public PathPoint previous;
   public boolean visited;

   public PathPoint(int x, int y, int z) {
      this.xCoord = x;
      this.yCoord = y;
      this.zCoord = z;
      this.hash = makeHash(x, y, z);
   }

   public static int makeHash(int x, int y, int z) {
      return y & 0xFF | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
   }

   public float distanceTo(PathPoint pathpointIn) {
      float f = pathpointIn.xCoord - this.xCoord;
      float f2 = pathpointIn.yCoord - this.yCoord;
      float f3 = pathpointIn.zCoord - this.zCoord;
      return MathHelper.sqrt_float(f * f + f2 * f2 + f3 * f3);
   }

   public float distanceToSquared(PathPoint pathpointIn) {
      float f = pathpointIn.xCoord - this.xCoord;
      float f2 = pathpointIn.yCoord - this.yCoord;
      float f3 = pathpointIn.zCoord - this.zCoord;
      return f * f + f2 * f2 + f3 * f3;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof PathPoint)) {
         return false;
      }

      PathPoint pathPoint = (PathPoint)obj;
      return this.hash == pathPoint.hash && this.xCoord == pathPoint.xCoord && this.yCoord == pathPoint.yCoord && this.zCoord == pathPoint.zCoord;
   }

   @Override
   public int hashCode() {
      return this.hash;
   }

   public boolean isAssigned() {
      return this.index >= 0;
   }

   @Override
   public String toString() {
      return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
   }
}
