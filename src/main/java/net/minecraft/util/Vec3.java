package net.minecraft.util;

public class Vec3 {
   public double xCoord;
   public double yCoord;
   public double zCoord;

   public Vec3(double x, double y, double z) {
      if (x == -0.0) {
         x = 0.0;
      }

      if (y == -0.0) {
         y = 0.0;
      }

      if (z == -0.0) {
         z = 0.0;
      }

      this.xCoord = x;
      this.yCoord = y;
      this.zCoord = z;
   }

   public Vec3(Vec3i vec3i) {
      this(vec3i.getX(), vec3i.getY(), vec3i.getZ());
   }

   public Vec3 subtractReverse(Vec3 vec) {
      return new Vec3(vec.xCoord - this.xCoord, vec.yCoord - this.yCoord, vec.zCoord - this.zCoord);
   }

   public Vec3 normalize() {
      double d = MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
      return d < 1.0E-4 ? new Vec3(0.0, 0.0, 0.0) : new Vec3(this.xCoord / d, this.yCoord / d, this.zCoord / d);
   }

   public double dotProduct(Vec3 vec) {
      return this.xCoord * vec.xCoord + this.yCoord * vec.yCoord + this.zCoord * vec.zCoord;
   }

   public Vec3 crossProduct(Vec3 vec) {
      return new Vec3(
         this.yCoord * vec.zCoord - this.zCoord * vec.yCoord,
         this.zCoord * vec.xCoord - this.xCoord * vec.zCoord,
         this.xCoord * vec.yCoord - this.yCoord * vec.xCoord
      );
   }

   public Vec3 subtract(Vec3 vec) {
      return this.subtract(vec.xCoord, vec.yCoord, vec.zCoord);
   }

   public Vec3 subtract(double x, double y, double z) {
      return this.addVector(-x, -y, -z);
   }

   public Vec3 add(Vec3 vec) {
      return this.addVector(vec.xCoord, vec.yCoord, vec.zCoord);
   }

   public Vec3 addVector(double x, double y, double z) {
      return new Vec3(this.xCoord + x, this.yCoord + y, this.zCoord + z);
   }

   public double distanceTo(Vec3 vec) {
      double d = vec.xCoord - this.xCoord;
      double d2 = vec.yCoord - this.yCoord;
      double d3 = vec.zCoord - this.zCoord;
      return MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
   }

   public double squareDistanceTo(Vec3 vec) {
      double d = vec.xCoord - this.xCoord;
      double d2 = vec.yCoord - this.yCoord;
      double d3 = vec.zCoord - this.zCoord;
      return d * d + d2 * d2 + d3 * d3;
   }

   public double lengthVector() {
      return MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
   }

   public Vec3 getIntermediateWithXValue(Vec3 vec, double x) {
      double d = vec.xCoord - this.xCoord;
      double d2 = vec.yCoord - this.yCoord;
      double d3 = vec.zCoord - this.zCoord;
      if (d * d < 1.0E-7F) {
         return null;
      }

      double d4 = (x - this.xCoord) / d;
      return d4 >= 0.0 && d4 <= 1.0 ? new Vec3(this.xCoord + d * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
   }

   public Vec3 getIntermediateWithYValue(Vec3 vec, double y) {
      double d = vec.xCoord - this.xCoord;
      double d2 = vec.yCoord - this.yCoord;
      double d3 = vec.zCoord - this.zCoord;
      if (d2 * d2 < 1.0E-7F) {
         return null;
      }

      double d4 = (y - this.yCoord) / d2;
      return d4 >= 0.0 && d4 <= 1.0 ? new Vec3(this.xCoord + d * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
   }

   public Vec3 getIntermediateWithZValue(Vec3 vec, double z) {
      double d = vec.xCoord - this.xCoord;
      double d2 = vec.yCoord - this.yCoord;
      double d3 = vec.zCoord - this.zCoord;
      if (d3 * d3 < 1.0E-7F) {
         return null;
      }

      double d4 = (z - this.zCoord) / d3;
      return d4 >= 0.0 && d4 <= 1.0 ? new Vec3(this.xCoord + d * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
   }

   @Override
   public String toString() {
      return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
   }

   public Vec3 rotatePitch(float pitch) {
      float cos = MathHelper.cos(pitch);
      float sin = MathHelper.sin(pitch);
      double xCoord = this.xCoord;
      double d = this.yCoord * cos + this.zCoord * sin;
      double d2 = this.zCoord * cos - this.yCoord * sin;
      return new Vec3(xCoord, d, d2);
   }

   public Vec3 rotateYaw(float yaw) {
      float cos = MathHelper.cos(yaw);
      float sin = MathHelper.sin(yaw);
      double d = this.xCoord * cos + this.zCoord * sin;
      double yCoord = this.yCoord;
      double d2 = this.zCoord * cos - this.xCoord * sin;
      return new Vec3(d, yCoord, d2);
   }
}
