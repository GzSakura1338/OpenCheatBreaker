package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingType;
import com.cheatbreaker.client.render.RenderUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class SliderComponent extends SettingControl {
    public boolean dragging = false;
    public float floatValue;
    public boolean flag = false;
    public boolean flag2 = false;
    public int decimals = 1;
    public float floatValue2;
    public ResourceLocation clientIcons;
    public ResourceLocation clientIcons2;

    public double round(double d, int n) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(d));
        return bigDecimal.setScale(n, RoundingMode.HALF_UP).doubleValue();
    }

    public void adjustPrecision(double d) {
        while (d >= 10.0) {
            d /= 10.0;
            this.decimals--;
        }
    }

    public SliderComponent(Setting setting, float f) {
        super(f);
        this.setting = setting;
        this.height = 22;
        this.floatValue2 = Float.parseFloat("" + setting.getValue());
        this.clientIcons = new ResourceLocation("client/icons/" + this.setting.getLeftIconName() + ".png");
        this.clientIcons2 = new ResourceLocation("client/icons/" + this.setting.getRightIconName() + ".png");
        this.adjustPrecision(Double.parseDouble(this.setting.getMaximumValue() + "") - Double.parseDouble(this.setting.getMinimumValue() + ""));
    }

    @Override
    public void render(int n, int n2, float f) {
        super.render(n, n2, f);
        short s = 148;
        if (this.dragging && !Mouse.isButtonDown(0)) {
            this.dragging = false;
        }

        float f2 = 8.0F;
        float f3 = 16.0F;
        float f4 = 17.25F;
        byte b = 20;
        if (!this.setting.getCenterLabel().isEmpty()) {
            CheatBreaker.getInstance()
                .getFontManager()
                .ubuntuM16
                .drawCenteredString(this.setting.getCenterLabel(), this.x + 172 + s / 2, this.y - 2, this.theme.getTextColor2Value());
            Gui.drawRect(this.x + 172 + s / 2 - 0.5F, this.y + 8, this.x + 172.0F + s / 2.0F + 0.5F, this.y + 14, this.theme.getPrimaryTextColorValue());
            this.flag2 = true;
        }

        float red = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
        float green = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
        float blue = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
        if (!this.setting.getLeftIconName().isEmpty() && !this.setting.getRightIconName().isEmpty()) {
            GlStateManager.color(red, green, blue, 0.43F);
            RenderUtils.drawTexture(
                this.clientIcons,
                this.x + 180.0F - this.setting.getLeftIconScale() / 2.0F + this.setting.getLeftIconXOffset(),
                this.y + this.setting.getLeftIconScale() / 2.0F + this.setting.getLeftIconYOffset(),
                this.setting.getLeftIconScale(),
                this.setting.getLeftIconScale()
            );
            Gui.drawRect(this.x + 180.0F - 0.5F, this.y + 12, this.x + 180.0F + 0.5F, this.y + 14, this.theme.getPrimaryTextColorValue());
            GlStateManager.color(red, green, blue, 0.43F);
            RenderUtils.drawTexture(
                this.clientIcons2,
                this.x + 170 + s - this.setting.getRightIconScale() - this.setting.getRightIconScale() / 2.0F + this.setting.getRightIconXOffset(),
                this.y + this.setting.getRightIconScale() / 2.0F + this.setting.getRightIconYOOffset(),
                this.setting.getRightIconScale(),
                this.setting.getRightIconScale()
            );
            Gui.drawRect(
                this.x + 170.0F + s - 10.0F - 0.5F, this.y + 12, this.x + 170.0F + s - 10.0F + 0.5F, this.y + 14, this.theme.getPrimaryTextColorValue()
            );
            this.flag = true;
        }

        if (!this.flag && !this.flag2) {
            this.height = 12;
            f2 = 2.0F;
            f3 = 6.0F;
            f4 = 7.25F;
            b = 10;
        }

        CheatBreaker.getInstance()
            .getFontManager()
            .ubuntuM16
            .drawString(this.setting.getName().toUpperCase(), this.x + 10, this.y + f2, this.theme.getTextColor2Value());
        if (this.setting.showValue()) {
            String str = this.setting.getValue().toString() + this.setting.getUnit();
            CheatBreaker.getInstance()
                .getFontManager()
                .ubuntuM16
                .drawString(
                    str,
                    (float)(this.x + 169) - CheatBreaker.getInstance().getFontManager().ubuntuM16.getStringWidth(str),
                    this.y + f2,
                    this.theme.getTextColor2Value()
                );
        }

        boolean flag = n > (this.x + 170.0F) * this.scale
            && n < (this.x + 170.0F + s - 2.0F) * this.scale
            && n2 > (this.y + 4.0F + this.scrollOffsetY) * this.scale
            && n2 < (this.y + b + this.scrollOffsetY) * this.scale;
        RenderUtils.drawRoundedRectFilledPrecise(
            this.x + 174,
            this.y + f3,
            this.x + 170 + s - 4,
            this.y + f3 + 2.0F,
            1.0,
            flag ? this.theme.getTextColor2Value() : this.theme.getPrimaryTextColorValue()
        );
        double d = s - 18;
        if (this.setting.getMinimumValue() != null && this.setting.getMaximumValue() != null) {
            float parseFloat = Float.parseFloat("" + this.setting.getMinimumValue());
            float parseFloat2 = Float.parseFloat("" + this.setting.getMaximumValue());
            if (this.dragging) {
                if (!Keyboard.isKeyDown(157)) {
                    this.floatValue = (float)Math.round((parseFloat + (n - (this.x + 180.0F) * this.scale) * ((parseFloat2 - parseFloat) / (d * this.scale))) * 100.0)
                        / 100.0F;
                } else {
                    this.floatValue = (float)((parseFloat + (n - (this.x + 180) * this.scale) * ((parseFloat2 - parseFloat) / (d * this.scale))) * 100.0) / 100.0F;
                }

                if (Keyboard.isKeyDown(42)) {
                    this.floatValue = (float)this.round(this.floatValue, this.decimals);
                } else if (this.decimals <= 0 && !Keyboard.isKeyDown(29) || this.setting.getType().equals(SettingType.INTEGER)) {
                    this.floatValue = Math.round(this.floatValue);
                }

                if (this.floatValue < parseFloat) {
                    this.floatValue = parseFloat;
                } else if (this.floatValue > parseFloat2) {
                    this.floatValue = parseFloat2;
                }

                switch (this.setting.getType()) {
                    case INTEGER:
                        this.setting.changeValue(Integer.parseInt((int)this.floatValue + ""));
                        break;
                    case FLOAT:
                        this.setting.changeValue(this.floatValue);
                        break;
                    case DOUBLE:
                        this.setting.changeValue(Double.parseDouble(this.floatValue + ""));
                }
            }

            float f5;
            f5 = (f5 = Float.parseFloat(this.setting.getValue() + "")) < this.floatValue2 ? this.floatValue2 - f5 : f5 - this.floatValue2;
            float f6 = ((parseFloat2 - parseFloat) / 20.0F + f5 * 8.0F) / (Minecraft.debugFPS + 1);
            if (f6 < 1.0E-4) {
                f6 = 1.0E-4F;
            }

            float f7;
            if (this.floatValue2 < (f7 = Float.parseFloat(this.setting.getValue() + ""))) {
                this.floatValue2 = Math.min(this.floatValue2 + f6, f7);
            } else if (this.floatValue2 > f7) {
                this.floatValue2 = Math.max(this.floatValue2 - f6, f7);
            }

            double d2 = 100.0F * ((this.floatValue2 - parseFloat) / (parseFloat2 - parseFloat));
            RenderUtils.drawRoundedRectFilledPrecise(
                this.x + 174, this.y + f3, this.x + 180.0 + d * d2 / 100.0, this.y + f3 + 2.0F, 0.0, this.theme.getTrackColorValue()
            );
            float red2 = RenderUtils.toColor(this.theme.getThumbColorValue()).getRed();
            float green2 = RenderUtils.toColor(this.theme.getThumbColorValue()).getGreen();
            float blue2 = RenderUtils.toColor(this.theme.getThumbColorValue()).getBlue();
            float alpha = RenderUtils.toColor(this.theme.getThumbColorValue()).getAlpha();
            float f8 = (this.theme.getThumbOutlineColorValue() >> 16 & 0xFF) / 255.0F;
            float f9 = (this.theme.getThumbOutlineColorValue() >> 8 & 0xFF) / 255.0F;
            float f10 = (this.theme.getThumbOutlineColorValue() & 0xFF) / 255.0F;
            float f11 = (this.theme.getThumbOutlineColorValue() >> 24 & 0xFF) / 255.0F;
            GlStateManager.color(f8, f9, f10, f11);
            RenderUtils.drawFilledCircle(this.x + 181.25 + d * d2 / 100.0, this.y + f4, 4.5);
            GlStateManager.color(red2, green2, blue2, alpha);
            RenderUtils.drawFilledCircle(this.x + 181.25 + d * d2 / 100.0, this.y + f4, 2.7);
            this.renderSettingLabel(this.setting, n, n2);
        }
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) {
        super.mouseClicked(n, n2, n3);
        if (!this.isHidden(this.setting)) {
            byte b = 20;
            if (this.height == 12) {
                b = 10;
            }

            boolean flag = n > (this.x + 170) * this.scale
                && n < (this.x + 170 + 148 - 2) * this.scale
                && n2 > (this.y + 4 + this.scrollOffsetY) * this.scale
                && n2 < (this.y + b + this.scrollOffsetY) * this.scale;
            if (n3 == 0 && flag) {
                this.dragging = true;
            }
        }
    }
}
