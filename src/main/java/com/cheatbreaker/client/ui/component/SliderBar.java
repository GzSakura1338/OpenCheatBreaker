package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.animation.Animation;
import com.cheatbreaker.client.animation.EaseInOutAnimation;
import com.cheatbreaker.client.mod.setting.Setting;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

public class SliderBar extends UIElement {
    public Setting setting;
    public Animation animation;
    public Number number;

    @Override
    public boolean mouseClicked(float f, float f2, int n, boolean flag) throws java.io.IOException {
        if (!flag) {
            return false;
        }

        if (Mouse.isButtonDown(0) && this.containsPoint(f, f2)) {
            this.animation.start();
            this.number = (Number)this.setting.getValue();
            float f3 = ((Number)this.setting.getMinimumValue()).floatValue();
            float f4 = ((Number)this.setting.getMaximumValue()).floatValue();
            if (f - this.x > this.width / 2.0F) {
                f += 2.0F;
            }

            float f5 = f3 + (f - this.x) * ((f4 - f3) / this.width);
            switch (this.setting.getType()) {
                case INTEGER:
                    this.setting.changeValue(this.findObject(Integer.parseInt((int)f5 + "")));
                    break;
                case FLOAT:
                    this.setting.changeValue(this.findObject(f5));
                    break;
                case DOUBLE:
                    this.setting.changeValue(this.findObject(Double.parseDouble((double)f5 + "")));
            }
        }

        return super.mouseClicked(f, f2, n, flag);
    }

    public SliderBar(Setting setting) {
        this.setting = setting;
        this.animation = new EaseInOutAnimation(300L);
        this.number = (Number)setting.getValue();
    }

    public Object findObject(Object obj) {
        try {
            return obj;
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public void render(float f, float f2, boolean flag) {
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -13158601);
        if (!this.animation.hasStarted()) {
            this.number = (Number)this.setting.getValue();
        }

        float f3 = ((Number)this.setting.getValue()).floatValue();
        float f4 = ((Number)this.setting.getMinimumValue()).floatValue();
        float f5 = ((Number)this.setting.getMaximumValue()).floatValue();
        float f6 = f3 - this.number.floatValue();
        float f7 = 100.0F * ((this.number.floatValue() + f6 * this.animation.getProgress() - f4) / (f5 - f4));
        Gui.drawRect(this.x, this.y, this.x + this.width / 100.0F * f7, this.y + this.height, -52429);
    }
}
