package net.optifine;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiQualitySettingsOF extends GuiScreen {
    public TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());
    public String title;
    public GuiScreen prevScreen;
    public static Options[] enumOptions = new Options[]{
        Options.MIPMAP_LEVELS,
        Options.MIPMAP_TYPE,
        Options.ANISOTROPIC_FILTERING,
        Options.AA_LEVEL,
        Options.CLEAR_WATER,
        Options.RANDOM_MOBS,
        Options.BETTER_GRASS,
        Options.BETTER_SNOW,
        Options.CUSTOM_FONTS,
        Options.CUSTOM_COLORS,
        Options.SWAMP_COLORS,
        Options.SMOOTH_BIOMES,
        Options.CONNECTED_TEXTURES,
        Options.NATURAL_TEXTURES,
        Options.CUSTOM_SKY
    };
    public GameSettings settings;

    public GuiQualitySettingsOF(GuiScreen guiScreen, GameSettings gameSettings) {
        this.prevScreen = guiScreen;
        this.settings = gameSettings;
    }

    @Override
    public void initGui() {
        this.title = I18n.format("of.options.qualityTitle");
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

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        if (guiButton.enabled) {
            if (guiButton.id < 200 && guiButton instanceof GuiOptionButton) {
                this.settings.setOptionValue(((GuiOptionButton)guiButton).returnEnumOptions(), 1);
                guiButton.displayString = this.settings.getKeyBinding(Options.getEnumOptions(guiButton.id));
            }

            if (guiButton.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.prevScreen);
            }

            if (guiButton.id != Options.AA_LEVEL.ordinal()) {
                ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
                this.setWorldAndResolution(this.mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
            }
        }
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
        super.drawScreen(n, n2, f);
        this.tooltipManager.drawTooltips(n, n2, this.buttonList);
    }
}
