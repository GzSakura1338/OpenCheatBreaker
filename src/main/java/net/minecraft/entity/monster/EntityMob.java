package net.minecraft.entity.monster;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public abstract class EntityMob extends EntityCreature implements IMob {
   public EntityMob(World worldIn) {
      super(worldIn);
      this.experienceValue = 5;
   }

   @Override
   public void onLivingUpdate() {
      this.updateArmSwingProgress();
      float brightness = this.getBrightness(1.0F);
      if (brightness > 0.5F) {
         this.entityAge += 2;
      }

      super.onLivingUpdate();
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.setDead();
      }
   }

   @Override
   public String getSwimSound() {
      return "game.hostile.swim";
   }

   @Override
   public String getSplashSound() {
      return "game.hostile.swim.splash";
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (!super.attackEntityFrom(source, amount)) {
         return false;
      }

      Entity entity = source.getEntity();
      return this.riddenByEntity != entity && this.ridingEntity != entity ? true : true;
   }

   @Override
   public String getHurtSound() {
      return "game.hostile.hurt";
   }

   @Override
   public String getDeathSound() {
      return "game.hostile.die";
   }

   @Override
   public String getFallSoundString(int damageValue) {
      return damageValue > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
   }

   @Override
   public boolean attackEntityAsMob(Entity entityIn) {
      float attributeValue = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
      int n = 0;
      if (entityIn instanceof EntityLivingBase) {
         attributeValue += EnchantmentHelper.getModifierForCreature(this.getHeldItem(), ((EntityLivingBase)entityIn).getCreatureAttribute());
         n += EnchantmentHelper.getKnockbackModifier(this);
      }

      boolean attackEntityFrom = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), attributeValue);
      if (attackEntityFrom) {
         if (n > 0) {
            entityIn.addVelocity(
               -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * n * 0.5F,
               0.1,
               MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * n * 0.5F
            );
            this.motionX *= 0.6;
            this.motionZ *= 0.6;
         }

         int fireAspectModifier = EnchantmentHelper.getFireAspectModifier(this);
         if (fireAspectModifier > 0) {
            entityIn.setFire(fireAspectModifier * 4);
         }

         this.applyEnchantments(this, entityIn);
      }

      return attackEntityFrom;
   }

   @Override
   public float getBlockPathWeight(BlockPos pos) {
      return 0.5F - this.worldObj.getLightBrightness(pos);
   }

   public boolean isValidLightLevel() {
      BlockPos blockPos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      if (this.worldObj.getLightFor(EnumSkyBlock.SKY, blockPos) > this.rand.nextInt(32)) {
         return false;
      }

      int lightFromNeighbors = this.worldObj.getLightFromNeighbors(blockPos);
      if (this.worldObj.isThundering()) {
         int skylightSubtracted = this.worldObj.getSkylightSubtracted();
         this.worldObj.setSkylightSubtracted(10);
         lightFromNeighbors = this.worldObj.getLightFromNeighbors(blockPos);
         this.worldObj.setSkylightSubtracted(skylightSubtracted);
      }

      return lightFromNeighbors <= this.rand.nextInt(8);
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevel() && super.getCanSpawnHere();
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
   }

   @Override
   public boolean canDropLoot() {
      return true;
   }
}
