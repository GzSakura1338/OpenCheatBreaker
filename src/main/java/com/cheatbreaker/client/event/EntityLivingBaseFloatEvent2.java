package com.cheatbreaker.client.event;

import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseFloatEvent2 extends Event {
   public float floatValue1;
   public float floatValue2;
   public float floatValue3;
   public float floatValue4;
   public EntityLivingBase entityLivingBase;
   public float brightness;

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public float getFloatValue4() {
      return this.floatValue4;
   }

   public float getBrightness() {
      return this.brightness;
   }

   public void setBrightness(float f) {
      this.brightness = f;
   }

   public EntityLivingBase getEntityLivingBase() {
      return this.entityLivingBase;
   }

   public void setFloatValue4(float f) {
      this.floatValue4 = f;
   }

   public float getFloatValue1() {
      return this.floatValue1;
   }

   public void setFloatValue2(float f) {
      this.floatValue2 = f;
   }

   public void setFloatValue3(float f) {
      this.floatValue3 = f;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public EntityLivingBaseFloatEvent2(float f, float f2, float f3, float f4, EntityLivingBase entityLivingBase, float f5) {
      this.floatValue1 = f;
      this.floatValue2 = f2;
      this.floatValue3 = f3;
      this.floatValue4 = f4;
      this.entityLivingBase = entityLivingBase;
      this.brightness = f5;
   }

   public void setFloatValue1(float f) {
      this.floatValue1 = f;
   }
}
