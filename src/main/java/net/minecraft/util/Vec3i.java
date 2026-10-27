package net.minecraft.util;

import com.google.common.base.MoreObjects;

public class Vec3i implements Comparable<Vec3i> {
   public static Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
   public int x;
   public int y;
   public int z;

   public Vec3i(int xIn, int yIn, int zIn) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
   }

   public Vec3i(double xIn, double yIn, double zIn) {
      this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof Vec3i)) {
         return false;
      }

      Vec3i vec3i = (Vec3i)obj;
      return this.getX() == vec3i.getX() && this.getY() == vec3i.getY() && this.getZ() == vec3i.getZ();
   }

   @Override
   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i vec3i) {
      return this.getY() == vec3i.getY() ? (this.getZ() == vec3i.getZ() ? this.getX() - vec3i.getX() : this.getZ() - vec3i.getZ()) : this.getY() - vec3i.getY();
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public Vec3i crossProduct(Vec3i vec) {
      return new Vec3i(
         this.getY() * vec.getZ() - this.getZ() * vec.getY(),
         this.getZ() * vec.getX() - this.getX() * vec.getZ(),
         this.getX() * vec.getY() - this.getY() * vec.getX()
      );
   }

   public double distanceSq(double toX, double toY, double toZ) {
      double d = this.getX() - toX;
      double d2 = this.getY() - toY;
      double d3 = this.getZ() - toZ;
      return d * d + d2 * d2 + d3 * d3;
   }

   public double distanceSqToCenter(double xIn, double yIn, double zIn) {
      double d = this.getX() + 0.5 - xIn;
      double d2 = this.getY() + 0.5 - yIn;
      double d3 = this.getZ() + 0.5 - zIn;
      return d * d + d2 * d2 + d3 * d3;
   }

   public double distanceSq(Vec3i to) {
      return this.distanceSq(to.getX(), to.getY(), to.getZ());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }
}
