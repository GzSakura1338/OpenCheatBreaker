package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEgg extends EntityThrowable {
    public EntityEgg(World world) {
        super(world);
    }

    public EntityEgg(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityEgg(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
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

        for (int i = 0; i < 8; i++) {
            this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        if (!this.worldObj.isRemote) {
            this.setDead();
        }
    }
}
