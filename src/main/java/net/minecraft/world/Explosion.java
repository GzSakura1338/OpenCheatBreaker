package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Explosion {
   public Random explosionRNG = new Random();
   public List<BlockPos> affectedBlockPositions = Lists.newArrayList();
   public Map<EntityPlayer, Vec3> playerKnockbackMap = Maps.newHashMap();
   public World worldObj;
   public Entity exploder;
   public float explosionSize;
   public double explosionX;
   public double explosionY;
   public double explosionZ;
   public boolean isFlaming;
   public boolean isSmoking;

   public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
      this(worldIn, entityIn, x, y, z, size, false, true, affectedPositions);
   }

   public Explosion(
      World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean smoking, List<BlockPos> affectedPositions
   ) {
      this(worldIn, entityIn, x, y, z, size, flaming, smoking);
      this.affectedBlockPositions.addAll(affectedPositions);
   }

   public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean smoking) {
      this.worldObj = worldIn;
      this.exploder = entityIn;
      this.explosionSize = size;
      this.explosionX = x;
      this.explosionY = y;
      this.explosionZ = z;
      this.isFlaming = flaming;
      this.isSmoking = smoking;
   }

   public void doExplosionA() {
      HashSet newHashSet = Sets.newHashSet();
      byte b = 16;

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
               if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) {
                  double d = i / 15.0F * 2.0F - 1.0F;
                  double d2 = j / 15.0F * 2.0F - 1.0F;
                  double d3 = k / 15.0F * 2.0F - 1.0F;
                  double sqrt = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                  d /= sqrt;
                  d2 /= sqrt;
                  d3 /= sqrt;
                  float f = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                  double explosionX = this.explosionX;
                  double explosionY = this.explosionY;
                  double explosionZ = this.explosionZ;
                  float f2 = 0.3F;

                  while (f > 0.0F) {
                     BlockPos blockPos = new BlockPos(explosionX, explosionY, explosionZ);
                     IBlockState blockState = this.worldObj.getBlockState(blockPos);
                     if (blockState.getBlock().getMaterial() != Material.air) {
                        float f3 = this.exploder != null
                           ? this.exploder.getExplosionResistance(this, this.worldObj, blockPos, blockState)
                           : blockState.getBlock().getExplosionResistance((Entity)null);
                        f -= (f3 + 0.3F) * 0.3F;
                     }

                     if (f > 0.0F && (this.exploder == null || this.exploder.verifyExplosion(this, this.worldObj, blockPos, blockState, f))) {
                        newHashSet.add(blockPos);
                     }

                     explosionX += d * 0.3F;
                     explosionY += d2 * 0.3F;
                     explosionZ += d3 * 0.3F;
                     f -= 0.22500001F;
                  }
               }
            }
         }
      }

      this.affectedBlockPositions.addAll(newHashSet);
      float f = this.explosionSize * 2.0F;
      int n = MathHelper.floor_double(this.explosionX - f - 1.0);
      int n2 = MathHelper.floor_double(this.explosionX + f + 1.0);
      int n3 = MathHelper.floor_double(this.explosionY - f - 1.0);
      int n4 = MathHelper.floor_double(this.explosionY + f + 1.0);
      int n5 = MathHelper.floor_double(this.explosionZ - f - 1.0);
      int n6 = MathHelper.floor_double(this.explosionZ + f + 1.0);
      List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB(n, n3, n5, n2, n4, n6));
      Vec3 vec3 = new Vec3(this.explosionX, this.explosionY, this.explosionZ);

      for (int i = 0; i < list.size(); i++) {
         Entity entity = (Entity)list.get(i);
         if (!entity.isImmuneToExplosions()) {
            double d = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / f;
            if (d <= 1.0) {
               double d2 = entity.posX - this.explosionX;
               double d3 = entity.posY + entity.getEyeHeight() - this.explosionY;
               double d4 = entity.posZ - this.explosionZ;
               double d5 = MathHelper.sqrt_double(d2 * d2 + d3 * d3 + d4 * d4);
               if (d5 != 0.0) {
                  d2 /= d5;
                  d3 /= d5;
                  d4 /= d5;
                  double blockDensity = this.worldObj.getBlockDensity(vec3, entity.getEntityBoundingBox());
                  double d6 = (1.0 - d) * blockDensity;
                  entity.attackEntityFrom(DamageSource.setExplosionSource(this), (int)((d6 * d6 + d6) / 2.0 * 8.0 * f + 1.0));
                  double computeDouble = EnchantmentProtection.computeDouble(entity, d6);
                  entity.motionX += d2 * computeDouble;
                  entity.motionY += d3 * computeDouble;
                  entity.motionZ += d4 * computeDouble;
                  if (entity instanceof EntityPlayer && !((EntityPlayer)entity).capabilities.disableDamage) {
                     this.playerKnockbackMap.put((EntityPlayer)entity, new Vec3(d2 * d6, d3 * d6, d4 * d6));
                  }
               }
            }
         }
      }
   }

   public void doExplosionB(boolean spawnParticles) {
      this.worldObj
         .playSoundEffect(
            this.explosionX,
            this.explosionY,
            this.explosionZ,
            "random.explode",
            4.0F,
            (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F
         );
      if (this.explosionSize >= 2.0F && this.isSmoking) {
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
      } else {
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
      }

      if (this.isSmoking) {
         for (BlockPos blockPos : this.affectedBlockPositions) {
            Block block = this.worldObj.getBlockState(blockPos).getBlock();
            if (spawnParticles) {
               double d = blockPos.getX() + this.worldObj.rand.nextFloat();
               double d2 = blockPos.getY() + this.worldObj.rand.nextFloat();
               double d3 = blockPos.getZ() + this.worldObj.rand.nextFloat();
               double d4 = d - this.explosionX;
               double d5 = d2 - this.explosionY;
               double d6 = d3 - this.explosionZ;
               double d7 = MathHelper.sqrt_double(d4 * d4 + d5 * d5 + d6 * d6);
               d4 /= d7;
               d5 /= d7;
               d6 /= d7;
               double d8 = 0.5 / (d7 / this.explosionSize + 0.1);
               d8 *= this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F;
               d4 *= d8;
               d5 *= d8;
               d6 *= d8;
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.EXPLOSION_NORMAL,
                     (d + this.explosionX * 1.0) / 2.0,
                     (d2 + this.explosionY * 1.0) / 2.0,
                     (d3 + this.explosionZ * 1.0) / 2.0,
                     d4,
                     d5,
                     d6
                  );
               this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d2, d3, d4, d5, d6);
            }

            if (block.getMaterial() != Material.air) {
               if (block.canDropFromExplosion(this)) {
                  block.dropBlockAsItemWithChance(this.worldObj, blockPos, this.worldObj.getBlockState(blockPos), 1.0F / this.explosionSize, 0);
               }

               this.worldObj.setBlockState(blockPos, Blocks.air.getDefaultState(), 3);
               block.onBlockDestroyedByExplosion(this.worldObj, blockPos, this);
            }
         }
      }

      if (this.isFlaming) {
         for (BlockPos blockPos : this.affectedBlockPositions) {
            if (this.worldObj.getBlockState(blockPos).getBlock().getMaterial() == Material.air
               && this.worldObj.getBlockState(blockPos.down()).getBlock().isFullBlock()
               && this.explosionRNG.nextInt(3) == 0) {
               this.worldObj.setBlockState(blockPos, Blocks.fire.getDefaultState());
            }
         }
      }
   }

   public Map<EntityPlayer, Vec3> getPlayerKnockbackMap() {
      return this.playerKnockbackMap;
   }

   public EntityLivingBase getExplosivePlacedBy() {
      return this.exploder == null
         ? null
         : (
            this.exploder instanceof EntityTNTPrimed
               ? ((EntityTNTPrimed)this.exploder).getTntPlacedBy()
               : (this.exploder instanceof EntityLivingBase ? (EntityLivingBase)this.exploder : null)
         );
   }

   public void clearAffectedBlockPositions() {
      this.affectedBlockPositions.clear();
   }

   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }
}
