package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIControlledByPlayer;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityPig extends EntityAnimal {
   public EntityAIControlledByPlayer aiControlledByPlayer;

   public EntityPig(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 0.9F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.25));
      this.tasks.addTask(2, this.aiControlledByPlayer = new EntityAIControlledByPlayer(this, 0.3F));
      this.tasks.addTask(3, new EntityAIMate(this, 1.0));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Items.carrot_on_a_stick, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Items.carrot, false));
      this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
   }

   @Override
   public boolean canBeSteered() {
      ItemStack heldItem = ((EntityPlayer)this.riddenByEntity).getHeldItem();
      return heldItem != null && heldItem.getItem() == Items.carrot_on_a_stick;
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("Saddle", this.getSaddled());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setSaddled(tagCompund.getBoolean("Saddle"));
   }

   @Override
   public String getLivingSound() {
      return "mob.pig.say";
   }

   @Override
   public String getHurtSound() {
      return "mob.pig.say";
   }

   @Override
   public String getDeathSound() {
      return "mob.pig.death";
   }

   @Override
   public void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.pig.step", 0.15F, 1.0F);
   }

   @Override
   public boolean interact(EntityPlayer player) {
      if (super.interact(player)) {
         return true;
      } else if (this.getSaddled() && !this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == player)) {
         player.mountEntity(this);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Item getDropItem() {
      return this.isBurning() ? Items.cooked_porkchop : Items.porkchop;
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int n = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + lootingModifier);

      for (int i = 0; i < n; i++) {
         if (this.isBurning()) {
            this.dropItem(Items.cooked_porkchop, 1);
         } else {
            this.dropItem(Items.porkchop, 1);
         }
      }

      if (this.getSaddled()) {
         this.dropItem(Items.saddle, 1);
      }
   }

   public boolean getSaddled() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setSaddled(boolean saddled) {
      if (saddled) {
         this.dataWatcher.updateObject(16, (byte)1);
      } else {
         this.dataWatcher.updateObject(16, (byte)0);
      }
   }

   @Override
   public void onStruckByLightning(EntityLightningBolt lightningBolt) {
      if (!this.worldObj.isRemote && !this.isDead) {
         EntityPigZombie entityPigZombie = new EntityPigZombie(this.worldObj);
         entityPigZombie.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
         entityPigZombie.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         entityPigZombie.setNoAI(this.isAIDisabled());
         if (this.hasCustomName()) {
            entityPigZombie.setCustomNameTag(this.getCustomNameTag());
            entityPigZombie.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
         }

         this.worldObj.spawnEntityInWorld(entityPigZombie);
         this.setDead();
      }
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      super.fall(distance, damageMultiplier);
      if (distance > 5.0F && this.riddenByEntity instanceof EntityPlayer) {
         ((EntityPlayer)this.riddenByEntity).triggerAchievement(AchievementList.flyPig);
      }
   }

   public EntityPig createChild(EntityAgeable ageable) {
      return new EntityPig(this.worldObj);
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return stack != null && stack.getItem() == Items.carrot;
   }

   public EntityAIControlledByPlayer getAIControlledByPlayer() {
      return this.aiControlledByPlayer;
   }
}
