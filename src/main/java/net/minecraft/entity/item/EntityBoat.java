package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBoat extends Entity {
   public boolean isBoatEmpty = true;
   public double speedMultiplier = 0.07;
   public int boatPosRotationIncrements;
   public double boatX;
   public double boatY;
   public double boatZ;
   public double boatYaw;
   public double boatPitch;
   public double velocityX;
   public double velocityY;
   public double velocityZ;

   public EntityBoat(World worldIn) {
      super(worldIn);
      this.preventEntitySpawning = true;
      this.setSize(1.5F, 0.6F);
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(1));
      this.dataWatcher.addObject(19, new Float(0.0F));
   }

   @Override
   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      return entityIn.getEntityBoundingBox();
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox() {
      return this.getEntityBoundingBox();
   }

   @Override
   public boolean canBePushed() {
      return true;
   }

   public EntityBoat(World worldIn, double d, double d2, double d3) {
      this(worldIn);
      this.setPosition(d, d2, d3);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = d;
      this.prevPosY = d2;
      this.prevPosZ = d3;
   }

   @Override
   public double getMountedYOffset() {
      return -0.3;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (!this.worldObj.isRemote && !this.isDead) {
         if (this.riddenByEntity != null && this.riddenByEntity == source.getEntity() && source instanceof EntityDamageSourceIndirect) {
            return false;
         }

         this.setForwardDirection(-this.getForwardDirection());
         this.setTimeSinceHit(10);
         this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
         this.setBeenAttacked();
         boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;
         if (flag || this.getDamageTaken() > 40.0F) {
            if (this.riddenByEntity != null) {
               this.riddenByEntity.mountEntity(this);
            }

            if (!flag && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
               this.dropItemWithOffset(Items.boat, 1, 0.0F);
            }

            this.setDead();
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public void performHurtAnimation() {
      this.setForwardDirection(-this.getForwardDirection());
      this.setTimeSinceHit(10);
      this.setDamageTaken(this.getDamageTaken() * 11.0F);
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean flag) {
      if (flag && this.riddenByEntity != null) {
         this.prevPosX = this.posX = x;
         this.prevPosY = this.posY = y;
         this.prevPosZ = this.posZ = z;
         this.rotationYaw = yaw;
         this.rotationPitch = pitch;
         this.boatPosRotationIncrements = 0;
         this.setPosition(x, y, z);
         this.motionX = this.velocityX = 0.0;
         this.motionY = this.velocityY = 0.0;
         this.motionZ = this.velocityZ = 0.0;
      } else {
         if (this.isBoatEmpty) {
            this.boatPosRotationIncrements = posRotationIncrements + 5;
         } else {
            double d = x - this.posX;
            double d2 = y - this.posY;
            double d3 = z - this.posZ;
            double d4 = d * d + d2 * d2 + d3 * d3;
            if (d4 <= 1.0) {
               return;
            }

            this.boatPosRotationIncrements = 3;
         }

         this.boatX = x;
         this.boatY = y;
         this.boatZ = z;
         this.boatYaw = yaw;
         this.boatPitch = pitch;
         this.motionX = this.velocityX;
         this.motionY = this.velocityY;
         this.motionZ = this.velocityZ;
      }
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.velocityX = this.motionX = x;
      this.velocityY = this.motionY = y;
      this.velocityZ = this.motionZ = z;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.getTimeSinceHit() > 0) {
         this.setTimeSinceHit(this.getTimeSinceHit() - 1);
      }

      if (this.getDamageTaken() > 0.0F) {
         this.setDamageTaken(this.getDamageTaken() - 1.0F);
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      byte b = 5;
      double d = 0.0;

      for (int i = 0; i < b; i++) {
         double d2 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (i + 0) / b - 0.125;
         double d3 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (i + 1) / b - 0.125;
         AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
            this.getEntityBoundingBox().minX, d2, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().maxX, d3, this.getEntityBoundingBox().maxZ
         );
         if (this.worldObj.isAABBInMaterial(axisAlignedBB, Material.water)) {
            d += 1.0 / b;
         }
      }

      double sqrt = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (sqrt > 0.2975) {
         double cos = Math.cos(this.rotationYaw * Math.PI / 180.0);
         double sin = Math.sin(this.rotationYaw * Math.PI / 180.0);

         for (int i = 0; i < 1.0 + sqrt * 60.0; i++) {
            double d2 = this.rand.nextFloat() * 2.0F - 1.0F;
            double d3 = (this.rand.nextInt(2) * 2 - 1) * 0.7;
            if (this.rand.nextBoolean()) {
               double d4 = this.posX - cos * d2 * 0.8 + sin * d3;
               double d5 = this.posZ - sin * d2 * 0.8 - cos * d3;
               this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, d4, this.posY - 0.125, d5, this.motionX, this.motionY, this.motionZ);
            } else {
               double d4 = this.posX + cos + sin * d2 * 0.7;
               double d5 = this.posZ + sin - cos * d2 * 0.7;
               this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, d4, this.posY - 0.125, d5, this.motionX, this.motionY, this.motionZ);
            }
         }
      }

      if (this.worldObj.isRemote && this.isBoatEmpty) {
         if (this.boatPosRotationIncrements > 0) {
            double d2 = this.posX + (this.boatX - this.posX) / this.boatPosRotationIncrements;
            double d3 = this.posY + (this.boatY - this.posY) / this.boatPosRotationIncrements;
            double d4 = this.posZ + (this.boatZ - this.posZ) / this.boatPosRotationIncrements;
            double d5 = MathHelper.wrapAngleTo180_double(this.boatYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + d5 / this.boatPosRotationIncrements);
            this.rotationPitch = (float)(this.rotationPitch + (this.boatPitch - this.rotationPitch) / this.boatPosRotationIncrements);
            this.boatPosRotationIncrements--;
            this.setPosition(d2, d3, d4);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            double d2 = this.posX + this.motionX;
            double d3 = this.posY + this.motionY;
            double d4 = this.posZ + this.motionZ;
            this.setPosition(d2, d3, d4);
            if (this.onGround) {
               this.motionX *= 0.5;
               this.motionY *= 0.5;
               this.motionZ *= 0.5;
            }

            this.motionX *= 0.99F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.99F;
         }
      } else {
         if (d < 1.0) {
            double d2 = d * 2.0 - 1.0;
            this.motionY += 0.04F * d2;
         } else {
            if (this.motionY < 0.0) {
               this.motionY /= 2.0;
            }

            this.motionY += 0.007F;
         }

         if (this.riddenByEntity instanceof EntityLivingBase) {
            EntityLivingBase riddenByEntity = (EntityLivingBase)this.riddenByEntity;
            float f = this.riddenByEntity.rotationYaw + -riddenByEntity.moveStrafing * 90.0F;
            this.motionX = this.motionX + -Math.sin(f * (float) Math.PI / 180.0F) * this.speedMultiplier * riddenByEntity.moveForward * 0.05F;
            this.motionZ = this.motionZ + Math.cos(f * (float) Math.PI / 180.0F) * this.speedMultiplier * riddenByEntity.moveForward * 0.05F;
         }

         double sqrt2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (sqrt2 > 0.35) {
            double d2 = 0.35 / sqrt2;
            this.motionX *= d2;
            this.motionZ *= d2;
            sqrt2 = 0.35;
         }

         if (sqrt2 > sqrt && this.speedMultiplier < 0.35) {
            this.speedMultiplier = this.speedMultiplier + (0.35 - this.speedMultiplier) / 35.0;
            if (this.speedMultiplier > 0.35) {
               this.speedMultiplier = 0.35;
            }
         } else {
            this.speedMultiplier = this.speedMultiplier - (this.speedMultiplier - 0.07) / 35.0;
            if (this.speedMultiplier < 0.07) {
               this.speedMultiplier = 0.07;
            }
         }

         for (int i = 0; i < 4; i++) {
            int n = MathHelper.floor_double(this.posX + (i % 2 - 0.5) * 0.8);
            int n2 = MathHelper.floor_double(this.posZ + (i / 2 - 0.5) * 0.8);

            for (int j = 0; j < 2; j++) {
               int n3 = MathHelper.floor_double(this.posY) + j;
               BlockPos blockPos = new BlockPos(n, n3, n2);
               Block block = this.worldObj.getBlockState(blockPos).getBlock();
               if (block == Blocks.snow_layer) {
                  this.worldObj.setBlockToAir(blockPos);
                  this.isCollidedHorizontally = false;
               } else if (block == Blocks.waterlily) {
                  this.worldObj.destroyBlock(blockPos, true);
                  this.isCollidedHorizontally = false;
               }
            }
         }

         if (this.onGround) {
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
         }

         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         if (this.isCollidedHorizontally && sqrt > 0.2975) {
            if (!this.worldObj.isRemote && !this.isDead) {
               this.setDead();
               if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                  for (int i = 0; i < 3; i++) {
                     this.dropItemWithOffset(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                  }

                  for (int i = 0; i < 2; i++) {
                     this.dropItemWithOffset(Items.stick, 1, 0.0F);
                  }
               }
            }
         } else {
            this.motionX *= 0.99F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.99F;
         }

         this.rotationPitch = 0.0F;
         double rotationYaw = this.rotationYaw;
         double d2 = this.prevPosX - this.posX;
         double d3 = this.prevPosZ - this.posZ;
         if (d2 * d2 + d3 * d3 > 0.001) {
            rotationYaw = (float)(MathHelper.atan2(d3, d2) * 180.0 / Math.PI);
         }

         double d4 = MathHelper.wrapAngleTo180_double(rotationYaw - this.rotationYaw);
         if (d4 > 20.0) {
            d4 = 20.0;
         }

         if (d4 < -20.0) {
            d4 = -20.0;
         }

         this.rotationYaw = (float)(this.rotationYaw + d4);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (!this.worldObj.isRemote) {
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F));
            if (list != null && !list.isEmpty()) {
               for (int i = 0; i < list.size(); i++) {
                  Entity entity = (Entity)list.get(i);
                  if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityBoat) {
                     entity.applyEntityCollision(this);
                  }
               }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
               this.riddenByEntity = null;
            }
         }
      }
   }

   @Override
   public void updateRiderPosition() {
      if (this.riddenByEntity != null) {
         double d = Math.cos(this.rotationYaw * Math.PI / 180.0) * 0.4;
         double d2 = Math.sin(this.rotationYaw * Math.PI / 180.0) * 0.4;
         this.riddenByEntity.setPosition(this.posX + d, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d2);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != playerIn) {
         return true;
      }

      if (!this.worldObj.isRemote) {
         playerIn.mountEntity(this);
      }

      return true;
   }

   @Override
   public void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
      if (onGroundIn) {
         if (this.fallDistance > 3.0F) {
            this.fall(this.fallDistance, 1.0F);
            if (!this.worldObj.isRemote && !this.isDead) {
               this.setDead();
               if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                  for (int i = 0; i < 3; i++) {
                     this.dropItemWithOffset(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                  }

                  for (int i = 0; i < 2; i++) {
                     this.dropItemWithOffset(Items.stick, 1, 0.0F);
                  }
               }
            }

            this.fallDistance = 0.0F;
         }
      } else if (this.worldObj.getBlockState(new BlockPos(this).down()).getBlock().getMaterial() != Material.water && y < 0.0) {
         this.fallDistance = (float)(this.fallDistance - y);
      }
   }

   public void setDamageTaken(float f) {
      this.dataWatcher.updateObject(19, f);
   }

   public float getDamageTaken() {
      return this.dataWatcher.getWatchableObjectFloat(19);
   }

   public void setTimeSinceHit(int n) {
      this.dataWatcher.updateObject(17, n);
   }

   public int getTimeSinceHit() {
      return this.dataWatcher.getWatchableObjectInt(17);
   }

   public void setForwardDirection(int n) {
      this.dataWatcher.updateObject(18, n);
   }

   public int getForwardDirection() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public void setIsBoatEmpty(boolean flag) {
      this.isBoatEmpty = flag;
   }
}
