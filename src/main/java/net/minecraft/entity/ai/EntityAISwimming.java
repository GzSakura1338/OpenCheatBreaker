package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAISwimming extends EntityAIBase {
    public EntityLiving theEntity;

    public EntityAISwimming(EntityLiving entityLiving) {
        this.theEntity = entityLiving;
        this.setMutexBits(4);
        entityLiving.getNavigator().setCanSwim(true);
    }

    @Override
    public boolean shouldExecute() {
        return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
    }

    @Override
    public void updateTask() {
        if (this.theEntity.getRNG().nextFloat() < 0.8F) {
            this.theEntity.getJumpHelper().setJumping();
        }
    }
}
