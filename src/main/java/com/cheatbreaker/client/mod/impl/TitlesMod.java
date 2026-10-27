package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.event.ScaledResolutionStringFloatIntEvent;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class TitlesMod extends AbstractHudMod {
   public int intValue = 0;
   public Setting subtitleDistance;
   public Setting textShadow;

   public void onScaledResolutionStringFloatIntEvent(ScaledResolutionStringFloatIntEvent scaledResolutionStringFloatIntEvent) {
      if (this.shouldRender()) {
         this.intValue = scaledResolutionStringFloatIntEvent.getTitlesTimer();
         int titleFadeIn = scaledResolutionStringFloatIntEvent.getTitleFadeIn();
         int titleFadeOut = scaledResolutionStringFloatIntEvent.getTitleFadeOut();
         int titleDisplayTime = scaledResolutionStringFloatIntEvent.getTitleDisplayTime();
         if (this.intValue > 0) {
            float f = this.intValue - scaledResolutionStringFloatIntEvent.getPartialTicks();
            int n = 255;
            if (this.intValue > titleFadeOut + titleDisplayTime) {
               float f2 = titleFadeIn + titleDisplayTime + titleFadeOut - f;
               n = (int)(f2 * 255.0F / titleFadeIn);
            }

            if (this.intValue <= titleFadeOut) {
               n = (int)(f * 255.0F / titleFadeOut);
            }

            n = MathHelper.clamp_int(n, 0, 255);
            if (n > 8) {
               this.handleScaledResolution(scaledResolutionStringFloatIntEvent.getScaledResolution(), scaledResolutionStringFloatIntEvent.getDisplayedTitle(), scaledResolutionStringFloatIntEvent.getDisplayedSubTitle(), n);
            }
         }
      }
   }

   public void handleScaledResolution(ScaledResolution scaledResolution, String str, String str2, int n) {
      GlStateManager.pushMatrix();
      this.applyHudTransformForSize(scaledResolution);
      this.setHudWidth(Math.max(this.mc.fontRendererObj.getStringWidth(str) * 4, this.mc.fontRendererObj.getStringWidth(str2) * 2));
      this.setHudHeight(
         this.mc.fontRendererObj.FONT_HEIGHT * 4
            + ((this.mc.fontRendererObj.FONT_HEIGHT + 1) * ((Float)this.subtitleDistance.getValue() / 100.0F * 2.0F) + 10.0F)
      );
      GlStateManager.enableBlend();
      GlStateManager.pushMatrix();
      GlStateManager.scale(4.0F, 4.0F, 4.0F);
      int n2 = n << 24 & 0xFF000000;
      this.mc
         .fontRendererObj
         .drawString(
            str, this.getHudWidth() / 8.0F - this.mc.fontRendererObj.getStringWidth(str) / 2, 0.0F, 16777215 | n2, this.textShadow.getBooleanValue()
         );
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scale(2.0F, 2.0F, 2.0F);
      this.mc
         .fontRendererObj
         .drawString(
            str2,
            this.getHudWidth() / 4.0F - this.mc.fontRendererObj.getStringWidth(str2) / 2,
            this.getHudHeight() / 2.0F - this.mc.fontRendererObj.FONT_HEIGHT + 1.0F,
            16777215 | n2,
            this.textShadow.getBooleanValue()
         );
      GlStateManager.popMatrix();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }

   public TitlesMod() {
      super("Titles");
      this.setAnchorPointAndDefault(AnchorPoint.MIDDLE_MIDDLE);
      this.setDefaultTranslationY(-7.0F);
      this.setDescription("Allows you to move and toggle titles");
      this.setCategories(ModCategory.HUD, ModCategory.SERVER);
      this.hudOnly = false;
      this.setRenderHudAndDefault(false);
      this.subtitleDistance = new Setting(this, "Subtitle Distance")
         .setDescription("Changes the distance that the Subtitle shows from the Title")
         .changeValue(100.0F)
         .setRange(0.0F, 100.0F)
         .setUnit("%");
      this.textShadow = new Setting(this, "Text Shadow").changeValue(true);
      this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
      this.addEventHandler(ScaledResolutionStringFloatIntEvent.class, this::onScaledResolutionStringFloatIntEvent);
      this.setEnabledByDefault(true);
      this.setShowLabelInList(false);
   }

   public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) {
      if (this.isRenderHud()) {
         if (this.intValue <= 0) {
            this.handleScaledResolution(scaledResolutionEvent7.getScaledResolution(), ChatFormatting.GOLD + "VICTORY!", ChatFormatting.GRAY + "You won the match!", 255);
         }
      }
   }

   @Override
   public void renderPreview(int n, int n2, int n3, int n4) {
      GlStateManager.pushMatrix();
      GlStateManager.scale(2.0F, 2.0F, 2.0F);
      String str = "VICTORY!";
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow(
            str, (int)((float)(n / 2 + 1 + n3 / 4) - Minecraft.getMinecraft().fontRendererObj.getStringWidth(str) / 2), n2 / 2 + n4 / 4 - 21, -22016
         );
      GlStateManager.popMatrix();
      String str2 = "You won the match!";
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow(
            str2, (int)(n + 1 + n3 / 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(str2) / 2.0F), n2 + n4 / 2 - 17, -5592406
         );
   }
}
