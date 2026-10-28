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
    public double lastReach;
    public Setting showTrailingZeros;
    public DecimalFormat decimalsValue = new DecimalFormat("#.#");

    @Override
    public Number getNumericValue() {
        return Double.parseDouble(this.formatReach(this.lastReach).replaceAll(",", "."));
    }

    @Override
    public String getDynamicText() {
        return super.getDynamicText() == null ? null : this.formatReach(this.lastReach);
    }

    @Override
    public String getStaticText() {
        return this.formatReach(2.780034105782015);
    }

    public ReachDisplayMod() throws java.io.IOException {
        super("Reach Display");
        this.setDescription("Displays how far you hit an entity.");
        this.setAuthors("dewgs");
        this.setLabel("[2.8 blocks]");
        this.addEventHandler(EntityEntityPlayerEvent.class, this::onEntityEntityPlayerEvent);
    }

    @Override
    public String getLabelText() {
        return "blocks";
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

    @Override
    public ValueRange getHiddenValueRange() {
        return new ValueRange(0, 2, 5);
    }

    public void onEntityEntityPlayerEvent(EntityEntityPlayerEvent entityEntityPlayerEvent) {
        if (entityEntityPlayerEvent.getEntityPlayer() == this.mc.thePlayer) {
            Vec3 position = this.mc.renderViewEntity.getPosition(1.0F);
            this.lastReach = this.mc.objectMouseOver.hitVec.distanceTo(position);
            this.lastAttackMillis = System.currentTimeMillis();
        }
    }

    @Override
    public void reset() {
        this.lastReach = 0.0;
    }

    public String formatReach(double d) {
        return this.showTrailingZeros.getBooleanValue() ? String.format("%." + this.decimals.getValue() + "f", d) : this.decimalsValue.format(d);
    }
}
