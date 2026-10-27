package net.minecraft.client.gui;

import com.cheatbreaker.client.ui.screen.ClassicMainMenuScreen;
import java.io.IOException;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.optifine.CustomLoadingScreen;
import net.optifine.CustomLoadingScreens;

public class GuiDownloadTerrain extends GuiScreen {
   public CustomLoadingScreen customLoadingScreen = CustomLoadingScreens.getCustomLoadingScreen();
   public int progress;
   public NetHandlerPlayClient netHandlerPlayClient;

   public GuiDownloadTerrain(NetHandlerPlayClient netHandler) {
      this.netHandlerPlayClient = netHandler;
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
   }

   @Override
   public void updateScreen() {
      this.progress++;
      if (this.progress % 20 == 0) {
         this.netHandlerPlayClient.addToSendQueue(new C00PacketKeepAlive());
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.customLoadingScreen != null) {
         this.customLoadingScreen.drawBackground(this.width, this.height);
      } else {
         this.drawBackground(0);
      }

      this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (this.mc.getCurrentServerData() != null && this.mc.theWorld != null) {
         this.mc.theWorld.sendQuittingDisconnectingPacket();
         this.mc.loadWorld((WorldClient)null);
      }

      if (guiButton.id == 1) {
         this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, this.mc.serverData));
      }

      if (guiButton.id == 2) {
         this.mc.displayGuiScreen(new ClassicMainMenuScreen());
      }
   }
}
