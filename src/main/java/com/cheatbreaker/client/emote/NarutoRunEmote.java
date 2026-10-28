package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.EaseInOutAnimation;
import com.cheatbreaker.client.animation.EaseOutAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class NarutoRunEmote extends AbstractEmote {
    public EaseOutAnimation easeOutAnimation = new EaseOutAnimation(300L);
    public EaseInOutAnimation easeInOutAnimation = new EaseInOutAnimation(500L);

    @Override
    public void afterModelRender(AbstractClientPlayer abstractClientPlayer, float f) {
    }

    public NarutoRunEmote() {
        super("Naruto Run", new EaseInOutAnimation(10000L));
        this.easeOutAnimation.start();
    }

    @Override
    public void applyPose(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
        modelPlayer.bipedRightArm.rotateAngleX = modelPlayer.bipedRightArm.rotateAngleX * this.easeInOutAnimation.getProgress();
        modelPlayer.bipedLeftArm.rotateAngleX = modelPlayer.bipedLeftArm.rotateAngleX * this.easeInOutAnimation.getProgress();
        modelPlayer.modelRenderer2.rotateAngleX = modelPlayer.modelRenderer2.rotateAngleX * this.easeInOutAnimation.getProgress();
        modelPlayer.modelRenderer.rotateAngleX = modelPlayer.modelRenderer.rotateAngleX * this.easeInOutAnimation.getProgress();
        if (this.easeOutAnimation.isRunning()) {
            modelPlayer.bipedRightArm.rotateAngleX = (float)Math.toRadians(90.0F * this.easeOutAnimation.getProgress());
            modelPlayer.bipedLeftArm.rotateAngleX = (float)Math.toRadians(90.0F * this.easeOutAnimation.getProgress());
            modelPlayer.modelRenderer2.rotateAngleX = (float)Math.toRadians(90.0F * this.easeOutAnimation.getProgress());
            modelPlayer.modelRenderer.rotateAngleX = (float)Math.toRadians(90.0F * this.easeOutAnimation.getProgress());
        } else if (this.animation.getClampedRemainingTime() <= this.easeInOutAnimation.getDuration()) {
            if (!this.easeInOutAnimation.hasStarted()) {
                this.easeInOutAnimation.start();
            }

            modelPlayer.bipedRightArm.rotateAngleX = Math.max(
                (float)Math.toRadians(90.0F - 90.0F * this.easeInOutAnimation.getProgress()), modelPlayer.bipedRightArm.rotateAngleZ
            );
            modelPlayer.bipedLeftArm.rotateAngleX = Math.min(
                (float)Math.toRadians(-270.0F - 90.0F * this.easeInOutAnimation.getProgress()), modelPlayer.bipedRightArm.rotateAngleZ
            );
            modelPlayer.modelRenderer2.rotateAngleX = Math.max(
                (float)Math.toRadians(90.0F - 90.0F * this.easeInOutAnimation.getProgress()), modelPlayer.modelRenderer2.rotateAngleZ
            );
            modelPlayer.modelRenderer.rotateAngleX = Math.min(
                (float)Math.toRadians(-270.0F - 90.0F * this.easeInOutAnimation.getProgress()), modelPlayer.modelRenderer2.rotateAngleZ
            );
        } else {
            abstractClientPlayer.setSneaking(true);
            modelPlayer.bipedRightArm.rotateAngleX = (float)Math.toRadians(90.0);
            modelPlayer.bipedLeftArm.rotateAngleX = (float)Math.toRadians(90.0);
            modelPlayer.modelRenderer2.rotateAngleX = (float)Math.toRadians(90.0);
            modelPlayer.modelRenderer.rotateAngleX = (float)Math.toRadians(90.0);
        }
    }
}
