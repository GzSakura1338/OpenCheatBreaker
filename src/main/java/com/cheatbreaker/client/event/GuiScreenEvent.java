package com.cheatbreaker.client.event;

import net.minecraft.client.gui.GuiScreen;

public class GuiScreenEvent extends CancellableEvent {
   public GuiScreen guiScreenIn;
   public GuiScreen currentScreen;

   public GuiScreenEvent(GuiScreen guiScreen, GuiScreen guiScreen2) {
      this.guiScreenIn = guiScreen;
      this.currentScreen = guiScreen2;
   }

   public GuiScreen getGuiScreenIn() {
      return this.guiScreenIn;
   }

   public GuiScreen getCurrentScreen() {
      return this.currentScreen;
   }
}
