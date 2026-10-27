package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.EntityDoubleFloatEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityFishHook;

public class HitboxesMod extends AbstractMod {
   public HitboxesMod.HitboxSettings player;
   public HitboxesMod.HitboxSettings mob;
   public HitboxesMod.HitboxSettings item;
   public HitboxesMod.HitboxSettings projectile;
   public HitboxesMod.HitboxSettings expOrb;

   public HitboxesMod() {
      super("Hitboxes");
      this.setDescription("Shows an outline around an entity's hitbox.");
      this.setLabel("Hitboxes", 1.0F);
      this.setCategories(ModCategory.HUD);
      this.setApplyDefaultColorOnEnable(false);
      this.player = new HitboxesMod.HitboxSettings("Player");
      this.mob = new HitboxesMod.HitboxSettings("Mob");
      this.item = new HitboxesMod.HitboxSettings("Item");
      this.projectile = new HitboxesMod.HitboxSettings("Projectile");
      this.expOrb = new HitboxesMod.HitboxSettings("Exp Orb");
      this.addEventHandler(EntityDoubleFloatEvent.class, this::onEntityDoubleFloatEvent);
   }

   public HitboxesMod.HitboxSettings findHitboxSettings(Entity entity) {
      if (entity instanceof AbstractClientPlayer) {
         return this.player;
      } else if (entity instanceof EntityFishHook) {
         return this.projectile;
      } else if (entity instanceof EntityItem) {
         return this.item;
      } else if (entity instanceof EntityXPOrb) {
         return this.expOrb;
      } else {
         return entity instanceof IProjectile ? this.projectile : this.mob;
      }
   }

   public void onEntityDoubleFloatEvent(EntityDoubleFloatEvent entityDoubleFloatEvent) {
   }

   public class HitboxSettings {
      public Setting hitboxOptions;
      public Setting show;
      public Setting show2;
      public Setting show3;
      public Setting show4;
      public Setting show5;
      public Setting showOwnHitbox;
      public Setting show6;
      public Setting lineDistance;
      public Setting lineThickness;
      public Setting ownHitboxOutlineColor;
      public Setting ownHitboxOverlayColor;
      public Setting outlineColor;
      public Setting overlayColor;
      public Setting eyeHeightColor;
      public Setting lookVectorColor;

      public HitboxSettings(String str) {
         this.hitboxOptions = new SettingGroup(HitboxesMod.this, str + " Hitbox Options").changeValue(true);
         boolean flag = str.equals("Player") || str.equals("Mob");
         this.show = new Setting(HitboxesMod.this, "Show " + str + " Hitbox").changeValue(flag).withParents(this.hitboxOptions);
         this.show2 = new Setting(HitboxesMod.this, "Show " + str + " Hitbox Outline")
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.show);
         this.show3 = new Setting(HitboxesMod.this, "Show " + str + " Hitbox Overlay")
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.show);
         this.show4 = new Setting(HitboxesMod.this, "Show " + str + " Eye Height")
            .changeValue(flag)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.show);
         this.show5 = new Setting(HitboxesMod.this, "Show " + str + " Look Vector")
            .changeValue(flag)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.show);
         if (str.equals("Player")) {
            this.showOwnHitbox = new Setting(HitboxesMod.this, "Show Own Hitbox")
               .changeValue(false)
               .setCustomizationLevel(SettingsLevel.MEDIUM)
               .withHierarchyParents(this.show);
         }

         this.show6 = new Setting(HitboxesMod.this, "Show " + str + " Dashed Line")
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.show2);
         this.lineDistance = new Setting(HitboxesMod.this, str + " Line Distance")
            .changeValue(10)
            .setRange(0, 20)
            .setUnit("px")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.show6);
         this.lineThickness = new Setting(HitboxesMod.this, str + " Line Thickness")
            .changeValue(1)
            .setRange(1, 5)
            .setUnit("px")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.show2);
         if (str.equals("Player")) {
            this.ownHitboxOutlineColor = new Setting(HitboxesMod.this, "Own Hitbox Outline Color")
               .changeValue(-1)
               .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
               .setCustomizationLevel(SettingsLevel.MEDIUM)
               .withHierarchyParents(this.show2);
            this.ownHitboxOverlayColor = new Setting(HitboxesMod.this, "Own Hitbox Overlay Color")
               .changeValue(553648127)
               .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
               .setCustomizationLevel(SettingsLevel.MEDIUM)
               .withHierarchyParents(this.show3);
         }

         this.outlineColor = new Setting(HitboxesMod.this, str + " Outline Color")
            .changeValue(-1)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withHierarchyParents(this.show2);
         this.overlayColor = new Setting(HitboxesMod.this, str + " Overlay Color")
            .changeValue(553648127)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withHierarchyParents(this.show3);
         this.eyeHeightColor = new Setting(HitboxesMod.this, str + " Eye Height Color")
            .changeValue(-65536)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.show4);
         this.lookVectorColor = new Setting(HitboxesMod.this, str + " Look Vector Color")
            .changeValue(-16776961)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.show5);
      }
   }
}
