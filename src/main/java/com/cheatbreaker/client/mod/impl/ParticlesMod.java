package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;

public class ParticlesMod extends AbstractMod {
    public Setting generalParticles;
    public Setting showSprintingParticles;
    public Setting showBlockBreakingParticles;
    public Setting attackParticles;
    public Setting showWhenDamaged;
    public Setting showWhenAttacking;
    public Setting sharpnessParticles;
    public Setting sharpnessMultiplier;
    public Setting critParticles;
    public Setting critMultiplier;
    public Setting effectParticles;
    public Setting showSplashParticles;
    public Setting instantSplashOpacity;
    public Setting normalSplashOpacity;
    public Setting showActiveEffectParticles;
    public Setting activeEffectOpacity;
    public Setting ambientEffectOpacity;

    public ParticlesMod() {
        super("Particles");
        this.setCategories(ModCategory.HUD, ModCategory.SERVER);
        this.generalParticles = new SettingGroup(this, "General Particles").changeValue(true);
        this.showSprintingParticles = new Setting(this, "Show Sprinting Particles").changeValue(true).withParents(this.generalParticles);
        this.showBlockBreakingParticles = new Setting(this, "Show Block Breaking Particles").changeValue(true).withParents(this.generalParticles);
        this.attackParticles = new SettingGroup(this, "Attack Particles").changeValue(true);
        this.showWhenDamaged = new Setting(this, "Show When Damaged")
            .changeValue("Override")
            .setStringValues("OFF", "Vanilla", "Override")
            .withHierarchyParents(this.attackParticles);
        this.showWhenAttacking = new Setting(this, "Show When Attacking")
            .changeValue("Override")
            .setStringValues("OFF", "Vanilla", "Override")
            .withHierarchyParents(this.attackParticles);
        this.sharpnessParticles = new Setting(this, "Sharpness Particles")
            .changeValue("Vanilla")
            .setStringValues("Never", "Vanilla", "Always")
            .setCondition(() -> this.showWhenDamaged.getValue().equals("Override") || this.showWhenAttacking.getValue().equals("Override"))
            .withHierarchyParents(this.attackParticles);
        this.sharpnessMultiplier = new Setting(this, "Sharpness Multiplier")
            .changeValue(1.0F)
            .setRange(0.1F, 10.0F)
            .setUnit("x")
            .setCondition(
                () -> !this.sharpnessParticles.getValue().equals("Never")
                    && (this.showWhenDamaged.getValue().equals("Override") || this.showWhenAttacking.getValue().equals("Override"))
            )
            .withHierarchyParents(this.attackParticles);
        this.critParticles = new Setting(this, "Crit Particles")
            .changeValue("Vanilla")
            .setStringValues("Never", "Vanilla", "Always")
            .setCondition(() -> this.showWhenDamaged.getValue().equals("Override") || this.showWhenAttacking.getValue().equals("Override"))
            .withHierarchyParents(this.attackParticles);
        this.critMultiplier = new Setting(this, "Crit Multiplier")
            .changeValue(1.0F)
            .setRange(0.1F, 10.0F)
            .setUnit("x")
            .setCondition(
                () -> !this.critParticles.getValue().equals("Never")
                    && (this.showWhenDamaged.getValue().equals("Override") || this.showWhenAttacking.getValue().equals("Override"))
            )
            .withHierarchyParents(this.attackParticles);
        this.effectParticles = new SettingGroup(this, "Effect Particles").changeValue(true);
        this.showSplashParticles = new Setting(this, "Show Splash Particles").changeValue(true).withHierarchyParents(this.effectParticles);
        this.instantSplashOpacity = new Setting(this, "Instant Splash Opacity")
            .changeValue(100.0F)
            .setRange(0.01F, 100.0F)
            .setUnit("%")
            .setIconNames("circle-hollow-64", "circle-64")
            .setIconScales(7.5F, 7.5F)
            .setIconXOffsets(-0.25F, -2.75F)
            .setIconYOffsets(-0.75F, -0.75F)
            .withHierarchyParents(this.showSplashParticles);
        this.normalSplashOpacity = new Setting(this, "Normal Splash Opacity")
            .changeValue(100.0F)
            .setRange(0.01F, 100.0F)
            .setUnit("%")
            .setIconNames("circle-hollow-64", "circle-64")
            .setIconScales(7.5F, 7.5F)
            .setIconXOffsets(-0.25F, -2.75F)
            .setIconYOffsets(-0.75F, -0.75F)
            .withHierarchyParents(this.showSplashParticles);
        this.showActiveEffectParticles = new Setting(this, "Show Active Effect Particles").changeValue(true).withHierarchyParents(this.effectParticles);
        this.activeEffectOpacity = new Setting(this, "Active Effect Opacity")
            .changeValue(100.0F)
            .setRange(0.01F, 100.0F)
            .setUnit("%")
            .setIconNames("circle-hollow-64", "circle-64")
            .setIconScales(7.5F, 7.5F)
            .setIconXOffsets(-0.25F, -2.75F)
            .setIconYOffsets(-0.75F, -0.75F)
            .withHierarchyParents(this.showActiveEffectParticles);
        this.ambientEffectOpacity = new Setting(this, "Ambient Effect Opacity")
            .changeValue(15.0F)
            .setRange(0.01F, 100.0F)
            .setUnit("%")
            .setIconNames("circle-hollow-64", "circle-64")
            .setIconScales(7.5F, 7.5F)
            .setIconXOffsets(-0.25F, -2.75F)
            .setIconYOffsets(-0.75F, -0.75F)
            .withHierarchyParents(this.showActiveEffectParticles);
        this.setLabel("Particles", 1.0F);
        this.setDescription("Change how particles appear.");
        this.setAuthors("dewgs (Multipliers)");
    }
}
