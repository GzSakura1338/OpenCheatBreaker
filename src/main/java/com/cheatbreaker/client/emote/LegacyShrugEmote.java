package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.CosineAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class LegacyShrugEmote extends AbstractEmote {
   public LegacyShrugEmote() {
      super("Legacy Shrug", "shrug", new CosineAnimation(500L));
   }

   @Override
   public void handleAbstractClientPlayer(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
      modelPlayer.bipedRightArm.offsetY = -0.2F * this.animation.getProgress();
      modelPlayer.bipedLeftArm.offsetY = -0.2F * this.animation.getProgress();
      modelPlayer.bipedRightArmwear.offsetY = -0.2F * this.animation.getProgress();
      modelPlayer.bipedLeftArmwear.offsetY = -0.2F * this.animation.getProgress();
      modelPlayer.bipedHead.offsetY = 0.05F * this.animation.getProgress();
      modelPlayer.bipedHeadwear.offsetY = 0.05F * this.animation.getProgress();
   }

   @Override
   public void noop(AbstractClientPlayer abstractClientPlayer, float f) {
   }
}
