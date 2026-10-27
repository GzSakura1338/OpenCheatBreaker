package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.EntityEntityPlayerEvent;
import com.cheatbreaker.client.mod.AbstractCombatValueHudMod;
import com.cheatbreaker.client.mod.ValueRange;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import java.text.DecimalFormat;
import java.util.Collections;
import net.minecraft.util.Vec3;

public class ReachDisplayMod extends AbstractCombatValueHudMod {
   public Setting decimals;
   public Setting showTrailingZeros;
   public double doubleValue;
   public DecimalFormat decimalsValue = new DecimalFormat("#.#");

   @Override
   public Number getNumericValue() {
      return Double.parseDouble(this.createStringBuilder(this.doubleValue).replaceAll(",", "."));
   }

   @Override
   public String getLabelText() {
      return "blocks";
   }

   public String createStringBuilder(double d) {
      return this.showTrailingZeros.getBooleanValue() ? String.format("%." + this.decimals.getValue() + "f", d) : this.decimalsValue.format(d);
   }

   public ReachDisplayMod() {
      super("Reach Display");
      this.setDescription("Displays how far you hit an entity.");
      this.setAuthors("dewgs");
      this.handleString("[2.8 blocks]");
      this.addEventHandler(EntityEntityPlayerEvent.class, this::onEntityEntityPlayerEvent);
   }

   @Override
   public ValueRange getHiddenValueRange() {
      return new ValueRange(0, 2, 5);
   }

   @Override
   public String getStaticText() {
      return this.createStringBuilder(2.780034105782015);
   }

   public void onEntityEntityPlayerEvent(EntityEntityPlayerEvent entityEntityPlayerEvent) {
      if (entityEntityPlayerEvent.getEntityPlayer() == this.mc.thePlayer) {
         Vec3 positionEyes = this.mc.getRenderViewEntity().getPositionEyes(1.0F);
         this.doubleValue = this.mc.objectMouseOver.hitVec.distanceTo(positionEyes);
         this.longValue = System.currentTimeMillis();
      }
   }

   @Override
   public void reset() {
      this.doubleValue = 0.0;
   }

   @Override
   public String getDynamicText() {
      return super.getDynamicText() == null ? null : this.createStringBuilder(this.doubleValue);
   }

   @Override
   public void createFormatOptions() {
      this.showTrailingZeros = new Setting(this, "Show trailing zeros")
         .setDescription("Show all trailing zeros.")
         .withHierarchyParents(this.formatOptions)
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .setCondition(() -> this.decimals.getIntValue() != 0);
      this.decimals = new Setting(this, "Decimals")
         .setDescription("Change how many decimals the mod should display.")
         .withHierarchyParents(this.formatOptions)
         .changeValue(1)
         .setRange(0, 16)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .setOnUpdate(
            obj -> this.decimalsValue = new DecimalFormat(
               (Integer)obj == 0 ? "#" : "#." + String.join("", Collections.nCopies(Integer.parseInt(obj.toString()), "#"))
            )
         );
      super.createFormatOptions();
   }
}
