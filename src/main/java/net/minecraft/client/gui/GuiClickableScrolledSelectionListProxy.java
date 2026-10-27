package net.minecraft.client.gui;

import com.cheatbreaker.client.render.TessellatorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import org.lwjgl.input.Mouse;

public class GuiClickableScrolledSelectionListProxy extends GuiSlot {
   public RealmsClickableScrolledSelectionList list;

   public GuiClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList selectionList, int n, int n2, int n3, int n4, int n5) {
      super(Minecraft.getMinecraft(), n, n2, n3, n4, n5);
      this.list = selectionList;
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
      if (this.scrollMultiplier > 0.0F && Mouse.getEventButtonState()) {
         this.list.customMouseEvent(this.top, this.bottom, this.headerPadding, this.amountScrolled, this.slotHeight);
      }
   }

   public void renderSelected(int n, int n2, int n3, TessellatorWrapper tessellatorWrapper) {
      this.list.renderSelected(n, n2, n3, tessellatorWrapper);
   }

   @Override
   public void drawSelectionBox(int n, int n2, int mouseXIn, int mouseYIn) {
      int size = this.getSize();

      for (int i = 0; i < size; i++) {
         int n3 = n2 + i * this.slotHeight + this.headerPadding;
         int n4 = this.slotHeight - 4;
         if (n3 > this.bottom || n3 + n4 < this.top) {
            this.process(i, n, n3);
         }

         if (this.showSelectionBox && this.isSelected(i)) {
            this.renderSelected(this.width, n3, n4, TessellatorWrapper.instance);
         }

         this.drawSlot(i, n, n3, n4, mouseXIn, mouseYIn);
      }
   }

   public int getMouseX() {
      return super.mouseX;
   }

   public int getMouseY() {
      return super.mouseY;
   }
}
