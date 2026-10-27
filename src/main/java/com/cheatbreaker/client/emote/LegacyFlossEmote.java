package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.CosineAnimation;
import com.cheatbreaker.client.animation.EaseOutAnimation;
import com.cheatbreaker.client.animation.LinearAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class LegacyFlossEmote extends AbstractEmote {
   public EaseOutAnimation easeOutAnimation = new EaseOutAnimation(375L);
   public CosineAnimation cosineAnimation = new CosineAnimation(375L);
   public CosineAnimation cosineAnimation2 = new CosineAnimation(250L);
   public boolean flag = false;
   public FlossDirection flossDirection;

   public LegacyFlossEmote() {
      super("Legacy Floss", "floss", new LinearAnimation(7500L));
      this.cosineAnimation2.start();
      this.flossDirection = FlossDirection.LEFT_TO_RIGHT;
   }

   @Override
   public void noop(AbstractClientPlayer abstractClientPlayer, float f) {
   }

   public FlossDirection ordinal() {
      switch (this.flossDirection) {
         case RIGHT_TO_BACK:
            return FlossDirection.RIGHT_TO_LEFT;
         case RIGHT_TO_LEFT:
            return FlossDirection.LEFT_TO_BACK;
         case LEFT_TO_BACK:
            return FlossDirection.LEFT_TO_RIGHT;
         default:
            return FlossDirection.RIGHT_TO_BACK;
      }
   }

   @Override
   public void handleAbstractClientPlayer(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
      CheatBreaker.getInstance().getEmoteManager().getModelBackups().put(abstractClientPlayer.getUniqueID(), modelPlayer);
      if (!this.easeOutAnimation.hasStarted()) {
         if (this.cosineAnimation2.getProgress() >= 0.5) {
            this.easeOutAnimation.start();
            this.cosineAnimation.start();
         }
      } else if (this.easeOutAnimation.isFinished()) {
         this.easeOutAnimation.start();
         this.cosineAnimation.start();
         this.flossDirection = this.ordinal();
      }

      if (this.cosineAnimation2.isFinished()) {
         this.flag = !this.flag;
         this.cosineAnimation2.start();
      }

      float progress = this.easeOutAnimation.getProgress();
      float progress2 = this.cosineAnimation.getProgress();
      modelPlayer.bipedRightArm.rotateAngleX = (float)Math.toRadians((this.flossDirection == FlossDirection.RIGHT_TO_BACK ? 45 : -45) * progress2);
      modelPlayer.bipedLeftArm.rotateAngleX = (float)Math.toRadians((this.flossDirection == FlossDirection.LEFT_TO_BACK ? 45 : -45) * progress2);
      modelPlayer.bipedRightArmwear.rotateAngleX = (float)Math.toRadians((this.flossDirection == FlossDirection.RIGHT_TO_BACK ? 45 : -45) * progress2);
      modelPlayer.bipedLeftArmwear.rotateAngleX = (float)Math.toRadians((this.flossDirection == FlossDirection.LEFT_TO_BACK ? 45 : -45) * progress2);
      float f2 = 150.0F;
      float f3 = f2 / 2.0F;
      switch (this.flossDirection) {
         case LEFT_TO_RIGHT:
            modelPlayer.bipedRightArm.rotateAngleZ = (float)Math.toRadians(f2 * progress - f3);
            modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(f2 * progress - f3);
            modelPlayer.bipedRightArmwear.rotateAngleZ = (float)Math.toRadians(f2 * progress - f3);
            modelPlayer.bipedLeftArmwear.rotateAngleZ = (float)Math.toRadians(f2 * progress - f3);
            break;
         case RIGHT_TO_BACK:
            modelPlayer.bipedRightArm.rotateAngleZ = (float)Math.toRadians(f3 - f3 * progress2);
            modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(f3 - f3 * progress2);
            modelPlayer.bipedRightArmwear.rotateAngleZ = (float)Math.toRadians(f3 - f3 * progress2);
            modelPlayer.bipedLeftArmwear.rotateAngleZ = (float)Math.toRadians(f3 - f3 * progress2);
            break;
         case RIGHT_TO_LEFT:
            modelPlayer.bipedRightArm.rotateAngleZ = (float)Math.toRadians(-f2 * progress + f3);
            modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(-f2 * progress + f3);
            modelPlayer.bipedRightArmwear.rotateAngleZ = (float)Math.toRadians(-f2 * progress + f3);
            modelPlayer.bipedLeftArmwear.rotateAngleZ = (float)Math.toRadians(-f2 * progress + f3);
            break;
         case LEFT_TO_BACK:
            modelPlayer.bipedRightArm.rotateAngleZ = (float)Math.toRadians(f3 * progress2 - f3);
            modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(f3 * progress2 - f3);
            modelPlayer.bipedRightArmwear.rotateAngleZ = (float)Math.toRadians(f3 * progress2 - f3);
            modelPlayer.bipedLeftArmwear.rotateAngleZ = (float)Math.toRadians(f3 * progress2 - f3);
      }

      progress2 = this.cosineAnimation2.getProgress();
      if (this.flag) {
         modelPlayer.bipedBody.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
         modelPlayer.bipedCape.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
         modelPlayer.bipedRightLeg.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
         modelPlayer.bipedLeftLeg.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
         modelPlayer.bipedLeftLeg.offsetX = 0.2F * progress2;
         modelPlayer.bipedRightLeg.offsetX = 0.2F * progress2;
         modelPlayer.bipedBodyWear.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
         modelPlayer.bipedRightLegwear.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
         modelPlayer.bipedLeftLegwear.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
         modelPlayer.bipedLeftLegwear.offsetX = 0.2F * progress2;
         modelPlayer.bipedRightLegwear.offsetX = 0.2F * progress2;
      } else {
         modelPlayer.bipedBody.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
         modelPlayer.bipedCape.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
         modelPlayer.bipedRightLeg.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
         modelPlayer.bipedLeftLeg.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
         modelPlayer.bipedLeftLeg.offsetX = -0.2F * progress2;
         modelPlayer.bipedRightLeg.offsetX = -0.2F * progress2;
         modelPlayer.bipedBodyWear.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
         modelPlayer.bipedRightLegwear.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
         modelPlayer.bipedLeftLegwear.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
         modelPlayer.bipedLeftLegwear.offsetX = -0.2F * progress2;
         modelPlayer.bipedRightLegwear.offsetX = -0.2F * progress2;
      }
   }
}
