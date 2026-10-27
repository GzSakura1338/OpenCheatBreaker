package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityAIMoveToBlock extends EntityAIBase {
   public BlockPos destinationBlock = BlockPos.ORIGIN;
   public EntityCreature theEntity;
   public int runDelay;
   public int timeoutCounter;
   public int safeWaitingTime;
   public double movementSpeed;
   public boolean isAboveDestination;
   public int searchLength;

   public EntityAIMoveToBlock(EntityCreature creature, double speedIn, int length) {
      this.theEntity = creature;
      this.movementSpeed = speedIn;
      this.searchLength = length;
      this.setMutexBits(5);
   }

   @Override
   public boolean shouldExecute() {
      if (this.runDelay > 0) {
         this.runDelay--;
         return false;
      } else {
         this.runDelay = 200 + this.theEntity.getRNG().nextInt(200);
         return this.searchForDestination();
      }
   }

   @Override
   public boolean continueExecuting() {
      return this.timeoutCounter >= -this.safeWaitingTime && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.theEntity.worldObj, this.destinationBlock);
   }

   @Override
   public void startExecuting() {
      this.theEntity
         .getNavigator()
         .tryMoveToXYZ(this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, this.destinationBlock.getZ() + 0.5, this.movementSpeed);
      this.timeoutCounter = 0;
      this.safeWaitingTime = this.theEntity.getRNG().nextInt(this.theEntity.getRNG().nextInt(1200) + 1200) + 1200;
   }

   @Override
   public void resetTask() {
   }

   @Override
   public void updateTask() {
      if (this.theEntity.getDistanceSqToCenter(this.destinationBlock.up()) > 1.0) {
         this.isAboveDestination = false;
         this.timeoutCounter++;
         if (this.timeoutCounter % 40 == 0) {
            this.theEntity
               .getNavigator()
               .tryMoveToXYZ(this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, this.destinationBlock.getZ() + 0.5, this.movementSpeed);
         }
      } else {
         this.isAboveDestination = true;
         this.timeoutCounter--;
      }
   }

   public boolean getIsAboveDestination() {
      return this.isAboveDestination;
   }

   public boolean searchForDestination() {
      int searchLength = this.searchLength;
      boolean flag = true;
      BlockPos blockPos = new BlockPos(this.theEntity);

      for (int i = 0; i <= 1; i = (i > 0 ? -i : 1 - i)) {
         for (int j = 0; j < searchLength; j++) {
            for (int k = 0; k <= j; k = (k > 0 ? -k : 1 - k)) {
               for (int m = (k < j && k > -j ? j : 0); m <= j; m = (m > 0 ? -m : 1 - m)) {
                  BlockPos blockPos2 = blockPos.add(k, i - 1, m);
                  if (this.theEntity.isWithinHomeDistanceFromPosition(blockPos2) && this.shouldMoveTo(this.theEntity.worldObj, blockPos2)) {
                     this.destinationBlock = blockPos2;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public abstract boolean shouldMoveTo(World world, BlockPos blockPos);
}
