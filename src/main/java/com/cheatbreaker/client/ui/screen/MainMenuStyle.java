package com.cheatbreaker.client.ui.screen;

import net.minecraft.client.gui.GuiScreen;

public enum MainMenuStyle {
   OLD,
   MAIN;

   public static GuiScreen createScreen(MainMenuStyle mainMenuStyle) {
      if (mainMenuStyle == null) {
         return new MainMenuScreen();
      } else {
         return mainMenuStyle == OLD ? new OldMainMenuScreen() : new MainMenuScreen();
      }
   }

   public static MainMenuStyle fromName(String str) {
      return Enum.valueOf(MainMenuStyle.class, str);
   }
}
