package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;

public class StatusLabel extends UIElement {
    public String label;
    public ColorAnimation colorAnimation;

    public StatusLabel(String str) {
        this.label = str;
        this.colorAnimation = new ColorAnimation(-1879048193, -1);
    }

    @Override
    public void render(float f, float f2, boolean flag) {
        float f3 = this.x + 6.0F;
        float f4 = this.y + 6.0F;
        int n = !this.enabled ? 1342177279 : this.colorAnimation.getColor(this.containsPoint(f, f2) && flag).getRGB();
        CheatBreaker.getInstance().getFontManager().robotoBold14.drawString(this.label, f3, f4, n);
    }
}
