package net.minecraft.entity.boss;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityDragon extends EntityLiving implements IMob, IEntityMultiPart, IBossDisplayData {
   public double targetX;
   public double[][] ringBuffer = new double[64][3];
   public double targetZ;
   public int ringBufferIndex = -1;
   public EntityDragonPart dragonPartHead;
   public EntityDragonPart dragonPartBody;
   public EntityDragonPart dragonPartTail1;
   public EntityDragonPart dragonPartTail2;
   public EntityDragonPart dragonPartTail3;
   public EntityDragonPart dragonPartWing1;
   public EntityDragonPart dragonPartWing2;
   public EntityDragonPart[] dragonPartArray;
   public double targetY;
   public float prevAnimTime;
   public float animTime;
   public boolean forceNewTarget;
   public boolean slowed;
   public Entity target;
   public int deathTicks;
   public EntityEnderCrystal healingEnderCrystal;

   public EntityDragon(World worldIn) {
      super(worldIn);
      this.dragonPartArray = new EntityDragonPart[]{
         this.dragonPartHead = new EntityDragonPart(this, "head", 6.0F, 6.0F),
         this.dragonPartBody = new EntityDragonPart(this, "body", 8.0F, 8.0F),
         this.dragonPartTail1 = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.dragonPartTail2 = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.dragonPartTail3 = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.dragonPartWing1 = new EntityDragonPart(this, "wing", 4.0F, 4.0F),
         this.dragonPartWing2 = new EntityDragonPart(this, "wing", 4.0F, 4.0F)
      };
      this.setHealth(this.getMaxHealth());
      this.setSize(16.0F, 8.0F);
      this.noClip = true;
      this.isImmuneToFire = true;
      this.targetY = 100.0;
      this.ignoreFrustumCheck = true;
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0);
   }

   @Override
   public void entityInit() {
      super.entityInit();
   }

   public double[] getMovementOffsets(int n, float f) {
      if (this.getHealth() <= 0.0F) {
         f = 0.0F;
      }

      f = 1.0F - f;
      int n2 = this.ringBufferIndex - n * 1 & 63;
      int n3 = this.ringBufferIndex - n * 1 - 1 & 63;
      double[] doubles = new double[3];
      double d = this.ringBuffer[n2][0];
      double d2 = MathHelper.wrapAngleTo180_double(this.ringBuffer[n3][0] - d);
      doubles[0] = d + d2 * f;
      d = this.ringBuffer[n2][1];
      d2 = this.ringBuffer[n3][1] - d;
      doubles[1] = d + d2 * f;
      doubles[2] = this.ringBuffer[n2][2] + (this.ringBuffer[n3][2] - this.ringBuffer[n2][2]) * f;
      return doubles;
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         float cos = MathHelper.cos(this.animTime * (float) Math.PI * 2.0F);
         float cos2 = MathHelper.cos(this.prevAnimTime * (float) Math.PI * 2.0F);
         if (cos2 <= -0.3F && cos >= -0.3F && !this.isSilent()) {
            this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
         }
      }

      this.prevAnimTime = this.animTime;
      if (this.getHealth() <= 0.0F) {
         float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float f2 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float f3 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX + f, this.posY + 2.0 + f2, this.posZ + f3, 0.0, 0.0, 0.0);
      } else {
         this.updateDragonEnderCrystal();
         float f = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
         f *= (float)Math.pow(2.0, this.motionY);
         if (this.slowed) {
            this.animTime += f * 0.5F;
         } else {
            this.animTime += f;
         }

         this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
         if (this.isAIDisabled()) {
            this.animTime = 0.5F;
         } else {
            if (this.ringBufferIndex < 0) {
               for (int i = 0; i < this.ringBuffer.length; i++) {
                  this.ringBuffer[i][0] = this.rotationYaw;
                  this.ringBuffer[i][1] = this.posY;
               }
            }

            if (++this.ringBufferIndex == this.ringBuffer.length) {
               this.ringBufferIndex = 0;
            }

            this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
            this.ringBuffer[this.ringBufferIndex][1] = this.posY;
            if (this.worldObj.isRemote) {
               if (this.newPosRotationIncrements > 0) {
                  double d = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
                  double d2 = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
                  double d3 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
                  double d4 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
                  this.rotationYaw = (float)(this.rotationYaw + d4 / this.newPosRotationIncrements);
                  this.rotationPitch = (float)(this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
                  this.newPosRotationIncrements--;
                  this.setPosition(d, d2, d3);
                  this.setRotation(this.rotationYaw, this.rotationPitch);
               }
            } else {
               double d = this.targetX - this.posX;
               double d2 = this.targetY - this.posY;
               double d3 = this.targetZ - this.posZ;
               double d4 = d * d + d2 * d2 + d3 * d3;
               if (this.target != null) {
                  this.targetX = this.target.posX;
                  this.targetZ = this.target.posZ;
                  double d5 = this.targetX - this.posX;
                  double d6 = this.targetZ - this.posZ;
                  double sqrt = Math.sqrt(d5 * d5 + d6 * d6);
                  double d7 = 0.4F + sqrt / 80.0 - 1.0;
                  if (d7 > 10.0) {
                     d7 = 10.0;
                  }

                  this.targetY = this.target.getEntityBoundingBox().minY + d7;
               } else {
                  this.targetX = this.targetX + this.rand.nextGaussian() * 2.0;
                  this.targetZ = this.targetZ + this.rand.nextGaussian() * 2.0;
               }

               if (this.forceNewTarget || d4 < 100.0 || d4 > 22500.0 || this.isCollidedHorizontally || this.isCollidedVertically) {
                  this.setNewTarget();
               }

               d2 /= MathHelper.sqrt_double(d * d + d3 * d3);
               float f2 = 0.6F;
               d2 = MathHelper.clamp_double(d2, -f2, f2);
               this.motionY += d2 * 0.1F;
               this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
               double d5 = 180.0 - MathHelper.atan2(d, d3) * 180.0 / Math.PI;
               double d6 = MathHelper.wrapAngleTo180_double(d5 - this.rotationYaw);
               if (d6 > 50.0) {
                  d6 = 50.0;
               }

               if (d6 < -50.0) {
                  d6 = -50.0;
               }

               Vec3 normalize = new Vec3(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ).normalize();
               double d7 = -MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
               Vec3 normalize2 = new Vec3(MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F), this.motionY, d7).normalize();
               float f3 = ((float)normalize2.dotProduct(normalize) + 0.5F) / 1.5F;
               if (f3 < 0.0F) {
                  f3 = 0.0F;
               }

               this.randomYawVelocity *= 0.8F;
               float f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
               double d8 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0 + 1.0;
               if (d8 > 40.0) {
                  d8 = 40.0;
               }

               this.randomYawVelocity = (float)(this.randomYawVelocity + d6 * (0.7F / d8 / f4));
               this.rotationYaw = this.rotationYaw + this.randomYawVelocity * 0.1F;
               float f5 = (float)(2.0 / (d8 + 1.0));
               float f6 = 0.06F;
               this.moveFlying(0.0F, -1.0F, f6 * (f3 * f5 + (1.0F - f5)));
               if (this.slowed) {
                  this.moveEntity(this.motionX * 0.8F, this.motionY * 0.8F, this.motionZ * 0.8F);
               } else {
                  this.moveEntity(this.motionX, this.motionY, this.motionZ);
               }

               Vec3 normalize3 = new Vec3(this.motionX, this.motionY, this.motionZ).normalize();
               float f7 = ((float)normalize3.dotProduct(normalize2) + 1.0F) / 2.0F;
               f7 = 0.8F + 0.15F * f7;
               this.motionX *= f7;
               this.motionZ *= f7;
               this.motionY *= 0.91F;
            }

            this.renderYawOffset = this.rotationYaw;
            this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
            this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
            this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
            this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
            this.dragonPartBody.height = 3.0F;
            this.dragonPartBody.width = 5.0F;
            this.dragonPartWing1.height = 2.0F;
            this.dragonPartWing1.width = 4.0F;
            this.dragonPartWing2.height = 3.0F;
            this.dragonPartWing2.width = 4.0F;
            float f2 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
            float cos = MathHelper.cos(f2);
            float f3 = -MathHelper.sin(f2);
            float f4 = this.rotationYaw * (float) Math.PI / 180.0F;
            float sin = MathHelper.sin(f4);
            float cos2 = MathHelper.cos(f4);
            this.dragonPartBody.onUpdate();
            this.dragonPartBody.setLocationAndAngles(this.posX + sin * 0.5F, this.posY, this.posZ - cos2 * 0.5F, 0.0F, 0.0F);
            this.dragonPartWing1.onUpdate();
            this.dragonPartWing1.setLocationAndAngles(this.posX + cos2 * 4.5F, this.posY + 2.0, this.posZ + sin * 4.5F, 0.0F, 0.0F);
            this.dragonPartWing2.onUpdate();
            this.dragonPartWing2.setLocationAndAngles(this.posX - cos2 * 4.5F, this.posY + 2.0, this.posZ - sin * 4.5F, 0.0F, 0.0F);
            if (!this.worldObj.isRemote && this.hurtTime == 0) {
               this.collideWithEntities(
                  this.worldObj
                     .getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.getEntityBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0))
               );
               this.collideWithEntities(
                  this.worldObj
                     .getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.getEntityBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0))
               );
               this.attackEntitiesInList(
                  this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.getEntityBoundingBox().expand(1.0, 1.0, 1.0))
               );
            }

            double[] movementOffsets = this.getMovementOffsets(5, 1.0F);
            double[] movementOffsets2 = this.getMovementOffsets(0, 1.0F);
            float sin2 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            float cos3 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            this.dragonPartHead.onUpdate();
            this.dragonPartHead
               .setLocationAndAngles(
                  this.posX + sin2 * 5.5F * cos, this.posY + (movementOffsets2[1] - movementOffsets[1]) * 1.0 + f3 * 5.5F, this.posZ - cos3 * 5.5F * cos, 0.0F, 0.0F
               );

            for (int i = 0; i < 3; i++) {
               EntityDragonPart entityDragonPart = null;
               if (i == 0) {
                  entityDragonPart = this.dragonPartTail1;
               }

               if (i == 1) {
                  entityDragonPart = this.dragonPartTail2;
               }

               if (i == 2) {
                  entityDragonPart = this.dragonPartTail3;
               }

               double[] movementOffsets3 = this.getMovementOffsets(12 + i * 2, 1.0F);
               float f5 = this.rotationYaw * (float) Math.PI / 180.0F + this.simplifyAngle(movementOffsets3[0] - movementOffsets[0]) * (float) Math.PI / 180.0F * 1.0F;
               float sin3 = MathHelper.sin(f5);
               float cos4 = MathHelper.cos(f5);
               float f6 = 1.5F;
               float f7 = (i + 1) * 2.0F;
               entityDragonPart.onUpdate();
               entityDragonPart.setLocationAndAngles(
                  this.posX - (sin * f6 + sin3 * f7) * cos,
                  this.posY + (movementOffsets3[1] - movementOffsets[1]) * 1.0 - (f7 + f6) * f3 + 1.5,
                  this.posZ + (cos2 * f6 + cos4 * f7) * cos,
                  0.0F,
                  0.0F
               );
            }

            if (!this.worldObj.isRemote) {
               this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getEntityBoundingBox())
                  | this.destroyBlocksInAABB(this.dragonPartBody.getEntityBoundingBox());
            }
         }
      }
   }

   public void updateDragonEnderCrystal() {
      if (this.healingEnderCrystal != null) {
         if (this.healingEnderCrystal.isDead) {
            if (!this.worldObj.isRemote) {
               this.attackEntityFromPart(this.dragonPartHead, DamageSource.setExplosionSource((Explosion)null), 10.0F);
            }

            this.healingEnderCrystal = null;
         } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 1.0F);
         }
      }

      if (this.rand.nextInt(10) == 0) {
         float f = 32.0F;
         List entitiesWithinAABB = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().expand(f, f, f));
         EntityEnderCrystal entityEnderCrystal = null;
         double d = Double.MAX_VALUE;

         for (EntityEnderCrystal entityEnderCrystal2 : (Iterable<EntityEnderCrystal>)entitiesWithinAABB) {
            double distanceSqToEntity = entityEnderCrystal2.getDistanceSqToEntity(this);
            if (distanceSqToEntity < d) {
               d = distanceSqToEntity;
               entityEnderCrystal = entityEnderCrystal2;
            }
         }

         this.healingEnderCrystal = entityEnderCrystal;
      }
   }

   public void collideWithEntities(List<Entity> list) {
      double d = (this.dragonPartBody.getEntityBoundingBox().minX + this.dragonPartBody.getEntityBoundingBox().maxX) / 2.0;
      double d2 = (this.dragonPartBody.getEntityBoundingBox().minZ + this.dragonPartBody.getEntityBoundingBox().maxZ) / 2.0;

      for (Entity entity : list) {
         if (entity instanceof EntityLivingBase) {
            double d3 = entity.posX - d;
            double d4 = entity.posZ - d2;
            double d5 = d3 * d3 + d4 * d4;
            entity.addVelocity(d3 / d5 * 4.0, 0.2F, d4 / d5 * 4.0);
         }
      }
   }

   public void attackEntitiesInList(List<Entity> list) {
      for (int i = 0; i < list.size(); i++) {
         Entity entity = (Entity)list.get(i);
         if (entity instanceof EntityLivingBase) {
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
            this.applyEnchantments(this, entity);
         }
      }
   }

   public void setNewTarget() {
      this.forceNewTarget = false;
      ArrayList newArrayList = Lists.newArrayList(this.worldObj.playerEntities);
      Iterator iterator = newArrayList.iterator();

      while (iterator.hasNext()) {
         if (((EntityPlayer)iterator.next()).isSpectator()) {
            iterator.remove();
         }
      }

      if (this.rand.nextInt(2) == 0 && !newArrayList.isEmpty()) {
         this.target = (Entity)newArrayList.get(this.rand.nextInt(newArrayList.size()));
      } else {
         boolean flag;
         do {
            this.targetX = 0.0;
            this.targetY = 70.0F + this.rand.nextFloat() * 50.0F;
            this.targetZ = 0.0;
            this.targetX = this.targetX + (this.rand.nextFloat() * 120.0F - 60.0F);
            this.targetZ = this.targetZ + (this.rand.nextFloat() * 120.0F - 60.0F);
            double d = this.posX - this.targetX;
            double d2 = this.posY - this.targetY;
            double d3 = this.posZ - this.targetZ;
            flag = d * d + d2 * d2 + d3 * d3 > 100.0;
         } while (!flag);

         this.target = null;
      }
   }

   public float simplifyAngle(double d) {
      return (float)MathHelper.wrapAngleTo180_double(d);
   }

   public boolean destroyBlocksInAABB(AxisAlignedBB axisAlignedBB) {
      int n = MathHelper.floor_double(axisAlignedBB.minX);
      int n2 = MathHelper.floor_double(axisAlignedBB.minY);
      int n3 = MathHelper.floor_double(axisAlignedBB.minZ);
      int n4 = MathHelper.floor_double(axisAlignedBB.maxX);
      int n5 = MathHelper.floor_double(axisAlignedBB.maxY);
      int n6 = MathHelper.floor_double(axisAlignedBB.maxZ);
      boolean flag = false;
      boolean flag2 = false;

      for (int i = n; i <= n4; i++) {
         for (int j = n2; j <= n5; j++) {
            for (int k = n3; k <= n6; k++) {
               BlockPos blockPos = new BlockPos(i, j, k);
               Block block = this.worldObj.getBlockState(blockPos).getBlock();
               if (block.getMaterial() != Material.air) {
                  if (block != Blocks.barrier
                     && block != Blocks.obsidian
                     && block != Blocks.end_stone
                     && block != Blocks.bedrock
                     && block != Blocks.command_block
                     && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
                     flag2 = this.worldObj.setBlockToAir(blockPos) || flag2;
                  } else {
                     flag = true;
                  }
               }
            }
         }
      }

      if (flag2) {
         double d = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * this.rand.nextFloat();
         double d2 = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * this.rand.nextFloat();
         double d3 = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * this.rand.nextFloat();
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d, d2, d3, 0.0, 0.0, 0.0);
      }

      return flag;
   }

   @Override
   public boolean attackEntityFromPart(EntityDragonPart dragonPart, DamageSource source, float f) {
      if (dragonPart != this.dragonPartHead) {
         f = f / 4.0F + 1.0F;
      }

      float f2 = this.rotationYaw * (float) Math.PI / 180.0F;
      float sin = MathHelper.sin(f2);
      float cos = MathHelper.cos(f2);
      this.targetX = this.posX + sin * 5.0F + (this.rand.nextFloat() - 0.5F) * 2.0F;
      this.targetY = this.posY + this.rand.nextFloat() * 3.0F + 1.0;
      this.targetZ = this.posZ - cos * 5.0F + (this.rand.nextFloat() - 0.5F) * 2.0F;
      this.target = null;
      if (source.getEntity() instanceof EntityPlayer || source.isExplosion()) {
         this.attackDragonFrom(source, f);
      }

      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage()) {
         this.attackDragonFrom(source, amount);
      }

      return false;
   }

   public boolean attackDragonFrom(DamageSource source, float amount) {
      return super.attackEntityFrom(source, amount);
   }

   @Override
   public void onKillCommand() {
      this.setDead();
   }

   @Override
   public void onDeathUpdate() {
      this.deathTicks++;
      if (this.deathTicks >= 180 && this.deathTicks <= 200) {
         float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float f2 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float f3 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + f, this.posY + 2.0 + f2, this.posZ + f3, 0.0, 0.0, 0.0);
      }

      boolean boolean2 = this.worldObj.getGameRules().getBoolean("doMobLoot");
      if (!this.worldObj.isRemote) {
         if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && boolean2) {
            int n = 1000;

            while (n > 0) {
               int xPSplit = EntityXPOrb.getXPSplit(n);
               n -= xPSplit;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, xPSplit));
            }
         }

         if (this.deathTicks == 1) {
            this.worldObj.playBroadcastSound(1018, new BlockPos(this), 0);
         }
      }

      this.moveEntity(0.0, 0.1F, 0.0);
      this.renderYawOffset = this.rotationYaw += 20.0F;
      if (this.deathTicks == 200 && !this.worldObj.isRemote) {
         if (boolean2) {
            int n = 2000;

            while (n > 0) {
               int xPSplit = EntityXPOrb.getXPSplit(n);
               n -= xPSplit;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, xPSplit));
            }
         }

         this.generatePortal(new BlockPos(this.posX, 64.0, this.posZ));
         this.setDead();
      }
   }

   public void generatePortal(BlockPos pos) {
      byte b = 4;
      double d = 12.25;
      double d2 = 6.25;

      for (int i = -1; i <= 32; i++) {
         for (int j = -4; j <= 4; j++) {
            for (int k = -4; k <= 4; k++) {
               double d3 = j * j + k * k;
               if (d3 <= 12.25) {
                  BlockPos blockPos = pos.add(j, i, k);
                  if (i < 0) {
                     if (d3 <= 6.25) {
                        this.worldObj.setBlockState(blockPos, Blocks.bedrock.getDefaultState());
                     }
                  } else if (i > 0) {
                     this.worldObj.setBlockState(blockPos, Blocks.air.getDefaultState());
                  } else if (d3 > 6.25) {
                     this.worldObj.setBlockState(blockPos, Blocks.bedrock.getDefaultState());
                  } else {
                     this.worldObj.setBlockState(blockPos, Blocks.end_portal.getDefaultState());
                  }
               }
            }
         }
      }

      this.worldObj.setBlockState(pos, Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(pos.up(), Blocks.bedrock.getDefaultState());
      BlockPos up = pos.up(2);
      this.worldObj.setBlockState(up, Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(up.west(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
      this.worldObj.setBlockState(up.east(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
      this.worldObj.setBlockState(up.north(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
      this.worldObj.setBlockState(up.south(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
      this.worldObj.setBlockState(pos.up(3), Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(pos.up(4), Blocks.dragon_egg.getDefaultState());
   }

   @Override
   public void despawnEntity() {
   }

   @Override
   public Entity[] getParts() {
      return this.dragonPartArray;
   }

   @Override
   public boolean canBeCollidedWith() {
      return false;
   }

   @Override
   public World getWorld() {
      return this.worldObj;
   }

   @Override
   public String getLivingSound() {
      return "mob.enderdragon.growl";
   }

   @Override
   public String getHurtSound() {
      return "mob.enderdragon.hit";
   }

   @Override
   public float getSoundVolume() {
      return 5.0F;
   }
}
