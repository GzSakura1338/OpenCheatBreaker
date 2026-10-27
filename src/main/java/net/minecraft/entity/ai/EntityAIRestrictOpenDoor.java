package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIRestrictOpenDoor extends EntityAIBase {
   public EntityCreature entityObj;
   public VillageDoorInfo frontDoor;

   public EntityAIRestrictOpenDoor(EntityCreature creatureIn) {
      this.entityObj = creatureIn;
      if (!(creatureIn.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      if (this.entityObj.worldObj.isDaytime()) {
         return false;
      }

      BlockPos blockPos = new BlockPos(this.entityObj);
      Village nearestVillage = this.entityObj.worldObj.getVillageCollection().getNearestVillage(blockPos, 16);
      if (nearestVillage == null) {
         return false;
      }

      this.frontDoor = nearestVillage.getNearestDoor(blockPos);
      return this.frontDoor == null ? false : this.frontDoor.getDistanceToInsideBlockSq(blockPos) < 2.25;
   }

   @Override
   public boolean continueExecuting() {
      return this.entityObj.worldObj.isDaytime()
         ? false
         : !this.frontDoor.getIsDetachedFromVillageFlag() && this.frontDoor.checkBlockPos(new BlockPos(this.entityObj));
   }

   @Override
   public void startExecuting() {
      ((PathNavigateGround)this.entityObj.getNavigator()).setBreakDoors(false);
      ((PathNavigateGround)this.entityObj.getNavigator()).setEnterDoors(false);
   }

   @Override
   public void resetTask() {
      ((PathNavigateGround)this.entityObj.getNavigator()).setBreakDoors(true);
      ((PathNavigateGround)this.entityObj.getNavigator()).setEnterDoors(true);
      this.frontDoor = null;
   }

   @Override
   public void updateTask() {
      this.frontDoor.incrementDoorOpeningRestrictionCounter();
   }
}
