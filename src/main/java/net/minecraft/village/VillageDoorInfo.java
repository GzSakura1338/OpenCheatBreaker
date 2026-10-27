package net.minecraft.village;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class VillageDoorInfo {
   public BlockPos doorBlockPos;
   public EnumFacing insideDirection;
   public BlockPos insideBlock;
   public int lastActivityTimestamp;
   public boolean isDetachedFromVillageFlag;
   public int doorOpeningRestrictionCounter;

   public VillageDoorInfo(BlockPos pos, int n, int n2, int n3) {
      this(pos, getFaceDirection(n, n2), n3);
   }

   public static EnumFacing getFaceDirection(int deltaX, int deltaZ) {
      return deltaX < 0 ? EnumFacing.WEST : (deltaX > 0 ? EnumFacing.EAST : (deltaZ < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH));
   }

   public VillageDoorInfo(BlockPos pos, EnumFacing facing, int timestamp) {
      this.doorBlockPos = pos;
      this.insideDirection = facing;
      this.insideBlock = pos.offset(facing, 2);
      this.lastActivityTimestamp = timestamp;
   }

   public int getDistanceSquared(int x, int y, int z) {
      return (int)this.doorBlockPos.distanceSq(x, y, z);
   }

   public int getDistanceToDoorBlockSq(BlockPos pos) {
      return (int)pos.distanceSq(this.getDoorBlockPos());
   }

   public int getDistanceToInsideBlockSq(BlockPos pos) {
      return (int)this.insideBlock.distanceSq(pos);
   }

   public boolean checkBlockPos(BlockPos pos) {
      int n = pos.getX() - this.doorBlockPos.getX();
      int n2 = pos.getZ() - this.doorBlockPos.getY();
      return n * this.insideDirection.getFrontOffsetX() + n2 * this.insideDirection.getFrontOffsetZ() >= 0;
   }

   public void resetDoorOpeningRestrictionCounter() {
      this.doorOpeningRestrictionCounter = 0;
   }

   public void incrementDoorOpeningRestrictionCounter() {
      this.doorOpeningRestrictionCounter++;
   }

   public int getDoorOpeningRestrictionCounter() {
      return this.doorOpeningRestrictionCounter;
   }

   public BlockPos getDoorBlockPos() {
      return this.doorBlockPos;
   }

   public BlockPos getInsideBlockPos() {
      return this.insideBlock;
   }

   public int getInsideOffsetX() {
      return this.insideDirection.getFrontOffsetX() * 2;
   }

   public int getInsideOffsetZ() {
      return this.insideDirection.getFrontOffsetZ() * 2;
   }

   public int getInsidePosY() {
      return this.lastActivityTimestamp;
   }

   public void setLastActivityTimestamp(int timestamp) {
      this.lastActivityTimestamp = timestamp;
   }

   public boolean getIsDetachedFromVillageFlag() {
      return this.isDetachedFromVillageFlag;
   }

   public void setIsDetachedFromVillageFlag(boolean detached) {
      this.isDetachedFromVillageFlag = detached;
   }
}
