package net.minecraft.entity.boss;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityWither extends EntityMob implements IRangedAttackMob, IBossDisplayData {
   public float[] sideHeadPitches = new float[2];
   public float[] sideHeadYaws = new float[2];
   public float[] prevSideHeadPitches = new float[2];
   public float[] prevSideHeadYaws = new float[2];
   public int[] intArray = new int[2];
   public int[] intArray2 = new int[2];
   public int blockBreakCounter;
   public static Predicate<Entity> attackEntitySelector = new Predicate<Entity>() {
      public boolean apply(Entity entity) {
         return entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
      }
   };

   public EntityWither(World worldIn) {
      super(worldIn);
      this.setHealth(this.getMaxHealth());
      this.setSize(0.9F, 3.5F);
      this.isImmuneToFire = true;
      ((PathNavigateGround)this.getNavigator()).setCanSwim(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0, 40, 20.0F));
      this.tasks.addTask(5, new EntityAIWander(this, 1.0));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 0, false, false, attackEntitySelector));
      this.experienceValue = 50;
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(0));
      this.dataWatcher.addObject(19, new Integer(0));
      this.dataWatcher.addObject(20, new Integer(0));
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("Invul", this.getInvulTime());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setInvulTime(tagCompund.getInteger("Invul"));
   }

   @Override
   public String getLivingSound() {
      return "mob.wither.idle";
   }

   @Override
   public String getHurtSound() {
      return "mob.wither.hurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.wither.death";
   }

   @Override
   public void onLivingUpdate() {
      this.motionY *= 0.6F;
      if (!this.worldObj.isRemote && this.getWatchedTargetId(0) > 0) {
         Entity entityByID = this.worldObj.getEntityByID(this.getWatchedTargetId(0));
         if (entityByID != null) {
            if (this.posY < entityByID.posY || !this.isArmored() && this.posY < entityByID.posY + 5.0) {
               if (this.motionY < 0.0) {
                  this.motionY = 0.0;
               }

               this.motionY = this.motionY + (0.5 - this.motionY) * 0.6F;
            }

            double d = entityByID.posX - this.posX;
            double d2 = entityByID.posZ - this.posZ;
            double d3 = d * d + d2 * d2;
            if (d3 > 9.0) {
               double d4 = MathHelper.sqrt_double(d3);
               this.motionX = this.motionX + (d / d4 * 0.5 - this.motionX) * 0.6F;
               this.motionZ = this.motionZ + (d2 / d4 * 0.5 - this.motionZ) * 0.6F;
            }
         }
      }

      if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05F) {
         this.rotationYaw = (float)MathHelper.atan2(this.motionZ, this.motionX) * (180.0F / (float)Math.PI) - 90.0F;
      }

      super.onLivingUpdate();

      for (int i = 0; i < 2; i++) {
         this.prevSideHeadYaws[i] = this.sideHeadYaws[i];
         this.prevSideHeadPitches[i] = this.sideHeadPitches[i];
      }

      for (int i = 0; i < 2; i++) {
         int watchedTargetId = this.getWatchedTargetId(i + 1);
         Entity entity = null;
         if (watchedTargetId > 0) {
            entity = this.worldObj.getEntityByID(watchedTargetId);
         }

         if (entity != null) {
            double headX = this.getHeadX(i + 1);
            double headY = this.getHeadY(i + 1);
            double headZ = this.getHeadZ(i + 1);
            double d = entity.posX - headX;
            double d2 = entity.posY + entity.getEyeHeight() - headY;
            double d3 = entity.posZ - headZ;
            double d4 = MathHelper.sqrt_double(d * d + d3 * d3);
            float f = (float)(MathHelper.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
            float f2 = (float)(-(MathHelper.atan2(d2, d4) * 180.0 / Math.PI));
            this.sideHeadPitches[i] = this.getNextAngle(this.sideHeadPitches[i], f2, 40.0F);
            this.sideHeadYaws[i] = this.getNextAngle(this.sideHeadYaws[i], f, 10.0F);
         } else {
            this.sideHeadYaws[i] = this.getNextAngle(this.sideHeadYaws[i], this.renderYawOffset, 10.0F);
         }
      }

      boolean isArmored = this.isArmored();

      for (int i = 0; i < 3; i++) {
         double headX = this.getHeadX(i);
         double headY = this.getHeadY(i);
         double headZ = this.getHeadZ(i);
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.SMOKE_NORMAL,
               headX + this.rand.nextGaussian() * 0.3F,
               headY + this.rand.nextGaussian() * 0.3F,
               headZ + this.rand.nextGaussian() * 0.3F,
               0.0,
               0.0,
               0.0
            );
         if (isArmored && this.worldObj.rand.nextInt(4) == 0) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.SPELL_MOB,
                  headX + this.rand.nextGaussian() * 0.3F,
                  headY + this.rand.nextGaussian() * 0.3F,
                  headZ + this.rand.nextGaussian() * 0.3F,
                  0.7F,
                  0.7F,
                  0.5
               );
         }
      }

      if (this.getInvulTime() > 0) {
         for (int i = 0; i < 3; i++) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.SPELL_MOB,
                  this.posX + this.rand.nextGaussian() * 1.0,
                  this.posY + this.rand.nextFloat() * 3.3F,
                  this.posZ + this.rand.nextGaussian() * 1.0,
                  0.7F,
                  0.7F,
                  0.9F
               );
         }
      }
   }

   @Override
   public void updateAITasks() {
      if (this.getInvulTime() > 0) {
         int n = this.getInvulTime() - 1;
         if (n <= 0) {
            this.worldObj
               .newExplosion(this, this.posX, this.posY + this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getBoolean("mobGriefing"));
            this.worldObj.playBroadcastSound(1013, new BlockPos(this), 0);
         }

         this.setInvulTime(n);
         if (this.ticksExisted % 10 == 0) {
            this.heal(10.0F);
         }
      } else {
         super.updateAITasks();

         for (int i = 1; i < 3; i++) {
            if (this.ticksExisted >= this.intArray[i - 1]) {
               this.intArray[i - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);
               if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL || this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
                  int n = i - 1;
                  int n2 = this.intArray2[i - 1];
                  this.intArray2[n] = this.intArray2[i - 1] + 1;
                  if (n2 > 15) {
                     float f = 10.0F;
                     float f2 = 5.0F;
                     double randomDoubleInRange = MathHelper.getRandomDoubleInRange(this.rand, this.posX - f, this.posX + f);
                     double randomDoubleInRange2 = MathHelper.getRandomDoubleInRange(this.rand, this.posY - f2, this.posY + f2);
                     double randomDoubleInRange3 = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - f, this.posZ + f);
                     this.launchWitherSkullToCoords(i + 1, randomDoubleInRange, randomDoubleInRange2, randomDoubleInRange3, true);
                     this.intArray2[i - 1] = 0;
                  }
               }

               int watchedTargetId = this.getWatchedTargetId(i);
               if (watchedTargetId > 0) {
                  Entity entityByID = this.worldObj.getEntityByID(watchedTargetId);
                  if (entityByID == null || !entityByID.isEntityAlive() || !(this.getDistanceSqToEntity(entityByID) <= 900.0) || !this.canEntityBeSeen(entityByID)) {
                     this.updateWatchedTargetId(i, 0);
                  } else if (entityByID instanceof EntityPlayer && ((EntityPlayer)entityByID).capabilities.disableDamage) {
                     this.updateWatchedTargetId(i, 0);
                  } else {
                     this.launchWitherSkullToEntity(i + 1, (EntityLivingBase)entityByID);
                     this.intArray[i - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                     this.intArray2[i - 1] = 0;
                  }
               } else {
                  List entitiesWithinAABB = this.worldObj
                     .getEntitiesWithinAABB(
                        EntityLivingBase.class,
                        this.getEntityBoundingBox().expand(20.0, 8.0, 20.0),
                        Predicates.and(attackEntitySelector, EntitySelectors.NOT_SPECTATING)
                     );

                  for (int j = 0; j < 10 && !entitiesWithinAABB.isEmpty(); j++) {
                     EntityLivingBase entityLivingBase = (EntityLivingBase)entitiesWithinAABB.get(this.rand.nextInt(entitiesWithinAABB.size()));
                     if (entityLivingBase != this && entityLivingBase.isEntityAlive() && this.canEntityBeSeen(entityLivingBase)) {
                        if (entityLivingBase instanceof EntityPlayer) {
                           if (!((EntityPlayer)entityLivingBase).capabilities.disableDamage) {
                              this.updateWatchedTargetId(i, entityLivingBase.getEntityId());
                           }
                        } else {
                           this.updateWatchedTargetId(i, entityLivingBase.getEntityId());
                        }
                        break;
                     }

                     entitiesWithinAABB.remove(entityLivingBase);
                  }
               }
            }
         }

         if (this.getAttackTarget() != null) {
            this.updateWatchedTargetId(0, this.getAttackTarget().getEntityId());
         } else {
            this.updateWatchedTargetId(0, 0);
         }

         if (this.blockBreakCounter > 0) {
            this.blockBreakCounter--;
            if (this.blockBreakCounter == 0 && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
               int n = MathHelper.floor_double(this.posY);
               int n2 = MathHelper.floor_double(this.posX);
               int n3 = MathHelper.floor_double(this.posZ);
               boolean flag = false;

               for (int i = -1; i <= 1; i++) {
                  for (int j = -1; j <= 1; j++) {
                     for (int k = 0; k <= 3; k++) {
                        int n4 = n2 + i;
                        int n5 = n + k;
                        int n6 = n3 + j;
                        BlockPos blockPos = new BlockPos(n4, n5, n6);
                        Block block = this.worldObj.getBlockState(blockPos).getBlock();
                        if (block.getMaterial() != Material.air && canDestroyBlock(block)) {
                           flag = this.worldObj.destroyBlock(blockPos, true) || flag;
                        }
                     }
                  }
               }

               if (flag) {
                  this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1012, new BlockPos(this), 0);
               }
            }
         }

         if (this.ticksExisted % 20 == 0) {
            this.heal(1.0F);
         }
      }
   }

   public static boolean canDestroyBlock(Block block) {
      return block != Blocks.bedrock && block != Blocks.end_portal && block != Blocks.end_portal_frame && block != Blocks.command_block && block != Blocks.barrier;
   }

   public void onSummoned() {
      this.setInvulTime(220);
      this.setHealth(this.getMaxHealth() / 3.0F);
   }

   @Override
   public void setInWeb() {
   }

   @Override
   public int getTotalArmorValue() {
      return 4;
   }

   public double getHeadX(int n) {
      if (n <= 0) {
         return this.posX;
      }

      float f = (this.renderYawOffset + 180 * (n - 1)) / 180.0F * (float) Math.PI;
      float cos = MathHelper.cos(f);
      return this.posX + cos * 1.3;
   }

   public double getHeadY(int n) {
      return n <= 0 ? this.posY + 3.0 : this.posY + 2.2;
   }

   public double getHeadZ(int n) {
      if (n <= 0) {
         return this.posZ;
      }

      float f = (this.renderYawOffset + 180 * (n - 1)) / 180.0F * (float) Math.PI;
      float sin = MathHelper.sin(f);
      return this.posZ + sin * 1.3;
   }

   public float getNextAngle(float f, float f2, float f3) {
      float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
      if (f4 > f3) {
         f4 = f3;
      }

      if (f4 < -f3) {
         f4 = -f3;
      }

      return f + f4;
   }

   public void launchWitherSkullToEntity(int n, EntityLivingBase entityLivingBase) {
      this.launchWitherSkullToCoords(n, entityLivingBase.posX, entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.5, entityLivingBase.posZ, n == 0 && this.rand.nextFloat() < 0.001F);
   }

   public void launchWitherSkullToCoords(int n, double x, double y, double z, boolean invulnerable) {
      this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1014, new BlockPos(this), 0);
      double headX = this.getHeadX(n);
      double headY = this.getHeadY(n);
      double headZ = this.getHeadZ(n);
      double d = x - headX;
      double d2 = y - headY;
      double d3 = z - headZ;
      EntityWitherSkull entityWitherSkull = new EntityWitherSkull(this.worldObj, this, d, d2, d3);
      if (invulnerable) {
         entityWitherSkull.setInvulnerable(true);
      }

      entityWitherSkull.posY = headY;
      entityWitherSkull.posX = headX;
      entityWitherSkull.posZ = headZ;
      this.worldObj.spawnEntityInWorld(entityWitherSkull);
   }

   @Override
   public void attackEntityWithRangedAttack(EntityLivingBase target, float f) {
      this.launchWitherSkullToEntity(0, target);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (source == DamageSource.drown || source.getEntity() instanceof EntityWither) {
         return false;
      }

      if (this.getInvulTime() > 0 && source != DamageSource.outOfWorld) {
         return false;
      }

      if (this.isArmored()) {
         Entity sourceOfDamage = source.getSourceOfDamage();
         if (sourceOfDamage instanceof EntityArrow) {
            return false;
         }
      }

      Entity entity = source.getEntity();
      if (entity != null
         && !(entity instanceof EntityPlayer)
         && entity instanceof EntityLivingBase
         && ((EntityLivingBase)entity).getCreatureAttribute() == this.getCreatureAttribute()) {
         return false;
      }

      if (this.blockBreakCounter <= 0) {
         this.blockBreakCounter = 20;
      }

      for (int i = 0; i < this.intArray2.length; i++) {
         this.intArray2[i] = this.intArray2[i] + 3;
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      EntityItem dropItem = this.dropItem(Items.nether_star, 1);
      if (dropItem != null) {
         dropItem.setNoDespawn();
      }

      if (!this.worldObj.isRemote) {
         for (EntityPlayer entityPlayer : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().expand(50.0, 100.0, 50.0))) {
            entityPlayer.triggerAchievement(AchievementList.killWither);
         }
      }
   }

   @Override
   public void despawnEntity() {
      this.entityAge = 0;
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 15728880;
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
   }

   @Override
   public void addPotionEffect(PotionEffect potioneffectIn) {
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(300.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6F);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0);
   }

   public float getHeadYaw(int n) {
      return this.sideHeadYaws[n];
   }

   public float getHeadPitch(int n) {
      return this.sideHeadPitches[n];
   }

   public int getInvulTime() {
      return this.dataWatcher.getWatchableObjectInt(20);
   }

   public void setInvulTime(int n) {
      this.dataWatcher.updateObject(20, n);
   }

   public int getWatchedTargetId(int n) {
      return this.dataWatcher.getWatchableObjectInt(17 + n);
   }

   public void updateWatchedTargetId(int targetOffset, int newId) {
      this.dataWatcher.updateObject(17 + targetOffset, newId);
   }

   public boolean isArmored() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   @Override
   public void mountEntity(Entity entityIn) {
      this.ridingEntity = null;
   }
}
