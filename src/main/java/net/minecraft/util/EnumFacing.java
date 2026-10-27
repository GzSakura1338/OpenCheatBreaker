package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public enum EnumFacing implements IStringSerializable {
   DOWN(0, 1, -1, "down", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vec3i(1, 0, 0));

   public int index;
   public int horizontalIndex;
   public int opposite;
   public String name;
   public EnumFacing.Axis axis;
   public EnumFacing.AxisDirection axisDirection;
   public Vec3i directionVec;
   public static EnumFacing[] VALUES = new EnumFacing[6];
   public static EnumFacing[] HORIZONTALS = new EnumFacing[4];
   public static Map<String, EnumFacing> NAME_LOOKUP = Maps.newHashMap();

   EnumFacing(
      int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, EnumFacing.AxisDirection axisDirectionIn, EnumFacing.Axis axisIn, Vec3i directionVecIn
   ) {
      this.index = indexIn;
      this.horizontalIndex = horizontalIndexIn;
      this.opposite = oppositeIn;
      this.name = nameIn;
      this.axis = axisIn;
      this.axisDirection = axisDirectionIn;
      this.directionVec = directionVecIn;
   }

   public int getIndex() {
      return this.index;
   }

   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   public EnumFacing.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public EnumFacing getOpposite() {
      return VALUES[this.opposite];
   }

   public EnumFacing rotateAround(EnumFacing.Axis axis) {
      switch (axis) {
         case X:
            if (this != WEST && this != EAST) {
               return this.rotateX();
            }

            return this;
         case Y:
            if (this != UP && this != DOWN) {
               return this.rotateY();
            }

            return this;
         case Z:
            if (this != NORTH && this != SOUTH) {
               return this.rotateZ();
            }

            return this;
         default:
            throw new IllegalStateException("Unable to get CW facing for axis " + axis);
      }
   }

   public EnumFacing rotateY() {
      switch (this) {
         case NORTH:
            return EAST;
         case EAST:
            return SOUTH;
         case SOUTH:
            return WEST;
         case WEST:
            return NORTH;
         default:
            throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   public EnumFacing rotateX() {
      switch (this) {
         case NORTH:
            return DOWN;
         case EAST:
         case WEST:
         default:
            throw new IllegalStateException("Unable to get X-rotated facing of " + this);
         case SOUTH:
            return UP;
         case UP:
            return NORTH;
         case DOWN:
            return SOUTH;
      }
   }

   public EnumFacing rotateZ() {
      switch (this) {
         case EAST:
            return DOWN;
         case SOUTH:
         default:
            throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
         case WEST:
            return UP;
         case UP:
            return EAST;
         case DOWN:
            return WEST;
      }
   }

   public EnumFacing rotateYCCW() {
      switch (this) {
         case NORTH:
            return WEST;
         case EAST:
            return NORTH;
         case SOUTH:
            return EAST;
         case WEST:
            return SOUTH;
         default:
            throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int getFrontOffsetX() {
      return this.axis == EnumFacing.Axis.X ? this.axisDirection.getOffset() : 0;
   }

   public int getFrontOffsetY() {
      return this.axis == EnumFacing.Axis.Y ? this.axisDirection.getOffset() : 0;
   }

   public int getFrontOffsetZ() {
      return this.axis == EnumFacing.Axis.Z ? this.axisDirection.getOffset() : 0;
   }

   public String getName2() {
      return this.name;
   }

   public EnumFacing.Axis getAxis() {
      return this.axis;
   }

   public static EnumFacing byName(String name) {
      return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
   }

   public static EnumFacing getFront(int index) {
      return VALUES[MathHelper.abs_int(index % VALUES.length)];
   }

   public static EnumFacing getHorizontal(int n) {
      return HORIZONTALS[MathHelper.abs_int(n % HORIZONTALS.length)];
   }

   public static EnumFacing fromAngle(double angle) {
      return getHorizontal(MathHelper.floor_double(angle / 90.0 + 0.5) & 3);
   }

   public static EnumFacing random(Random rand) {
      return values()[rand.nextInt(values().length)];
   }

   public static EnumFacing getFacingFromVector(float f, float f2, float f3) {
      EnumFacing enumFacing = NORTH;
      float f4 = Float.MIN_VALUE;

      for (EnumFacing enumFacing2 : values()) {
         float f5 = f * enumFacing2.directionVec.getX() + f2 * enumFacing2.directionVec.getY() + f3 * enumFacing2.directionVec.getZ();
         if (f5 > f4) {
            f4 = f5;
            enumFacing = enumFacing2;
         }
      }

      return enumFacing;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public static EnumFacing getFacingFromAxis(EnumFacing.AxisDirection axisDirection, EnumFacing.Axis axis) {
      for (EnumFacing enumFacing : values()) {
         if (enumFacing.getAxisDirection() == axisDirection && enumFacing.getAxis() == axis) {
            return enumFacing;
         }
      }

      throw new IllegalArgumentException("No such direction: " + axisDirection + " " + axis);
   }

   public Vec3i getDirectionVec() {
      return this.directionVec;
   }

   static {
      for (EnumFacing enumFacing : values()) {
         VALUES[enumFacing.index] = enumFacing;
         if (enumFacing.getAxis().isHorizontal()) {
            HORIZONTALS[enumFacing.horizontalIndex] = enumFacing;
         }

         NAME_LOOKUP.put(enumFacing.getName2().toLowerCase(), enumFacing);
      }
   }

   public enum Axis implements IStringSerializable, Predicate<EnumFacing> {
      X("x", EnumFacing.Plane.HORIZONTAL),
      Y("y", EnumFacing.Plane.VERTICAL),
      Z("z", EnumFacing.Plane.HORIZONTAL);

      public String name;
      public EnumFacing.Plane plane;
      public static Map<String, EnumFacing.Axis> NAME_LOOKUP = Maps.newHashMap();

      Axis(String name, EnumFacing.Plane plane) {
         this.name = name;
         this.plane = plane;
      }

      public static EnumFacing.Axis byName(String name) {
         return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
      }

      public String getName2() {
         return this.name;
      }

      public boolean isVertical() {
         return this.plane == EnumFacing.Plane.VERTICAL;
      }

      public boolean isHorizontal() {
         return this.plane == EnumFacing.Plane.HORIZONTAL;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public boolean apply(EnumFacing enumFacing) {
         return enumFacing != null && enumFacing.getAxis() == this;
      }

      public EnumFacing.Plane getPlane() {
         return this.plane;
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (EnumFacing.Axis axis : values()) {
            NAME_LOOKUP.put(axis.getName2().toLowerCase(), axis);
         }
      }
   }

   public enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      public int offset;
      public String description;

      AxisDirection(int offset, String description) {
         this.offset = offset;
         this.description = description;
      }

      public int getOffset() {
         return this.offset;
      }

      @Override
      public String toString() {
         return this.description;
      }
   }

   public enum Plane implements Predicate<EnumFacing>, Iterable<EnumFacing> {
      HORIZONTAL,
      VERTICAL;

      public EnumFacing[] facings() {
         switch (this) {
            case HORIZONTAL:
               return new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
            case VERTICAL:
               return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
            default:
               throw new Error("Someone's been tampering with the universe!");
         }
      }

      public EnumFacing random(Random rand) {
         EnumFacing[] facings = this.facings();
         return facings[rand.nextInt(facings.length)];
      }

      public boolean apply(EnumFacing enumFacing) {
         return enumFacing != null && enumFacing.getAxis().getPlane() == this;
      }

      @Override
      public Iterator<EnumFacing> iterator() {
         return Iterators.forArray(this.facings());
      }
   }
}
