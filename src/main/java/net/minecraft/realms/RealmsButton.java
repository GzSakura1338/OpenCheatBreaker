package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonRealmsProxy;
import net.minecraft.util.ResourceLocation;

public class RealmsButton {
   public static ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   public GuiButtonRealmsProxy proxy;

   public RealmsButton(int buttonId, int x, int y, String text) {
      this.proxy = new GuiButtonRealmsProxy(this, buttonId, x, y, text);
   }

   public RealmsButton(int buttonId, int x, int y, int widthIn, int heightIn, String text) {
      this.proxy = new GuiButtonRealmsProxy(this, buttonId, x, y, text, widthIn, heightIn);
   }

   public GuiButton getProxy() {
      return this.proxy;
   }

   public int id() {
      return this.proxy.getId();
   }

   public boolean active() {
      return this.proxy.getEnabled();
   }

   public void active(boolean flag) {
      this.proxy.setEnabled(flag);
   }

   public void msg(String str) {
      this.proxy.setText(str);
   }

   public void render(int n, int n2) {
      this.proxy.drawButton(Minecraft.getMinecraft(), n, n2);
   }

   public void clicked(int n, int n2) {
   }

   public void released(int n, int n2) {
   }

   public void blit(int n, int n2, int n3, int n4, int n5, int n6) {
      this.proxy.drawTexturedModalRect(n, n2, n3, n4, n5, n6);
   }

   public void renderBg(int n, int n2) {
   }

   public int getYImage(boolean flag) {
      return this.proxy.getDelegateYImage(flag);
   }

   public int getHeight() {
      return this.proxy.getHeight();
   }

   public int getPositionY() {
      return this.proxy.getPositionY();
   }

   public int getButtonWidth() {
      return this.proxy.getButtonWidth();
   }
}
