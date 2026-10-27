package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractIconHudMod extends AbstractBackgroundHudMod {
   public Setting showIcon;
   public ResourceLocation resourceLocation2 = null;

   @Override
   public BackgroundSizeLimits getSizeLimits() {
      return new BackgroundSizeLimits(10.0F, 16.0F, 64.0F, 40.0F, 56.0F, 80.0F);
   }

   @Override
   public boolean wrapsTextWithoutBackground() {
      return false;
   }

   @Override
   public boolean isBoxDrawn() {
      return this.showBackground.getBooleanValue() || this.showIcon.getBooleanValue() || this.alwaysCenter.getBooleanValue();
   }

   public AbstractIconHudMod(String str) {
      super(str);
   }

   @Override
   public Setting getActiveFormat() {
      return !this.showIcon.getBooleanValue() && !this.showBackground.getBooleanValue() ? this.formatNoBackground : this.formatBackground;
   }

   public ResourceLocation getResourceLocation3() {
      return this.resourceLocation2;
   }

   @Override
   public void renderText(String str) {
      this.extraWidth = this.showIcon.getBooleanValue() ? this.boxHeight : 0.0F;
      super.renderText(str);
      if (this.showIcon.getBooleanValue() && this.resourceLocation2 != null) {
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         RenderUtils.drawSquareTexture(this.resourceLocation2, this.boxHeight / 2.0F, 0.0F, 0.0F);
      }
   }

   @Override
   public void createGeneralOptions() {
      this.showIcon = new Setting(this, "Show icon")
         .setDescription("Show an icon corresponding to the mod.")
         .changeValue(true)
         .withParents(this.generalOptions);
   }

   public void setResourceLocation2(ResourceLocation resourceLocation) {
      this.resourceLocation2 = resourceLocation;
   }
}
