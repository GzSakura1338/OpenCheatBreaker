package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAITempt extends EntityAIBase {
   public EntityCreature temptedEntity;
   public double speed;
   public double targetX;
   public double targetY;
   public double targetZ;
   public double pitch;
   public double yaw;
   public EntityPlayer temptingPlayer;
   public int delayTemptCounter;
   public boolean isRunning;
   public Item temptItem;
   public boolean scaredByPlayerMovement;
   public boolean avoidWater;

   public EntityAITempt(EntityCreature temptedEntityIn, double speedIn, Item temptItemIn, boolean scaredByPlayerMovementIn) {
      this.temptedEntity = temptedEntityIn;
      this.speed = speedIn;
      this.temptItem = temptItemIn;
      this.scaredByPlayerMovement = scaredByPlayerMovementIn;
      this.setMutexBits(3);
      if (!(temptedEntityIn.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      if (this.delayTemptCounter > 0) {
         this.delayTemptCounter--;
         return false;
      }

      this.temptingPlayer = this.temptedEntity.worldObj.getClosestPlayerToEntity(this.temptedEntity, 10.0);
      if (this.temptingPlayer == null) {
         return false;
      }

      ItemStack currentEquippedItem = this.temptingPlayer.getCurrentEquippedItem();
      return currentEquippedItem == null ? false : currentEquippedItem.getItem() == this.temptItem;
   }

   @Override
   public boolean continueExecuting() {
      if (this.scaredByPlayerMovement) {
         if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 36.0) {
            if (this.temptingPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002) {
               return false;
            }

            if (Math.abs(this.temptingPlayer.rotationPitch - this.pitch) > 5.0 || Math.abs(this.temptingPlayer.rotationYaw - this.yaw) > 5.0) {
               return false;
            }
         } else {
            this.targetX = this.temptingPlayer.posX;
            this.targetY = this.temptingPlayer.posY;
            this.targetZ = this.temptingPlayer.posZ;
         }

         this.pitch = this.temptingPlayer.rotationPitch;
         this.yaw = this.temptingPlayer.rotationYaw;
      }

      return this.shouldExecute();
   }

   @Override
   public void startExecuting() {
      this.targetX = this.temptingPlayer.posX;
      this.targetY = this.temptingPlayer.posY;
      this.targetZ = this.temptingPlayer.posZ;
      this.isRunning = true;
      this.avoidWater = ((PathNavigateGround)this.temptedEntity.getNavigator()).getAvoidsWater();
      ((PathNavigateGround)this.temptedEntity.getNavigator()).setAvoidsWater(false);
   }

   @Override
   public void resetTask() {
      this.temptingPlayer = null;
      this.temptedEntity.getNavigator().clearPathEntity();
      this.delayTemptCounter = 100;
      this.isRunning = false;
      ((PathNavigateGround)this.temptedEntity.getNavigator()).setAvoidsWater(this.avoidWater);
   }

   @Override
   public void updateTask() {
      this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, 30.0F, this.temptedEntity.getVerticalFaceSpeed());
      if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 6.25) {
         this.temptedEntity.getNavigator().clearPathEntity();
      } else {
         this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.speed);
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
