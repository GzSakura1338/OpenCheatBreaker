package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.event.SimpleEvent13;
import com.cheatbreaker.client.event.SimpleEvent26;
import com.cheatbreaker.client.event.SimpleEvent29;
import com.cheatbreaker.client.event.SimpleEvent38;
import com.cheatbreaker.client.event.SimpleEvent42;
import com.cheatbreaker.client.event.SimpleEvent44;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.HudAlignment;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.screen.ModSettingsScreen;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.ui.screen.ProfileNameScreen;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.optifine.GlStateManager;

public class PotionEffectsMod extends AbstractHudMod {
    public ResourceLocation inventoryPng = new ResourceLocation("textures/gui/container/inventory.png");
    public Integer[] amplifierOptions = new Integer[]{
        Potion.moveSpeed.id,
        Potion.moveSlowdown.id,
        Potion.digSpeed.id,
        Potion.digSlowdown.id,
        Potion.damageBoost.id,
        Potion.jump.id,
        Potion.confusion.id,
        Potion.regeneration.id,
        Potion.resistance.id,
        Potion.fireResistance.id,
        Potion.waterBreathing.id,
        Potion.invisibility.id,
        Potion.blindness.id,
        Potion.nightVision.id,
        Potion.hunger.id,
        Potion.weakness.id,
        Potion.poison.id,
        Potion.wither.id,
        Potion.absorption.id
    };
    public int blinkCounter = 0;
    public final int unusedZero = 0;
    public List<PotionEffect> activeEffects = new ArrayList<>();
    public List<PotionEffect> previewEffects = new ArrayList<>();
    public List<PotionEffect> displayedEffects = new ArrayList<>();
    public Setting componentOptions;
    public Setting icon;
    public Setting effectName;
    public Setting effectNameText;
    public Setting effectAmplifierText;
    public static Map<Integer, Integer> defaultColors = new Builder<Integer, Integer>()
        .put(1, -11141121)
        .put(2, -10851199)
        .put(3, -2506685)
        .put(4, -11910633)
        .put(5, -7134173)
        .put(6, -515037)
        .put(7, -12383735)
        .put(8, -1)
        .put(9, -11199158)
        .put(10, -3318613)
        .put(11, -11286957)
        .put(12, -1795526)
        .put(13, -13741415)
        .put(14, -8420462)
        .put(15, -14737629)
        .put(16, -14737503)
        .put(17, -10979757)
        .put(18, -12038840)
        .put(19, -11627727)
        .put(20, -13293017)
        .put(21, -2222326)
        .put(22, -14331227)
        .put(23, -474845)
        .build();
    public Setting showRomanNumerals;
    public Setting showLevel1;
    public Setting maxRomanNumeral;
    public Setting duration;
    public Setting compact;
    public Setting generalOptions;
    public Setting showPotionInfoInInventory;
    public Setting textShadow;
    public Setting effectSpacing;
    public Setting sortingOptions;
    public Setting sortingMethod;
    public Setting reverseSorting;
    public Setting blinkOptions;
    public Setting blink;
    public Setting makeNameBlink;
    public static Map<Integer, Integer> potionColors = new Builder<Integer, Integer>()
        .put(1, -8605754)
        .put(2, -10851199)
        .put(3, -2506685)
        .put(4, -11910633)
        .put(5, -7134173)
        .put(6, -515037)
        .put(7, -12383735)
        .put(8, -8887657)
        .put(9, -11199158)
        .put(10, -3318613)
        .put(11, -6732486)
        .put(12, -1795526)
        .put(13, -13741415)
        .put(14, -8420462)
        .put(15, -14737629)
        .put(16, -14737503)
        .put(17, -10979757)
        .put(18, -12038840)
        .put(19, -11627727)
        .put(20, -13293017)
        .put(21, -492253)
        .put(22, -14331227)
        .put(23, -515037)
        .build();
    public Setting makeIconBlink;
    public Setting makeDurationBlink;
    public Setting blinkDuration;
    public Setting blinkSpeed;
    public Setting excludingOptions;
    public static Map<Integer, Integer> colorCodeColors = new Builder<Integer, Integer>()
        .put(1, -11141121)
        .put(2, -11184811)
        .put(3, -171)
        .put(4, -11184811)
        .put(5, -5636096)
        .put(6, -43691)
        .put(7, -5636096)
        .put(8, -1)
        .put(9, -5635926)
        .put(10, -43521)
        .put(11, -5592406)
        .put(12, -22016)
        .put(13, -11184641)
        .put(14, -5592406)
        .put(15, -16777216)
        .put(16, -16777046)
        .put(17, -16733696)
        .put(18, -11184811)
        .put(19, -11141291)
        .put(20, -16777216)
        .put(21, -43691)
        .put(22, -171)
        .put(23, -171)
        .build();
    public PotionEffectsMod.ExclusionSettings permanent;
    public PotionEffectsMod.ExclusionSettings duration2;
    public PotionEffectsMod.ExclusionSettings amplifier;
    public Setting harmfulEffects;
    public Setting excludeSpecificEffects;
    public Setting colorOptions;
    public Setting overrideNameColorWithPackFormat;
    public Setting colorNameBasedOn;
    public static Map<Integer, Integer> vibrantColors = new Builder<Integer, Integer>()
        .put(1, -7405569)
        .put(2, -10528630)
        .put(3, -10240)
        .put(4, -9728589)
        .put(5, -5177344)
        .put(6, -961967)
        .put(7, -8845049)
        .put(8, -2097153)
        .put(9, -1)
        .put(10, -36483)
        .put(11, -11511440)
        .put(12, -24259)
        .put(13, -7229441)
        .put(14, -5592406)
        .put(15, -7975034)
        .put(16, -14263332)
        .put(17, -11818424)
        .put(18, -9474163)
        .put(19, -14689758)
        .put(20, -5276243)
        .put(21, -511965)
        .put(22, -7866)
        .put(23, -103)
        .build();
    public Setting colorDurationBasedOn;
    public Setting colorType;
    public Setting nameColor;
    public Setting durationColor;

