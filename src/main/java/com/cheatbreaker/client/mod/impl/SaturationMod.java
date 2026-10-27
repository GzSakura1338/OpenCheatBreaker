package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.EntityPlayerEvent;
import com.cheatbreaker.client.event.S06PacketUpdateHealthEvent;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.mod.AbstractValueHudMod;
import com.cheatbreaker.client.mod.BackgroundSizeLimits;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.ui.AnchorPoint;
import java.text.DecimalFormat;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;

public class SaturationMod extends AbstractValueHudMod {
   public DecimalFormat decimalsValue = new DecimalFormat("#");
   public SaturationMod.AmountColorSetting medium;
   public SaturationMod.AmountColorSetting highest;
   public Setting decimals;
   public Setting showFoodValuesOverlay;
   public Setting showSaturationOverlay;
   public static ResourceLocation iconsPng = new ResourceLocation("client/appleskin/icons.png");
   public Setting showTrailingZeros;
   public Setting colorBasedOnAmount;
   public int bottomOffset = 39;
   public SaturationMod.AmountColorSetting empty;
   public SaturationMod.AmountColorSetting low;
   public String saturationText = "5";
   public int textColorValue;
   public SaturationMod.AmountColorSetting high;
   public Setting appleskinOptions;
   public float flashProgress = 0.0F;
   public byte flashDirection = 1;

   public String formatValue(double d) {
      return this.showTrailingZeros.getBooleanValue() ? String.format("%." + this.decimals.getValue() + "f", d) : this.decimalsValue.format(d);
   }

   @Override
   public BackgroundSizeLimits getSizeLimits() {
      return new BackgroundSizeLimits(10.0F, 13.0F, 24.0F, 18.0F, 20.0F, 80.0F);
   }

   @Override
   public int getTextColor() {
      return this.textColorValue;
   }

   @Override
   public String getStaticText() {
      return this.saturationText;
   }

   @Override
   public String getDynamicText() {
      return super.getDynamicText() == null ? null : this.saturationText;
   }

   public void onS06PacketUpdateHealthEvent(S06PacketUpdateHealthEvent s06PacketUpdateHealthEvent) {
      this.saturationText = this.formatValue(this.mc.thePlayer.getFoodStats().getSaturationLevel());
      this.updateTextColor();
   }

   public void updateTextColor() {
      double d = this.getNumericValue().doubleValue();
      if (this.colorBasedOnAmount.getBooleanValue()) {
         if (d <= 0.0) {
            this.textColorValue = this.empty.amountColor.getColorValue();
         } else if (d <= 5.0) {
            this.textColorValue = this.low.amountColor.getColorValue();
         } else if (d <= 10.0) {
            this.textColorValue = this.medium.amountColor.getColorValue();
         } else if (d <= 15.0) {
            this.textColorValue = this.high.amountColor.getColorValue();
         } else {
            this.textColorValue = this.highest.amountColor.getColorValue();
         }
      } else {
         this.textColorValue = this.textColor.getColorValue();
      }
   }

   @Override
   public void createGeneralOptions() {
      super.createGeneralOptions();
      this.colorBasedOnAmount = new Setting(this, "Color Based on Amount")
         .setDescription("Make the text color be based off the amount of saturation levels you have.")
         .withHierarchyParents(this.generalOptions)
         .changeValue(false)
         .setOnUpdate(obj -> this.updateTextColor());
      this.appleskinOptions = new SettingGroup(this, "AppleSkin Options").changeValue(false).setCustomizationLevel(SettingsLevel.MEDIUM);
      this.showSaturationOverlay = new Setting(this, "Show Saturation Overlay")
         .setDescription("Show the AppleSkin Saturation Overlay")
         .withHierarchyParents(this.appleskinOptions)
         .changeValue(false);
      this.showFoodValuesOverlay = new Setting(this, "Show Food Values Overlay")
         .setDescription("Show the AppleSkin Food Values Overlay")
         .withHierarchyParents(this.appleskinOptions)
         .changeValue(false);
   }

   @Override
   public boolean wrapsTextWithoutBackground() {
      return false;
   }

