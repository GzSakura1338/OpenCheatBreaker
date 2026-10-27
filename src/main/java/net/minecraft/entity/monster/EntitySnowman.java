package net.minecraft.entity.monster;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySnowman extends EntityGolem implements IRangedAttackMob {
   public EntitySnowman(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 1.9F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAIArrowAttack(this, 1.25, 20, 10.0F));
      this.tasks.addTask(2, new EntityAIWander(this, 1.0));
      this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(4, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, IMob.mobSelector));
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.worldObj.isRemote) {
         int n = MathHelper.floor_double(this.posX);
         int n2 = MathHelper.floor_double(this.posY);
         int n3 = MathHelper.floor_double(this.posZ);
         if (this.isWet()) {
            this.attackEntityFrom(DamageSource.drown, 1.0F);
         }

         if (this.worldObj.getBiomeGenForCoords(new BlockPos(n, 0, n3)).getFloatTemperature(new BlockPos(n, n2, n3)) > 1.0F) {
            this.attackEntityFrom(DamageSource.onFire, 1.0F);
         }

         for (int i = 0; i < 4; i++) {
            n = MathHelper.floor_double(this.posX + (i % 2 * 2 - 1) * 0.25F);
            n2 = MathHelper.floor_double(this.posY);
            n3 = MathHelper.floor_double(this.posZ + (i / 2 % 2 * 2 - 1) * 0.25F);
            BlockPos blockPos = new BlockPos(n, n2, n3);
            if (this.worldObj.getBlockState(blockPos).getBlock().getMaterial() == Material.air
               && this.worldObj.getBiomeGenForCoords(new BlockPos(n, 0, n3)).getFloatTemperature(blockPos) < 0.8F
               && Blocks.snow_layer.canPlaceBlockAt(this.worldObj, blockPos)) {
               this.worldObj.setBlockState(blockPos, Blocks.snow_layer.getDefaultState());
            }
         }
      }
   }

   @Override
   public Item getDropItem() {
      return Items.snowball;
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int nextInt = this.rand.nextInt(16);

      for (int i = 0; i < nextInt; i++) {
         this.dropItem(Items.snowball, 1);
      }
   }

   @Override
   public void attackEntityWithRangedAttack(EntityLivingBase target, float f) {
      EntitySnowball entitySnowball = new EntitySnowball(this.worldObj, this);
      double d = target.posY + target.getEyeHeight() - 1.1F;
      double d2 = target.posX - this.posX;
      double d3 = d - entitySnowball.posY;
      double d4 = target.posZ - this.posZ;
      float f2 = MathHelper.sqrt_double(d2 * d2 + d4 * d4) * 0.2F;
      entitySnowball.setThrowableHeading(d2, d3 + f2, d4, 1.6F, 12.0F);
      this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.worldObj.spawnEntityInWorld(entitySnowball);
   }

   @Override
   public float getEyeHeight() {
      return 1.7F;
   }
}
