package com.cheatbreaker.client.event;

import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseDoubleEvent4 extends CancellableEvent {
   public EntityLivingBase entityLivingBase;
   public double x;
   public double y;
   public double z;
   public double doubleValue4;

   public double getDoubleValue4() {
      return this.doubleValue4;
   }

   public double getY() {
      return this.y;
   }

   public void setX(double d) {
      this.x = d;
   }

   public EntityLivingBaseDoubleEvent4(EntityLivingBase entityLivingBase, double d, double d2, double d3, double d4) {
      this.entityLivingBase = entityLivingBase;
      this.x = d;
      this.y = d2;
      this.z = d3;
      this.doubleValue4 = d4;
   }

   public void setZ(double d) {
      this.z = d;
   }

   public EntityLivingBase getEntityLivingBase() {
      return this.entityLivingBase;
   }

   public void setDoubleValue4(double d) {
      this.doubleValue4 = d;
   }

   public void setY(double d) {
      this.y = d;
   }

   public double getX() {
      return this.x;
   }

   public double getZ() {
      return this.z;
   }
}
