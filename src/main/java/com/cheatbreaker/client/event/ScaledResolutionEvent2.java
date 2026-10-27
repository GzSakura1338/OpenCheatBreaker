package com.cheatbreaker.client.event;

import net.minecraft.client.gui.ScaledResolution;

public class ScaledResolutionEvent2 extends Event {
   public ScaledResolution scaledResolution;

   public ScaledResolutionEvent2(ScaledResolution scaledResolution) {
      this.scaledResolution = scaledResolution;
   }

   public ScaledResolution getScaledResolution() {
      return this.scaledResolution;
   }
}
