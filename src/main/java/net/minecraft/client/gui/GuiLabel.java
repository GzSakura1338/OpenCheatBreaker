package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiLabel extends Gui {
   public int width = 200;
   public int height = 20;
   public boolean visible = true;
   public FontRenderer fontRenderer;
   public int id;
   public int x;
   public int y;
   public List<String> texts;
   public boolean centered;
   public boolean labelBgEnabled;
   public int color;
   public int backgroundColor;
   public int upperRightColor;
   public int lowerLeftColor;
   public int borderThickness;

   public GuiLabel(FontRenderer fontRendererObj, int n, int n2, int n3, int n4, int n5, int n6) {
      this.fontRenderer = fontRendererObj;
      this.id = n;
      this.x = n2;
      this.y = n3;
      this.width = n4;
      this.height = n5;
      this.texts = Lists.newArrayList();
      this.centered = false;
      this.labelBgEnabled = false;
      this.color = n6;
      this.backgroundColor = -1;
      this.upperRightColor = -1;
      this.lowerLeftColor = -1;
      this.borderThickness = 0;
   }

   public void addLine(String str) {
      this.texts.add(I18n.format(str));
   }

   public GuiLabel setCentered() {
      this.centered = true;
      return this;
   }

   public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         this.drawLabelBackground(mc, mouseX, mouseY);
         int n = this.y + this.height / 2 + this.borderThickness / 2;
         int n2 = n - this.texts.size() * 10 / 2;

         for (int i = 0; i < this.texts.size(); i++) {
            if (this.centered) {
               this.drawCenteredString(this.fontRenderer, this.texts.get(i), this.x + this.width / 2, n2 + i * 10, this.color);
            } else {
               this.drawString(this.fontRenderer, this.texts.get(i), this.x, n2 + i * 10, this.color);
            }
         }
      }
   }

   public void drawLabelBackground(Minecraft mcIn, int n, int n2) {
      if (this.labelBgEnabled) {
         int n3 = this.width + this.borderThickness * 2;
         int n4 = this.height + this.borderThickness * 2;
         int n5 = this.x - this.borderThickness;
         int n6 = this.y - this.borderThickness;
         drawRect(n5, n6, n5 + n3, n6 + n4, this.backgroundColor);
         this.drawHorizontalLine(n5, n5 + n3, n6, this.upperRightColor);
         this.drawHorizontalLine(n5, n5 + n3, n6 + n4, this.lowerLeftColor);
         this.drawVerticalLine(n5, n6, n6 + n4, this.upperRightColor);
         this.drawVerticalLine(n5 + n3, n6, n6 + n4, this.lowerLeftColor);
      }
   }
}
