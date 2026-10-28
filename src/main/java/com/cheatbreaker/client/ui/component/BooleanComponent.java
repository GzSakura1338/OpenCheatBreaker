package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class BooleanComponent extends SettingControl {
    public String valueText;
    public int intValue = 0;
    public float floatValue = 0.0F;
    public IconComponent leftPng;
    public IconComponent rightPng;

    @Override
    public void render(int n, int n2, float f) {
        super.render(n, n2, f);
        boolean isMouseOver = this.leftPng.isMouseOver(n, n2);
        boolean isMouseOver2 = this.rightPng.isMouseOver(n, n2);
        CheatBreaker.getInstance()
            .getFontManager()
            .ubuntuM16
            .drawString(
                this.setting.getName().toUpperCase(),
                this.x + 10,
                this.y + 2.0F,
                !isMouseOver2 && !isMouseOver ? this.theme.getTextColor2Value() : this.theme.getTextColor3Value()
            );
        if (this.intValue == 0) {
            CheatBreaker.getInstance()
                .getFontManager()
                .ubuntuM16
                .drawCenteredString(
                    this.setting.getBooleanValue() ? this.setting.getOnText() : this.setting.getOffText(),
                    this.x + this.width - 48,
                    this.y + 2,
                    this.theme.getTextColor2Value()
                );
        } else {
            boolean flag = this.intValue == 1;
            float f2 = this.x + this.width - 48.0F - (flag ? -this.floatValue : this.floatValue);
            CheatBreaker.getInstance().getFontManager().ubuntuM16.drawCenteredString(this.valueText, f2, this.y + 2, this.theme.getTextColor2Value());
            if (flag) {
                CheatBreaker.getInstance()
                    .getFontManager()
                    .ubuntuM16
                    .drawCenteredString(
                        this.setting.getBooleanValue() ? this.setting.getOnText() : this.setting.getOffText(),
                        this.x + this.width - 98 + this.floatValue,
                        this.y + 2,
                        this.theme.getTextColor2Value()
                    );
            } else {
                CheatBreaker.getInstance()
                    .getFontManager()
                    .ubuntuM16
                    .drawCenteredString(
                        this.setting.getBooleanValue() ? this.setting.getOnText() : this.setting.getOffText(),
                        this.x + this.width + 2 - this.floatValue,
                        this.y + 2,
                        this.theme.getTextColor2Value()
                    );
            }

            if (this.floatValue >= 50.0F) {
                this.intValue = 0;
                this.floatValue = 0.0F;
            } else {
                float animationStep = ModsScreen.animationStep(50.0F + this.floatValue * 15.0F);
                this.floatValue = Math.min(this.floatValue + animationStep, 50.0F);
            }

            Gui.drawRect(this.x + this.width - 130, this.y + 2, this.x + this.width - 72, this.y + 12, this.theme.getMenuBackgroundColorValue());
            Gui.drawRect(this.x + this.width - 22, this.y + 2, this.x + this.width + 4, this.y + 12, this.theme.getMenuBackgroundColorValue());
        }

        this.leftPng.setBounds(this.x + this.width - 84, this.y + 1, 20, 10);
        this.leftPng.scrollOffsetY = this.scrollOffsetY;
        this.leftPng.render(n, n2, f);
        this.rightPng.setBounds(this.x + this.width - 24, this.y + 1, 20, 10);
        this.rightPng.scrollOffsetY = this.scrollOffsetY;
        this.rightPng.render(n, n2, f);
    }

    public BooleanComponent(Setting setting, float f) {
        super(f);
        this.setting = setting;
        this.height = 12;
        this.leftPng = new IconComponent(null, "left.png", this.x - 10, this.y - this.height / 2, 20, 10, f);
        this.leftPng.setSmall(true);
        this.rightPng = new IconComponent(null, "right.png", this.x - 10, this.y - this.height / 2, 20, 10, f);
        this.rightPng.setSmall(true);
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) {
        super.mouseClicked(n, n2, n3);
        if (!this.isHidden(this.setting)) {
            boolean isMouseOver = this.leftPng.isMouseOver(n, n2);
            boolean isMouseOver2 = this.rightPng.isMouseOver(n, n2);
            if ((isMouseOver || isMouseOver2) && this.intValue == 0) {
                this.intValue = isMouseOver ? 1 : 2;
                this.floatValue = 0.0F;
                this.valueText = this.setting.getBooleanValue() ? this.setting.getOnText() : this.setting.getOffText();
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                this.setting.changeValue(!(Boolean)this.setting.getValue());
            }
        }
    }
}
