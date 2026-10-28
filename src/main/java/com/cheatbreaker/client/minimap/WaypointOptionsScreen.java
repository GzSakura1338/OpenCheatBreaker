package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class WaypointOptionsScreen extends AbstractMinimapScreen {
    public String title = "Radar Options";
    public GuiScreen guiScreen;
    public static MinimapOption[] minimapOptionArray = new MinimapOption[]{
        MinimapOption.WAYPOINTDISTANCE, MinimapOption.DEATHPOINTS, MinimapOption.WAYPOINTBORDER, MinimapOption.WAYPOINTTEXTSHADOW
    };
    public MinimapSettings minimapSettings;

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        if (guiButton.enabled) {
            if (guiButton.id < 100 && guiButton instanceof MinimapOptionButton) {
                this.minimapSettings.toggleOption(((MinimapOptionButton)guiButton).getMinimapOption(), 1);
                guiButton.displayString = this.minimapSettings.getOptionLabel(MinimapOption.fromId(guiButton.id));
            }

            if (guiButton.id == 200) {
                this.getMc().displayGuiScreen(this.guiScreen);
            }
        }
    }

    @Override
    public void initGui() {
        int n = 0;
        this.title = Translations.translate("options.minimap.waypoints.title");

        for (int i = 0; i < minimapOptionArray.length; i++) {
            MinimapOption minimapOption = minimapOptionArray[i];
            if (minimapOption.isFloat()) {
                float floatOption = this.minimapSettings.getFloatOption(minimapOption);
                if (floatOption < 0.0F) {
                    floatOption = 10001.0F;
                }

                floatOption = (floatOption - 50.0F) / 9951.0F;
                this.getButtonList()
                    .add(
                        new SliderButton(
                            minimapOption.getId(), this.getWidth() / 2 - 155 + n % 2 * 160, this.getHeight() / 6 + 24 * (n >> 1), minimapOption, floatOption, this.minimapSettings
                        )
                    );
            } else {
                MinimapOptionButton minimapOptionButton = new MinimapOptionButton(
                    minimapOption.getId(),
                    this.getWidth() / 2 - 155 + n % 2 * 160,
                    this.getHeight() / 6 + 24 * (n >> 1),
                    minimapOption,
                    this.minimapSettings.getOptionLabel(minimapOption)
                );
                this.getButtonList().add(minimapOptionButton);
            }

            n++;
        }

        this.getButtonList().add(new GuiButton(200, this.getWidth() / 2 - 100, this.getHeight() / 6 + 168, Translations.translate("gui.done")));
    }

    public WaypointOptionsScreen(GuiScreen guiScreen, MinimapSettings minimapSettings) {
        this.guiScreen = guiScreen;
        this.minimapSettings = minimapSettings;
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        super.drawMinimapBackground();
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.getWidth() / 2, 20, 16777215);
        super.drawScreen(n, n2, f);
    }
}
