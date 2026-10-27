package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEnderPearl extends EntityThrowable {
   public EntityLivingBase owner;

   public EntityEnderPearl(World worldIn) {
      super(worldIn);
   }

   public EntityEnderPearl(World worldIn, EntityLivingBase entityLivingBase) {
      super(worldIn, entityLivingBase);
      this.owner = entityLivingBase;
   }

   public EntityEnderPearl(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public void onImpact(MovingObjectPosition movingObjectPosition) {
      EntityLivingBase thrower = this.getThrower();
      if (movingObjectPosition.entityHit != null) {
         if (movingObjectPosition.entityHit == this.owner) {
            return;
         }

         movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), 0.0F);
      }

      for (int i = 0; i < 32; i++) {
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.PORTAL,
               this.posX,
               this.posY + this.rand.nextDouble() * 2.0,
               this.posZ,
               this.rand.nextGaussian(),
               0.0,
               this.rand.nextGaussian()
            );
      }

      if (!this.worldObj.isRemote) {
         if (thrower instanceof EntityPlayerMP) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)thrower;
            if (entityPlayerMP.playerNetServerHandler.getNetworkManager().isChannelOpen() && entityPlayerMP.worldObj == this.worldObj && !entityPlayerMP.isPlayerSleeping()) {
               if (this.rand.nextFloat() < 0.05F && this.worldObj.getGameRules().getBoolean("doMobSpawning")) {
                  EntityEndermite entityEndermite = new EntityEndermite(this.worldObj);
                  entityEndermite.setSpawnedByPlayer(true);
                  entityEndermite.setLocationAndAngles(thrower.posX, thrower.posY, thrower.posZ, thrower.rotationYaw, thrower.rotationPitch);
                  this.worldObj.spawnEntityInWorld(entityEndermite);
               }

               if (thrower.isRiding()) {
                  thrower.mountEntity((Entity)null);
               }

               thrower.setPositionAndUpdate(this.posX, this.posY, this.posZ);
               thrower.fallDistance = 0.0F;
               thrower.attackEntityFrom(DamageSource.fall, 5.0F);
            }
         } else if (thrower != null) {
            thrower.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            thrower.fallDistance = 0.0F;
         }

         this.setDead();
      }
   }

   @Override
   public void onUpdate() {
      EntityLivingBase thrower = this.getThrower();
      if (thrower != null && thrower instanceof EntityPlayer && !thrower.isEntityAlive()) {
         this.setDead();
      } else {
         super.onUpdate();
      }
   }
}
