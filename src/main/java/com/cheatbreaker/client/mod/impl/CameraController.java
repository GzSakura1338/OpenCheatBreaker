package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.IntEvent4;
import net.minecraft.client.Minecraft;

public class CameraController {
   public Minecraft mc = Minecraft.getMinecraft();
   public float savedPrevYaw;
   public int activeMode;
   public float savedPitch;
   public float savedYaw;
   public int savedPerspective;
   public float savedPrevPitch;
   public CheatBreaker cheatBreaker = CheatBreaker.getInstance();
   public boolean active = false;

   public void restore() {
      this.mc.gameSettings.thirdPersonView = this.savedPerspective;
      this.active = false;
   }

   public void start(int n) {
      if (this.cheatBreaker.getModManager().perspectiveMod.isEnabled()) {
         if (!this.active) {
            this.savedYaw = this.mc.thePlayer.rotationYaw;
            this.savedPitch = this.mc.thePlayer.rotationPitch;
            this.savedPrevYaw = this.mc.thePlayer.prevRotationYaw;
            this.savedPrevPitch = this.mc.thePlayer.prevRotationPitch;
            this.savedPerspective = this.mc.gameSettings.thirdPersonView;
            if (n == 1) {
               this.mc.gameSettings.thirdPersonView = 1;
            } else {
               this.mc.gameSettings.thirdPersonView = 2;
            }

            this.activeMode = n;
         }

         this.active = true;
      }
   }

   public void handleKeys() {
      PerspectiveMod perspectiveMod = this.cheatBreaker.getModManager().perspectiveMod;
      if (perspectiveMod.toggleBackCamera.getBooleanValue() && perspectiveMod.backCameraKeybind.isKeybindDown()) {
         if (this.active) {
            this.restore();
         } else {
            this.start(1);
         }
      }

      if (perspectiveMod.toggleFrontCamera.getBooleanValue() && perspectiveMod.frontCameraKeybind.isKeybindDown()) {
         if (this.active) {
            this.restore();
         } else {
            this.start(2);
         }
      }
   }

   public CameraController() {
      this.cheatBreaker.getEventBus().checkClass2(ClientTickEvent.class, clientTickEvent -> {
         PerspectiveMod perspectiveMod = this.cheatBreaker.getModManager().perspectiveMod;
         if (this.active) {
            if ((this.activeMode != 1 || !perspectiveMod.toggleBackCamera.getBooleanValue()) && (this.activeMode != 2 || !perspectiveMod.toggleFrontCamera.getBooleanValue())) {
               if (!perspectiveMod.backCameraKeybind.isKeybindDown() && !perspectiveMod.frontCameraKeybind.isKeybindDown()) {
                  this.restore();
               }
            }
         }
      });
      this.cheatBreaker.getEventBus().checkClass2(IntEvent1.class, intEvent1 -> this.handleKeys());
      this.cheatBreaker.getEventBus().checkClass2(IntEvent4.class, intEvent4 -> this.handleKeys());
   }
}
