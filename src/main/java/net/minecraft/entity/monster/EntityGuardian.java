package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood.FishType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGuardian extends EntityMob {
   public EntityAIWander wander;
   public float spikesExtension;
   public float spikesExtensionRate;
   public float tailAngle;
   public float prevTailAngle;
   public EntityLivingBase targetedEntity;
   public int beamTicks;
   public boolean flopping;
   public float prevSpikesExtension;

   public EntityGuardian(World worldIn) {
      super(worldIn);
      this.experienceValue = 10;
      this.setSize(0.85F, 0.85F);
      this.tasks.addTask(4, new EntityGuardian.AIGuardianAttack(this));
      EntityAIMoveTowardsRestriction entityAIMoveTowardsRestriction;
      this.tasks.addTask(5, entityAIMoveTowardsRestriction = new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(7, this.wander = new EntityAIWander(this, 1.0, 80));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityGuardian.class, 12.0F, 0.01F));
      this.tasks.addTask(9, new EntityAILookIdle(this));
      this.wander.setMutexBits(3);
      entityAIMoveTowardsRestriction.setMutexBits(3);
      this.targetTasks
         .addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, new EntityGuardian.GuardianTargetSelector(this)));
      this.moveHelper = new EntityGuardian.GuardianMoveHelper(this);
      this.prevSpikesExtension = this.spikesExtension = this.rand.nextFloat();
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setElder(tagCompund.getBoolean("Elder"));
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("Elder", this.isElder());
   }

   @Override
   public PathNavigate getNewNavigator(World worldIn) {
      return new PathNavigateSwimmer(this, worldIn);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, 0);
      this.dataWatcher.addObject(17, 0);
   }

   public boolean isSyncedFlagSet(int flagId) {
      return (this.dataWatcher.getWatchableObjectInt(16) & flagId) != 0;
   }

   public void setSyncedFlag(int flagId, boolean state) {
      int watchableObjectInt = this.dataWatcher.getWatchableObjectInt(16);
      if (state) {
         this.dataWatcher.updateObject(16, watchableObjectInt | flagId);
      } else {
         this.dataWatcher.updateObject(16, watchableObjectInt & ~flagId);
      }
   }

   public boolean areSpikesRetracted() {
      return this.isSyncedFlagSet(2);
   }

   public void setSpikesRetracted(boolean flag) {
      this.setSyncedFlag(2, flag);
   }

   public int getWarmupTime() {
      return this.isElder() ? 60 : 80;
   }

   public boolean isElder() {
      return this.isSyncedFlagSet(4);
   }

   public void setElder(boolean elder) {
      this.setSyncedFlag(4, elder);
      if (elder) {
         this.setSize(1.9975F, 1.9975F);
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
         this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0);
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(80.0);
         this.enablePersistence();
         this.wander.setExecutionChance(400);
      }
   }

   public void setElder() {
      this.setElder(true);
      this.prevTailAngle = this.tailAngle = 1.0F;
   }

   public void setTargetedEntity(int entityId) {
      this.dataWatcher.updateObject(17, entityId);
   }

   public boolean hasTargetedEntity() {
      return this.dataWatcher.getWatchableObjectInt(17) != 0;
   }

   public EntityLivingBase getTargetedEntity() {
      if (!this.hasTargetedEntity()) {
         return null;
      }

      if (this.worldObj.isRemote) {
         if (this.targetedEntity != null) {
            return this.targetedEntity;
         } else {
            Entity entityByID = this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(17));
            if (entityByID instanceof EntityLivingBase) {
               this.targetedEntity = (EntityLivingBase)entityByID;
               return this.targetedEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.getAttackTarget();
      }
   }

   @Override
   public void onDataWatcherUpdate(int dataID) {
      super.onDataWatcherUpdate(dataID);
      if (dataID == 16) {
         if (this.isElder() && this.width < 1.0F) {
            this.setSize(1.9975F, 1.9975F);
         }
      } else if (dataID == 17) {
         this.beamTicks = 0;
         this.targetedEntity = null;
      }
   }

   @Override
   public int getTalkInterval() {
      return 160;
   }

   @Override
   public String getLivingSound() {
      return !this.isInWater() ? "mob.guardian.land.idle" : (this.isElder() ? "mob.guardian.elder.idle" : "mob.guardian.idle");
   }

   @Override
   public String getHurtSound() {
      return !this.isInWater() ? "mob.guardian.land.hit" : (this.isElder() ? "mob.guardian.elder.hit" : "mob.guardian.hit");
   }

   @Override
   public String getDeathSound() {
      return !this.isInWater() ? "mob.guardian.land.death" : (this.isElder() ? "mob.guardian.elder.death" : "mob.guardian.death");
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public float getEyeHeight() {
      return this.height * 0.5F;
   }

   @Override
   public float getBlockPathWeight(BlockPos pos) {
      return this.worldObj.getBlockState(pos).getBlock().getMaterial() == Material.water
         ? 10.0F + this.worldObj.getLightBrightness(pos) - 0.5F
         : super.getBlockPathWeight(pos);
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         this.prevSpikesExtension = this.spikesExtension;
         if (!this.isInWater()) {
            this.spikesExtensionRate = 2.0F;
            if (this.motionY > 0.0 && this.flopping && !this.isSilent()) {
               this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.guardian.flop", 1.0F, 1.0F, false);
            }

            this.flopping = this.motionY < 0.0 && this.worldObj.isBlockNormalCube(new BlockPos(this).down(), false);
         } else if (this.areSpikesRetracted()) {
            if (this.spikesExtensionRate < 0.5F) {
               this.spikesExtensionRate = 4.0F;
            } else {
               this.spikesExtensionRate = this.spikesExtensionRate + (0.5F - this.spikesExtensionRate) * 0.1F;
            }
         } else {
            this.spikesExtensionRate = this.spikesExtensionRate + (0.125F - this.spikesExtensionRate) * 0.2F;
         }

         this.spikesExtension = this.spikesExtension + this.spikesExtensionRate;
         this.prevTailAngle = this.tailAngle;
         if (!this.isInWater()) {
            this.tailAngle = this.rand.nextFloat();
         } else if (this.areSpikesRetracted()) {
            this.tailAngle = this.tailAngle + (0.0F - this.tailAngle) * 0.25F;
         } else {
            this.tailAngle = this.tailAngle + (1.0F - this.tailAngle) * 0.06F;
         }

         if (this.areSpikesRetracted() && this.isInWater()) {
            Vec3 look = this.getLook(0.0F);

            for (int i = 0; i < 2; i++) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.WATER_BUBBLE,
                     this.posX + (this.rand.nextDouble() - 0.5) * this.width - look.xCoord * 1.5,
                     this.posY + this.rand.nextDouble() * this.height - look.yCoord * 1.5,
                     this.posZ + (this.rand.nextDouble() - 0.5) * this.width - look.zCoord * 1.5,
                     0.0,
                     0.0,
                     0.0
                  );
            }
         }

         if (this.hasTargetedEntity()) {
            if (this.beamTicks < this.getWarmupTime()) {
               this.beamTicks++;
            }

            EntityLivingBase targetedEntity = this.getTargetedEntity();
            if (targetedEntity != null) {
               this.getLookHelper().setLookPositionWithEntity(targetedEntity, 90.0F, 90.0F);
               this.getLookHelper().onUpdateLook();
               double beamProgress = this.getBeamProgress(0.0F);
               double d = targetedEntity.posX - this.posX;
               double d2 = targetedEntity.posY + targetedEntity.height * 0.5F - (this.posY + this.getEyeHeight());
               double d3 = targetedEntity.posZ - this.posZ;
               double sqrt = Math.sqrt(d * d + d2 * d2 + d3 * d3);
               d /= sqrt;
               d2 /= sqrt;
               d3 /= sqrt;
               double nextDouble = this.rand.nextDouble();

               while (nextDouble < sqrt) {
                  nextDouble += 1.8 - beamProgress + this.rand.nextDouble() * (1.7 - beamProgress);
                  this.worldObj
                     .spawnParticle(
                        EnumParticleTypes.WATER_BUBBLE,
                        this.posX + d * nextDouble,
                        this.posY + d2 * nextDouble + this.getEyeHeight(),
                        this.posZ + d3 * nextDouble,
                        0.0,
                        0.0,
                        0.0
                     );
               }
            }
         }
      }

      if (this.inWater) {
         this.setAir(300);
      } else if (this.onGround) {
         this.motionY += 0.5;
         this.motionX = this.motionX + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F;
         this.motionZ = this.motionZ + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F;
         this.rotationYaw = this.rand.nextFloat() * 360.0F;
         this.onGround = false;
         this.isAirBorne = true;
      }

      if (this.hasTargetedEntity()) {
         this.rotationYaw = this.rotationYawHead;
      }

      super.onLivingUpdate();
   }

   public float getSpikesExtension(float f) {
      return this.prevSpikesExtension + (this.spikesExtension - this.prevSpikesExtension) * f;
   }

   public float getTailAngle(float f) {
      return this.prevTailAngle + (this.tailAngle - this.prevTailAngle) * f;
   }

   public float getBeamProgress(float f) {
      return (this.beamTicks + f) / this.getWarmupTime();
   }

   @Override
   public void updateAITasks() {
      super.updateAITasks();
      if (this.isElder()) {
         short s = 1200;
         short s2 = 1200;
         short s3 = 6000;
         byte b = 2;
         if ((this.ticksExisted + this.getEntityId()) % 1200 == 0) {
            Potion digSlowdown = Potion.digSlowdown;

            for (EntityPlayerMP entityPlayerMP : this.worldObj.getPlayers(EntityPlayerMP.class, new Predicate<EntityPlayerMP>() {
               public boolean apply(EntityPlayerMP entityPlayerMP2) {
                  return EntityGuardian.this.getDistanceSqToEntity(entityPlayerMP2) < 2500.0 && entityPlayerMP2.theItemInWorldManager.survivalOrAdventure();
               }
            })) {
               if (!entityPlayerMP.isPotionActive(digSlowdown) || entityPlayerMP.getActivePotionEffect(digSlowdown).getAmplifier() < 2 || entityPlayerMP.getActivePotionEffect(digSlowdown).getDuration() < 1200) {
                  entityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(10, 0.0F));
                  entityPlayerMP.addPotionEffect(new PotionEffect(digSlowdown.id, 6000, 2));
               }
            }
         }

         if (!this.hasHome()) {
            this.setHomePosAndDistance(new BlockPos(this), 16);
         }
      }
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int n = this.rand.nextInt(3) + this.rand.nextInt(lootingModifier + 1);
      if (n > 0) {
         this.entityDropItem(new ItemStack(Items.prismarine_shard, n, 0), 1.0F);
      }

      if (this.rand.nextInt(3 + lootingModifier) > 1) {
         this.entityDropItem(new ItemStack(Items.fish, 1, FishType.COD.getMetadata()), 1.0F);
      } else if (this.rand.nextInt(3 + lootingModifier) > 1) {
         this.entityDropItem(new ItemStack(Items.prismarine_crystals, 1, 0), 1.0F);
      }

      if (wasRecentlyHit && this.isElder()) {
         this.entityDropItem(new ItemStack(Blocks.sponge, 1, 1), 1.0F);
      }
   }

   @Override
   public void addRandomDrop() {
      ItemStack itemStack = WeightedRandom.getRandomItem(this.rand, EntityFishHook.getFishingLoot()).getItemStack(this.rand);
      this.entityDropItem(itemStack, 1.0F);
   }

   @Override
   public boolean isValidLightLevel() {
      return true;
   }

   @Override
   public boolean isNotColliding() {
      return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty();
   }

   @Override
   public boolean getCanSpawnHere() {
      return (this.rand.nextInt(20) == 0 || !this.worldObj.canBlockSeeSky(new BlockPos(this))) && super.getCanSpawnHere();
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!this.areSpikesRetracted() && !source.isMagicDamage() && source.getSourceOfDamage() instanceof EntityLivingBase) {
         EntityLivingBase sourceOfDamage = (EntityLivingBase)source.getSourceOfDamage();
         if (!source.isExplosion()) {
            sourceOfDamage.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0F);
            sourceOfDamage.playSound("damage.thorns", 0.5F, 1.0F);
         }
      }

      this.wander.makeUpdate();
      return super.attackEntityFrom(source, amount);
   }

   @Override
   public int getVerticalFaceSpeed() {
      return 180;
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      if (this.isServerWorld()) {
         if (this.isInWater()) {
            this.moveFlying(strafe, forward, 0.1F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9F;
            this.motionY *= 0.9F;
            this.motionZ *= 0.9F;
            if (!this.areSpikesRetracted() && this.getAttackTarget() == null) {
               this.motionY -= 0.005;
            }
         } else {
            super.moveEntityWithHeading(strafe, forward);
         }
      } else {
         super.moveEntityWithHeading(strafe, forward);
      }
   }

   static class AIGuardianAttack extends EntityAIBase {
      public EntityGuardian theEntity;
      public int tickCounter;

      public AIGuardianAttack(EntityGuardian guardian) {
         this.theEntity = guardian;
         this.setMutexBits(3);
      }

      @Override
      public boolean shouldExecute() {
         EntityLivingBase attackTarget = this.theEntity.getAttackTarget();
         return attackTarget != null && attackTarget.isEntityAlive();
      }

      @Override
      public boolean continueExecuting() {
         return super.continueExecuting() && (this.theEntity.isElder() || this.theEntity.getDistanceSqToEntity(this.theEntity.getAttackTarget()) > 9.0);
      }

      @Override
      public void startExecuting() {
         this.tickCounter = -10;
         this.theEntity.getNavigator().clearPathEntity();
         this.theEntity.getLookHelper().setLookPositionWithEntity(this.theEntity.getAttackTarget(), 90.0F, 90.0F);
         this.theEntity.isAirBorne = true;
      }

      @Override
      public void resetTask() {
         this.theEntity.setTargetedEntity(0);
         this.theEntity.setAttackTarget((EntityLivingBase)null);
         this.theEntity.wander.makeUpdate();
      }

      @Override
      public void updateTask() {
         EntityLivingBase attackTarget = this.theEntity.getAttackTarget();
         this.theEntity.getNavigator().clearPathEntity();
         this.theEntity.getLookHelper().setLookPositionWithEntity(attackTarget, 90.0F, 90.0F);
         if (!this.theEntity.canEntityBeSeen(attackTarget)) {
            this.theEntity.setAttackTarget((EntityLivingBase)null);
         } else {
            this.tickCounter++;
            if (this.tickCounter == 0) {
               this.theEntity.setTargetedEntity(this.theEntity.getAttackTarget().getEntityId());
               this.theEntity.worldObj.setEntityState(this.theEntity, (byte)21);
            } else if (this.tickCounter >= this.theEntity.getWarmupTime()) {
               float f = 1.0F;
               if (this.theEntity.worldObj.getDifficulty() == EnumDifficulty.HARD) {
                  f += 2.0F;
               }

               if (this.theEntity.isElder()) {
                  f += 2.0F;
               }

               attackTarget.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.theEntity, this.theEntity), f);
               attackTarget.attackEntityFrom(
                  DamageSource.causeMobDamage(this.theEntity),
                  (float)this.theEntity.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()
               );
               this.theEntity.setAttackTarget((EntityLivingBase)null);
            } else if (this.tickCounter >= 60 && this.tickCounter % 20 == 0) {
            }

            super.updateTask();
         }
      }
   }

   static class GuardianMoveHelper extends EntityMoveHelper {
      public EntityGuardian entityGuardian;

      public GuardianMoveHelper(EntityGuardian guardian) {
         super(guardian);
         this.entityGuardian = guardian;
      }

      @Override
      public void onUpdateMoveHelper() {
         if (this.update && !this.entityGuardian.getNavigator().noPath()) {
            double d = this.posX - this.entityGuardian.posX;
            double d2 = this.posY - this.entityGuardian.posY;
            double d3 = this.posZ - this.entityGuardian.posZ;
            double d4 = d * d + d2 * d2 + d3 * d3;
            d4 = MathHelper.sqrt_double(d4);
            d2 /= d4;
            float f = (float)(MathHelper.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
            this.entityGuardian.rotationYaw = this.limitAngle(this.entityGuardian.rotationYaw, f, 30.0F);
            this.entityGuardian.renderYawOffset = this.entityGuardian.rotationYaw;
            float f2 = (float)(this.speed * this.entityGuardian.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            this.entityGuardian.setAIMoveSpeed(this.entityGuardian.getAIMoveSpeed() + (f2 - this.entityGuardian.getAIMoveSpeed()) * 0.125F);
            double d5 = Math.sin((this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.5) * 0.05;
            double cos = Math.cos(this.entityGuardian.rotationYaw * (float) Math.PI / 180.0F);
            double sin = Math.sin(this.entityGuardian.rotationYaw * (float) Math.PI / 180.0F);
            this.entityGuardian.motionX += d5 * cos;
            this.entityGuardian.motionZ += d5 * sin;
            d5 = Math.sin((this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.75) * 0.05;
            this.entityGuardian.motionY += d5 * (sin + cos) * 0.25;
            this.entityGuardian.motionY = this.entityGuardian.motionY + this.entityGuardian.getAIMoveSpeed() * d2 * 0.1;
            EntityLookHelper lookHelper = this.entityGuardian.getLookHelper();
            double d6 = this.entityGuardian.posX + d / d4 * 2.0;
            double d7 = this.entityGuardian.getEyeHeight() + this.entityGuardian.posY + d2 / d4 * 1.0;
            double d8 = this.entityGuardian.posZ + d3 / d4 * 2.0;
            double lookPosX = lookHelper.getLookPosX();
            double lookPosY = lookHelper.getLookPosY();
            double lookPosZ = lookHelper.getLookPosZ();
            if (!lookHelper.getIsLooking()) {
               lookPosX = d6;
               lookPosY = d7;
               lookPosZ = d8;
            }

            this.entityGuardian
               .getLookHelper()
               .setLookPosition(lookPosX + (d6 - lookPosX) * 0.125, lookPosY + (d7 - lookPosY) * 0.125, lookPosZ + (d8 - lookPosZ) * 0.125, 10.0F, 40.0F);
            this.entityGuardian.setSpikesRetracted(true);
         } else {
            this.entityGuardian.setAIMoveSpeed(0.0F);
            this.entityGuardian.setSpikesRetracted(false);
         }
      }
   }

   static class GuardianTargetSelector implements Predicate<EntityLivingBase> {
      public EntityGuardian parentEntity;

      public GuardianTargetSelector(EntityGuardian guardian) {
         this.parentEntity = guardian;
      }

      public boolean apply(EntityLivingBase entityLivingBase) {
         return (entityLivingBase instanceof EntityPlayer || entityLivingBase instanceof EntitySquid) && entityLivingBase.getDistanceSqToEntity(this.parentEntity) > 9.0;
      }
   }
}
