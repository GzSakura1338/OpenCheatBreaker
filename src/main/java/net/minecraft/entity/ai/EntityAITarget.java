package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public abstract class EntityAITarget extends EntityAIBase {
    public EntityCreature taskOwner;
    public boolean shouldCheckSight;
    public boolean nearbyOnly;
    public int targetSearchStatus;
    public int targetSearchDelay;
    public int targetUnseenTicks;

    public EntityAITarget(EntityCreature entityCreature, boolean flag) {
        this(entityCreature, flag, false);
    }

    public EntityAITarget(EntityCreature entityCreature, boolean flag, boolean flag2) {
        this.taskOwner = entityCreature;
        this.shouldCheckSight = flag;
        this.nearbyOnly = flag2;
    }

    @Override
    public boolean continueExecuting() {
        EntityLivingBase attackTarget = this.taskOwner.getAttackTarget();
        if (attackTarget == null) {
            return false;
        }

        if (!attackTarget.isEntityAlive()) {
            return false;
        }

        double targetDistance = this.getTargetDistance();
        if (this.taskOwner.getDistanceSqToEntity(attackTarget) > targetDistance * targetDistance) {
            return false;
        }

        if (this.shouldCheckSight) {
            if (this.taskOwner.getEntitySenses().canSee(attackTarget)) {
                this.targetUnseenTicks = 0;
            } else if (++this.targetUnseenTicks > 60) {
                return false;
            }
        }

        return !(attackTarget instanceof EntityPlayerMP) || !((EntityPlayerMP)attackTarget).theItemInWorldManager.isCreative();
    }

    public double getTargetDistance() {
        IAttributeInstance entityAttribute = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.followRange);
        return entityAttribute == null ? 16.0 : entityAttribute.getAttributeValue();
    }

    @Override
    public void startExecuting() {
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.targetUnseenTicks = 0;
    }

    @Override
    public void resetTask() {
        this.taskOwner.setAttackTarget(null);
    }

    public boolean isSuitableTarget(EntityLivingBase entityLivingBase, boolean flag) {
        if (entityLivingBase == null) {
            return false;
        }

        if (entityLivingBase == this.taskOwner) {
            return false;
        }

        if (!entityLivingBase.isEntityAlive()) {
            return false;
        }

        if (!this.taskOwner.canAttackClass(entityLivingBase.getClass())) {
            return false;
        }

        if (this.taskOwner instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)this.taskOwner).getWatchableObjectString())) {
            if (entityLivingBase instanceof IEntityOwnable
                && ((IEntityOwnable)this.taskOwner).getWatchableObjectString().equals(((IEntityOwnable)entityLivingBase).getWatchableObjectString())) {
                return false;
            }

            if (entityLivingBase == ((IEntityOwnable)this.taskOwner).getOwner()) {
                return false;
            }
        } else if (entityLivingBase instanceof EntityPlayer && !flag && ((EntityPlayer)entityLivingBase).capabilities.disableDamage) {
            return false;
        }

        if (!this.taskOwner.isWithinHomeDistance(MathHelper.floor_double(entityLivingBase.posX), MathHelper.floor_double(entityLivingBase.posY), MathHelper.floor_double(entityLivingBase.posZ))) {
            return false;
        }

        if (this.shouldCheckSight && !this.taskOwner.getEntitySenses().canSee(entityLivingBase)) {
            return false;
        }

        if (this.nearbyOnly) {
            if (--this.targetSearchDelay <= 0) {
                this.targetSearchStatus = 0;
            }

            if (this.targetSearchStatus == 0) {
                this.targetSearchStatus = this.canEasilyReach(entityLivingBase) ? 1 : 2;
            }

            return this.targetSearchStatus != 2;
        } else {
            return true;
        }
    }

    public boolean canEasilyReach(EntityLivingBase entityLivingBase) {
        this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
        PathEntity pathToEntityLiving = this.taskOwner.getNavigator().getPathToEntityLiving(entityLivingBase);
        if (pathToEntityLiving == null) {
            return false;
        }

        PathPoint finalPathPoint = pathToEntityLiving.getFinalPathPoint();
        if (finalPathPoint == null) {
            return false;
        }

        int n = finalPathPoint.xCoord - MathHelper.floor_double(entityLivingBase.posX);
        int n2 = finalPathPoint.zCoord - MathHelper.floor_double(entityLivingBase.posZ);
        return n * n + n2 * n2 <= 2.25;
    }
}
