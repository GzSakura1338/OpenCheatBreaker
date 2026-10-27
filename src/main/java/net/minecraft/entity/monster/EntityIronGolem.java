package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityIronGolem extends EntityGolem {
   public int homeCheckTimer;
   public Village villageObj;
   public int attackTimer;
   public int holdRoseTick;

   public EntityIronGolem(World worldIn) {
      super(worldIn);
      this.setSize(1.4F, 2.9F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0, true));
      this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9, 32.0F));
      this.tasks.addTask(3, new EntityAIMoveThroughVillage(this, 0.6, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(5, new EntityAILookAtVillager(this));
      this.tasks.addTask(6, new EntityAIWander(this, 0.6));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIDefendVillage(this));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
      this.targetTasks
         .addTask(3, new EntityIronGolem.AINearestAttackableTargetNonCreeper<>(this, EntityLiving.class, 10, false, true, IMob.VISIBLE_MOB_SELECTOR));
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
   }

   @Override
   public void updateAITasks() {
      if (--this.homeCheckTimer <= 0) {
         this.homeCheckTimer = 70 + this.rand.nextInt(50);
         this.villageObj = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(this), 32);
         if (this.villageObj == null) {
            this.detachHome();
         } else {
            BlockPos center = this.villageObj.getCenter();
            this.setHomePosAndDistance(center, (int)(this.villageObj.getVillageRadius() * 0.6F));
         }
      }

      super.updateAITasks();
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
   }

   @Override
   public int decreaseAirSupply(int n) {
      return n;
   }

   @Override
   public void collideWithEntity(Entity entityIn) {
      if (entityIn instanceof IMob && !(entityIn instanceof EntityCreeper) && this.getRNG().nextInt(20) == 0) {
         this.setAttackTarget((EntityLivingBase)entityIn);
      }

      super.collideWithEntity(entityIn);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.attackTimer > 0) {
         this.attackTimer--;
      }

      if (this.holdRoseTick > 0) {
         this.holdRoseTick--;
      }

      if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.5000003E-7F && this.rand.nextInt(5) == 0) {
         int n = MathHelper.floor_double(this.posX);
         int n2 = MathHelper.floor_double(this.posY - 0.2F);
         int n3 = MathHelper.floor_double(this.posZ);
         IBlockState blockState = this.worldObj.getBlockState(new BlockPos(n, n2, n3));
         Block block = blockState.getBlock();
         if (block.getMaterial() != Material.air) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.BLOCK_CRACK,
                  this.posX + (this.rand.nextFloat() - 0.5) * this.width,
                  this.getEntityBoundingBox().minY + 0.1,
                  this.posZ + (this.rand.nextFloat() - 0.5) * this.width,
                  4.0 * (this.rand.nextFloat() - 0.5),
                  0.5,
                  (this.rand.nextFloat() - 0.5) * 4.0,
                  Block.getStateId(blockState)
               );
         }
      }
   }

   @Override
   public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
      return this.isPlayerCreated() && EntityPlayer.class.isAssignableFrom(cls) ? false : (cls == EntityCreeper.class ? false : super.canAttackClass(cls));
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("PlayerCreated", this.isPlayerCreated());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setPlayerCreated(tagCompund.getBoolean("PlayerCreated"));
   }

   @Override
   public boolean attackEntityAsMob(Entity entityIn) {
      this.attackTimer = 10;
      this.worldObj.setEntityState(this, (byte)4);
      boolean attackEntityFrom = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 7 + this.rand.nextInt(15));
      if (attackEntityFrom) {
         entityIn.motionY += 0.4F;
         this.applyEnchantments(this, entityIn);
      }

      this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
      return attackEntityFrom;
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 4) {
         this.attackTimer = 10;
         this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
      } else if (id == 11) {
         this.holdRoseTick = 400;
      } else {
         super.handleStatusUpdate(id);
      }
   }

   public Village getVillage() {
      return this.villageObj;
   }

   public int getAttackTimer() {
      return this.attackTimer;
   }

   public void setHoldingRose(boolean flag) {
      this.holdRoseTick = flag ? 400 : 0;
      this.worldObj.setEntityState(this, (byte)11);
   }

   @Override
   public String getHurtSound() {
      return "mob.irongolem.hit";
   }

   @Override
   public String getDeathSound() {
      return "mob.irongolem.death";
   }

   @Override
   public void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.irongolem.walk", 1.0F, 1.0F);
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int nextInt = this.rand.nextInt(3);

      for (int i = 0; i < nextInt; i++) {
         this.dropItemWithOffset(Item.getItemFromBlock(Blocks.red_flower), 1, EnumFlowerType.POPPY.getMeta());
      }

      int n = 3 + this.rand.nextInt(3);

      for (int i = 0; i < n; i++) {
         this.dropItem(Items.iron_ingot, 1);
      }
   }

   public int getHoldRoseTick() {
      return this.holdRoseTick;
   }

   public boolean isPlayerCreated() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setPlayerCreated(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
      if (flag) {
         this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -2));
      }
   }

   @Override
   public void onDeath(DamageSource cause) {
      if (!this.isPlayerCreated() && this.attackingPlayer != null && this.villageObj != null) {
         this.villageObj.setReputationForPlayer(this.attackingPlayer.getName(), -5);
      }

      super.onDeath(cause);
   }

   static class AINearestAttackableTargetNonCreeper<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
      public AINearestAttackableTargetNonCreeper(
         final EntityCreature creature, Class<T> classTarget, int chance, boolean flag, boolean flag2, final Predicate<? super T> predicate
      ) {
         super(creature, classTarget, chance, flag, flag2, predicate);
         this.targetEntitySelector = new Predicate<T>() {
            public boolean apply(T value) {
               if (predicate != null && !predicate.apply(value)) {
                  return false;
               }

               if (value instanceof EntityCreeper) {
                  return false;
               }

               if (value instanceof EntityPlayer) {
                  double targetDistance = AINearestAttackableTargetNonCreeper.this.getTargetDistance();
                  if (value.isSneaking()) {
                     targetDistance *= 0.8F;
                  }

                  if (value.isInvisible()) {
                     float armorVisibility = ((EntityPlayer)value).getArmorVisibility();
                     if (armorVisibility < 0.1F) {
                        armorVisibility = 0.1F;
                     }

                     targetDistance *= 0.7F * armorVisibility;
                  }

                  if (value.getDistanceToEntity(creature) > targetDistance) {
                     return false;
                  }
               }

               return AINearestAttackableTargetNonCreeper.this.isSuitableTarget(value, false);
            }
         };
      }
   }
}
