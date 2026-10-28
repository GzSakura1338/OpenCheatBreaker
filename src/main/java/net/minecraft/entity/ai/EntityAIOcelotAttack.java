package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityAIOcelotAttack extends EntityAIBase {
    public EntityLiving theEntity;
    public World theWorld;
    public EntityLivingBase theVictim;
    public int attackCountdown;

    public EntityAIOcelotAttack(EntityLiving entityLiving) {
        this.theEntity = entityLiving;
        this.theWorld = entityLiving.worldObj;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase attackTarget = this.theEntity.getAttackTarget();
        if (attackTarget == null) {
            return false;
        }

        this.theVictim = attackTarget;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return this.theVictim.isEntityAlive()
            && !(this.theEntity.getDistanceSqToEntity(this.theVictim) > 225.0)
            && (!this.theEntity.getNavigator().noPath() || this.shouldExecute());
    }

    @Override
    public void resetTask() {
        this.theVictim = null;
        this.theEntity.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        this.theEntity.getLookHelper().setLookPositionWithEntity(this.theVictim, 30.0F, 30.0F);
        double d = this.theEntity.width * 2.0F * this.theEntity.width * 2.0F;
        double distanceSq = this.theEntity.getDistanceSq(this.theVictim.posX, this.theVictim.boundingBox.minY, this.theVictim.posZ);
        double d2 = 0.8;
        if (distanceSq > d && distanceSq < 16.0) {
            d2 = 1.33;
        } else if (distanceSq < 225.0) {
            d2 = 0.6;
        }

        this.theEntity.getNavigator().tryMoveToEntityLiving(this.theVictim, d2);
        this.attackCountdown = Math.max(this.attackCountdown - 1, 0);
        if (distanceSq <= d && this.attackCountdown <= 0) {
            this.attackCountdown = 20;
            this.theEntity.attackEntityAsMob(this.theVictim);
        }
    }
}
