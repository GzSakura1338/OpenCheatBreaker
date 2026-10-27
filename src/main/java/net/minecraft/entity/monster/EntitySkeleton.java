package net.minecraft.entity.monster;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;

public class EntitySkeleton extends EntityMob implements IRangedAttackMob {
   public EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.0, 20, 60, 15.0F);
   public EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.2, false);

   public EntitySkeleton(World worldIn) {
      super(worldIn);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIRestrictSun(this));
      this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0));
      this.tasks.addTask(3, new EntityAIAvoidEntity<>(this, EntityWolf.class, 6.0F, 1.0, 1.2));
      this.tasks.addTask(4, new EntityAIWander(this, 1.0));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
      if (worldIn != null && !worldIn.isRemote) {
         this.setCombatTask();
      }
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(13, new Byte((byte)0));
   }

   @Override
   public String getLivingSound() {
      return "mob.skeleton.say";
   }

   @Override
   public String getHurtSound() {
      return "mob.skeleton.hurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.skeleton.death";
   }

   @Override
   public void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.skeleton.step", 0.15F, 1.0F);
   }

   @Override
   public boolean attackEntityAsMob(Entity entityIn) {
      if (super.attackEntityAsMob(entityIn)) {
         if (this.getSkeletonType() == 1 && entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(Potion.wither.id, 200));
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isDaytime() && !this.worldObj.isRemote) {
         float brightness = this.getBrightness(1.0F);
         BlockPos blockPos = new BlockPos(this.posX, Math.round(this.posY), this.posZ);
         if (brightness > 0.5F && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && this.worldObj.canSeeSky(blockPos)) {
            boolean flag = true;
            ItemStack equipmentInSlot = this.getEquipmentInSlot(4);
            if (equipmentInSlot != null) {
               if (equipmentInSlot.isItemStackDamageable()) {
                  equipmentInSlot.setItemDamage(equipmentInSlot.getItemDamage() + this.rand.nextInt(2));
                  if (equipmentInSlot.getItemDamage() >= equipmentInSlot.getMaxDamage()) {
                     this.renderBrokenItemStack(equipmentInSlot);
                     this.setCurrentItemOrArmor(4, (ItemStack)null);
                  }
               }

               flag = false;
            }

            if (flag) {
               this.setFire(8);
            }
         }
      }

      if (this.worldObj.isRemote && this.getSkeletonType() == 1) {
         this.setSize(0.72F, 2.535F);
      }

      super.onLivingUpdate();
   }

   @Override
   public void updateRidden() {
      super.updateRidden();
      if (this.ridingEntity instanceof EntityCreature) {
         EntityCreature ridingEntity = (EntityCreature)this.ridingEntity;
         this.renderYawOffset = ridingEntity.renderYawOffset;
      }
   }

   @Override
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (cause.getSourceOfDamage() instanceof EntityArrow && cause.getEntity() instanceof EntityPlayer) {
         EntityPlayer entity = (EntityPlayer)cause.getEntity();
         double d = entity.posX - this.posX;
         double d2 = entity.posZ - this.posZ;
         if (d * d + d2 * d2 >= 2500.0) {
            entity.triggerAchievement(AchievementList.snipeSkeleton);
         }
      } else if (cause.getEntity() instanceof EntityCreeper
         && ((EntityCreeper)cause.getEntity()).getPowered()
         && ((EntityCreeper)cause.getEntity()).isAIEnabled()) {
         ((EntityCreeper)cause.getEntity()).onHeadDropped();
         this.entityDropItem(new ItemStack(Items.skull, 1, this.getSkeletonType() == 1 ? 1 : 0), 0.0F);
      }
   }

   @Override
   public Item getDropItem() {
      return Items.arrow;
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      if (this.getSkeletonType() == 1) {
         int n = this.rand.nextInt(3 + lootingModifier) - 1;

         for (int i = 0; i < n; i++) {
            this.dropItem(Items.coal, 1);
         }
      } else {
         int nextInt = this.rand.nextInt(3 + lootingModifier);

         for (int i = 0; i < nextInt; i++) {
            this.dropItem(Items.arrow, 1);
         }
      }

      int nextInt = this.rand.nextInt(3 + lootingModifier);

      for (int i = 0; i < nextInt; i++) {
         this.dropItem(Items.bone, 1);
      }
   }

   @Override
   public void addRandomDrop() {
      if (this.getSkeletonType() == 1) {
         this.entityDropItem(new ItemStack(Items.skull, 1, 1), 0.0F);
      }
   }

   @Override
   public void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      super.setEquipmentBasedOnDifficulty(difficulty);
      this.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      if (this.worldObj.provider instanceof WorldProviderHell && this.getRNG().nextInt(5) > 0) {
         this.tasks.addTask(4, this.aiAttackOnCollide);
         this.setSkeletonType(1);
         this.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
         this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0);
      } else {
         this.tasks.addTask(4, this.aiArrowAttack);
         this.setEquipmentBasedOnDifficulty(difficulty);
         this.setEnchantmentBasedOnDifficulty(difficulty);
      }

      this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * difficulty.getClampedAdditionalDifficulty());
      if (this.getEquipmentInSlot(4) == null) {
         Calendar currentDate = this.worldObj.getCurrentDate();
         if (currentDate.get(2) + 1 == 10 && currentDate.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
            this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
            this.equipmentDropChances[4] = 0.0F;
         }
      }

      return livingdata;
   }

   public void setCombatTask() {
      this.tasks.removeTask(this.aiAttackOnCollide);
      this.tasks.removeTask(this.aiArrowAttack);
      ItemStack heldItem = this.getHeldItem();
      if (heldItem != null && heldItem.getItem() == Items.bow) {
         this.tasks.addTask(4, this.aiArrowAttack);
      } else {
         this.tasks.addTask(4, this.aiAttackOnCollide);
      }
   }

   @Override
   public void attackEntityWithRangedAttack(EntityLivingBase target, float f) {
      EntityArrow entityArrow = new EntityArrow(this.worldObj, this, target, 1.6F, 14 - this.worldObj.getDifficulty().getDifficultyId() * 4);
      int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
      int enchantmentLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
      entityArrow.setDamage(f * 2.0F + this.rand.nextGaussian() * 0.25 + this.worldObj.getDifficulty().getDifficultyId() * 0.11F);
      if (enchantmentLevel > 0) {
         entityArrow.setDamage(entityArrow.getDamage() + enchantmentLevel * 0.5 + 0.5);
      }

      if (enchantmentLevel2 > 0) {
         entityArrow.setKnockbackStrength(enchantmentLevel2);
      }

      if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0 || this.getSkeletonType() == 1) {
         entityArrow.setFire(100);
      }

      this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.worldObj.spawnEntityInWorld(entityArrow);
   }

   public int getSkeletonType() {
      return this.dataWatcher.getWatchableObjectByte(13);
   }

   public void setSkeletonType(int n) {
      this.dataWatcher.updateObject(13, (byte)n);
      this.isImmuneToFire = n == 1;
      if (n == 1) {
         this.setSize(0.72F, 2.535F);
      } else {
         this.setSize(0.6F, 1.95F);
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("SkeletonType", 99)) {
         byte byte2 = tagCompund.getByte("SkeletonType");
         this.setSkeletonType(byte2);
      }

      this.setCombatTask();
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setByte("SkeletonType", (byte)this.getSkeletonType());
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      super.setCurrentItemOrArmor(slotIn, stack);
      if (!this.worldObj.isRemote && slotIn == 0) {
         this.setCombatTask();
      }
   }

   @Override
   public float getEyeHeight() {
      return this.getSkeletonType() == 1 ? super.getEyeHeight() : 1.74F;
   }

   @Override
   public double getYOffset() {
      return this.isChild() ? 0.0 : -0.35;
   }
}
