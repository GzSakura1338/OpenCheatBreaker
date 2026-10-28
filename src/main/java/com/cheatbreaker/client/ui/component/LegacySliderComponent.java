package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingType;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.optifine.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class LegacySliderComponent extends SettingComponent {
    public float value = -1.0F;
    public float floatValue;
    public boolean dragging = false;
    public Setting setting2;

    @Override
    public void mouseClicked(int n, int n2, int n3) {
        boolean flag = n > (this.x + 170) * this.scale
            && n < (this.x + 170 + 170 - 2) * this.scale
            && n2 > (this.y + 4 + this.scrollOffsetY) * this.scale
            && n2 < (this.y + 10 + this.scrollOffsetY) * this.scale;
        if (n3 == 0 && flag) {
            this.dragging = true;
        }
    }

    public LegacySliderComponent(Setting setting, float f) {
        super(f);
        this.setting2 = setting;
        this.height = 14;
        this.value = Float.parseFloat("" + setting.getValue());
    }

    @Override
    public void render(int n, int n2, float f) {
        short s = 148;
        CheatBreaker.getInstance().getFontManager().ubuntuM16.drawString(this.setting2.getName().toUpperCase(), this.x + 10, this.y + 2, -1895825408);
        if (this.dragging && !Mouse.isButtonDown(0)) {
            this.dragging = false;
        }

        String str = this.setting2.getValue().toString();
        CheatBreaker.getInstance()
            .getFontManager()
            .ubuntuM16
            .drawString(str, (float)(this.x + 169) - CheatBreaker.getInstance().getFontManager().ubuntuM16.getStringWidth(str), this.y + 2, -1895825408);
        boolean flag = n > (this.x + 172) * this.scale
            && n < (this.x + 172 + s - 2) * this.scale
            && n2 > (this.y + 4 + this.scrollOffsetY) * this.scale
            && n2 < (this.y + 10 + this.scrollOffsetY) * this.scale;
        RenderUtils.drawRoundedRectFilledPrecise(this.x + 174, this.y + 6, this.x + 170 + s - 4, this.y + 8, 1.0, flag ? -1895825408 : 1862270976);
        double d = s - 18;
        float parseFloat = Float.parseFloat("" + this.setting2.getMinimumValue());
        float parseFloat2 = Float.parseFloat("" + this.setting2.getMaximumValue());
        if (this.dragging) {
            this.floatValue = (float)Math.round((parseFloat + (n - (this.x + 180) * this.scale) * ((parseFloat2 - parseFloat) / (d * this.scale))) * 100.0) / 100.0F;
            if (this.setting2.getType().equals(SettingType.INTEGER) || Keyboard.isKeyDown(42)) {
                this.floatValue = Math.round(this.floatValue);
            }

            if (this.floatValue < parseFloat) {
                this.floatValue = parseFloat;
            } else if (this.floatValue > parseFloat2) {
                this.floatValue = parseFloat2;
            }

            switch (this.setting2.getType()) {
                case INTEGER:
                    this.setting2.changeValue(Integer.parseInt((int)this.floatValue + ""));
                    break;
                case FLOAT:
                    this.setting2.changeValue(this.floatValue);
                    break;
                case DOUBLE:
                    this.setting2.changeValue(Double.parseDouble(this.floatValue + ""));
            }
        }

        float f2;
        float f3;
        f2 = (f2 = Float.parseFloat(this.setting2.getValue() + "")) < this.value ? this.value - f2 : (f3 = f2 - this.value);
        float f4 = ((parseFloat2 - parseFloat) / 20.0F + f2 * 8.0F) / (Minecraft.debugFPS + 1);
        if (f4 < 1.0E-4) {
            f4 = 1.0E-4F;
        }

        float f5;
        if (this.value < (f5 = Float.parseFloat(this.setting2.getValue() + ""))) {
            this.value = Math.min(this.value + f4, f5);
        } else if (this.value > f5) {
            this.value = Math.max(this.value - f4, f5);
        }

        double d2 = 100.0F * ((this.value - parseFloat) / (parseFloat2 - parseFloat));
        RenderUtils.drawRoundedRectFilledPrecise(this.x + 174, this.y + 6, this.x + 180 + d * d2 / 100.0, this.y + 8, 4.0, -12418828);
        GlStateManager.color(0.25F, 0.45F, 1.0F, 1.0F);
        RenderUtils.drawFilledCircle(this.x + 181.25F + d * d2 / 100.0, this.y + 7.25F, 4.5);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtils.drawFilledCircle(this.x + 181.25F + d * d2 / 100.0, this.y + 7.25F, 2.7F);
        this.renderSettingLabel(this.setting2, n, n2);
    }
}
