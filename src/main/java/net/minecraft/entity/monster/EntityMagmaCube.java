package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityMagmaCube extends EntitySlime {
   public EntityMagmaCube(World worldIn) {
      super(worldIn);
      this.isImmuneToFire = true;
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   @Override
   public boolean isNotColliding() {
      return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
         && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
   }

   @Override
   public int getTotalArmorValue() {
      return this.getSlimeSize() * 3;
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 15728880;
   }

   @Override
   public float getBrightness(float partialTicks) {
      return 1.0F;
   }

   @Override
   public EnumParticleTypes getParticleType() {
      return EnumParticleTypes.FLAME;
   }

   @Override
   public EntitySlime createInstance() {
      return new EntityMagmaCube(this.worldObj);
   }

   @Override
   public Item getDropItem() {
      return Items.magma_cream;
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      Item dropItem = this.getDropItem();
      if (dropItem != null && this.getSlimeSize() > 1) {
         int n = this.rand.nextInt(4) - 2;
         if (lootingModifier > 0) {
            n += this.rand.nextInt(lootingModifier + 1);
         }

         for (int i = 0; i < n; i++) {
            this.dropItem(dropItem, 1);
         }
      }
   }

   @Override
   public boolean isBurning() {
      return false;
   }

   @Override
   public int getJumpDelay() {
      return super.getJumpDelay() * 4;
   }

   @Override
   public void alterSquishAmount() {
      this.squishAmount *= 0.9F;
   }

   @Override
   public void jump() {
      this.motionY = 0.42F + this.getSlimeSize() * 0.1F;
      this.isAirBorne = true;
   }

   @Override
   public void handleJumpLava() {
      this.motionY = 0.22F + this.getSlimeSize() * 0.05F;
      this.isAirBorne = true;
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
   }

   @Override
   public boolean canDamagePlayer() {
      return true;
   }

   @Override
   public int getAttackStrength() {
      return super.getAttackStrength() + 2;
   }

   @Override
   public String getJumpSound() {
      return this.getSlimeSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
   }

   @Override
   public boolean makesSoundOnLand() {
      return true;
   }
}
