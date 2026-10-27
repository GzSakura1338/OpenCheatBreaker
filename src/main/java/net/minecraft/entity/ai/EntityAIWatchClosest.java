package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIWatchClosest extends EntityAIBase {
   public EntityLiving theWatcher;
   public Entity closestEntity;
   public Class<? extends Entity> watchedClass;
   public int lookTime;
   public float maxDistanceForPlayer;
   public float chance;

   public EntityAIWatchClosest(EntityLiving entitylivingIn, Class<? extends Entity> watchTargetClass, float maxDistance) {
      this.theWatcher = entitylivingIn;
      this.watchedClass = watchTargetClass;
      this.maxDistanceForPlayer = maxDistance;
      this.chance = 0.02F;
      this.setMutexBits(2);
   }

   public EntityAIWatchClosest(EntityLiving entitylivingIn, Class<? extends Entity> watchTargetClass, float maxDistance, float chanceIn) {
      this.theWatcher = entitylivingIn;
      this.watchedClass = watchTargetClass;
      this.maxDistanceForPlayer = maxDistance;
      this.chance = chanceIn;
      this.setMutexBits(2);
   }

   @Override
   public boolean shouldExecute() {
      if (this.theWatcher.getRNG().nextFloat() >= this.chance) {
         return false;
      }

      if (this.theWatcher.getAttackTarget() != null) {
         this.closestEntity = this.theWatcher.getAttackTarget();
      }

      if (this.watchedClass == EntityPlayer.class) {
         this.closestEntity = this.theWatcher.worldObj.getClosestPlayerToEntity(this.theWatcher, this.maxDistanceForPlayer);
      } else {
         this.closestEntity = this.theWatcher
            .worldObj
            .findNearestEntityWithinAABB(
               this.watchedClass, this.theWatcher.getEntityBoundingBox().expand(this.maxDistanceForPlayer, 3.0, this.maxDistanceForPlayer), this.theWatcher
            );
      }

      return this.closestEntity != null;
   }

   @Override
   public boolean continueExecuting() {
      return !this.closestEntity.isEntityAlive()
         ? false
         : (this.theWatcher.getDistanceSqToEntity(this.closestEntity) > this.maxDistanceForPlayer * this.maxDistanceForPlayer ? false : this.lookTime > 0);
   }

   @Override
   public void startExecuting() {
      this.lookTime = 40 + this.theWatcher.getRNG().nextInt(40);
   }

   @Override
   public void resetTask() {
      this.closestEntity = null;
   }

   @Override
   public void updateTask() {
      this.theWatcher
         .getLookHelper()
         .setLookPosition(
            this.closestEntity.posX,
            this.closestEntity.posY + this.closestEntity.getEyeHeight(),
            this.closestEntity.posZ,
            10.0F,
            this.theWatcher.getVerticalFaceSpeed()
         );
      this.lookTime--;
   }
}
