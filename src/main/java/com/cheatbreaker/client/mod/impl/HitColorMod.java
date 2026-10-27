package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.EntityLivingBaseFloatEvent2;
import com.cheatbreaker.client.event.EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import net.minecraft.util.ResourceLocation;

public class HitColorMod extends AbstractMod {
   public Setting generalOptions;
   public Setting enableHitColor;
   public Setting affectArmor;
   public Setting affectedByBrightness;
   public Setting animationType;
   public Setting colorOptions;
   public Setting hitColor;

   public float getEntityLivingBase(EntityLivingBaseFloatEvent2 entityLivingBaseFloatEvent2) {
      return 1.0F - entityLivingBaseFloatEvent2.getEntityLivingBase().hurtTime / 10.0F;
   }

   public HitColorMod() {
      super("Hit Color");
      this.setApplyDefaultColorOnEnable(false);
      this.setCategories(ModCategory.HUD);
      this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
      this.enableHitColor = new Setting(this, "Enable Hit Color").changeValue(true).withParents(this.generalOptions);
      this.affectArmor = new Setting(this, "Affect Armor").changeValue(true).withHierarchyParents(this.enableHitColor);
      this.affectedByBrightness = new Setting(this, "Affected by brightness").changeValue(true).withHierarchyParents(this.enableHitColor);
      this.animationType = new Setting(this, "Animation Type")
         .changeValue("None")
         .setStringValues("None", "Linear In/Out", "Linear Out")
         .withHierarchyParents(this.enableHitColor);
      this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true).withParents(this.enableHitColor);
      this.hitColor = new Setting(this, "Hit Color")
         .changeValue(1727987712)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions);
      this.setDescription("Customize the damage tint overlay on entities.");
      this.setAuthors("aycy (Fade animation)");
      this.setKeywords("Damage Tint");
      this.setIcon(new ResourceLocation("client/icons/mods/hittint.png"), 32, 32);
      this.addEventHandler(EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent.class, this::onEntityLivingBaseRendererLivingEntityDoubleFloatIntEvent);
      this.addEventHandler(EntityLivingBaseFloatEvent2.class, this::onEntityLivingBaseFloatEvent2);
      this.setEnabledByDefault(true);
   }

   public void onEntityLivingBaseFloatEvent2(EntityLivingBaseFloatEvent2 entityLivingBaseFloatEvent2) {
      float f = this.affectedByBrightness.getBooleanValue() ? entityLivingBaseFloatEvent2.getBrightness() : 1.0F;
      int colorValue = this.hitColor.getColorValue();
      entityLivingBaseFloatEvent2.setFloatValue1((colorValue >> 16 & 0xFF) / 255.0F * f);
      entityLivingBaseFloatEvent2.setFloatValue2((colorValue >> 8 & 0xFF) / 255.0F * f);
      entityLivingBaseFloatEvent2.setFloatValue3((colorValue & 0xFF) / 255.0F * f);
      entityLivingBaseFloatEvent2.setFloatValue4(this.computeFloat(entityLivingBaseFloatEvent2, colorValue));
   }

   public float computeFloat(String str, float f) {
      if (str.equals("Linear In/Out")) {
         return f < 0.5F ? f / 0.5F : (1.0F - f) / 0.5F;
      } else {
         return str.equals("Linear Out") ? 1.0F - f : f;
      }
   }

   public float computeFloat(EntityLivingBaseFloatEvent2 entityLivingBaseFloatEvent2, int n) {
      float f = (n >> 24 & 0xFF) / 255.0F;
      String stringValue = this.animationType.getStringValue();
      if (!stringValue.equals("None")) {
         float entityLivingBase = this.getEntityLivingBase(entityLivingBaseFloatEvent2);
         f *= this.computeFloat(stringValue, entityLivingBase);
      }

      return f;
   }

   public void onEntityLivingBaseRendererLivingEntityDoubleFloatIntEvent(EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent entityLivingBaseRendererLivingEntityDoubleFloatIntEvent) {
      entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.setFlag(true);
      if (!this.enableHitColor.getBooleanValue()) {
      }
   }
}
