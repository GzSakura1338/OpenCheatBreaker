package net.optifine;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiDetailSettingsOF extends GuiScreen {
    public TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());
    public String title;
    public GuiScreen prevScreen;
    public static Options[] enumOptions = new Options[]{
        Options.CLOUDS,
        Options.CLOUD_HEIGHT,
        Options.TREES,
        Options.GRASS,
        Options.WATER,
        Options.RAIN,
        Options.SKY,
        Options.STARS,
        Options.SUN_MOON,
        Options.SHOW_CAPES,
        Options.FOG_FANCY,
        Options.FOG_START,
        Options.DEPTH_FOG,
        Options.HELD_ITEM_TOOLTIPS,
        Options.TRANSLUCENT_BLOCKS,
        Options.DROPPED_ITEMS,
        Options.VIGNETTE
    };
    public GameSettings settings;

    public GuiDetailSettingsOF(GuiScreen guiScreen, GameSettings gameSettings) {
        this.prevScreen = guiScreen;
        this.settings = gameSettings;
    }

    @Override
    public void initGui() {
        this.title = I18n.format("of.options.detailsTitle");
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
