package com.cheatbreaker.client.event;

import net.minecraft.client.particle.EntityFX;

public class EntityFXDoubleEvent extends CancellableEvent {
   public EntityFX entityFX;
   public double doubleValue1;
   public double doubleValue2;
   public double doubleValue3;

   public EntityFX getEntityFX() {
      return this.entityFX;
   }

   public EntityFXDoubleEvent(EntityFX entityFX, double d, double d2, double d3) {
      this.entityFX = entityFX;
      this.doubleValue1 = d;
      this.doubleValue2 = d2;
      this.doubleValue3 = d3;
   }

   public double getDoubleValue3() {
      return this.doubleValue3;
   }

   public void setDoubleValue1(double d) {
      this.doubleValue1 = d;
   }

   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   public void setDoubleValue2(double d) {
      this.doubleValue2 = d;
   }

   public double getDoubleValue1() {
      return this.doubleValue1;
   }

   public void setDoubleValue3(double d) {
      this.doubleValue3 = d;
   }
}
