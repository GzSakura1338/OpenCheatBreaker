package com.cheatbreaker.client.event;

import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseFloatEvent1 extends CancellableEvent {
   public float partialTicks;
   public EntityLivingBase entityLivingBase;
   public float floatValue2;
   public float floatValue3;

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public EntityLivingBaseFloatEvent1(float f, EntityLivingBase entityLivingBase, float f2, float f3) {
      this.partialTicks = f;
      this.entityLivingBase = entityLivingBase;
      this.floatValue2 = f2;
      this.floatValue3 = f3;
   }

   public void setFloatValue3(float f) {
      this.floatValue3 = f;
   }

   public EntityLivingBase getEntityLivingBase() {
      return this.entityLivingBase;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public void setFloatValue2(float f) {
      this.floatValue2 = f;
   }
}
