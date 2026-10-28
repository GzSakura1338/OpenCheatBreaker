package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAISit extends EntityAIBase {
    public EntityTameable theEntity;
    public boolean isSitting;

    public EntityAISit(EntityTameable entityTameable) {
        this.theEntity = entityTameable;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.theEntity.isTamed()) {
            return false;
        }

        if (this.theEntity.isInWater()) {
            return false;
        }

        if (!this.theEntity.onGround) {
            return false;
        }

        EntityLivingBase owner = this.theEntity.getOwner();
        return owner == null || (!(this.theEntity.getDistanceSqToEntity(owner) < 144.0) || owner.getAITarget() == null) && this.isSitting;
    }

    @Override
    public void startExecuting() {
        this.theEntity.getNavigator().clearPathEntity();
        this.theEntity.setSitting(true);
    }

    @Override
    public void resetTask() {
        this.theEntity.setSitting(false);
    }

    public void setSitting(boolean flag) {
        this.isSitting = flag;
    }
}
