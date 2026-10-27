package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.RenderGlobal;

public class RenderGlobalFloatEvent extends Event {
   public RenderGlobal renderGlobal;
   public float floatValue;

   public RenderGlobalFloatEvent(RenderGlobal renderGlobal, float f) {
      this.renderGlobal = renderGlobal;
      this.floatValue = f;
   }

   public float getFloatValue() {
      return this.floatValue;
   }
}
