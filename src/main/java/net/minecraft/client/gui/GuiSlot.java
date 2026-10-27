package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

public abstract class GuiSlot {
   public boolean centerListVertically = true;
   public int initialClickY = -2;
   public int selectedElement = -1;
   public boolean visible = true;
   public boolean showSelectionBox = true;
   public boolean enabled = true;
   public Minecraft mc;
   public int width;
   public int scrollUpButtonID;
   public int scrollDownButtonID;
   public int mouseX;
   public int mouseY;
   public int height;
   public int top;
   public float scrollMultiplier;
   public float amountScrolled;
   public int bottom;
   public long lastClicked;
   public int slotHeight;
   public int left;
   public boolean hasListHeader;
   public int headerPadding;
   public int right;

   public GuiSlot(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
      this.mc = mcIn;
      this.width = width;
      this.height = height;
      this.top = topIn;
      this.bottom = bottomIn;
      this.slotHeight = slotHeightIn;
      this.left = 0;
      this.right = width;
   }

   public void setDimensions(int widthIn, int heightIn, int topIn, int bottomIn) {
      this.width = widthIn;
      this.height = heightIn;
      this.top = topIn;
      this.bottom = bottomIn;
      this.left = 0;
      this.right = widthIn;
   }

   public void setShowSelectionBox(boolean showSelectionBoxIn) {
      this.showSelectionBox = showSelectionBoxIn;
   }

   public void setHasListHeader(boolean hasListHeaderIn, int headerPaddingIn) {
      this.hasListHeader = hasListHeaderIn;
      this.headerPadding = headerPaddingIn;
      if (!hasListHeaderIn) {
         this.headerPadding = 0;
      }
   }

   public abstract int getSize();

   public abstract void elementClicked(int n, boolean flag, int n2, int n3);

   public abstract boolean isSelected(int n);

   public int getContentHeight() {
      return this.getSize() * this.slotHeight + this.headerPadding;
   }

   public abstract void drawBackground();

   public void process(int n, int n2, int n3) {
   }

   public abstract void drawSlot(int n, int n2, int n3, int n4, int n5, int n6);

   public void drawListHeader(int n, int n2, Tessellator tessellator) {
   }

   public void process(int n, int n2) {
   }

   public void renderDecorations(int n, int n2) {
   }

   public int getSlotIndexFromScreenCoords(int n, int n2) {
      int n3 = this.left + this.width / 2 - this.getListWidth() / 2;
      int n4 = this.left + this.width / 2 + this.getListWidth() / 2;
      int n5 = n2 - this.top - this.headerPadding + (int)this.amountScrolled - 4;
      int n6 = n5 / this.slotHeight;
      return n < this.getScrollBarX() && n >= n3 && n <= n4 && n6 >= 0 && n5 >= 0 && n6 < this.getSize() ? n6 : -1;
   }

   public void registerScrollButtons(int scrollUpButtonIDIn, int scrollDownButtonIDIn) {
      this.scrollUpButtonID = scrollUpButtonIDIn;
      this.scrollDownButtonID = scrollDownButtonIDIn;
   }

   public void bindAmountScrolled() {
      float maxScroll = this.getMaxScroll();
      if (this.amountScrolled < 0.0F) {
         this.amountScrolled = 0.0F;
      }

      if (this.amountScrolled > maxScroll) {
         this.amountScrolled = maxScroll;
      }
   }

   public int getMaxScroll() {
      int n = this.getContentHeight() - (this.bottom - this.top - 4);
      if (CheatBreaker.getInstance().getKeybindSettings().centerShortLists.getBooleanValue()) {
         return n < 0 ? n / 2 : n;
      } else {
         return Math.max(0, n);
      }
   }

   public int getAmountScrolled() {
      return (int)this.amountScrolled;
   }

   public boolean isMouseYWithinSlotBounds(int n) {
      return n >= this.top && n <= this.bottom && this.mouseX >= this.left && this.mouseX <= this.right;
   }

   public void scrollBy(int amount) {
      this.amountScrolled += amount;
      this.bindAmountScrolled();
      this.initialClickY = -2;
   }

