package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.MathHelper;

public class GuiSimpleScrolledSelectionListProxy extends GuiSlot {
   public RealmsSimpleScrolledSelectionList list;

   public GuiSimpleScrolledSelectionListProxy(RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.list = realmsSimpleScrolledSelectionList;
   }

   @Override
   public int getSize() {
      return this.list.getItemCount();
   }

   @Override
   public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      this.list.selectItem(slotIndex, isDoubleClick, mouseX, mouseY);
   }

   @Override
   public boolean isSelected(int slotIndex) {
      return this.list.isSelectedItem(slotIndex);
   }

   @Override
   public void drawBackground() {
      this.list.renderBackground();
   }

   @Override
   public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
      this.list.renderItem(entryID, n, n2, n3, mouseXIn, mouseYIn);
   }

   public int getWidth() {
      return super.width;
   }

   @Override
   public int getContentHeight() {
      return this.list.getMaxPosition();
   }

   @Override
   public int getScrollBarX() {
      return this.list.getScrollbarPosition();
   }

   @Override
   public void handleMouseInput() {
      super.handleMouseInput();
   }

   @Override
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

   public int getMouseX() {
      return super.mouseX;
   }

   public int getMouseY() {
      return super.mouseY;
   }
}
