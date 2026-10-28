package net.optifine;

import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiOptionSliderOF extends GuiOptionSlider implements IOptionControl {
    public Options option = null;

    public GuiOptionSliderOF(int n, int n2, int n3, Options options) {
        super(n, n2, n3, options);
        this.option = options;
    }

    @Override
    public Options getOption() {
        return this.option;
    }
}
