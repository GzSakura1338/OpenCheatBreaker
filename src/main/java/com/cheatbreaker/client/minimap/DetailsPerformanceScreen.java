package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class DetailsPerformanceScreen extends AbstractMinimapScreen {
    public String title = "Details / Performance";
    public static MinimapOption[] minimapOptionArray = new MinimapOption[]{
        MinimapOption.LIGHTING,
        MinimapOption.TERRAIN,
        MinimapOption.WATERTRANSPARENCY,
        MinimapOption.BLOCKTRANSPARENCY,
        MinimapOption.BIOMES,
        MinimapOption.FILTERING,
        MinimapOption.CHUNKGRID,
        MinimapOption.BIOMEOVERLAY
    };
    public final int intValue = -1;
    public GuiScreen guiScreen;
    public MinimapSettings minimapSettings;

    public int getIntValue() {
        return this.getWidth() / 2 - 155;
    }

    public DetailsPerformanceScreen(GuiScreen guiScreen, IVoxelMap voxelMap) {
        this.guiScreen = guiScreen;
        this.minimapSettings = voxelMap.getMapOptions();
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        super.drawMinimapBackground();
        this.drawDefaultBackground();
        this.drawCenteredString(this.getFontRendererObj(), this.title, this.getWidth() / 2, 20, 16777215);
        super.drawScreen(n, n2, f);
    }

    @Override
    public void initGui() {
        this.title = Translations.translate("options.minimap.detailsperformance");
        int intValue = this.getIntValue();
        int n = 0;

        for (int i = 0; i < minimapOptionArray.length; i++) {
            MinimapOption minimapOption = minimapOptionArray[i];
            String optionLabel = this.minimapSettings.getOptionLabel(minimapOption);
            if ((minimapOption == MinimapOption.WATERTRANSPARENCY || minimapOption == MinimapOption.BLOCKTRANSPARENCY || minimapOption == MinimapOption.BIOMES)
                && !this.minimapSettings.multicore
                && this.minimapSettings.getBooleanOption(minimapOption)) {
                optionLabel = "§c" + optionLabel;
            }

            MinimapOptionButton minimapOptionButton = new MinimapOptionButton(minimapOption.getId(), intValue + n % 2 * 160, this.getHeight() / 6 + 24 * (n >> 1), minimapOption, optionLabel);
            this.buttonList.add(minimapOptionButton);
            n++;
        }

        this.buttonList.add(new GuiButton(200, this.getWidth() / 2 - 100, this.getHeight() / 6 + 168, Translations.translate("gui.done")));
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        if (guiButton.id < 100 && guiButton instanceof MinimapOptionButton) {
            this.minimapSettings.toggleOption(((MinimapOptionButton)guiButton).getMinimapOption(), 1);
            String str = "";
            if ((
                    guiButton.id == MinimapOption.WATERTRANSPARENCY.ordinal()
                        || guiButton.id == MinimapOption.BLOCKTRANSPARENCY.ordinal()
                        || guiButton.id == MinimapOption.BIOMES.ordinal()
                )
                && !this.minimapSettings.multicore
                && this.minimapSettings.getBooleanOption(MinimapOption.fromId(guiButton.id))) {
                str = "§c";
            }

            guiButton.displayString = str + this.minimapSettings.getOptionLabel(MinimapOption.fromId(guiButton.id));
        }

        if (guiButton.id == 200) {
            this.getMc().displayGuiScreen(this.guiScreen);
        }
    }
}
