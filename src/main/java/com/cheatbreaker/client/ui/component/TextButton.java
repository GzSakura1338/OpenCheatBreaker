package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.gui.Gui;

public class TextButton extends UIElement {
    public boolean colorsEnabled = true;
    public boolean flag = false;
    public String label;
    public ColorAnimation colorAnimation;
    public int[] intArray;
    public ColorAnimation colorAnimation2;
    public ColorAnimation colorAnimation3;

    public TextButton(String str) {
        this.label = str;
        this.colorAnimation = new ColorAnimation(-14277082, -11493284);
        this.colorAnimation2 = new ColorAnimation(-13487566, -10176146);
        this.colorAnimation3 = new ColorAnimation(-14013910, -11164318);
    }

    @Override
    public void setEnabled(boolean flag) {
        if (this.colorsEnabled != flag) {
            if (flag) {
                this.useDefaultColors();
            } else {
                this.useUniformColors();
            }
        }

        this.colorsEnabled = flag;
    }

    public void useDefaultColors() {
        this.setIntArray(new int[]{-14277082, -11493284, -1356454362, -13487566, -10176146, -1355664846, -14013910, -11164318, -1356191190});
    }

    public void useLightColors() {
        this.setIntArray(new int[]{-11119018, -11493284, -1353296298, -10329502, -10176146, -1352506782, -11579569, -11164318, -1353756849});
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public void render(float f, float f2, boolean flag) {
        boolean flag2 = this.flag || flag && this.containsPoint(f, f2);
        if (this.intArray != null && !this.colorsEnabled) {
            this.colorAnimation.setInactiveColor(this.intArray[2]);
            this.colorAnimation.setActiveColor(this.intArray[2]);
            this.colorAnimation2.setInactiveColor(this.intArray[5]);
            this.colorAnimation2.setActiveColor(this.intArray[5]);
            this.colorAnimation3.setInactiveColor(this.intArray[8]);
            this.colorAnimation3.setActiveColor(this.intArray[8]);
            this.intArray = null;
        } else if (this.intArray != null && this.colorAnimation.isFinished()) {
            this.colorAnimation.setInactiveColor(this.intArray[0]);
            this.colorAnimation.setActiveColor(this.intArray[1]);
            this.colorAnimation2.setInactiveColor(this.intArray[3]);
            this.colorAnimation2.setActiveColor(this.intArray[4]);
            this.colorAnimation3.setInactiveColor(this.intArray[6]);
            this.colorAnimation3.setActiveColor(this.intArray[7]);
            this.intArray = null;
        }

        Gui.floatValue = 0.0F;
        RenderUtils.drawBorderedGradientRect(
            this.x,
            this.y,
            this.x + this.width,
            this.y + this.height,
            this.colorAnimation.getColor(flag2).getRGB(),
            this.colorAnimation2.getColor(flag2).getRGB(),
            this.colorAnimation3.getColor(flag2).getRGB()
        );
        float f3 = this.x + this.width / 2.0F;
        float f4 = this.y + 2.0F;
        CheatBreaker.getInstance().getFontManager().robotoRegular13.drawCenteredString(this.label, f3, f4, -1);
    }

    @Override
    public boolean mouseClicked(float f, float f2, int n, boolean flag) {
        return !flag ? false : false;
    }

    @Override
    public boolean containsPoint(float f, float f2) {
        return this.colorsEnabled && super.containsPoint(f, f2);
    }

    public void setLabel(String str) {
        this.label = str;
    }

    public void useUniformColors() {
        this.setIntArray(new int[]{-1356454362, -1356454362, -1356454362, -1355664846, -1355664846, -1355664846, -1356191190, -1356191190, -1356191190});
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public boolean isEnabled() {
        return this.colorsEnabled;
    }

    public void setIntArray(int[] ints) {
        this.intArray = ints;
    }

    public void setIntArray() {
        this.setIntArray(new int[]{-11493284, -11493284, -1353670564, -10176146, -10176146, -1352353426, -11164318, -11164318, -1353341598});
    }

    public void render2(float f, float f2, boolean flag) {
        this.render(f, f2, flag);
    }
}
