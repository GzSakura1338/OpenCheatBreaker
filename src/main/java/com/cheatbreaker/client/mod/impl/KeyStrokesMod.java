package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.SimpleEvent17;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.AnchorPoint;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyStrokesMod extends AbstractHudMod {
    public List<Long> leftClicks = new ArrayList<>();
    public List<Long> rightClicks = new ArrayList<>();
    public Setting keyOptions;
    public Setting showMovementKeys;
    public Setting showClicks;
    public Setting showSpacebar;
    public Setting showSneakKey;
    public Setting clickOptions;
    public KeyStrokeKey useKey;
    public Setting leftCps;
    public KeyStrokeKey jumpKey;
    public KeyStrokeKey sneakKey;
    public Setting rightCps;
    public KeyStrokeKey rightKey;
    public Setting cpsBackgroundHeight;
    public Setting cpsColor;
    public KeyStrokeKey leftKey;
    public Setting cpsColorPressed;
    public Setting spacebarOptions;
    public Setting showSpacebarLine;
    public Setting centerSpacebarLine;
    public Setting spacebarLineWidth;
    public Setting spacebarLineHeight;
    public KeyStrokeKey backKey;
    public Setting spacebarHeight;
    public Setting sneakOptions;
    public KeyStrokeKey forwardKey;
    public Setting showSneakKeybindName;
    public Setting sneakBoxHeight;
    public Setting textOptions;
    public Setting textShadow;
    public Setting literalKeys;
    public Setting keyVisual;
    public Setting replaceNamesWithArrows;
    public Setting legacyPosition;
    public KeyStrokeKey attackKey;
    public Setting backgroundOptions;
    public Setting showBackground;
    public Setting showBorder;
    public Setting splitBoxSize;
    public Setting boxSize;
    public Setting boxSizeHeight;
    public Setting gap;
    public Setting borderThickness;
    public Setting cornerRadius;
    public Setting fadeOptions;
    public Setting textFadeTime;
    public Setting backgroundFadeTime;
    public Setting borderFadeTime;
    public Setting colorOptions;
    public Setting textColor;
    public Setting textColorPressed;
    public Setting backgroundColor;
    public Setting backgroundColorPressed;
    public Setting borderColor;
    public Setting borderColorPressed;

    public void lambda$new$0(Object obj) {
        this.rebuildKeys();
    }

    public void rebuildKeys() {
        int resolveKeyCode = this.resolveKeyCode(this.mc.gameSettings.keyBindForward, "Discrete Literal");
        int resolveKeyCode2 = this.resolveKeyCode(this.mc.gameSettings.keyBindLeft, "Discrete Literal");
        int resolveKeyCode3 = this.resolveKeyCode(this.mc.gameSettings.keyBindBack, "Discrete Literal");
        int resolveKeyCode4 = this.resolveKeyCode(this.mc.gameSettings.keyBindRight, "Discrete Literal");
        int resolveKeyCode5 = this.resolveKeyCode(this.mc.gameSettings.keyBindJump, "Discrete Literal");
        int resolveKeyCode6 = this.resolveKeyCode(this.mc.gameSettings.keyBindAttack, "Discrete Literal");
        int resolveKeyCode7 = this.resolveKeyCode(this.mc.gameSettings.keyBindUseItem, "Discrete Literal");
        int resolveKeyCode8 = this.resolveKeyCode(this.mc.gameSettings.keyBindSneak, "Discrete Literal");
        boolean booleanValue = this.replaceNamesWithArrows.getBooleanValue();
        float floatValue = this.borderThickness.getFloatValue();
        if (!this.showBorder.getBooleanValue()) {
            floatValue = 0.0F;
        }

        float floatValue2 = this.boxSize.getFloatValue();
        float f = this.splitBoxSize.getBooleanValue() ? this.boxSizeHeight.getFloatValue() : this.boxSize.getFloatValue();
        String keyLabel = this.getKeyLabel(this.resolveKeyCode(this.mc.gameSettings.keyBindForward, "Discrete"));
        String keyLabel2 = this.getKeyLabel(this.resolveKeyCode(this.mc.gameSettings.keyBindLeft, "Discrete"));
        String keyLabel3 = this.getKeyLabel(this.resolveKeyCode(this.mc.gameSettings.keyBindBack, "Discrete"));
        String keyLabel4 = this.getKeyLabel(this.resolveKeyCode(this.mc.gameSettings.keyBindRight, "Discrete"));
        String keyLabel5 = this.getKeyLabel(this.resolveKeyCode(this.mc.gameSettings.keyBindJump, "Discrete"));
        String keyLabel6 = this.getKeyLabel(this.resolveKeyCode(this.mc.gameSettings.keyBindAttack, "Discrete"));
        String keyLabel7 = this.getKeyLabel(this.resolveKeyCode(this.mc.gameSettings.keyBindUseItem, "Discrete"));
        String str = this.showSneakKeybindName.getBooleanValue()
            ? this.getKeyLabel(this.resolveKeyCode(this.mc.gameSettings.keyBindSneak, "Discrete"))
            : "SNEAK";
        float f2 = this.mc.fontRendererObj.getStringWidth(keyLabel) * this.scale.getFloatValue();
        float f3 = this.mc.fontRendererObj.getStringWidth(keyLabel2) * this.scale.getFloatValue();
        float f4 = this.mc.fontRendererObj.getStringWidth(keyLabel3) * this.scale.getFloatValue();
        float f5 = this.mc.fontRendererObj.getStringWidth(keyLabel4) * this.scale.getFloatValue();
        float f6 = this.mc.fontRendererObj.getStringWidth(keyLabel5) * this.scale.getFloatValue();
        float f7 = this.mc.fontRendererObj.getStringWidth(keyLabel6) * this.scale.getFloatValue();
        float f8 = this.mc.fontRendererObj.getStringWidth(keyLabel7) * this.scale.getFloatValue();
        float f9 = this.mc.fontRendererObj.getStringWidth(str) * this.scale.getFloatValue();
        this.forwardKey = new KeyStrokeKey(booleanValue ? "▲" : (f2 > floatValue2 ? keyLabel.substring(0, 1) : keyLabel), resolveKeyCode, floatValue2, f);
        this.leftKey = new KeyStrokeKey(booleanValue ? "◀" : (f3 > floatValue2 ? keyLabel2.substring(0, 1) : keyLabel2), resolveKeyCode2, floatValue2, f);
        this.backKey = new KeyStrokeKey(booleanValue ? "▼" : (f4 > floatValue2 ? keyLabel3.substring(0, 1) : keyLabel3), resolveKeyCode3, floatValue2, f);
        this.rightKey = new KeyStrokeKey(booleanValue ? "▶" : (f5 > floatValue2 ? keyLabel4.substring(0, 1) : keyLabel4), resolveKeyCode4, floatValue2, f);
        float f10 = (this.leftKey.getWidth() + this.backKey.getWidth() + this.rightKey.getWidth() + this.gap.getFloatValue() + floatValue) / 2.0F;
        float f11 = this.leftKey.getWidth() + this.backKey.getWidth() + this.rightKey.getWidth() + 2.0F * this.gap.getFloatValue() + floatValue * 2.0F;
        this.attackKey = new KeyStrokeKey(f7 > f10 ? keyLabel6.substring(0, 1) : keyLabel6, resolveKeyCode6, f10, f);
        this.useKey = new KeyStrokeKey(f8 > f10 ? keyLabel7.substring(0, 1) : keyLabel7, resolveKeyCode7, f10, f);
        this.jumpKey = new KeyStrokeKey(f6 > f11 ? keyLabel5.substring(0, 1) : keyLabel5, resolveKeyCode5, f11, f * this.spacebarHeight.getFloatValue() / 100.0F);
        this.sneakKey = new KeyStrokeKey(f9 > f11 ? str.substring(0, 1) : str, resolveKeyCode8, f11, f * this.sneakBoxHeight.getFloatValue() / 100.0F);
    }

    public String getKeyLabel(int n) {
        if (n < 0) {
            String buttonName = Mouse.getButtonName(n + 100);
            if (buttonName != null) {
                switch (buttonName.toLowerCase()) {
                    case "button0":
                        return "LMB";
                    case "button1":
                        return "RMB";
                    case "button2":
                        return "MMB";
                    case "button3":
                        return "FMB";
                    case "button4":
                        return "BMB";
                }
            }

            return buttonName;
        } else {
            if (this.literalKeys.getBooleanValue()) {
                switch (n) {
                    case 12:
                    case 74:
                        return "-";
                    case 13:
                        return "=";
                    case 26:
                        return "[";
                    case 27:
                        return "]";
                    case 39:
                        return ";";
                    case 40:
                        return "'";
                    case 41:
                        return "~";
                    case 43:
                        return "\\";
                    case 51:
                        return ",";
                    case 52:
                        return ".";
                    case 53:
                    case 181:
                        return "/";
                    case 55:
                        return "*";
                    case 78:
                        return "+";
                    case 200:
                        return "▲";
                    case 203:
                        return "◀";
                    case 205:
                        return "▶";
                    case 208:
                        return "▼";
                }
            }

            return Keyboard.getKeyName(n);
        }
    }

    public void lambda$new$1(Object obj) {
        this.rebuildKeys();
    }

    public void lambda$new$2(Object obj) {
        this.rebuildKeys();
    }

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        if (this.shouldRender()) {
            GlStateManager.pushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
            this.leftClicks.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
            this.rightClicks.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
            float f = 0.0F;
            float f2 = 0.0F;
            float floatValue = this.gap.getFloatValue();
            float floatValue2 = this.borderThickness.getFloatValue();
            float floatValue3 = this.cpsBackgroundHeight.getFloatValue();
            if (!this.showBorder.getBooleanValue()) {
                floatValue2 = 0.0F;
            }

            if (this.showMovementKeys.getBooleanValue()) {
                this.forwardKey
                    .render(
                        this.leftKey.getWidth() + floatValue + floatValue2,
                        0.0F,
                        this.textColor.getColorValue(),
                        this.textColorPressed.getColorValue(),
                        this.backgroundColor.getColorValue(),
                        this.backgroundColorPressed.getColorValue(),
                        this.borderColor.getColorValue(),
                        this.borderColorPressed.getColorValue(),
                        this.textShadow.getBooleanValue()
                    );
                this.leftKey
                    .render(
                        0.0F,
                        this.forwardKey.getHeight() + floatValue + floatValue2,
                        this.textColor.getColorValue(),
                        this.textColorPressed.getColorValue(),
                        this.backgroundColor.getColorValue(),
                        this.backgroundColorPressed.getColorValue(),
                        this.borderColor.getColorValue(),
                        this.borderColorPressed.getColorValue(),
                        this.textShadow.getBooleanValue()
                    );
                this.backKey
                    .render(
                        this.leftKey.getWidth() + floatValue + floatValue2,
                        this.forwardKey.getHeight() + floatValue + floatValue2,
                        this.textColor.getColorValue(),
                        this.textColorPressed.getColorValue(),
                        this.backgroundColor.getColorValue(),
                        this.backgroundColorPressed.getColorValue(),
                        this.borderColor.getColorValue(),
                        this.borderColorPressed.getColorValue(),
                        this.textShadow.getBooleanValue()
                    );
                this.rightKey
                    .render(
                        this.leftKey.getWidth() + this.backKey.getWidth() + 2.0F * floatValue + floatValue2 * 2.0F,
                        this.forwardKey.getHeight() + floatValue + floatValue2,
                        this.textColor.getColorValue(),
                        this.textColorPressed.getColorValue(),
                        this.backgroundColor.getColorValue(),
                        this.backgroundColorPressed.getColorValue(),
                        this.borderColor.getColorValue(),
                        this.borderColorPressed.getColorValue(),
                        this.textShadow.getBooleanValue()
                    );
                f = this.leftKey.getWidth() + this.backKey.getWidth() + this.rightKey.getWidth() + 2.0F * floatValue + floatValue2;
                f2 += this.forwardKey.getHeight() + 2.0F * floatValue + this.backKey.getHeight();
            }

            if (this.showClicks.getBooleanValue()) {
                this.attackKey
                    .render(
                        0.0F,
                        f2 + floatValue2 * 2.0F,
                        this.textColor.getColorValue(),
                        this.textColorPressed.getColorValue(),
                        this.backgroundColor.getColorValue(),
                        this.backgroundColorPressed.getColorValue(),
                        this.borderColor.getColorValue(),
                        this.borderColorPressed.getColorValue(),
                        this.textShadow.getBooleanValue()
                    );
                this.useKey
                    .render(
                        this.attackKey.getWidth() + floatValue + floatValue2,
                        f2 + floatValue2 * 2.0F,
                        this.textColor.getColorValue(),
                        this.textColorPressed.getColorValue(),
                        this.backgroundColor.getColorValue(),
                        this.backgroundColorPressed.getColorValue(),
                        this.borderColor.getColorValue(),
                        this.borderColorPressed.getColorValue(),
                        this.textShadow.getBooleanValue()
                    );
                f = this.attackKey.getWidth() + this.useKey.getWidth() + floatValue + floatValue2;
                f2 += this.useKey.getHeight() + floatValue + floatValue2;
            }

            if (this.showSpacebar.getBooleanValue()) {
                this.jumpKey
                    .render(
                        0.0F,
                        f2 + floatValue2 * 2.0F,
                        this.textColor.getColorValue(),
                        this.textColorPressed.getColorValue(),
                        this.backgroundColor.getColorValue(),
                        this.backgroundColorPressed.getColorValue(),
                        this.borderColor.getColorValue(),
                        this.borderColorPressed.getColorValue(),
                        this.textShadow.getBooleanValue()
                    );
                f = this.jumpKey.getWidth();
                f2 += this.jumpKey.getHeight() + floatValue + floatValue2;
            }

            if (this.showSneakKey.getBooleanValue()) {
                this.sneakKey
                    .render(
                        0.0F,
                        f2 + floatValue2 * 2.0F,
                        this.textColor.getColorValue(),
                        this.textColorPressed.getColorValue(),
                        this.backgroundColor.getColorValue(),
                        this.backgroundColorPressed.getColorValue(),
                        this.borderColor.getColorValue(),
                        this.borderColorPressed.getColorValue(),
                        this.textShadow.getBooleanValue()
                    );
                f = this.attackKey.getWidth() + this.useKey.getWidth() + floatValue + floatValue2;
                f2 += this.sneakKey.getHeight() + floatValue + floatValue2;
            }

            if (this.leftCps.getValue().equals("Separate")) {
                String str = this.leftClicks.size() + " CPS";
                if (this.showBackground.getBooleanValue()) {
                    RenderUtils.drawRoundedRect(
                        0.0F,
                        f2 + floatValue2 * 2.0F,
                        this.leftKey.getWidth() + this.backKey.getWidth() + this.rightKey.getWidth() + 2.0F * floatValue + floatValue2 * 2.0F,
                        f2 + floatValue2 * 2.0F + floatValue3,
                        this.cornerRadius.getFloatValue(),
                        this.backgroundColor.getColorValue()
                    );
                }

                if (this.showBorder.getBooleanValue()) {
                    RenderUtils.drawRoundedBorder(
                        -floatValue2 + 1.0F,
                        f2 + floatValue2 + 1.0F,
                        this.leftKey.getWidth() + this.backKey.getWidth() + this.rightKey.getWidth() + floatValue + floatValue2 * 2.0F + 1.0F,
                        f2 + floatValue2 * 2.0F + floatValue3,
                        this.cornerRadius.getFloatValue(),
                        floatValue2,
                        this.borderColor.getColorValue()
                    );
                }

                GlStateManager.enableBlend();
                this.mc
                    .fontRendererObj
                    .drawString(
                        str,
                        this.hudWidth / 2.0F - this.mc.fontRendererObj.getStringWidth(str) / 2 - 0.1F,
                        f2 + floatValue2 * 2.0F + (floatValue3 / 2.0F - 3.49F),
                        this.cpsColor.getColorValue(),
                        this.textShadow.getBooleanValue()
                    );
                f = this.attackKey.getWidth() + this.useKey.getWidth() + floatValue + floatValue2;
                f2 += floatValue3 + floatValue + floatValue2;
            }

            if (this.rightCps.getValue().equals("Separate")) {
                String str = this.rightClicks.size() + " RCPS";
                if (this.showBackground.getBooleanValue()) {
                    RenderUtils.drawRoundedRect(
                        0.0F,
                        f2 + floatValue2 * 2.0F,
                        this.leftKey.getWidth() + this.backKey.getWidth() + this.rightKey.getWidth() + 2.0F * floatValue + floatValue2 * 2.0F,
                        f2 + floatValue2 * 2.0F + floatValue3,
                        this.cornerRadius.getFloatValue(),
                        this.backgroundColor.getColorValue()
                    );
                }

                if (this.showBorder.getBooleanValue()) {
                    RenderUtils.drawRoundedBorder(
                        -floatValue2 + 1.0F,
                        f2 + floatValue2 + 1.0F,
                        this.leftKey.getWidth() + this.backKey.getWidth() + this.rightKey.getWidth() + floatValue + floatValue2 * 2.0F + 1.0F,
                        f2 + floatValue2 * 2.0F + floatValue3,
                        this.cornerRadius.getFloatValue(),
                        floatValue2,
                        this.borderColor.getColorValue()
                    );
                }

                GlStateManager.enableBlend();
                this.mc
                    .fontRendererObj
                    .drawString(
                        str,
                        this.hudWidth / 2.0F - this.mc.fontRendererObj.getStringWidth(str) / 2 - 0.1F,
                        f2 + floatValue2 * 2.0F + (floatValue3 / 2.0F - 3.49F),
                        this.cpsColor.getColorValue(),
                        this.textShadow.getBooleanValue()
                    );
                f = this.attackKey.getWidth() + this.useKey.getWidth() + floatValue + floatValue2;
                f2 += floatValue3 + floatValue + floatValue2;
            }

            GlStateManager.disableBlend();
            this.setHudSize(f, f2 - floatValue + floatValue2);
            GlStateManager.popMatrix();
        }
    }

    public void lambda$new$3(Object obj) {
        this.rebuildKeys();
    }

    public void lambda$new$4(Object obj) {
        this.rebuildKeys();
    }

    public void lambda$new$5(Object obj) {
        this.rebuildKeys();
    }

    public KeyStrokesMod() throws java.io.IOException {
        super("Key Strokes");
        this.setAnchorPointAndDefault(AnchorPoint.LEFT_TOP);
        this.setTranslationAndDefault(0.0F, 5.0F);
        this.setEnabledByDefault(false);
        this.keyOptions = new SettingGroup(this, "Key Options").changeValue(false);
        this.showMovementKeys = new Setting(this, "Show movement keys").withHierarchyParents(this.keyOptions).changeValue(true);
        this.showClicks = new Setting(this, "Show clicks").withHierarchyParents(this.keyOptions).changeValue(true);
        this.showSpacebar = new Setting(this, "Show spacebar").withHierarchyParents(this.keyOptions).changeValue(false);
        this.showSneakKey = new Setting(this, "Show sneak key").withHierarchyParents(this.keyOptions).changeValue(false);
        this.clickOptions = new SettingGroup(this, "Click Options")
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.showClicks);
        this.leftCps = new Setting(this, "Left CPS")
            .withHierarchyParents(this.clickOptions)
            .changeValue("OFF")
            .setStringValues("OFF", "Replace Clicks", "With Clicks", "Separate")
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.rightCps = new Setting(this, "Right CPS")
            .withHierarchyParents(this.clickOptions)
            .changeValue("OFF")
            .setStringValues("OFF", "Replace Clicks", "With Clicks", "Separate")
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.cpsBackgroundHeight = new Setting(this, "CPS Background Height")
            .withHierarchyParents(this.clickOptions)
            .changeValue(13.0F)
            .setRange(10.0F, 24.0F)
            .setUnit("px")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setCondition(() -> this.leftCps.getValue().equals("Separate") || this.rightCps.getValue().equals("Separate"));
        this.cpsColor = new Setting(this, "CPS Color")
            .withHierarchyParents(this.clickOptions)
            .changeValue(-1)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCondition(() -> this.leftCps.getValue().equals("With Clicks") || this.rightCps.getValue().equals("With Clicks"));
        this.cpsColorPressed = new Setting(this, "CPS Color (Pressed)")
            .withHierarchyParents(this.clickOptions)
            .changeValue(-16777216)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCondition(() -> this.leftCps.getValue().equals("With Clicks") || this.rightCps.getValue().equals("With Clicks"));
        this.spacebarOptions = new SettingGroup(this, "Spacebar Options")
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.showSpacebar);
        this.showSpacebarLine = new Setting(this, "Show Spacebar Line")
            .withHierarchyParents(this.spacebarOptions)
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.centerSpacebarLine = new Setting(this, "Center Spacebar Line")
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.showSpacebarLine);
        this.spacebarLineWidth = new Setting(this, "Spacebar Line Width")
            .changeValue(4.0F)
            .setRange(1.0F, 8.0F)
            .setUnit("px")
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withHierarchyParents(this.showSpacebarLine);
        this.spacebarLineHeight = new Setting(this, "Spacebar Line Height")
            .changeValue(1.0F)
            .setRange(0.5F, 2.0F)
            .setUnit("px")
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withHierarchyParents(this.showSpacebarLine);
        this.spacebarHeight = new Setting(this, "Spacebar Height")
            .withHierarchyParents(this.spacebarOptions)
            .changeValue(50.0F)
            .setRange(0.0F, 100.0F)
            .setUnit("%")
            .setOnUpdate(this::lambda$new$10)
            .setCustomizationLevel(SettingsLevel.ADVANCED);
        this.sneakOptions = new SettingGroup(this, "Sneak Options")
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.showSneakKey);
        this.showSneakKeybindName = new Setting(this, "Show Sneak Keybind Name")
            .withHierarchyParents(this.sneakOptions)
            .changeValue(false)
            .setOnUpdate(this::lambda$new$11)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.sneakBoxHeight = new Setting(this, "Sneak Box Height")
            .withHierarchyParents(this.sneakOptions)
            .changeValue(72.0F)
            .setRange(45.0F, 100.0F)
            .setUnit("%")
            .setOnUpdate(this::lambda$new$5)
            .setCustomizationLevel(SettingsLevel.ADVANCED);
        this.textOptions = new SettingGroup(this, "Text Options").changeValue(false).setCustomizationLevel(SettingsLevel.MEDIUM);
        this.textShadow = new Setting(this, "Text Shadow").withHierarchyParents(this.textOptions).changeValue(false).setCustomizationLevel(SettingsLevel.MEDIUM);
        this.literalKeys = new Setting(this, "Literal Keys")
            .withHierarchyParents(this.textOptions)
            .changeValue(false)
            .setOnUpdate(this::lambda$new$3)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.keyVisual = new Setting(this, "Key Visual")
            .withHierarchyParents(this.textOptions)
            .changeValue("Bound Literal")
            .setStringValues("Bound Literal", "Discrete Visual", "Discrete Literal")
            .setOnUpdate(this::lambda$new$2)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.replaceNamesWithArrows = new Setting(this, "Replace names with arrows")
            .changeValue(false)
            .setOnUpdate(this::lambda$new$14)
            .withParents(this.textOptions, this.showMovementKeys);
        this.legacyPosition = new Setting(this, "Legacy Position")
            .withHierarchyParents(this.textOptions)
            .withOptionalParents(this.showMovementKeys, this.showClicks)
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.backgroundOptions = new SettingGroup(this, "Background Options").changeValue(false).setCustomizationLevel(SettingsLevel.MEDIUM);
        this.showBackground = new Setting(this, "Show Background")
            .withHierarchyParents(this.backgroundOptions)
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.showBorder = new Setting(this, "Show Border")
            .withHierarchyParents(this.backgroundOptions)
            .changeValue(false)
            .setOnUpdate(this::lambda$new$9)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.splitBoxSize = new Setting(this, "Split Box Size")
            .withHierarchyParents(this.backgroundOptions)
            .changeValue(false)
            .setOnUpdate(this::lambda$new$0)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.boxSize = new Setting(this, "Box size")
            .withHierarchyParents(this.backgroundOptions)
            .changeValue(18.0F)
            .setRange(10.0F, 32.0F)
            .setUnit("px")
            .setOnUpdate(this::lambda$new$8);
        this.boxSizeHeight = new Setting(this, "Box size height")
            .withHierarchyParents(this.backgroundOptions)
            .changeValue(18.0F)
            .setRange(10.0F, 32.0F)
            .setUnit("px")
            .setOnUpdate(this::lambda$new$4)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.splitBoxSize);
        this.gap = new Setting(this, "Gap")
            .withHierarchyParents(this.backgroundOptions)
            .changeValue(1.0F)
            .setRange(0.0F, 4.0F)
            .setUnit("px")
            .setOnUpdate(this::lambda$new$1)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.borderThickness = new Setting(this, "Border Thickness")
            .withParents(this.backgroundOptions, this.showBorder)
            .changeValue(1.0F)
            .setRange(0.0F, 3.0F)
            .setUnit("px")
            .setOnUpdate(this::lambda$new$12)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.cornerRadius = new Setting(this, "Corner Radius")
            .setDescription("Change the corner radius of the background.")
            .changeValue(0.0F)
            .setRange(0.0F, 10.0F)
            .setUnit("px")
            .withHierarchyParents(this.showBackground);
        this.fadeOptions = new SettingGroup(this, "Fade Options").changeValue(false).setCustomizationLevel(SettingsLevel.MEDIUM);
        this.textFadeTime = new Setting(this, "Text Fade Time")
            .withHierarchyParents(this.fadeOptions)
            .changeValue(0.0F)
            .setRange(0.0F, 200.0F)
            .setUnit("ms")
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.backgroundFadeTime = new Setting(this, "Background Fade Time")
            .withParents(this.fadeOptions, this.showBackground)
            .changeValue(75.0F)
            .setRange(0.0F, 200.0F)
            .setUnit("ms")
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.borderFadeTime = new Setting(this, "Border Fade Time")
            .withHierarchyParents(this.fadeOptions, this.showBorder)
            .changeValue(100.0F)
            .setRange(0.0F, 200.0F)
            .setUnit("ms")
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true);
        this.textColor = new Setting(this, "Text Color").withHierarchyParents(this.colorOptions).changeValue(-1).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.textColorPressed = new Setting(this, "Text Color (Pressed)")
            .withHierarchyParents(this.colorOptions)
            .changeValue(-16777216)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.backgroundColor = new Setting(this, "Background Color")
            .withParents(this.colorOptions, this.showBackground)
            .changeValue(1862270976)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.backgroundColorPressed = new Setting(this, "Background Color (Pressed)")
            .withParents(this.colorOptions, this.showBackground)
            .changeValue(1879048191)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.borderColor = new Setting(this, "Border Color")
            .withParents(this.colorOptions, this.showBorder)
            .changeValue(-1627389952)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.borderColorPressed = new Setting(this, "Border Color (Pressed)")
            .withHierarchyParents(this.colorOptions, this.showBorder)
            .changeValue(-1610612737)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.rebuildKeys();
        this.setIcon(new ResourceLocation("client/icons/mods/wasd.png"), 55, 37);
        this.setDescription("Displays when your movement keys, mouse or spacebar is pressed.");
        this.setAliases("Keystrokes");
        this.setAuthors("Fyu");
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
        this.addEventHandler(IntEvent1.class, this::onIntEvent1);
        this.addEventHandler(SimpleEvent17.class, this::lambda$new$7);
    }

    public void lambda$new$7(SimpleEvent17 simpleEvent17) {
        this.rebuildKeys();
    }

    public void lambda$new$8(Object obj) {
        this.rebuildKeys();
    }

    public void lambda$new$9(Object obj) {
        this.rebuildKeys();
    }

    public void lambda$new$10(Object obj) {
        this.rebuildKeys();
    }

    public void lambda$new$11(Object obj) {
        this.rebuildKeys();
    }

    public int resolveKeyCode(KeyBinding keyBinding, String str) {
        return this.keyVisual.getStringValue().contains(str) ? keyBinding.getKeyCodeDefault() : keyBinding.getKeyCode();
    }

    public void lambda$new$12(Object obj) {
        this.rebuildKeys();
    }

    public void lambda$new$14(Object obj) {
        this.rebuildKeys();
    }

    public void onIntEvent1(IntEvent1 intEvent1) {
        if (intEvent1.getEventButton() == 0) {
            this.leftClicks.add(System.currentTimeMillis());
        }

        if (intEvent1.getEventButton() == 1) {
            this.rightClicks.add(System.currentTimeMillis());
        }
    }
}
