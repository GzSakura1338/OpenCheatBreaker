package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIWatchClosest extends EntityAIBase {
    public EntityLiving theWatcher;
    public Entity closestEntity;
    public Class watchedClass;
    public int lookTime;
    public float maxDistanceForPlayer;
    public float chance;

    public EntityAIWatchClosest(EntityLiving entityLiving, Class clazz, float f) {
        this.theWatcher = entityLiving;
        this.watchedClass = clazz;
        this.maxDistanceForPlayer = f;
        this.chance = 0.02F;
        this.setMutexBits(2);
    }

    public EntityAIWatchClosest(EntityLiving entityLiving, Class clazz, float f, float f2) {
        this.theWatcher = entityLiving;
        this.watchedClass = clazz;
        this.maxDistanceForPlayer = f;
        this.chance = f2;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        if (this.theWatcher.getRNG().nextFloat() >= this.chance) {
            return false;
        }

        if (this.theWatcher.getAttackTarget() != null) {
            this.closestEntity = this.theWatcher.getAttackTarget();
        }

        if (this.watchedClass == EntityPlayer.class) {
            this.closestEntity = this.theWatcher.worldObj.getClosestPlayerToEntity(this.theWatcher, this.maxDistanceForPlayer);
        } else {
            this.closestEntity = this.theWatcher
                .worldObj
                .findNearestEntityWithinAABB(
                    this.watchedClass, this.theWatcher.boundingBox.expand(this.maxDistanceForPlayer, 3.0, this.maxDistanceForPlayer), this.theWatcher
                );
        }

        return this.closestEntity != null;
    }

    @Override
    public boolean continueExecuting() {
        return this.closestEntity.isEntityAlive()
            && !(this.theWatcher.getDistanceSqToEntity(this.closestEntity) > this.maxDistanceForPlayer * this.maxDistanceForPlayer)
            && this.lookTime > 0;
    }

    @Override
    public void startExecuting() {
        this.lookTime = 40 + this.theWatcher.getRNG().nextInt(40);
    }

    @Override
    public void resetTask() {
        this.closestEntity = null;
    }

    @Override
    public void updateTask() {
        this.theWatcher
            .getLookHelper()
            .setLookPosition(
                this.closestEntity.posX,
                this.closestEntity.posY + this.closestEntity.getEyeHeight(),
                this.closestEntity.posZ,
                10.0F,
                this.theWatcher.getVerticalFaceSpeed()
            );
        this.lookTime--;
    }
}
