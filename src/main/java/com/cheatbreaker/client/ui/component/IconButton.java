package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class IconButton extends UIElement {
   public boolean flag = true;
   public float floatValue = 4.0F;
   public float floatValue2 = 0.8F;
   public String text;
   public boolean flag2;
   public ResourceLocation resourceLocation;
   public ColorAnimation colorAnimation;
   public ColorAnimation colorAnimation2;
   public ColorAnimation colorAnimation3;

   public void setText(String str) {
      this.text = str;
   }

   public void setResourceLocation(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      if (this.flag) {
         boolean flag2 = flag && this.containsPoint(f, f2) && this.enabled;
         int n = this.enabled ? this.colorAnimation.getColor(flag2).getRGB() : this.colorAnimation.getColor(false).getRGB() / 2;
         int n2 = this.enabled ? this.colorAnimation2.getColor(flag2).getRGB() : this.colorAnimation2.getColor(false).getRGB() / 2;
         int n3 = this.enabled ? this.colorAnimation3.getColor(flag2).getRGB() : this.colorAnimation3.getColor(false).getRGB() / 2;
         RenderUtils.drawBorderedGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, n, n2, n3);
      }

      if (this.flag2) {
         float f3 = this.x + this.width / 2.0F;
         float f4 = this.y + 2.0F;
         CheatBreaker.getInstance().getFontManager().robotoRegular13.drawCenteredString(this.text, f3, f4, -1);
      } else {
         float f3 = this.enabled ? 1.0F : 0.7F;
         GlStateManager.color(f3, f3, f3, this.floatValue2);
         RenderUtils.drawSquareTextureViaRenderEngine(
            this.resourceLocation, this.floatValue, this.x + this.width / 2.0F - this.floatValue, this.y + this.height / 2.0F - this.floatValue
         );
      }
   }

   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   public float getFloatValue() {
      return 22 + CheatBreaker.getInstance().getFontManager().robotoRegular13.getStringWidth(this.text) + 6;
   }

   public IconButton(float f, ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
      this.floatValue = f;
      this.colorAnimation = new ColorAnimation(1342177279, -1353670564);
      this.colorAnimation2 = new ColorAnimation(444958085, 1063565678);
      this.colorAnimation3 = new ColorAnimation(444958085, 1062577506);
   }

   public void setFloatValue2(float f) {
      this.floatValue2 = f;
   }

   public IconButton(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
      this.floatValue = 4.0F;
      this.colorAnimation = new ColorAnimation(1342177279, -1353670564);
      this.colorAnimation2 = new ColorAnimation(444958085, 1063565678);
      this.colorAnimation3 = new ColorAnimation(444958085, 1062577506);
   }

   public IconButton(String str) {
      this.text = str;
      this.flag2 = true;
      this.colorAnimation = new ColorAnimation(1342177279, -1353670564);
      this.colorAnimation2 = new ColorAnimation(444958085, 1063565678);
      this.colorAnimation3 = new ColorAnimation(444958085, 1062577506);
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      return !flag ? false : false;
   }
}
