package net.minecraft.client.gui;

import net.minecraft.client.settings.GameSettings.Options;

public class GuiOptionButton extends GuiButton {
    public Options enumOptions;

    public GuiOptionButton(int n, int n2, int n3, String str) {
        this(n, n2, n3, null, str);
    }

    public GuiOptionButton(int n, int n2, int n3, int n4, int n5, String str) {
        super(n, n2, n3, n4, n5, str);
        this.enumOptions = null;
    }

    public GuiOptionButton(int n, int n2, int n3, Options options, String str) {
        super(n, n2, n3, 150, 20, str);
        this.enumOptions = options;
    }

    public Options returnEnumOptions() {
        return this.enumOptions;
    }
}
