package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.util.ClientVersion;
import java.io.File;
import java.util.List;
import java.util.Collections;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.client.gui.Gui;
import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.font.CustomFontRenderer;
import java.util.Map;
import com.cheatbreaker.client.theme.Theme;
import net.minecraft.util.ResourceLocation;

public class ThemeEntryComponent extends SettingComponent
{
    public int intValue;
    public ResourceLocation rightPng;
    public Panel panel;
    public int intValue2;
    public Theme theme2;
    public Button delete64Png;
    public Button pencil64Png;
    
    public ThemeEntryComponent(final Panel panel, final int intValue2, final Theme theme2, final float n) {
        super(n);
        this.intValue = 0;
        this.rightPng = new ResourceLocation("client/icons/right.png");
        this.panel = panel;
        this.intValue2 = intValue2;
        this.theme2 = theme2;
        (this.delete64Png = new Button(null, null, "delete-64.png", this.x + 4, this.y + this.height - 20, this.x + this.width - 4, this.y + this.height - 6, this.theme.getNormalHighlightColorValue(), n, false, true)).setSmallIcon(true);
        (this.pencil64Png = new Button(null, null, "pencil-64.png", this.x + 4, this.y + this.height - 20, this.x + this.width - 4, this.y + this.height - 6, this.theme.getNormalHighlightColorValue(), n, false, true)).setSmallIcon(true);
    }
    
