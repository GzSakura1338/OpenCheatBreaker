package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractValueHudMod;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSoup;

public class PotionCounterMod extends AbstractValueHudMod {
    public Setting counter;
    public Setting hideWhenEmpty;

    @Override
    public void createGeneralOptions() {
        super.createGeneralOptions();
        this.hideWhenEmpty = new Setting(this, "Hide When Empty")
            .setDescription("Hide the mod when there are none of the selected counter item in your inventory.")
            .withHierarchyParents(this.generalOptions)
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
        this.counter = new Setting(this, "Counter")
            .setDescription("Count Healing potions or Soup.")
            .withHierarchyParents(this.generalOptions)
            .changeValue("Pot")
            .setStringValues("Pot", "Soup");
    }

    public PotionCounterMod() throws java.io.IOException {
        super("Potion Counter");
        this.setDescription("Displays the amount of healing potions or bowls of soup in your inventory.");
        this.setAuthors("Maxwell");
        this.setLabel("[10 Pots]");
    }

    @Override
    public String getLabelText() {
        return PotionCounterFormat.pluralize(this.getIntValue(), this.counter.getStringValue());
    }

    @Override
    public String getStaticText() {
        return "10";
    }

    @Override
    public String getDynamicText() {
        return this.getIntValue() == 0 && this.hideWhenEmpty.getBooleanValue() ? null : super.getDynamicText();
    }

    @Override
    public Number getNumericValue() {
        return this.getIntValue();
    }

    public int getIntValue() {
        return this.counter.getValue().equals("Pot")
            ? (int)Arrays.stream(this.mc.thePlayer.inventory.mainInventory)
                .filter(Objects::nonNull)
                .filter(itemStack -> itemStack.getItem() instanceof ItemPotion)
                .filter(itemStack -> itemStack.getMetadata() == 16421 | itemStack.getMetadata() == 16453)
                .count()
            : (int)Arrays.stream(this.mc.thePlayer.inventory.mainInventory).filter(Objects::nonNull).filter(itemStack -> itemStack.getItem() instanceof ItemSoup).count();
    }
}
