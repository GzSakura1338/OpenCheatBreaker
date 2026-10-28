package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityTameable;

public class EntityAITargetNonTamed extends EntityAINearestAttackableTarget {
    public EntityTameable theTameable;

    public EntityAITargetNonTamed(EntityTameable entityTameable, Class clazz, int n, boolean flag) {
        super(entityTameable, clazz, n, flag);
        this.theTameable = entityTameable;
    }

    @Override
    public boolean shouldExecute() {
        return !this.theTameable.isTamed() && super.shouldExecute();
    }
}
