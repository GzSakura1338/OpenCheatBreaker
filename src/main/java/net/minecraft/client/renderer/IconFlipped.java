package net.minecraft.client.renderer;

import net.minecraft.util.IIcon;

public class IconFlipped implements IIcon {
    public IIcon baseIcon;
    public boolean flipU;
    public boolean flipV;

    public IconFlipped(IIcon icon, boolean flag, boolean flag2) {
        this.baseIcon = icon;
        this.flipU = flag;
        this.flipV = flag2;
    }

    @Override
    public int getIconWidth() {
        return this.baseIcon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return this.baseIcon.getIconHeight();
    }

    @Override
    public float getMinU() {
        return this.flipU ? this.baseIcon.getMaxU() : this.baseIcon.getMinU();
    }

    @Override
    public float getMaxU() {
        return this.flipU ? this.baseIcon.getMinU() : this.baseIcon.getMaxU();
    }

    @Override
    public float getInterpolatedU(double d) {
        float f = this.getMaxU() - this.getMinU();
        return this.getMinU() + f * ((float)d / 16.0F);
    }

    @Override
    public float getMinV() {
        return this.baseIcon.getMinV();
    }

    @Override
    public float getMaxV() {
        return this.flipV ? this.baseIcon.getMinV() : this.baseIcon.getMaxV();
    }

    @Override
    public float getInterpolatedV(double d) {
        float f = this.getMaxV() - this.getMinV();
        return this.getMinV() + f * ((float)d / 16.0F);
    }

    @Override
    public String getIconName() {
        return this.baseIcon.getIconName();
    }
}
