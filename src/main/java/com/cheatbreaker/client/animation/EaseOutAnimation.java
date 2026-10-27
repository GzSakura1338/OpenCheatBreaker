package com.cheatbreaker.client.animation;

public class EaseOutAnimation extends LinearAnimation {
   public EaseOutAnimation(long l) {
      super(l);
   }

   @Override
   public float computeProgress() {
      float computeProgress = super.computeProgress();
      return (float)Math.pow(computeProgress * (2.0F - computeProgress), 1.0);
   }
}
