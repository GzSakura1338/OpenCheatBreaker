package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIAttackOnCollide extends EntityAIBase {
   public EntityCreature attacker;
   public World worldObj;
   public int attackTick;
   public double speedTowardsTarget;
   public boolean longMemory;
   public PathEntity entityPathEntity;
   public Class<? extends Entity> classTarget;
   public int delayCounter;
   public double targetX;
   public double targetY;
   public double targetZ;

   public EntityAIAttackOnCollide(EntityCreature creature, Class<? extends Entity> targetClass, double speedIn, boolean useLongMemory) {
      this(creature, speedIn, useLongMemory);
      this.classTarget = targetClass;
   }

   public EntityAIAttackOnCollide(EntityCreature creature, double speedIn, boolean useLongMemory) {
      this.attacker = creature;
      this.worldObj = creature.worldObj;
      this.speedTowardsTarget = speedIn;
      this.longMemory = useLongMemory;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase attackTarget = this.attacker.getAttackTarget();
      if (attackTarget == null) {
         return false;
      }

      if (!attackTarget.isEntityAlive()) {
         return false;
      }

      if (this.classTarget != null && !this.classTarget.isAssignableFrom(attackTarget.getClass())) {
         return false;
      }

      this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(attackTarget);
      return this.entityPathEntity != null;
   }

   @Override
   public boolean continueExecuting() {
      EntityLivingBase attackTarget = this.attacker.getAttackTarget();
      return attackTarget == null
         ? false
         : (
            !attackTarget.isEntityAlive()
               ? false
               : (!this.longMemory ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(attackTarget)))
         );
   }

   @Override
   public void startExecuting() {
      this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
      this.delayCounter = 0;
   }

   @Override
   public void resetTask() {
      this.attacker.getNavigator().clearPathEntity();
   }

   @Override
   public void updateTask() {
      EntityLivingBase attackTarget = this.attacker.getAttackTarget();
      this.attacker.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);
      double distanceSq = this.attacker.getDistanceSq(attackTarget.posX, attackTarget.getEntityBoundingBox().minY, attackTarget.posZ);
      double computeDouble = this.computeDouble(attackTarget);
      this.delayCounter--;
      if ((this.longMemory || this.attacker.getEntitySenses().canSee(attackTarget))
         && this.delayCounter <= 0
         && (
            this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
               || attackTarget.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0
               || this.attacker.getRNG().nextFloat() < 0.05F
         )) {
         this.targetX = attackTarget.posX;
         this.targetY = attackTarget.getEntityBoundingBox().minY;
         this.targetZ = attackTarget.posZ;
         this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
         if (distanceSq > 1024.0) {
            this.delayCounter += 10;
         } else if (distanceSq > 256.0) {
            this.delayCounter += 5;
         }

         if (!this.attacker.getNavigator().tryMoveToEntityLiving(attackTarget, this.speedTowardsTarget)) {
            this.delayCounter += 15;
         }
      }

      this.attackTick = Math.max(this.attackTick - 1, 0);
      if (distanceSq <= computeDouble && this.attackTick <= 0) {
         this.attackTick = 20;
         if (this.attacker.getHeldItem() != null) {
            this.attacker.swingItem();
         }

         this.attacker.attackEntityAsMob(attackTarget);
      }
   }

   public double computeDouble(EntityLivingBase attackTarget) {
      return this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width;
   }
}
