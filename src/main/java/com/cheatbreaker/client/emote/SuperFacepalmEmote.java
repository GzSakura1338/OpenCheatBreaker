package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.CosineAnimation;
import com.cheatbreaker.client.animation.EaseOutAnimation;
import com.cheatbreaker.client.animation.LinearAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class SuperFacepalmEmote extends AbstractEmote {
   public EaseOutAnimation easeOutAnimation = new EaseOutAnimation(1000L);
   public EaseOutAnimation easeOutAnimation2 = new EaseOutAnimation(2000L);
   public CosineAnimation cosineAnimation = new CosineAnimation(100L);
   public float floatValue = (float)Math.toRadians(45.0);
   public float floatValue2 = (float)Math.toRadians(-30.0);
   public float floatValue3 = (float)Math.toRadians(-100.0);

   @Override
   public void handleAbstractClientPlayer(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
      float progress = this.easeOutAnimation.getProgress();
      if (!this.easeOutAnimation.hasStarted() && this.animation.getElapsedTime() >= 150L) {
         this.easeOutAnimation.start();
      }

      if (this.easeOutAnimation.hasStarted()) {
         if (this.easeOutAnimation.isFinished() && !this.cosineAnimation.isRunning() && !this.easeOutAnimation2.hasStarted()) {
            this.cosineAnimation.start();
         }

         float rotateAngleX = modelPlayer.bipedHead.rotateAngleX;
         float rotateAngleY = modelPlayer.bipedHead.rotateAngleY;
         modelPlayer.bipedHead.rotateAngleZ = -((float)Math.toRadians(10.0F * this.cosineAnimation.getProgress()));
         modelPlayer.bipedHeadwear.rotateAngleZ = -((float)Math.toRadians(10.0F * this.cosineAnimation.getProgress()));
         modelPlayer.bipedHead.rotateAngleY = (float)Math.toRadians(10.0) * progress - (float)Math.toRadians(10.0F * this.cosineAnimation.getProgress());
         modelPlayer.bipedHeadwear.rotateAngleY = (float)Math.toRadians(10.0) * progress - (float)Math.toRadians(10.0F * this.cosineAnimation.getProgress());
         modelPlayer.bipedHead.rotateAngleX = this.floatValue * progress;
         modelPlayer.bipedHeadwear.rotateAngleX = this.floatValue * progress;
         modelPlayer.bipedRightArm.rotateAngleY = this.floatValue2 * progress
            - (this.easeOutAnimation2.hasStarted() ? 0.0F : (float)Math.toRadians(10.0F * this.cosineAnimation.getProgress()));
         modelPlayer.bipedRightArm.rotateAngleX = this.floatValue3 * progress;
         modelPlayer.bipedRightArmwear.rotateAngleY = this.floatValue2 * progress
            - (this.easeOutAnimation2.hasStarted() ? 0.0F : (float)Math.toRadians(10.0F * this.cosineAnimation.getProgress()));
         modelPlayer.bipedRightArmwear.rotateAngleX = this.floatValue3 * progress;
         if (!this.easeOutAnimation2.hasStarted() && this.animation.getClampedRemainingTime() <= this.easeOutAnimation2.getDuration()) {
            this.easeOutAnimation2.start();
         }

         if (this.easeOutAnimation2.hasStarted()) {
            progress = this.easeOutAnimation2.getProgress();
            modelPlayer.bipedHead.rotateAngleY = rotateAngleY * progress;
            modelPlayer.bipedHeadwear.rotateAngleY = rotateAngleY * progress;
            modelPlayer.bipedHead.rotateAngleZ = 0.0F;
            modelPlayer.bipedHeadwear.rotateAngleZ = 0.0F;
            modelPlayer.bipedHead.rotateAngleX = modelPlayer.bipedHead.rotateAngleX - (this.floatValue - rotateAngleX) * progress;
            modelPlayer.bipedHeadwear.rotateAngleX = modelPlayer.bipedHeadwear.rotateAngleX - (this.floatValue - rotateAngleX) * progress;
            modelPlayer.bipedRightArm.rotateAngleY = modelPlayer.bipedRightArm.rotateAngleY - this.floatValue2 * progress;
            modelPlayer.bipedRightArm.rotateAngleX = modelPlayer.bipedRightArm.rotateAngleX - this.floatValue3 * progress;
            modelPlayer.bipedRightArmwear.rotateAngleY = modelPlayer.bipedRightArmwear.rotateAngleY - this.floatValue2 * progress;
            modelPlayer.bipedRightArmwear.rotateAngleX = modelPlayer.bipedRightArmwear.rotateAngleX - this.floatValue3 * progress;
         }
      }
   }

   @Override
   public void noop(AbstractClientPlayer abstractClientPlayer, float f) {
   }

   public SuperFacepalmEmote() {
      super("Super Facepalm", new LinearAnimation(20000L));
   }
}
