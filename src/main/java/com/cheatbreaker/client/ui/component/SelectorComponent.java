package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;

public class SelectorComponent extends SettingControl {
    public String valueText;
    public ResourceLocation leftPng = new ResourceLocation("client/icons/left.png");
    public ResourceLocation rightPng = new ResourceLocation("client/icons/right.png");
    public int intValue = 0;
    public float floatValue = 0.0F;

    @Override
    public void mouseClicked(int n, int n2, int n3) {
        super.mouseClicked(n, n2, n3);
        if (!this.isHidden(this.setting)) {
            boolean flag = n > (this.x + this.width - 48) * this.scale
                && n < (this.x + this.width - 10) * this.scale
                && n2 > (this.y + this.scrollOffsetY) * this.scale
                && n2 < (this.y + 10 + this.scrollOffsetY) * this.scale;
            boolean flag2 = n > (this.x + this.width - 92) * this.scale
                && n < (this.x + this.width - 48) * this.scale
                && n2 > (this.y + this.scrollOffsetY) * this.scale
                && n2 < (this.y + 10 + this.scrollOffsetY) * this.scale;
            if ((flag2 || flag) && this.intValue == 0) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));

                for (int i = 0; i < this.setting.getStringValues().length; i++) {
                    if (this.setting.getStringValues()[i].toLowerCase().equalsIgnoreCase((String)this.setting.getValue())) {
                        this.valueText = (String)this.setting.getValue();
                        if (flag) {
                            if (i + 1 >= this.setting.getStringValues().length) {
                                this.intValue = 2;
                                this.setting.changeValue(this.setting.getStringValues()[0]);
                            } else {
                                this.intValue = 2;
                                this.setting.changeValue(this.setting.getStringValues()[i + 1]);
                            }
                            break;
                        }

                        if (flag2) {
                            if (i - 1 < 0) {
                                this.intValue = 1;
                                this.setting.changeValue(this.setting.getStringValues()[this.setting.getStringValues().length - 1]);
                            } else {
                                this.intValue = 1;
                                this.setting.changeValue(this.setting.getStringValues()[i - 1]);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public SelectorComponent(Setting setting, float f) {
        super(f);
        this.setting = setting;
        this.height = 12;
    }

    @Override
    public void render(int n, int n2, float f) {
        super.render(n, n2, f);
        boolean flag = n2 > (this.y + this.scrollOffsetY) * this.scale && n2 < (this.y + 10 + this.scrollOffsetY) * this.scale;
        boolean flag2 = n > (this.x + this.width - 48) * this.scale && n < (this.x + this.width - 10) * this.scale && flag;
        boolean flag3 = n > (this.x + this.width - 92) * this.scale && n < (this.x + this.width - 48) * this.scale && flag;
        CheatBreaker.getInstance()
            .getFontManager()
            .ubuntuM16
            .drawString(
                this.setting.getName().toUpperCase(), this.x + 10, this.y + 2, !flag3 && !flag2 ? this.theme.getTextColor2Value() : this.theme.getTextColor3Value()
            );
        boolean endsWith = this.setting.getName().toLowerCase().endsWith("color");
        if (!endsWith) {
            if (this.intValue == 0) {
                CheatBreaker.getInstance()
                    .getFontManager()
                    .ubuntuM16
                    .drawCenteredString((String)this.setting.getValue(), this.x + this.width - 48, this.y + 2, this.theme.getTextColor2Value());
            } else {
                boolean flag4 = this.intValue == 1;
                float f2 = this.x + this.width - 48 - (flag4 ? -this.floatValue : this.floatValue);
                CheatBreaker.getInstance().getFontManager().ubuntuM16.drawCenteredString(this.valueText, f2, this.y + 2, this.theme.getTextColor2Value());
                if (flag4) {
                    CheatBreaker.getInstance()
                        .getFontManager()
                        .ubuntuM16
                        .drawCenteredString((String)this.setting.getValue(), this.x + this.width - 98 + this.floatValue, this.y + 2, this.theme.getTextColor2Value());
                } else {
                    CheatBreaker.getInstance()
                        .getFontManager()
                        .ubuntuM16
                        .drawCenteredString((String)this.setting.getValue(), this.x + this.width + 2 - this.floatValue, this.y + 2, this.theme.getTextColor2Value());
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
        } else if (this.intValue == 0) {
            float stringWidth = CheatBreaker.getInstance().getFontManager().ubuntuM16.getStringWidth((String)this.setting.getValue());
            CheatBreaker.getInstance()
                .getFontManager()
                .ubuntuM16
                .drawString((String)this.setting.getValue(), this.x + this.width - 47.5F - stringWidth / 2.0F, this.y + 2.5F, -16777216);
            CheatBreaker.getInstance()
                .getFontManager()
                .ubuntuM16
                .drawString("§" + this.setting.getValue() + this.setting.getValue(), this.x + this.width - 48 - stringWidth / 2.0F, this.y + 2, -16777216);
        } else {
            boolean flag4 = this.intValue == 1;
            float f2 = this.x + this.width - 48 - (flag4 ? -this.floatValue : this.floatValue);
            CheatBreaker.getInstance().getFontManager().ubuntuM16.drawCenteredString(this.valueText, f2, this.y + 2, this.theme.getTextColor2Value());
            float stringWidth = CheatBreaker.getInstance().getFontManager().ubuntuM16.getStringWidth((String)this.setting.getValue());
            if (flag4) {
                CheatBreaker.getInstance()
                    .getFontManager()
                    .ubuntuM16
                    .drawString((String)this.setting.getValue(), this.x + this.width - 97.5F - stringWidth / 2.0F + this.floatValue, this.y + 2.5F, -16777216);
                CheatBreaker.getInstance()
                    .getFontManager()
                    .ubuntuM16
                    .drawString(
                        "§" + this.setting.getValue() + this.setting.getValue(), this.x + this.width - 98 - stringWidth / 2.0F + this.floatValue, this.y + 2, -16777216
                    );
            } else {
                CheatBreaker.getInstance()
                    .getFontManager()
                    .ubuntuM16
                    .drawString((String)this.setting.getValue(), this.x + this.width - 1.5F - stringWidth / 2.0F - this.floatValue, this.y + 2.5F, -16777216);
                CheatBreaker.getInstance()
                    .getFontManager()
                    .ubuntuM16
                    .drawString(
                        "§" + this.setting.getValue() + this.setting.getValue(), this.x + this.width - 2 - stringWidth / 2.0F - this.floatValue, this.y + 2, -16777216
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

        float red = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
        float green = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
        float blue = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
        GlStateManager.color(red, green, blue, flag3 ? 0.8F : 0.45F);
        RenderUtils.drawSquareTextureViaRenderEngine(this.leftPng, 4.0F, this.x + this.width - 82, this.y + 3);
        GlStateManager.color(red, green, blue, flag2 ? 0.8F : 0.45F);
        RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 4.0F, this.x + this.width - 22, this.y + 3);
        this.renderSettingLabel(this.setting, n, n2);
    }
}
