package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.GlStateCache;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class KeyStrokeKey {
    public Color cpsTextFrom;
    public String label;
    public Color borderFrom;
    public int keyCode;
    public float width;
    public Color borderTo;
    public Color cpsTextTo;
    public Color backgroundFrom;
    public long lastChangeTime;
    public Color textTo;
    public Color textFrom;
    public Color backgroundTo;
    public boolean wasPressed;
    public float height;

    public float getHeight() {
        return this.height;
    }

    public String getLabel() {
        return this.label;
    }

    public float getWidth() {
        return this.width;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public KeyStrokeKey(String str, int n, float f, float f2) {
        this.label = str;
        this.keyCode = n;
        this.width = f;
        this.height = f2;
    }

    public void render(float f, float f2, int n, int n2, int n3, int n4, int n5, int n6, boolean flag) {
        Minecraft minecraft = Minecraft.getMinecraft();
        KeyStrokesMod keyStrokesMod = CheatBreaker.getInstance().getModManager().keyStrokesMod;
        int n7 = this.keyCode < 0 ? this.keyCode + 100 : -1;
        boolean flag2 = (minecraft.currentScreen == null || minecraft.currentScreen instanceof GuiContainer || minecraft.currentScreen instanceof ModsScreen)
            && (n7 != -1 ? Mouse.isButtonDown(n7) : Keyboard.isKeyDown(this.keyCode));
        if (flag2 && !this.wasPressed) {
            this.wasPressed = true;
            this.lastChangeTime = System.currentTimeMillis();
            this.backgroundFrom = new Color(n3, true);
            this.backgroundTo = new Color(n4, true);
            this.textFrom = new Color(n, true);
            this.textTo = new Color(n2, true);
            this.cpsTextFrom = new Color(keyStrokesMod.cpsColor.getColorValue(), true);
            this.cpsTextTo = new Color(keyStrokesMod.cpsColorPressed.getColorValue(), true);
            this.borderFrom = new Color(n5, true);
            this.borderTo = new Color(n6, true);
        } else if (this.wasPressed && !flag2) {
            this.wasPressed = false;
            this.lastChangeTime = System.currentTimeMillis();
            this.backgroundFrom = new Color(n4, true);
            this.backgroundTo = new Color(n3, true);
            this.textFrom = new Color(n2, true);
            this.textTo = new Color(n, true);
            this.cpsTextFrom = new Color(keyStrokesMod.cpsColorPressed.getColorValue(), true);
            this.cpsTextTo = new Color(keyStrokesMod.cpsColor.getColorValue(), true);
            this.borderFrom = new Color(n6, true);
            this.borderTo = new Color(n5, true);
        }

        float value = (Float)keyStrokesMod.backgroundFadeTime.getValue();
        int n8;
        if ((float)(System.currentTimeMillis() - this.lastChangeTime) < value) {
            float f3 = (float)(System.currentTimeMillis() - this.lastChangeTime) / value;
            n8 = new Color(
                    (int)Math.abs(f3 * this.backgroundTo.getRed() + (1.0F - f3) * this.backgroundFrom.getRed()),
                    (int)Math.abs(f3 * this.backgroundTo.getGreen() + (1.0F - f3) * this.backgroundFrom.getGreen()),
                    (int)Math.abs(f3 * this.backgroundTo.getBlue() + (1.0F - f3) * this.backgroundFrom.getBlue()),
                    (int)Math.abs(f3 * this.backgroundTo.getAlpha() + (1.0F - f3) * this.backgroundFrom.getAlpha())
                )
                .getRGB();
        } else {
            n8 = flag2 ? n4 : n3;
        }

        float value2 = (Float)keyStrokesMod.textFadeTime.getValue();
        int n9;
        int n10;
        int n11;
        if ((float)(System.currentTimeMillis() - this.lastChangeTime) < value2) {
            float f3 = (float)(System.currentTimeMillis() - this.lastChangeTime);
            float f4 = f3 / value2;
            int abs = (int)Math.abs(f4 * this.textTo.getRed() + (1.0F - f4) * this.textFrom.getRed());
            int abs2 = (int)Math.abs(f4 * this.textTo.getGreen() + (1.0F - f4) * this.textFrom.getGreen());
            int abs3 = (int)Math.abs(f4 * this.textTo.getBlue() + (1.0F - f4) * this.textFrom.getBlue());
            int abs4 = (int)Math.abs(f4 * this.textTo.getAlpha() + (1.0F - f4) * this.textFrom.getAlpha());
            int abs5 = (int)Math.abs(f4 * this.cpsTextTo.getRed() + (1.0F - f4) * this.cpsTextFrom.getRed());
            int abs6 = (int)Math.abs(f4 * this.cpsTextTo.getGreen() + (1.0F - f4) * this.cpsTextFrom.getGreen());
            int abs7 = (int)Math.abs(f4 * this.cpsTextTo.getBlue() + (1.0F - f4) * this.cpsTextFrom.getBlue());
            int abs8 = (int)Math.abs(f4 * this.cpsTextTo.getAlpha() + (1.0F - f4) * this.cpsTextFrom.getAlpha());
            n9 = new Color(abs, abs2, abs3, abs4).getRGB();
            n10 = new Color(abs5, abs6, abs7, abs8).getRGB();
            n11 = new Color(abs / 4, abs2 / 4, abs3 / 4, abs4).getRGB();
        } else {
            n9 = flag2 ? n2 : n;
            n10 = flag2 ? keyStrokesMod.cpsColorPressed.getColorValue() : keyStrokesMod.cpsColor.getColorValue();
            int n12 = n2;
            int n13 = n;
            int n14 = n12 >> 16 & 0xFF;
            int n15 = n12 >> 8 & 0xFF;
            int n16 = n12 & 0xFF;
            int n17 = n13 >> 16 & 0xFF;
            int n18 = n13 >> 8 & 0xFF;
            int n19 = n13 & 0xFF;
            n11 = flag2 ? new Color(n14 / 4, n15 / 4, n16 / 4).getRGB() : new Color(n17 / 4, n18 / 4, n19 / 4).getRGB();
        }

        float value3 = (Float)keyStrokesMod.borderFadeTime.getValue();
        int n12;
        if ((float)(System.currentTimeMillis() - this.lastChangeTime) < value3) {
            float f3 = (float)(System.currentTimeMillis() - this.lastChangeTime) / value3;
            n12 = new Color(
                    (int)Math.abs(f3 * this.borderTo.getRed() + (1.0F - f3) * this.borderFrom.getRed()),
                    (int)Math.abs(f3 * this.borderTo.getGreen() + (1.0F - f3) * this.borderFrom.getGreen()),
                    (int)Math.abs(f3 * this.borderTo.getBlue() + (1.0F - f3) * this.borderFrom.getBlue()),
                    (int)Math.abs(f3 * this.borderTo.getAlpha() + (1.0F - f3) * this.borderFrom.getAlpha())
                )
                .getRGB();
        } else {
            n12 = flag2 ? n6 : n5;
        }

        if ((Boolean)keyStrokesMod.showBackground.getValue()) {
            RenderUtils.drawRoundedRect(f, f2, f + this.width, f2 + this.height, keyStrokesMod.cornerRadius.getFloatValue(), n8);
        }

        if ((Boolean)keyStrokesMod.showBorder.getValue()) {
            float value4 = (Float)keyStrokesMod.borderThickness.getValue();
            RenderUtils.drawRoundedBorder(f, f2, f + this.width, f2 + this.height, keyStrokesMod.cornerRadius.getFloatValue(), value4, n12);
        }

        if (this.keyCode == minecraft.gameSettings.keyBindJump.getKeyCode() && (Boolean)keyStrokesMod.showSpacebarLine.getValue()) {
            float f3 = (boolean)keyStrokesMod.centerSpacebarLine.getValue() ? this.height / 2.0F - (Float)keyStrokesMod.spacebarLineHeight.getValue() / 2.0F : 3.0F;
            if (flag) {
                Gui.drawRect(
                    f + this.width / 2.0F - this.width / (12.0F - (Float)keyStrokesMod.spacebarLineWidth.getValue()) + 1.0F,
                    f2 + f3 + 1.0F,
                    f + this.width / 2.0F + this.width / (12.0F - (Float)keyStrokesMod.spacebarLineWidth.getValue()) + 1.0F,
                    f2 + f3 + 1.0F + (Float)keyStrokesMod.spacebarLineHeight.getValue(),
                    n11
                );
            }

            Gui.drawRect(
                f + this.width / 2.0F - this.width / (12.0F - (Float)keyStrokesMod.spacebarLineWidth.getValue()),
                f2 + f3,
                f + this.width / 2.0F + this.width / (12.0F - (Float)keyStrokesMod.spacebarLineWidth.getValue()),
                f2 + f3 + (Float)keyStrokesMod.spacebarLineHeight.getValue(),
                n9
            );
        } else {
            GlStateCache.enableBlend();
            float f3 = f + this.width / 2.0F - minecraft.fontRendererObj.getStringWidth(this.label) / 2 + 0.26F;
            float f4 = f2 + this.height / 2.0F - minecraft.fontRendererObj.FONT_HEIGHT / 2 + 0.65F;
            if ((Boolean)keyStrokesMod.legacyPosition.getValue()) {
                f3 = f + this.width / 2.0F - minecraft.fontRendererObj.getStringWidth(this.label) / 2;
                f4 = f2 + this.height / 2.0F - minecraft.fontRendererObj.FONT_HEIGHT / 2 + 1.0F;
            }

            float value4 = (Float)keyStrokesMod.boxSize.getValue();
            boolean flag3 = this.keyCode == minecraft.gameSettings.keyBindAttack.getKeyCode();
            boolean flag4 = this.keyCode == minecraft.gameSettings.keyBindUseItem.getKeyCode();
            String str = flag3 ? (String)keyStrokesMod.leftCps.getValue() : (flag4 ? (String)keyStrokesMod.rightCps.getValue() : "");
            if (flag3 || flag4) {
                if (value4 > 14.0F && str.equals("With Clicks")) {
                    GlStateCache.glPushMatrix();
                    float f5 = 0.6666F;
                    GL11.glScalef(f5, f5, 0.0F);
                    f4 = f2 + this.height / 2.0F - minecraft.fontRendererObj.FONT_HEIGHT / 2 + 6.0F;
                    String str2 = keyStrokesMod.leftClicks.size() + " CPS";
                    if (this.label.equals("RMB")) {
                        str2 = keyStrokesMod.rightClicks.size() + " CPS";
                    }

                    float f6 = f + this.width / 2.0F - minecraft.fontRendererObj.getStringWidth(str2) / 2.0F * f5;
                    minecraft.fontRendererObj.drawString(str2, f6 / f5, f4 / f5, n10, flag);
                    GL11.glScalef(1.0F, 1.0F, 0.0F);
                    GlStateCache.glPopMatrix();
                    f4 = f2 + this.height / 2.0F - minecraft.fontRendererObj.FONT_HEIGHT / 2 - 3.0F;
                } else if (str.equals("Replace Clicks")) {
                    GlStateCache.glPushMatrix();
                    float f5 = 1.0F;
                    GL11.glScalef(f5, f5, 0.0F);
                    String label = this.label;
                    String str2 = value4 < 15.0F ? "" : " CPS";
                    if (flag3 && keyStrokesMod.leftClicks.size() > 0) {
                        label = keyStrokesMod.leftClicks.size() + str2;
                        if (value4 > 15.0F) {
                            f5 = 0.6666F;
                            GL11.glScalef(f5, f5, 0.0F);
                        }
                    }

                    if (flag4 && keyStrokesMod.rightClicks.size() > 0) {
                        label = keyStrokesMod.rightClicks.size() + str2;
                        if (value4 > 15.0F) {
                            f5 = 0.6666F;
                            GL11.glScalef(f5, f5, 0.0F);
                        }
                    }

                    f3 = f + this.width / 2.0F - minecraft.fontRendererObj.getStringWidth(label) / 2.0F * f5 + 0.26F;
                    f4 = f2 + this.height / 2.0F - minecraft.fontRendererObj.FONT_HEIGHT / 2 + (value4 > 15.0F && !label.equals(this.label) ? 1.65F : 0.65F);
                    if ((Boolean)keyStrokesMod.legacyPosition.getValue()) {
                        f3 = f + this.width / 2.0F - minecraft.fontRendererObj.getStringWidth(label) / 2.0F * f5;
                        f4 = f2 + this.height / 2.0F - minecraft.fontRendererObj.FONT_HEIGHT / 2 + (value4 > 15.0F && !label.equals(this.label) ? 2.0F : 1.0F);
                    }

                    minecraft.fontRendererObj.drawString(label, f3 / f5, f4 / f5, n9, flag);
                    GL11.glScalef(1.0F, 1.0F, 0.0F);
                    GlStateCache.glPopMatrix();
                }
            }

            if (!str.equals("Replace Clicks")) {
                minecraft.fontRendererObj.drawString(this.label, f3, f4, n9, flag);
            }

            GlStateCache.disableBlend();
        }
    }
}
