package net.minecraft.entity;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityFlying extends EntityLiving {
    public EntityFlying(World world) {
        super(world);
    }

    @Override
    public void fall(float distance) {
    }

    @Override
    public void updateFallState(double distanceFallenThisTick, boolean isOnGround) {
    }

    @Override
    public void moveEntityWithHeading(float f, float f2) {
        if (this.isInWater()) {
            this.moveFlying(f, f2, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8F;
            this.motionY *= 0.8F;
            this.motionZ *= 0.8F;
        } else if (this.handleLavaMovement()) {
            this.moveFlying(f, f2, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        } else {
            float f3 = 0.91F;
            if (this.onGround) {
                f3 = this.worldObj
                        .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ))
                        .slipperiness
                    * 0.91F;
            }

            float f4 = 0.16277136F / (f3 * f3 * f3);
            this.moveFlying(f, f2, this.onGround ? 0.1F * f4 : 0.02F);
            f3 = 0.91F;
            if (this.onGround) {
                f3 = this.worldObj
                        .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ))
                        .slipperiness
                    * 0.91F;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= f3;
            this.motionY *= f3;
            this.motionZ *= f3;
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d = this.posX - this.prevPosX;
        double d2 = this.posZ - this.prevPosZ;
        float f3 = MathHelper.sqrt_double(d * d + d2 * d2) * 4.0F;
        if (f3 > 1.0F) {
            f3 = 1.0F;
        }

        this.limbSwingAmount = this.limbSwingAmount + (f3 - this.limbSwingAmount) * 0.4F;
        this.limbSwing = this.limbSwing + this.limbSwingAmount;
    }

    @Override
    public boolean isOnLadder() {
        return false;
    }
}
