package com.cheatbreaker.client.event;

import net.minecraft.entity.Entity;

public class EntityDoubleFloatEvent extends CancellableEvent {
   public Entity entity;
   public double doubleValue1;
   public double doubleValue2;
   public double doubleValue3;
   public float floatValue1;
   public float floatValue2;

   public double getDoubleValue1() {
      return this.doubleValue1;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public EntityDoubleFloatEvent(Entity entity, double d, double d2, double d3, float f, float f2) {
      this.entity = entity;
      this.doubleValue1 = d;
      this.doubleValue2 = d2;
      this.doubleValue3 = d3;
      this.floatValue1 = f;
      this.floatValue2 = f2;
   }

   public float getFloatValue1() {
      return this.floatValue1;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   public double getDoubleValue3() {
      return this.doubleValue3;
   }
}
