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

    @Override
    public void afterModelRender(AbstractClientPlayer abstractClientPlayer, float f) {
    }

    @Override
    public void applyPose(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
        CheatBreaker.getInstance().getEmoteManager().getModelBackups().put(abstractClientPlayer.getUniqueID(), modelPlayer);
        if (!this.easeOutAnimation.hasStarted()) {
            if (this.cosineAnimation2.getProgress() >= 0.5) {
                this.easeOutAnimation.start();
                this.cosineAnimation.start();
            }
        } else if (this.easeOutAnimation.isFinished()) {
            this.easeOutAnimation.start();
            this.cosineAnimation.start();
            this.flossDirection = this.nextFlossDirection();
        }

        if (this.cosineAnimation2.isFinished()) {
            this.flag = !this.flag;
            this.cosineAnimation2.start();
        }

        float progress = this.easeOutAnimation.getProgress();
        float progress2 = this.cosineAnimation.getProgress();
        modelPlayer.bipedRightArm.rotateAngleX = (float)Math.toRadians((this.flossDirection == FlossDirection.RIGHT_TO_BACK ? 45 : -45) * progress2);
        modelPlayer.bipedLeftArm.rotateAngleX = (float)Math.toRadians((this.flossDirection == FlossDirection.LEFT_TO_BACK ? 45 : -45) * progress2);
        modelPlayer.modelRenderer2.rotateAngleX = (float)Math.toRadians((this.flossDirection == FlossDirection.RIGHT_TO_BACK ? 45 : -45) * progress2);
        modelPlayer.modelRenderer.rotateAngleX = (float)Math.toRadians((this.flossDirection == FlossDirection.LEFT_TO_BACK ? 45 : -45) * progress2);
        float f2 = 150.0F;
        float f3 = f2 / 2.0F;
        float toRadians = (float)Math.toRadians(f2 * progress - f3);
        float toRadians2 = (float)Math.toRadians(f3 - f3 * progress2);
        float toRadians3 = (float)Math.toRadians(-f2 * progress + f3);
        float toRadians4 = (float)Math.toRadians(f3 * progress2 - f3);
        switch (this.flossDirection) {
            case LEFT_TO_RIGHT:
                modelPlayer.bipedRightArm.rotateAngleZ = toRadians;
                modelPlayer.bipedLeftArm.rotateAngleZ = toRadians;
                modelPlayer.modelRenderer2.rotateAngleZ = toRadians;
                modelPlayer.modelRenderer.rotateAngleZ = toRadians;
                break;
            case RIGHT_TO_BACK:
                modelPlayer.bipedRightArm.rotateAngleZ = toRadians2;
                modelPlayer.bipedLeftArm.rotateAngleZ = toRadians2;
                modelPlayer.modelRenderer2.rotateAngleZ = toRadians2;
                modelPlayer.modelRenderer.rotateAngleZ = toRadians2;
                break;
            case RIGHT_TO_LEFT:
                modelPlayer.bipedRightArm.rotateAngleZ = toRadians3;
                modelPlayer.bipedLeftArm.rotateAngleZ = toRadians3;
                modelPlayer.modelRenderer2.rotateAngleZ = toRadians3;
                modelPlayer.modelRenderer.rotateAngleZ = toRadians3;
                break;
            case LEFT_TO_BACK:
                modelPlayer.bipedRightArm.rotateAngleZ = toRadians4;
                modelPlayer.bipedLeftArm.rotateAngleZ = toRadians4;
                modelPlayer.modelRenderer2.rotateAngleZ = toRadians4;
                modelPlayer.modelRenderer.rotateAngleZ = toRadians4;
        }

        progress2 = this.cosineAnimation2.getProgress();
        if (this.flag) {
            modelPlayer.bipedBody.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
            modelPlayer.bipedCloak.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
            modelPlayer.bipedRightLeg.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
            modelPlayer.bipedLeftLeg.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
            modelPlayer.bipedLeftLeg.offsetX = 0.2F * progress2;
            modelPlayer.bipedRightLeg.offsetX = 0.2F * progress2;
            modelPlayer.modelRenderer5.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
            modelPlayer.modelRenderer4.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
            modelPlayer.modelRenderer3.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
            modelPlayer.modelRenderer3.offsetX = 0.2F * progress2;
            modelPlayer.modelRenderer4.offsetX = 0.2F * progress2;
        } else {
            modelPlayer.bipedBody.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
            modelPlayer.bipedCloak.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
            modelPlayer.bipedRightLeg.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
            modelPlayer.bipedLeftLeg.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
            modelPlayer.bipedLeftLeg.offsetX = -0.2F * progress2;
            modelPlayer.bipedRightLeg.offsetX = -0.2F * progress2;
            modelPlayer.modelRenderer5.rotateAngleZ = (float)Math.toRadians(15.0F * progress2);
            modelPlayer.modelRenderer4.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
            modelPlayer.modelRenderer3.rotateAngleZ = (float)Math.toRadians(-15.0F * progress2);
            modelPlayer.modelRenderer3.offsetX = -0.2F * progress2;
            modelPlayer.modelRenderer4.offsetX = -0.2F * progress2;
        }
    }

    public FlossDirection nextFlossDirection() {
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

    public LegacyFlossEmote() {
        super("Legacy Floss", "legacy_floss", new LinearAnimation(7500L));
        this.cosineAnimation2.start();
        this.flossDirection = FlossDirection.LEFT_TO_RIGHT;
    }
}
