package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAIFindEntityNearest extends EntityAIBase {
   public static Logger LOGGER = LogManager.getLogger();
   public EntityLiving mob;
   public Class<? extends EntityLivingBase> targetClass;
   public Predicate<EntityLivingBase> targetPredicate;
   public EntityLivingBase target;
   public EntityAINearestAttackableTarget.Sorter sorter;

   public EntityAIFindEntityNearest(EntityLiving mobIn, Class<? extends EntityLivingBase> clazz) {
      this.mob = mobIn;
      this.targetClass = clazz;
      if (mobIn instanceof EntityCreature) {
         LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
      }

      this.targetPredicate = new Predicate<EntityLivingBase>() {
         public boolean apply(EntityLivingBase entityLivingBase) {
            double followRange = EntityAIFindEntityNearest.this.getFollowRange();
            if (entityLivingBase.isSneaking()) {
               followRange *= 0.8F;
            }

            return entityLivingBase.isInvisible()
               ? false
               : (
                  entityLivingBase.getDistanceToEntity(EntityAIFindEntityNearest.this.mob) > followRange
                     ? false
                     : EntityAITarget.isSuitableTarget(EntityAIFindEntityNearest.this.mob, entityLivingBase, false, true)
               );
         }
      };
      this.sorter = new EntityAINearestAttackableTarget.Sorter(mobIn);
   }

   @Override
   public boolean shouldExecute() {
      double followRange = this.getFollowRange();
      List entitiesWithinAABB = this.mob.worldObj.getEntitiesWithinAABB(this.targetClass, this.mob.getEntityBoundingBox().expand(followRange, 4.0, followRange), this.targetPredicate);
      Collections.sort(entitiesWithinAABB, this.sorter);
      if (entitiesWithinAABB.isEmpty()) {
         return false;
      }

      this.target = (EntityLivingBase)entitiesWithinAABB.get(0);
      return true;
   }

   @Override
   public boolean continueExecuting() {
      EntityLivingBase attackTarget = this.mob.getAttackTarget();
      if (attackTarget == null) {
         return false;
      }

      if (!attackTarget.isEntityAlive()) {
         return false;
      }

      double followRange = this.getFollowRange();
      return this.mob.getDistanceSqToEntity(attackTarget) > followRange * followRange
         ? false
         : !(attackTarget instanceof EntityPlayerMP) || !((EntityPlayerMP)attackTarget).theItemInWorldManager.isCreative();
   }

   @Override
   public void startExecuting() {
      this.mob.setAttackTarget(this.target);
      super.startExecuting();
   }

   @Override
   public void resetTask() {
      this.mob.setAttackTarget((EntityLivingBase)null);
      super.startExecuting();
   }

   public double getFollowRange() {
      IAttributeInstance entityAttribute = this.mob.getEntityAttribute(SharedMonsterAttributes.followRange);
      return entityAttribute == null ? 16.0 : entityAttribute.getAttributeValue();
   }
}
