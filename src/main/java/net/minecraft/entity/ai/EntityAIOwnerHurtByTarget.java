package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtByTarget extends EntityAITarget {
   public EntityTameable theDefendingTameable;
   public EntityLivingBase theOwnerAttacker;
   public int lastAttackedTime;

   public EntityAIOwnerHurtByTarget(EntityTameable theDefendingTameableIn) {
      super(theDefendingTameableIn, false);
      this.theDefendingTameable = theDefendingTameableIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theDefendingTameable.isTamed()) {
         return false;
      }

      EntityLivingBase owner = this.theDefendingTameable.getOwner();
      if (owner == null) {
         return false;
      }

      this.theOwnerAttacker = owner.getAITarget();
      int revengeTimer = owner.getRevengeTimer();
      return revengeTimer != this.lastAttackedTime
         && this.isSuitableTarget(this.theOwnerAttacker, false)
         && this.theDefendingTameable.shouldAttackEntity(this.theOwnerAttacker, owner);
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.theOwnerAttacker);
      EntityLivingBase owner = this.theDefendingTameable.getOwner();
      if (owner != null) {
         this.lastAttackedTime = owner.getRevengeTimer();
      }

      super.startExecuting();
   }
}
