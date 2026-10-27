package com.cheatbreaker.client.event;

import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseDoubleEvent1 extends CancellableEvent {
   public EntityLivingBase entityLivingBase;
   public double x;
   public double y;
   public double z;

   public double getY() {
      return this.y;
   }

   public double getX() {
      return this.x;
   }

   public EntityLivingBase getEntityLivingBase() {
      return this.entityLivingBase;
   }

   public EntityLivingBaseDoubleEvent1(EntityLivingBase entityLivingBase, double d, double d2, double d3) {
      this.entityLivingBase = entityLivingBase;
      this.x = d;
      this.y = d2;
      this.z = d3;
   }

   public double getZ() {
      return this.z;
   }
}
