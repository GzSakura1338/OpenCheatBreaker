package com.cheatbreaker.client.event;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;

public class GuiScreenServerDataEvent extends SimpleEvent45 {
   public ServerData serverData;
   public GuiScreen guiScreen;

   public void setGuiScreen(GuiScreen guiScreen) {
      this.guiScreen = guiScreen;
   }

   public GuiScreen getGuiScreen() {
      return this.guiScreen;
   }

   public ServerData getServerData() {
      return this.serverData;
   }

   public GuiScreenServerDataEvent(ServerData serverData, GuiScreen guiScreen) {
      this.serverData = serverData;
      this.guiScreen = guiScreen;
   }

   public void setServerData(ServerData serverData) {
      this.serverData = serverData;
   }
}
