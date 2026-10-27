package net.optifine.model;

import net.minecraft.util.EnumFacing;

public class QuadBounds {
   public float minX = Float.MAX_VALUE;
   public float minY = Float.MAX_VALUE;
   public float minZ = Float.MAX_VALUE;
   public float maxX = -Float.MAX_VALUE;
   public float maxY = -Float.MAX_VALUE;
   public float maxZ = -Float.MAX_VALUE;

   public QuadBounds(int[] ints) {
      int n = ints.length / 4;

      for (int i = 0; i < 4; i++) {
         int n2 = i * n;
         float intBitsToFloat = Float.intBitsToFloat(ints[n2 + 0]);
         float intBitsToFloat2 = Float.intBitsToFloat(ints[n2 + 1]);
         float intBitsToFloat3 = Float.intBitsToFloat(ints[n2 + 2]);
         if (this.minX > intBitsToFloat) {
            this.minX = intBitsToFloat;
         }

         if (this.minY > intBitsToFloat2) {
            this.minY = intBitsToFloat2;
         }

         if (this.minZ > intBitsToFloat3) {
            this.minZ = intBitsToFloat3;
         }

         if (this.maxX < intBitsToFloat) {
            this.maxX = intBitsToFloat;
         }

         if (this.maxY < intBitsToFloat2) {
            this.maxY = intBitsToFloat2;
         }

         if (this.maxZ < intBitsToFloat3) {
            this.maxZ = intBitsToFloat3;
         }
      }
   }

   public float getMinX() {
      return this.minX;
   }

   public float getMinY() {
      return this.minY;
   }

   public float getMinZ() {
      return this.minZ;
   }

   public float getMaxX() {
      return this.maxX;
   }

   public float getMaxY() {
      return this.maxY;
   }

   public float getMaxZ() {
      return this.maxZ;
   }

   public boolean isFaceQuad(EnumFacing enumFacing) {
      float f;
      float f2;
      float f3;
      switch (enumFacing) {
         case DOWN:
            f = this.getMinY();
            f2 = this.getMaxY();
            f3 = 0.0F;
            break;
         case UP:
            f = this.getMinY();
            f2 = this.getMaxY();
            f3 = 1.0F;
            break;
         case NORTH:
            f = this.getMinZ();
            f2 = this.getMaxZ();
            f3 = 0.0F;
            break;
         case SOUTH:
            f = this.getMinZ();
            f2 = this.getMaxZ();
            f3 = 1.0F;
            break;
         case WEST:
            f = this.getMinX();
            f2 = this.getMaxX();
            f3 = 0.0F;
            break;
         case EAST:
            f = this.getMinX();
            f2 = this.getMaxX();
            f3 = 1.0F;
            break;
         default:
            return false;
      }

      return f == f3 && f2 == f3;
   }

   public boolean isFullQuad(EnumFacing enumFacing) {
      float f;
      float f2;
      float f3;
      float f4;
      switch (enumFacing) {
         case DOWN:
         case UP:
            f = this.getMinX();
            f2 = this.getMaxX();
            f3 = this.getMinZ();
            f4 = this.getMaxZ();
            break;
         case NORTH:
         case SOUTH:
            f = this.getMinX();
            f2 = this.getMaxX();
            f3 = this.getMinY();
            f4 = this.getMaxY();
            break;
         case WEST:
         case EAST:
            f = this.getMinY();
            f2 = this.getMaxY();
            f3 = this.getMinZ();
            f4 = this.getMaxZ();
            break;
         default:
            return false;
      }

      return f == 0.0F && f2 == 1.0F && f3 == 0.0F && f4 == 1.0F;
   }
}
