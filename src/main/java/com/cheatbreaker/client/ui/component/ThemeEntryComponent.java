package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.impl.AutoTextMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.theme.Theme;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.util.ClientVersion;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ThemeEntryComponent extends SettingComponent {
    public int intValue = 0;
    public ResourceLocation rightPng = new ResourceLocation("client/icons/right.png");
    public Panel panel;
    public int intValue2;
    public Theme entryTheme;
    public Button delete64Png;
    public Button pencil64Png;

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        boolean flag = !this.entryTheme.isFlag() && this.delete64Png.isMouseOver(n, n2);
        boolean isMouseOver = this.pencil64Png.isMouseOver(n, n2);
        boolean flag2 = n > this.x * this.scale
            && n < (this.x + 12) * this.scale
            && n2 >= (this.y + this.scrollOffsetY) * this.scale
            && n2 <= (this.y + this.height / 2 + this.scrollOffsetY) * this.scale;
        boolean flag3 = n > this.x * this.scale
            && n < (this.x + 12) * this.scale
            && n2 > (this.y + this.height / 2 + this.scrollOffsetY) * this.scale
            && n2 < (this.y + this.height + this.scrollOffsetY) * this.scale;
        ThemeManagerPanel panel = (ThemeManagerPanel)this.panel;
        if (this.entryTheme.isBuiltIn() || !flag2 && !flag3) {
            if (flag) {
                File file = new File(
                    Minecraft.getMinecraft().mcDataDir
                        + File.separator
                        + "config"
                        + File.separator
                        + "cheatbreaker-client-"
                        + ClientVersion.version
                        + "-"
                        + CheatBreaker.getInstance().getBranch()
                        + File.separator
                        + "themes"
                );
                File file2 = new File(file + File.separator + this.entryTheme.getThemeNameValue() + ".json");
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                if (CheatBreaker.getInstance().getConfigManager().theme == this.entryTheme) {
                    Theme orElse = CheatBreaker.getInstance()
                        .themes
                        .values()
                        .stream()
                        .filter(theme -> theme != this.entryTheme)
                        .findFirst()
                        .orElse(CheatBreaker.getInstance().getConfigManager().getDefaultDarkTheme());
                    CheatBreaker.getInstance().getConfigManager().theme = orElse;
                    CheatBreaker.getInstance().configFileManager.loadTheme(orElse.getThemeNameValue());
                }

                if (file2.exists() && file2.delete()) {
                    Map themes = CheatBreaker.getInstance().themes;
                    themes.values().removeIf(obj -> obj == this.entryTheme);
                    CheatBreaker.getInstance().setThemes(themes);
                    panel.themeEntryComponentList.removeIf(themeEntryComponent -> themeEntryComponent == this);
                }

                CheatBreaker.getInstance().getUiManager().openThemes();
            } else if (!this.entryTheme.isBuiltIn() && isMouseOver) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                ModsScreen.modsScreen.settingsPanel.showingThemeSettings = true;
                ModsScreen.modsScreen.settingsPanel.theme3 = this.entryTheme;
                ModsScreen.modsScreen.settingsPanel.panel = ModsScreen.modsScreen.themeManagerPanel;
                ModsScreen.modsScreen.settingsPanel.buildThemeComponents();
                ModsScreen.modsScreen.settingsPanel.showingGlobalSettings = false;
                ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.settingsPanel;
            } else if (CheatBreaker.getInstance().getConfigManager().theme != null && CheatBreaker.getInstance().getConfigManager().theme != this.entryTheme) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                CheatBreaker.getInstance().configFileManager.saveTheme(CheatBreaker.getInstance().getConfigManager().theme.getThemeNameValue());
                AutoTextMod autoTextMod = CheatBreaker.getInstance().getModManager().autoTextMod;
                autoTextMod.getSettings().removeIf(setting -> setting.getName().startsWith("Hot key"));
                CheatBreaker.getInstance().getModManager().autoTextMod.hotkeySettings.clear();
                CheatBreaker.getInstance().getModManager().autoTextMod.nextHotkeyIndex = 1;
                CheatBreaker.getInstance().getConfigManager().theme = this.entryTheme;
                CheatBreaker.getInstance().configFileManager.loadTheme(CheatBreaker.getInstance().getConfigManager().theme.getThemeNameValue());
                CheatBreaker.getInstance().getUiManager().openThemes();
            }
        } else {
            if (flag2
                && ((ThemeManagerPanel)this.panel).themeEntryComponentList.indexOf(this) != 0
                && ((ThemeManagerPanel)this.panel).themeEntryComponentList.indexOf(this) != 1
                && ((ThemeManagerPanel)this.panel).themeEntryComponentList.indexOf(this) > 2) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                this.entryTheme.intValue = panel.themeEntryComponentList.indexOf(this) - 1;
                panel.themeEntryComponentList.get(panel.themeEntryComponentList.indexOf(this) - 1).entryTheme.intValue = panel.themeEntryComponentList.indexOf(this);
                Collections.swap(panel.themeEntryComponentList, panel.themeEntryComponentList.indexOf(this), panel.themeEntryComponentList.indexOf(this) - 1);
            }

            if (flag3 && panel.themeEntryComponentList.indexOf(this) != panel.themeEntryComponentList.size() - 1) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                this.entryTheme.intValue = panel.themeEntryComponentList.indexOf(this) + 1;
                panel.themeEntryComponentList.get(panel.themeEntryComponentList.indexOf(this) + 1).entryTheme.intValue = panel.themeEntryComponentList.indexOf(this);
                Collections.swap(panel.themeEntryComponentList, panel.themeEntryComponentList.indexOf(this), panel.themeEntryComponentList.indexOf(this) + 1);
            }
        }
    }

    public ThemeEntryComponent(Panel panel, int n, Theme theme, float f) {
        super(f);
        this.panel = panel;
        this.intValue2 = n;
        this.entryTheme = theme;
        this.delete64Png = new Button(
            null,
            null,
            "delete-64.png",
            this.x + 4,
            this.y + this.height - 20,
            this.x + this.width - 4,
            this.y + this.height - 6,
            this.theme.getNormalHighlightColorValue(),
            f,
            false,
            true
        );
        this.delete64Png.setSmallIcon(true);
        this.pencil64Png = new Button(
            null,
            null,
            "pencil-64.png",
            this.x + 4,
            this.y + this.height - 20,
            this.x + this.width - 4,
            this.y + this.height - 6,
            this.theme.getNormalHighlightColorValue(),
            f,
            false,
            true
        );
        this.pencil64Png.setSmallIcon(true);
    }

    @Override
    public void render(int n, int n2, float f) {
        boolean flag = n > (this.x + 12) * 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue()
            && n < (this.x + this.width - (this.entryTheme.isBuiltIn() ? 32 : 65)) * 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue()
            && this.isMouseOver(n, n2);
        Gui.drawRect(this.x, this.y + this.height - 1, this.x + this.width - 32, this.y + this.height, this.theme.getPrimaryTextColorValue2());
        byte b = 75;
        if (flag) {
            if (this.intValue < b) {
                float animationStep = ModsScreen.animationStep(790.0F);
                this.intValue = (int)(this.intValue + animationStep);
                if (this.intValue > b) {
                    this.intValue = b;
                }
            }
        } else if (this.intValue > 0) {
            float animationStep = ModsScreen.animationStep(790.0F);
            this.intValue = this.intValue - animationStep < 0.0F ? 0 : (int)(this.intValue - animationStep);
        }

        if (this.intValue > 0) {
            float f2 = (float)this.intValue / b * 100.0F;
            Gui.drawRect(
                this.x + 12,
                (int)(this.y + (this.height - this.height * f2 / 100.0F)),
                this.x + this.width - (this.entryTheme.isBuiltIn() ? 32.0F : 65.5F),
                this.y + this.height,
                this.intValue2
            );
        }

        boolean flag2 = n > this.x * this.scale
            && n < (this.x + 12) * this.scale
            && n2 >= (this.y + this.scrollOffsetY) * this.scale
            && n2 <= (this.y + this.height / 2 + this.scrollOffsetY) * this.scale;
        boolean flag3 = n > this.x * this.scale
            && n < (this.x + 12) * this.scale
            && n2 > (this.y + this.height / 2 + this.scrollOffsetY) * this.scale
            && n2 < (this.y + this.height + this.scrollOffsetY) * this.scale;
        float red = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
        float green = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
        float blue = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
        CheatBreaker.getInstance().getModManager().setIntValue2(this.panel.scrollOffset);
        GL11.glColor4f(red, green, blue, 0.35F);
        float f2 = 2.5F;
        if (!this.entryTheme.isBuiltIn()) {
            boolean flag4 = false;
            boolean flag5 = false;
            ThemeManagerPanel panel = (ThemeManagerPanel)this.panel;
            if (panel.themeEntryComponentList.indexOf(this) != 0 && panel.themeEntryComponentList.indexOf(this) > 2) {
                flag4 = true;
                GL11.glPushMatrix();
                if (flag2) {
                    GL11.glColor4f(red, green, blue, 0.65F);
                }

                GL11.glTranslatef(this.x + 6 - f2, this.y + 7.0F, 0.0F);
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, f2, -1.0F, 0.0F);
                GL11.glPopMatrix();
                GL11.glColor4f(red, green, blue, 0.35F);
            }

            if (panel.themeEntryComponentList.indexOf(this) != panel.themeEntryComponentList.size() - 1) {
                flag5 = true;
                GL11.glPushMatrix();
                if (flag3) {
                    GL11.glColor4f(red, green, blue, 0.65F);
                }

                GL11.glTranslatef(this.x + 6 + f2, this.y + 7.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, f2, 2.0F, 0.0F);
                GL11.glPopMatrix();
            }

            if (!flag4 && !flag5) {
                RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 2.5F, this.x + 4, this.y + 6.0F);
            }
        } else {
            RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 2.5F, this.x + 4, this.y + 6.0F);
        }

        if (CheatBreaker.getInstance().getConfigManager().theme == this.entryTheme) {
            CheatBreaker.getInstance()
                .getFontManager()
                .playBold18
                .drawString(
                    this.entryTheme.getThemeNameValue().toUpperCase(),
                    this.x + 16.0F,
                    this.y + 3,
                    CheatBreaker.getInstance().getConfigManager().theme.getTextColor2Value2()
                );
        } else {
            CheatBreaker.getInstance()
                .getFontManager()
                .playRegular16
                .drawString(
                    this.entryTheme.getThemeNameValue().toUpperCase(),
                    this.x + 16.0F,
                    this.y + 3.5F,
                    CheatBreaker.getInstance().getConfigManager().theme.getTextColor2Value2()
                );
        }

        if (CheatBreaker.getInstance().getConfigManager().theme == this.entryTheme) {
            CheatBreaker.getInstance()
                .getFontManager()
                .playRegular14
                .drawString(
                    " (Active)",
                    this.x + 17.0F + CheatBreaker.getInstance().getFontManager().playBold18.getStringWidth(this.entryTheme.getThemeNameValue().toUpperCase()),
                    this.y + 4.0F,
                    CheatBreaker.getInstance().getConfigManager().theme.getTextColor4Value2()
                );
        }

        if (!this.entryTheme.isBuiltIn()) {
            boolean flag4 = !this.entryTheme.isFlag();
            this.pencil64Png.setBounds(this.x + this.width - (flag4 ? 62 : 45), this.y + 1, 14, 14);
            this.pencil64Png.render(n, n2, f);
            this.pencil64Png.scrollOffsetY = this.panel.scrollOffset;
            if (flag4) {
                this.delete64Png.setBounds(this.x + this.width - 45, this.y + 1, 14, 14);
                this.delete64Png.render(n, n2, f);
                this.delete64Png.scrollOffsetY = this.panel.scrollOffset;
            }
        }
    }
}