    public void addPreviewEffects() {
        this.previewEffects.add(new PotionEffect(Potion.moveSpeed.id, 1200, 3));
        this.previewEffects.add(new PotionEffect(Potion.damageBoost.id, 30, 3));
    }

    public void onSimpleEvent13(SimpleEvent13 simpleEvent13) {
        if (!this.showPotionInfoInInventory.getBooleanValue()) {
            simpleEvent13.setCancelled(true);
        }
    }

    public void sortEffects(List<PotionEffect> list) {
        switch (this.sortingMethod.getStringValue()) {
            case "Alphabetical":
                list.sort(Comparator.comparing(potionEffect -> I18n.format(potionEffect.getEffectName())));
                break;
            case "Duration":
                list.sort(Comparator.comparingInt(PotionEffect::getDuration));
                break;
            case "Amplifier":
                list.sort(Comparator.comparingInt(PotionEffect::getAmplifier));
                Collections.reverse(list);
        }

        if (this.reverseSorting.getBooleanValue()) {
            Collections.reverse(list);
        }
    }

    public String stripColors(String str) {
        return str == null ? null : Pattern.compile("(?i)§[0-9A-F]").matcher(str).replaceAll("§r");
    }

    public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) {
        if (this.isRenderHud()) {
            if (this.activeEffects.isEmpty()) {
                GlStateManager.pushMatrix();
                this.applyHudTransformForSize(scaledResolutionEvent7.getScaledResolution());
                this.renderEffects(this.displayedEffects);
                GlStateManager.popMatrix();
            }
        }
    }

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        if (this.shouldRender()) {
            GlStateManager.pushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
            if (!this.activeEffects.isEmpty()) {
                this.renderEffects(this.activeEffects);
            }

            GlStateManager.popMatrix();
        }
    }

    public void refreshEffects() {
        if (this.mc.thePlayer != null) {
            this.activeEffects = new ArrayList<>(this.mc.thePlayer.getActivePotionEffects());
            this.displayedEffects = this.activeEffects.isEmpty() ? this.previewEffects : this.activeEffects;
            this.sortEffects(this.displayedEffects);
        }
    }

    public PotionEffectsMod() throws java.io.IOException {
        super("Potion Effects");
        this.setEnabledByDefault(false);
        this.setAnchorPointAndDefault(AnchorPoint.LEFT_MIDDLE);
        this.componentOptions = new SettingGroup(this, "Component Options").changeValue(false);
        this.icon = new Setting(this, "Icon").withHierarchyParents(this.componentOptions).changeValue(true).setCustomizationLevel(SettingsLevel.MEDIUM);
        this.effectName = new Setting(this, "Effect Name").withHierarchyParents(this.componentOptions).changeValue(true);
        this.effectNameText = new Setting(this, "Effect Name Text")
            .withHierarchyParents(this.componentOptions)
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.effectName);
        this.effectAmplifierText = new Setting(this, "Effect Amplifier Text")
            .withHierarchyParents(this.componentOptions)
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.effectName);
        this.showRomanNumerals = new Setting(this, "Show Roman Numerals")
            .withHierarchyParents(this.componentOptions)
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.effectAmplifierText);
        this.showLevel1 = new Setting(this, "Show Level 1")
            .withHierarchyParents(this.componentOptions)
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withHierarchyParents(this.effectAmplifierText);
        this.maxRomanNumeral = new Setting(this, "Max Roman Numeral")
            .withHierarchyParents(this.componentOptions)
            .changeValue(10)
            .setRange(1, 255)
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withHierarchyParents(this.showRomanNumerals);
        this.duration = new Setting(this, "Duration").withHierarchyParents(this.componentOptions).changeValue(true).setCustomizationLevel(SettingsLevel.MEDIUM);
        this.compact = new Setting(this, "Compact").withHierarchyParents(this.componentOptions).changeValue(false).setCustomizationLevel(SettingsLevel.MEDIUM);
        this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
        this.showPotionInfoInInventory = new Setting(this, "Show Potion info in inventory").withHierarchyParents(this.generalOptions).changeValue(false);
        this.textShadow = new Setting(this, "Text Shadow")
            .withHierarchyParents(this.generalOptions)
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setCondition(() -> this.effectName.getBooleanValue() || this.duration.getBooleanValue());
        this.effectSpacing = new Setting(this, "Effect Spacing")
            .withHierarchyParents(this.generalOptions)
            .changeValue(4.0F)
            .setRange(0.0F, 15.0F)
            .setUnit("px")
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.sortingOptions = new SettingGroup(this, "Sorting Options").changeValue(false).setCustomizationLevel(SettingsLevel.MEDIUM);
        this.sortingMethod = new Setting(this, "Sorting Method")
            .withHierarchyParents(this.sortingOptions)
            .changeValue("Vanilla")
            .setStringValues("Vanilla", "Alphabetical", "Duration", "Amplifier")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setOnUpdate(obj -> this.sortEffects(this.displayedEffects));
        this.reverseSorting = new Setting(this, "Reverse Sorting")
            .withHierarchyParents(this.sortingOptions)
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setOnUpdate(obj -> this.sortEffects(this.displayedEffects));
        this.blinkOptions = new SettingGroup(this, "Blink Options").changeValue(false);
        this.blink = new Setting(this, "Blink").withHierarchyParents(this.blinkOptions).changeValue(true);
        this.makeNameBlink = new Setting(this, "Make Effect Name Text Blink")
            .withHierarchyParents(this.blinkOptions)
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setCondition(this.effectName::getBooleanValue)
            .withHierarchyParents(this.blink);
        this.makeIconBlink = new Setting(this, "Make Effect Icon Blink")
            .withHierarchyParents(this.blinkOptions)
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setCondition(this.icon::getBooleanValue)
            .withHierarchyParents(this.blink);
        this.makeDurationBlink = new Setting(this, "Make Duration Text Blink")
            .withHierarchyParents(this.blinkOptions)
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setCondition(this.duration::getBooleanValue)
            .withHierarchyParents(this.blink);
        this.blinkDuration = new Setting(this, "Blink Duration")
            .withHierarchyParents(this.blinkOptions)
            .changeValue(10.0F)
            .setRange(2.0F, 60.0F)
            .setUnit("s")
            .withHierarchyParents(this.blink);
        this.blinkSpeed = new Setting(this, "Blink Speed")
            .withHierarchyParents(this.blinkOptions)
            .changeValue(1.0F)
            .setRange(0.2F, 3.5F)
            .setUnit("s")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.blink);
        this.excludingOptions = new SettingGroup(this, "Excluding Options").changeValue(true).setCustomizationLevel(SettingsLevel.MEDIUM);
        this.permanent = new PotionEffectsMod.ExclusionSettings("Permanent");
        this.duration2 = new PotionEffectsMod.ExclusionSettings("Duration", 30.0F, 2.0F, 90.0F, "s");
        this.amplifier = new PotionEffectsMod.ExclusionSettings("Amplifier", 10, 0, 20, "");
        this.harmfulEffects = new Setting(this, "Exclude Helpful/Harmful Effects")
            .withHierarchyParents(this.excludingOptions)
            .changeValue("OFF")
            .setStringValues("OFF", "Only Helpful", "Only Harmful")
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.excludeSpecificEffects = new Setting(this, "Exclude Specific Effects")
            .withHierarchyParents(this.excludingOptions)
            .changeValue(new ArrayList())
            .setAcceptedValues(this.amplifierOptions)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true).withOptionalParents(this.effectName, this.duration);
        this.overrideNameColorWithPackFormat = new Setting(this, "Override Name Color With Pack Format")
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withParents(this.effectNameText, this.colorOptions);
        this.colorNameBasedOn = new Setting(this, "Color Name Based On")
            .changeValue("Static")
            .setStringValues("Static", "Effect", "Duration")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withParents(this.effectName, this.colorOptions);
        this.colorDurationBasedOn = new Setting(this, "Color Duration Based On")
            .changeValue("Static")
            .setStringValues("Static", "Effect", "Duration")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withParents(this.duration, this.colorOptions);
        this.colorType = new Setting(this, "Color Type")
            .withHierarchyParents(this.colorOptions)
            .changeValue("Default")
            .setStringValues("Default", "Potion Colors", "Color Codes", "Good/Bad", "Vibrant")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setCondition(() -> this.colorDurationBasedOn.getValue().equals("Effect") || this.colorNameBasedOn.getValue().equals("Effect"));
        this.nameColor = new Setting(this, "Name Color")
            .changeValue(-1)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCondition(() -> this.colorNameBasedOn.getValue().equals("Static"))
            .withParents(this.effectName, this.colorOptions);
        this.durationColor = new Setting(this, "Duration Color")
            .changeValue(-1)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCondition(() -> this.colorDurationBasedOn.getValue().equals("Static"))
            .withParents(this.duration, this.colorOptions);
        this.setIcon(new ResourceLocation("client/icons/mods/speed_icon.png"), 28, 28);
        this.setIconOffsetX(-30);
        this.setDescription("Displays your active potion effects.");
        this.setAuthors("bspkrs", "jadedcat");
        this.setKeywords("Potion Status", "Status Effects");
        this.addEventHandler(ClientTickEvent.class, clientTickEvent -> this.blinkCounter++);
        this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
        this.addEventHandler(SimpleEvent29.class, simpleEvent29 -> this.refreshEffects());
        this.addEventHandler(SimpleEvent42.class, simpleEvent42 -> this.refreshEffects());
        this.addEventHandler(SimpleEvent44.class, simpleEvent44 -> this.refreshEffects());
        this.addEventHandler(SimpleEvent38.class, simpleEvent38 -> this.refreshEffects());
        this.addEventHandler(SimpleEvent26.class, simpleEvent26 -> this.refreshEffects());
        this.addEventHandler(SimpleEvent13.class, this::onSimpleEvent13);
        this.addPreviewEffects();
    }

    public boolean isExcluded(PotionEffect potionEffect) {
        if (this.permanent.exclude.getBooleanValue() && potionEffect.getIsPotionDurationMax()) {
            if (this.permanent.excludeList.getListValue().contains(potionEffect.getPotionID()) && this.permanent.exclusionType.getStringValue().equals("Whitelist")) {
                return true;
            }

            if (!this.permanent.excludeList.getListValue().contains(potionEffect.getPotionID()) && this.permanent.exclusionType.getStringValue().equals("Blacklist")) {
                return true;
            }
        }

        if (this.matchesExclusion(potionEffect, this.duration2, potionEffect.getDuration(), this.duration2.excluded.getFloatValue() * 20.0F) && !potionEffect.getIsPotionDurationMax()) {
            return true;
        } else if (this.matchesExclusion(potionEffect, this.amplifier, potionEffect.getAmplifier(), this.amplifier.excluded.getIntValue() - 1)) {
            return true;
        } else if (this.harmfulEffects.getValue().equals("Only Helpful") && !Potion.potionTypes[potionEffect.getPotionID()].isBadEffect()) {
            return true;
        } else {
            return this.harmfulEffects.getValue().equals("Only Harmful") && Potion.potionTypes[potionEffect.getPotionID()].isBadEffect()
                ? true
                : this.excludeSpecificEffects.getListValue().contains(potionEffect.getPotionID());
        }
    }

    public boolean matchesExclusion(PotionEffect potionEffect, PotionEffectsMod.ExclusionSettings exclusionSettings, int n, float f) {
        if (this.matchesCondition(exclusionSettings.excludeSet, n, f)) {
            return exclusionSettings.excludeList.getListValue().contains(potionEffect.getPotionID()) && exclusionSettings.exclusionType.getStringValue().equals("Whitelist")
                ? true
                : !exclusionSettings.excludeList.getListValue().contains(potionEffect.getPotionID()) && exclusionSettings.exclusionType.getStringValue().equals("Blacklist");
        } else {
            return false;
        }
    }

    public int getEffectColor(int n, Object obj, PotionEffect potionEffect) {
        int n2;
        int duration = potionEffect.getDuration();
        label59:
        switch ((String)obj) {
            case "Effect":
                Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                switch (this.colorType.getStringValue()) {
                    case "Default":
                        n2 = defaultColors.get(potionEffect.getPotionID());
                        break label59;
                    case "Potion Colors":
                        n2 = potionColors.get(potionEffect.getPotionID());
                        break label59;
                    case "Color Codes":
                        n2 = colorCodeColors.get(potionEffect.getPotionID());
                        break label59;
                    case "Vibrant":
                        n2 = vibrantColors.get(potionEffect.getPotionID());
                        break label59;
                    default:
                        if (!potion.isBadEffect()) {
                            n2 = -15691760;
                        } else {
                            n2 = -7335920;
                        }
                        break label59;
                }
            case "Duration":
                if (duration >= 1200) {
                    n2 = -16733696;
                } else if (duration >= 600) {
                    n2 = -11141291;
                } else if (duration >= 200) {
                    n2 = -171;
                } else {
                    n2 = duration >= 100 ? -43691 : -5636096;
                }
                break;
            default:
                n2 = n;
        }

        int n3 = n2 >> 24 & 0xFF;
        n3 = (int)(n3 * (this.isExcluded(potionEffect) ? 0.5F : 1.0F));
        return n2 & 16777215 | n3 << 24;
    }

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        this.refreshEffects();
    }

    public String getAmplifierText(int n) {
        String str = this.effectNameText.getBooleanValue() ? " " : "";
        if (n < 0) {
            n = 127 + Math.abs(128 + n);
        }

        int n2 = n + 1;
        int n3 = this.showLevel1.getBooleanValue() ? 0 : 1;
        if (this.showRomanNumerals.getBooleanValue() && n >= n3 && n <= this.maxRomanNumeral.getIntValue()) {
            return str
                + String.join("", Collections.nCopies(n2, "I"))
                    .replace("IIIII", "V")
                    .replace("IIII", "IV")
                    .replace("VV", "X")
                    .replace("VIV", "IX")
                    .replace("XXXXX", "L")
                    .replace("XXXX", "XL")
                    .replace("LL", "C")
                    .replace("LXL", "XC")
                    .replace("CCCCC", "D")
                    .replace("CCCC", "CD")
                    .replace("DD", "M")
                    .replace("DCD", "CM");
        } else {
            return n >= n3 ? str + n2 : "";
        }
    }

    @Override
    public void renderPreview(int n, int n2, int n3, int n4) {
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString("Speed II", n + 8 + n3 / 2 - 20, n2 + n4 / 2 - 36, -1);
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString("0:42", n + 8 + n3 / 2 - 20, n2 + n4 / 2 - 26, -1);
    }

    public boolean shouldBlink(float f) {
        float f2 = this.blinkSpeed.getFloatValue() * 10.0F;
        if (this.blink.getBooleanValue() && f <= this.blinkDuration.getFloatValue() * 20.0F) {
            if (this.blinkCounter > f2 * 2.0F) {
                this.blinkCounter = 0;
            }

            return this.blinkCounter <= f2;
        } else {
            return true;
        }
    }

    public void renderEffects(List<PotionEffect> list) {
        GlStateManager.enableBlend();
        HudAlignment hudAlignment = this.getHudAlignment();
        int n = 0;
        int n2 = 0;
        float floatValue = this.effectSpacing.getFloatValue();
        float f = floatValue
            + (
                !this.icon.getBooleanValue() && (!this.effectName.getBooleanValue() || !this.duration.getBooleanValue())
                    ? this.mc.fontRendererObj.FONT_HEIGHT - 1
                    : 18.0F
            );
        float f2 = this.icon.getBooleanValue() ? 20.0F : 0.0F;
        int stringWidth = this.mc.fontRendererObj.getStringWidth(" ");

        for (PotionEffect potionEffect : list) {
            if (!this.isExcluded(potionEffect)
                || this.mc.currentScreen instanceof ModsScreen
                || this.mc.currentScreen instanceof ModSettingsScreen
                || this.mc.currentScreen instanceof ProfileNameScreen) {
                boolean shouldBlink = this.shouldBlink(potionEffect.getDuration());
                boolean booleanValue = this.overrideNameColorWithPackFormat.getBooleanValue();
                int n3 = 0;
                int n4 = this.duration.getBooleanValue() && !this.compact.getBooleanValue()
                    ? 0
                    : (!this.icon.getBooleanValue() && !this.compact.getBooleanValue() ? 0 : 5);
                String str = (this.effectNameText.getBooleanValue() ? StatCollector.translateToLocal(potionEffect.getEffectName()) : "")
                    + (this.effectAmplifierText.getBooleanValue() ? this.getAmplifierText(potionEffect.getAmplifier()) : "");
                if (this.effectName.getBooleanValue() && (shouldBlink || !this.makeNameBlink.getBooleanValue())) {
                    n3 = this.mc.fontRendererObj.getStringWidth(str) + (int)f2;
                    int effectColor = this.getEffectColor(this.nameColor.getColorValue(), this.colorNameBasedOn.getValue(), potionEffect);
                    if (hudAlignment == HudAlignment.RIGHT) {
                        this.mc
                            .fontRendererObj
                            .drawString(
                                (booleanValue ? str : this.stripColors(str)) + "§r",
                                this.getHudWidth() - n3,
                                (float)n + n4,
                                effectColor,
                                this.textShadow.getBooleanValue()
                            );
                    } else if (hudAlignment == HudAlignment.LEFT) {
                        this.mc
                            .fontRendererObj
                            .drawString((booleanValue ? str : this.stripColors(str)) + "§r", f2, (float)n + n4, effectColor, this.textShadow.getBooleanValue());
                    } else if (hudAlignment == HudAlignment.CENTER) {
                        this.mc
                            .fontRendererObj
                            .drawString(
                                (booleanValue ? str : this.stripColors(str)) + "§r",
                                this.getHudWidth() / 2.0F - n3 / 2 + (this.compact.getBooleanValue() ? f2 / 2.0F - 2.0F : f2),
                                (float)n + n4,
                                effectColor,
                                this.textShadow.getBooleanValue()
                            );
                    }

                    if (n3 > n2) {
                        n2 = n3;
                    }
                }

                String durationString = Potion.getDurationString(potionEffect);
                int n5 = this.duration.getBooleanValue() ? this.mc.fontRendererObj.getStringWidth(durationString) + (int)f2 : 18;
                int n6 = this.effectName.getBooleanValue() && !str.isEmpty() && !this.compact.getBooleanValue()
                    ? 10
                    : (!this.icon.getBooleanValue() && !this.compact.getBooleanValue() ? 0 : 5);
                boolean flag = this.compact.getBooleanValue()
                    && this.effectName.getBooleanValue()
                    && (this.effectNameText.getBooleanValue() || this.effectAmplifierText.getBooleanValue())
                    && !str.isEmpty();
                if (this.duration.getBooleanValue() && (shouldBlink || !this.makeDurationBlink.getBooleanValue())) {
                    int effectColor = this.getEffectColor(this.durationColor.getColorValue(), this.colorDurationBasedOn.getValue(), potionEffect);
                    if (hudAlignment == HudAlignment.RIGHT) {
                        this.mc
                            .fontRendererObj
                            .drawString(
                                durationString + "§r", this.getHudWidth() - n5 - (flag ? n3 - f2 + stringWidth : 0.0F), n + n6, effectColor, this.textShadow.getBooleanValue()
                            );
                    } else if (hudAlignment == HudAlignment.LEFT) {
                        this.mc.fontRendererObj.drawString(durationString + "§r", flag ? n3 + stringWidth : f2, n + n6, effectColor, this.textShadow.getBooleanValue());
                    } else if (hudAlignment == HudAlignment.CENTER) {
                        this.mc
                            .fontRendererObj
                            .drawString(
                                durationString + "§r",
                                this.getHudWidth() / 2.0F + (flag ? n3 / 2 + 2.0F - f2 / 2.0F : -(n5 / 2) + f2),
                                n + n6,
                                effectColor,
                                this.textShadow.getBooleanValue()
                            );
                    }
                }

                Potion potion;
                if ((potion = Potion.potionTypes[potionEffect.getPotionID()]).hasStatusIcon()
                    && this.icon.getBooleanValue()
                    && (shouldBlink || !this.makeIconBlink.getBooleanValue())) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, this.isExcluded(potionEffect) ? 0.5F : 1.0F);
                    this.mc.getTextureManager().bindTexture(this.inventoryPng);
                    int statusIconIndex = potion.getStatusIconIndex();
                    if (hudAlignment != HudAlignment.LEFT && (this.effectName.getBooleanValue() || this.duration.getBooleanValue())) {
                        if (hudAlignment == HudAlignment.RIGHT) {
                            RenderUtils.drawTexturedRect(this.getHudWidth() - 20.0F, n, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                        } else if (hudAlignment == HudAlignment.CENTER) {
                            RenderUtils.drawTexturedRect(
                                this.getHudWidth() / 2.0F - (this.effectName.getBooleanValue() ? n3 : n5) / 2 - (flag ? f2 / 2.0F + 2.0F : 0.0F),
                                n,
                                statusIconIndex % 8 * 18,
                                198 + statusIconIndex / 8 * 18,
                                18,
                                18
                            );
                        }
                    } else {
                        RenderUtils.drawTexturedRect(0.0F, n, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                    }
                }

                if (n5 > n2) {
                    n2 = n5;
                }

                if (n5 / 2 + n3 + stringWidth > n2 && this.compact.getBooleanValue()) {
                    n2 = n5 / 2 + n3 + stringWidth;
                }

                n = (int)(n + f);
            }
        }

        this.setHudSize(n2, n - floatValue);
        GlStateManager.disableBlend();
    }

    public class ExclusionSettings {
        public Setting exclusionType;
        public Setting excludeSet;
        public Setting excluded;
        public Setting exclude;
        public Setting excludeList;

        public void createSettings(String str) {
            this.exclusionType = new Setting(PotionEffectsMod.this, str + " Exclusion Type")
                .withHierarchyParents(PotionEffectsMod.this.excludingOptions)
                .changeValue("Blacklist")
                .setStringValues("Whitelist", "Blacklist")
                .setCustomizationLevel(SettingsLevel.ADVANCED);
            this.excludeList = new Setting(PotionEffectsMod.this, str + " Exclude List")
                .withHierarchyParents(PotionEffectsMod.this.excludingOptions)
                .changeValue(new ArrayList())
                .setAcceptedValues(PotionEffectsMod.this.amplifierOptions)
                .setCustomizationLevel(SettingsLevel.ADVANCED);
        }

        public ExclusionSettings(String str) {
            this.exclude = new Setting(PotionEffectsMod.this, "Exclude " + str + " Effects")
                .withHierarchyParents(PotionEffectsMod.this.excludingOptions)
                .changeValue(false)
                .setCustomizationLevel(SettingsLevel.MEDIUM);
            this.createSettings(str);
            this.exclusionType.withHierarchyParents(this.exclude);
            this.excludeList.withHierarchyParents(this.exclude);
        }

        public ExclusionSettings(String str, Number number, Number number2, Number number3, String str2) {
            this.excludeSet = new Setting(PotionEffectsMod.this, "Exclude Set " + str)
                .withHierarchyParents(PotionEffectsMod.this.excludingOptions)
                .changeValue("OFF")
                .setStringValues("OFF", "Above", "Below", "At", "Not At")
                .setCustomizationLevel(SettingsLevel.MEDIUM);
            this.excluded = new Setting(PotionEffectsMod.this, "Excluded " + str)
                .setDescription("The " + str.toLowerCase() + " effects are excluded")
                .withHierarchyParents(PotionEffectsMod.this.excludingOptions)
                .changeValue(number)
                .setRange(number2, number3)
                .setUnit(str2)
                .setCondition(() -> !this.excludeSet.getValue().equals("OFF"))
                .setCustomizationLevel(SettingsLevel.MEDIUM);
            this.createSettings(str);
            this.exclusionType.setCondition(() -> !this.excludeSet.getValue().equals("OFF"));
            this.excludeList.setCondition(() -> !this.excludeSet.getValue().equals("OFF"));
        }
    }
}
