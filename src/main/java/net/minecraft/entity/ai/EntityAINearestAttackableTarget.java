package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class EntityAINearestAttackableTarget<T extends EntityLivingBase> extends EntityAITarget {
   public Class<T> targetClass;
   public int targetChance;
   public EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
   public Predicate<? super T> targetEntitySelector;
   public EntityLivingBase targetEntity;

   public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
      this(creature, classTarget, checkSight, false);
   }

   public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
      this(creature, classTarget, 10, checkSight, onlyNearby, (Predicate<? super T>)null);
   }

   public EntityAINearestAttackableTarget(
      EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, final Predicate<? super T> targetSelector
   ) {
      super(creature, checkSight, onlyNearby);
      this.targetClass = classTarget;
      this.targetChance = chance;
      this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(creature);
      this.setMutexBits(1);
      this.targetEntitySelector = new Predicate<T>() {
         public boolean apply(T value) {
            if (targetSelector != null && !targetSelector.apply(value)) {
               return false;
            }

            if (value instanceof EntityPlayer) {
               double targetDistance = EntityAINearestAttackableTarget.this.getTargetDistance();
               if (value.isSneaking()) {
                  targetDistance *= 0.8F;
               }

               if (value.isInvisible()) {
                  float armorVisibility = ((EntityPlayer)value).getArmorVisibility();
                  if (armorVisibility < 0.1F) {
                     armorVisibility = 0.1F;
                  }

                  targetDistance *= 0.7F * armorVisibility;
               }

               if (value.getDistanceToEntity(EntityAINearestAttackableTarget.this.taskOwner) > targetDistance) {
                  return false;
               }
            }

            return EntityAINearestAttackableTarget.this.isSuitableTarget(value, false);
         }
      };
   }

   @Override
   public boolean shouldExecute() {
      if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      }

      double targetDistance = this.getTargetDistance();
      List entitiesWithinAABB = this.taskOwner
         .worldObj
         .getEntitiesWithinAABB(
            this.targetClass,
            this.taskOwner.getEntityBoundingBox().expand(targetDistance, 4.0, targetDistance),
            Predicates.and(this.targetEntitySelector, EntitySelectors.NOT_SPECTATING)
         );
      Collections.sort(entitiesWithinAABB, this.theNearestAttackableTargetSorter);
      if (entitiesWithinAABB.isEmpty()) {
         return false;
      }

      this.targetEntity = (EntityLivingBase)entitiesWithinAABB.get(0);
      return true;
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.targetEntity);
      super.startExecuting();
   }

   public static class Sorter implements Comparator<Entity> {
      public Entity theEntity;

      public Sorter(Entity theEntityIn) {
         this.theEntity = theEntityIn;
      }

      public int compare(Entity entity, Entity entity2) {
         double distanceSqToEntity = this.theEntity.getDistanceSqToEntity(entity);
         double distanceSqToEntity2 = this.theEntity.getDistanceSqToEntity(entity2);
         return distanceSqToEntity < distanceSqToEntity2 ? -1 : (distanceSqToEntity > distanceSqToEntity2 ? 1 : 0);
      }
   }
}
