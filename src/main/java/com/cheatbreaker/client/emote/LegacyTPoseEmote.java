package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.EaseInOutAnimation;
import com.cheatbreaker.client.animation.EaseOutAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class LegacyTPoseEmote extends AbstractEmote {
    public EaseOutAnimation easeOutAnimation = new EaseOutAnimation(600L);
    public EaseInOutAnimation easeInOutAnimation = new EaseInOutAnimation(600L);

    @Override
    public void applyPose(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
        modelPlayer.bipedRightArm.rotateAngleX = modelPlayer.bipedRightArm.rotateAngleX * this.easeInOutAnimation.getProgress();
        modelPlayer.bipedLeftArm.rotateAngleX = modelPlayer.bipedLeftArm.rotateAngleX * this.easeInOutAnimation.getProgress();
        modelPlayer.modelRenderer2.rotateAngleX = modelPlayer.modelRenderer2.rotateAngleX * this.easeInOutAnimation.getProgress();
        modelPlayer.modelRenderer.rotateAngleX = modelPlayer.modelRenderer.rotateAngleX * this.easeInOutAnimation.getProgress();
        if (this.easeOutAnimation.isRunning()) {
            modelPlayer.bipedRightArm.rotateAngleZ = (float)Math.toRadians(90.0F * this.easeOutAnimation.getProgress());
            modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(-90.0F * this.easeOutAnimation.getProgress());
            modelPlayer.modelRenderer2.rotateAngleZ = (float)Math.toRadians(90.0F * this.easeOutAnimation.getProgress());
            modelPlayer.modelRenderer.rotateAngleZ = (float)Math.toRadians(-90.0F * this.easeOutAnimation.getProgress());
        } else if (this.animation.getClampedRemainingTime() <= this.easeInOutAnimation.getDuration()) {
            if (!this.easeInOutAnimation.hasStarted()) {
                this.easeInOutAnimation.start();
            }

            modelPlayer.bipedRightArm.rotateAngleZ = Math.max(
                (float)Math.toRadians(90.0F - 90.0F * this.easeInOutAnimation.getProgress()), modelPlayer.bipedRightArm.rotateAngleZ
            );
            modelPlayer.bipedLeftArm.rotateAngleZ = Math.min(
                (float)Math.toRadians(-90.0F + 90.0F * this.easeInOutAnimation.getProgress()), modelPlayer.bipedLeftArm.rotateAngleZ
            );
            modelPlayer.modelRenderer2.rotateAngleZ = Math.max(
                (float)Math.toRadians(90.0F - 90.0F * this.easeInOutAnimation.getProgress()), modelPlayer.modelRenderer2.rotateAngleZ
            );
            modelPlayer.modelRenderer.rotateAngleZ = Math.min(
                (float)Math.toRadians(-90.0F + 90.0F * this.easeInOutAnimation.getProgress()), modelPlayer.modelRenderer.rotateAngleZ
            );
        } else {
            modelPlayer.bipedRightArm.rotateAngleZ = (float)Math.toRadians(90.0);
            modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(-90.0);
            modelPlayer.modelRenderer2.rotateAngleZ = (float)Math.toRadians(90.0);
            modelPlayer.modelRenderer.rotateAngleZ = (float)Math.toRadians(-90.0);
        }
    }

    public LegacyTPoseEmote() {
        super("Legacy T-Pose", "t_pose", new EaseInOutAnimation(5000L));
        this.easeOutAnimation.start();
    }

    @Override
    public void afterModelRender(AbstractClientPlayer abstractClientPlayer, float f) {
    }
}
