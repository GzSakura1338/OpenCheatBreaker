package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent extends CancellableEvent {
   public RendererLivingEntity rendererLivingEntity;
   public EntityLivingBase entityLivingBase;
   public double doubleValue1;
   public double doubleValue2;
   public double doubleValue3;
   public float floatValue1;
   public float floatValue2;
   public int intValue;
   public float floatValue3;
   public float floatValue4;
   public float floatValue5;
   public float floatValue6;
   public float floatValue7;
   public float floatValue8;
   public float floatValue9;

   public EntityLivingBase getEntityLivingBase() {
      return this.entityLivingBase;
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public float getFloatValue9() {
      return this.floatValue9;
   }

   public double getDoubleValue3() {
      return this.doubleValue3;
   }

   public EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent(
      RendererLivingEntity rendererLivingEntity,
      EntityLivingBase entityLivingBase,
      double d,
      double d2,
      double d3,
      float f,
      float f2,
      int n,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9
   ) {
      this.rendererLivingEntity = rendererLivingEntity;
      this.entityLivingBase = entityLivingBase;
      this.doubleValue1 = d;
      this.doubleValue2 = d2;
      this.doubleValue3 = d3;
      this.floatValue1 = f;
      this.floatValue2 = f2;
      this.intValue = n;
      this.floatValue3 = f3;
      this.floatValue4 = f4;
      this.floatValue5 = f5;
      this.floatValue6 = f6;
      this.floatValue7 = f7;
      this.floatValue8 = f8;
      this.floatValue9 = f9;
   }

   public float getFloatValue6() {
      return this.floatValue6;
   }

   public float getFloatValue7() {
      return this.floatValue7;
   }

   public double getDoubleValue1() {
      return this.doubleValue1;
   }

   public float getFloatValue5() {
      return this.floatValue5;
   }

   public RendererLivingEntity getRendererLivingEntity() {
      return this.rendererLivingEntity;
   }

   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public float getFloatValue1() {
      return this.floatValue1;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public float getFloatValue8() {
      return this.floatValue8;
   }

   public float getFloatValue4() {
      return this.floatValue4;
   }
}
