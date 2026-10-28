package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.util.MathHelper;

public class EntityAIArrowAttack extends EntityAIBase {
    public int rangedAttackTime = -1;
    public IRangedAttackMob rangedAttackEntityHost;
    public EntityLivingBase attackTarget;
    public EntityLiving entityHost;
    public double entityMoveSpeed;
    public int seenTargetTicks;
    public int minIntervalTicks;
    public int maxRangedAttackTime;
    public float maxShootRange;
    public float maxAttackDistance;

    public EntityAIArrowAttack(IRangedAttackMob rangedAttackMob, double d, int n, float f) {
        this(rangedAttackMob, d, n, n, f);
    }

    public EntityAIArrowAttack(IRangedAttackMob rangedAttackMob, double d, int n, int n2, float f) {
        if (!(rangedAttackMob instanceof EntityLivingBase)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }

        this.rangedAttackEntityHost = rangedAttackMob;
        this.entityHost = (EntityLiving)rangedAttackMob;
        this.entityMoveSpeed = d;
        this.minIntervalTicks = n;
        this.maxRangedAttackTime = n2;
        this.maxShootRange = f;
        this.maxAttackDistance = f * f;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase attackTarget = this.entityHost.getAttackTarget();
        if (attackTarget == null) {
            return false;
        }

        this.attackTarget = attackTarget;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    @Override
    public void resetTask() {
        this.attackTarget = null;
        this.seenTargetTicks = 0;
        this.rangedAttackTime = -1;
    }

    @Override
    public void updateTask() {
        double distanceSq = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        boolean canSee = this.entityHost.getEntitySenses().canSee(this.attackTarget);
        if (canSee) {
            this.seenTargetTicks++;
        } else {
            this.seenTargetTicks = 0;
        }

        if (distanceSq <= this.maxAttackDistance && this.seenTargetTicks >= 20) {
            this.entityHost.getNavigator().clearPathEntity();
        } else {
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }

        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
        if (--this.rangedAttackTime == 0) {
            if (distanceSq > this.maxAttackDistance || !canSee) {
                return;
            }

            float f = MathHelper.sqrt_double(distanceSq) / this.maxShootRange;
            float f2 = f;
            if (f < 0.1F) {
                f2 = 0.1F;
            }

            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, f2);
            this.rangedAttackTime = MathHelper.floor_float(f * (this.maxRangedAttackTime - this.minIntervalTicks) + this.minIntervalTicks);
        } else if (this.rangedAttackTime < 0) {
            float f = MathHelper.sqrt_double(distanceSq) / this.maxShootRange;
            this.rangedAttackTime = MathHelper.floor_float(f * (this.maxRangedAttackTime - this.minIntervalTicks) + this.minIntervalTicks);
        }
    }
}
