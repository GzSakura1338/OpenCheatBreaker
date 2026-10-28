package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.CosineAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class LegacyShrugEmote extends AbstractEmote {
    public LegacyShrugEmote() {
        super("Legacy Shrug", "legacy_shrug", new CosineAnimation(500L));
    }

    @Override
    public void applyPose(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
        modelPlayer.bipedRightArm.offsetY = -0.2F * this.animation.getProgress();
        modelPlayer.bipedLeftArm.offsetY = -0.2F * this.animation.getProgress();
        modelPlayer.bipedHead.offsetY = 0.05F * this.animation.getProgress();
        modelPlayer.bipedHeadwear.offsetY = 0.05F * this.animation.getProgress();
        modelPlayer.modelRenderer2.offsetY = -0.2F * this.animation.getProgress();
        modelPlayer.modelRenderer.offsetY = -0.2F * this.animation.getProgress();
    }

    @Override
    public void afterModelRender(AbstractClientPlayer abstractClientPlayer, float f) {
    }
}
