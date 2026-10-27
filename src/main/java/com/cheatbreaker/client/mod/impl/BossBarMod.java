package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.event.ScaledResolutionEvent8;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import java.awt.Color;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.ResourceLocation;

public class BossBarMod
extends AbstractHudMod {
    public final String text = "Wither";
    public Pattern pattern = Pattern.compile("(?i)\u00a7[0-9A-F]");
    public Pattern pattern2 = Pattern.compile("(?i)\u00a7[1-69A-E]");
    public Setting generalOptions;
    public Setting renderText;
    public Setting renderHealth;
    public ResourceLocation clientIconsBossbar;
    public Setting textShadow;
    public Setting bossBarTexture;
    public Setting setting;
    public Setting bossBarWidth;
    public Setting bossBarHeight;
    public Setting colorOptions;
    public Setting stripColorFormatting;
    public Setting textColor;
    public Setting bossBarColor;
    public Setting topBossBarColor;
    public Setting bottomBossBarColor;
    public Setting outlineBossBarColor;

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        if (!this.shouldRender() || this.hidden && !(this.mc.currentScreen instanceof ModsScreen)) {
            return;
        }
        GlStateManager.pushMatrix();
        this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime;
            this.render(BossStatus.bossName, BossStatus.healthScale, 0, (Boolean)this.renderText.getValue() != false ? 10 : 0);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderPreview(int n, int n2, int n3, int n4) {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.text, (int)((float)(n + 1 + n3 / 2) - (float)Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.text) / 2.0f), n2 + n4 / 2 - 36, -1);
    }

    public void onScaledResolutionEvent8(ScaledResolutionEvent8 scaledResolutionEvent8) {
        if (!this.isRenderHud() && BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime;
            this.render(BossStatus.bossName, BossStatus.healthScale, scaledResolutionEvent8.getScaledResolution().getScaledWidth() / 2 - 91, 12);
        }
    }

    public void render(String string, float f, int n, int n2) {
        float f2;
        GlStateManager.enableBlend();
        int n3 = this.bossBarColor.getColorValue();
        if (this.bossBarTexture.getValue().equals("Texture Pack")) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(Gui.icons);
        } else {
            GlStateManager.color((float)(n3 >> 16 & 0xFF) / 255.0f, (float)(n3 >> 8 & 0xFF) / 255.0f, (float)(n3 & 0xFF) / 255.0f, (float)(n3 >> 24 & 0xFF) / 255.0f);
            this.mc.getTextureManager().bindTexture(this.clientIconsBossbar);
        }
        FontRenderer fontRenderer = this.mc.fontRendererObj;
        float f3 = this.bossBarTexture.getValue().equals("Custom") ? this.bossBarWidth.getFloatValue() : 182.0f;
        float f4 = (Boolean)this.renderText.getValue() == false || (float)fontRenderer.getStringWidth(string) < f3 ? (float)n : (float)n + ((float)fontRenderer.getStringWidth(string) - f3) * this.setting.getFloatValue() / 100.0f;
        float f5 = f * f3;
        if (((Boolean)this.renderHealth.getValue()).booleanValue()) {
            if (this.bossBarTexture.getValue().equals("Custom")) {
                int n4 = new Color((this.topBossBarColor.getColorValue() >> 16 & 0xFF) / 4, (this.topBossBarColor.getColorValue() >> 8 & 0xFF) / 4, (this.topBossBarColor.getColorValue() & 0xFF) / 4, this.topBossBarColor.getColorValue() >> 24 & 0xFF).getRGB();
                int n5 = new Color((this.bottomBossBarColor.getColorValue() >> 16 & 0xFF) / 4, (this.bottomBossBarColor.getColorValue() >> 8 & 0xFF) / 4, (this.bottomBossBarColor.getColorValue() & 0xFF) / 4, this.bottomBossBarColor.getColorValue() >> 24 & 0xFF).getRGB();
                RenderUtils.drawBorderedGradientRect(f4, n2, f4 + f3, (float)n2 + this.bossBarHeight.getFloatValue(), this.outlineBossBarColor.getColorValue(), n4, n5);
                if (f5 > 0.0f) {
                    RenderUtils.drawBorderedGradientRect(f4, n2, f4 + f5, (float)n2 + this.bossBarHeight.getFloatValue(), 0, this.topBossBarColor.getColorValue(), this.bottomBossBarColor.getColorValue());
                }
                RenderUtils.drawBorderedGradientRect(f4, n2, f4 + f3, (float)n2 + this.bossBarHeight.getFloatValue(), this.outlineBossBarColor.getColorValue(), 0, 0);
            } else {
                this.mc.ingameGUI.drawTexturedModalRect(f4, (float)n2, 0, 74, (int)f3, 5);
                this.mc.ingameGUI.drawTexturedModalRect(f4, (float)n2, 0, 74, (int)f3, 5);
                if (f5 > 0.0f) {
                    this.mc.ingameGUI.drawTexturedModalRect(f4, (float)n2, 0, 79, (int)f5, 5);
                }
            }
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        if (((Boolean)this.renderText.getValue()).booleanValue()) {
            fontRenderer.drawString(this.formatText(string), (float)n + this.hudWidth / 2.0f - (float)(fontRenderer.getStringWidth(string) / 2), n2 - 10, this.textColor.getColorValue(), (Boolean)this.textShadow.getValue());
        }
        float f6 = ((Boolean)this.renderHealth.getValue()).booleanValue() ? (this.bossBarTexture.getValue().equals("Custom") ? this.bossBarHeight.getFloatValue() : 5.0f) : 0.0f;
        float f7 = (Boolean)this.renderText.getValue() != false ? 9.0f : 0.0f;
        float f8 = f2 = (Boolean)this.renderText.getValue() != false && (Boolean)this.renderHealth.getValue() != false ? 1.0f : 0.0f;
        this.setHudSize(((Boolean)this.renderHealth.getValue()).booleanValue() ? (((Boolean)this.renderText.getValue()).booleanValue() ? Math.max((float)fontRenderer.getStringWidth(string), f3) : f3) : (float)fontRenderer.getStringWidth(string), f6 + f7 + f2);
        GlStateManager.disableBlend();
    }

    public BossBarMod() {
        super("Boss Bar");
        this.setAnchorPointAndDefault(AnchorPoint.MIDDLE_TOP);
        this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
        this.renderText = new Setting(this, "Render Text").changeValue(true).withParents(this.generalOptions);
        this.renderHealth = new Setting(this, "Render Health").changeValue(true).withParents(this.generalOptions);
        this.textShadow = new Setting(this, "Text Shadow").changeValue(true).withHierarchyParents(this.renderText);
        this.bossBarTexture = new Setting(this, "Boss Bar Texture").changeValue("Texture Pack").setStringValues("Texture Pack", "Default", "Blurred", "Custom").setCustomizationLevel((Object)SettingsLevel.MEDIUM).withHierarchyParents(this.renderHealth).setOnUpdate(object -> {
            this.clientIconsBossbar = new ResourceLocation("client/icons/bossbar/" + object.toString().toLowerCase() + ".png");
        });
        this.setting = new Setting(this, "Text Position").changeValue(Float.valueOf(50.0f)).setRange(Float.valueOf(0.0f), Float.valueOf(100.0f)).setUnit("%").setCustomizationLevel((Object)SettingsLevel.MEDIUM).setCondition(this.renderHealth::getBooleanValue).withHierarchyParents(this.renderText);
        this.bossBarWidth = new Setting(this, "Boss Bar Width").changeValue(Float.valueOf(182.0f)).setRange(Float.valueOf(100.0f), Float.valueOf(200.0f)).setUnit("px").setCustomizationLevel((Object)SettingsLevel.MEDIUM).setCondition(() -> this.bossBarTexture.getValue().equals("Custom")).withHierarchyParents(this.renderHealth);
        this.bossBarHeight = new Setting(this, "Boss Bar Height").changeValue(Float.valueOf(5.0f)).setRange(Float.valueOf(3.0f), Float.valueOf(24.0f)).setUnit("px").setCustomizationLevel((Object)SettingsLevel.MEDIUM).setCondition(() -> this.bossBarTexture.getValue().equals("Custom")).withHierarchyParents(this.renderHealth);
        this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true);
        this.stripColorFormatting = new Setting(this, "Strip Color Formatting").setDescription("Remove all color formatting so you can set your own color.").changeValue("OFF").setStringValues("OFF", "Not Neutral", "ALL").withHierarchyParents(this.colorOptions);
        this.textColor = new Setting(this, "Text Color").changeValue(-1).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE).setCustomizationLevel((Object)SettingsLevel.MEDIUM).withParents(this.colorOptions, this.renderText);
        this.bossBarColor = new Setting(this, "Boss Bar Color").changeValue(-65337).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE).setCustomizationLevel((Object)SettingsLevel.MEDIUM).setCondition(() -> !this.bossBarTexture.getValue().equals("Texture Pack") && !this.bossBarTexture.getValue().equals("Custom")).withParents(this.colorOptions, this.renderHealth);
        this.topBossBarColor = new Setting(this, "Top Boss Bar Color").changeValue(-1310536).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE).setCustomizationLevel((Object)SettingsLevel.MEDIUM).setCondition(() -> this.bossBarTexture.getValue().equals("Custom")).withParents(this.colorOptions, this.renderHealth);
        this.bottomBossBarColor = new Setting(this, "Bottom Boss Bar Color").changeValue(-7602067).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE).setCustomizationLevel((Object)SettingsLevel.MEDIUM).setCondition(() -> this.bossBarTexture.getValue().equals("Custom")).withParents(this.colorOptions, this.renderHealth);
        this.outlineBossBarColor = new Setting(this, "Outline Boss Bar Color").changeValue(-11927494).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE).setCustomizationLevel((Object)SettingsLevel.MEDIUM).setCondition(() -> this.bossBarTexture.getValue().equals("Custom")).withParents(this.colorOptions, this.renderHealth);
        this.setIcon(new ResourceLocation("client/icons/mods/bossbar.png"), 106, 5);
        this.setIconOffsetY(3);
        this.setDescription("Displays the current boss and health.");
        this.setCategories(ModCategory.HUD, ModCategory.SERVER);
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
        this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
        this.addEventHandler(ScaledResolutionEvent8.class, this::onScaledResolutionEvent8);
        this.setEnabledByDefault(true);
    }

    public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) {
        if (!this.isRenderHud()) {
            return;
        }
        GlStateManager.pushMatrix();
        this.applyHudTransformForSize(scaledResolutionEvent7.getScaledResolution());
        if (BossStatus.bossName == null || BossStatus.statusBarTime <= 0) {
            this.render(this.text, 1.0f, 0, (Boolean)this.renderText.getValue() != false ? 10 : 0);
        }
        GlStateManager.popMatrix();
    }

    public String formatText(String string) {
        if (string == null) {
            return null;
        }
        if (this.stripColorFormatting.getValue().equals("OFF")) {
            return string;
        }
        return (this.stripColorFormatting.getValue().equals("ALL") ? this.pattern : this.pattern2).matcher(string).replaceAll("\u00a7r");
    }
}
