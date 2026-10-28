package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;

public class EntityAIRestrictSun extends EntityAIBase {
    public EntityCreature theEntity;

    public EntityAIRestrictSun(EntityCreature entityCreature) {
        this.theEntity = entityCreature;
    }

    @Override
    public boolean shouldExecute() {
        return this.theEntity.worldObj.isDaytime();
    }

    @Override
    public void startExecuting() {
        this.theEntity.getNavigator().setAvoidSun(true);
    }

    @Override
    public void resetTask() {
        this.theEntity.getNavigator().setAvoidSun(false);
    }
}
