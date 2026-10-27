package com.cheatbreaker.client.event;

import net.minecraft.client.gui.ScaledResolution;

public class ScaledResolutionEvent3 extends Event {
   public ScaledResolution scaledResolution;

   public ScaledResolutionEvent3(ScaledResolution scaledResolution) {
      this.scaledResolution = scaledResolution;
   }

   public ScaledResolution getScaledResolution() {
      return this.scaledResolution;
   }
}
