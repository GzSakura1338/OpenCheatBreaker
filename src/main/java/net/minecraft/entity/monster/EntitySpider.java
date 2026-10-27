package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntitySpider extends EntityMob {
   public EntitySpider(World worldIn) {
      super(worldIn);
      this.setSize(1.4F, 0.9F);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(4, new EntitySpider.AISpiderAttack(this, EntityPlayer.class));
      this.tasks.addTask(4, new EntitySpider.AISpiderAttack(this, EntityIronGolem.class));
      this.tasks.addTask(5, new EntityAIWander(this, 0.8));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntitySpider.AISpiderTarget<>(this, EntityPlayer.class));
      this.targetTasks.addTask(3, new EntitySpider.AISpiderTarget<>(this, EntityIronGolem.class));
   }

   @Override
   public double getMountedYOffset() {
      return this.height * 0.5F;
   }

   @Override
   public PathNavigate getNewNavigator(World worldIn) {
      return new PathNavigateClimber(this, worldIn);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote) {
         this.setBesideClimbableBlock(this.isCollidedHorizontally);
      }
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
   }

   @Override
   public String getLivingSound() {
      return "mob.spider.say";
   }

   @Override
   public String getHurtSound() {
      return "mob.spider.say";
   }

   @Override
   public String getDeathSound() {
      return "mob.spider.death";
   }

   @Override
   public void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.spider.step", 0.15F, 1.0F);
   }

   @Override
   public Item getDropItem() {
      return Items.string;
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      super.dropFewItems(wasRecentlyHit, lootingModifier);
      if (wasRecentlyHit && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + lootingModifier) > 0)) {
         this.dropItem(Items.spider_eye, 1);
      }
   }

   @Override
   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   @Override
   public void setInWeb() {
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   @Override
   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      return potioneffectIn.getPotionID() == Potion.poison.id ? false : super.isPotionApplicable(potioneffectIn);
   }

   public boolean isBesideClimbableBlock() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 1);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -2);
      }

      this.dataWatcher.updateObject(16, watchableObjectByte);
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      if (this.worldObj.rand.nextInt(100) == 0) {
         EntitySkeleton entitySkeleton = new EntitySkeleton(this.worldObj);
         entitySkeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
         entitySkeleton.onInitialSpawn(difficulty, (IEntityLivingData)null);
         this.worldObj.spawnEntityInWorld(entitySkeleton);
         entitySkeleton.mountEntity(this);
      }

      if (livingdata == null) {
         livingdata = new EntitySpider.GroupData();
         if (this.worldObj.getDifficulty() == EnumDifficulty.HARD && this.worldObj.rand.nextFloat() < 0.1F * difficulty.getClampedAdditionalDifficulty()) {
            ((EntitySpider.GroupData)livingdata).setEffect(this.worldObj.rand);
         }
      }

      if (livingdata instanceof EntitySpider.GroupData) {
         int potionEffectId = ((EntitySpider.GroupData)livingdata).potionEffectId;
         if (potionEffectId > 0 && Potion.potionTypes[potionEffectId] != null) {
            this.addPotionEffect(new PotionEffect(potionEffectId, Integer.MAX_VALUE));
         }
      }

      return livingdata;
   }

   @Override
   public float getEyeHeight() {
      return 0.65F;
   }

   static class AISpiderAttack extends EntityAIAttackOnCollide {
      public AISpiderAttack(EntitySpider spider, Class<? extends Entity> targetClass) {
         super(spider, targetClass, 1.0, true);
      }

      @Override
      public boolean continueExecuting() {
         float brightness = this.attacker.getBrightness(1.0F);
         if (brightness >= 0.5F && this.attacker.getRNG().nextInt(100) == 0) {
            this.attacker.setAttackTarget((EntityLivingBase)null);
            return false;
         } else {
            return super.continueExecuting();
         }
      }

      @Override
      public double computeDouble(EntityLivingBase attackTarget) {
         return 4.0F + attackTarget.width;
      }
   }

   static class AISpiderTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget {
      public AISpiderTarget(EntitySpider spider, Class<T> classTarget) {
         super(spider, classTarget, true);
      }

      @Override
      public boolean shouldExecute() {
         float brightness = this.taskOwner.getBrightness(1.0F);
         return brightness >= 0.5F ? false : super.shouldExecute();
      }
   }

   public static class GroupData implements IEntityLivingData {
      public int potionEffectId;

      public void setEffect(Random rand) {
         int nextInt = rand.nextInt(5);
         if (nextInt <= 1) {
            this.potionEffectId = Potion.moveSpeed.id;
         } else if (nextInt <= 2) {
            this.potionEffectId = Potion.damageBoost.id;
         } else if (nextInt <= 3) {
            this.potionEffectId = Potion.regeneration.id;
         } else if (nextInt <= 4) {
            this.potionEffectId = Potion.invisibility.id;
         }
      }
   }
}
