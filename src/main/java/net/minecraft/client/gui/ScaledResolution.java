package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class ScaledResolution {
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;
    public double scaledWidthD;
    public double scaledHeightD;

    public ScaledResolution(Minecraft mc, int n, int n2) {
        this.scaledWidth = n;
        this.scaledHeight = n2;
        this.scaleFactor = 1;
        boolean isUnicode = mc.isUnicode();
        int guiScale = mc.gameSettings.guiScale;
        if (guiScale == 0) {
            guiScale = 1000;
        }

        while (this.scaleFactor < guiScale && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            this.scaleFactor++;
        }

        if (isUnicode && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            this.scaleFactor--;
        }

        this.scaledWidthD = (double)this.scaledWidth / this.scaleFactor;
        this.scaledHeightD = (double)this.scaledHeight / this.scaleFactor;
        this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
        this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }

    public double getScaledWidth_double() {
        return this.scaledWidthD;
    }

    public double getScaledHeight_double() {
        return this.scaledHeightD;
    }

    public int getScaleFactor() {
        return this.scaleFactor;
    }

    public ScaledResolution(Minecraft mc) {
        this(mc, mc.displayWidth, mc.displayHeight);
    }
}
