package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.LanServerDetector.LanServer;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanDetected implements GuiListExtended.IGuiListEntry {
   public long time = 0L;
   public GuiMultiplayer parent;
   public LanServer serverInfo;
   public Minecraft mc;

   public ServerListEntryLanDetected(GuiMultiplayer guiMultiplayer, LanServer lanServer) {
      this.parent = guiMultiplayer;
      this.serverInfo = lanServer;
      this.mc = Minecraft.getMinecraft();
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      this.mc.fontRendererObj.drawString(I18n.format("lanServer.title"), x + 32 + 3, y + 1, 16777215);
      this.mc.fontRendererObj.drawString(this.serverInfo.getServerMotd(), x + 32 + 3, y + 12, 8421504);
      if (this.mc.gameSettings.hideServerAddress) {
         this.mc.fontRendererObj.drawString(I18n.format("selectServer.hiddenAddress"), x + 32 + 3, y + 12 + 11, 3158064);
      } else {
         this.mc.fontRendererObj.drawString(this.serverInfo.getServerIpPort(), x + 32 + 3, y + 12 + 11, 3158064);
      }
   }

   @Override
   public boolean mousePressed(int slotIndex, int n, int n2, int n3, int n4, int n5) {
      this.parent.selectServer(slotIndex);
      if (Minecraft.getSystemTime() - this.time < 250L) {
         this.parent.connectToSelected();
      }

      this.time = Minecraft.getSystemTime();
      return false;
   }

   @Override
   public void setSelected(int n, int n2, int n3) {
   }

   @Override
   public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
   }

   public LanServer getLanServer() {
      return this.serverInfo;
   }
}
