package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.theme.Theme;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.ui.screen.ThemeNameScreen;
import com.cheatbreaker.client.util.FileOpener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class ThemeManagerPanel extends Panel {
    public List<ThemeEntryComponent> themeEntryComponentList = new ArrayList<>();
    public int highlightColor = this.theme2.getNormalHighlightColorValue();
    public Button openThemesFolder;
    public Button arrow64Png;
    public Button addNewTheme;

    public void loadThemes() {
        new Thread(() -> {
            this.themeEntryComponentList.clear();
            Theme theme = CheatBreaker.getInstance().getThemes().get("Default Dark");
            Theme theme2 = CheatBreaker.getInstance().getThemes().get("Default Light");
            this.themeEntryComponentList.add(0, new ThemeEntryComponent(this, this.highlightColor, theme, this.scale));
            this.themeEntryComponentList.add(1, new ThemeEntryComponent(this, this.highlightColor, theme2, this.scale));

            for (Theme theme3 : CheatBreaker.getInstance().getConfigFileManager().getCustomThemes()) {
                this.themeEntryComponentList.add(new ThemeEntryComponent(this, this.highlightColor, theme3, this.scale));
            }
        }).start();
    }

    @Override
    public void selectMod(AbstractMod abstractMod) {
    }

    @Override
    public void keyTyped(char c, int n) {
        if (n == 14) {
            ModsScreen.modsScreen.settingsPanel.abstractMod = null;
            ModsScreen.modsScreen.settingsPanel.theme3 = null;
            ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.settingsPanel;
        }
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws IOException {
        if (this.arrow64Png.isMouseOver(n, n2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.settingsPanel;
        } else if (this.addNewTheme.isMouseOver(n, n2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            Minecraft.getMinecraft().displayGuiScreen(new ThemeNameScreen(ModsScreen.modsScreen, this, this.highlightColor, this.scale));
        } else if (this.openThemesFolder.isMouseOver(n, n2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));

            try {
                FileOpener.openFile(CheatBreaker.getInstance().getConfigFileManager().themesDirectory);
            } catch (IllegalArgumentException | IOException e) {
                e.printStackTrace();
            }
        } else {
            for (ThemeEntryComponent themeEntryComponent : this.themeEntryComponentList) {
                if (themeEntryComponent.isMouseOver(n + 15, n2)) {
                    themeEntryComponent.mouseClicked(n, n2, n3);
                    return;
                }
            }

            boolean flag = n > (this.x + this.width - 92) * this.scale
                && n < (this.x + this.width - 6) * this.scale
                && n2 > (this.y + this.contentHeight + 7 + this.scrollOffset) * this.scale
                && n2 < (this.y + this.contentHeight + 17 + this.scrollOffset) * this.scale;
            if (flag) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                Minecraft.getMinecraft().displayGuiScreen(new ThemeNameScreen(ModsScreen.modsScreen, this, this.highlightColor, this.scale));
            }

            double d = this.height - 10;
            double contentHeight = this.contentHeight;
            double d2 = d / contentHeight * 100.0;
            double d3 = d / 100.0 * d2;
            double d4 = this.scrollOffset / 100.0 * d2;
            boolean flag2 = n > (this.x + this.width - 9) * this.scale
                && n < (this.x + this.width - 3) * this.scale
                && n2 > (this.y + 11 - d4) * this.scale
                && n2 < (this.y + 8 + d3 - d4) * this.scale;
            boolean flag3 = n > (this.x + this.width - 9) * this.scale
                && n < (this.x + this.width - 3) * this.scale
                && n2 > (double)(this.y + 11) * this.scale
                && n2 < (this.y + 6 + d - 3.0) * this.scale;
            if (n3 == 0 && flag3 || flag2) {
                this.dragging = true;
            }
        }
    }

    @Override
    public boolean hasSettings(AbstractMod abstractMod) {
        return false;
    }

    @Override
    public void render(int n, int n2, float f) {
        RenderUtils.drawRoundedRectFilledPrecise(this.x, this.y, this.x + this.width, this.y + this.height + 2, 8.0, this.theme2.getMenuBackgroundColorValue());
        this.updateScroll(n, n2);
        Gui.drawRect(this.x + 32, this.y + 4, this.x + 33, this.y + Math.max(this.height, this.contentHeight) - 4, this.theme2.getPrimaryTextColorValue2());
        CheatBreaker.getInstance()
            .getFontManager()
            .ubuntuM16
            .drawString("Theme Manager".toUpperCase(), this.x + 38, this.y + 6, this.theme2.getPrimaryTextColorValue());
        Gui.drawRect(this.x + 38, this.y + 17, this.x + this.width - 6, this.y + 18, this.theme2.getPrimaryTextColorValue2());
        this.arrow64Png.setBounds(this.x + 2, this.y + 2, 28, 28);
        this.arrow64Png.scrollOffsetY = this.scrollOffset;
        this.arrow64Png.render(n, n2, f);
        this.contentHeight = 15;

        for (int i = 0; i < this.themeEntryComponentList.size(); i++) {
            ThemeEntryComponent themeEntryComponent = this.themeEntryComponentList.get(i);
            themeEntryComponent.setBounds(this.x + 38, this.y + 18 + i * 18, this.width - 15, 18);
            themeEntryComponent.scrollOffsetY = this.scrollOffset;
            themeEntryComponent.render(n, n2, f);
            this.contentHeight = this.contentHeight + themeEntryComponent.getHeight();
        }

        this.addNewTheme
            .setBounds(
                this.x + this.width - (this.addNewTheme.isMouseOver(n, n2) ? 155 : 80),
                (int)(this.y + this.contentHeight + 7.5F),
                this.addNewTheme.isMouseOver(n, n2) ? 148 : 73,
                10
            );
        this.addNewTheme.scrollOffsetY = this.scrollOffset;
        this.addNewTheme.render(n, n2, f);
        this.contentHeight += 10;
        this.openThemesFolder.scrollOffsetY = this.scrollOffset;
        this.openThemesFolder.setBounds(this.x + this.width - 135, this.y + this.contentHeight + 16, 125, 20);
        this.openThemesFolder.render(n, n2, f);
        this.contentHeight += 50;
        this.endScrollRegion(n, n2);
    }

    public ThemeManagerPanel(float f, int n, int n2, int n3, int n4) {
        super(f, n, n2, n3, n4);
        int n5 = this.x + n3 - 120;
        this.openThemesFolder = new Button(
            CheatBreaker.getInstance().getFontManager().playBold18,
            null,
            "Open Themes Folder",
            n5,
            this.y + n4 + 4,
            110,
            28,
            this.highlightColor,
            f,
            false,
            true
        );
        this.arrow64Png = new Button(null, "arrow-64.png", this.x + 2, this.y + 4, 28, 28, this.highlightColor, f, false, true);
        n5 = this.x + 2;
        int n6 = this.y + 4;
        this.addNewTheme = new Button(
            CheatBreaker.getInstance().getFontManager().ubuntuM16, null, "Add New Theme", n5, n6, 14, 14, this.highlightColor, f, false, true
        );
        this.addNewTheme.setHoverLabel("(COPIES CURRENT) Add New Theme");
        this.loadThemes();
    }
}
