package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityChicken extends EntityAnimal {
   public float wingRotation;
   public float destPos;
   public float prevMaxWingDeviation;
   public float prevFlapProgress;
   public float wingRotDelta = 1.0F;
   public int timeUntilNextEgg;
   public boolean chickenJockey;

   public EntityChicken(World worldIn) {
      super(worldIn);
      this.setSize(0.4F, 0.7F);
      this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.4));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(3, new EntityAITempt(this, 1.0, Items.wheat_seeds, false));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1));
      this.tasks.addTask(5, new EntityAIWander(this, 1.0));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
   }

   @Override
   public float getEyeHeight() {
      return this.height;
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.prevFlapProgress = this.wingRotation;
      this.prevMaxWingDeviation = this.destPos;
      this.destPos = (float)(this.destPos + (this.onGround ? -1 : 4) * 0.3);
      this.destPos = MathHelper.clamp_float(this.destPos, 0.0F, 1.0F);
      if (!this.onGround && this.wingRotDelta < 1.0F) {
         this.wingRotDelta = 1.0F;
      }

      this.wingRotDelta = (float)(this.wingRotDelta * 0.9);
      if (!this.onGround && this.motionY < 0.0) {
         this.motionY *= 0.6;
      }

      this.wingRotation = this.wingRotation + this.wingRotDelta * 2.0F;
      if (!this.worldObj.isRemote && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 0) {
         this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         this.dropItem(Items.egg, 1);
         this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
      }
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
   }

   @Override
   public String getLivingSound() {
      return "mob.chicken.say";
   }

   @Override
   public String getHurtSound() {
      return "mob.chicken.hurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.chicken.hurt";
   }

   @Override
   public void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.chicken.step", 0.15F, 1.0F);
   }

   @Override
   public Item getDropItem() {
      return Items.feather;
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int n = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingModifier);

      for (int i = 0; i < n; i++) {
         this.dropItem(Items.feather, 1);
      }

      if (this.isBurning()) {
         this.dropItem(Items.cooked_chicken, 1);
      } else {
         this.dropItem(Items.chicken, 1);
      }
   }

   public EntityChicken createChild(EntityAgeable ageable) {
      return new EntityChicken(this.worldObj);
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return stack != null && stack.getItem() == Items.wheat_seeds;
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.chickenJockey = tagCompund.getBoolean("IsChickenJockey");
      if (tagCompund.hasKey("EggLayTime")) {
         this.timeUntilNextEgg = tagCompund.getInteger("EggLayTime");
      }
   }

   @Override
   public int getExperiencePoints(EntityPlayer player) {
      return this.isChickenJockey() ? 10 : super.getExperiencePoints(player);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("IsChickenJockey", this.chickenJockey);
      tagCompound.setInteger("EggLayTime", this.timeUntilNextEgg);
   }

   @Override
   public boolean canDespawn() {
      return this.isChickenJockey() && this.riddenByEntity == null;
   }

   @Override
   public void updateRiderPosition() {
      super.updateRiderPosition();
      float sin = MathHelper.sin(this.renderYawOffset * (float) Math.PI / 180.0F);
      float cos = MathHelper.cos(this.renderYawOffset * (float) Math.PI / 180.0F);
      float f = 0.1F;
      float f2 = 0.0F;
      this.riddenByEntity
         .setPosition(this.posX + f * sin, this.posY + this.height * 0.5F + this.riddenByEntity.getYOffset() + f2, this.posZ - f * cos);
      if (this.riddenByEntity instanceof EntityLivingBase) {
         ((EntityLivingBase)this.riddenByEntity).renderYawOffset = this.renderYawOffset;
      }
   }

   public boolean isChickenJockey() {
      return this.chickenJockey;
   }

   public void setChickenJockey(boolean jockey) {
      this.chickenJockey = jockey;
   }
}
