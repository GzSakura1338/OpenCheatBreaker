package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.EaseInOutAnimation;
import com.cheatbreaker.client.animation.LinearAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class HandsUpEmote extends AbstractEmote {
    public EaseInOutAnimation fadeIn = new EaseInOutAnimation(250L);
    public EaseInOutAnimation fadeOut = new EaseInOutAnimation(250L);

    @Override
    public void afterModelRender(AbstractClientPlayer abstractClientPlayer, float f) {
    }

    public HandsUpEmote() {
        super("Hands Up", new LinearAnimation(2000L));
        this.fadeIn.start();
    }

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

        modelPlayer.bipedLeftArm.rotateAngleX = (float)Math.toRadians(-180.0F * f2);
        modelPlayer.bipedRightArm.rotateAngleX = (float)Math.toRadians(-180.0F * f2);
        modelPlayer.bipedRightArm.rotateAngleZ = (float)Math.toRadians(-15.0F * f2);
        modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(15.0F * f2);
        modelPlayer.modelRenderer.rotateAngleX = (float)Math.toRadians(-180.0F * f2);
        modelPlayer.modelRenderer2.rotateAngleX = (float)Math.toRadians(-180.0F * f2);
        modelPlayer.modelRenderer2.rotateAngleZ = (float)Math.toRadians(-15.0F * f2);
        modelPlayer.modelRenderer.rotateAngleZ = (float)Math.toRadians(15.0F * f2);
    }
}
