package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.EaseInOutAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class DabEmote extends AbstractEmote {
   public EaseInOutAnimation easeInOutAnimation = new EaseInOutAnimation(250L);
   public EaseInOutAnimation easeInOutAnimation2 = new EaseInOutAnimation(250L);
   public boolean flag = false;
   public int intValue;

   @Override
   public void noop(AbstractClientPlayer abstractClientPlayer, float f) {
   }

   public DabEmote() {
      super("Dab", new EaseInOutAnimation(7500L));
      this.easeInOutAnimation.start();
      this.intValue = 1000;
   }

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

      if (this.intValue == 0) {
         this.flag = true;
      } else if (this.intValue == 1000) {
         this.flag = false;
      }

      if (this.flag) {
         modelPlayer.bipedRightArm.rotateAngleX = (float)Math.toRadians(90.0F * f2);
         modelPlayer.bipedRightArm.rotateAngleY = (float)Math.toRadians(-35.0F * f2);
         modelPlayer.bipedLeftArm.rotateAngleX = (float)Math.toRadians(-15.0F * f2);
         modelPlayer.bipedLeftArm.rotateAngleY = (float)Math.toRadians(15.0F * f2);
         modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(110.0F * f2);
         float rotationPitch = abstractClientPlayer.rotationPitch;
         float f3 = abstractClientPlayer.prevRenderYawOffset - abstractClientPlayer.rotationYaw;
         modelPlayer.bipedHead.rotateAngleX = (float)Math.toRadians(-rotationPitch * f2) + (float)Math.toRadians(-45.0F * f2 + rotationPitch);
         modelPlayer.bipedHead.rotateAngleY = (float)Math.toRadians(f3 * f2) + (float)Math.toRadians(35.0F * f2 - f3);
         modelPlayer.bipedHeadwear.rotateAngleX = (float)Math.toRadians(-rotationPitch * f2) + (float)Math.toRadians(-45.0F * f2 + rotationPitch);
         modelPlayer.bipedHeadwear.rotateAngleY = (float)Math.toRadians(f3 * f2) + (float)Math.toRadians(35.0F * f2 - f3);
         this.intValue++;
      } else {
         modelPlayer.bipedRightArm.rotateAngleX = (float)Math.toRadians(-90.0F * f2);
         modelPlayer.bipedRightArm.rotateAngleY = (float)Math.toRadians(-35.0F * f2);
         modelPlayer.bipedLeftArm.rotateAngleX = (float)Math.toRadians(15.0F * f2);
         modelPlayer.bipedLeftArm.rotateAngleY = (float)Math.toRadians(15.0F * f2);
         modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(-110.0F * f2);
         float rotationPitch = abstractClientPlayer.rotationPitch;
         float f3 = abstractClientPlayer.prevRenderYawOffset - abstractClientPlayer.rotationYaw;
         modelPlayer.bipedHead.rotateAngleX = (float)Math.toRadians(-rotationPitch * f2) + (float)Math.toRadians(45.0F * f2 + rotationPitch);
         modelPlayer.bipedHead.rotateAngleY = (float)Math.toRadians(f3 * f2) + (float)Math.toRadians(35.0F * f2 - f3);
         modelPlayer.bipedHeadwear.rotateAngleX = (float)Math.toRadians(-rotationPitch * f2) + (float)Math.toRadians(45.0F * f2 + rotationPitch);
         modelPlayer.bipedHeadwear.rotateAngleY = (float)Math.toRadians(f3 * f2) + (float)Math.toRadians(35.0F * f2 - f3);
         this.intValue--;
      }
   }
}
