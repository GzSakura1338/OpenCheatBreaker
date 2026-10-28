package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

public class EntityAIAvoidEntity extends EntityAIBase {
    public IEntitySelector iEntitySelector = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity.isEntityAlive() && EntityAIAvoidEntity.this.theEntity.getEntitySenses().canSee(entity);
        }
    };
    public EntityCreature theEntity;
    public Class targetEntityClass;
    public float distanceFromEntity;
    public Entity closestLivingEntity;
    public double farSpeed;
    public PathEntity entityPathEntity;
    public double nearSpeed;
    public PathNavigate entityPathNavigate;

    public EntityAIAvoidEntity(EntityCreature entityCreature, Class clazz, float f, double d, double d2) {
        this.theEntity = entityCreature;
        this.targetEntityClass = clazz;
        this.distanceFromEntity = f;
        this.farSpeed = d;
        this.nearSpeed = d2;
        this.entityPathNavigate = entityCreature.getNavigator();
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.targetEntityClass == EntityPlayer.class) {
            if (this.theEntity instanceof EntityTameable && ((EntityTameable)this.theEntity).isTamed()) {
                return false;
            }

            this.closestLivingEntity = this.theEntity.worldObj.getClosestPlayerToEntity(this.theEntity, this.distanceFromEntity);
            if (this.closestLivingEntity == null) {
                return false;
            }
        } else {
            List selectEntitiesWithinAABB = this.theEntity
                .worldObj
                .selectEntitiesWithinAABB(
                    this.targetEntityClass, this.theEntity.boundingBox.expand(this.distanceFromEntity, 3.0, this.distanceFromEntity), this.iEntitySelector
                );
            if (selectEntitiesWithinAABB.isEmpty()) {
                return false;
            }

            this.closestLivingEntity = (Entity)selectEntitiesWithinAABB.get(0);
        }

        Vec3 findRandomTargetBlockAwayFrom = RandomPositionGenerator.findRandomTargetBlockAwayFrom(
            this.theEntity, 16, 7, Vec3.createVectorHelper(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ)
        );
        if (findRandomTargetBlockAwayFrom == null) {
            return false;
        }

        if (this.closestLivingEntity.getDistanceSq(findRandomTargetBlockAwayFrom.xCoord, findRandomTargetBlockAwayFrom.yCoord, findRandomTargetBlockAwayFrom.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(this.theEntity)) {
            return false;
        }

        this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(findRandomTargetBlockAwayFrom.xCoord, findRandomTargetBlockAwayFrom.yCoord, findRandomTargetBlockAwayFrom.zCoord);
        return this.entityPathEntity != null && this.entityPathEntity.isDestinationSame(findRandomTargetBlockAwayFrom);
    }

    @Override
    public boolean continueExecuting() {
        return !this.entityPathNavigate.noPath();
    }

    @Override
    public void startExecuting() {
        this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
    }

    @Override
    public void resetTask() {
        this.closestLivingEntity = null;
    }

    @Override
    public void updateTask() {
        if (this.theEntity.getDistanceSqToEntity(this.closestLivingEntity) < 49.0) {
            this.theEntity.getNavigator().setSpeed(this.nearSpeed);
        } else {
            this.theEntity.getNavigator().setSpeed(this.farSpeed);
        }
    }
}
