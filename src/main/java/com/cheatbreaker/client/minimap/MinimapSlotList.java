package com.cheatbreaker.client.minimap;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;

public abstract class MinimapSlotList {
   public int listWidth = 220;
   public boolean allowNegativeScroll = true;
   public float scrollMultiplier = -2.0F;
   public int selectedElement = -1;
   public float amountScrolled;
   public boolean showSelectionBox = true;
   public boolean insetSlots = true;
   public int headerPadding;
   public boolean drawOverlays = true;
   public boolean enabled = true;
   public int mouseY;
   public int scrollDownButtonId;
   public Minecraft mc;
   public int width;
   public int mouseX;
   public int height;
   public long lastClicked;
   public boolean hasListHeader;
   public int top;
   public int bottom;
   public float initialClickY;
   public int scrollUpButtonId;
   public int slotHeight;
   public int left;
   public int right;

   public void setScrollButtonIds(int n, int n2) {
      this.scrollUpButtonId = n;
      this.scrollDownButtonId = n2;
   }

   public int getContentHeight() {
      return this.getSize() * this.slotHeight + this.headerPadding;
   }

   public void setListWidth(int n) {
      this.listWidth = n;
   }

   public void onElementClicked(int n, int n2) {
   }

   public void setLeftPosition(int n) {
      this.left = n;
      this.right = n + this.width;
   }

