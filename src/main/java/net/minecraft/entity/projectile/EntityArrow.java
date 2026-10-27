package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityArrow extends Entity implements IProjectile {
   public int xTile = -1;
   public int yTile = -1;
   public int zTile = -1;
   public Block inTile;
   public int inData;
   public boolean inGround;
   public double damage = 2.0;
   public int arrowShake;
   public Entity shootingEntity;
   public int ticksInGround;
   public int ticksInAir;
   public int canBePickedUp;
   public int knockbackStrength;

   public EntityArrow(World worldIn) {
      super(worldIn);
      this.renderDistanceWeight = 10.0;
      this.setSize(0.5F, 0.5F);
   }

   public EntityArrow(World worldIn, double x, double y, double z) {
      super(worldIn);
      this.renderDistanceWeight = 10.0;
      this.setSize(0.5F, 0.5F);
      this.setPosition(x, y, z);
   }

   public EntityArrow(World worldIn, EntityLivingBase shooter, EntityLivingBase target, float velocity, float innacuracy) {
      super(worldIn);
      this.renderDistanceWeight = 10.0;
      this.shootingEntity = shooter;
      if (shooter instanceof EntityPlayer) {
         this.canBePickedUp = 1;
      }

      this.posY = shooter.posY + shooter.getEyeHeight() - 0.1F;
      double d = target.posX - shooter.posX;
      double d2 = target.getEntityBoundingBox().minY + target.height / 3.0F - this.posY;
      double d3 = target.posZ - shooter.posZ;
      double d4 = MathHelper.sqrt_double(d * d + d3 * d3);
      if (d4 >= 1.0E-7) {
         float f = (float)(MathHelper.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
         float f2 = (float)(-(MathHelper.atan2(d2, d4) * 180.0 / Math.PI));
         double d5 = d / d4;
         double d6 = d3 / d4;
         this.setLocationAndAngles(shooter.posX + d5, this.posY, shooter.posZ + d6, f, f2);
         float f3 = (float)(d4 * 0.2F);
         this.setThrowableHeading(d, d2 + f3, d3, velocity, innacuracy);
      }
   }

   public EntityArrow(World worldIn, EntityLivingBase shooter, float velocity) {
      super(worldIn);
      this.renderDistanceWeight = 10.0;
      this.shootingEntity = shooter;
      if (shooter instanceof EntityPlayer) {
         this.canBePickedUp = 1;
      }

      this.setSize(0.5F, 0.5F);
      this.setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
      this.posX = this.posX - MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.posY -= 0.1F;
      this.posZ = this.posZ - MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
      this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
      this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI);
      this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity * 1.5F, 1.0F);
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(16, (byte)0);
   }

   @Override
   public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
      float f = MathHelper.sqrt_double(x * x + y * y + z * z);
      x /= f;
      y /= f;
      z /= f;
      x += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * inaccuracy;
      y += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * inaccuracy;
      z += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * inaccuracy;
      x *= velocity;
      y *= velocity;
      z *= velocity;
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      float f2 = MathHelper.sqrt_double(x * x + z * z);
      this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(x, z) * 180.0 / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(y, f2) * 180.0 / Math.PI);
      this.ticksInGround = 0;
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean flag) {
      this.setPosition(x, y, z);
      this.setRotation(yaw, pitch);
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt_double(x * x + z * z);
         this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(x, z) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(y, f) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch;
         this.prevRotationYaw = this.rotationYaw;
         this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.ticksInGround = 0;
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f) * 180.0 / Math.PI);
      }

      BlockPos blockPos = new BlockPos(this.xTile, this.yTile, this.zTile);
      IBlockState blockState = this.worldObj.getBlockState(blockPos);
      Block block = blockState.getBlock();
      if (block.getMaterial() != Material.air) {
         block.setBlockBoundsBasedOnState(this.worldObj, blockPos);
         AxisAlignedBB collisionBoundingBox = block.getCollisionBoundingBox(this.worldObj, blockPos, blockState);
         if (collisionBoundingBox != null && collisionBoundingBox.isVecInside(new Vec3(this.posX, this.posY, this.posZ))) {
            this.inGround = true;
         }
      }

      if (this.arrowShake > 0) {
         this.arrowShake--;
      }

      if (this.inGround) {
         int metaFromState = block.getMetaFromState(blockState);
         if (block == this.inTile && metaFromState == this.inData) {
            this.ticksInGround++;
            if (this.ticksInGround >= 1200) {
               this.setDead();
            }
         } else {
            this.inGround = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         }
      } else {
         this.ticksInAir++;
         Vec3 vec3 = new Vec3(this.posX, this.posY, this.posZ);
         Vec3 vec32 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         MovingObjectPosition rayTraceBlocks = this.worldObj.rayTraceBlocks(vec3, vec32, false, true, false);
         vec3 = new Vec3(this.posX, this.posY, this.posZ);
         vec32 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         if (rayTraceBlocks != null) {
            vec32 = new Vec3(rayTraceBlocks.hitVec.xCoord, rayTraceBlocks.hitVec.yCoord, rayTraceBlocks.hitVec.zCoord);
         }

         Entity entity = null;
         List list = this.worldObj
            .getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
         double d = 0.0;

         for (int i = 0; i < list.size(); i++) {
            Entity entity2 = (Entity)list.get(i);
            if (entity2.canBeCollidedWith() && (entity2 != this.shootingEntity || this.ticksInAir >= 5)) {
               float f = 0.3F;
               AxisAlignedBB expand = entity2.getEntityBoundingBox().expand(f, f, f);
               MovingObjectPosition calculateIntercept = expand.calculateIntercept(vec3, vec32);
               if (calculateIntercept != null) {
                  double squareDistanceTo = vec3.squareDistanceTo(calculateIntercept.hitVec);
                  if (squareDistanceTo < d || d == 0.0) {
                     entity = entity2;
                     d = squareDistanceTo;
                  }
               }
            }
         }

         if (entity != null) {
            rayTraceBlocks = new MovingObjectPosition(entity);
         }

         if (rayTraceBlocks != null && rayTraceBlocks.entityHit != null && rayTraceBlocks.entityHit instanceof EntityPlayer) {
            EntityPlayer entityHit = (EntityPlayer)rayTraceBlocks.entityHit;
            if (entityHit.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityHit)) {
               rayTraceBlocks = null;
            }
         }

         if (rayTraceBlocks != null) {
            if (rayTraceBlocks.entityHit != null) {
               float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
               int n = MathHelper.ceiling_double_int(f * this.damage);
               if (this.getIsCritical()) {
                  n += this.rand.nextInt(n / 2 + 2);
               }

               DamageSource damageSource;
               if (this.shootingEntity == null) {
                  damageSource = DamageSource.causeArrowDamage(this, this);
               } else {
                  damageSource = DamageSource.causeArrowDamage(this, this.shootingEntity);
               }

               if (this.isBurning() && !(rayTraceBlocks.entityHit instanceof EntityEnderman)) {
                  rayTraceBlocks.entityHit.setFire(5);
               }

               if (rayTraceBlocks.entityHit.attackEntityFrom(damageSource, n)) {
                  if (rayTraceBlocks.entityHit instanceof EntityLivingBase) {
                     EntityLivingBase entityHit = (EntityLivingBase)rayTraceBlocks.entityHit;
                     if (!this.worldObj.isRemote) {
                        entityHit.setArrowCountInEntity(entityHit.getArrowCountInEntity() + 1);
                     }

                     if (this.knockbackStrength > 0) {
                        float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                        if (f2 > 0.0F) {
                           rayTraceBlocks.entityHit
                              .addVelocity(this.motionX * this.knockbackStrength * 0.6F / f2, 0.1, this.motionZ * this.knockbackStrength * 0.6F / f2);
                        }
                     }

                     if (this.shootingEntity instanceof EntityLivingBase) {
                        EnchantmentHelper.applyThornEnchantments(entityHit, this.shootingEntity);
                        EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.shootingEntity, entityHit);
                     }

                     if (this.shootingEntity != null
                        && rayTraceBlocks.entityHit != this.shootingEntity
                        && rayTraceBlocks.entityHit instanceof EntityPlayer
                        && this.shootingEntity instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                     }
                  }

                  this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                  if (!(rayTraceBlocks.entityHit instanceof EntityEnderman)) {
                     this.setDead();
                  }
               } else {
                  this.motionX *= -0.1F;
                  this.motionY *= -0.1F;
                  this.motionZ *= -0.1F;
                  this.rotationYaw += 180.0F;
                  this.prevRotationYaw += 180.0F;
                  this.ticksInAir = 0;
               }
            } else {
               BlockPos blockPos2 = rayTraceBlocks.getBlockPos();
               this.xTile = blockPos2.getX();
               this.yTile = blockPos2.getY();
               this.zTile = blockPos2.getZ();
               IBlockState blockState2 = this.worldObj.getBlockState(blockPos2);
               this.inTile = blockState2.getBlock();
               this.inData = this.inTile.getMetaFromState(blockState2);
               this.motionX = (float)(rayTraceBlocks.hitVec.xCoord - this.posX);
               this.motionY = (float)(rayTraceBlocks.hitVec.yCoord - this.posY);
               this.motionZ = (float)(rayTraceBlocks.hitVec.zCoord - this.posZ);
               float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
               this.posX = this.posX - this.motionX / f * 0.05F;
               this.posY = this.posY - this.motionY / f * 0.05F;
               this.posZ = this.posZ - this.motionZ / f * 0.05F;
               this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
               this.inGround = true;
               this.arrowShake = 7;
               this.setIsCritical(false);
               if (this.inTile.getMaterial() != Material.air) {
                  this.inTile.onEntityCollidedWithBlock(this.worldObj, blockPos2, blockState2, this);
               }
            }
         }

         if (this.getIsCritical()) {
            for (int i = 0; i < 4; i++) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.CRIT,
                     this.posX + this.motionX * i / 4.0,
                     this.posY + this.motionY * i / 4.0,
                     this.posZ + this.motionZ * i / 4.0,
                     -this.motionX,
                     -this.motionY + 0.2,
                     -this.motionZ
                  );
            }
         }

         this.posX = this.posX + this.motionX;
         this.posY = this.posY + this.motionY;
         this.posZ = this.posZ + this.motionZ;
         float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
         this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f) * 180.0 / Math.PI);

         while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
            this.prevRotationPitch -= 360.0F;
         }

         while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
         }

         while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
         }

         while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
         }

         this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
         this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
         float f2 = 0.99F;
         float f3 = 0.05F;
         if (this.isInWater()) {
            for (int i = 0; i < 4; i++) {
               float f4 = 0.25F;
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.WATER_BUBBLE,
                     this.posX - this.motionX * f4,
                     this.posY - this.motionY * f4,
                     this.posZ - this.motionZ * f4,
                     this.motionX,
                     this.motionY,
                     this.motionZ
                  );
            }

            f2 = 0.6F;
         }

         if (this.isWet()) {
            this.extinguish();
         }

         this.motionX *= f2;
         this.motionY *= f2;
         this.motionZ *= f2;
         this.motionY -= f3;
         this.setPosition(this.posX, this.posY, this.posZ);
         this.doBlockCollisions();
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setShort("xTile", (short)this.xTile);
      tagCompound.setShort("yTile", (short)this.yTile);
      tagCompound.setShort("zTile", (short)this.zTile);
      tagCompound.setShort("life", (short)this.ticksInGround);
      ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(this.inTile);
      tagCompound.setString("inTile", nameForObject == null ? "" : nameForObject.toString());
      tagCompound.setByte("inData", (byte)this.inData);
      tagCompound.setByte("shake", (byte)this.arrowShake);
      tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      tagCompound.setByte("pickup", (byte)this.canBePickedUp);
      tagCompound.setDouble("damage", this.damage);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.xTile = tagCompund.getShort("xTile");
      this.yTile = tagCompund.getShort("yTile");
      this.zTile = tagCompund.getShort("zTile");
      this.ticksInGround = tagCompund.getShort("life");
      if (tagCompund.hasKey("inTile", 8)) {
         this.inTile = Block.getBlockFromName(tagCompund.getString("inTile"));
      } else {
         this.inTile = Block.getBlockById(tagCompund.getByte("inTile") & 255);
      }

      this.inData = tagCompund.getByte("inData") & 255;
      this.arrowShake = tagCompund.getByte("shake") & 255;
      this.inGround = tagCompund.getByte("inGround") == 1;
      if (tagCompund.hasKey("damage", 99)) {
         this.damage = tagCompund.getDouble("damage");
      }

      if (tagCompund.hasKey("pickup", 99)) {
         this.canBePickedUp = tagCompund.getByte("pickup");
      } else if (tagCompund.hasKey("player", 99)) {
         this.canBePickedUp = tagCompund.getBoolean("player") ? 1 : 0;
      }
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
         boolean flag = this.canBePickedUp == 1 || this.canBePickedUp == 2 && entityIn.capabilities.isCreativeMode;
         if (this.canBePickedUp == 1 && !entityIn.inventory.addItemStackToInventory(new ItemStack(Items.arrow, 1))) {
            flag = false;
         }

         if (flag) {
            this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            entityIn.onItemPickup(this, 1);
            this.setDead();
         }
      }
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   public void setDamage(double damageIn) {
      this.damage = damageIn;
   }

   public double getDamage() {
      return this.damage;
   }

   public void setKnockbackStrength(int knockbackStrengthIn) {
      this.knockbackStrength = knockbackStrengthIn;
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }

   @Override
   public float getEyeHeight() {
      return 0.0F;
   }

   public void setIsCritical(boolean critical) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
      if (critical) {
         this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -2));
      }
   }

   public boolean getIsCritical() {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
      return (watchableObjectByte & 1) != 0;
   }

   public boolean isInGround() {
      return this.inGround;
   }
}
