package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ModEntry extends SettingComponent {
   public ResourceLocation rightPng = new ResourceLocation("client/icons/right.png");
   public int intValue = 0;
   public Panel panel;
   public int intValue2;
   public AbstractMod abstractMod;

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      double d = this.height - 10;
      double d2 = d / this.panel.intValue2 * 100.0;
      double d3 = d / 100.0 * d2;
      double d4 = this.panel.intValue / 100.0 * d2;
      boolean flag = n > (this.x + this.width - 9) * this.scale
         && n < (this.x + this.width - 3) * this.scale
         && n2 > (this.y + 11 - d4) * this.scale
         && n2 < (this.y + 8 + d3 - d4) * this.scale;
      boolean flag2 = n > (this.x + this.width - 9) * this.scale
         && n < (this.x + this.width - 3) * this.scale
         && n2 > (this.y + 11) * this.scale
         && n2 < (this.y + 6 + d - 3.0) * this.scale;
      if (n3 == 0 && flag2 || flag) {
         this.panel.flag2 = true;
      }

      this.panel.selectMod(this.abstractMod);
   }

   @Override
   public void render(int n, int n2, float f) {
      boolean isMouseOver = this.isMouseOver(n, n2);
      byte b = 75;
      Gui.drawRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, this.theme.getPrimaryTextColorValue2());
      if (this.panel.hasSettings(this.abstractMod)) {
         if (isMouseOver) {
            float animationStep = ModsScreen.animationStep(790.0F);
            if (this.intValue + animationStep < b) {
               this.intValue = (int)(this.intValue + animationStep);
               if (this.intValue > b) {
                  this.intValue = b;
               }
            }
         } else if (this.intValue > 0) {
            float animationStep = ModsScreen.animationStep(790.0F);
            this.intValue = this.intValue - animationStep < 0.0F ? 0 : (int)(this.intValue - animationStep);
         }

         if (this.intValue > 0) {
            float f2 = (float)this.intValue / b * 100.0F;
            Gui.drawRect(this.x, (int)(this.y + (this.height - this.height * f2 / 100.0F)), this.x + this.width, this.y + this.height, this.intValue2);
         }
      }

      float red = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
      float green = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
      float blue = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
      GlStateManager.color(red, green, blue, 0.35F);
      RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 2.5F, this.x + 6, this.y + 6.0F);
      CheatBreaker.getInstance()
         .getFontManager()
         .playBold18
         .drawString(this.abstractMod.getName().toUpperCase(), this.x + 14.0F, this.y + 3.0F, this.theme.getTextColor2Value2());
   }

   public ModEntry(Panel panel, int n, AbstractMod abstractMod, float f) {
      super(f);
      this.panel = panel;
      this.intValue2 = n;
      this.abstractMod = abstractMod;
   }
}