   public void drawSelectionBox(int n, int n2, int n3, int n4) {
      int size = this.getSize();
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();

      for (int i = 0; i < size; i++) {
         int n5 = n2 + i * this.slotHeight + this.headerPadding;
         int n6 = this.insetSlots ? this.slotHeight - 4 : 0;
         int n7 = this.insetSlots ? 0 : this.slotHeight - 4;
         if (n5 + n7 <= this.bottom && n5 + n6 >= this.top) {
            if (this.showSelectionBox && this.isSelected(i)) {
               int n8 = this.left + (this.width / 2 - this.getListWidth() / 2);
               int n9 = this.left + this.width / 2 + this.getListWidth() / 2;
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.disableTexture2D();
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
               short s = 128;
               short s2 = 128;
               short s3 = 128;
               worldRenderer.pos(n8, n5 + n6 + 2, 0.0).tex(0.0, 1.0).color(s, s2, s3, 255).endVertex();
               worldRenderer.pos(n9, n5 + n6 + 2, 0.0).tex(1.0, 1.0).color(s, s2, s3, 255).endVertex();
               worldRenderer.pos(n9, n5 - 2, 0.0).tex(1.0, 0.0).color(s, s2, s3, 255).endVertex();
               worldRenderer.pos(n8, n5 - 2, 0.0).tex(0.0, 0.0).color(s, s2, s3, 255).endVertex();
               instance.draw();
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
               worldRenderer.pos(n8 + 1, n5 + n6 + 1, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
               worldRenderer.pos(n9 - 1, n5 + n6 + 1, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
               worldRenderer.pos(n9 - 1, n5 - 1, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
               worldRenderer.pos(n8 + 1, n5 - 1, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
               instance.draw();
               GlStateManager.enableTexture2D();
            }

            this.drawSlot(i, n, n5, n6, instance, n3, n4);
         }
      }
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public int getAmountScrolled() {
      return (int)this.amountScrolled;
   }

   public void setShowSelectionBox(boolean flag) {
      this.showSelectionBox = flag;
   }

   public void overlayBackground(int n, int n2, int n3, int n4) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      byte b = 64;
      byte b2 = 64;
      byte b3 = 64;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(this.left, n2, 0.0).tex(0.0, n2 / f).color(b, b2, b3, n4).endVertex();
      worldRenderer.pos(this.left + this.width, n2, 0.0).tex(this.width / f, n2 / f).color(b, b2, b3, n4).endVertex();
      worldRenderer.pos(this.left + this.width, n, 0.0).tex(this.width / f, n / f).color(b, b2, b3, n3).endVertex();
      worldRenderer.pos(this.left, n, 0.0).tex(0.0, n / f).color(b, b2, b3, n3).endVertex();
      instance.draw();
   }

   public abstract void elementClicked(int n, boolean flag, int n2, int n3);

   public int getMaxScroll() {
      return this.getContentHeight() - (this.bottom - this.top - 4);
   }

   public void setHasListHeader(boolean flag, int n) {
      this.hasListHeader = flag;
      this.headerPadding = n;
      if (!flag) {
         this.headerPadding = 0;
      }
   }

   public int getSlotIndexFromScreenCoords(int n, int n2) {
      int n3 = this.left + this.width / 2 - this.getListWidth() / 2;
      int n4 = this.left + this.width / 2 + this.getListWidth() / 2;
      int n5 = n2 - this.top - this.headerPadding + (int)this.amountScrolled - 4;
      int n6 = n5 / this.slotHeight;
      return n < this.getScrollBarX() && n >= n3 && n <= n4 && n6 >= 0 && n5 >= 0 && n6 < this.getSize() ? n6 : -1;
   }

   public void setInsetSlots(boolean flag) {
      this.insetSlots = flag;
   }

   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.enabled) {
         if (guiButton.id == this.scrollUpButtonId) {
            this.amountScrolled = this.amountScrolled - this.slotHeight * 2 / 3;
            this.scrollMultiplier = -2.0F;
            this.bindAmountScrolled();
         } else if (guiButton.id == this.scrollDownButtonId) {
            this.amountScrolled = this.amountScrolled + this.slotHeight * 2 / 3;
            this.scrollMultiplier = -2.0F;
            this.bindAmountScrolled();
         }
      }
   }

   public int getListWidth() {
      return this.listWidth;
   }

   public void drawScreen(int n, int n2, float f) {
      this.mouseX = n;
      this.mouseY = n2;
      this.drawBackground();
      int size = this.getSize();
      int scrollBarX = this.getScrollBarX();
      int n3 = scrollBarX + 6;
      if (n > this.left && n < this.right && n2 > this.top && n2 < this.bottom) {
         if (!Mouse.isButtonDown(0) || !this.getEnabled()) {
            while (!this.mc.gameSettings.touchscreen && Mouse.next()) {
               int eventDWheel = Mouse.getEventDWheel();
               if (eventDWheel != 0) {
                  if (eventDWheel > 0) {
                     eventDWheel = -1;
                  } else if (eventDWheel < 0) {
                     eventDWheel = 1;
                  }

                  this.amountScrolled = this.amountScrolled + eventDWheel * this.slotHeight / 2;
               }

               try {
                  this.mc.currentScreen.handleMouseInput();
               } catch (IOException e) {
               }
            }

            this.scrollMultiplier = -1.0F;
         } else if (this.scrollMultiplier == -1.0F) {
            boolean flag = true;
            if (n2 >= this.top && n2 <= this.bottom) {
               int n4 = this.width / 2 - this.getListWidth() / 2;
               int n5 = this.width / 2 + this.getListWidth() / 2;
               int n6 = n2 - this.top - this.headerPadding + (int)this.amountScrolled - 4;
               int n7 = n6 / this.slotHeight;
               if (n >= n4 && n <= n5 && n7 >= 0 && n6 >= 0 && n7 < size) {
                  boolean flag2 = n7 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                  this.elementClicked(n7, flag2, n, n2);
                  this.selectedElement = n7;
                  this.lastClicked = Minecraft.getSystemTime();
               } else if (n >= n4 && n <= n5 && n6 < 0) {
                  this.onElementClicked(n - n4, n2 - this.top + (int)this.amountScrolled - 4);
                  flag = false;
               }

               if (n >= scrollBarX && n <= n3) {
                  this.initialClickY = -1.0F;
                  int maxScroll = this.getMaxScroll();
                  if (maxScroll < 1) {
                     maxScroll = 1;
                  }

                  int n8 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                  if (n8 < 32) {
                     n8 = 32;
                  }

                  if (n8 > this.bottom - this.top - 8) {
                     n8 = this.bottom - this.top - 8;
                  }

                  this.initialClickY = this.initialClickY / ((this.bottom - this.top - n8) / maxScroll);
               } else {
                  this.initialClickY = 1.0F;
               }

               if (flag) {
                  this.scrollMultiplier = n2;
               } else {
                  this.scrollMultiplier = -2.0F;
               }
            } else {
               this.scrollMultiplier = -2.0F;
            }
         } else if (this.scrollMultiplier >= 0.0F) {
            this.amountScrolled = this.amountScrolled - (n2 - this.scrollMultiplier) * this.initialClickY;
            this.scrollMultiplier = n2;
         }
      }

      this.bindAmountScrolled();
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      if (this.drawOverlays) {
         this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         float f2 = 32.0F;
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         byte b = 32;
         byte b2 = 32;
         byte b3 = 32;
         worldRenderer.pos(this.left, this.bottom, 0.0)
            .tex(this.left / f2, (this.bottom + (int)this.amountScrolled) / f2)
            .color(b, b2, b3, 255)
            .endVertex();
         worldRenderer.pos(this.right, this.bottom, 0.0)
            .tex(this.right / f2, (this.bottom + (int)this.amountScrolled) / f2)
            .color(b, b2, b3, 255)
            .endVertex();
         worldRenderer.pos(this.right, this.top, 0.0)
            .tex(this.right / f2, (this.top + (int)this.amountScrolled) / f2)
            .color(b, b2, b3, 255)
            .endVertex();
         worldRenderer.pos(this.left, this.top, 0.0).tex(this.left / f2, (this.top + (int)this.amountScrolled) / f2).color(b, b2, b3, 255).endVertex();
         instance.draw();
      }

      int n4 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
      int n5 = this.top + 4 - (int)this.amountScrolled;
      if (this.hasListHeader) {
         this.drawListHeader(n4, n5, instance);
      }

      this.drawSelectionBox(n4, n5, n, n2);
      GlStateManager.disableDepth();
      byte b = 4;
      if (this.insetSlots) {
         this.overlayBackground(0, this.top, 255, 255);
         this.overlayBackground(this.bottom, this.height, 255, 255);
      }

      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
      GlStateManager.disableAlpha();
      GlStateManager.shadeModel(7425);
      GlStateManager.disableTexture2D();
      if (this.insetSlots) {
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer.pos(this.left, this.top + b, 0.0).tex(0.0, 1.0).color(0, 0, 0, 0).endVertex();
         worldRenderer.pos(this.right, this.top + b, 0.0).tex(1.0, 1.0).color(0, 0, 0, 0).endVertex();
         worldRenderer.pos(this.right, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(this.left, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
         instance.draw();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer.pos(this.left, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(this.right, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(this.right, this.bottom - b, 0.0).tex(1.0, 0.0).color(0, 0, 0, 0).endVertex();
         worldRenderer.pos(this.left, this.bottom - b, 0.0).tex(0.0, 0.0).color(0, 0, 0, 0).endVertex();
         instance.draw();
      }

      int maxScroll = this.getMaxScroll();
      if (maxScroll > 0) {
         int n6 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
         if (n6 < 32) {
            n6 = 32;
         }

         if (n6 > this.bottom - this.top - 8) {
            n6 = this.bottom - this.top - 8;
         }

         int n7 = (int)this.amountScrolled * (this.bottom - this.top - n6) / maxScroll + this.top;
         if (n7 < this.top) {
            n7 = this.top;
         }

         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer.pos(scrollBarX, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(n3, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(n3, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(scrollBarX, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
         instance.draw();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer.pos(scrollBarX, n7 + n6, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
         worldRenderer.pos(n3, n7 + n6, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
         worldRenderer.pos(n3, n7, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
         worldRenderer.pos(scrollBarX, n7, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
         instance.draw();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer.pos(scrollBarX, n7 + n6 - 1, 0.0).tex(0.0, 1.0).color(192, 192, 192, 255).endVertex();
         worldRenderer.pos(n3 - 1, n7 + n6 - 1, 0.0).tex(1.0, 1.0).color(192, 192, 192, 255).endVertex();
         worldRenderer.pos(n3 - 1, n7, 0.0).tex(1.0, 0.0).color(192, 192, 192, 255).endVertex();
         worldRenderer.pos(scrollBarX, n7, 0.0).tex(0.0, 0.0).color(192, 192, 192, 255).endVertex();
         instance.draw();
      }

      this.onMouseReleased(n, n2);
      GlStateManager.enableTexture2D();
      GlStateManager.shadeModel(7424);
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
   }

   public void setDimensions(int n, int n2, int n3, int n4) {
      this.width = n;
      this.height = n2;
      this.top = n3;
      this.bottom = n4;
      this.left = 0;
      this.right = n;
   }

   public abstract int getSize();

   public void onMouseReleased(int n, int n2) {
   }

   public abstract void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6);

   public void setDrawOverlays(boolean flag) {
      this.drawOverlays = flag;
   }

   public boolean isMouseYWithinSlotBounds(int n) {
      return n >= this.top && n <= this.bottom;
   }

   public abstract boolean isSelected(int n);

   public void bindAmountScrolled() {
      int maxScroll = this.getMaxScroll();
      if (maxScroll < 0) {
         maxScroll /= 2;
      }

      if (!this.allowNegativeScroll && maxScroll < 0) {
         maxScroll = 0;
      }

      if (this.amountScrolled < 0.0F) {
         this.amountScrolled = 0.0F;
      }

      if (this.amountScrolled > maxScroll) {
         this.amountScrolled = maxScroll;
      }
   }

   public int getScrollBarX() {
      return this.width / 2 + 124;
   }

   public MinimapSlotList(Minecraft mc, int n, int n2, int n3, int n4, int n5) {
      this.mc = mc;
      this.width = n;
      this.height = n2;
      this.top = n3;
      this.bottom = n4;
      this.slotHeight = n5;
      this.left = 0;
      this.right = n;
   }

   public void drawListHeader(int n, int n2, Tessellator tessellator) {
   }

   public void setEnabled(boolean flag) {
      this.enabled = flag;
   }

   public int getSlotHeight() {
      return this.slotHeight;
   }

   public void scrollBy(int n) {
      this.amountScrolled += n;
      this.bindAmountScrolled();
      this.scrollMultiplier = -2.0F;
   }

   public abstract void drawBackground();
}
