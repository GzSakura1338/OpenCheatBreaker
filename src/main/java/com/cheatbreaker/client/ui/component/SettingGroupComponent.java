package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class SettingGroupComponent extends SettingComponent {
   public ResourceLocation rightPng = new ResourceLocation("client/icons/right.png");
   public ResourceLocation downPng = new ResourceLocation("client/icons/down.png");
   public int hoverProgress = 0;

   @Override
   public void render(int n, int n2, float f) {
      boolean flag = n > this.x * this.scale
         && n < (this.x + this.width - 176) * this.scale
         && n2 > (this.y + this.scrollOffsetY) * this.scale
         && n2 < (this.y + 15 + this.scrollOffsetY) * this.scale;
      byte b = 120;
      if (flag) {
         float animationStep = ModsScreen.animationStep(790.0F);
         this.hoverProgress = this.hoverProgress + animationStep < b ? (int)(this.hoverProgress + animationStep) : b;
      } else if (this.hoverProgress > 0) {
         float animationStep = ModsScreen.animationStep(790.0F);
         this.hoverProgress = this.hoverProgress - animationStep < 0.0F ? 0 : (int)(this.hoverProgress - animationStep);
      }

      if (!this.setting.getBooleanValue()) {
         RenderUtils.drawRoundedRectFilledPrecise(
            this.x,
            this.y + this.height - 11,
            this.x + this.width / 2 - 12,
            this.y + this.height,
            this.theme.getButtonCornerRadiusValue(),
            this.theme.getButtonBackgroundColorValue()
         );
      }

      if (this.hoverProgress > 0) {
         float f2 = (float)this.hoverProgress / b * 100.0F;
         RenderUtils.drawRoundedRectFilledPrecise(
            this.x,
            (int)(this.y + (this.height - 11.0F * f2 / 100.0F)),
            this.x + this.width / 2 - 12,
            this.y + this.height,
            this.theme.getButtonCornerRadiusValue(),
            this.theme.getLabelHighlightColorValue()
         );
      }

      boolean booleanValue = this.setting.getBooleanValue();
      float f2 = booleanValue ? 0.35F : 0.75F;
      float red = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
      float green = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
      float blue = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
      int n3 = booleanValue ? this.theme.getPrimaryTextColorValue() : this.theme.getTextColor4Value();
      GlStateManager.color(red, green, blue, f2);
      RenderUtils.drawSquareTextureViaRenderEngine(booleanValue ? this.downPng : this.rightPng, 2.5F, this.x + 3, this.y + 4.0F);
      CheatBreaker.getInstance().getFontManager().ubuntuM16.drawString(this.setting.getName().toUpperCase(), this.x + 10, this.y + 2, n3);
      if (this.setting.getBooleanValue()) {
         Gui.drawRect(this.x + 2, this.y + this.height - 1, this.x + this.width / 2 - 12, this.y + this.height, this.theme.getTextColor3Value2());
      }
   }

   public SettingGroupComponent(Setting setting, float f) {
      super(f);
      this.setting = setting;
      this.height = 12;
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      if (!this.isHidden(this.setting)) {
         boolean flag = n > this.x * this.scale
            && n < (this.x + this.width - 176) * this.scale
            && n2 > (this.y + this.scrollOffsetY) * this.scale
            && n2 < (this.y + 18 + this.scrollOffsetY) * this.scale;
         if (flag) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.setting.changeValue(!this.setting.getBooleanValue());
         }
      }
   }
}
