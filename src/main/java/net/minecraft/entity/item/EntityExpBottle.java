package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable {
    public EntityExpBottle(World world) {
        super(world);
    }

    public EntityExpBottle(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityExpBottle(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
    }

    @Override
    public float getGravityVelocity() {
        return 0.07F;
    }

    @Override
    public float constantFloat() {
        return 0.7F;
    }

    @Override
    public float constantFloat2() {
        return -20.0F;
    }

    @Override
    public void onImpact(MovingObjectPosition movingObjectPosition) {
        if (!this.worldObj.isRemote) {
            this.worldObj.playAuxSFX(2002, (int)Math.round(this.posX), (int)Math.round(this.posY), (int)Math.round(this.posZ), 0);
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
