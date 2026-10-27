package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.optifine.Config;
import net.optifine.Lang;
import net.optifine.gui.GuiAnimationSettingsOF;
import net.optifine.gui.GuiDetailSettingsOF;
import net.optifine.gui.GuiOptionButtonOF;
import net.optifine.gui.GuiOptionSliderOF;
import net.optifine.gui.GuiOtherSettingsOF;
import net.optifine.gui.GuiPerformanceSettingsOF;
import net.optifine.gui.GuiQualitySettingsOF;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderOptions;
import net.optifine.shaders.gui.GuiShaders;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiVideoSettings extends GuiScreenOF {
   public String screenTitle = "Video Settings";
   public TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());
   public GuiScreen parentGuiScreen;
   public static Options[] videoOptions = new Options[]{
      Options.GRAPHICS,
      Options.RENDER_DISTANCE,
      Options.AMBIENT_OCCLUSION,
      Options.FRAMERATE_LIMIT,
      Options.AO_LEVEL,
      Options.VIEW_BOBBING,
      Options.GUI_SCALE,
      Options.USE_VBO,
      Options.GAMMA,
      Options.BLOCK_ALTERNATIVES,
      Options.DYNAMIC_LIGHTS,
      Options.DYNAMIC_FOV
   };
   public GameSettings guiGameSettings;

   public GuiVideoSettings(GuiScreen parentScreenIn, GameSettings gameSettingsIn) {
      this.parentGuiScreen = parentScreenIn;
      this.guiGameSettings = gameSettingsIn;
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("options.videoTitle");
      this.buttonList.clear();

      for (int i = 0; i < videoOptions.length; i++) {
         Options options = videoOptions[i];
         if (options != null) {
            int n = this.width / 2 - 155 + i % 2 * 160;
            int n2 = this.height / 6 + 21 * (i / 2) - 12;
            if (options.getEnumFloat()) {
               this.buttonList.add(new GuiOptionSliderOF(options.returnEnumOrdinal(), n, n2, options));
            } else {
               this.buttonList.add(new GuiOptionButtonOF(options.returnEnumOrdinal(), n, n2, options, this.guiGameSettings.getKeyBinding(options)));
            }
         }
      }

      int n = this.height / 6 + 21 * (videoOptions.length / 2) - 12;
      int n2 = 0;
      n2 = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(231, n2, n, Lang.get("of.options.shaders")));
      n2 = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(202, n2, n, Lang.get("of.options.quality")));
      n += 21;
      n2 = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(201, n2, n, Lang.get("of.options.details")));
      n2 = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(212, n2, n, Lang.get("of.options.performance")));
      n += 21;
      n2 = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(211, n2, n, Lang.get("of.options.animations")));
      n2 = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(222, n2, n, Lang.get("of.options.other")));
      n += 21;
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      this.actionPerformed(button, 1);
   }

   @Override
   public void actionPerformedRightClick(GuiButton guiButton) {
      if (guiButton.id == Options.GUI_SCALE.ordinal()) {
         this.actionPerformed(guiButton, -1);
      }
   }

   public void actionPerformed(GuiButton guiButton, int n) {
      if (guiButton.enabled) {
         int guiScale = this.guiGameSettings.guiScale;
         if (guiButton.id < 200 && guiButton instanceof GuiOptionButton) {
            this.guiGameSettings.setOptionValue(((GuiOptionButton)guiButton).returnEnumOptions(), n);
            guiButton.displayString = this.guiGameSettings.getKeyBinding(Options.getEnumOptions(guiButton.id));
         }

         if (guiButton.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentGuiScreen);
         }

         if (this.guiGameSettings.guiScale != guiScale) {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            int scaledWidth = scaledResolution.getScaledWidth();
            int scaledHeight = scaledResolution.getScaledHeight();
            this.setWorldAndResolution(this.mc, scaledWidth, scaledHeight);
         }

         if (guiButton.id == 201) {
            this.mc.gameSettings.saveOptions();
            GuiDetailSettingsOF guiDetailSettingsOF = new GuiDetailSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiDetailSettingsOF);
         }

         if (guiButton.id == 202) {
            this.mc.gameSettings.saveOptions();
            GuiQualitySettingsOF guiQualitySettingsOF = new GuiQualitySettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiQualitySettingsOF);
         }

         if (guiButton.id == 211) {
            this.mc.gameSettings.saveOptions();
            GuiAnimationSettingsOF guiAnimationSettingsOF = new GuiAnimationSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiAnimationSettingsOF);
         }

         if (guiButton.id == 212) {
            this.mc.gameSettings.saveOptions();
            GuiPerformanceSettingsOF guiPerformanceSettingsOF = new GuiPerformanceSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiPerformanceSettingsOF);
         }

         if (guiButton.id == 222) {
            this.mc.gameSettings.saveOptions();
            GuiOtherSettingsOF guiOtherSettingsOF = new GuiOtherSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiOtherSettingsOF);
         }

         if (guiButton.id == 231) {
            if (Config.isAntialiasing() || Config.isAntialiasingConfigured()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.aa1"), Lang.get("of.message.shaders.aa2"));
               return;
            }

            if (Config.isAnisotropicFiltering()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.af1"), Lang.get("of.message.shaders.af2"));
               return;
            }

            if (Config.isFastRender()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.fr1"), Lang.get("of.message.shaders.fr2"));
               return;
            }

            if (Config.getGameSettings().anaglyph) {
               Config.showGuiMessage(Lang.get("of.message.shaders.an1"), Lang.get("of.message.shaders.an2"));
               return;
            }

            this.mc.gameSettings.saveOptions();
            GuiShaders guiShaders = new GuiShaders(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiShaders);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 15, 16777215);
      String version = Config.getVersion();
      String str = "HD_U";
      if (str.equals("HD")) {
         version = "OptiFine HD M6_pre2";
      }

      if (str.equals("HD_U")) {
         version = "OptiFine HD M6_pre2 Ultra";
      }

      if (str.equals("L")) {
         version = "OptiFine M6_pre2 Light";
      }

      this.drawString(this.fontRendererObj, version, 2, this.height - 10, 8421504);
      String str2 = "Minecraft 1.8.9";
      int stringWidth = this.fontRendererObj.getStringWidth(str2);
      this.drawString(this.fontRendererObj, str2, this.width - stringWidth - 2, this.height - 10, 8421504);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.tooltipManager.drawTooltips(mouseX, mouseY, this.buttonList);
   }

   public static int getButtonWidth(GuiButton guiButton) {
      return guiButton.width;
   }

   public static int getButtonHeight(GuiButton guiButton) {
      return guiButton.height;
   }

   public static void drawGradientRect(GuiScreen guiScreen, int n, int n2, int n3, int n4, int n5, int n6) {
      guiScreen.drawGradientRect(n, n2, n3, n4, n5, n6);
   }

   public static String getGuiChatText(GuiChat guiChat) {
      return guiChat.inputField.getText();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      if (Keyboard.isKeyDown(29)) {
         int eventDWheel = Mouse.getEventDWheel();
         if (eventDWheel != 0) {
            int guiScale = this.guiGameSettings.guiScale;
            this.guiGameSettings.setOptionValue(Options.GUI_SCALE, eventDWheel < 0 ? -1 : 1);
            if (this.guiGameSettings.guiScale != guiScale) {
               ScaledResolution scaledResolution = new ScaledResolution(this.mc);
               int scaledWidth = scaledResolution.getScaledWidth();
               int scaledHeight = scaledResolution.getScaledHeight();
               this.setWorldAndResolution(this.mc, scaledWidth, scaledHeight);
            }
         }
      }
   }
}
