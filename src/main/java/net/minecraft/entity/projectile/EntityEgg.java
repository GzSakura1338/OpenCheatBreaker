package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEgg extends EntityThrowable {
   public EntityEgg(World worldIn) {
      super(worldIn);
   }

   public EntityEgg(World worldIn, EntityLivingBase throwerIn) {
      super(worldIn, throwerIn);
   }

   public EntityEgg(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public void onImpact(MovingObjectPosition movingObjectPosition) {
      if (movingObjectPosition.entityHit != null) {
         movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
      }

      if (!this.worldObj.isRemote && this.rand.nextInt(8) == 0) {
         byte b = 1;
         if (this.rand.nextInt(32) == 0) {
            b = 4;
         }

         for (int i = 0; i < b; i++) {
            EntityChicken entityChicken = new EntityChicken(this.worldObj);
            entityChicken.setGrowingAge(-24000);
            entityChicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            this.worldObj.spawnEntityInWorld(entityChicken);
         }
      }

      double d = 0.08;

      for (int i = 0; i < 8; i++) {
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.ITEM_CRACK,
               this.posX,
               this.posY,
               this.posZ,
               (this.rand.nextFloat() - 0.5) * 0.08,
               (this.rand.nextFloat() - 0.5) * 0.08,
               (this.rand.nextFloat() - 0.5) * 0.08,
               Item.getIdFromItem(Items.egg)
            );
      }

      if (!this.worldObj.isRemote) {
         this.setDead();
      }
   }
}
