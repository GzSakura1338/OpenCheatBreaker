package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot {
   public GuiListExtended(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
   }

   @Override
   public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
   }

   @Override
   public boolean isSelected(int slotIndex) {
      return false;
   }

   @Override
   public void drawBackground() {
   }

   @Override
   public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
      this.getListEntry(entryID)
         .drawEntry(entryID, n, n2, this.getListWidth(), n3, mouseXIn, mouseYIn, this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == entryID);
   }

   @Override
   public void process(int n, int n2, int n3) {
      this.getListEntry(n).setSelected(n, n2, n3);
   }

   public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
      if (this.isMouseYWithinSlotBounds(mouseY)) {
         int slotIndexFromScreenCoords = this.getSlotIndexFromScreenCoords(mouseX, mouseY);
         if (slotIndexFromScreenCoords >= 0) {
            int n = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int n2 = this.top + 4 - this.getAmountScrolled() + slotIndexFromScreenCoords * this.slotHeight + this.headerPadding;
            int n3 = mouseX - n;
            int n4 = mouseY - n2;
            if (this.getListEntry(slotIndexFromScreenCoords).mousePressed(slotIndexFromScreenCoords, mouseX, mouseY, mouseEvent, n3, n4)) {
               this.setEnabled(false);
               return true;
            }
         }
      }

      return false;
   }

   public boolean mouseReleased(int n, int n2, int n3) {
      for (int i = 0; i < this.getSize(); i++) {
         int n4 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int n5 = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
         int n6 = n - n4;
         int n7 = n2 - n5;
         this.getListEntry(i).mouseReleased(i, n, n2, n3, n6, n7);
      }

      this.setEnabled(true);
      return false;
   }

   public abstract GuiListExtended.IGuiListEntry getListEntry(int n);

   public interface IGuiListEntry {
      void setSelected(int n, int n2, int n3);

      void drawEntry(int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean flag);

      boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6);

      void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6);
   }
}
