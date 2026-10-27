package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class RealmsEditBox {
   public GuiTextField editBox;

   public RealmsEditBox(int n, int n2, int n3, int n4, int n5) {
      this.editBox = new GuiTextField(n, Minecraft.getMinecraft().fontRendererObj, n2, n3, n4, n5);
   }

   public String getValue() {
      return this.editBox.getText();
   }

   public void setValue(String str) {
      this.editBox.setText(str);
   }

   public void keyPressed(char c, int n) {
      this.editBox.textboxKeyTyped(c, n);
   }

   public boolean isFocused() {
      return this.editBox.isFocused();
   }

   public void mouseClicked(int n, int n2, int n3) {
      this.editBox.mouseClicked(n, n2, n3);
   }

   public void setMaxLength(int n) {
      this.editBox.setMaxStringLength(n);
   }

   public void setFocused(boolean flag) {
      this.editBox.setFocused(flag);
   }

   public void updateCursorCounter() {
      this.editBox.updateCursorCounter();
   }

   public void drawTextBox() {
      this.editBox.drawTextBox();
   }

   public void setEnabled(boolean flag) {
      this.editBox.setEnabled(flag);
   }
}
