package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAIFindEntityNearestPlayer extends EntityAIBase {
   public static Logger LOGGER = LogManager.getLogger();
   public EntityLiving entityLiving;
   public Predicate<Entity> predicate;
   public EntityAINearestAttackableTarget.Sorter sorter;
   public EntityLivingBase entityTarget;

   public EntityAIFindEntityNearestPlayer(EntityLiving entityLivingIn) {
      this.entityLiving = entityLivingIn;
      if (entityLivingIn instanceof EntityCreature) {
         LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
      }

      this.predicate = new Predicate<Entity>() {
         public boolean apply(Entity entity) {
            if (!(entity instanceof EntityPlayer)) {
               return false;
            }

            if (((EntityPlayer)entity).capabilities.disableDamage) {
               return false;
            }

            double maxTargetRange = EntityAIFindEntityNearestPlayer.this.maxTargetRange();
            if (entity.isSneaking()) {
               maxTargetRange *= 0.8F;
            }

            if (entity.isInvisible()) {
               float armorVisibility = ((EntityPlayer)entity).getArmorVisibility();
               if (armorVisibility < 0.1F) {
                  armorVisibility = 0.1F;
               }

               maxTargetRange *= 0.7F * armorVisibility;
            }

            return entity.getDistanceToEntity(EntityAIFindEntityNearestPlayer.this.entityLiving) > maxTargetRange
               ? false
               : EntityAITarget.isSuitableTarget(EntityAIFindEntityNearestPlayer.this.entityLiving, (EntityLivingBase)entity, false, true);
         }
      };
      this.sorter = new EntityAINearestAttackableTarget.Sorter(entityLivingIn);
   }

   @Override
   public boolean shouldExecute() {
      double maxTargetRange = this.maxTargetRange();
      List entitiesWithinAABB = this.entityLiving
         .worldObj
         .getEntitiesWithinAABB(EntityPlayer.class, this.entityLiving.getEntityBoundingBox().expand(maxTargetRange, 4.0, maxTargetRange), this.predicate);
      Collections.sort(entitiesWithinAABB, this.sorter);
      if (entitiesWithinAABB.isEmpty()) {
         return false;
      }

      this.entityTarget = (EntityLivingBase)entitiesWithinAABB.get(0);
      return true;
   }

   @Override
   public boolean continueExecuting() {
      EntityLivingBase attackTarget = this.entityLiving.getAttackTarget();
      if (attackTarget == null) {
         return false;
      }

      if (!attackTarget.isEntityAlive()) {
         return false;
      }

      if (attackTarget instanceof EntityPlayer && ((EntityPlayer)attackTarget).capabilities.disableDamage) {
         return false;
      }

      Team team = this.entityLiving.getTeam();
      Team team2 = attackTarget.getTeam();
      if (team != null && team2 == team) {
         return false;
      }

      double maxTargetRange = this.maxTargetRange();
      return this.entityLiving.getDistanceSqToEntity(attackTarget) > maxTargetRange * maxTargetRange
         ? false
         : !(attackTarget instanceof EntityPlayerMP) || !((EntityPlayerMP)attackTarget).theItemInWorldManager.isCreative();
   }

   @Override
   public void startExecuting() {
      this.entityLiving.setAttackTarget(this.entityTarget);
      super.startExecuting();
   }

   @Override
   public void resetTask() {
      this.entityLiving.setAttackTarget((EntityLivingBase)null);
      super.startExecuting();
   }

   public double maxTargetRange() {
      IAttributeInstance entityAttribute = this.entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
      return entityAttribute == null ? 16.0 : entityAttribute.getAttributeValue();
   }
}
