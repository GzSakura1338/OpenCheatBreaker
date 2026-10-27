package com.cheatbreaker.client.animation;

import com.cheatbreaker.client.util.FastTrig;

public class CosineAnimation extends LinearAnimation {
   @Override
   public float computeProgress() {
      float computeProgress = super.computeProgress();
      float f = computeProgress * 2.0F - 1.0F;
      return (float)(FastTrig.cos(f * Math.PI) + 1.0) / 2.0F;
   }

   public CosineAnimation(long l) {
      super(l, 0.0F);
   }
}
