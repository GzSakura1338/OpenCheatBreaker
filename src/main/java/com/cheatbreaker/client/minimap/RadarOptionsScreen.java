package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class RadarOptionsScreen extends AbstractMinimapScreen {
    public String title = "Radar Options";
    public GuiScreen guiScreen;
    public static MinimapOption[] minimapOptionArray = new MinimapOption[]{
        MinimapOption.SHOWRADAR,
        MinimapOption.RANDOMOBS,
        MinimapOption.SHOWHOSTILES,
        MinimapOption.SHOWNEUTRALS,
        MinimapOption.SHOWPLAYERS,
        MinimapOption.SHOWPLAYERNAMES,
        MinimapOption.SHOWPLAYERHELMETS,
        MinimapOption.SHOWMOBHELMETS,
        MinimapOption.RADARFILTERING,
        MinimapOption.RADAROUTLINES
    };
    public RadarSettings radarSettings;

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        if (guiButton.enabled) {
            if (guiButton.id < 100 && guiButton instanceof MinimapOptionButton) {
                this.radarSettings.toggleOption(((MinimapOptionButton)guiButton).getMinimapOption(), 1);
                guiButton.displayString = this.radarSettings.getOptionLabel(MinimapOption.fromId(guiButton.id));

                for (Object obj : this.getButtonList()) {
                    if (obj instanceof MinimapOptionButton) {
                        MinimapOptionButton minimapOptionButton = (MinimapOptionButton)obj;
                        if (!minimapOptionButton.getMinimapOption().equals(MinimapOption.SHOWRADAR)) {
                            minimapOptionButton.enabled = this.radarSettings.showRadar;
                        }

                        if (minimapOptionButton.getMinimapOption() == MinimapOption.SHOWPLAYERHELMETS || minimapOptionButton.getMinimapOption() == MinimapOption.SHOWPLAYERNAMES) {
                            minimapOptionButton.enabled = minimapOptionButton.enabled && this.radarSettings.showPlayers;
                        } else if (minimapOptionButton.getMinimapOption().equals(MinimapOption.SHOWMOBHELMETS)) {
                            minimapOptionButton.enabled = minimapOptionButton.enabled && (this.radarSettings.showNeutrals || this.radarSettings.showHostiles);
                        }
                    } else if (obj instanceof GuiButton) {
                        GuiButton guiButton2 = (GuiButton)obj;
                        if (guiButton2.id == 101) {
                            guiButton2.enabled = this.radarSettings.showRadar;
                        }
                    }
                }
            }

            if (guiButton.id == 101) {
                this.getMc().displayGuiScreen(new SelectMobsScreen(this, this.radarSettings));
            }

            if (guiButton.id == 200) {
                this.getMc().displayGuiScreen(this.guiScreen);
            }
        }
    }

    @Override
    public void initGui() {
        int n = 0;
        this.title = Translations.translate("options.minimap.radar.title");

        for (int i = 0; i < minimapOptionArray.length; i++) {
            MinimapOption minimapOption = minimapOptionArray[i];
            MinimapOptionButton minimapOptionButton = new MinimapOptionButton(
                minimapOption.getId(), this.getWidth() / 2 - 155 + n % 2 * 160, this.getHeight() / 6 + 24 * (n >> 1), minimapOption, this.radarSettings.getOptionLabel(minimapOption)
            );
            this.getButtonList().add(minimapOptionButton);
            n++;
        }

        for (Object obj : this.getButtonList()) {
            if (obj instanceof MinimapOptionButton) {
                MinimapOptionButton minimapOptionButton = (MinimapOptionButton)obj;
                if (!minimapOptionButton.getMinimapOption().equals(MinimapOption.SHOWRADAR)) {
                    minimapOptionButton.enabled = this.radarSettings.showRadar;
                }

                if (minimapOptionButton.getMinimapOption().equals(MinimapOption.SHOWPLAYERHELMETS) || minimapOptionButton.getMinimapOption().equals(MinimapOption.SHOWPLAYERNAMES)) {
                    minimapOptionButton.enabled = minimapOptionButton.enabled && this.radarSettings.showPlayers;
                } else if (minimapOptionButton.getMinimapOption().equals(MinimapOption.SHOWMOBHELMETS)) {
                    minimapOptionButton.enabled = minimapOptionButton.enabled && (this.radarSettings.showNeutrals || this.radarSettings.showHostiles);
                }
            }
        }

        this.getButtonList()
            .add(
                new GuiButton(101, this.getWidth() / 2 - 155, this.getHeight() / 6 + 144 - 6, 150, 20, Translations.translate("options.minimap.radar.selectmobs"))
            );
        this.getButtonList().add(new GuiButton(200, this.getWidth() / 2 - 100, this.getHeight() / 6 + 168, Translations.translate("gui.done")));
    }

    public RadarOptionsScreen(GuiScreen guiScreen, IVoxelMap voxelMap) {
        this.guiScreen = guiScreen;
        this.radarSettings = voxelMap.getRadarSettings();
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        super.drawMinimapBackground();
        this.drawDefaultBackground();
        this.drawCenteredString(this.getFontRendererObj(), this.title, this.getWidth() / 2, 20, 16777215);
        super.drawScreen(n, n2, f);
    }
}
