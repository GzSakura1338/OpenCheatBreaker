package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.EntityDoubleEvent;
import com.cheatbreaker.client.event.EntityLivingBaseFloatEvent1;
import com.cheatbreaker.client.event.FloatEvent1;
import com.cheatbreaker.client.mod.AbstractBackgroundHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class HurtCamMod extends AbstractBackgroundHudMod {
   public int intValue = 0;
   public float floatValue = 0.0F;
   public Setting cameraOptions;
   public Setting hurtCamera;
   public Setting directionalRotationTilt;
   public Setting fullRotationalTilt;
   public Setting dampenedHurtAggression;
   public Setting clampBound;
   public Setting clampedRotation;
   public Setting hurtShakeRotation;
   public static final double CONSTANT_57_29577951308232 = 180.0 / Math.PI;
   public static final int CONSTANT_90 = 90;
   public Setting hurtShakeIntensity;
   public Setting hurtShakeDampening;

   public HurtCamMod() {
      super("Hurt Cam");
      this.hudOnly = false;
      this.setRenderHudAndDefault(false);
      this.cameraOptions = new SettingGroup(this, "Camera Options").changeValue(true);
      this.hurtCamera = new Setting(this, "Hurt Camera").changeValue(true).withHierarchyParents(this.cameraOptions);
      this.directionalRotationTilt = new Setting(this, "Directional Rotation Tilt").changeValue(false).withHierarchyParents(this.hurtCamera);
      this.fullRotationalTilt = new Setting(this, "Full Rotational Tilt").changeValue(false).withHierarchyParents(this.hurtCamera);
      this.dampenedHurtAggression = new Setting(this, "Dampened Hurt Aggression").changeValue(false).withHierarchyParents(this.hurtCamera);
      this.clampBound = new Setting(this, "Clamp Bound").changeValue(true).withHierarchyParents(this.directionalRotationTilt);
      this.clampedRotation = new Setting(this, "Clamped Rotation").setUnit("°").changeValue(75.0F).setRange(0, 180.0F).withHierarchyParents(this.clampBound);
      this.hurtShakeRotation = new Setting(this, "Hurt Shake Rotation")
         .setUnit("°")
         .changeValue(-90.0F)
         .setRange(-180.0F, 180.0F)
         .withHierarchyParents(this.hurtCamera)
         .withInvertedHierarchyParents(this.directionalRotationTilt);
      this.hurtShakeIntensity = new Setting(this, "Hurt Shake Intensity")
         .setUnit("%")
         .changeValue(100.0F)
         .setRange(30.0F, 250.0F)
         .withHierarchyParents(this.hurtCamera);
      this.hurtShakeDampening = new Setting(this, "Hurt Shake Dampening")
         .setUnit("%")
         .changeValue(115.0F)
         .setRange(50.0F, 250.0F)
         .withHierarchyParents(this.dampenedHurtAggression);
      this.addEventHandler(EntityLivingBaseFloatEvent1.class, this::onEntityLivingBaseFloatEvent1);
      this.addEventHandler(FloatEvent1.class, this::onFloatEvent1);
      this.addEventHandler(EntityDoubleEvent.class, this::onEntityDoubleEvent);
      this.setEnabledByDefault(true);
      this.setCategories(ModCategory.COMBAT, ModCategory.HUD);
      this.handleString("[Hurt at 58°]");
      this.setDescription("Customize the hurt shaking effect to your liking");
      this.setKeywords("Hit animation", "Hurt screen movement", "Damage screen movement");
      this.setAliases(
         "Hurt the Camera",
         "Damage Affects Camera",
         "NoHurtCamera",
         "No Hurt Camera",
         "NoShakeCamera",
         "No Shake Camera",
         "NoHurtShake",
         "No hurt shake",
         "Disable hurt shake"
      );
   }

   @Override
   public String getLabelText() {
      return "Hurt";
   }

   public void onFloatEvent1(FloatEvent1 floatEvent1) {
      if (this.dampenedHurtAggression.getBooleanValue()) {
         float sqrt = (float)Math.sqrt(Math.sqrt(floatEvent1.getFloatValue1() / Math.PI));
         float f;
         floatEvent1.setFloatValue1((float)((f = sqrt / (this.hurtShakeDampening.getFloatValue() / 100.0F)) * f * f * f * Math.PI));
      }

      if (this.directionalRotationTilt.getBooleanValue()) {
         floatEvent1.setAttackedAtYaw(this.computeFloat(this.floatValue));
      } else {
         floatEvent1.setAttackedAtYaw(this.hurtShakeRotation.getFloatValue() + 90.0F);
      }
   }

   public void onEntityLivingBaseFloatEvent1(EntityLivingBaseFloatEvent1 entityLivingBaseFloatEvent1) {
      this.intValue = entityLivingBaseFloatEvent1.getEntityLivingBase().hurtTime;
      if (this.hurtCamera.getBooleanValue()) {
         if (this.fullRotationalTilt.getBooleanValue()) {
            entityLivingBaseFloatEvent1.setFloatValue3(this.hurtShakeIntensity.getFloatValue() / 100.0F);
         } else {
            entityLivingBaseFloatEvent1.setFloatValue2(14.0F * (this.hurtShakeIntensity.getFloatValue() / 100.0F));
         }
      } else {
         entityLivingBaseFloatEvent1.setFlag(true);
      }
   }

   public void onEntityDoubleEvent(EntityDoubleEvent entityDoubleEvent) {
      if (this.directionalRotationTilt.getBooleanValue() && entityDoubleEvent.getEntity() != null) {
         EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
         if (entityDoubleEvent.getEntity().equals(thePlayer)) {
            double d = thePlayer.motionZ - entityDoubleEvent.getZ();
            double d2 = thePlayer.motionX - entityDoubleEvent.getX();
            float f = (float)(Math.atan2(d, d2) * (180.0 / Math.PI) - thePlayer.rotationYaw);
            if (Float.isFinite(f)) {
               this.floatValue = f;
            }
         }
      }
   }

   public float computeFloat(float f) {
      if (this.clampBound.getBooleanValue()) {
         float computeInt = this.computeInt(f - 90.0F);
         float floatValue = this.clampedRotation.getFloatValue();
         if (Math.abs(computeInt) < floatValue) {
            return Math.signum(computeInt) * floatValue + 90.0F;
         }
      }

      return f;
   }

   @Override
   public String getDynamicText() {
      if (this.intValue <= 0) {
         return "Not hurt";
      } else {
         return this.directionalRotationTilt.getBooleanValue() ? "Hurt at " + this.computeInt((int)(this.floatValue - 90.0F)) + "°" : "Hurt";
      }
   }

   public int computeInt(float f) {
      return ((int)f + 540) % 360 - 180;
   }
}