   public void actionPerformed(GuiButton button) {
      if (button.enabled) {
         if (button.id == this.scrollUpButtonID) {
            this.amountScrolled = this.amountScrolled - this.slotHeight * 2 / 3;
            this.initialClickY = -2;
            this.bindAmountScrolled();
         } else if (button.id == this.scrollDownButtonID) {
            this.amountScrolled = this.amountScrolled + this.slotHeight * 2 / 3;
            this.initialClickY = -2;
            this.bindAmountScrolled();
         }
      }
   }

   public void drawScreen(int mouseXIn, int mouseYIn, float f) {
      if (this.visible) {
         this.mouseX = mouseXIn;
         this.mouseY = mouseYIn;
         this.drawBackground();
         int scrollBarX = this.getScrollBarX();
         int n = scrollBarX + 6;
         this.bindAmountScrolled();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         this.drawContainerBackground(instance);
         int n2 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int n3 = this.top + 4 - (int)this.amountScrolled;
         if (this.hasListHeader) {
            this.drawListHeader(n2, n3, instance);
         }

         this.drawSelectionBox(n2, n3, mouseXIn, mouseYIn);
         GlStateManager.disableDepth();
         byte b = 4;
         this.overlayBackground(0, this.top, 255, 255);
         this.overlayBackground(this.bottom, this.height, 255, 255);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableAlpha();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
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
         int maxScroll = this.getMaxScroll();
         if (maxScroll > 0) {
            int n4 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            n4 = MathHelper.clamp_int(n4, 32, this.bottom - this.top - 8);
            int n5 = (int)this.amountScrolled * (this.bottom - this.top - n4) / maxScroll + this.top;
            if (n5 < this.top) {
               n5 = this.top;
            }

            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(scrollBarX, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(n, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(n, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(scrollBarX, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            instance.draw();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(scrollBarX, n5 + n4, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(n, n5 + n4, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(n, n5, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(scrollBarX, n5, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            instance.draw();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(scrollBarX, n5 + n4 - 1, 0.0).tex(0.0, 1.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(n - 1, n5 + n4 - 1, 0.0).tex(1.0, 1.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(n - 1, n5, 0.0).tex(1.0, 0.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(scrollBarX, n5, 0.0).tex(0.0, 0.0).color(192, 192, 192, 255).endVertex();
            instance.draw();
         }

         this.renderDecorations(mouseXIn, mouseYIn);
         GlStateManager.enableTexture2D();
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
      }
   }

   public void handleMouseInput() {
      if (this.isMouseYWithinSlotBounds(this.mouseY)) {
         if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
            int n = (this.width - this.getListWidth()) / 2;
            int n2 = (this.width + this.getListWidth()) / 2;
            int n3 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
            int n4 = n3 / this.slotHeight;
            if (n4 < this.getSize() && this.mouseX >= n && this.mouseX <= n2 && n4 >= 0 && n3 >= 0) {
               this.elementClicked(n4, false, this.mouseX, this.mouseY);
               this.selectedElement = n4;
            } else if (this.mouseX >= n && this.mouseX <= n2 && n3 < 0) {
               this.process(this.mouseX - n, this.mouseY - this.top + (int)this.amountScrolled - 4);
            }
         }

         if (!Mouse.isButtonDown(0) || !this.getEnabled()) {
            this.initialClickY = -1;
         } else if (this.initialClickY == -1) {
            boolean flag = true;
            if (this.mouseY >= this.top && this.mouseY <= this.bottom) {
               int n = (this.width - this.getListWidth()) / 2;
               int n2 = (this.width + this.getListWidth()) / 2;
               int n3 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
               int n4 = n3 / this.slotHeight;
               if (n4 < this.getSize() && this.mouseX >= n && this.mouseX <= n2 && n4 >= 0 && n3 >= 0) {
                  boolean flag2 = n4 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                  this.elementClicked(n4, flag2, this.mouseX, this.mouseY);
                  this.selectedElement = n4;
                  this.lastClicked = Minecraft.getSystemTime();
               } else if (this.mouseX >= n && this.mouseX <= n2 && n3 < 0) {
                  this.process(this.mouseX - n, this.mouseY - this.top + (int)this.amountScrolled - 4);
                  flag = false;
               }

               int scrollBarX = this.getScrollBarX();
               int n5 = scrollBarX + 6;
               if (this.mouseX >= scrollBarX && this.mouseX <= n5) {
                  this.scrollMultiplier = -1.0F;
                  int maxScroll = this.getMaxScroll();
                  if (maxScroll < 1) {
                     maxScroll = 1;
                  }

                  int n6 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / this.getContentHeight());
                  n6 = MathHelper.clamp_int(n6, 32, this.bottom - this.top - 8);
                  this.scrollMultiplier = this.scrollMultiplier / ((float)(this.bottom - this.top - n6) / maxScroll);
               } else {
                  this.scrollMultiplier = 1.0F;
               }

               if (flag) {
                  this.initialClickY = this.mouseY;
               } else {
                  this.initialClickY = -2;
               }
            } else {
               this.initialClickY = -2;
            }
         } else if (this.initialClickY >= 0) {
            this.amountScrolled = this.amountScrolled - (this.mouseY - this.initialClickY) * this.scrollMultiplier;
            this.initialClickY = this.mouseY;
         }

         int eventDWheel = Mouse.getEventDWheel();
         if (eventDWheel != 0) {
            if (eventDWheel > 0) {
               eventDWheel = -1;
            } else if (eventDWheel < 0) {
               eventDWheel = 1;
            }

            this.amountScrolled = this.amountScrolled + eventDWheel * this.slotHeight / 2;
         }
      }
   }

   public void setEnabled(boolean enabledIn) {
      this.enabled = enabledIn;
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public int getListWidth() {
      return 220;
   }

   public void drawSelectionBox(int n, int n2, int mouseXIn, int mouseYIn) {
      int size = this.getSize();
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();

      for (int i = 0; i < size; i++) {
         int n3 = n2 + i * this.slotHeight + this.headerPadding;
         int n4 = this.slotHeight - 4;
         if (n3 > this.bottom || n3 + n4 < this.top) {
            this.process(i, n, n3);
         }

         if (this.showSelectionBox && this.isSelected(i)) {
            int n5 = this.left + (this.width / 2 - this.getListWidth() / 2);
            int n6 = this.left + this.width / 2 + this.getListWidth() / 2;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableTexture2D();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(n5, n3 + n4 + 2, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(n6, n3 + n4 + 2, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(n6, n3 - 2, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(n5, n3 - 2, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(n5 + 1, n3 + n4 + 1, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(n6 - 1, n3 + n4 + 1, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(n6 - 1, n3 - 1, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(n5 + 1, n3 - 1, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            instance.draw();
            GlStateManager.enableTexture2D();
         }

         if (!(this instanceof GuiResourcePackList) || n3 >= this.top - this.slotHeight && n3 <= this.bottom) {
            this.drawSlot(i, n, n3, n4, mouseXIn, mouseYIn);
         }
      }
   }

   public int getScrollBarX() {
      return this.width / 2 + 124;
   }

   public void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(this.left, endY, 0.0).tex(0.0, endY / 32.0F).color(64, 64, 64, endAlpha).endVertex();
      worldRenderer.pos(this.left + this.width, endY, 0.0).tex(this.width / 32.0F, endY / 32.0F).color(64, 64, 64, endAlpha).endVertex();
      worldRenderer.pos(this.left + this.width, startY, 0.0).tex(this.width / 32.0F, startY / 32.0F).color(64, 64, 64, startAlpha).endVertex();
      worldRenderer.pos(this.left, startY, 0.0).tex(0.0, startY / 32.0F).color(64, 64, 64, startAlpha).endVertex();
      instance.draw();
   }

   public void setSlotXBoundsFromLeft(int leftIn) {
      this.left = leftIn;
      this.right = leftIn + this.width;
   }

   public int getSlotHeight() {
      return this.slotHeight;
   }

   public void drawContainerBackground(Tessellator tessellator) {
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(this.left, this.bottom, 0.0).tex(this.left / f, (this.bottom + (int)this.amountScrolled) / f).color(32, 32, 32, 255).endVertex();
      worldRenderer.pos(this.right, this.bottom, 0.0).tex(this.right / f, (this.bottom + (int)this.amountScrolled) / f).color(32, 32, 32, 255).endVertex();
      worldRenderer.pos(this.right, this.top, 0.0).tex(this.right / f, (this.top + (int)this.amountScrolled) / f).color(32, 32, 32, 255).endVertex();
      worldRenderer.pos(this.left, this.top, 0.0).tex(this.left / f, (this.top + (int)this.amountScrolled) / f).color(32, 32, 32, 255).endVertex();
      tessellator.draw();
   }
}
