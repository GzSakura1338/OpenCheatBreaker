package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import net.minecraft.util.ResourceLocation;

public class EnchantmentGlintMod extends AbstractMod {
    public EnchantmentGlintMod.GlintSettings inventory;
    public Setting potionOptions;
    public Setting showPotionColor;
    public Setting onlyRenderPotionGlint;
    public Setting excludePotionGlint;
    public Setting shinyPots;
    public Setting shinyPotsBoundary;
    public EnchantmentGlintMod.GlintSettings dropped;
    public EnchantmentGlintMod.GlintSettings held;
    public EnchantmentGlintMod.GlintSettings armor;

    public EnchantmentGlintMod() throws java.io.IOException {
        super("Enchantment Glint");
        this.setApplyDefaultColorOnEnable(false);
        this.inventory = new EnchantmentGlintMod.GlintSettings("Inventory");
        this.potionOptions = new SettingGroup(this, "Potion Options").changeValue(true).withHierarchyParents(this.inventory.glint);
        this.showPotionColor = new Setting(this, "Show Potion Color").changeValue(false).withHierarchyParents(this.potionOptions);
        this.onlyRenderPotionGlint = new Setting(this, "Only Render Potion Glint").changeValue(false).withHierarchyParents(this.potionOptions);
        this.excludePotionGlint = new Setting(this, "Exclude Potion Glint").changeValue(false).withHierarchyParents(this.potionOptions);
        this.shinyPots = new Setting(this, "Shiny Pots")
            .setDescription("Show a glint box around potions.")
            .changeValue(false)
            .withHierarchyParents(this.potionOptions);
        this.shinyPotsBoundary = new Setting(this, "Shiny Pots Boundary")
            .changeValue(true)
            .setToggleTexts("To Border", "Entire Slot")
            .withHierarchyParents(this.shinyPots);
        this.dropped = new EnchantmentGlintMod.GlintSettings("Dropped");
        this.held = new EnchantmentGlintMod.GlintSettings("Held");
        this.armor = new EnchantmentGlintMod.GlintSettings("Armor");
        this.setIcon(new ResourceLocation("client/icons/mods/ench_sword.png"), 32, 32);
        this.setDescription("Customize the enchantment glint and/or enable shiny pots overlay.");
        this.setCategories(ModCategory.MISC);
        this.setEnabledByDefault(true);
    }

    public class GlintSettings {
        public Setting glintSettings;
        public Setting glint;
        public Setting glintColor;

        public GlintSettings(String str) {
            this.glintSettings = new SettingGroup(EnchantmentGlintMod.this, str + " Glint Settings").changeValue(true);
            this.glint = new Setting(EnchantmentGlintMod.this, str + " Glint").changeValue(true).withHierarchyParents(this.glintSettings);
            this.glintColor = new Setting(EnchantmentGlintMod.this, str + " Glint Color")
                .changeValue(-8372020)
                .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
                .withHierarchyParents(this.glint);
        }
    }
}
