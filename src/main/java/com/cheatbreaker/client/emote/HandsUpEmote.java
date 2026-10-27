package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.EaseInOutAnimation;
import com.cheatbreaker.client.animation.LinearAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class HandsUpEmote extends AbstractEmote {
   public EaseInOutAnimation easeInOutAnimation = new EaseInOutAnimation(250L);
   public EaseInOutAnimation easeInOutAnimation2 = new EaseInOutAnimation(250L);

   @Override
   public void handleAbstractClientPlayer(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
      float f2 = 1.0F;
      if (this.easeInOutAnimation.getDuration() > this.animation.getElapsedTime()) {
         f2 = this.easeInOutAnimation.getProgress();
      } else if (this.animation.getClampedRemainingTime() <= this.easeInOutAnimation2.getDuration()) {
         if (!this.easeInOutAnimation2.hasStarted()) {
            this.easeInOutAnimation2.start();
         }

         f2 = 1.0F - this.easeInOutAnimation2.getProgress();
      }

      modelPlayer.bipedLeftArm.rotateAngleX = (float)Math.toRadians(-180.0F * f2);
      modelPlayer.bipedRightArm.rotateAngleX = (float)Math.toRadians(-180.0F * f2);
      modelPlayer.bipedRightArm.rotateAngleZ = (float)Math.toRadians(-15.0F * f2);
      modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(15.0F * f2);
      modelPlayer.bipedLeftArmwear.rotateAngleX = (float)Math.toRadians(-180.0F * f2);
      modelPlayer.bipedRightArmwear.rotateAngleX = (float)Math.toRadians(-180.0F * f2);
      modelPlayer.bipedRightArmwear.rotateAngleZ = (float)Math.toRadians(-15.0F * f2);
      modelPlayer.bipedLeftArmwear.rotateAngleZ = (float)Math.toRadians(15.0F * f2);
   }

   @Override
   public void noop(AbstractClientPlayer abstractClientPlayer, float f) {
   }

   public HandsUpEmote() {
      super("Hands Up", new LinearAnimation(2000L));
      this.easeInOutAnimation.start();
   }
}
