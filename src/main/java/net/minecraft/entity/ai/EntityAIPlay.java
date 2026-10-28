package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.Vec3;

public class EntityAIPlay extends EntityAIBase {
    public EntityVillager villagerObj;
    public EntityLivingBase targetVillager;
    public double speed;
    public int playTime;

    public EntityAIPlay(EntityVillager entityVillager, double d) {
        this.villagerObj = entityVillager;
        this.speed = d;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.villagerObj.getGrowingAge() >= 0) {
            return false;
        }

        if (this.villagerObj.getRNG().nextInt(400) != 0) {
            return false;
        }

        List entitiesWithinAABB = this.villagerObj.worldObj.getEntitiesWithinAABB(EntityVillager.class, this.villagerObj.boundingBox.expand(6.0, 3.0, 6.0));
        double d = Double.MAX_VALUE;

        for (EntityVillager entityVillager : (Iterable<EntityVillager>)entitiesWithinAABB) {
            if (entityVillager != this.villagerObj && !entityVillager.isPlaying() && entityVillager.getGrowingAge() < 0) {
                double distanceSqToEntity = entityVillager.getDistanceSqToEntity(this.villagerObj);
                if (distanceSqToEntity <= d) {
                    d = distanceSqToEntity;
                    this.targetVillager = entityVillager;
                }
            }
        }

        if (this.targetVillager == null) {
            Vec3 findRandomTarget = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);
            return findRandomTarget != null;
        } else {
            return true;
        }
    }

    @Override
    public boolean continueExecuting() {
        return this.playTime > 0;
    }

    @Override
    public void startExecuting() {
        if (this.targetVillager != null) {
            this.villagerObj.setPlaying(true);
        }

        this.playTime = 1000;
    }

    @Override
    public void resetTask() {
        this.villagerObj.setPlaying(false);
        this.targetVillager = null;
    }

    @Override
    public void updateTask() {
        this.playTime--;
        if (this.targetVillager != null) {
            if (this.villagerObj.getDistanceSqToEntity(this.targetVillager) > 4.0) {
                this.villagerObj.getNavigator().tryMoveToEntityLiving(this.targetVillager, this.speed);
            }
        } else if (this.villagerObj.getNavigator().noPath()) {
            Vec3 findRandomTarget = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);
            if (findRandomTarget == null) {
                return;
            }

            this.villagerObj.getNavigator().tryMoveToXYZ(findRandomTarget.xCoord, findRandomTarget.yCoord, findRandomTarget.zCoord, this.speed);
        }
    }
}
