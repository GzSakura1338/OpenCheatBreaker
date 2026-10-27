package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIFleeSun extends EntityAIBase {
   public EntityCreature theCreature;
   public double shelterX;
   public double shelterY;
   public double shelterZ;
   public double movementSpeed;
   public World theWorld;

   public EntityAIFleeSun(EntityCreature theCreatureIn, double movementSpeedIn) {
      this.theCreature = theCreatureIn;
      this.movementSpeed = movementSpeedIn;
      this.theWorld = theCreatureIn.worldObj;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theWorld.isDaytime()) {
         return false;
      }

      if (!this.theCreature.isBurning()) {
         return false;
      }

      if (!this.theWorld.canSeeSky(new BlockPos(this.theCreature.posX, this.theCreature.getEntityBoundingBox().minY, this.theCreature.posZ))) {
         return false;
      }

      Vec3 findPossibleShelter = this.findPossibleShelter();
      if (findPossibleShelter == null) {
         return false;
      }

      this.shelterX = findPossibleShelter.xCoord;
      this.shelterY = findPossibleShelter.yCoord;
      this.shelterZ = findPossibleShelter.zCoord;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      return !this.theCreature.getNavigator().noPath();
   }

   @Override
   public void startExecuting() {
      this.theCreature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
   }

   public Vec3 findPossibleShelter() {
      Random rNG = this.theCreature.getRNG();
      BlockPos blockPos = new BlockPos(this.theCreature.posX, this.theCreature.getEntityBoundingBox().minY, this.theCreature.posZ);

      for (int i = 0; i < 10; i++) {
         BlockPos blockPos2 = blockPos.add(rNG.nextInt(20) - 10, rNG.nextInt(6) - 3, rNG.nextInt(20) - 10);
         if (!this.theWorld.canSeeSky(blockPos2) && this.theCreature.getBlockPathWeight(blockPos2) < 0.0F) {
            return new Vec3(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
         }
      }

      return null;
   }
}
