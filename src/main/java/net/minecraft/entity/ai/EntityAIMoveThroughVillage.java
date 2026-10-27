package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveThroughVillage extends EntityAIBase {
   public List<VillageDoorInfo> doorList = Lists.newArrayList();
   public EntityCreature theEntity;
   public PathEntity entityPathNavigate;
   public VillageDoorInfo doorInfo;
   public double movementSpeed;
   public boolean isNocturnal;

   public EntityAIMoveThroughVillage(EntityCreature theEntityIn, double movementSpeedIn, boolean isNocturnalIn) {
      this.theEntity = theEntityIn;
      this.movementSpeed = movementSpeedIn;
      this.isNocturnal = isNocturnalIn;
      this.setMutexBits(1);
      if (!(theEntityIn.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      this.resizeDoorList();
      if (this.isNocturnal && this.theEntity.worldObj.isDaytime()) {
         return false;
      }

      Village nearestVillage = this.theEntity.worldObj.getVillageCollection().getNearestVillage(new BlockPos(this.theEntity), 0);
      if (nearestVillage == null) {
         return false;
      }

      this.doorInfo = this.findNearestDoor(nearestVillage);
      if (this.doorInfo == null) {
         return false;
      }

      PathNavigateGround navigator = (PathNavigateGround)this.theEntity.getNavigator();
      boolean enterDoors = navigator.getEnterDoors();
      navigator.setBreakDoors(false);
      this.entityPathNavigate = navigator.getPathToPos(this.doorInfo.getDoorBlockPos());
      navigator.setBreakDoors(enterDoors);
      if (this.entityPathNavigate != null) {
         return true;
      }

      Vec3 findRandomTargetBlockTowards = RandomPositionGenerator.findRandomTargetBlockTowards(
         this.theEntity,
         10,
         7,
         new Vec3(this.doorInfo.getDoorBlockPos().getX(), this.doorInfo.getDoorBlockPos().getY(), this.doorInfo.getDoorBlockPos().getZ())
      );
      if (findRandomTargetBlockTowards == null) {
         return false;
      }

      navigator.setBreakDoors(false);
      this.entityPathNavigate = this.theEntity.getNavigator().getPathToXYZ(findRandomTargetBlockTowards.xCoord, findRandomTargetBlockTowards.yCoord, findRandomTargetBlockTowards.zCoord);
      navigator.setBreakDoors(enterDoors);
      return this.entityPathNavigate != null;
   }

   @Override
   public boolean continueExecuting() {
      if (this.theEntity.getNavigator().noPath()) {
         return false;
      }

      float f = this.theEntity.width + 4.0F;
      return this.theEntity.getDistanceSq(this.doorInfo.getDoorBlockPos()) > f * f;
   }

   @Override
   public void startExecuting() {
      this.theEntity.getNavigator().setPath(this.entityPathNavigate, this.movementSpeed);
   }

   @Override
   public void resetTask() {
      if (this.theEntity.getNavigator().noPath() || this.theEntity.getDistanceSq(this.doorInfo.getDoorBlockPos()) < 16.0) {
         this.doorList.add(this.doorInfo);
      }
   }

   public VillageDoorInfo findNearestDoor(Village villageIn) {
      VillageDoorInfo villageDoorInfo = null;
      int n = Integer.MAX_VALUE;

      for (VillageDoorInfo villageDoorInfo2 : villageIn.getVillageDoorInfoList()) {
         int distanceSquared = villageDoorInfo2.getDistanceSquared(
            MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY), MathHelper.floor_double(this.theEntity.posZ)
         );
         if (distanceSquared < n && !this.doesDoorListContain(villageDoorInfo2)) {
            villageDoorInfo = villageDoorInfo2;
            n = distanceSquared;
         }
      }

      return villageDoorInfo;
   }

   public boolean doesDoorListContain(VillageDoorInfo doorInfoIn) {
      for (VillageDoorInfo villageDoorInfo : this.doorList) {
         if (doorInfoIn.getDoorBlockPos().equals(villageDoorInfo.getDoorBlockPos())) {
            return true;
         }
      }

      return false;
   }

   public void resizeDoorList() {
      if (this.doorList.size() > 15) {
         this.doorList.remove(0);
      }
   }
}
