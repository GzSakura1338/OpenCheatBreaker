package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.CosineAnimation;
import com.cheatbreaker.client.animation.LinearAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class LegacyWaveEmote extends AbstractEmote {
   public LinearAnimation linearAnimation = new LinearAnimation(250L);
   public LinearAnimation linearAnimation2 = new LinearAnimation(250L);
   public CosineAnimation cosineAnimation = new CosineAnimation(500L);

   @Override
   public void noop(AbstractClientPlayer abstractClientPlayer, float f) {
   }

   public LegacyWaveEmote() {
      super("Legacy Wave", "wave", new LinearAnimation(2000L));
      this.linearAnimation.start();
   }

   @Override
   public void handleAbstractClientPlayer(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
      float f2 = 1.0F;
      float f3 = 0.5F;
      if (this.linearAnimation.getDuration() > this.animation.getElapsedTime()) {
         f2 = this.linearAnimation.getProgress();
      } else if (this.animation.getClampedRemainingTime() <= this.linearAnimation2.getDuration()) {
         if (!this.linearAnimation2.hasStarted()) {
            this.linearAnimation2.start();
         }

         f2 = 1.0F - this.linearAnimation2.getProgress();
      } else {
         if (!this.cosineAnimation.hasStarted()) {
            this.cosineAnimation.restartFrom(125.0F);
            this.cosineAnimation.enableRepeat();
         }

         f3 = this.cosineAnimation.getProgress();
      }

      modelPlayer.bipedLeftArm.rotateAngleX = (float)Math.toRadians(-150.0F * f2);
      modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(40.0F * f3 - 20.0F);
      modelPlayer.bipedLeftArmwear.rotateAngleX = (float)Math.toRadians(-150.0F * f2);
      modelPlayer.bipedLeftArmwear.rotateAngleZ = (float)Math.toRadians(40.0F * f3 - 20.0F);
   }
}
