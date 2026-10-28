package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractNamedMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;

public class TrimpMod extends AbstractNamedMod {
    public static Setting airAccelerate;
    public static Setting sharkingWaterFriction;
    public static Setting accelerate;
    public static Setting trimpMultiplier;
    public static Setting hardCap;
    public static Setting maxAirAccelPerTick;
    public static Setting sharking;
    public static Setting trimp;
    public static Setting sharkingSurfaceTension;
    public static Setting uncappedBunnyhop;
    public static Setting increasedFalldistance;
    public static Setting softCapDegen;
    public static Setting softCap;

    public static boolean isActive() {
        return CheatBreaker.getInstance().getModManager().bunnyhop.isStaffModEnabled() && CheatBreaker.getInstance().getModManager().bunnyhop.isEnabled();
    }

    public TrimpMod(String str) {
        super(str);
        this.setStaffMod(true);
        this.setCategories(ModCategory.MISC);
        trimp = new Setting(this, "Trimp").changeValue(true);
        trimpMultiplier = new Setting(this, "Trimp Multiplier").changeValue(1.4F).setRange(1.0F, 4.0F).setUnit("x").withHierarchyParents(trimp);
        hardCap = new Setting(this, "Hard cap").changeValue(2.0F).setRange(1.0F, 4.0F);
        softCap = new Setting(this, "Soft cap").changeValue(1.4F).setRange(1.0F, 4.0F);
        softCapDegen = new Setting(this, "Soft cap degen").changeValue(0.65F).setRange(0.1F, 4.0F);
        sharking = new Setting(this, "Sharking").changeValue(true);
        sharkingSurfaceTension = new Setting(this, "Sharking surface tension").changeValue(0.2).setRange(0.0, 1.0).withHierarchyParents(sharking);
        sharkingWaterFriction = new Setting(this, "Sharking water friction").changeValue(0.1).setRange(0.0, 1.0).withHierarchyParents(sharking);
        accelerate = new Setting(this, "Accelerate").changeValue(20.0).setRange(5.0, 100.0);
        airAccelerate = new Setting(this, "Air accelerate").changeValue(28.0).setRange(5.0, 150.0);
        maxAirAccelPerTick = new Setting(this, "Max air accel per tick").changeValue(0.095).setRange(0.0, 1.0);
        uncappedBunnyhop = new Setting(this, "Uncapped BunnyHop").changeValue(true);
        increasedFalldistance = new Setting(this, "Increased FallDistance").changeValue(0.0).setRange(0.0, 10.0);
    }
}
