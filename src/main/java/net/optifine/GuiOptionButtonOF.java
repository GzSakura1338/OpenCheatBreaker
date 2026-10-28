package net.optifine;

import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiOptionButtonOF extends GuiOptionButton implements IOptionControl {
    public Options option = null;

    public GuiOptionButtonOF(int n, int n2, int n3, Options options, String str) {
        super(n, n2, n3, options, str);
        this.option = options;
    }

    @Override
    public Options getOption() {
        return this.option;
    }
}
