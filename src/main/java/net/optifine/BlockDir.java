package net.optifine;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public enum BlockDir {
   DOWN(EnumFacing.DOWN),
   UP(EnumFacing.UP),
   NORTH(EnumFacing.NORTH),
   SOUTH(EnumFacing.SOUTH),
   WEST(EnumFacing.WEST),
   EAST(EnumFacing.EAST),
   NORTH_WEST(EnumFacing.NORTH, EnumFacing.WEST),
   NORTH_EAST(EnumFacing.NORTH, EnumFacing.EAST),
   SOUTH_WEST(EnumFacing.SOUTH, EnumFacing.WEST),
   SOUTH_EAST(EnumFacing.SOUTH, EnumFacing.EAST),
   DOWN_NORTH(EnumFacing.DOWN, EnumFacing.NORTH),
   DOWN_SOUTH(EnumFacing.DOWN, EnumFacing.SOUTH),
   UP_NORTH(EnumFacing.UP, EnumFacing.NORTH),
   UP_SOUTH(EnumFacing.UP, EnumFacing.SOUTH),
   DOWN_WEST(EnumFacing.DOWN, EnumFacing.WEST),
   DOWN_EAST(EnumFacing.DOWN, EnumFacing.EAST),
   UP_WEST(EnumFacing.UP, EnumFacing.WEST),
   UP_EAST(EnumFacing.UP, EnumFacing.EAST);

   public EnumFacing facing1;
   public EnumFacing facing2;

   BlockDir(EnumFacing enumFacing) {
      this.facing1 = enumFacing;
   }

   BlockDir(EnumFacing enumFacing, EnumFacing enumFacing2) {
      this.facing1 = enumFacing;
      this.facing2 = enumFacing2;
   }

   public BlockPos offset(BlockPos blockPos) {
      blockPos = blockPos.offset(this.facing1, 1);
      if (this.facing2 != null) {
         blockPos = blockPos.offset(this.facing2, 1);
      }

      return blockPos;
   }

   public boolean isDouble() {
      return this.facing2 != null;
   }

   public int getIntValue() {
      int frontOffsetZ = this.facing1.getFrontOffsetZ();
      if (this.facing2 != null) {
         frontOffsetZ += this.facing2.getFrontOffsetZ();
      }

      return frontOffsetZ;
   }

   public int getIntValue2() {
      int frontOffsetY = this.facing1.getFrontOffsetY();
      if (this.facing2 != null) {
         frontOffsetY += this.facing2.getFrontOffsetY();
      }

      return frontOffsetY;
   }

   public int getIntValue3() {
      int frontOffsetX = this.facing1.getFrontOffsetX();
      if (this.facing2 != null) {
         frontOffsetX += this.facing2.getFrontOffsetX();
      }

      return frontOffsetX;
   }

   public EnumFacing getFacing2() {
      return this.facing2;
   }

   public EnumFacing getFacing1() {
      return this.facing1;
   }
}
