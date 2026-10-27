package com.cheatbreaker.client.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class AbstractFontRenderer {
   public final float TEXTURE_SIZE = 1048.0F;
   public Glyph[] glyphs = new Glyph[256];
   public int lineHeight = -1;
   public int charSpacing = 0;
   public Font font;
   public boolean antialiasing;
   public boolean fractionalMetrics;
   public DynamicTexture dynamicTexture;

   public AbstractFontRenderer(ResourceLocation resourceLocation, float f) {
      Font font;
      try {
         InputStream inputStream = Minecraft.getMinecraft() != null && Minecraft.getMinecraft().getResourceManager() != null
            ? Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream()
            : Minecraft.getMinecraft().getMcDefaultResourcePack().getResourceStream(resourceLocation);
         font = Font.createFont(0, inputStream).deriveFont(f);
      } catch (Exception e) {
         font = new Font("Arial", 0, (int)f);
      }

      this.font = font;
      this.antialiasing = true;
      this.fractionalMetrics = true;
      this.dynamicTexture = this.createTexture(this.font, this.antialiasing, this.fractionalMetrics, this.glyphs);
   }

   public void setFont(Font font) {
      this.font = font;
      this.dynamicTexture = this.createTexture(font, this.antialiasing, this.fractionalMetrics, this.glyphs);
   }

   public int getStringWidth(String str) {
      int n = 0;

      for (char c : str.toCharArray()) {
         if (c < this.glyphs.length && c >= 0) {
            n += this.glyphs[c].width - 8 + this.charSpacing;
         }
      }

      return n / 2;
   }

   public int getLineHeight() {
      return (this.lineHeight - 8) / 2;
   }

   public void drawQuad(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
      float f9 = f5 / 1048.0F;
      float f10 = f6 / 1048.0F;
      float f11 = f7 / 1048.0F;
      float f12 = f8 / 1048.0F;
      GL11.glTexCoord2f(f9 + f11, f10);
      GL11.glVertex2d(f + f3, f2);
      GL11.glTexCoord2f(f9, f10);
      GL11.glVertex2d(f, f2);
      GL11.glTexCoord2f(f9, f10 + f12);
      GL11.glVertex2d(f, f2 + f4);
      GL11.glTexCoord2f(f9, f10 + f12);
      GL11.glVertex2d(f, f2 + f4);
      GL11.glTexCoord2f(f9 + f11, f10 + f12);
      GL11.glVertex2d(f + f3, f2 + f4);
      GL11.glTexCoord2f(f9 + f11, f10);
      GL11.glVertex2d(f + f3, f2);
   }

   public DynamicTexture createTexture(Font font, boolean flag, boolean flag2, Glyph[] glyphs) {
      BufferedImage renderGlyphSheet = this.renderGlyphSheet(font, flag, flag2, glyphs);

      try {
         return new DynamicTexture(renderGlyphSheet);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public void setAntialiasing(boolean flag) {
      if (this.antialiasing != flag) {
         this.antialiasing = flag;
         this.dynamicTexture = this.createTexture(this.font, flag, this.fractionalMetrics, this.glyphs);
      }
   }

   public void drawGlyph(Glyph[] glyphs, char c, float f, float f2) {
      try {
         this.drawQuad(f, f2, glyphs[c].width, glyphs[c].height, glyphs[c].textureX, glyphs[c].textureY, glyphs[c].width, glyphs[c].height);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public Font getFont() {
      return this.font;
   }

   public BufferedImage renderGlyphSheet(Font font, boolean flag, boolean flag2, Glyph[] glyphs) {
      short s = 1048;
      BufferedImage bufferedImage = new BufferedImage(s, s, 2);
      Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
      graphics.setFont(font);
      graphics.setColor(new Color(255, 255, 255, 0));
      graphics.fillRect(0, 0, s, s);
      graphics.setColor(Color.WHITE);
      graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, flag2 ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
      graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, flag ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, flag ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
      FontMetrics fontMetrics = graphics.getFontMetrics();
      int n = 0;
      int n2 = 0;
      int n3 = 1;

      for (int i = 0; i < glyphs.length; i++) {
         char c = (char)i;
         Glyph glyph = new Glyph(this);
         Rectangle2D stringBounds = fontMetrics.getStringBounds(String.valueOf(c), graphics);
         glyph.width = stringBounds.getBounds().width + 8;
         glyph.height = stringBounds.getBounds().height;
         if (n2 + glyph.width >= s) {
            n2 = 0;
            n3 += n;
            n = 0;
         }

         if (glyph.height > n) {
            n = glyph.height;
         }

         glyph.textureX = n2;
         glyph.textureY = n3;
         if (glyph.height > this.lineHeight) {
            this.lineHeight = glyph.height;
         }

         glyphs[i] = glyph;
         graphics.drawString(String.valueOf(c), n2 + 2, n3 + fontMetrics.getAscent());
         n2 += glyph.width;
      }

      return bufferedImage;
   }

   public void setFractionalMetrics(boolean flag) {
      if (this.fractionalMetrics != flag) {
         this.fractionalMetrics = flag;
         this.dynamicTexture = this.createTexture(this.font, this.antialiasing, flag, this.glyphs);
      }
   }
}
