package com.cheatbreaker.client.event;

import net.minecraft.client.gui.ScaledResolution;

public class ScaledResolutionEvent6 extends Event {
   public ScaledResolution scaledResolution;

   public ScaledResolution getScaledResolution() {
      return this.scaledResolution;
   }

   public ScaledResolutionEvent6(ScaledResolution scaledResolution) {
      this.scaledResolution = scaledResolution;
   }
}
