package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.client.renderer.entity.RenderTNTPrimed;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.ResourceLocation;

public class TNTTimerMod extends AbstractMod {
    public Setting textOptions;
    public Setting textShadow;
    public Setting dynamicTextColor;
    public Setting staticTextColor;
    public Setting backgroundOptions;
    public Setting showBackground;
    public Setting backgroundColor;
    public boolean flag;

    public Color createColor(int n) {
        return this.dynamicTextColor.getBooleanValue()
            ? new Color(1.0F - Math.min(n / (this.flag ? 52.0F : 80.0F), 1.0F), Math.min(n / (this.flag ? 52.0F : 80.0F), 1.0F), 0.0F)
            : this.staticTextColor.toColor(this.staticTextColor.getColorValue());
    }

    public String createDecimalFormat(int n, float f) {
        return new DecimalFormat("0.00").format((n - f) / 20.0F);
    }

    public void renderTimer(RenderTNTPrimed renderTNTPrimed, EntityTNTPrimed entityTNTPrimed, double d, double d2, double d3, float f) {
    }

    public TNTTimerMod() throws java.io.IOException {
        super("TNT Timer");
        this.setEnabledByDefault(false);
        this.setDescription("Shows a timer above TNT.");
        this.setAuthors("Sk1er");
        this.setKeywords("TNT Countdown");
        this.setCategories(ModCategory.HUD);
        this.setIcon(new ResourceLocation("client/icons/mods/tnt.png"), 36, 40);
        this.textOptions = new SettingGroup(this, "Text Options").changeValue(true);
        this.textShadow = new Setting(this, "Text Shadow").changeValue(false).withParents(this.textOptions);
        this.dynamicTextColor = new Setting(this, "Dynamic Text Color").changeValue(true).withParents(this.textOptions);
        this.staticTextColor = new Setting(this, "Static Text Color")
            .changeValue(-1)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withInvertedHierarchyParents(this.dynamicTextColor);
        this.backgroundOptions = new SettingGroup(this, "Background Options").changeValue(true);
        this.showBackground = new Setting(this, "Show Background")
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.SIMPLE)
            .withParents(this.backgroundOptions);
        this.backgroundColor = new Setting(this, "Background Color")
            .changeValue(1862270976)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.SIMPLE)
            .withHierarchyParents(this.showBackground);
    }
}
