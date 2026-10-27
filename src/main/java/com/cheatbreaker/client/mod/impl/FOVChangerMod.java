package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.FloatEvent3;
import com.cheatbreaker.client.event.FloatEvent9;
import com.cheatbreaker.client.event.SimpleEvent48;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;

public class FOVChangerMod extends AbstractMod {
   public float floatValue = 70.0F;
   public Setting defaultFov;
   public Setting slownessFov;
   public static final int CONSTANT_70 = 70;
   public Setting sprintingFov;
   public Setting flyingFov;
   public Setting speedFov;
   public Setting staticFov;
   public static final int CONSTANT_30 = 30;
   public static final int CONSTANT_110 = 110;
   public Setting staticSwiftness;
   public Setting aimingMultiplier;

   public void onFloatEvent3(FloatEvent3 floatEvent3) {
      boolean flag = false;
      EntityPlayerSP renderViewEntity = (EntityPlayerSP)this.mc.getRenderViewEntity();
      if (renderViewEntity.isPotionActive(Potion.moveSpeed)) {
         floatEvent3.floatValue = this.speedFov.getIntValue();
         flag = true;
      }

      if (renderViewEntity.isPotionActive(Potion.moveSlowdown)) {
         floatEvent3.floatValue = this.slownessFov.getIntValue();
         flag = true;
      }

      if (renderViewEntity.capabilities.isFlying) {
         floatEvent3.floatValue = this.flyingFov.getIntValue();
         flag = true;
      }

      if (renderViewEntity.isSprinting()) {
         floatEvent3.floatValue = this.sprintingFov.getIntValue();
         flag = true;
      }

      if (!flag) {
         floatEvent3.floatValue = this.defaultFov.getIntValue();
      }

      if (this.staticFov.getBooleanValue()) {
         floatEvent3.setFlag(true);
      }
   }

   public FOVChangerMod() {
      super("FOV Changer");
      this.setDescription("Changes your FOV based on different cases.");
      this.setCategories(ModCategory.MISC);
      this.setLabel("FOV", 1.5F);
      this.defaultFov = new Setting(this, "Default FOV").changeValue(70).setRange(30, 110);
      this.slownessFov = new Setting(this, "Slowness FOV").changeValue(70).setRange(30, 110);
      this.sprintingFov = new Setting(this, "Sprinting FOV").changeValue(70).setRange(30, 110);
      this.flyingFov = new Setting(this, "Flying FOV").changeValue(70).setRange(30, 110);
      this.speedFov = new Setting(this, "Speed FOV").changeValue(70).setRange(30, 110);
      this.staticFov = new Setting(this, "Static FOV").changeValue(true);
      this.staticSwiftness = new Setting(this, "Static Swiftness").setDescription("Determines if your FOV changes as you move.").changeValue(false);
      this.aimingMultiplier = new Setting(this, "Aiming Multiplier").setUnit("x").changeValue(0.15F).setRange(0.15F, 0.75F);
      this.addEventHandler(FloatEvent3.class, this::onFloatEvent3);
      this.addEventHandler(FloatEvent9.class, floatEvent9 -> floatEvent9.setFloatValue(this.aimingMultiplier.getFloatValue()));
      this.addEventHandler(SimpleEvent48.class, simpleEvent48 -> {
         if (this.staticSwiftness.getBooleanValue()) {
            simpleEvent48.setFlag(true);
         }
      });
   }

   public void setFloatValue(float f) {
      this.floatValue = f;
   }
}
