package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class EntityPlayerRenderPlayerDoubleFloatEvent extends CancellableEvent {
   public EntityPlayer entityPlayer;
   public RenderPlayer renderPlayer;
   public float floatValue;
   public double doubleValue1;
   public double doubleValue2;
   public double doubleValue3;

   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   public double getDoubleValue1() {
      return this.doubleValue1;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public EntityPlayer getEntityPlayer() {
      return this.entityPlayer;
   }

   public RenderPlayer getRenderPlayer() {
      return this.renderPlayer;
   }

   public EntityPlayerRenderPlayerDoubleFloatEvent(EntityPlayer entityPlayer, RenderPlayer renderPlayer, float f, double d, double d2, double d3) {
      this.entityPlayer = entityPlayer;
      this.renderPlayer = renderPlayer;
      this.floatValue = f;
      this.doubleValue1 = d;
      this.doubleValue2 = d2;
      this.doubleValue3 = d3;
   }

   public double getDoubleValue3() {
      return this.doubleValue3;
   }
}
