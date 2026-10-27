package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.animation.EaseInOutAnimation;
import net.minecraft.client.gui.Gui;

public class HoverableElement extends UIElement {
   public ColorAnimation colorAnimation = new ColorAnimation(520093696, 1056964608);
   public ColorAnimation colorAnimation2 = new ColorAnimation(0, -1073741825);
   public boolean flag;
   public EaseInOutAnimation easeInOutAnimation = new EaseInOutAnimation(750L);

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      return !flag ? false : false;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.colorAnimation.getColor(this.containsPoint(f, f2) && flag).getRGB());
   }
}
