package com.cheatbreaker.client.animation;

import java.awt.Color;
import net.minecraft.util.MathHelper;

public class ColorAnimation extends EaseOutAnimation {
    public boolean wasActive;
    public Color fromColor;
    public Color toColor;
    public int inactiveColor;
    public int activeColor;

    public void setActiveColor(int n) {
        this.activeColor = n;
    }

    public void setInactiveColor(int n) {
        this.inactiveColor = n;
    }

    public Color getColor(boolean flag) {
        Color color = new Color(flag ? this.activeColor : this.inactiveColor, true);
        if (flag && !this.wasActive) {
            this.wasActive = true;
            this.fromColor = new Color(this.inactiveColor, true);
            this.toColor = new Color(this.activeColor, true);
            this.start();
        } else if (this.wasActive && !flag) {
            this.wasActive = false;
            this.fromColor = new Color(this.activeColor, true);
            this.toColor = new Color(this.inactiveColor, true);
            this.start();
        }

        if (this.isRunning()) {
            float progress = super.getProgress();
            float f = 1.0F - progress;
            int clampColorComponent = this.clampColorComponent(progress * this.toColor.getRed() + f * this.fromColor.getRed());
            int clampColorComponent2 = this.clampColorComponent(progress * this.toColor.getGreen() + f * this.fromColor.getGreen());
            int clampColorComponent3 = this.clampColorComponent(progress * this.toColor.getBlue() + f * this.fromColor.getBlue());
            int clampColorComponent4 = this.clampColorComponent(progress * this.toColor.getAlpha() + f * this.fromColor.getAlpha());
            color = new Color(clampColorComponent, clampColorComponent2, clampColorComponent3, clampColorComponent4);
        }

        return color;
    }

    public ColorAnimation(long l, int n, int n2) {
        super(l);
        this.inactiveColor = n;
        this.activeColor = n2;
    }

    public int clampColorComponent(float f) {
        return MathHelper.clamp_int((int)Math.abs(f), 0, 255);
    }

    public ColorAnimation(int n, int n2) {
        this(175L, n, n2);
    }
}
