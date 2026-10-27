package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.SimpleEvent16;
import com.cheatbreaker.client.mod.AbstractBackgroundHudMod;
import com.cheatbreaker.client.mod.BackgroundSizeLimits;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;

public class ToggleSprintMod extends AbstractBackgroundHudMod {
   public Setting descendString;
   public Setting togglesneakOptions;
   public Setting ridingString;
   public static Setting toggleSprint;
   public static boolean flag = false;
   public Setting flyString;
   public Setting sneakingString;
   public Setting flyBoostOptions;
   public Setting sprintOptions;
   public static Setting flyBoost;
   public Setting sneakingToggleString;
   public static Setting sneakInContainer;
   public static float floatValue = 0.05F;
   public Setting sprintingToggleString;
   public Setting sprintingVanillaString;
   public Setting sprintingHeldString;
   public Setting dismountString;
   public static Setting toggleSneak;
   public static Setting doubleTap;
   public Setting flyBoostString;
   public static Setting flyBoostAmount;

   @Override
   public boolean showBackgroundByDefault() {
      return false;
   }

   public void onSimpleEvent16(SimpleEvent16 simpleEvent16) {
      simpleEvent16.setFlag(true);
      this.mc.thePlayer.run();
   }

   public ToggleSprintMod() {
      super("ToggleSprint");
      this.addEventHandler(SimpleEvent16.class, this::onSimpleEvent16);
      this.setEnabledByDefault(false);
      this.hudOnly = false;
      this.setDescription("Makes your sprint and sneak keys toggleable.");
      this.setAuthors("deeznueces");
      this.setCategories(ModCategory.HUD, ModCategory.COMBAT);
      this.handleString("[Sprinting (Toggled)]");
      this.setLabelScale(1.0F);
   }

   @Override
   public String getLabelText() {
      return "Sprint Status";
   }

   @Override
   public String getDynamicText() {
      return !ToggleSprintInput.statusText.isEmpty() ? ToggleSprintInput.statusText : null;
   }

   @Override
   public BackgroundSizeLimits getSizeLimits() {
      return new BackgroundSizeLimits(10.0F, 13.0F, 24.0F, 40.0F, 56.0F, 120.0F);
   }

   @Override
   public void createGeneralOptions() {
      this.sprintOptions = new SettingGroup(this, "Sprint Options").changeValue(true);
      toggleSprint = new Setting(this, "Toggle Sprint")
         .setDescription("Makes sprinting toggleable instead of held down.")
         .withHierarchyParents(this.sprintOptions)
         .changeValue(true);
      doubleTap = new Setting(this, "Double Tap")
         .setDescription("Determine if double tapping the moving forward key should make the player start sprinting.")
         .withHierarchyParents(this.sprintOptions)
         .changeValue(false);
      this.togglesneakOptions = new SettingGroup(this, "ToggleSneak Options").changeValue(true);
      toggleSneak = new Setting(this, "Toggle Sneak")
         .setDescription("Makes sneaking toggleable instead of held down.")
         .withHierarchyParents(this.togglesneakOptions)
         .changeValue(false);
      sneakInContainer = new Setting(this, "Sneak In Container")
         .setDescription("Allows sneaking in containers.")
         .changeValue(false)
         .withHierarchyParents(toggleSneak);
      this.flyBoostOptions = new SettingGroup(this, "Fly Boost Options").changeValue(false);
      flyBoost = new Setting(this, "Fly Boost")
         .setDescription("Determine if pressing the sprint key should boost the fly speed.")
         .withHierarchyParents(this.flyBoostOptions)
         .changeValue(true);
      flyBoostAmount = new Setting(this, "Fly Boost Amount")
         .setDescription("Change the boost amount.")
         .changeValue(4.0F)
         .setRange(1.0F, 10.0F)
         .setUnit("x")
         .withHierarchyParents(flyBoost);
   }

   @Override
   public void createFormatOptions() {
      this.flyBoostString = new Setting(this, "Fly Boost String")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Flying (%BOOST%x boost)")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.flyString = new Setting(this, "Fly String")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Flying")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.ridingString = new Setting(this, "Riding String")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Riding")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.descendString = new Setting(this, "Descend String")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Descending")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.dismountString = new Setting(this, "Dismount String")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Dismounting")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.sneakingString = new Setting(this, "Sneaking String")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Sneaking (Key Held)")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.sprintingHeldString = new Setting(this, "Sprinting Held String")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Sprinting (Key Held)")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.sprintingVanillaString = new Setting(this, "Sprinting Vanilla String")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Sprinting (Vanilla)")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.sprintingToggleString = new Setting(this, "Sprinting Toggle String")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Sprinting (Toggled)")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.sneakingToggleString = new Setting(this, "Sneaking Toggle String")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Sneaking (Toggled)")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
   }

   @Override
   public String getStaticText() {
      return this.sprintingToggleString.getStringValue();
   }
}
