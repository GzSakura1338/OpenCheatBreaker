package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
    public EntityTameable theEntityTameable;
    public EntityLivingBase theTarget;
    public int lastAttackTime;

    public EntityAIOwnerHurtTarget(EntityTameable entityTameable) {
        super(entityTameable, false);
        this.theEntityTameable = entityTameable;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.theEntityTameable.isTamed()) {
            return false;
        }

        EntityLivingBase owner = this.theEntityTameable.getOwner();
        if (owner == null) {
            return false;
        }

        this.theTarget = owner.getLastAttacker();
        int lastAttackerTime = owner.getLastAttackerTime();
        return lastAttackerTime != this.lastAttackTime && this.isSuitableTarget(this.theTarget, false) && this.theEntityTameable.canAttackWithOwner(this.theTarget, owner);
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.theTarget);
        EntityLivingBase owner = this.theEntityTameable.getOwner();
        if (owner != null) {
            this.lastAttackTime = owner.getLastAttackerTime();
        }

        super.startExecuting();
    }
}
