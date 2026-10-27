package com.cheatbreaker.client.minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class SliderButton extends GuiButton {
   public float value = 1.0F;
   public boolean dragging = false;
   public MinimapOption minimapOption = null;
   public MinimapSettings minimapSettings;

   @Override
   public int getHoverState(boolean flag) {
      return 0;
   }

   @Override
   public void mouseDragged(Minecraft mc, int n, int n2) {
      if (this.visible) {
         if (this.dragging) {
            this.value = (n - (this.xPosition + 4)) / (this.width - 8);
            if (this.value < 0.0F) {
               this.value = 0.0F;
            }

            if (this.value > 1.0F) {
               this.value = 1.0F;
            }

            this.minimapSettings.setFloatOption(this.minimapOption, this.value);
            this.displayString = this.minimapSettings.getOptionLabel(this.minimapOption);
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.xPosition + (int)(this.value * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
         this.drawTexturedModalRect(this.xPosition + (int)(this.value * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
      }
   }

   public SliderButton(int n, int n2, int n3, MinimapOption minimapOption, float f, MinimapSettings minimapSettings) {
      super(n, n2, n3, 150, 20, "");
      this.minimapSettings = minimapSettings;
      this.minimapOption = minimapOption;
      this.value = f;
      this.displayString = this.minimapSettings.getOptionLabel(minimapOption);
   }

   @Override
   public boolean mousePressed(Minecraft mc, int n, int n2) {
      if (super.mousePressed(mc, n, n2)) {
         this.value = (n - (this.xPosition + 4)) / (this.width - 8);
         if (this.value < 0.0F) {
            this.value = 0.0F;
         }

         if (this.value > 1.0F) {
            this.value = 1.0F;
         }

         this.minimapSettings.setFloatOption(this.minimapOption, this.value);
         this.displayString = this.minimapSettings.getOptionLabel(this.minimapOption);
         this.dragging = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void mouseReleased(int n, int n2) {
      this.dragging = false;
   }
}
