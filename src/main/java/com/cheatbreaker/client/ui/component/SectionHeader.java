package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.gui.Gui;

public class SectionHeader extends UIElement {
    public String title;

    @Override
    public void render(float f, float f2, boolean flag) {
        this.drawHeader(this.title, f, f2, flag);
    }

    @Override
    public boolean mouseClicked(float f, float f2, int n, boolean flag) {
        return !flag ? false : false;
    }

    public SectionHeader(String str) {
        this.title = str;
    }

    public String getTitle() {
        return this.title;
    }

    public void drawHeader(String str, float f, float f2, boolean flag) {
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, flag && this.containsPoint(f, f2) ? -16747106 : -13158601);
        float f3 = this.x + this.width / 2.0F;
        CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString(str, f3, this.y + this.height / 2.0F - 5.0F, -1);
    }

    public void setTitle(String str) {
        this.title = str;
    }
}
