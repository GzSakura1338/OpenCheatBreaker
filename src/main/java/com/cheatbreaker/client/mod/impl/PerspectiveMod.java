package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.mod.AbstractBackgroundHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import org.lwjgl.opengl.GL11;

public class PerspectiveMod extends AbstractBackgroundHudMod {
   public Setting thirdPersonOptions;
   public Setting toggleBackCamera;
   public Setting toggleFrontCamera;
   public Setting backCameraKeybind;
   public Setting frontCameraKeybind;
   public Setting firstPersonOptions;
   public Setting centeredSwinging;
   public Setting straightSwinging;
   public Setting staticItemScale;
   public Setting dragToLookOptions;
   public Setting lookView;
   public Setting toggleDragToLook;
   public Setting pitchLock;
   public Setting invertPitchUpAndDown;
   public Setting invertYawLeftAndRight;
   public Setting dragToLookKeybind;

   public void run() {
      if (this.dragToLookKeybind.isKeybindDown() && !this.toggleDragToLook.getBooleanValue() && !this.isRestricted()) {
         CheatBreaker.getInstance().getModManager().freeLookHandler.activate();
      }

      if (this.backCameraKeybind.isKeybindDown() && !this.toggleBackCamera.getBooleanValue()) {
         CheatBreaker.getInstance().getModManager().cameraController.start(1);
      }

      if (this.frontCameraKeybind.isKeybindDown() && !this.toggleFrontCamera.getBooleanValue()) {
         CheatBreaker.getInstance().getModManager().cameraController.start(2);
      }
   }

   public void process(float f, float f2, float f3) {
      if (this.isEnabled()) {
         if (!this.centeredSwinging.getBooleanValue()) {
            GL11.glTranslatef(f, f2, f3);
         }
      } else {
         GL11.glTranslatef(f, f2, f3);
      }
   }

   public PerspectiveMod() {
      super("Perspective");
      this.setDescription("Allows you to change how your perspective looks.");
      this.setKeywords("Freelook", "Snaplook", "Spinlook", "Spin View", "360 Perspective");
      this.setCategories(ModCategory.MISC);
      this.hudOnly = false;
      this.setRenderHudAndDefault(false);
      this.setLabel("[Drag View]", 1.5F);
      this.thirdPersonOptions = new SettingGroup(this, "Third Person Options").changeValue(false);
      this.toggleBackCamera = new Setting(this, "Toggle Back Camera")
         .setDescription("Make the back camera keybind toggled instead of held.")
         .withHierarchyParents(this.thirdPersonOptions)
         .changeValue(false);
      this.toggleFrontCamera = new Setting(this, "Toggle Front Camera")
         .setDescription("Make the front camera keybind toggled instead of held.")
         .withHierarchyParents(this.thirdPersonOptions)
         .changeValue(false);
      this.backCameraKeybind = new Setting(this, "Back Camera Keybind")
         .setDescription("The key to use the Back Camera.")
         .changeValue(0)
         .setMouseBind(false)
         .withParents(this.thirdPersonOptions);
      this.frontCameraKeybind = new Setting(this, "Front Camera Keybind")
         .setDescription("The key to use the Front Camera.")
         .changeValue(0)
         .setMouseBind(false)
         .withParents(this.thirdPersonOptions);
      this.firstPersonOptions = new SettingGroup(this, "First Person Options").changeValue(false);
      this.centeredSwinging = new Setting(this, "Centered Swinging").withHierarchyParents(this.firstPersonOptions).changeValue(false);
      this.straightSwinging = new Setting(this, "Straight Swinging").withHierarchyParents(this.firstPersonOptions).changeValue(false);
      this.staticItemScale = new Setting(this, "Static Item Scale")
         .withHierarchyParents(this.firstPersonOptions)
         .changeValue(0.4F)
         .setRange(0.1F, 0.5F)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .setUnit("x")
         .setIconNames("letter-t-64", "letter-t-64")
         .setIconScales(6.0F, 8.0F)
         .setIconXOffsets(0.0F, -2.0F)
         .setIconYOffsets(2.0F, -1.5F);
      this.dragToLookOptions = new SettingGroup(this, "Drag to Look Options").changeValue(true);
      this.lookView = new Setting(this, "Look View")
         .setDescription("Change which perspective mode to use when toggling Drag to Look.")
         .withHierarchyParents(this.dragToLookOptions)
         .changeValue("Third")
         .setStringValues("Third", "Reverse", "First");
      this.toggleDragToLook = new Setting(this, "Toggle Drag to Look")
         .setDescription("Make the Drag to Look keybind toggled instead of held.")
         .withHierarchyParents(this.dragToLookOptions)
         .changeValue(false);
      this.pitchLock = new Setting(this, "Pitch Lock")
         .setDescription("Toggle the pitch axis limitation.")
         .withHierarchyParents(this.dragToLookOptions)
         .changeValue(true);
      this.invertPitchUpAndDown = new Setting(this, "Invert Pitch (Up and Down)")
         .setDescription("Invert the rotation pitch when moving the camera.")
         .withHierarchyParents(this.dragToLookOptions)
         .changeValue(false);
      this.invertYawLeftAndRight = new Setting(this, "Invert Yaw (Left and Right)")
         .setDescription("Invert the rotation yaw when moving the camera.")
         .withHierarchyParents(this.dragToLookOptions)
         .changeValue(false);
      this.dragToLookKeybind = new Setting(this, "Drag to Look Keybind")
         .setDescription("The key to use Drag to Look.")
         .changeValue(56)
         .setMouseBind(false)
         .withParents(this.dragToLookOptions);
      this.addEventHandler(IntEvent4.class, intEvent4 -> this.run());
      this.addEventHandler(IntEvent1.class, intEvent1 -> this.run());
      this.setEnabledByDefault(true);
   }

   public void process(float f) {
      if (this.isEnabled()) {
         if (!this.straightSwinging.getBooleanValue()) {
            GL11.glRotatef(f, 0.0F, 1.0F, 0.0F);
         }
      } else {
         GL11.glRotatef(f, 0.0F, 1.0F, 0.0F);
      }
   }

   @Override
   public String getDynamicText() {
      if (CheatBreaker.getInstance().getModManager().freeLookHandler.active) {
         return "Drag";
      }

      switch (this.mc.gameSettings.thirdPersonView) {
         case 1:
            return "Back";
         case 2:
            return "Front";
         default:
            return "Eye";
      }
   }

   @Override
   public String getLabelText() {
      return "View";
   }

   @Override
   public String getDefaultFormat() {
      return "%VALUE% %LABEL%";
   }
}
