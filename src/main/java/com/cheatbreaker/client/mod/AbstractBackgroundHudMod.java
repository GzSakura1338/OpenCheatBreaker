package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.event.WorldClientEvent;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.AnchorPoint;
import java.awt.Color;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public abstract class AbstractBackgroundHudMod extends AbstractHudMod {
    public float boxHeight;
    public float extraWidth = 0.0F;
    public Setting backgroundOptions;
    public Setting showBackground;
    public Setting alwaysCenter;
    public Setting staticBackgroundWidth;
    public Setting textOverflow;
    public Setting showBorder;
    public Setting backgroundWidthPadding;
    public Setting cornerRadius;
    public Setting backgroundWidth;
    public Setting backgroundHeight;
    public Setting borderThickness;
    public float boxWidth;
    public Setting generalOptions;
    public Setting textShadow;
    public Setting formatOptions;
    public String displayText;
    public Setting formatBackground;
    public Setting formatNoBackground;
    public Setting colorOptions;
    public Setting textColor;
    public Setting backgroundColor;
    public Setting borderColor;
    public Setting showBackgroundValue;

    public String getDefaultFormat() {
        return "%VALUE%";
    }

    public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) { try {
        if (this.isRenderHud() && this.getDynamicText() == null) {
            GlStateManager.pushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent7.getScaledResolution());
            this.renderText(this.getStaticText());
            GlStateManager.popMatrix();
        }
    } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }

    public boolean centerByDefault() {
        return false;
    }

    public boolean hasLowContrast() {
        Color color = new Color(this.textColor.getColorValue(), true);
        Color color2 = new Color(this.backgroundColor.getColorValue(), true);
        double luminance = this.getLuminance(color);
        double luminance2 = this.getLuminance(color2);
        return Math.abs(luminance - luminance2) < 0.3;
    }

    public boolean isBoxDrawn() {
        return this.showBackground.getBooleanValue() || this.alwaysCenter.getBooleanValue();
    }

    public Setting getActiveFormat() {
        return this.showBackground.getBooleanValue() ? this.formatBackground : this.formatNoBackground;
    }

    public int getTextColor() {
        return this.textColor.getColorValue();
    }

    public double getLuminance(Color color) {
        return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
    }

    public void renderText(String str) throws java.io.IOException {
        this.displayText = this.formatText(str);
        float stringWidth = this.mc.fontRendererObj.getStringWidth(this.displayText);
        this.boxHeight = this.backgroundHeight.getFloatValue();
        this.boxWidth = !this.staticBackgroundWidth.getBooleanValue()
            ? stringWidth + this.backgroundWidthPadding.getFloatValue() + this.extraWidth
            : this.backgroundWidth.getFloatValue();
        if (this.textOverflow.getValue().equals("Extend Width") && this.staticBackgroundWidth.getBooleanValue()) {
            this.boxWidth = Math.max(this.backgroundWidth.getFloatValue(), stringWidth + this.backgroundWidthPadding.getFloatValue() + this.extraWidth);
        }

        if (this.isBoxDrawn()) {
            this.setHudSize(this.boxWidth, this.boxHeight);
            if (this.showBackground.getBooleanValue()) {
                RenderUtils.drawRoundedRect(0.0F, 0.0F, this.boxWidth, this.boxHeight, this.cornerRadius.getFloatValue(), this.backgroundColor.getColorValue());
                if (this.showBorder.getBooleanValue()) {
                    RenderUtils.drawRoundedBorder(
                        0.0F,
                        0.0F,
                        this.boxWidth,
                        this.boxHeight,
                        this.cornerRadius.getFloatValue(),
                        this.borderThickness.getFloatValue(),
                        this.borderColor.getColorValue()
                    );
                }
            }

            GlStateManager.enableBlend();
            float f = 1.0F;
            if (stringWidth > this.boxWidth - this.backgroundWidthPadding.getFloatValue() - this.extraWidth && this.textOverflow.getValue().equals("Scale Text")) {
                f = (this.boxWidth - this.backgroundWidthPadding.getFloatValue() - this.extraWidth) / stringWidth;
                GL11.glScalef(f, f, 1.0F);
                f = stringWidth / (this.boxWidth - this.backgroundWidthPadding.getFloatValue() - this.extraWidth);
            }

            this.mc
                .fontRendererObj
                .drawString(
                    this.displayText,
                    this.hudWidth / 2.0F * f - this.mc.fontRendererObj.getStringWidth(this.displayText) / 2 + this.extraWidth * f / 2.0F + 0.6F,
                    this.boxHeight / 2.0F * f - 3.49F,
                    this.getTextColor(),
                    this.shouldDrawShadow()
                );
            if (stringWidth > this.boxWidth) {
                GL11.glScalef(f, f, 1.0F);
            }
        } else {
            GlStateManager.enableBlend();
            this.setHudSize(
                this.mc.fontRendererObj.drawString(this.displayText, 0.0F, 0.0F, this.getTextColor(), this.shouldDrawShadow()), this.mc.fontRendererObj.FONT_HEIGHT
            );
        }

        GlStateManager.disableBlend();
    }

    public abstract String getLabelText();

    public boolean shouldDrawShadow() {
        switch (this.textShadow.getStringValue()) {
            case "ON":
                return true;
            case "BG Invert":
                return !this.showBackground.getBooleanValue();
            case "Contrast":
                return this.hasLowContrast();
            case "Auto":
                return this.hasLowContrast() || !this.showBackground.getBooleanValue();
            default:
                return false;
        }
    }

    public void createColorOptions() {
    }

    public BackgroundSizeLimits getSizeLimits() {
        return new BackgroundSizeLimits(10.0F, 13.0F, 24.0F, 40.0F, 56.0F, 80.0F);
    }

    public boolean showBackgroundByDefault() {
        return true;
    }

    public void createGeneralOptions() {
    }

    public void createFormatOptions() {
    }

    public void onWorldClientEvent(WorldClientEvent worldClientEvent) {
        this.showBackgroundValue = this.getActiveFormat();
    }

    public boolean wrapsTextWithoutBackground() {
        return true;
    }

    public String getStaticText() {
        return "";
    }

    public AbstractBackgroundHudMod(String str) throws java.io.IOException {
        super(str);
        this.setAnchorPointAndDefault(AnchorPoint.RIGHT_TOP);
        this.setTranslationAndDefault(0.0F, 0.0F);
        this.setEnabledByDefault(false);
        this.backgroundOptions = new SettingGroup(this, "Background Options").changeValue(true).setCondition(() -> this.hudOnly || this.isRenderHud());
        this.showBackground = new Setting(this, "Show Background")
            .setDescription("Draw a background.")
            .withHierarchyParents(this.backgroundOptions)
            .changeValue(this.showBackgroundByDefault())
            .setOnUpdate(obj -> this.showBackgroundValue = this.getActiveFormat());
        this.alwaysCenter = new Setting(this, "Always Center")
            .setDescription("Force the text to be centered in the mod's placement.")
            .withHierarchyParents(this.backgroundOptions)
            .changeValue(this.centerByDefault())
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.staticBackgroundWidth = new Setting(this, "Static Background Width")
            .setDescription("§2Enabled:§r Background width is set by a slider.\n§4Disabled:§r Background width is set by the text size.")
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withOptionalHierarchyParents(this.showBackground, this.alwaysCenter);
        this.textOverflow = new Setting(this, "Text Overflow")
            .setDescription(
                "What the mod will do if the text overflows from the background\n§bExtend Width:§r Increases the background width.\n§bScale Text:§r Scales the text down.\n§bDo Nothing:§r Does no action."
            )
            .changeValue("Extend Width")
            .setStringValues("Extend Width", "Scale Text", "Do Nothing")
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withHierarchyParents(this.staticBackgroundWidth);
        this.showBorder = new Setting(this, "Show Border")
            .setDescription("Draw a border around the background.")
            .changeValue(false)
            .withHierarchyParents(this.showBackground);
        this.backgroundWidthPadding = new Setting(this, "Background Width Padding")
            .setDescription("Change how spaced the background width is from the text.")
            .withHierarchyParents(this.backgroundOptions)
            .changeValue(6.0F)
            .setRange(0.0F, 10.0F)
            .setUnit("px")
            .setCondition(
                () -> (this.showBackground.getBooleanValue() || this.alwaysCenter.getBooleanValue())
                    && (!this.staticBackgroundWidth.getBooleanValue() || !this.textOverflow.getValue().equals("Do Nothing"))
            )
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.cornerRadius = new Setting(this, "Corner Radius")
            .setDescription("Change the corner radius of the background.")
            .changeValue(0.0F)
            .setRange(0.0F, 10.0F)
            .setUnit("px")
            .withHierarchyParents(this.showBackground)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.backgroundWidth = new Setting(this, "Background Width")
            .setDescription("Change the width of the background.")
            .changeValue(this.getSizeLimits().getDefaultWidth())
            .setRange(this.getSizeLimits().getMinWidth(), this.getSizeLimits().getMaxWidth())
            .setUnit("px")
            .withHierarchyParents(this.staticBackgroundWidth);
        this.backgroundHeight = new Setting(this, "Background Height")
            .setDescription("Change the height of the background.")
            .changeValue(this.getSizeLimits().getDefaultHeight())
            .setRange(this.getSizeLimits().getMinHeight(), this.getSizeLimits().getMaxHeight())
            .setUnit("px")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.staticBackgroundWidth);
        this.borderThickness = new Setting(this, "Border Thickness")
            .setDescription("Change the thickness of the border.")
            .changeValue(1.0F)
            .setRange(0.25F, 5.0F)
            .setUnit("px")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.showBorder);
        this.generalOptions = new SettingGroup(this, "General Options").changeValue(false).setCondition(() -> this.hudOnly || this.isRenderHud());
        this.textShadow = new Setting(this, "Text Shadow")
            .changeValue("Auto")
            .setStringValues("OFF", "ON", "Auto", "BG Invert", "Contrast")
            .withHierarchyParents(this.generalOptions);
        this.createGeneralOptions();
        this.formatOptions = new SettingGroup(this, "Format Options")
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setCondition(() -> this.hudOnly || this.isRenderHud());
        this.createFormatOptions();
        this.formatBackground = new Setting(this, "Format (Background)")
            .changeValue(this.getDefaultFormat())
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withHierarchyParents(this.formatOptions, this.showBackground);
        if (this.wrapsTextWithoutBackground()) {
            this.formatNoBackground = new Setting(this, "Format (No Background)")
                .changeValue("[" + this.getDefaultFormat() + "]")
                .withHierarchyParents(this.formatOptions)
                .withInvertedParents(this.showBackground)
                .setCustomizationLevel(SettingsLevel.ADVANCED);
        } else {
            this.formatNoBackground = new Setting(this, "Format (No Background)")
                .changeValue(this.getDefaultFormat())
                .withHierarchyParents(this.formatOptions)
                .withInvertedParents(this.showBackground)
                .setCustomizationLevel(SettingsLevel.ADVANCED);
        }

        this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true).setCondition(() -> this.hudOnly || this.isRenderHud());
        this.textColor = new Setting(this, "Text Color")
            .setDescription("Sets the color for the text.")
            .withHierarchyParents(this.colorOptions)
            .changeValue(-1)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.createColorOptions();
        this.backgroundColor = new Setting(this, "Background Color")
            .setDescription("Sets the color for the background.")
            .changeValue(1862270976)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withHierarchyParents(this.colorOptions, this.showBackground);
        this.borderColor = new Setting(this, "Border Color")
            .setDescription("Sets the color for the border.")
            .changeValue(-1627389952)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withHierarchyParents(this.colorOptions, this.showBorder);
        this.showBackgroundValue = this.getActiveFormat();
        this.setLabelScale(1.5F);
        this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
        this.addEventHandler(WorldClientEvent.class, this::onWorldClientEvent);
    }

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) { try { try { try { try { try { try { try { try { try {
        String dynamicText = this.getDynamicText();
        if (dynamicText != null && this.shouldRender() && this.isRenderHud()) {
            GlStateManager.pushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
            if (dynamicText.isEmpty()) {
                dynamicText = "";
            }

            this.renderText(dynamicText);
            GlStateManager.popMatrix();
        }
    } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }

    public String formatText(String str) {
        try {
            return this.showBackgroundValue.getStringValue().replaceAll("%LABEL%", this.getLabelText()).replaceAll("%VALUE%", str);
        } catch (NullPointerException e) {
            return "";
        }
    }

    public abstract String getDynamicText();
}
