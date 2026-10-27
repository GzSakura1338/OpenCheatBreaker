package com.cheatbreaker.client.event;

import net.minecraft.entity.Entity;

public class EntityDoubleEvent extends Event {
   public Entity entity;
   public double x;
   public double y;
   public double z;

   public EntityDoubleEvent(Entity entity, double d, double d2, double d3) {
      this.entity = entity;
      this.x = d;
      this.y = d2;
      this.z = d3;
   }

   public double getZ() {
      return this.z;
   }

   public double getY() {
      return this.y;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public double getX() {
      return this.x;
   }
}
