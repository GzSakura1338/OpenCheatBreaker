package com.cheatbreaker.client.event;

import net.minecraft.entity.item.EntityItem;

public class EntityItemDoubleEvent extends CancellableEvent {
   public EntityItem entityItem;
   public double x;
   public double y;
   public double z;

   public EntityItem getEntityItem() {
      return this.entityItem;
   }

   public EntityItemDoubleEvent(EntityItem entityItem, double d, double d2, double d3) {
      this.entityItem = entityItem;
      this.x = d;
      this.y = d2;
      this.z = d3;
   }

   public double getZ() {
      return this.z;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }
}
