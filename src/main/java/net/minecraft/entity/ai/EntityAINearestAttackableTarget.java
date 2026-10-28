package net.minecraft.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;

public class EntityAINearestAttackableTarget extends EntityAITarget {
    public Class targetClass;
    public int targetChance;
    public EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    public IEntitySelector targetEntitySelector;
    public EntityLivingBase targetEntity;

    public EntityAINearestAttackableTarget(EntityCreature entityCreature, Class clazz, int n, boolean flag) {
        this(entityCreature, clazz, n, flag, false);
    }

    public EntityAINearestAttackableTarget(EntityCreature entityCreature, Class clazz, int n, boolean flag, boolean flag2) {
        this(entityCreature, clazz, n, flag, flag2, null);
    }

    public EntityAINearestAttackableTarget(EntityCreature entityCreature, Class clazz, int n, boolean flag, boolean flag2, final IEntitySelector entitySelector) {
        super(entityCreature, flag, flag2);
        this.targetClass = clazz;
        this.targetChance = n;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(entityCreature);
        this.setMutexBits(1);
        this.targetEntitySelector = new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity entity) {
                return entity instanceof EntityLivingBase
                    && (entitySelector == null || entitySelector.isEntityApplicable(entity))
                    && EntityAINearestAttackableTarget.this.isSuitableTarget((EntityLivingBase)entity, false);
            }
        };
    }

    @Override
    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        }

        double targetDistance = this.getTargetDistance();
        List selectEntitiesWithinAABB = this.taskOwner
            .worldObj
            .selectEntitiesWithinAABB(this.targetClass, this.taskOwner.boundingBox.expand(targetDistance, 4.0, targetDistance), this.targetEntitySelector);
        Collections.sort(selectEntitiesWithinAABB, this.theNearestAttackableTargetSorter);
        if (selectEntitiesWithinAABB.isEmpty()) {
            return false;
        }

        this.targetEntity = (EntityLivingBase)selectEntitiesWithinAABB.get(0);
        return true;
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }

    public static class Sorter implements Comparator {
        public Entity theEntity;

        public Sorter(Entity entity) {
            this.theEntity = entity;
        }

        public int compare(Entity entity, Entity entity2) {
            double distanceSqToEntity = this.theEntity.getDistanceSqToEntity(entity);
            double distanceSqToEntity2 = this.theEntity.getDistanceSqToEntity(entity2);
            return distanceSqToEntity < distanceSqToEntity2 ? -1 : (distanceSqToEntity > distanceSqToEntity2 ? 1 : 0);
        }

        @Override
        public int compare(Object obj, Object obj2) {
            return this.compare((Entity)obj, (Entity)obj2);
        }
    }
}
