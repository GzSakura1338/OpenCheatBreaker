package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.optifine.Config;
import net.optifine.GuiAnimationSettingsOF;
import net.optifine.GuiDetailSettingsOF;
import net.optifine.GuiOptionButtonOF;
import net.optifine.GuiOptionSliderOF;
import net.optifine.GuiOtherSettingsOF;
import net.optifine.GuiPerformanceSettingsOF;
import net.optifine.GuiQualitySettingsOF;
import net.optifine.GuiScreenOF;
import net.optifine.Lang;
import net.optifine.TooltipManager;
import net.optifine.TooltipProviderOptions;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import shadersmod.client.GuiShaders;

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
        Options.ADVANCED_OPENGL,
        Options.GAMMA,
        Options.CHUNK_LOADING,
        Options.DYNAMIC_LIGHTS,
        Options.DYNAMIC_FOV
    };
    public GameSettings guiGameSettings;

    public GuiVideoSettings(GuiScreen guiScreen, GameSettings gameSettings) {
        this.parentGuiScreen = guiScreen;
        this.guiGameSettings = gameSettings;
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
        boolean flag = false;
        int n2 = this.width / 2 - 155 + 0;
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
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        this.actionPerformed(button, 1);
    }

    @Override
    public void actionPerformedRightClick(GuiButton guiButton) throws java.io.IOException {
        if (guiButton.id == Options.GUI_SCALE.ordinal()) {
            this.actionPerformed(guiButton, -1);
        }
    }

    public void actionPerformed(GuiButton guiButton, int n) throws java.io.IOException {
        if (guiButton.enabled) {
            if (guiButton.id == Options.CHUNK_LOADING.ordinal()) {
                return;
            }

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
                ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 15, 16777215);
        String version = Config.getVersion();
        String str = "HD_U";
        if (str.equals("HD")) {
            version = "OptiFine HD E7";
        }

        if (str.equals("HD_U")) {
            version = "OptiFine HD E7 Ultra";
        }

        if (str.equals("L")) {
            version = "OptiFine E7 Light";
        }

        this.drawString(this.fontRendererObj, version, 2, this.height - 10, 8421504);
        String str2 = "Minecraft 1.7.10";
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
        GuiScreen.drawVerticalGradient(n, n2, n3, n4, n5, n6);
    }

    @Override
    public void handleMouseInput() throws java.io.IOException {
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
