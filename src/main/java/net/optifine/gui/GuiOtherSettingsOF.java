package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiOtherSettingsOF extends GuiScreen implements GuiYesNoCallback {
   public TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());
   public String title;
   public GuiScreen prevScreen;
   public static Options[] enumOptions = new Options[]{
      Options.LAGOMETER,
      Options.PROFILER,
      Options.SHOW_FPS,
      Options.ADVANCED_TOOLTIPS,
      Options.WEATHER,
      Options.TIME,
      Options.USE_FULLSCREEN,
      Options.FULLSCREEN_MODE,
      Options.ANAGLYPH,
      Options.AUTOSAVE_TICKS,
      Options.SCREENSHOT_SIZE,
      Options.SHOW_GL_ERRORS
   };
   public GameSettings settings;

   public GuiOtherSettingsOF(GuiScreen guiScreen, GameSettings gameSettings) {
      this.prevScreen = guiScreen;
      this.settings = gameSettings;
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.otherTitle");
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

      this.buttonList.add(new GuiButton(210, this.width / 2 - 100, this.height / 6 + 168 + 11 - 44, I18n.format("of.options.other.reset")));
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
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
            this.mc.gameSettings.saveOptions();
            GuiYesNo guiYesNo = new GuiYesNo(this, I18n.format("of.message.other.reset"), "", 9999);
            this.mc.displayGuiScreen(guiYesNo);
         }
      }
   }

   @Override
   public void confirmClicked(boolean flag, int n) {
      if (flag) {
         this.mc.gameSettings.resetSettings();
      }

      this.mc.displayGuiScreen(this);
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      super.drawScreen(n, n2, f);
      this.tooltipManager.drawTooltips(n, n2, this.buttonList);
   }
}
