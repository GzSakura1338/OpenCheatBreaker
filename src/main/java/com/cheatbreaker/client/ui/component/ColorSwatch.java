package com.cheatbreaker.client.ui.component;

import net.minecraft.client.gui.Gui;

public class ColorSwatch extends SettingComponent {
    public int color;
    public float unusedFloat;

    public ColorSwatch(float f, int n, float f2) {
        super(f);
        this.color = n;
        this.unusedFloat = f2;
    }

    @Override
    public void render(int n, int n2, float f) {
        Gui.drawRectWithInsetBorder(this.x, this.y, this.x + this.width, this.y + this.height, 1.0F, this.theme.getTextColor4Value(), 0);
        Gui.drawRect(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.color | 0xFF000000);
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) {
    }
}
