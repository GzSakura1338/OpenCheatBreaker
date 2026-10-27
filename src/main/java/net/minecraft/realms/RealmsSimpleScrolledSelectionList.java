package net.minecraft.realms;

import com.cheatbreaker.client.render.TessellatorWrapper;
import net.minecraft.client.gui.GuiSimpleScrolledSelectionListProxy;

public class RealmsSimpleScrolledSelectionList {
   public GuiSimpleScrolledSelectionListProxy proxy;

   public RealmsSimpleScrolledSelectionList(int n, int n2, int n3, int n4, int n5) {
      this.proxy = new GuiSimpleScrolledSelectionListProxy(this, n, n2, n3, n4, n5);
   }

   public void render(int n, int n2, float f) {
      this.proxy.drawScreen(n, n2, f);
   }

   public void renderItem(int n, int n2, int n3, int n4, TessellatorWrapper tessellatorWrapper, int n5, int n6) {
   }

   public void renderItem(int n, int n2, int n3, int n4, int n5, int n6) {
      this.renderItem(n, n2, n3, n4, TessellatorWrapper.instance, n5, n6);
   }

   public int getItemCount() {
      return 0;
   }

   public void selectItem(int n, boolean flag, int n2, int n3) {
   }

   public boolean isSelectedItem(int n) {
      return false;
   }

   public void renderBackground() {
   }

   public int getMaxPosition() {
      return 0;
   }

   public int getScrollbarPosition() {
      return this.proxy.getWidth() / 2 + 124;
   }

   public void mouseEvent() {
      this.proxy.handleMouseInput();
   }

   public void scroll(int n) {
      this.proxy.scrollBy(n);
   }

   public void renderList(int n, int n2, int n3, int n4) {
   }

   public int getIntValue() {
      return this.proxy.getAmountScrolled();
   }

   public int getMouseY() {
      return this.proxy.getMouseY();
   }

   public int getWidth() {
      return this.proxy.getWidth();
   }

   public int getMouseX() {
      return this.proxy.getMouseX();
   }
}
