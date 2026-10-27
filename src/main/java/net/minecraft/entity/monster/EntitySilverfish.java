package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockSilverfish.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntitySilverfish extends EntityMob {
   public EntitySilverfish.AISummonSilverfish summonSilverfish;

   public EntitySilverfish(World worldIn) {
      super(worldIn);
      this.setSize(0.4F, 0.3F);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(3, this.summonSilverfish = new EntitySilverfish.AISummonSilverfish(this));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(5, new EntitySilverfish.AIHideInStone(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
   }

   @Override
   public double getYOffset() {
      return 0.2;
   }

   @Override
   public float getEyeHeight() {
      return 0.1F;
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public String getLivingSound() {
      return "mob.silverfish.say";
   }

   @Override
   public String getHurtSound() {
      return "mob.silverfish.hit";
   }

   @Override
   public String getDeathSound() {
      return "mob.silverfish.kill";
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (source instanceof EntityDamageSource || source == DamageSource.magic) {
         this.summonSilverfish.onHurt();
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.silverfish.step", 0.15F, 1.0F);
   }

   @Override
   public Item getDropItem() {
      return null;
   }

   @Override
   public void onUpdate() {
      this.renderYawOffset = this.rotationYaw;
      super.onUpdate();
   }

   @Override
   public float getBlockPathWeight(BlockPos pos) {
      return this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.stone ? 10.0F : super.getBlockPathWeight(pos);
   }

   @Override
   public boolean isValidLightLevel() {
      return true;
   }

   @Override
   public boolean getCanSpawnHere() {
      if (super.getCanSpawnHere()) {
         EntityPlayer closestPlayerToEntity = this.worldObj.getClosestPlayerToEntity(this, 5.0);
         return closestPlayerToEntity == null;
      } else {
         return false;
      }
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   static class AIHideInStone extends EntityAIWander {
      public EntitySilverfish silverfish;
      public EnumFacing facing;
      public boolean canInfest;

      public AIHideInStone(EntitySilverfish silverfishIn) {
         super(silverfishIn, 1.0, 10);
         this.silverfish = silverfishIn;
         this.setMutexBits(1);
      }

      @Override
      public boolean shouldExecute() {
         if (this.silverfish.getAttackTarget() != null) {
            return false;
         }

         if (!this.silverfish.getNavigator().noPath()) {
            return false;
         }

         Random rNG = this.silverfish.getRNG();
         if (rNG.nextInt(10) == 0) {
            this.facing = EnumFacing.random(rNG);
            BlockPos offset = new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5, this.silverfish.posZ).offset(this.facing);
            IBlockState blockState = this.silverfish.worldObj.getBlockState(offset);
            if (BlockSilverfish.canContainSilverfish(blockState)) {
               this.canInfest = true;
               return true;
            }
         }

         this.canInfest = false;
         return super.shouldExecute();
      }

      @Override
      public boolean continueExecuting() {
         return this.canInfest ? false : super.continueExecuting();
      }

      @Override
      public void startExecuting() {
         if (!this.canInfest) {
            super.startExecuting();
         } else {
            World worldObj = this.silverfish.worldObj;
            BlockPos offset = new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5, this.silverfish.posZ).offset(this.facing);
            IBlockState blockState = worldObj.getBlockState(offset);
            if (BlockSilverfish.canContainSilverfish(blockState)) {
               worldObj.setBlockState(offset, Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, EnumType.forModelBlock(blockState)), 3);
               this.silverfish.spawnExplosionParticle();
               this.silverfish.setDead();
            }
         }
      }
   }

   static class AISummonSilverfish extends EntityAIBase {
      public EntitySilverfish silverfish;
      public int delay;

      public AISummonSilverfish(EntitySilverfish silverfishIn) {
         this.silverfish = silverfishIn;
      }

      public void onHurt() {
         if (this.delay == 0) {
            this.delay = 20;
         }
      }

      @Override
      public boolean shouldExecute() {
         return this.delay > 0;
      }

      @Override
      public void updateTask() {
         this.delay--;
         if (this.delay <= 0) {
            World worldObj = this.silverfish.worldObj;
            Random rNG = this.silverfish.getRNG();
            BlockPos blockPos = new BlockPos(this.silverfish);

            for (int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 - i : 0 - i)) {
               for (int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 - j : 0 - j)) {
                  for (int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 - k : 0 - k)) {
                     BlockPos blockPos2 = blockPos.add(j, i, k);
                     IBlockState blockState = worldObj.getBlockState(blockPos2);
                     if (blockState.getBlock() == Blocks.monster_egg) {
                        if (worldObj.getGameRules().getBoolean("mobGriefing")) {
                           worldObj.destroyBlock(blockPos2, true);
                        } else {
                           worldObj.setBlockState(blockPos2, blockState.getValue(BlockSilverfish.VARIANT).getModelBlock(), 3);
                        }

                        if (rNG.nextBoolean()) {
                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
