package com.cheatbreaker.client.event;

import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseFloatEvent3 extends CancellableEvent {
   public float floatValue1;
   public EntityLivingBase entityLivingBase;
   public float partialTickTime;

   public float getPartialTickTime() {
      return this.partialTickTime;
   }

   public void setFloatValue1(float f) {
      this.floatValue1 = f;
   }

   public void setEntityLivingBase(EntityLivingBase entityLivingBase) {
      this.entityLivingBase = entityLivingBase;
   }

   public float getFloatValue1() {
      return this.floatValue1;
   }

   public void setPartialTickTime(float f) {
      this.partialTickTime = f;
   }

   public EntityLivingBase getEntityLivingBase() {
      return this.entityLivingBase;
   }

   public EntityLivingBaseFloatEvent3(float f, EntityLivingBase entityLivingBase, float f2) {
      this.floatValue1 = f;
      this.entityLivingBase = entityLivingBase;
      this.partialTickTime = f2;
   }
}
