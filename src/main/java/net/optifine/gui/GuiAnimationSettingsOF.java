package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.optifine.Lang;

public class GuiAnimationSettingsOF extends GuiScreen {
   public GuiScreen prevScreen;
   public String title;
   public GameSettings settings;
   public static Options[] enumOptions = new Options[]{
      Options.ANIMATED_WATER,
      Options.ANIMATED_LAVA,
      Options.ANIMATED_FIRE,
      Options.ANIMATED_PORTAL,
      Options.ANIMATED_REDSTONE,
      Options.ANIMATED_EXPLOSION,
      Options.ANIMATED_FLAME,
      Options.ANIMATED_SMOKE,
      Options.VOID_PARTICLES,
      Options.WATER_PARTICLES,
      Options.RAIN_SPLASH,
      Options.PORTAL_PARTICLES,
      Options.POTION_PARTICLES,
      Options.DRIPPING_WATER_LAVA,
      Options.ANIMATED_TERRAIN,
      Options.ANIMATED_TEXTURES,
      Options.FIREWORK_PARTICLES,
      Options.PARTICLES
   };

   public GuiAnimationSettingsOF(GuiScreen guiScreen, GameSettings gameSettings) {
      this.prevScreen = guiScreen;
      this.settings = gameSettings;
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.animationsTitle");
      this.buttonList.clear();

      for (int i = 0; i < enumOptions.length; i++) {
         Options options = enumOptions[i];
         int n = this.width / 2 - 155 + i % 2 * 160;
         int n2 = this.height / 6 + 21 * (i / 2) - 12;
         if (!options.getEnumFloat()) {
            this.buttonList.add(new GuiOptionButtonOF(options.returnEnumOrdinal(), n, n2, options, this.settings.getKeyBinding(options)));
         } else {
            this.buttonList.add(new GuiOptionSliderOF(options.returnEnumOrdinal(), n, n2, options));
         }
      }

      this.buttonList.add(new GuiButton(210, this.width / 2 - 155, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOn")));
      this.buttonList.add(new GuiButton(211, this.width / 2 - 155 + 80, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOff")));
      this.buttonList.add(new GuiOptionButton(200, this.width / 2 + 5, this.height / 6 + 168 + 11, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.enabled) {
         if (guiButton.id < 200 && guiButton instanceof GuiOptionButton) {
            this.settings.setOptionValue(((GuiOptionButton)guiButton).returnEnumOptions(), 1);
            guiButton.displayString = this.settings.getKeyBinding(Options.getEnumOptions(guiButton.id));
         }

         if (guiButton.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.prevScreen);
         }

         if (guiButton.id == 210) {
            this.mc.gameSettings.setAllAnimations(true);
         }

         if (guiButton.id == 211) {
            this.mc.gameSettings.setAllAnimations(false);
         }

         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         this.setWorldAndResolution(this.mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
      }
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      super.drawScreen(n, n2, f);
   }
}
