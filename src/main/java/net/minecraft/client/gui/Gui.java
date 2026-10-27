package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Gui {
   public static ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
   public static ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
   public static float zLevel;

   public void drawHorizontalLine(int startX, int endX, int y, int color) {
      if (endX < startX) {
         int startX2 = startX;
         startX = endX;
         endX = startX2;
      }

      drawRect(startX, y, endX + 1, y + 1, color);
   }

   public void drawVerticalLine(int x, int startY, int endY, int color) {
      if (endY < startY) {
         int startY2 = startY;
         startY = endY;
         endY = startY2;
      }

      drawRect(x, startY + 1, x + 1, endY, color);
   }

   public static void drawRect(int left, int top, int right, int bottom, int color) {
      if (left < right) {
         int left2 = left;
         left = right;
         right = left2;
      }

      if (top < bottom) {
         int top2 = top;
         top = bottom;
         bottom = top2;
      }

      float f = (color >> 24 & 0xFF) / 255.0F;
      float f2 = (color >> 16 & 0xFF) / 255.0F;
      float f3 = (color >> 8 & 0xFF) / 255.0F;
      float f4 = (color & 0xFF) / 255.0F;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(f2, f3, f4, f);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(left, bottom, 0.0).color(f2, f3, f4, f).endVertex();
      worldRenderer.pos(right, bottom, 0.0).color(f2, f3, f4, f).endVertex();
      worldRenderer.pos(right, top, 0.0).color(f2, f3, f4, f).endVertex();
      worldRenderer.pos(left, top, 0.0).color(f2, f3, f4, f).endVertex();
      instance.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      float f = (startColor >> 24 & 0xFF) / 255.0F;
      float f2 = (startColor >> 16 & 0xFF) / 255.0F;
      float f3 = (startColor >> 8 & 0xFF) / 255.0F;
      float f4 = (startColor & 0xFF) / 255.0F;
      float f5 = (endColor >> 24 & 0xFF) / 255.0F;
      float f6 = (endColor >> 16 & 0xFF) / 255.0F;
      float f7 = (endColor >> 8 & 0xFF) / 255.0F;
      float f8 = (endColor & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(right, top, zLevel).color(f2, f3, f4, f).endVertex();
      worldRenderer.pos(left, top, zLevel).color(f2, f3, f4, f).endVertex();
      worldRenderer.pos(left, bottom, zLevel).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(right, bottom, zLevel).color(f6, f7, f8, f5).endVertex();
      instance.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
   }

   public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, x, y, color);
   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      float f = 0.00390625F;
      float f2 = 0.00390625F;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(x + 0, y + height, zLevel).tex((textureX + 0) * f, (textureY + height) * f2).endVertex();
      worldRenderer.pos(x + width, y + height, zLevel).tex((textureX + width) * f, (textureY + height) * f2).endVertex();
      worldRenderer.pos(x + width, y + 0, zLevel).tex((textureX + width) * f, (textureY + 0) * f2).endVertex();
      worldRenderer.pos(x + 0, y + 0, zLevel).tex((textureX + 0) * f, (textureY + 0) * f2).endVertex();
      instance.draw();
   }

   public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
      float f = 0.00390625F;
      float f2 = 0.00390625F;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(xCoord + 0.0F, yCoord + maxV, zLevel).tex((minU + 0) * f, (minV + maxV) * f2).endVertex();
      worldRenderer.pos(xCoord + maxU, yCoord + maxV, zLevel).tex((minU + maxU) * f, (minV + maxV) * f2).endVertex();
      worldRenderer.pos(xCoord + maxU, yCoord + 0.0F, zLevel).tex((minU + maxU) * f, (minV + 0) * f2).endVertex();
      worldRenderer.pos(xCoord + 0.0F, yCoord + 0.0F, zLevel).tex((minU + 0) * f, (minV + 0) * f2).endVertex();
      instance.draw();
   }

   public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(xCoord + 0, yCoord + heightIn, zLevel).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
      worldRenderer.pos(xCoord + widthIn, yCoord + heightIn, zLevel).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
      worldRenderer.pos(xCoord + widthIn, yCoord + 0, zLevel).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
      worldRenderer.pos(xCoord + 0, yCoord + 0, zLevel).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
      instance.draw();
   }

   public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
      float f = 1.0F / textureWidth;
      float f2 = 1.0F / textureHeight;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(x, y + height, 0.0).tex(u * f, (v + height) * f2).endVertex();
      worldRenderer.pos(x + width, y + height, 0.0).tex((u + width) * f, (v + height) * f2).endVertex();
      worldRenderer.pos(x + width, y, 0.0).tex((u + width) * f, v * f2).endVertex();
      worldRenderer.pos(x, y, 0.0).tex(u * f, v * f2).endVertex();
      instance.draw();
   }

   public static void drawScaledCustomSizeModalRect(
      int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight
   ) {
      float f = 1.0F / tileWidth;
      float f2 = 1.0F / tileHeight;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(x, y + height, 0.0).tex(u * f, (v + vHeight) * f2).endVertex();
      worldRenderer.pos(x + width, y + height, 0.0).tex((u + uWidth) * f, (v + vHeight) * f2).endVertex();
      worldRenderer.pos(x + width, y, 0.0).tex((u + uWidth) * f, v * f2).endVertex();
      worldRenderer.pos(x, y, 0.0).tex(u * f, v * f2).endVertex();
      instance.draw();
   }

   public static void drawRect(float f, float f2, float f3, float f4, int n) {
      if (f < f3) {
         float f5 = f;
         f = f3;
         f3 = f5;
      }

      if (f2 < f4) {
         float f5 = f2;
         f2 = f4;
         f4 = f5;
      }

      float f5 = (n >> 24 & 0xFF) / 255.0F;
      float f6 = (n >> 16 & 0xFF) / 255.0F;
      float f7 = (n >> 8 & 0xFF) / 255.0F;
      float f8 = (n & 0xFF) / 255.0F;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.disableCull();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(f, f4, 0.0).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(f3, f4, 0.0).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(f3, f2, 0.0).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(f, f2, 0.0).color(f6, f7, f8, f5).endVertex();
      instance.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawRectWithOuterBorder(float f, float f2, float f3, float f4, float f5, int n, int n2) {
      drawRect(f, f2, f3, f4, n2);
      drawRect(f - f5, f2 - f5, f, f4 + f5, n);
      drawRect(f3, f2 - f5, f3 + f5, f4 + f5, n);
      drawRect(f, f2 - f5, f3, f2, n);
      drawRect(f, f4, f3, f4 + f5, n);
   }

   public static void drawInnerBorder(float f, float f2, float f3, float f4, float f5, int n) {
      drawRect(f, f2 + f5, f + f5, f4 - f5, n);
      drawRect(f3 - f5, f2 + f5, f3, f4 - f5, n);
      drawRect(f, f2, f3, f2 + f5, n);
      drawRect(f, f4 - f5, f3, f4, n);
   }

   public static void drawRectWithInnerBorder(float f, float f2, float f3, float f4, float f5, int n, int n2) {
      drawRect(f + f5, f2 + f5, f3 - f5, f4 - f5, n2);
      drawRect(f, f2 + f5, f + f5, f4 - f5, n);
      drawRect(f3 - f5, f2 + f5, f3, f4 - f5, n);
      drawRect(f, f2, f3, f2 + f5, n);
      drawRect(f, f4 - f5, f3, f4, n);
   }

   public void drawGradientRect(float f, float f2, float f3, float f4, int n, int n2) {
      float f5 = (n >> 24 & 0xFF) / 255.0F;
      float f6 = (n >> 16 & 0xFF) / 255.0F;
      float f7 = (n >> 8 & 0xFF) / 255.0F;
      float f8 = (n & 0xFF) / 255.0F;
      float f9 = (n2 >> 24 & 0xFF) / 255.0F;
      float f10 = (n2 >> 16 & 0xFF) / 255.0F;
      float f11 = (n2 >> 8 & 0xFF) / 255.0F;
      float f12 = (n2 & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(f3, f2, zLevel).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(f, f2, zLevel).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(f, f4, zLevel).color(f10, f11, f12, f9).endVertex();
      worldRenderer.pos(f3, f4, zLevel).color(f10, f11, f12, f9).endVertex();
      instance.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }
}
