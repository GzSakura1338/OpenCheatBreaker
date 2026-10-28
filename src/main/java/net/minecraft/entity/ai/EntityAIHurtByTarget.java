package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget {
    public boolean entityCallsForHelp;
    public int revengeTimerOld;

    public EntityAIHurtByTarget(EntityCreature entityCreature, boolean flag) {
        super(entityCreature, false);
        this.entityCallsForHelp = flag;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        int revengeTimer = this.taskOwner.getRevengeTimer();
        return revengeTimer != this.revengeTimerOld && this.isSuitableTarget(this.taskOwner.getAITarget(), false);
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
        this.revengeTimerOld = this.taskOwner.getRevengeTimer();
        if (this.entityCallsForHelp) {
            double targetDistance = this.getTargetDistance();

            for (EntityCreature entityCreature : (Iterable<EntityCreature>)(this.taskOwner
                .worldObj
                .getEntitiesWithinAABB(
                    this.taskOwner.getClass(),
                    AxisAlignedBB.getBoundingBox(
                            this.taskOwner.posX,
                            this.taskOwner.posY,
                            this.taskOwner.posZ,
                            this.taskOwner.posX + 1.0,
                            this.taskOwner.posY + 1.0,
                            this.taskOwner.posZ + 1.0
                        )
                        .expand(targetDistance, 10.0, targetDistance)
                ))) {
                if (this.taskOwner != entityCreature && entityCreature.getAttackTarget() == null && !entityCreature.isOnSameTeam(this.taskOwner.getAITarget())) {
                    entityCreature.setAttackTarget(this.taskOwner.getAITarget());
                }
            }
        }

        super.startExecuting();
    }
}
