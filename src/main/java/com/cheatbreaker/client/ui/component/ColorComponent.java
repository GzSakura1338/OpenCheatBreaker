package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.render.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class ColorComponent extends SettingControl {
   public float pickerBottom;
   public boolean expanded = false;
   public boolean draggingColorArea = false;
   public boolean draggingHueSlider = false;
   public boolean draggingAlphaSlider = false;
   public int recentX;
   public int favoritesY;
   public float pickerHeight;
   public float pickerLeft;
   public float pickerTop;
   public int favoritesX;
   public int recentY;
   public float pickerRight;
   public float hue = 1.0F;
   public float alphaPosition = 1.0F;
   public ColorSwatch colorSwatch;
   public float pickerWidth;
   public List<ColorSwatch> vanillaSwatches;

   public void saveToRecent() {
      if (CheatBreaker.getInstance().getKeybindSettings().recentColors.size() >= 16) {
         CheatBreaker.getInstance().getKeybindSettings().recentColors.remove(0);
      }

      CheatBreaker.getInstance().getKeybindSettings().recentColors.add(new ColorSwatch(this.scale, (Integer)this.setting.getValue(), 1.0F));
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
   }

   public void renderSwatches(List<ColorSwatch> list, int n, int n2, int n3, int n4, int n5) {
      int n6 = 0;
      int n7 = 0;
      byte b = 8;

      for (ColorSwatch colorSwatch : list) {
         colorSwatch.scale = this.scale;
         if (n6 == b) {
            n7++;
            n6 = 0;
         }

         if (list == this.vanillaSwatches) {
            int n8 = b * 2 / 8 * 12;
            int n9 = n + n8 - n7 * 12 - 12;
            int n10 = n2 + n6 * n8 - n6 * 12;
            colorSwatch.scrollOffsetY = this.scrollOffsetY;
            colorSwatch.setBounds(n9, n10, 10, 10);
            String str = "0123456789abcdefklmnor";
            int n11 = n6 + n7 * b;
            String substring = str.substring(n11, n11 + 1);
            if (colorSwatch.isMouseOver(n3, n4)) {
               Gui.drawRect(n9 + 12, n10 - 1, n9 + 26, n10 + 11, -1087492562);
               CheatBreaker.getInstance().getFontManager().ubuntuM16.drawStringWithOutline("&" + substring, n9 + 14, n10, -1);
            }
         } else {
            int n8 = n + n7 * 12;
            int n9 = n2 + n6 * 12;
            colorSwatch.scrollOffsetY = this.scrollOffsetY;
            colorSwatch.setBounds(n8, n9, 10, 10);
         }

         colorSwatch.render(n3, n4, n5);
         n6++;
      }
   }

   public void clickSwatches(List<ColorSwatch> list, int n, int n2, int n3, int n4) {
      int n5 = 0;
      int n6 = 0;
      byte b = 8;

      for (ColorSwatch colorSwatch : list) {
         if (list != this.vanillaSwatches) {
            if (n5 == b) {
               n6++;
               n5 = 0;
            }

            int n7 = n + n6 * 12;
            int n8 = n2 + n5 * 12;
            colorSwatch.scrollOffsetY = this.scrollOffsetY;
            colorSwatch.setBounds(n7, n8, 10, 10);
         }

         if (colorSwatch.isMouseOver(n3, n4)) {
            int color = colorSwatch.color;
            if (list == this.vanillaSwatches) {
               this.setting.changeValue(new Color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, this.setting.getIntValue() >> 24 & 0xFF).getRGB());
            } else {
               this.setting.changeValue(new Color(colorSwatch.color, true).getRGB());
            }

            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            float[] floats = Color.RGBtoHSB(colorSwatch.color >> 16 & 0xFF, colorSwatch.color >> 8 & 0xFF, colorSwatch.color & 0xFF, null);
            this.hue = floats[0];
            int n7 = (int)(floats[1] * this.pickerWidth);
            int n8 = (int)(this.pickerHeight - floats[2] * this.pickerHeight);
            this.setting.color = new int[]{n7, n8};
         }

         n5++;
      }
   }

   public void drawCheckerboard() {
      boolean flag = true;

      for (int i = 2; i < this.pickerHeight - 4.0F; i += 4) {
         if (!flag) {
            Gui.drawRect(
               this.pickerLeft + this.pickerWidth + 19.0F, this.pickerTop + i, this.pickerLeft + this.pickerWidth + 23.0F, this.pickerTop + i + 4.0F, -1
            );
            Gui.drawRect(
               this.pickerLeft + this.pickerWidth + 23.0F,
               this.pickerTop + i + 4.0F,
               this.pickerLeft + this.pickerWidth + 27.0F,
               this.pickerTop + i + 8.0F,
               -1
            );
            Gui.drawRect(
               this.pickerLeft + this.pickerWidth + 23.0F,
               this.pickerTop + i,
               this.pickerLeft + this.pickerWidth + 27.0F,
               this.pickerTop + i + 4.0F,
               -7303024
            );
            Gui.drawRect(
               this.pickerLeft + this.pickerWidth + 19.0F,
               this.pickerTop + i + 4.0F,
               this.pickerLeft + this.pickerWidth + 23.0F,
               this.pickerTop + i + 8.0F,
               -7303024
            );
         }

         flag = !flag;
      }
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      if (!this.isHidden(this.setting)) {
         boolean flag = n > (this.x + this.width - 40) * this.scale
            && n < (this.x + this.width - 12) * this.scale
            && n2 > (this.y + this.scrollOffsetY) * this.scale
            && n2 < (this.y + 18 + this.scrollOffsetY) * this.scale;
         boolean flag2 = n > this.x * this.scale
            && n < (this.x + this.width - 40) * this.scale
            && n2 > (this.y + this.scrollOffsetY) * this.scale
            && n2 < (this.y + 18 + this.scrollOffsetY) * this.scale;
         if (flag2) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            float[] floats = Color.RGBtoHSB(
               (Integer)this.setting.getValue() >> 16 & 0xFF, (Integer)this.setting.getValue() >> 8 & 0xFF, (Integer)this.setting.getValue() & 0xFF, null
            );
            this.hue = floats[0];
            int n4 = (int)(floats[1] * this.pickerWidth);
            int n5 = (int)(this.pickerHeight - floats[2] * this.pickerHeight);
            this.setting.color = new int[]{n4, n5};
            this.expanded = !this.expanded;
         } else if (flag) {
            if (CheatBreaker.getInstance().getKeybindSettings().isColorSaved((Integer)this.setting.getValue())) {
               CheatBreaker.getInstance().getKeybindSettings().removeSavedColor((Integer)this.setting.getValue());
            } else {
               if (CheatBreaker.getInstance().getKeybindSettings().savedColors.size() >= 16) {
                  CheatBreaker.getInstance().getKeybindSettings().savedColors.remove(0);
               }

               CheatBreaker.getInstance().getKeybindSettings().savedColors.add(new ColorSwatch(this.scale, (Integer)this.setting.getValue(), 1.0F));
            }

            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         } else if (this.expanded) {
            this.clickSwatches(this.vanillaSwatches, 0, 0, n, n2);
            this.clickSwatches(CheatBreaker.getInstance().getKeybindSettings().recentColors, this.recentX, this.recentY, n, n2);
            this.clickSwatches(CheatBreaker.getInstance().getKeybindSettings().savedColors, this.favoritesX, this.favoritesY, n, n2);
            boolean flag3 = n > (this.pickerLeft - 51.0F) * this.scale
               && n2 > (this.pickerTop + 1.0F + this.scrollOffsetY) * this.scale
               && n < (this.pickerLeft - 43.0F) * this.scale
               && n2 < (this.pickerTop + 9.0F + this.scrollOffsetY) * this.scale;
            boolean flag4 = n > (this.pickerLeft - 51.0F) * this.scale
               && n2 > (this.pickerTop + 10.0F + this.scrollOffsetY) * this.scale
               && n < (this.pickerLeft - 43.0F) * this.scale
               && n2 < (this.pickerTop + 19.0F + this.scrollOffsetY) * this.scale;
            if (n > this.pickerLeft * this.scale
               && n < (this.pickerLeft + this.pickerWidth) * this.scale
               && n2 > (this.pickerTop + this.scrollOffsetY) * this.scale
               && n2 < (this.pickerTop + this.pickerHeight + this.scrollOffsetY) * this.scale) {
               this.draggingColorArea = true;
            }

            if (n > (this.pickerLeft + this.pickerWidth + 4.0F) * this.scale
               && n < (this.pickerLeft + this.pickerWidth + 14.0F) * this.scale
               && n2 > (this.pickerTop - 1.0F + this.scrollOffsetY) * this.scale
               && n2 < (this.pickerTop + 1.0F + this.pickerHeight + this.scrollOffsetY) * this.scale) {
               this.draggingHueSlider = true;
            }

            if (n > (this.pickerLeft + this.pickerWidth + 18.0F) * this.scale
               && n < (this.pickerLeft + this.pickerWidth + 28.0F) * this.scale
               && n2 > (this.pickerTop - 1.0F + this.scrollOffsetY) * this.scale
               && n2 < (this.pickerTop + 1.0F + this.pickerHeight + this.scrollOffsetY) * this.scale) {
               this.draggingAlphaSlider = true;
            }

            if (flag3) {
               Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
               this.setting.chroma = !this.setting.chroma;
            }

            if (flag4) {
               Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
               this.setting.speed = !this.setting.speed;
            }
         }

         if (this.setting == CheatBreaker.getInstance().getModManager().packTweaksMod.placedStringColor) {
            Minecraft.getMinecraft().renderGlobal.loadRenderers();
         }
      }
   }

   public void drawHueSlider(int n, int n2) {
      Gui.drawRect(
         this.pickerLeft + this.pickerWidth + 4.0F,
         this.pickerTop - 1.0F,
         this.pickerLeft + this.pickerWidth + 14.0F,
         this.pickerTop + 1.0F + this.pickerHeight,
         this.theme.getTextColor5Value()
      );

      for (int i = 0; i < this.pickerHeight; i++) {
         if (this.draggingHueSlider
            && n2 >= (this.scrollOffsetY + this.pickerTop + i) * this.scale
            && n2 <= (this.scrollOffsetY + this.pickerTop + i + 1.0F) * this.scale) {
            int value = (Integer)this.setting.getValue();
            float[] floats = Color.RGBtoHSB(value >> 16 & 0xFF, value >> 8 & 0xFF, value & 0xFF, null);
            int rGB = new Color(
                  Color.HSBtoRGB(this.hue, floats[1], floats[2]) >> 16 & 0xFF,
                  Color.HSBtoRGB(this.hue, floats[1], floats[2]) >> 8 & 0xFF,
                  Color.HSBtoRGB(this.hue, floats[1], floats[2]) & 0xFF,
                  value >> 24 & 0xFF
               )
               .getRGB();
            this.setting.changeValue(rGB);
            this.hue = i / this.pickerHeight;
         }

         int n3 = Color.HSBtoRGB(i / this.pickerHeight, 1.0F, 1.0F);
         Gui.drawRect(
            this.pickerLeft + this.pickerWidth + 5.0F, this.pickerTop + i, this.pickerLeft + this.pickerWidth + 13.0F, this.pickerTop + i + 1.0F, n3
         );
      }

      float f = -1.0F + this.pickerHeight * this.hue;
      Gui.drawRect(
         this.pickerLeft + this.pickerWidth + 4.0F, this.pickerTop + f, this.pickerLeft + this.pickerWidth + 14.0F, this.pickerTop + f + 3.0F, -822083584
      );
      Gui.drawRect(
         this.pickerLeft + this.pickerWidth + 4.0F,
         this.pickerTop + f + 1.0F,
         this.pickerLeft + this.pickerWidth + 14.0F,
         this.pickerTop + f + 2.0F,
         -805306369
      );
   }

   public void drawAlphaSlider(int n, int n2) {
      Gui.drawRect(
         this.pickerLeft + this.pickerWidth + 18.0F,
         this.pickerTop - 1.0F,
         this.pickerLeft + this.pickerWidth + 28.0F,
         this.pickerTop + 1.0F + this.pickerHeight,
         this.theme.getTextColor5Value()
      );
      this.drawCheckerboard();

      for (int i = 0; i < this.pickerHeight; i++) {
         int value = (Integer)this.setting.getValue();
         int rGB = new Color(value >> 16 & 0xFF, value >> 8 & 0xFF, value & 0xFF, Math.round(255.0F - i / this.pickerHeight * 255.0F)).getRGB();
         if (this.draggingAlphaSlider
            && n2 >= (this.scrollOffsetY + this.pickerTop + i) * this.scale
            && n2 <= (this.scrollOffsetY + this.pickerTop + i + 1.0F) * this.scale) {
            this.alphaPosition = i / this.pickerHeight;
            this.setting.changeValue(rGB);
         }

         Gui.drawRect(
            this.pickerLeft + this.pickerWidth + 19.0F, this.pickerTop + i, this.pickerLeft + this.pickerWidth + 27.0F, this.pickerTop + i + 1.0F, rGB
         );
      }

      float f = -1.0F + this.pickerHeight * this.alphaPosition;
      Gui.drawRect(
         this.pickerLeft + this.pickerWidth + 18.0F,
         this.pickerTop + f,
         this.pickerLeft + this.pickerWidth + 28.0F,
         this.pickerTop + f + 3.0F,
         -822083584
      );
      Gui.drawRect(
         this.pickerLeft + this.pickerWidth + 18.0F,
         this.pickerTop + f + 1.0F,
         this.pickerLeft + this.pickerWidth + 28.0F,
         this.pickerTop + f + 2.0F,
         -805306369
      );
   }

   @Override
   public void render(int n, int n2, float f) {
      super.render(n, n2, f);
      this.height = this.expanded ? 130 : 18;
      this.pickerLeft = this.x + 56;
      this.pickerRight = this.x + 176;
      this.pickerTop = this.y + 25;
      this.pickerBottom = this.y + 119;
      this.pickerWidth = this.pickerRight - this.pickerLeft;
      this.pickerHeight = this.pickerBottom - this.pickerTop;
      CheatBreaker.getInstance()
         .getFontManager()
         .ubuntuM16
         .drawString(this.setting.getName().toUpperCase(), this.x + 10, this.y + 4, this.theme.getTextColor2Value());
      this.colorSwatch.color = this.setting.getColorValue();
      this.colorSwatch.setBounds(this.x + 160, this.y + 3, 14, 14);
      this.colorSwatch.scrollOffsetY = this.scrollOffsetY;
      this.colorSwatch.render(n, n2, f);
      Gui.drawRect(this.x + 186, this.y + 16, this.x + this.width - 16, this.y + 17, this.theme.getIntValue2());
      float f2 = this.x + 188;
      CheatBreaker.getInstance().getFontManager().playBold18.drawString("#", f2, this.y + 4, this.theme.getTextColor4Value());
      CheatBreaker.getInstance()
         .getFontManager()
         .playBold18
         .drawString(Integer.toHexString(this.setting.getColorValue()), this.x + 194, this.y + 4, this.theme.getTextColor4Value());
      boolean flag = n > (this.x + this.width - 40) * this.scale
         && n < (this.x + this.width - 12) * this.scale
         && n2 > (this.y + this.scrollOffsetY) * this.scale
         && n2 < (this.y + 18 + this.scrollOffsetY) * this.scale;
      String str = flag ? "(Favorite)" : "(+)";
      if (CheatBreaker.getInstance().getKeybindSettings().isColorSaved((Integer)this.setting.getValue())) {
         str = flag ? "(Un-favorite)" : "(-)";
      }

      CheatBreaker.getInstance()
         .getFontManager()
         .playBold18
         .drawString(
            str,
            this.x + this.width - 16 - CheatBreaker.getInstance().getFontManager().playBold18.getStringWidth(str),
            this.y + 4,
            flag ? this.theme.getTextColor5Value() : this.theme.getTextColor4Value()
         );
      this.renderSettingLabel(this.setting, n, n2);
      if (this.expanded) {
         if (this.draggingColorArea && !Mouse.isButtonDown(0)) {
            this.draggingColorArea = false;
            this.saveToRecent();
         }

         if (this.draggingHueSlider && !Mouse.isButtonDown(0)) {
            this.draggingHueSlider = false;
            this.saveToRecent();
         }

         if (this.draggingAlphaSlider && !Mouse.isButtonDown(0)) {
            this.draggingAlphaSlider = false;
            this.saveToRecent();
         }

         Gui.drawRect(this.x + 55, this.y + 24, this.x + 177, this.y + 120, this.theme.getTextColor5Value());
         Tessellator instance = Tessellator.getInstance();
         GlStateManager.disableTexture2D();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         worldRenderer.pos(this.pickerLeft, this.pickerBottom, 0.0);
         worldRenderer.pos(this.pickerRight, this.pickerBottom, 0.0);
         worldRenderer.pos(this.pickerRight, this.pickerTop, 0.0);
         worldRenderer.pos(this.pickerLeft, this.pickerTop, 0.0);
         instance.draw();
         int[] ints = null;

         for (int i = 0; i < this.pickerWidth; i++) {
            for (int j = 0; j < this.pickerHeight; j++) {
               float f3 = i / this.pickerWidth;
               float f4 = 1.0F - j / this.pickerHeight;
               int n3 = Color.HSBtoRGB(this.hue, f3, f4);
               int rGB = new Color(n3 >> 16 & 0xFF, n3 >> 8 & 0xFF, n3 & 0xFF, this.setting.getIntValue() >> 24 & 0xFF).getRGB();
               boolean flag2 = n >= (this.pickerLeft + i) * this.scale && n <= (this.pickerLeft + i + 1.0F) * this.scale;
               boolean flag3 = n2 <= (this.pickerTop + j + 1.0F + this.scrollOffsetY) * this.scale
                  && n2 > (this.pickerTop + j + this.scrollOffsetY) * this.scale;
               boolean flag4 = flag2 && flag3;
               boolean flag5 = i == 0 && n < this.pickerLeft * this.scale && flag3;
               boolean flag6 = j == 0 && n2 < (this.pickerTop + this.scrollOffsetY) * this.scale && flag2;
               boolean flag7 = i == this.pickerWidth - 1.0F && n > (this.pickerLeft + this.pickerWidth) * this.scale && flag3;
               boolean flag8 = j == this.pickerHeight - 1.0F && n2 > (this.pickerTop + this.pickerHeight + this.scrollOffsetY) * this.scale && flag2;
               if (this.draggingColorArea && (flag4 || flag5 || flag6 || flag7 || flag8)) {
                  this.setting.changeValue(rGB);
                  this.setting.color = new int[]{i, j};
               }

               if (this.setting.color != null) {
                  ints = this.setting.color;
               } else if (rGB == (Integer)this.setting.getValue()) {
                  ints = new int[]{i, j};
               }

               worldRenderer.begin(7, DefaultVertexFormats.POSITION);
               GlStateManager.color((rGB >> 16 & 0xFF) / 255.0F, (rGB >> 8 & 0xFF) / 255.0F, (rGB & 0xFF) / 255.0F, 1.0F);
               worldRenderer.pos(this.pickerLeft + i, this.pickerTop + j + 1.0F, 0.0).endVertex();
               worldRenderer.pos(this.pickerLeft + i + 1.0F, this.pickerTop + j + 1.0F, 0.0).endVertex();
               worldRenderer.pos(this.pickerLeft + i + 1.0F, this.pickerTop + j, 0.0).endVertex();
               worldRenderer.pos(this.pickerLeft + i, this.pickerTop + j, 0.0).endVertex();
               instance.draw();
            }
         }

         if (ints != null) {
            GlStateManager.pushMatrix();
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.75F);
            RenderUtils.drawFilledCircle(this.pickerLeft + ints[0] + 1.115F, this.pickerTop + ints[1] + 1.115F, 4.0);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtils.drawFilledCircle(this.pickerLeft + ints[0] + 1.115F, this.pickerTop + ints[1] + 1.115F, 2.7);
            GlStateManager.popMatrix();
         }

         Gui.drawRect(this.pickerLeft - 51.0F, this.pickerTop + 1.0F, this.pickerLeft - 43.0F, this.pickerTop + 9.0F, -16777216);
         Gui.drawRect(this.pickerLeft - 50.0F, this.pickerTop + 2.0F, this.pickerLeft - 44.0F, this.pickerTop + 8.0F, this.setting.chroma ? -13369549 : -1);
         f2 = this.pickerLeft - 40.0F;
         CheatBreaker.getInstance().getFontManager().playRegular16.drawString("CHROMA", f2, this.pickerTop, this.theme.getTextColor4Value());
         if (this.setting.chroma) {
            Gui.drawRect(this.pickerLeft - 51.0F, this.pickerTop + 10.0F, this.pickerLeft - 43.0F, this.pickerTop + 18.0F, -16777216);
            Gui.drawRect(this.pickerLeft - 50.0F, this.pickerTop + 11.0F, this.pickerLeft - 44.0F, this.pickerTop + 17.0F, this.setting.speed ? -13369549 : -1);
            f2 = this.pickerLeft - 40.0F;
            CheatBreaker.getInstance().getFontManager().playRegular16.drawString("FAST", f2, this.pickerTop + 10.0F, this.theme.getTextColor4Value());
         }

         this.drawHueSlider(n, n2);
         this.drawAlphaSlider(n, n2);
         this.recentX = (int)(this.pickerLeft + this.pickerWidth + 64.0F);
         this.recentY = (int)this.pickerTop;
         this.renderSwatches(CheatBreaker.getInstance().getKeybindSettings().recentColors, this.recentX, this.recentY, n, n2, (int)f);
         this.favoritesX = (int)(this.pickerLeft + this.pickerWidth + 94.0F);
         this.favoritesY = (int)this.pickerTop;
         this.renderSwatches(CheatBreaker.getInstance().getKeybindSettings().savedColors, this.favoritesX, this.favoritesY, n, n2, (int)f);
         this.renderSwatches(this.vanillaSwatches, (int)(this.pickerLeft + this.pickerWidth + 34.0F), (int)this.pickerTop, n, n2, (int)f);
      }
   }

   public ColorComponent(Setting setting, float f) {
      super(f);
      this.setting = setting;
      this.colorSwatch = new ColorSwatch(f, (Integer)setting.getValue(), 1.0F);
      this.vanillaSwatches = new ArrayList<>();

      for (int i = 0; i < 16; i++) {
         int n = Minecraft.getMinecraft().fontRendererObj.getColorCode()[i];
         this.vanillaSwatches.add(new ColorSwatch(f, n, 1.0F));
      }
   }
}
