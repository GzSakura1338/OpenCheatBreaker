package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanScan implements GuiListExtended.IGuiListEntry {
   public Minecraft mc = Minecraft.getMinecraft();

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      int n = y + slotHeight / 2 - this.mc.fontRendererObj.FONT_HEIGHT / 2;
      this.mc
         .fontRendererObj
         .drawString(
            I18n.format("lanServer.scanning"),
            this.mc.currentScreen.width / 2 - this.mc.fontRendererObj.getStringWidth(I18n.format("lanServer.scanning")) / 2,
            n,
            16777215
         );
      String str;
      switch ((int)(Minecraft.getSystemTime() / 300L % 4L)) {
         case 0:
         default:
            str = "O o o";
            break;
         case 1:
         case 3:
            str = "o O o";
            break;
         case 2:
            str = "o o O";
      }

      this.mc
         .fontRendererObj
         .drawString(
            str, this.mc.currentScreen.width / 2 - this.mc.fontRendererObj.getStringWidth(str) / 2, n + this.mc.fontRendererObj.FONT_HEIGHT, 8421504
         );
   }

   @Override
   public void setSelected(int n, int n2, int n3) {
   }

   @Override
   public boolean mousePressed(int slotIndex, int n, int n2, int n3, int n4, int n5) {
      return false;
   }

   @Override
   public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
   }
}