    @Override
    public void render(final int n, final int n2, final float n3) {
        final boolean b = n > (this.x + 12) * 1.0f / CheatBreaker.getInstance().getUiManager().getFloatValue() && n < (this.x + this.width - (this.theme2.isFlag2() ? 32 : 65)) * 1.0f / CheatBreaker.getInstance().getUiManager().getFloatValue() && this.isMouseOver(n, n2);
        Gui.drawRect(this.x, this.y + this.height - 1, this.x + this.width - 32, this.y + this.height, this.theme.getPrimaryTextColorValue2());
        final int intValue = 75;
        if (b) {
            if (this.intValue < intValue) {
                this.intValue += ModsScreen.animationStep(790.0f);
                if (this.intValue > intValue) {
                    this.intValue = intValue;
                }
            }
        }
        else if (this.intValue > 0) {
            final float animationStep = ModsScreen.animationStep(790.0f);
            this.intValue = ((this.intValue - animationStep < 0.0f) ? 0 : ((int)(this.intValue - animationStep)));
        }
        if (this.intValue > 0) {
            Gui.drawRect((float)(this.x + 12), (float)(int)(this.y + (this.height - this.height * (this.intValue / (float)intValue * 100.0f) / 100.0f)), this.x + this.width - (this.theme2.isFlag2() ? 32.0f : 65.5f), (float)(this.y + this.height), this.intValue2);
        }
        final boolean b2 = n > this.x * this.scale && n < (this.x + 12) * this.scale && n2 >= (this.y + this.scrollOffsetY) * this.scale && n2 <= (this.y + this.height / 2 + this.scrollOffsetY) * this.scale;
        final boolean b3 = n > this.x * this.scale && n < (this.x + 12) * this.scale && n2 > (this.y + this.height / 2 + this.scrollOffsetY) * this.scale && n2 < (this.y + this.height + this.scrollOffsetY) * this.scale;
        final float n4 = (float)RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
        final float n5 = (float)RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
        final float n6 = (float)RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
        CheatBreaker.getInstance().getModManager().setIntValue2(this.panel.intValue);
        GL11.glColor4f(n4, n5, n6, 0.35f);
        final float n7 = 2.5f;
        if (!this.theme2.isFlag2()) {
            boolean b4 = false;
            boolean b5 = false;
            final ThemeManagerPanel themeManagerPanel = (ThemeManagerPanel)this.panel;
            if (themeManagerPanel.themeEntryComponentList.indexOf(this) != 0 && themeManagerPanel.themeEntryComponentList.indexOf(this) > 2) {
                b4 = true;
                GL11.glPushMatrix();
                if (b2) {
                    GL11.glColor4f(n4, n5, n6, 0.65f);
                }
                GL11.glTranslatef(this.x + 6 - n7, this.y + 7.0f, 0.0f);
                GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, n7, -1.0f, 0.0f);
                GL11.glPopMatrix();
                GL11.glColor4f(n4, n5, n6, 0.35f);
            }
            if (themeManagerPanel.themeEntryComponentList.indexOf(this) != themeManagerPanel.themeEntryComponentList.size() - 1) {
                b5 = true;
                GL11.glPushMatrix();
                if (b3) {
                    GL11.glColor4f(n4, n5, n6, 0.65f);
                }
                GL11.glTranslatef(this.x + 6 + n7, this.y + 7.0f, 0.0f);
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, n7, 2.0f, 0.0f);
                GL11.glPopMatrix();
            }
            if (!b4 && !b5) {
                RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 2.5f, (float)(this.x + 4), this.y + 6.0f);
            }
        }
        else {
            RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 2.5f, (float)(this.x + 4), this.y + 6.0f);
        }
        if (CheatBreaker.getInstance().getConfigManager().theme == this.theme2) {
            CheatBreaker.getInstance().getFontManager().playBold18.drawString(this.theme2.getThemeNameValue().toUpperCase(), this.x + 16.0f, (float)(this.y + 3), CheatBreaker.getInstance().getConfigManager().theme.getTextColor2Value2());
        }
        else {
            CheatBreaker.getInstance().getFontManager().playRegular16.drawString(this.theme2.getThemeNameValue().toUpperCase(), this.x + 16.0f, this.y + 3.5f, CheatBreaker.getInstance().getConfigManager().theme.getTextColor2Value2());
        }
        if (CheatBreaker.getInstance().getConfigManager().theme == this.theme2) {
            CheatBreaker.getInstance().getFontManager().playRegular14.drawString(" (Active)", this.x + 17.0f + CheatBreaker.getInstance().getFontManager().playBold18.getStringWidth(this.theme2.getThemeNameValue().toUpperCase()), this.y + 4.0f, CheatBreaker.getInstance().getConfigManager().theme.getTextColor4Value2());
        }
        if (!this.theme2.isFlag2()) {
            final boolean b6 = !this.theme2.isFlag();
            this.pencil64Png.setBounds(this.x + this.width - (b6 ? 62 : 45), this.y + 1, 14, 14);
            this.pencil64Png.render(n, n2, n3);
            this.pencil64Png.scrollOffsetY = this.panel.intValue;
            if (b6) {
                this.delete64Png.setBounds(this.x + this.width - 45, this.y + 1, 14, 14);
                this.delete64Png.render(n, n2, n3);
                this.delete64Png.scrollOffsetY = this.panel.intValue;
            }
        }
    }
    
    @Override
    public void mouseClicked(final int n, final int n2, final int n3) {
        final boolean b = !this.theme2.isFlag() && this.delete64Png.isMouseOver(n, n2);
        final boolean mouseOver = this.pencil64Png.isMouseOver(n, n2);
        final boolean b2 = n > this.x * this.scale && n < (this.x + 12) * this.scale && n2 >= (this.y + this.scrollOffsetY) * this.scale && n2 <= (this.y + this.height / 2 + this.scrollOffsetY) * this.scale;
        final boolean b3 = n > this.x * this.scale && n < (this.x + 12) * this.scale && n2 > (this.y + this.height / 2 + this.scrollOffsetY) * this.scale && n2 < (this.y + this.height + this.scrollOffsetY) * this.scale;
        final ThemeManagerPanel themeManagerPanel = (ThemeManagerPanel)this.panel;
        if (!this.theme2.isFlag2() && (b2 || b3)) {
            if (b2 && ((ThemeManagerPanel)this.panel).themeEntryComponentList.indexOf(this) != 0 && ((ThemeManagerPanel)this.panel).themeEntryComponentList.indexOf(this) != 1 && ((ThemeManagerPanel)this.panel).themeEntryComponentList.indexOf(this) > 2) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
                this.theme2.intValue = themeManagerPanel.themeEntryComponentList.indexOf(this) - 1;
                themeManagerPanel.themeEntryComponentList.get(themeManagerPanel.themeEntryComponentList.indexOf(this) - 1).theme2.intValue = themeManagerPanel.themeEntryComponentList.indexOf(this);
                Collections.swap(themeManagerPanel.themeEntryComponentList, themeManagerPanel.themeEntryComponentList.indexOf(this), themeManagerPanel.themeEntryComponentList.indexOf(this) - 1);
            }
            if (b3 && themeManagerPanel.themeEntryComponentList.indexOf(this) != themeManagerPanel.themeEntryComponentList.size() - 1) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
                this.theme2.intValue = themeManagerPanel.themeEntryComponentList.indexOf(this) + 1;
                themeManagerPanel.themeEntryComponentList.get(themeManagerPanel.themeEntryComponentList.indexOf(this) + 1).theme2.intValue = themeManagerPanel.themeEntryComponentList.indexOf(this);
                Collections.swap(themeManagerPanel.themeEntryComponentList, themeManagerPanel.themeEntryComponentList.indexOf(this), themeManagerPanel.themeEntryComponentList.indexOf(this) + 1);
            }
        }
        else if (b) {
            final File file = new File(new File(Minecraft.getMinecraft().mcDataDir + File.separator + "config" + File.separator + "cheatbreaker-client-" + ClientVersion.text + "-" + CheatBreaker.getInstance().getBranch() + File.separator + "themes") + File.separator + this.theme2.getThemeNameValue() + ".json");
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
            if (CheatBreaker.getInstance().getConfigManager().theme == this.theme2) {
                final Theme theme = CheatBreaker.getInstance().themes.values().stream().filter(theme2 -> theme2 != this.theme2).findFirst().orElse(CheatBreaker.getInstance().getConfigManager().getDefaultDarkTheme());
                CheatBreaker.getInstance().getConfigManager().theme = theme;
                CheatBreaker.getInstance().configFileManager.loadTheme(theme.getThemeNameValue());
            }
            if (file.exists() && file.delete()) {
                final Map<String, Theme> themes = CheatBreaker.getInstance().themes;
                themes.entrySet().stream().filter(entry -> entry.getValue() == this.theme2).forEach(entry2 -> {
                    final Object o = themes.remove(entry2.getKey());
                    return;
                });
                CheatBreaker.getInstance().setThemes(themes);
                themeManagerPanel.themeEntryComponentList.removeIf(themeEntryComponent -> themeEntryComponent == this);
            }
            CheatBreaker.getInstance().getUiManager().run3();
        }
        else if (!this.theme2.isFlag2() && mouseOver) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
            ModsScreen.modsScreen.settingsPanel.showingThemeSettings = true;
            ModsScreen.modsScreen.settingsPanel.theme3 = this.theme2;
            ModsScreen.modsScreen.settingsPanel.panel = ModsScreen.modsScreen.themeManagerPanel;
            ModsScreen.modsScreen.settingsPanel.buildThemeComponents();
            ModsScreen.modsScreen.settingsPanel.showingGlobalSettings = false;
            ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.settingsPanel;
        }
        else if (CheatBreaker.getInstance().getConfigManager().theme != null && CheatBreaker.getInstance().getConfigManager().theme != this.theme2) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
            CheatBreaker.getInstance().configFileManager.saveTheme(CheatBreaker.getInstance().getConfigManager().theme.getThemeNameValue());
            CheatBreaker.getInstance().getModManager().autoTextMod.getSettings().removeIf(setting -> setting.getName().startsWith("Hot key"));
            CheatBreaker.getInstance().getModManager().autoTextMod.hotkeySettings.clear();
            CheatBreaker.getInstance().getModManager().autoTextMod.nextHotkeyIndex = 1;
            CheatBreaker.getInstance().getConfigManager().theme = this.theme2;
            CheatBreaker.getInstance().configFileManager.loadTheme(CheatBreaker.getInstance().getConfigManager().theme.getThemeNameValue());
            CheatBreaker.getInstance().getUiManager().run3();
        }
    }
}
