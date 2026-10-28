package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.EntityPlayerEvent;
import com.cheatbreaker.client.event.S06PacketUpdateHealthEvent;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.mod.AbstractValueHudMod;
import com.cheatbreaker.client.mod.BackgroundSizeLimits;
import com.cheatbreaker.client.mod.impl.FoodInfo;
import com.cheatbreaker.client.mod.impl.FoodValue;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.ui.AnchorPoint;
import java.text.DecimalFormat;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;

public class SaturationMod extends AbstractValueHudMod {
    public static ResourceLocation iconsPng = new ResourceLocation("client/appleskin/icons.png");
    public DecimalFormat decimalsValue = new DecimalFormat("#");
    public AmountColorSetting low;
    public AmountColorSetting highest;
    public int bottomOffset = 39;
    public AmountColorSetting medium;
    public String saturationText = "5";
    public Setting colorBasedOnAmount;
    public Setting appleskinOptions;
    public Setting showTrailingZeros;
    public AmountColorSetting high;
    public AmountColorSetting empty;
    public float flashProgress = 0.0f;
    public Setting decimals;
    public byte flashDirection = 1;
    public Setting showFoodValuesOverlay;
    public Setting showSaturationOverlay;
    public int textColorValue;

    @Override
    public void createFormatOptions() {
        this.showTrailingZeros = new Setting(this, "Show trailing zeros").setDescription("Show all trailing zeros.").withHierarchyParents(this.formatOptions).changeValue(false).setCustomizationLevel((Object)SettingsLevel.MEDIUM).setCondition(() -> this.decimals.getIntValue() != 0);
        this.decimals = new Setting(this, "Decimals").setDescription("Change how many decimals the mod should display.").withHierarchyParents(this.formatOptions).changeValue(0).setRange(0, 17).setCustomizationLevel((Object)SettingsLevel.MEDIUM).setOnUpdate(object -> {
            this.decimalsValue = new DecimalFormat((Integer)object == 0 ? "#" : "#." + String.join((CharSequence)"", Collections.nCopies(Integer.parseInt(object.toString()), "#")));
        });
    }

    @Override
    public String getLabelText() {
        return "Saturation";
    }

    public static void drawFoodBar(int n, int n2, Minecraft minecraft, int n3, int n4, float f) {
        if (n == 0) {
            return;
        }
        int n5 = n2 / 2;
        int n6 = (int)Math.ceil((float)Math.min(20, n2 + n) / 2.0f);
        int n7 = n6 - n5;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(Gui.icons);
        SaturationMod.beginAlpha(f);
        for (int i = n5; i < n5 + n7; ++i) {
            int n8 = i * 2 + 1;
            int n9 = n3 - i * 8 - 9;
            int n10 = n4;
            int n11 = 16;
            int n12 = 13;
            if (minecraft.thePlayer.isPotionActive(Potion.hunger)) {
                n11 += 36;
            }
            Gui.drawTexturedRect(n9, n10, 16 + n12 * 9, 27.0f, 9, 9);
            if (n8 < n2 + n) {
                Gui.drawTexturedRect(n9, n10, n11 + 36, 27.0f, 9, 9);
                continue;
            }
            if (n8 != n2 + n) continue;
            Gui.drawTexturedRect(n9, n10, n11 + 45, 27.0f, 9, 9);
        }
        SaturationMod.endAlpha(f);
    }

    public void lambda$createGeneralOptions$1(Object object) {
        this.updateTextColor();
    }

    public String formatValue(double d) {
        return this.showTrailingZeros.getBooleanValue() ? String.format("%." + this.decimals.getValue() + "f", d) : this.decimalsValue.format(d);
    }

    public void onS06PacketUpdateHealthEvent(S06PacketUpdateHealthEvent s06PacketUpdateHealthEvent) {
        this.saturationText = this.formatValue(this.mc.thePlayer.getFoodStats().getSaturationLevel());
        this.updateTextColor();
    }

    @Override
    public String getStaticText() {
        return this.saturationText;
    }

    @Override
    public boolean centerByDefault() {
        return true;
    }

    @Override
    public String getDefaultFormat() {
        return "%VALUE%";
    }

    @Override
    public Number getNumericValue() {
        return Double.parseDouble(this.saturationText.replaceAll(",", "."));
    }

