package com.cheatbreaker.client.minimap;

import net.minecraft.client.gui.GuiButton;

public class MinimapOptionButton extends GuiButton {
    public MinimapOption minimapOption;

    public MinimapOptionButton(int n, int n2, int n3, int n4, int n5, String str) {
        super(n, n2, n3, n4, n5, str);
        this.minimapOption = null;
    }

    public MinimapOptionButton(int n, int n2, int n3, String str) {
        this(n, n2, n3, null, str);
    }

    public MinimapOptionButton(int n, int n2, int n3, MinimapOption minimapOption, String str) {
        super(n, n2, n3, 150, 20, str);
        this.minimapOption = minimapOption;
    }

    public MinimapOption getMinimapOption() {
        return this.minimapOption;
    }
}
