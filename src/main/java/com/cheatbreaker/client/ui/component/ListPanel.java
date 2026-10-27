package com.cheatbreaker.client.ui.component;

import java.util.ArrayList;
import java.util.List;

public class ListPanel<T extends UIElement> extends UIElement {
   public List<T> list = new ArrayList<>();

   @Override
   public void keyTyped(char c, int n) {
      this.list.forEach(value -> value.keyTyped(c, n));
   }

   @Override
   public boolean mouseReleased(float f, float f2, int n, boolean flag) {
      if (!flag) {
         return false;
      }

      boolean flag2 = false;

      for (UIElement uielement : this.list) {
         if (flag2) {
            break;
         }

         flag2 = uielement.mouseReleased(f, f2, n, flag);
      }

      return flag2;
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      if (!flag) {
         return false;
      }

      boolean flag2 = false;

      for (UIElement uielement : this.list) {
         if (flag2) {
            break;
         }

         flag2 = uielement.mouseClicked(f, f2, n, flag);
      }

      return flag2;
   }

   public ListPanel(List<T> list) {
      this.list.addAll(list);
   }

   @Override
   public void onClose() {
      this.list.forEach(UIElement::onClose);
   }

   @Override
   public void tick() {
      this.list.forEach(UIElement::tick);
   }

   public List<T> getList() {
      return this.list;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      for (UIElement uielement : this.list) {
         uielement.renderAndTestHover(f, f2, flag);
      }
   }
}
