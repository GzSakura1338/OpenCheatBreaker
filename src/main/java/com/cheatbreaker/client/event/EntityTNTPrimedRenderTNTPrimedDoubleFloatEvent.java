package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.entity.RenderTNTPrimed;
import net.minecraft.entity.item.EntityTNTPrimed;

public class EntityTNTPrimedRenderTNTPrimedDoubleFloatEvent extends Event {
   public RenderTNTPrimed renderTNTPrimed;
   public EntityTNTPrimed entityTNTPrimed;
   public double x;
   public double y;
   public double z;
   public float partialTicks;

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public EntityTNTPrimedRenderTNTPrimedDoubleFloatEvent(RenderTNTPrimed renderTNTPrimed, EntityTNTPrimed entityTNTPrimed, double d, double d2, double d3, float f) {
      this.renderTNTPrimed = renderTNTPrimed;
      this.entityTNTPrimed = entityTNTPrimed;
      this.x = d;
      this.y = d2;
      this.z = d3;
      this.partialTicks = f;
   }

   public RenderTNTPrimed getRenderTNTPrimed() {
      return this.renderTNTPrimed;
   }

   public double getZ() {
      return this.z;
   }

   public EntityTNTPrimed getEntityTNTPrimed() {
      return this.entityTNTPrimed;
   }
}
