package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.font.CustomFontRenderer;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class InitializationScreen extends UIScreen {
   public ResourceLocation logo108Png = new ResourceLocation("client/logo_108.png");
   public CustomFontRenderer customFontRenderer = new CustomFontRenderer(new ResourceLocation("client/font/Ubuntu-M.ttf"), 14.0F);
   public Logger logger = LogManager.getLogger("CheatBreaker Initialization");
   public int intValue;
   public String text;
   public int intValue2;
   public Framebuffer framebuffer;

   public void render() {
      int scaleFactor = this.scaledResolution.getScaleFactor();
      GL11.glDisable(2896);
      GL11.glDisable(2912);
      this.framebuffer.unbindFramebuffer();
      this.framebuffer.framebufferRender(this.scaledResolution.getScaledWidth() * scaleFactor, this.scaledResolution.getScaledHeight() * scaleFactor);
      GL11.glAlphaFunc(516, 0.1F);
      GL11.glFlush();
   }

   @Override
   public void mouseClickedScaled(float f, float f2, int n) {
   }

   public void render2() {
      try {
         this.framebuffer.bindFramebuffer(false);
         GL11.glMatrixMode(5889);
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0, this.scaledResolution.getScaledWidth(), this.scaledResolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
         GL11.glMatrixMode(5888);
         GL11.glLoadIdentity();
         GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
         GL11.glDisable(2896);
         GL11.glDisable(2912);
         GL11.glDisable(2929);
         GL11.glEnable(3553);
         GL11.glEnable(3008);
      } catch (RuntimeException e) {
      }
   }

   public void process3(float f, float f2) {
      float f3 = 27.0F;
      float f4 = f / 2.0F - f3;
      float f5 = f2 / 2.0F - f3;
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtils.drawSquareTexture(this.logo108Png, f3, f4, f5);
   }

   public InitializationScreen(int n) {
      this.intValue2 = n;
      this.mc = Minecraft.getMinecraft();
      this.scaledResolution = new ScaledResolution(this.mc);
      this.framebuffer = new Framebuffer(
         this.scaledResolution.getScaledWidth() * this.scaledResolution.getScaleFactor(),
         this.scaledResolution.getScaledHeight() * this.scaledResolution.getScaleFactor(),
         true
      );
   }

   public void handleString2(String str) {
      if (this.text != null) {
         this.logger.info("[PHASE] {} (Completed) -> {}", this.text, str);
      } else {
         this.logger.info("[PHASE] {}", str);
      }

      this.text = str;
      this.run11();
   }

   @Override
   public void mouseReleasedScaled(float f, float f2, int n) {
   }

   @Override
   public void drawScreenScaled(float f, float f2) {
      this.render2();
      float uiScale = this.getUiScale();
      float f3 = this.scaledResolution.getScaledWidth() / uiScale;
      float f4 = this.scaledResolution.getScaledHeight() / uiScale;
      GL11.glScaled(uiScale, uiScale, uiScale);
      Gui.drawRect(0.0F, 0.0F, f3, f4, -15987700);
      this.process3(f3, f4);
      float f5 = 160.0F;
      float f6 = f3 / 2.0F - 80.0F;
      float f7 = f4 - 40.0F;
      RenderUtils.drawRoundedRectFilledPrecise(f6, f7, f6 + f5, f7 + 10.0F, 8.0, -15395563);
      float f8 = f5 * ((float)this.intValue / this.intValue2);
      if (this.text != null) {
         this.customFontRenderer.drawCenteredString(this.text.toLowerCase(), f3 / 2.0F, f7 - 11.0F, -3092272);
      }

      RenderUtils.drawRoundedRectFilledPrecise(f6, f7, f6 + Math.max(f8, 8.0F), f7 + 10.0F, 8.0, -2473389);
      this.render();
      this.mc.updateDisplay();
   }

   public void run11() {
      this.intValue++;
      if (this.intValue2 <= this.intValue) {
         this.intValue2 = this.intValue + 1;
      }

      this.drawScreenScaled(0.0F, 0.0F);
   }
}
