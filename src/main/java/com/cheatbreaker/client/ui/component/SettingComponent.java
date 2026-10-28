package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.theme.Theme;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.client.Minecraft;

public abstract class SettingComponent {
    public int scrollOffsetY = 0;
    public int height;
    public Setting setting;
    public int width;
    public int y;
    public Theme theme;
    public int x;
    public float scale;

    public void mouseScrolled(int n) {
    }

    public void onClose() {
    }

    public SettingComponent(float f) {
        this.theme = CheatBreaker.getInstance().getConfigManager().theme;
        this.scale = f;
    }

    public void keyTyped(char c, int n) {
    }

    public abstract void mouseClicked(int n, int n2, int n3) throws java.io.IOException;

    public boolean mouseClickedFloat(float f, float f2, int n, boolean flag) {
        return false;
    }

    public abstract void render(int n, int n2, float f);

    public boolean isMouseOver(int n, int n2) {
        return n > this.x * this.scale
            && n < (this.x + this.width) * this.scale
            && n2 > (this.y + this.scrollOffsetY) * this.scale
            && n2 < (this.y + this.height + this.scrollOffsetY) * this.scale
            && Minecraft.getMinecraft().currentScreen instanceof ModsScreen;
    }

    public void setBounds(int n, int n2, int n3, int n4) {
        this.x = n;
        this.y = n2;
        this.width = n3;
        this.height = n4;
    }

    public boolean isHiddenByCustomizationLevel(Setting setting) {
        if (setting.getCustomizationLevel() != null) {
            if (CheatBreaker.getInstance().getKeybindSettings().customizationLevel.getValue().equals("Simple")) {
                return !setting.getCustomizationLevel().equals(SettingsLevel.SIMPLE);
            }

            if (CheatBreaker.getInstance().getKeybindSettings().customizationLevel.getValue().equals("Medium")) {
                return !setting.getCustomizationLevel().equals(SettingsLevel.SIMPLE) && !setting.getCustomizationLevel().equals(SettingsLevel.MEDIUM);
            }
        }

        return false;
    }

    public void renderSettingLabel(Setting setting, int n, int n2) {
    }

    public int getHeight() {
        return this.height;
    }

    public void tick() {
    }

    public boolean isHidden(Setting setting) {
        if (setting == null) {
            return true;
        }

        if (ModsScreen.modsScreen.settingsPanel.search.getText().length() != 0
            && this.isHiddenByCustomizationLevel(setting)
            && CheatBreaker.getInstance().getKeybindSettings().bypassCustomizationLevel.getBooleanValue()) {
            return false;
        }

        if (ModsScreen.modsScreen.settingsPanel.search.getText().length() != 0
            && CheatBreaker.getInstance().getKeybindSettings().bypassCustomizationLevel.getBooleanValue()) {
            return false;
        }

        if (this.isHiddenByCustomizationLevel(setting)) {
            return true;
        }

        if (setting.getCondition() != null && !Boolean.valueOf(setting.getCondition().getAsBoolean())) {
            return true;
        }

        Setting[][] settingses = new Setting[][]{setting.getHierarchyParents(), setting.getInvertedHierarchyParents(), setting.getOptionalHierarchyParents()};
        Setting[][] settingses2 = new Setting[][]{setting.getParents(), setting.getInvertedParents(), setting.getOptionalParents()};

        for (Setting[] settings : settingses) {
            if (settings != null) {
                if (!setting.areParentsSatisfied(settings, settings == setting.getInvertedHierarchyParents(), settings == setting.getOptionalHierarchyParents())) {
                    return true;
                }

                for (Setting setting2 : settings) {
                    if (this.isHidden(setting2)) {
                        return true;
                    }
                }
            }
        }

        for (Setting[] settings : settingses2) {
            if (settings != null && !setting.areParentsSatisfied(settings, settings == setting.getInvertedParents(), settings == setting.getOptionalParents())) {
                return true;
            }
        }

        return false;
    }
}
