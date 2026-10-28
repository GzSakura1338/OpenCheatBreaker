package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.EaseInOutAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class DabEmote extends AbstractEmote {
    public EaseInOutAnimation fadeIn = new EaseInOutAnimation(250L);
    public EaseInOutAnimation fadeOut = new EaseInOutAnimation(250L);
    public boolean flag = false;
    public int intValue;

    @Override
    public void applyPose(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
        float f2 = 1.0F;
        if (this.fadeIn.getDuration() > this.animation.getElapsedTime()) {
            f2 = this.fadeIn.getProgress();
        } else if (this.animation.getClampedRemainingTime() <= this.fadeOut.getDuration()) {
            if (!this.fadeOut.hasStarted()) {
                this.fadeOut.start();
            }

            f2 = 1.0F - this.fadeOut.getProgress();
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
            modelPlayer.modelRenderer2.rotateAngleX = (float)Math.toRadians(90.0F * f2);
            modelPlayer.modelRenderer2.rotateAngleY = (float)Math.toRadians(-35.0F * f2);
            modelPlayer.modelRenderer.rotateAngleX = (float)Math.toRadians(-15.0F * f2);
            modelPlayer.modelRenderer.rotateAngleY = (float)Math.toRadians(15.0F * f2);
            modelPlayer.modelRenderer.rotateAngleZ = (float)Math.toRadians(110.0F * f2);
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
            modelPlayer.modelRenderer2.rotateAngleX = (float)Math.toRadians(-90.0F * f2);
            modelPlayer.modelRenderer2.rotateAngleY = (float)Math.toRadians(-35.0F * f2);
            modelPlayer.modelRenderer.rotateAngleX = (float)Math.toRadians(15.0F * f2);
            modelPlayer.modelRenderer.rotateAngleY = (float)Math.toRadians(15.0F * f2);
            modelPlayer.modelRenderer.rotateAngleZ = (float)Math.toRadians(-110.0F * f2);
            float rotationPitch = abstractClientPlayer.rotationPitch;
            float f3 = abstractClientPlayer.prevRenderYawOffset - abstractClientPlayer.rotationYaw;
            modelPlayer.bipedHead.rotateAngleX = (float)Math.toRadians(-rotationPitch * f2) + (float)Math.toRadians(45.0F * f2 + rotationPitch);
            modelPlayer.bipedHead.rotateAngleY = (float)Math.toRadians(f3 * f2) + (float)Math.toRadians(35.0F * f2 - f3);
            modelPlayer.bipedHeadwear.rotateAngleX = (float)Math.toRadians(-rotationPitch * f2) + (float)Math.toRadians(45.0F * f2 + rotationPitch);
            modelPlayer.bipedHeadwear.rotateAngleY = (float)Math.toRadians(f3 * f2) + (float)Math.toRadians(35.0F * f2 - f3);
            this.intValue--;
        }
    }

    @Override
    public void afterModelRender(AbstractClientPlayer abstractClientPlayer, float f) {
    }

    public DabEmote() {
        super("Dab", new EaseInOutAnimation(7500L));
        this.fadeIn.start();
        this.intValue = 1000;
    }
}
