package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable {
   public EntityExpBottle(World worldIn) {
      super(worldIn);
   }

   public EntityExpBottle(World worldIn, EntityLivingBase entityLivingBase) {
      super(worldIn, entityLivingBase);
   }

   public EntityExpBottle(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public float getGravityVelocity() {
      return 0.07F;
   }

   @Override
   public float getVelocity() {
      return 0.7F;
   }

   @Override
   public float getInaccuracy() {
      return -20.0F;
   }

   @Override
   public void onImpact(MovingObjectPosition movingObjectPosition) {
      if (!this.worldObj.isRemote) {
         this.worldObj.playAuxSFX(2002, new BlockPos(this), 0);
         int n = 3 + this.worldObj.rand.nextInt(5) + this.worldObj.rand.nextInt(5);

         while (n > 0) {
            int xPSplit = EntityXPOrb.getXPSplit(n);
            n -= xPSplit;
            this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, xPSplit));
         }

         this.setDead();
      }
   }
}
