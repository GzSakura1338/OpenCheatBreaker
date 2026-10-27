package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;

public class ParticlesMod extends AbstractMod {
   public Setting generalParticles = new SettingGroup(this, "General Particles").changeValue(true);
   public Setting showSprintingParticles = new Setting(this, "Show Sprinting Particles").changeValue(true).withParents(this.generalParticles);
   public Setting showBlockBreakingParticles = new Setting(this, "Show Block Breaking Particles").changeValue(true).withParents(this.generalParticles);
   public Setting attackParticles = new SettingGroup(this, "Attack Particles").changeValue(true);
   public Setting showWhenDamaged = new Setting(this, "Show When Damaged")
      .changeValue("Override")
      .setStringValues("OFF", "Vanilla", "Override")
      .withHierarchyParents(this.attackParticles);
   public Setting showWhenAttacking = new Setting(this, "Show When Attacking")
      .changeValue("Override")
      .setStringValues("OFF", "Vanilla", "Override")
      .withHierarchyParents(this.attackParticles);
   public Setting sharpnessParticles = new Setting(this, "Sharpness Particles")
      .changeValue("Vanilla")
      .setStringValues("Never", "Vanilla", "Always")
      .setCondition(() -> this.showWhenDamaged.getValue().equals("Override") || this.showWhenAttacking.getValue().equals("Override"))
      .withHierarchyParents(this.attackParticles);
   public Setting sharpnessMultiplier = new Setting(this, "Sharpness Multiplier")
      .changeValue(1.0F)
      .setRange(0.1F, 10.0F)
      .setUnit("x")
      .setCondition(
         () -> !this.sharpnessParticles.getValue().equals("Never")
            && (this.showWhenDamaged.getValue().equals("Override") || this.showWhenAttacking.getValue().equals("Override"))
      )
      .withHierarchyParents(this.attackParticles);
   public Setting critParticles = new Setting(this, "Crit Particles")
      .changeValue("Vanilla")
      .setStringValues("Never", "Vanilla", "Always")
      .setCondition(() -> this.showWhenDamaged.getValue().equals("Override") || this.showWhenAttacking.getValue().equals("Override"))
      .withHierarchyParents(this.attackParticles);
   public Setting critMultiplier = new Setting(this, "Crit Multiplier")
      .changeValue(1.0F)
      .setRange(0.1F, 10.0F)
      .setUnit("x")
      .setCondition(
         () -> !this.critParticles.getValue().equals("Never")
            && (this.showWhenDamaged.getValue().equals("Override") || this.showWhenAttacking.getValue().equals("Override"))
      )
      .withHierarchyParents(this.attackParticles);
   public Setting effectParticles = new SettingGroup(this, "Effect Particles").changeValue(true);
   public Setting showSplashParticles = new Setting(this, "Show Splash Particles").changeValue(true).withHierarchyParents(this.effectParticles);
   public Setting instantSplashOpacity = new Setting(this, "Instant Splash Opacity")
      .changeValue(100.0F)
      .setRange(0.01F, 100.0F)
      .setUnit("%")
      .setIconNames("circle-hollow-64", "circle-64")
      .setIconScales(7.5F, 7.5F)
      .setIconXOffsets(-0.25F, -2.75F)
      .setIconYOffsets(-0.75F, -0.75F)
      .withHierarchyParents(this.showSplashParticles);
   public Setting normalSplashOpacity = new Setting(this, "Normal Splash Opacity")
      .changeValue(100.0F)
      .setRange(0.01F, 100.0F)
      .setUnit("%")
      .setIconNames("circle-hollow-64", "circle-64")
      .setIconScales(7.5F, 7.5F)
      .setIconXOffsets(-0.25F, -2.75F)
      .setIconYOffsets(-0.75F, -0.75F)
      .withHierarchyParents(this.showSplashParticles);
   public Setting showActiveEffectParticles = new Setting(this, "Show Active Effect Particles").changeValue(true).withHierarchyParents(this.effectParticles);
   public Setting activeEffectOpacity = new Setting(this, "Active Effect Opacity")
      .changeValue(100.0F)
      .setRange(0.01F, 100.0F)
      .setUnit("%")
      .setIconNames("circle-hollow-64", "circle-64")
      .setIconScales(7.5F, 7.5F)
      .setIconXOffsets(-0.25F, -2.75F)
      .setIconYOffsets(-0.75F, -0.75F)
      .withHierarchyParents(this.showActiveEffectParticles);
   public Setting ambientEffectOpacity = new Setting(this, "Ambient Effect Opacity")
      .changeValue(15.0F)
      .setRange(0.01F, 100.0F)
      .setUnit("%")
      .setIconNames("circle-hollow-64", "circle-64")
      .setIconScales(7.5F, 7.5F)
      .setIconXOffsets(-0.25F, -2.75F)
      .setIconYOffsets(-0.75F, -0.75F)
      .withHierarchyParents(this.showActiveEffectParticles);

   public ParticlesMod() {
      super("Particles");
      this.setLabel("Particles", 1.0F);
      this.setDescription("Change how particles appear.");
      this.setAuthors("dewgs (Multipliers)");
      this.setCategories(ModCategory.HUD, ModCategory.SERVER);
   }
}
