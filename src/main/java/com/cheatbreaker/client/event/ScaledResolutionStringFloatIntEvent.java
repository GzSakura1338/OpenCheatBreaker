package com.cheatbreaker.client.event;

import net.minecraft.client.gui.ScaledResolution;

public class ScaledResolutionStringFloatIntEvent extends CancellableEvent {
   public ScaledResolution scaledResolution;
   public String displayedTitle;
   public String displayedSubTitle;
   public float partialTicks;
   public int titlesTimer;
   public int titleDisplayTime;
   public int titleFadeOut;
   public int titleFadeIn;

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public int getTitleDisplayTime() {
      return this.titleDisplayTime;
   }

   public int getTitleFadeOut() {
      return this.titleFadeOut;
   }

   public ScaledResolution getScaledResolution() {
      return this.scaledResolution;
   }

   public String getDisplayedSubTitle() {
      return this.displayedSubTitle;
   }

   public String getDisplayedTitle() {
      return this.displayedTitle;
   }

   public int getTitlesTimer() {
      return this.titlesTimer;
   }

   public ScaledResolutionStringFloatIntEvent(ScaledResolution scaledResolution, String str, String str2, float f, int n, int n2, int n3, int n4) {
      this.scaledResolution = scaledResolution;
      this.displayedTitle = str;
      this.displayedSubTitle = str2;
      this.partialTicks = f;
      this.titlesTimer = n;
      this.titleDisplayTime = n2;
      this.titleFadeOut = n3;
      this.titleFadeIn = n4;
   }

   public int getTitleFadeIn() {
      return this.titleFadeIn;
   }
}
