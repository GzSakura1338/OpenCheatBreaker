package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiListButton extends GuiButton {
   public String localizationStr;
   public boolean value;
   public GuiPageButtonList.GuiResponder guiResponder;

   public GuiListButton(GuiPageButtonList.GuiResponder responder, int n, int n2, int n3, String str, boolean flag) {
      super(n, n2, n3, 150, 20, "");
      this.localizationStr = str;
      this.value = flag;
      this.displayString = this.buildDisplayString();
      this.guiResponder = responder;
   }

   public String buildDisplayString() {
      return I18n.format(this.localizationStr) + ": " + (this.value ? I18n.format("gui.yes") : I18n.format("gui.no"));
   }

   public void setValue(boolean flag) {
      this.value = flag;
      this.displayString = this.buildDisplayString();
      this.guiResponder.setBooleanValue(this.id, flag);
   }

   @Override
   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         this.value = !this.value;
         this.displayString = this.buildDisplayString();
         this.guiResponder.setBooleanValue(this.id, this.value);
         return true;
      } else {
         return false;
      }
   }
}
