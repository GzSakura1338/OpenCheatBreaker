package com.cheatbreaker.client.event;

import net.minecraft.client.gui.ScaledResolution;

public class ScaledResolutionIntEvent extends CancellableEvent {
   public ScaledResolution scaledResolution;
   public int intValue1;
   public int intValue2;

   public int getIntValue1() {
      return this.intValue1;
   }

   public ScaledResolution getScaledResolution() {
      return this.scaledResolution;
   }

   public ScaledResolutionIntEvent(ScaledResolution scaledResolution, int n, int n2) {
      this.scaledResolution = scaledResolution;
      this.intValue1 = n;
      this.intValue2 = n2;
   }

   public int getIntValue2() {
      return this.intValue2;
   }
}
