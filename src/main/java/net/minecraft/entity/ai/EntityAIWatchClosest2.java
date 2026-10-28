package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIWatchClosest2 extends EntityAIWatchClosest {
    public EntityAIWatchClosest2(EntityLiving entityLiving, Class clazz, float f, float f2) {
        super(entityLiving, clazz, f, f2);
        this.setMutexBits(3);
    }
}
