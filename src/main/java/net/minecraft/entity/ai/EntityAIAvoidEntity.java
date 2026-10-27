package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Vec3;

public class EntityAIAvoidEntity<T extends Entity> extends EntityAIBase {
   public Predicate<Entity> canBeSeenSelector = new Predicate<Entity>() {
      public boolean apply(Entity entity) {
         return entity.isEntityAlive() && EntityAIAvoidEntity.this.theEntity.getEntitySenses().canSee(entity);
      }
   };
   public EntityCreature theEntity;
   public Class<T> classToAvoid;
   public Predicate<? super T> avoidTargetSelector;
   public T closestLivingEntity;
   public float avoidDistance;
   public PathEntity entityPathEntity;
   public double farSpeed;
   public double nearSpeed;
   public PathNavigate entityPathNavigate;

   public EntityAIAvoidEntity(EntityCreature theEntityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
      this(theEntityIn, classToAvoidIn, Predicates.alwaysTrue(), avoidDistanceIn, farSpeedIn, nearSpeedIn);
   }

   public EntityAIAvoidEntity(
      EntityCreature theEntityIn,
      Class<T> classToAvoidIn,
      Predicate<? super T> avoidTargetSelectorIn,
      float avoidDistanceIn,
      double farSpeedIn,
      double nearSpeedIn
   ) {
      this.theEntity = theEntityIn;
      this.classToAvoid = classToAvoidIn;
      this.avoidTargetSelector = avoidTargetSelectorIn;
      this.avoidDistance = avoidDistanceIn;
      this.farSpeed = farSpeedIn;
      this.nearSpeed = nearSpeedIn;
      this.entityPathNavigate = theEntityIn.getNavigator();
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      List entitiesWithinAABB = this.theEntity
         .worldObj
         .getEntitiesWithinAABB(
            this.classToAvoid,
            this.theEntity.getEntityBoundingBox().expand(this.avoidDistance, 3.0, this.avoidDistance),
            Predicates.and(new Predicate[]{EntitySelectors.NOT_SPECTATING, this.canBeSeenSelector, this.avoidTargetSelector})
         );
      if (entitiesWithinAABB.isEmpty()) {
         return false;
      }

      this.closestLivingEntity = (T)entitiesWithinAABB.get(0);
      Vec3 findRandomTargetBlockAwayFrom = RandomPositionGenerator.findRandomTargetBlockAwayFrom(
         this.theEntity, 16, 7, new Vec3(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ)
      );
      if (findRandomTargetBlockAwayFrom == null) {
         return false;
      }

      if (this.closestLivingEntity.getDistanceSq(findRandomTargetBlockAwayFrom.xCoord, findRandomTargetBlockAwayFrom.yCoord, findRandomTargetBlockAwayFrom.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(this.theEntity)) {
         return false;
      }

      this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(findRandomTargetBlockAwayFrom.xCoord, findRandomTargetBlockAwayFrom.yCoord, findRandomTargetBlockAwayFrom.zCoord);
      return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(findRandomTargetBlockAwayFrom);
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
