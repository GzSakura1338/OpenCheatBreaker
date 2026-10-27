package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsRestriction extends EntityAIBase {
   public EntityCreature theEntity;
   public double movePosX;
   public double movePosY;
   public double movePosZ;
   public double movementSpeed;

   public EntityAIMoveTowardsRestriction(EntityCreature creatureIn, double speedIn) {
      this.theEntity = creatureIn;
      this.movementSpeed = speedIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (this.theEntity.isWithinHomeDistanceCurrentPosition()) {
         return false;
      }

      BlockPos homePosition = this.theEntity.getHomePosition();
      Vec3 findRandomTargetBlockTowards = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, new Vec3(homePosition.getX(), homePosition.getY(), homePosition.getZ()));
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
      return !this.theEntity.getNavigator().noPath();
   }

   @Override
   public void startExecuting() {
      this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.movementSpeed);
   }
}
