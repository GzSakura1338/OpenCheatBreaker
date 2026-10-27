package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingsLevel;

public abstract class AbstractCombatValueHudMod extends AbstractValueHudMod {
   public Setting waitingNoBackground;
   public Setting waitingBackground;
   public long longValue;
   public Setting hideWhenNotAttacking;

   @Override
   public void createGeneralOptions() {
      super.createGeneralOptions();
      this.hideWhenNotAttacking = new Setting(this, "Hide When Not Attacking")
         .withParents(this.generalOptions)
         .changeValue(false)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
   }

   public boolean check22() {
      return this.longValue == 0L;
   }

   @Override
   public String formatText(String str) {
      return this.getText() != null && this.getDynamicText() != null ? this.getText() : super.formatText(str);
   }

   public String getText() {
      if (this.check22()) {
         Setting setting = this.showBackground.getBooleanValue() ? this.waitingBackground : this.waitingNoBackground;
         if (!setting.getStringValue().isEmpty()) {
            return setting.getStringValue().replaceAll("%LABEL%", this.getLabelText()).replaceAll("%VALUE%", this.getDynamicText());
         }
      }

      return null;
   }

   public void reset() {
   }

   @Override
   public void createFormatOptions() {
      this.waitingBackground = new Setting(this, "Waiting (Background)")
         .changeValue(this.constantText())
         .setCustomizationLevel(SettingsLevel.ADVANCED)
         .withParents(this.formatOptions, this.showBackground);
      this.waitingNoBackground = new Setting(this, "Waiting (No Background)")
         .changeValue(this.wrapsTextWithoutBackground() && !this.constantText().isEmpty() ? "[" + this.constantText() + "]" : this.constantText())
         .setCustomizationLevel(SettingsLevel.ADVANCED)
         .withParents(this.formatOptions)
         .withInvertedParents(this.showBackground);
   }

   @Override
   public String getDynamicText() {
      return this.longValue == 0L && this.hideWhenNotAttacking.getBooleanValue() ? null : super.getDynamicText();
   }

   public String constantText() {
      return "";
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      if (System.currentTimeMillis() - this.longValue > 2000L) {
         this.longValue = 0L;
         this.reset();
      }
   }

   public AbstractCombatValueHudMod(String str) {
      super(str);
      this.setCategories(ModCategory.HUD, ModCategory.COMBAT);
      this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
   }
}
