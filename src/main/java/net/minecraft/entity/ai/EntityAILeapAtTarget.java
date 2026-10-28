package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityAILeapAtTarget extends EntityAIBase {
    public EntityLiving leaper;
    public EntityLivingBase leapTarget;
    public float leapMotionY;

    public EntityAILeapAtTarget(EntityLiving entityLiving, float f) {
        this.leaper = entityLiving;
        this.leapMotionY = f;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        this.leapTarget = this.leaper.getAttackTarget();
        if (this.leapTarget == null) {
            return false;
        }

        double distanceSqToEntity = this.leaper.getDistanceSqToEntity(this.leapTarget);
        return distanceSqToEntity >= 4.0 && distanceSqToEntity <= 16.0 && this.leaper.onGround && this.leaper.getRNG().nextInt(5) == 0;
    }

    @Override
    public boolean continueExecuting() {
        return !this.leaper.onGround;
    }

    @Override
    public void startExecuting() {
        double d = this.leapTarget.posX - this.leaper.posX;
        double d2 = this.leapTarget.posZ - this.leaper.posZ;
        float f = MathHelper.sqrt_double(d * d + d2 * d2);
        this.leaper.motionX = this.leaper.motionX + (d / f * 0.5 * 0.8F + this.leaper.motionX * 0.2F);
        this.leaper.motionZ = this.leaper.motionZ + (d2 / f * 0.5 * 0.8F + this.leaper.motionZ * 0.2F);
        this.leaper.motionY = this.leapMotionY;
    }
}