    public void updateTextColor() {
        double d = this.getNumericValue().doubleValue();
        this.textColorValue = this.colorBasedOnAmount.getBooleanValue() ? (d <= 0.0 ? this.empty.amountColor.getColorValue() : (d <= 5.0 ? this.low.amountColor.getColorValue() : (d <= 10.0 ? this.medium.amountColor.getColorValue() : (d <= 15.0 ? this.high.amountColor.getColorValue() : this.highest.amountColor.getColorValue())))) : this.textColor.getColorValue();
    }

    @Override
    public boolean wrapsTextWithoutBackground() {
        return false;
    }

    @Override
    public BackgroundSizeLimits getSizeLimits() {
        return new BackgroundSizeLimits(10.0f, 13.0f, 24.0f, 18.0f, 20.0f, 80.0f);
    }

    @Override
    public boolean showBackgroundByDefault() {
        return false;
    }

    @Override
    public void createColorOptions() {
        this.highest = new AmountColorSetting(this, "Highest", -2097185);
        this.high = new AmountColorSetting(this, "High", -4522079);
        this.medium = new AmountColorSetting(this, "Medium", -103);
        this.low = new AmountColorSetting(this, "Low", -48574);
        this.empty = new AmountColorSetting(this, "Empty", -5627615);
        this.textColor.withInvertedParents(this.colorBasedOnAmount);
        this.textColor.setOnUpdate(this::lambda$createColorOptions$3);
    }

    public static void beginAlpha(float f) {
        GlStateManager.enableBlend();
        if (f == 1.0f) {
            return;
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, f);
        GlStateManager.blendFunc(770, 771);
    }

    public void lambda$createColorOptions$3(Object object) {
        this.updateTextColor();
    }

