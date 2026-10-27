package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityAIVillagerMate extends EntityAIBase {
   public EntityVillager villagerObj;
   public EntityVillager mate;
   public World worldObj;
   public int matingTimeout;
   public Village villageObj;

   public EntityAIVillagerMate(EntityVillager villagerIn) {
      this.villagerObj = villagerIn;
      this.worldObj = villagerIn.worldObj;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      if (this.villagerObj.getGrowingAge() != 0) {
         return false;
      }

      if (this.villagerObj.getRNG().nextInt(500) != 0) {
         return false;
      }

      this.villageObj = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(this.villagerObj), 0);
      if (this.villageObj == null) {
         return false;
      }

      if (this.checkSufficientDoorsPresentForNewVillager() && this.villagerObj.getIsWillingToMate(true)) {
         Entity findNearestEntityWithinAABB = this.worldObj
            .findNearestEntityWithinAABB(EntityVillager.class, this.villagerObj.getEntityBoundingBox().expand(8.0, 3.0, 8.0), this.villagerObj);
         if (findNearestEntityWithinAABB == null) {
            return false;
         }

         this.mate = (EntityVillager)findNearestEntityWithinAABB;
         return this.mate.getGrowingAge() == 0 && this.mate.getIsWillingToMate(true);
      } else {
         return false;
      }
   }

   @Override
   public void startExecuting() {
      this.matingTimeout = 300;
      this.villagerObj.setMating(true);
   }

   @Override
   public void resetTask() {
      this.villageObj = null;
      this.mate = null;
      this.villagerObj.setMating(false);
   }

   @Override
   public boolean continueExecuting() {
      return this.matingTimeout >= 0
         && this.checkSufficientDoorsPresentForNewVillager()
         && this.villagerObj.getGrowingAge() == 0
         && this.villagerObj.getIsWillingToMate(false);
   }

   @Override
   public void updateTask() {
      this.matingTimeout--;
      this.villagerObj.getLookHelper().setLookPositionWithEntity(this.mate, 10.0F, 30.0F);
      if (this.villagerObj.getDistanceSqToEntity(this.mate) > 2.25) {
         this.villagerObj.getNavigator().tryMoveToEntityLiving(this.mate, 0.25);
      } else if (this.matingTimeout == 0 && this.mate.isMating()) {
         this.giveBirth();
      }

      if (this.villagerObj.getRNG().nextInt(35) == 0) {
         this.worldObj.setEntityState(this.villagerObj, (byte)12);
      }
   }

   public boolean checkSufficientDoorsPresentForNewVillager() {
      if (!this.villageObj.isMatingSeason()) {
         return false;
      }

      int n = (int)(this.villageObj.getNumVillageDoors() * 0.35);
      return this.villageObj.getNumVillagers() < n;
   }

   public void giveBirth() {
      EntityVillager createChild = this.villagerObj.createChild(this.mate);
      this.mate.setGrowingAge(6000);
      this.villagerObj.setGrowingAge(6000);
      this.mate.setIsWillingToMate(false);
      this.villagerObj.setIsWillingToMate(false);
      createChild.setGrowingAge(-24000);
      createChild.setLocationAndAngles(this.villagerObj.posX, this.villagerObj.posY, this.villagerObj.posZ, 0.0F, 0.0F);
      this.worldObj.spawnEntityInWorld(createChild);
      this.worldObj.setEntityState(createChild, (byte)12);
   }
}
