package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.CosineAnimation;
import com.cheatbreaker.client.animation.LinearAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class LegacyWaveEmote extends AbstractEmote {
    public LinearAnimation fadeIn = new LinearAnimation(250L);
    public LinearAnimation fadeOut = new LinearAnimation(250L);
    public CosineAnimation cosineAnimation = new CosineAnimation(500L);

    public LegacyWaveEmote() {
        super("Legacy Wave", "legacy_wave", new LinearAnimation(2000L));
        this.fadeIn.start();
    }

    @Override
    public void afterModelRender(AbstractClientPlayer abstractClientPlayer, float f) {
    }

    @Override
    public void applyPose(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
        float f2 = 1.0F;
        float f3 = 0.5F;
        if (this.fadeIn.getDuration() > this.animation.getElapsedTime()) {
            f2 = this.fadeIn.getProgress();
        } else if (this.animation.getClampedRemainingTime() <= this.fadeOut.getDuration()) {
            if (!this.fadeOut.hasStarted()) {
                this.fadeOut.start();
            }

            f2 = 1.0F - this.fadeOut.getProgress();
        } else {
            if (!this.cosineAnimation.hasStarted()) {
                this.cosineAnimation.restartFrom(125.0F);
                this.cosineAnimation.enableRepeat();
            }

            f3 = this.cosineAnimation.getProgress();
        }

        modelPlayer.bipedLeftArm.rotateAngleX = (float)Math.toRadians(-150.0F * f2);
        modelPlayer.bipedLeftArm.rotateAngleZ = (float)Math.toRadians(40.0F * f3 - 20.0F);
        modelPlayer.modelRenderer.rotateAngleX = (float)Math.toRadians(-150.0F * f2);
        modelPlayer.modelRenderer.rotateAngleZ = (float)Math.toRadians(40.0F * f3 - 20.0F);
    }
}