   public SaturationMod() {
      super("Saturation");
      this.setDescription("Displays your current food saturation level.");
      this.setAuthors("Eum3 (Number HUD)", "squeek502 (AppleSkin)");
      this.setIcon(new ResourceLocation("client/icons/mods/saturation.png"), 36, 36);
      this.setAnchorPointAndDefault(AnchorPoint.MIDDLE_BOTTOM_RIGHT);
      this.setTranslationAndDefault(-10.0F, -35.0F);
      this.hudOnly = false;
      this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
      this.addEventHandler(EntityPlayerEvent.class, this::onEntityPlayerEvent);
      this.addEventHandler(S06PacketUpdateHealthEvent.class, this::onS06PacketUpdateHealthEvent);
   }

   public static void drawSaturationBar(float f, float f2, Minecraft mc, int n, int n2, float f3) {
      if (!(f2 + f < 0.0F)) {
         int n3 = f != 0.0F ? Math.max(0, (int)f2 / 2) : 0;
         int ceil = (int)Math.ceil(Math.min(20.0F, f2 + f) / 2.0F);
         int n4 = ceil - n3;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         mc.getTextureManager().bindTexture(iconsPng);
         beginAlpha(f3);

         for (int i = n3; i < n3 + n4; i++) {
            int n5 = n - i * 8 - 9;
            int n6 = n2;
            float f4 = (f2 + f) / 2.0F - i;
            if (f4 >= 1.0F) {
               mc.ingameGUI.drawTexturedModalRect(n5, n6, 27, 0, 9, 9);
            } else if (f4 > 0.5) {
               mc.ingameGUI.drawTexturedModalRect(n5, n6, 18, 0, 9, 9);
            } else if (f4 > 0.25) {
               mc.ingameGUI.drawTexturedModalRect(n5, n6, 9, 0, 9, 9);
            } else if (f4 > 0.0F) {
               mc.ingameGUI.drawTexturedModalRect(n5, n6, 0, 0, 9, 9);
            }
         }

         endAlpha(f3);
         mc.getTextureManager().bindTexture(Gui.icons);
      }
   }

   @Override
   public boolean centerByDefault() {
      return true;
   }

   @Override
   public Number getNumericValue() {
      return Double.parseDouble(this.saturationText.replaceAll(",", "."));
   }

   public static void drawFoodBar(int n, int n2, Minecraft mc, int n3, int n4, float f) {
      if (n != 0) {
         int n5 = n2 / 2;
         int ceil = (int)Math.ceil(Math.min(20, n2 + n) / 2.0F);
         int n6 = ceil - n5;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         mc.getTextureManager().bindTexture(Gui.icons);
         beginAlpha(f);

         for (int i = n5; i < n5 + n6; i++) {
            int n7 = i * 2 + 1;
            int n8 = n3 - i * 8 - 9;
            int n9 = n4;
            int n10 = 16;
            byte b = 13;
            if (mc.thePlayer.isPotionActive(Potion.hunger)) {
               n10 += 36;
            }

            mc.ingameGUI.drawTexturedModalRect(n8, n9, 16 + b * 9, 27, 9, 9);
            if (n7 < n2 + n) {
               mc.ingameGUI.drawTexturedModalRect(n8, n9, n10 + 36, 27, 9, 9);
            } else if (n7 == n2 + n) {
               mc.ingameGUI.drawTexturedModalRect(n8, n9, n10 + 45, 27, 9, 9);
            }
         }

         endAlpha(f);
      }
   }

   public void onEntityPlayerEvent(EntityPlayerEvent entityPlayerEvent) {
      this.saturationText = this.formatValue(entityPlayerEvent.getEntityPlayer().getFoodStats().getSaturationLevel());
      this.updateTextColor();
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      this.flashProgress = this.flashProgress + this.flashDirection * 0.125F;
      if (this.flashProgress >= 1.5F) {
         this.flashProgress = 1.0F;
         this.flashDirection = -1;
      } else if (this.flashProgress <= -0.5F) {
         this.flashProgress = 0.0F;
         this.flashDirection = 1;
      }
   }

   @Override
   public boolean showBackgroundByDefault() {
      return false;
   }

