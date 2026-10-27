package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingsLevel;

public abstract class AbstractValueHudMod extends AbstractBackgroundHudMod {
   public Setting hiddenValueS;
   public Setting hideWhenValueIs;

   @Override
   public void createGeneralOptions() {
      this.hideWhenValueIs = new Setting(this, "Hide when value is")
         .withHierarchyParents(this.generalOptions)
         .changeValue("OFF")
         .setStringValues("OFF", "Above", "Below", "At", "Not At")
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.hiddenValueS = new Setting(this, "Hidden value(s)")
         .withHierarchyParents(this.generalOptions)
         .changeValue(this.getHiddenValueRange().getDefaultValue())
         .setRange(this.getHiddenValueRange().getMin(), this.getHiddenValueRange().getMax())
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .setCondition(() -> !this.hideWhenValueIs.getValue().equals("OFF"));
   }

   @Override
   public String getDefaultFormat() {
      return "%VALUE% %LABEL%";
   }

   public abstract Number getNumericValue();

   @Override
   public String getDynamicText() {
      return this.matchesCondition(this.hideWhenValueIs, this.getNumericValue(), this.hiddenValueS.getIntValue()) ? null : "" + this.getNumericValue();
   }

   public ValueRange getHiddenValueRange() {
      return new ValueRange(0, 0, 30);
   }

   public AbstractValueHudMod(String str) {
      super(str);
   }
}
