package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class MenuTextButton extends UIElement {
   public boolean clickable = true;
   public String label;
   public boolean selected;

   public String getLabel() {
      return this.label;
   }

   public MenuTextButton(String str, boolean flag) {
      this.label = str;
      this.selected = flag;
   }

   @Override
   public boolean containsPoint(float f, float f2) {
      return this.clickable && super.containsPoint(f, f2);
   }

   @Override
   public void setEnabled(boolean flag) {
      this.clickable = flag;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      boolean containsPoint = this.containsPoint(f, f2);
      if (this.selected) {
         Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.clickable ? (containsPoint ? -15395563 : -14540254) : -1356717534);
      }

      int n = -3092272;
      if (containsPoint) {
         n = -1;
      }

      float f3 = this.x + this.width / 2.0F;
      CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString(this.label, f3, this.y + this.height / 2.0F - 5.0F, n);
   }
}
