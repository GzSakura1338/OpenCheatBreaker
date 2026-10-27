package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySnowball extends EntityThrowable {
   public EntitySnowball(World worldIn) {
      super(worldIn);
   }

   public EntitySnowball(World worldIn, EntityLivingBase throwerIn) {
      super(worldIn, throwerIn);
   }

   public EntitySnowball(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public void onImpact(MovingObjectPosition movingObjectPosition) {
      if (movingObjectPosition.entityHit != null) {
         byte b = 0;
         if (movingObjectPosition.entityHit instanceof EntityBlaze) {
            b = 3;
         }

         movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), b);
      }

      for (int i = 0; i < 8; i++) {
         this.worldObj.spawnParticle(EnumParticleTypes.SNOWBALL, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
      }

      if (!this.worldObj.isRemote) {
         this.setDead();
      }
   }
}
