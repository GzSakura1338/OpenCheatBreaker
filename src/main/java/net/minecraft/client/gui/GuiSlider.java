package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiSlider extends GuiButton {
   public float sliderPosition = 1.0F;
   public boolean isMouseDown;
   public String name;
   public float min;
   public float max;
   public GuiSlider.FormatHelper formatHelper;
   public GuiPageButtonList.GuiResponder responder;

   public GuiSlider(
      GuiPageButtonList.GuiResponder guiResponder,
      int idIn,
      int x,
      int y,
      String name,
      float min,
      float max,
      float defaultValue,
      GuiSlider.FormatHelper formatter
   ) {
      super(idIn, x, y, 150, 20, "");
      this.name = name;
      this.min = min;
      this.max = max;
      this.sliderPosition = (defaultValue - min) / (max - min);
      this.formatHelper = formatter;
      this.responder = guiResponder;
      this.displayString = this.getDisplayString();
   }

   public float getSliderValue() {
      return this.min + (this.max - this.min) * this.sliderPosition;
   }

   public void setSliderValue(float f, boolean flag) {
      this.sliderPosition = (f - this.min) / (this.max - this.min);
      this.displayString = this.getDisplayString();
      if (flag) {
         this.responder.onTick(this.id, this.getSliderValue());
      }
   }

   public float getProgress() {
      return this.sliderPosition;
   }

   public String getDisplayString() {
      return this.formatHelper == null
         ? I18n.format(this.name) + ": " + this.getSliderValue()
         : this.formatHelper.getText(this.id, I18n.format(this.name), this.getSliderValue());
   }

   @Override
   public int getHoverState(boolean mouseOver) {
      return 0;
   }

   @Override
   public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         if (this.isMouseDown) {
            this.sliderPosition = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
            if (this.sliderPosition < 0.0F) {
               this.sliderPosition = 0.0F;
            }

            if (this.sliderPosition > 1.0F) {
               this.sliderPosition = 1.0F;
            }

            this.displayString = this.getDisplayString();
            this.responder.onTick(this.id, this.getSliderValue());
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.xPosition + (int)(this.sliderPosition * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
         this.drawTexturedModalRect(this.xPosition + (int)(this.sliderPosition * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
      }
   }

   public void setSliderProgress(float f) {
      this.sliderPosition = f;
      this.displayString = this.getDisplayString();
      this.responder.onTick(this.id, this.getSliderValue());
   }

   @Override
   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         this.sliderPosition = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
         if (this.sliderPosition < 0.0F) {
            this.sliderPosition = 0.0F;
         }

         if (this.sliderPosition > 1.0F) {
            this.sliderPosition = 1.0F;
         }

         this.displayString = this.getDisplayString();
         this.responder.onTick(this.id, this.getSliderValue());
         this.isMouseDown = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY) {
      this.isMouseDown = false;
   }

   public interface FormatHelper {
      String getText(int n, String str, float f);
   }
}
