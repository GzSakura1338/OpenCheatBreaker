package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class SettingsNavigationItem extends SettingComponent {
   public ResourceLocation rightPng = new ResourceLocation("client/icons/right.png");
   public int intValue = 0;
   public String text;
   public int intValue2;

   @Override
   public void render(int n, int n2, float f) {
      boolean isMouseOver = this.isMouseOver(n, n2);
      byte b = 75;
      Gui.drawRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, this.theme.getPrimaryTextColorValue2());
      float animationStep = ModsScreen.animationStep(790.0F);
      if (isMouseOver) {
         if (this.intValue < b) {
            this.intValue = (int)(this.intValue + animationStep);
            if (this.intValue > b) {
               this.intValue = b;
            }
         }
      } else if (this.intValue > 0) {
         this.intValue = this.intValue - animationStep < 0.0F ? 0 : (int)(this.intValue - animationStep);
      }

      if (this.intValue > 0) {
         float f2 = (float)this.intValue / b * 100.0F;
         Gui.drawRect(this.x, (int)(this.y + (this.height - this.height * f2 / 100.0F)), this.x + this.width, this.y + this.height, this.intValue2);
      }

      float red = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
      float green = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
      float blue = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
      GlStateManager.color(red, green, blue, 0.35F);
      RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 2.5F, this.x + 6, this.y + 6.0F);
      CheatBreaker.getInstance()
         .getFontManager()
         .playBold18
         .drawString(this.text.toUpperCase(), this.x + 14.0F, this.y + 3.0F, this.theme.getTextColor2Value2());
   }

   public SettingsNavigationItem(String str, int n, float f) {
      super(f);
      this.text = str;
      this.intValue2 = n;
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
   }
}