   @Override
   public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
      super.onScaledResolutionEvent3(scaledResolutionEvent3);
      if (this.showFoodValuesOverlay.getBooleanValue() || this.showSaturationOverlay.getBooleanValue()) {
         if (this.mc.thePlayer.ridingEntity == null && this.mc.playerController.shouldDrawHUD()) {
            Minecraft minecraft = Minecraft.getMinecraft();
            EntityPlayerSP thePlayer = minecraft.thePlayer;
            ItemStack heldItem = thePlayer.getHeldItem();
            FoodStats foodStats = thePlayer.getFoodStats();
            ScaledResolution scaledResolution = scaledResolutionEvent3.getScaledResolution();
            int n = scaledResolution.getScaledWidth() / 2 + 91;
            int n2 = scaledResolution.getScaledHeight() - this.bottomOffset;
            if (this.showSaturationOverlay.getBooleanValue()) {
               drawSaturationBar(0.0F, foodStats.getSaturationLevel(), minecraft, n, n2, 1.0F);
            }

            if (this.showFoodValuesOverlay.getBooleanValue() && FoodInfo.getItem(heldItem)) {
               FoodValue createFoodValue = FoodInfo.createFoodValue(heldItem, thePlayer);
               drawFoodBar(createFoodValue.intValue, foodStats.getFoodLevel(), minecraft, n, n2, this.flashProgress);
               if (this.showSaturationOverlay.getBooleanValue()) {
                  int n3 = foodStats.getFoodLevel() + createFoodValue.intValue;
                  float f = foodStats.getSaturationLevel() + createFoodValue.getFloatValue();
                  drawSaturationBar(
                     f > n3 ? n3 - foodStats.getSaturationLevel() : createFoodValue.getFloatValue(), foodStats.getSaturationLevel(), minecraft, n, n2, this.flashProgress
                  );
               }
            } else {
               this.flashProgress = 0.0F;
               this.flashDirection = 1;
            }
         }
      }
   }

   @Override
   public void createColorOptions() {
      this.highest = new SaturationMod.AmountColorSetting("Highest", -2097185);
      this.high = new SaturationMod.AmountColorSetting("High", -4522079);
      this.medium = new SaturationMod.AmountColorSetting("Medium", -103);
      this.low = new SaturationMod.AmountColorSetting("Low", -48574);
      this.empty = new SaturationMod.AmountColorSetting("Empty", -5627615);
      this.textColor.withInvertedParents(this.colorBasedOnAmount);
      this.textColor.setOnUpdate(obj -> this.updateTextColor());
   }

   @Override
   public void createFormatOptions() {
      this.showTrailingZeros = new Setting(this, "Show trailing zeros")
         .setDescription("Show all trailing zeros.")
         .withHierarchyParents(this.formatOptions)
         .changeValue(false)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .setCondition(() -> this.decimals.getIntValue() != 0);
      this.decimals = new Setting(this, "Decimals")
         .setDescription("Change how many decimals the mod should display.")
         .withHierarchyParents(this.formatOptions)
         .changeValue(0)
         .setRange(0, 17)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .setOnUpdate(
            obj -> this.decimalsValue = new DecimalFormat(
               (Integer)obj == 0 ? "#" : "#." + String.join("", Collections.nCopies(Integer.parseInt(obj.toString()), "#"))
            )
         );
   }

   @Override
   public String getDefaultFormat() {
      return "%VALUE%";
   }

   @Override
   public String getLabelText() {
      return "Saturation";
   }

   public static void endAlpha(float f) {
      GlStateManager.disableBlend();
      if (f != 1.0F) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public static void beginAlpha(float f) {
      GlStateManager.enableBlend();
      if (f != 1.0F) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, f);
         GlStateManager.blendFunc(770, 771);
      }
   }

   public class AmountColorSetting {
      public Setting amountColor;

      public AmountColorSetting(String str, int n) {
         this.amountColor = new Setting(SaturationMod.this, str + " Amount Color")
            .setDescription("The text color that will show when the saturation level is " + str.toLowerCase() + ".")
            .changeValue(n)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withParents(SaturationMod.this.colorBasedOnAmount, SaturationMod.this.colorOptions)
            .setOnUpdate(obj -> SaturationMod.this.updateTextColor());
      }
   }
}
