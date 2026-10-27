package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.emote.EmoteWheelEntry;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.EmoteWheelScreen;
import com.cheatbreaker.client.util.FastTrig;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class EmoteWheelButton extends UIElement {
   public Color color = new Color(0.3F, 0.3F, 0.3F, 0.3F);
   public EmoteWheelScreen emoteWheelScreen;
   public float floatValue;
   public EmoteWheelEntry emoteWheelEntry;
   public ColorAnimation colorAnimation;
   public ColorAnimation colorAnimation2;

   public EmoteWheelButton(EmoteWheelScreen emoteWheelScreen, int n, EmoteWheelEntry emoteWheelEntry) {
      float f = 45.0F;
      this.emoteWheelScreen = emoteWheelScreen;
      this.floatValue = f / 2.0F + n * f;
      this.emoteWheelEntry = emoteWheelEntry;
      this.colorAnimation = new ColorAnimation(
         500L, new Color(0.2F, 0.2F, 0.2F, 0.1F).getRGB(), CheatBreaker.getInstance().getKeybindSettings().emoteRadialColor.getColorValue()
      );
      this.colorAnimation2 = new ColorAnimation(500L, new Color(0.0F, 0.0F, 0.0F, 0.9F).getRGB(), -1);
   }

   public boolean check(float f, float f2) {
      float scaledWidth = this.emoteWheelScreen.getScaledWidth();
      float scaledHeight = this.emoteWheelScreen.getScaledHeight();
      float floatValue = this.emoteWheelScreen.getFloatValue();
      int n = (int)(f - scaledWidth / 2.0F);
      int n2 = (int)(f2 - scaledHeight / 2.0F);
      double sqrt = Math.sqrt(n * n + n2 * n2);
      double d = Math.toDegrees(Math.atan2(n2, n)) + 90.0;
      double d2 = this.floatValue - 45.0F;
      double floatValue2 = this.floatValue;
      boolean flag = d < 0.0;
      if (d2 < 0.0) {
         d2 += 360.0;
      }

      if (d2 > floatValue2) {
         floatValue2 += 360.0;
         flag = true;
      }

      if (flag) {
         d += 360.0;
      }

      return sqrt >= 28.0 * floatValue && sqrt <= 245.0 * floatValue && d >= d2 && d <= floatValue2;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      this.colorAnimation.setActiveColor(CheatBreaker.getInstance().getKeybindSettings().emoteRadialColor.getColorValue());
      boolean flag2 = this.emoteWheelEntry != null && this.check(f, f2);
      if (flag2) {
         this.emoteWheelScreen.emoteWheelEntry = this.emoteWheelEntry;
      }

      Color color = this.colorAnimation.getColor(flag2);
      float scaledWidth = this.emoteWheelScreen.getScaledWidth();
      float scaledHeight = this.emoteWheelScreen.getScaledHeight();
      float floatValue = this.emoteWheelScreen.getFloatValue();
      float f3 = 10.0F;
      float f4 = this.emoteWheelScreen.intValue >= f3 ? 1.0F : this.emoteWheelScreen.intValue / f3;
      GlStateManager.pushMatrix();
      if (this.emoteWheelEntry == null) {
         GL11.glColor4d(this.color.getRed() / 255.0F, this.color.getGreen() / 255.0F, this.color.getBlue() / 255.0F, this.color.getAlpha() / 255.0F * f4);
      } else {
         GL11.glColor4d(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F * f4);
      }

      RenderUtils.drawArc(scaledWidth / 2.0F, scaledHeight / 2.0F, 123.2 * floatValue, 28.0 * floatValue, 360.0F - this.floatValue + 90.0F, 360.0F - this.floatValue + 90.0F + 45.0F);
      GlStateManager.popMatrix();
      if (this.emoteWheelEntry != null) {
         color = this.colorAnimation2.getColor(flag2);
         GlStateManager.pushMatrix();
         float f5 = this.floatValue - 90.0F;
         f5 -= 22.5F;
         double toRadians = Math.toRadians(f5);
         float f6 = 84.0F * floatValue;
         float f7 = 28.0F * floatValue;
         double d = scaledWidth / 2.0F + f6 * FastTrig.cos(toRadians);
         double d2 = scaledHeight / 2.0F + f6 * FastTrig.sin(toRadians);
         GL11.glColor4d(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F * f4);
         RenderUtils.drawSquareTextureViaRenderEngine(this.emoteWheelEntry.getResourceLocation(), f7, (float)d - f7, (float)d2 - f7);
         GlStateManager.popMatrix();
      }
   }
}
