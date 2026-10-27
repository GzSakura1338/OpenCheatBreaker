package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitySenses {
   public List<Entity> seenEntities = Lists.newArrayList();
   public List<Entity> unseenEntities = Lists.newArrayList();
   public EntityLiving entityObj;

   public EntitySenses(EntityLiving entityObjIn) {
      this.entityObj = entityObjIn;
   }

   public void clearSensingCache() {
      this.seenEntities.clear();
      this.unseenEntities.clear();
   }

   public boolean canSee(Entity entityIn) {
      if (this.seenEntities.contains(entityIn)) {
         return true;
      }

      if (this.unseenEntities.contains(entityIn)) {
         return false;
      }

      this.entityObj.worldObj.theProfiler.startSection("canSee");
      boolean canEntityBeSeen = this.entityObj.canEntityBeSeen(entityIn);
      this.entityObj.worldObj.theProfiler.endSection();
      if (canEntityBeSeen) {
         this.seenEntities.add(entityIn);
      } else {
         this.unseenEntities.add(entityIn);
      }

      return canEntityBeSeen;
   }
}
