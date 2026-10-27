package com.cheatbreaker.client.event;

import net.minecraft.client.gui.ScaledResolution;

public class ScaledResolutionEvent8 extends Event {
   public ScaledResolution scaledResolution;

   public ScaledResolution getScaledResolution() {
      return this.scaledResolution;
   }

   public ScaledResolutionEvent8(ScaledResolution scaledResolution) {
      this.scaledResolution = scaledResolution;
   }
}
