package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Vec3;

public class EntityAIWander extends EntityAIBase {
   public EntityCreature entity;
   public double xPosition;
   public double yPosition;
   public double zPosition;
   public double speed;
   public int executionChance;
   public boolean mustUpdate;

   public EntityAIWander(EntityCreature creatureIn, double speedIn) {
      this(creatureIn, speedIn, 120);
   }

   public EntityAIWander(EntityCreature creatureIn, double speedIn, int chance) {
      this.entity = creatureIn;
      this.speed = speedIn;
      this.executionChance = chance;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.mustUpdate) {
         if (this.entity.getAge() >= 100) {
            return false;
         }

         if (this.entity.getRNG().nextInt(this.executionChance) != 0) {
            return false;
         }
      }

      Vec3 findRandomTarget = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);
      if (findRandomTarget == null) {
         return false;
      }

      this.xPosition = findRandomTarget.xCoord;
      this.yPosition = findRandomTarget.yCoord;
      this.zPosition = findRandomTarget.zCoord;
      this.mustUpdate = false;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      return !this.entity.getNavigator().noPath();
   }

   @Override
   public void startExecuting() {
      this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
   }

   public void makeUpdate() {
      this.mustUpdate = true;
   }

   public void setExecutionChance(int newchance) {
      this.executionChance = newchance;
   }
}
