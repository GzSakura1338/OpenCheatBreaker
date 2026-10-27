package net.minecraft.entity.monster;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.world.World;

public abstract class EntityGolem extends EntityCreature implements IAnimals {
   public EntityGolem(World worldIn) {
      super(worldIn);
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
   }

   @Override
   public String getLivingSound() {
      return "none";
   }

   @Override
   public String getHurtSound() {
      return "none";
   }

   @Override
   public String getDeathSound() {
      return "none";
   }

   @Override
   public int getTalkInterval() {
      return 120;
   }

   @Override
   public boolean canDespawn() {
      return false;
   }
}
