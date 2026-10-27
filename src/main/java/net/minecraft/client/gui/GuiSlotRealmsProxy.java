package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsScrolledSelectionList;

public class GuiSlotRealmsProxy extends GuiSlot {
   public RealmsScrolledSelectionList selectionList;

   public GuiSlotRealmsProxy(RealmsScrolledSelectionList selectionListIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.selectionList = selectionListIn;
   }

   @Override
   public int getSize() {
      return this.selectionList.getItemCount();
   }

   @Override
   public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      this.selectionList.selectItem(slotIndex, isDoubleClick, mouseX, mouseY);
   }

   @Override
   public boolean isSelected(int slotIndex) {
      return this.selectionList.isSelectedItem(slotIndex);
   }

   @Override
   public void drawBackground() {
      this.selectionList.renderBackground();
   }

   @Override
   public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
      this.selectionList.renderItem(entryID, n, n2, n3, mouseXIn, mouseYIn);
   }

   public int getWidth() {
      return super.width;
   }

   @Override
   public int getContentHeight() {
      return this.selectionList.getMaxPosition();
   }

   @Override
   public int getScrollBarX() {
      return this.selectionList.getScrollbarPosition();
   }

   @Override
   public void handleMouseInput() {
      super.handleMouseInput();
   }

   public int getMouseX() {
      return super.mouseX;
   }

   public int getMouseY() {
      return super.mouseY;
   }
}
