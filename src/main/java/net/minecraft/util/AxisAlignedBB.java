package net.minecraft.util;

public class AxisAlignedBB {
   public double minX;
   public double minY;
   public double minZ;
   public double maxX;
   public double maxY;
   public double maxZ;

   public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
      this.minX = Math.min(x1, x2);
      this.minY = Math.min(y1, y2);
      this.minZ = Math.min(z1, z2);
      this.maxX = Math.max(x1, x2);
      this.maxY = Math.max(y1, y2);
      this.maxZ = Math.max(z1, z2);
   }

   public AxisAlignedBB(BlockPos pos1, BlockPos pos2) {
      this.minX = pos1.getX();
      this.minY = pos1.getY();
      this.minZ = pos1.getZ();
      this.maxX = pos2.getX();
      this.maxY = pos2.getY();
      this.maxZ = pos2.getZ();
   }

   public AxisAlignedBB addCoord(double x, double y, double z) {
      double minX = this.minX;
      double minY = this.minY;
      double minZ = this.minZ;
      double maxX = this.maxX;
      double maxY = this.maxY;
      double maxZ = this.maxZ;
      if (x < 0.0) {
         minX += x;
      } else if (x > 0.0) {
         maxX += x;
      }

      if (y < 0.0) {
         minY += y;
      } else if (y > 0.0) {
         maxY += y;
      }

      if (z < 0.0) {
         minZ += z;
      } else if (z > 0.0) {
         maxZ += z;
      }

      return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public AxisAlignedBB expand(double x, double y, double z) {
      double d = this.minX - x;
      double d2 = this.minY - y;
      double d3 = this.minZ - z;
      double d4 = this.maxX + x;
      double d5 = this.maxY + y;
      double d6 = this.maxZ + z;
      return new AxisAlignedBB(d, d2, d3, d4, d5, d6);
   }

   public AxisAlignedBB union(AxisAlignedBB other) {
      double min = Math.min(this.minX, other.minX);
      double min2 = Math.min(this.minY, other.minY);
      double min3 = Math.min(this.minZ, other.minZ);
      double max = Math.max(this.maxX, other.maxX);
      double max2 = Math.max(this.maxY, other.maxY);
      double max3 = Math.max(this.maxZ, other.maxZ);
      return new AxisAlignedBB(min, min2, min3, max, max2, max3);
   }

   public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
      double min = Math.min(x1, x2);
      double min2 = Math.min(y1, y2);
      double min3 = Math.min(z1, z2);
      double max = Math.max(x1, x2);
      double max2 = Math.max(y1, y2);
      double max3 = Math.max(z1, z2);
      return new AxisAlignedBB(min, min2, min3, max, max2, max3);
   }

   public AxisAlignedBB offset(double x, double y, double z) {
      return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
   }

   public double calculateXOffset(AxisAlignedBB other, double offsetX) {
      if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
         if (offsetX > 0.0 && other.maxX <= this.minX) {
            double d = this.minX - other.maxX;
            if (d < offsetX) {
               offsetX = d;
            }
         } else if (offsetX < 0.0 && other.minX >= this.maxX) {
            double d = this.maxX - other.minX;
            if (d > offsetX) {
               offsetX = d;
            }
         }

         return offsetX;
      } else {
         return offsetX;
      }
   }

   public double calculateYOffset(AxisAlignedBB other, double offsetY) {
      if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
         if (offsetY > 0.0 && other.maxY <= this.minY) {
            double d = this.minY - other.maxY;
            if (d < offsetY) {
               offsetY = d;
            }
         } else if (offsetY < 0.0 && other.minY >= this.maxY) {
            double d = this.maxY - other.minY;
            if (d > offsetY) {
               offsetY = d;
            }
         }

         return offsetY;
      } else {
         return offsetY;
      }
   }

   public double calculateZOffset(AxisAlignedBB other, double offsetZ) {
      if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY) {
         if (offsetZ > 0.0 && other.maxZ <= this.minZ) {
            double d = this.minZ - other.maxZ;
            if (d < offsetZ) {
               offsetZ = d;
            }
         } else if (offsetZ < 0.0 && other.minZ >= this.maxZ) {
            double d = this.maxZ - other.minZ;
            if (d > offsetZ) {
               offsetZ = d;
            }
         }

         return offsetZ;
      } else {
         return offsetZ;
      }
   }

   public boolean intersectsWith(AxisAlignedBB other) {
      return !(other.maxX > this.minX) || !(other.minX < this.maxX)
         ? false
         : (other.maxY > this.minY && other.minY < this.maxY ? other.maxZ > this.minZ && other.minZ < this.maxZ : false);
   }

   public boolean isVecInside(Vec3 vec) {
      return !(vec.xCoord > this.minX) || !(vec.xCoord < this.maxX)
         ? false
         : (vec.yCoord > this.minY && vec.yCoord < this.maxY ? vec.zCoord > this.minZ && vec.zCoord < this.maxZ : false);
   }

   public double getAverageEdgeLength() {
      double d = this.maxX - this.minX;
      double d2 = this.maxY - this.minY;
      double d3 = this.maxZ - this.minZ;
      return (d + d2 + d3) / 3.0;
   }

   public AxisAlignedBB contract(double x, double y, double z) {
      double d = this.minX + x;
      double d2 = this.minY + y;
      double d3 = this.minZ + z;
      double d4 = this.maxX - x;
      double d5 = this.maxY - y;
      double d6 = this.maxZ - z;
      return new AxisAlignedBB(d, d2, d3, d4, d5, d6);
   }

   public MovingObjectPosition calculateIntercept(Vec3 vecA, Vec3 vecB) {
      Vec3 intermediateWithXValue = vecA.getIntermediateWithXValue(vecB, this.minX);
      Vec3 intermediateWithXValue2 = vecA.getIntermediateWithXValue(vecB, this.maxX);
      Vec3 intermediateWithYValue = vecA.getIntermediateWithYValue(vecB, this.minY);
      Vec3 intermediateWithYValue2 = vecA.getIntermediateWithYValue(vecB, this.maxY);
      Vec3 intermediateWithZValue = vecA.getIntermediateWithZValue(vecB, this.minZ);
      Vec3 intermediateWithZValue2 = vecA.getIntermediateWithZValue(vecB, this.maxZ);
      if (!this.isVecInYZ(intermediateWithXValue)) {
         intermediateWithXValue = null;
      }

      if (!this.isVecInYZ(intermediateWithXValue2)) {
         intermediateWithXValue2 = null;
      }

      if (!this.isVecInXZ(intermediateWithYValue)) {
         intermediateWithYValue = null;
      }

      if (!this.isVecInXZ(intermediateWithYValue2)) {
         intermediateWithYValue2 = null;
      }

      if (!this.isVecInXY(intermediateWithZValue)) {
         intermediateWithZValue = null;
      }

      if (!this.isVecInXY(intermediateWithZValue2)) {
         intermediateWithZValue2 = null;
      }

      Vec3 vec3 = null;
      if (intermediateWithXValue != null) {
         vec3 = intermediateWithXValue;
      }

      if (intermediateWithXValue2 != null && (vec3 == null || vecA.squareDistanceTo(intermediateWithXValue2) < vecA.squareDistanceTo(vec3))) {
         vec3 = intermediateWithXValue2;
      }

      if (intermediateWithYValue != null && (vec3 == null || vecA.squareDistanceTo(intermediateWithYValue) < vecA.squareDistanceTo(vec3))) {
         vec3 = intermediateWithYValue;
      }

      if (intermediateWithYValue2 != null && (vec3 == null || vecA.squareDistanceTo(intermediateWithYValue2) < vecA.squareDistanceTo(vec3))) {
         vec3 = intermediateWithYValue2;
      }

      if (intermediateWithZValue != null && (vec3 == null || vecA.squareDistanceTo(intermediateWithZValue) < vecA.squareDistanceTo(vec3))) {
         vec3 = intermediateWithZValue;
      }

      if (intermediateWithZValue2 != null && (vec3 == null || vecA.squareDistanceTo(intermediateWithZValue2) < vecA.squareDistanceTo(vec3))) {
         vec3 = intermediateWithZValue2;
      }

      if (vec3 == null) {
         return null;
      }

      EnumFacing enumFacing = null;
      if (vec3 == intermediateWithXValue) {
         enumFacing = EnumFacing.WEST;
      } else if (vec3 == intermediateWithXValue2) {
         enumFacing = EnumFacing.EAST;
      } else if (vec3 == intermediateWithYValue) {
         enumFacing = EnumFacing.DOWN;
      } else if (vec3 == intermediateWithYValue2) {
         enumFacing = EnumFacing.UP;
      } else if (vec3 == intermediateWithZValue) {
         enumFacing = EnumFacing.NORTH;
      } else {
         enumFacing = EnumFacing.SOUTH;
      }

      return new MovingObjectPosition(vec3, enumFacing);
   }

   public boolean isVecInYZ(Vec3 vec) {
      return vec == null ? false : vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
   }

   public boolean isVecInXZ(Vec3 vec) {
      return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
   }

   public boolean isVecInXY(Vec3 vec) {
      return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
   }

   @Override
   public String toString() {
      return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   public boolean hasNaN() {
      return Double.isNaN(this.minX)
         || Double.isNaN(this.minY)
         || Double.isNaN(this.minZ)
         || Double.isNaN(this.maxX)
         || Double.isNaN(this.maxY)
         || Double.isNaN(this.maxZ);
   }
}
