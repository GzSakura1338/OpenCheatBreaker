package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public abstract class EntityAITarget extends EntityAIBase {
   public EntityCreature taskOwner;
   public boolean shouldCheckSight;
   public boolean nearbyOnly;
   public int targetSearchStatus;
   public int targetSearchDelay;
   public int targetUnseenTicks;

   public EntityAITarget(EntityCreature creature, boolean checkSight) {
      this(creature, checkSight, false);
   }

   public EntityAITarget(EntityCreature creature, boolean checkSight, boolean onlyNearby) {
      this.taskOwner = creature;
      this.shouldCheckSight = checkSight;
      this.nearbyOnly = onlyNearby;
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

      Team team = this.taskOwner.getTeam();
      Team team2 = attackTarget.getTeam();
      if (team != null && team2 == team) {
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

      return !(attackTarget instanceof EntityPlayer) || !((EntityPlayer)attackTarget).capabilities.disableDamage;
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
      this.taskOwner.setAttackTarget((EntityLivingBase)null);
   }

   public static boolean isSuitableTarget(EntityLiving attacker, EntityLivingBase target, boolean includeInvincibles, boolean checkSight) {
      if (target == null) {
         return false;
      }

      if (target == attacker) {
         return false;
      }

      if (!target.isEntityAlive()) {
         return false;
      }

      if (!attacker.canAttackClass((Class<? extends EntityLivingBase>)target.getClass())) {
         return false;
      }

      Team team = attacker.getTeam();
      Team team2 = target.getTeam();
      if (team != null && team2 == team) {
         return false;
      }

      if (attacker instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)attacker).getOwnerId())) {
         if (target instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId().equals(((IEntityOwnable)target).getOwnerId())) {
            return false;
         }

         if (target == ((IEntityOwnable)attacker).getOwner()) {
            return false;
         }
      } else if (target instanceof EntityPlayer && !includeInvincibles && ((EntityPlayer)target).capabilities.disableDamage) {
         return false;
      }

      return !checkSight || attacker.getEntitySenses().canSee(target);
   }

   public boolean isSuitableTarget(EntityLivingBase target, boolean includeInvincibles) {
      if (!isSuitableTarget(this.taskOwner, target, includeInvincibles, this.shouldCheckSight)) {
         return false;
      }

      if (!this.taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(target))) {
         return false;
      }

      if (this.nearbyOnly) {
         if (--this.targetSearchDelay <= 0) {
            this.targetSearchStatus = 0;
         }

         if (this.targetSearchStatus == 0) {
            this.targetSearchStatus = this.canEasilyReach(target) ? 1 : 2;
         }

         if (this.targetSearchStatus == 2) {
            return false;
         }
      }

      return true;
   }

   public boolean canEasilyReach(EntityLivingBase target) {
      this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
      PathEntity pathToEntityLiving = this.taskOwner.getNavigator().getPathToEntityLiving(target);
      if (pathToEntityLiving == null) {
         return false;
      }

      PathPoint finalPathPoint = pathToEntityLiving.getFinalPathPoint();
      if (finalPathPoint == null) {
         return false;
      }

      int n = finalPathPoint.xCoord - MathHelper.floor_double(target.posX);
      int n2 = finalPathPoint.zCoord - MathHelper.floor_double(target.posZ);
      return n * n + n2 * n2 <= 2.25;
   }
}
