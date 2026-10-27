package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveIndoors extends EntityAIBase {
   public int insidePosX = -1;
   public VillageDoorInfo doorInfo;
   public int insidePosZ = -1;
   public EntityCreature entityObj;

   public EntityAIMoveIndoors(EntityCreature entityObjIn) {
      this.entityObj = entityObjIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      BlockPos blockPos = new BlockPos(this.entityObj);
      if ((!this.entityObj.worldObj.isDaytime() || this.entityObj.worldObj.isRaining() && !this.entityObj.worldObj.getBiomeGenForCoords(blockPos).canRain())
         && !this.entityObj.worldObj.provider.getHasNoSky()) {
         if (this.entityObj.getRNG().nextInt(50) != 0) {
            return false;
         }

         if (this.insidePosX != -1 && this.entityObj.getDistanceSq(this.insidePosX, this.entityObj.posY, this.insidePosZ) < 4.0) {
            return false;
         }

         Village nearestVillage = this.entityObj.worldObj.getVillageCollection().getNearestVillage(blockPos, 14);
         if (nearestVillage == null) {
            return false;
         }

         this.doorInfo = nearestVillage.getDoorInfo(blockPos);
         return this.doorInfo != null;
      } else {
         return false;
      }
   }

   @Override
   public boolean continueExecuting() {
      return !this.entityObj.getNavigator().noPath();
   }

   @Override
   public void startExecuting() {
      this.insidePosX = -1;
      BlockPos insideBlockPos = this.doorInfo.getInsideBlockPos();
      int n = insideBlockPos.getX();
      int n2 = insideBlockPos.getY();
      int n3 = insideBlockPos.getZ();
      if (this.entityObj.getDistanceSq(insideBlockPos) > 256.0) {
         Vec3 findRandomTargetBlockTowards = RandomPositionGenerator.findRandomTargetBlockTowards(this.entityObj, 14, 3, new Vec3(n + 0.5, n2, n3 + 0.5));
         if (findRandomTargetBlockTowards != null) {
            this.entityObj.getNavigator().tryMoveToXYZ(findRandomTargetBlockTowards.xCoord, findRandomTargetBlockTowards.yCoord, findRandomTargetBlockTowards.zCoord, 1.0);
         }
      } else {
         this.entityObj.getNavigator().tryMoveToXYZ(n + 0.5, n2, n3 + 0.5, 1.0);
      }
   }

   @Override
   public void resetTask() {
      this.insidePosX = this.doorInfo.getInsideBlockPos().getX();
      this.insidePosZ = this.doorInfo.getInsideBlockPos().getZ();
      this.doorInfo = null;
   }
}
