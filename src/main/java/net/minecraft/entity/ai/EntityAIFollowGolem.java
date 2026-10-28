package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;

public class EntityAIFollowGolem extends EntityAIBase {
    public EntityVillager theVillager;
    public EntityIronGolem theGolem;
    public int takeGolemRoseTick;
    public boolean tookGolemRose;

    public EntityAIFollowGolem(EntityVillager entityVillager) {
        this.theVillager = entityVillager;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.theVillager.getGrowingAge() >= 0) {
            return false;
        }

        if (!this.theVillager.worldObj.isDaytime()) {
            return false;
        }

        List entitiesWithinAABB = this.theVillager.worldObj.getEntitiesWithinAABB(EntityIronGolem.class, this.theVillager.boundingBox.expand(6.0, 2.0, 6.0));
        if (entitiesWithinAABB.isEmpty()) {
            return false;
        }

        for (EntityIronGolem entityIronGolem : (Iterable<EntityIronGolem>)entitiesWithinAABB) {
            if (entityIronGolem.getHoldRoseTick() > 0) {
                this.theGolem = entityIronGolem;
                break;
            }
        }

        return this.theGolem != null;
    }

    @Override
    public boolean continueExecuting() {
        return this.theGolem.getHoldRoseTick() > 0;
    }

    @Override
    public void startExecuting() {
        this.takeGolemRoseTick = this.theVillager.getRNG().nextInt(320);
        this.tookGolemRose = false;
        this.theGolem.getNavigator().clearPathEntity();
    }

    @Override
    public void resetTask() {
        this.theGolem = null;
        this.theVillager.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        this.theVillager.getLookHelper().setLookPositionWithEntity(this.theGolem, 30.0F, 30.0F);
        if (this.theGolem.getHoldRoseTick() == this.takeGolemRoseTick) {
            this.theVillager.getNavigator().tryMoveToEntityLiving(this.theGolem, 0.5);
            this.tookGolemRose = true;
        }

        if (this.tookGolemRose && this.theVillager.getDistanceSqToEntity(this.theGolem) < 4.0) {
            this.theGolem.setHoldingRose(false);
            this.theVillager.getNavigator().clearPathEntity();
        }
    }
}
