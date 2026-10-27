package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsTarget extends EntityAIBase {
   public EntityCreature theEntity;
   public EntityLivingBase targetEntity;
   public double movePosX;
   public double movePosY;
   public double movePosZ;
   public double speed;
   public float maxTargetDistance;

   public EntityAIMoveTowardsTarget(EntityCreature creature, double speedIn, float targetMaxDistance) {
      this.theEntity = creature;
      this.speed = speedIn;
      this.maxTargetDistance = targetMaxDistance;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      this.targetEntity = this.theEntity.getAttackTarget();
      if (this.targetEntity == null) {
         return false;
      }

      if (this.targetEntity.getDistanceSqToEntity(this.theEntity) > this.maxTargetDistance * this.maxTargetDistance) {
         return false;
      }

      Vec3 findRandomTargetBlockTowards = RandomPositionGenerator.findRandomTargetBlockTowards(
         this.theEntity, 16, 7, new Vec3(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ)
      );
      if (findRandomTargetBlockTowards == null) {
         return false;
      }

      this.movePosX = findRandomTargetBlockTowards.xCoord;
      this.movePosY = findRandomTargetBlockTowards.yCoord;
      this.movePosZ = findRandomTargetBlockTowards.zCoord;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      return !this.theEntity.getNavigator().noPath()
         && this.targetEntity.isEntityAlive()
         && this.targetEntity.getDistanceSqToEntity(this.theEntity) < this.maxTargetDistance * this.maxTargetDistance;
   }

   @Override
   public void resetTask() {
      this.targetEntity = null;
   }

   @Override
   public void startExecuting() {
      this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
   }
}