    @Override
    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        super.onScaledResolutionEvent3(scaledResolutionEvent3);
        if (!this.showFoodValuesOverlay.getBooleanValue() && !this.showSaturationOverlay.getBooleanValue()) {
            return;
        }
        if (this.mc.thePlayer.ridingEntity != null || !this.mc.playerController.shouldDrawHUD()) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityClientPlayerMP entityClientPlayerMP = minecraft.thePlayer;
        ItemStack itemStack = entityClientPlayerMP.getHeldItem();
        FoodStats foodStats = entityClientPlayerMP.getFoodStats();
        ScaledResolution scaledResolution = scaledResolutionEvent3.getScaledResolution();
        int n = scaledResolution.getScaledWidth() / 2 + 91;
        int n2 = scaledResolution.getScaledHeight() - this.bottomOffset;
        if (this.showSaturationOverlay.getBooleanValue()) {
            SaturationMod.drawSaturationBar(0.0f, foodStats.getSaturationLevel(), minecraft, n, n2, 1.0f);
        }
        if (!this.showFoodValuesOverlay.getBooleanValue() || !FoodInfo.getItem(itemStack)) {
            this.flashProgress = 0.0f;
            this.flashDirection = 1;
            return;
        }
        FoodValue foodValue = FoodInfo.createFoodValue(itemStack, entityClientPlayerMP);
        SaturationMod.drawFoodBar(foodValue.healAmount, foodStats.getFoodLevel(), minecraft, n, n2, this.flashProgress);
        if (this.showSaturationOverlay.getBooleanValue()) {
            int n3 = foodStats.getFoodLevel() + foodValue.healAmount;
            float f = foodStats.getSaturationLevel() + foodValue.getFloatValue();
            SaturationMod.drawSaturationBar(f > (float)n3 ? (float)n3 - foodStats.getSaturationLevel() : foodValue.getFloatValue(), foodStats.getSaturationLevel(), minecraft, n, n2, this.flashProgress);
        }
    }

    @Override
    public int getTextColor() {
        return this.textColorValue;
    }

    public void onEntityPlayerEvent(EntityPlayerEvent entityPlayerEvent) {
        this.saturationText = this.formatValue(entityPlayerEvent.getEntityPlayer().getFoodStats().getSaturationLevel());
        this.updateTextColor();
    }

    @Override
    public void createGeneralOptions() {
        super.createGeneralOptions();
        this.colorBasedOnAmount = new Setting(this, "Color Based on Amount").setDescription("Make the text color be based off the amount of saturation levels you have.").withHierarchyParents(this.generalOptions).changeValue(false).setCustomizationLevel((Object)SettingsLevel.MEDIUM).setOnUpdate(this::lambda$createGeneralOptions$1);
        this.appleskinOptions = new SettingGroup(this, "AppleSkin Options").changeValue(false).setCustomizationLevel((Object)SettingsLevel.MEDIUM);
        this.showSaturationOverlay = new Setting(this, "Show Saturation Overlay").setDescription("Show the AppleSkin Saturation Overlay").withHierarchyParents(this.appleskinOptions).changeValue(false);
        this.showFoodValuesOverlay = new Setting(this, "Show Food Values Overlay").setDescription("Show the AppleSkin Food Values Overlay").withHierarchyParents(this.appleskinOptions).changeValue(false);
    }

    @Override
    public String getDynamicText() {
        if (super.getDynamicText() == null) {
            return null;
        }
        return this.saturationText;
    }

    public static void endAlpha(float f) {
        GlStateManager.disableBlend();
        if (f == 1.0f) {
            return;
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public SaturationMod() throws java.io.IOException {
        super("Saturation");
        this.setDescription("Displays your current food saturation level.");
        this.setAuthors("Eum3 (Number HUD)", "squeek502 (AppleSkin)");
        this.setIcon(new ResourceLocation("client/icons/mods/saturation.png"), 36, 36);
        this.setAnchorPointAndDefault(AnchorPoint.MIDDLE_BOTTOM_RIGHT);
        this.setTranslationAndDefault(-10.0f, -35.0f);
        this.hudOnly = false;
        this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
        this.addEventHandler(EntityPlayerEvent.class, this::onEntityPlayerEvent);
        this.addEventHandler(S06PacketUpdateHealthEvent.class, this::onS06PacketUpdateHealthEvent);
    }

    public void onClientTickEvent(ClientTickEvent clientTickEvent) {
        this.flashProgress += (float)this.flashDirection * 0.125f;
        if (this.flashProgress >= 1.5f) {
            this.flashProgress = 1.0f;
            this.flashDirection = (byte)-1;
        } else if (this.flashProgress <= -0.5f) {
            this.flashProgress = 0.0f;
            this.flashDirection = 1;
        }
    }

    public static void drawSaturationBar(float f, float f2, Minecraft minecraft, int n, int n2, float f3) {
        if (f2 + f < 0.0f) {
            return;
        }
        int n3 = f != 0.0f ? Math.max(0, (int)f2 / 2) : 0;
        int n4 = (int)Math.ceil(Math.min(20.0f, f2 + f) / 2.0f);
        int n5 = n4 - n3;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(iconsPng);
        SaturationMod.beginAlpha(f3);
        for (int i = n3; i < n3 + n5; ++i) {
            int n6 = n - i * 8 - 9;
            int n7 = n2;
            float f4 = (f2 + f) / 2.0f - (float)i;
            if (f4 >= 1.0f) {
                Gui.drawTexturedRect(n6, n7, 27.0f, 0.0f, 9, 9);
                continue;
            }
            if ((double)f4 > 0.5) {
                Gui.drawTexturedRect(n6, n7, 18.0f, 0.0f, 9, 9);
                continue;
            }
            if ((double)f4 > 0.25) {
                Gui.drawTexturedRect(n6, n7, 9.0f, 0.0f, 9, 9);
                continue;
            }
            if (!(f4 > 0.0f)) continue;
            Gui.drawTexturedRect(n6, n7, 0.0f, 0.0f, 9, 9);
        }
        SaturationMod.endAlpha(f3);
        minecraft.getTextureManager().bindTexture(Gui.icons);
    }

    public class AmountColorSetting {
        public Setting amountColor;
        public /* synthetic */ SaturationMod this$0;

        public AmountColorSetting(SaturationMod saturationMod, String string, int n) {
            this.this$0 = saturationMod;
            this.amountColor = new Setting(saturationMod, string + " Amount Color").setDescription("The text color that will show when the saturation level is " + string.toLowerCase() + ".").changeValue(n).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE).setCustomizationLevel((Object)SettingsLevel.ADVANCED).withParents(saturationMod.colorBasedOnAmount, saturationMod.colorOptions).setOnUpdate(object -> this.this$0.updateTextColor());
        }
    }
}
