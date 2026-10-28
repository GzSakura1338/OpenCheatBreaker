package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.IntEvent4;
import net.minecraft.client.Minecraft;

public class FreeLookHandler {
    public float yaw;
    public int savedPerspective;
    public float pitch;
    public float prevYaw;
    public Minecraft mc = Minecraft.getMinecraft();
    public CheatBreaker cheatBreaker = CheatBreaker.getInstance();
    public float prevPitch;
    public boolean active = false;

    public void onKeyEvent() {
        if (this.cheatBreaker.getModManager().perspectiveMod.toggleDragToLook.getBooleanValue()
            && this.cheatBreaker.getModManager().perspectiveMod.dragToLookKeybind.isKeybindDown()) {
            if (!this.active) {
                this.activate();
            } else {
                this.deactivate();
            }
        }
    }

    public void deactivate() {
        this.mc.gameSettings.thirdPersonView = this.savedPerspective;
        this.active = false;
    }

    public void onMouseMoved(float f, float f2) {
        float pitch = this.pitch;
        float yaw = this.yaw;
        this.yaw = (float)(this.yaw + (this.cheatBreaker.getModManager().perspectiveMod.invertYawLeftAndRight.getBooleanValue() ? -f : f) * 0.15);
        this.pitch = (float)(this.pitch - (this.cheatBreaker.getModManager().perspectiveMod.invertPitchUpAndDown.getBooleanValue() ? -f2 : f2) * 0.15);
        if (this.cheatBreaker.getModManager().perspectiveMod.pitchLock.getBooleanValue()) {
            if (this.pitch < -90.0F) {
                this.pitch = -90.0F;
            }

            if (this.pitch > 90.0F) {
                this.pitch = 90.0F;
            }
        }

        this.prevPitch = this.prevPitch + (this.pitch - pitch);
        this.prevYaw = this.prevYaw + (this.yaw - yaw);
    }

    public void activate() {
        if (this.cheatBreaker.getModManager().perspectiveMod.isEnabled()) {
            if (!this.active) {
                this.yaw = this.mc.thePlayer.rotationYaw;
                this.pitch = this.mc.thePlayer.rotationPitch;
                this.prevYaw = this.mc.thePlayer.prevRotationYaw;
                this.prevPitch = this.mc.thePlayer.prevRotationPitch;
                this.savedPerspective = this.mc.gameSettings.thirdPersonView;
                switch (this.cheatBreaker.getModManager().perspectiveMod.lookView.getStringValue()) {
                    case "Third":
                        this.mc.gameSettings.thirdPersonView = 1;
                        break;
                    case "Reverse":
                        this.mc.gameSettings.thirdPersonView = 2;
                        break;
                    default:
                        this.mc.gameSettings.thirdPersonView = 0;
                }
            }

            this.active = true;
        }
    }

    public FreeLookHandler() {
        this.cheatBreaker
            .getEventBus()
            .checkClass2(
                ClientTickEvent.class,
                clientTickEvent -> {
                    if (!this.cheatBreaker.getModManager().perspectiveMod.toggleDragToLook.getBooleanValue()
                        && this.active
                        && !this.cheatBreaker.getModManager().perspectiveMod.dragToLookKeybind.isKeybindDown()) {
                        this.deactivate();
                    }
                }
            );
        this.cheatBreaker.getEventBus().checkClass2(IntEvent1.class, intEvent1 -> this.onKeyEvent());
        this.cheatBreaker.getEventBus().checkClass2(IntEvent4.class, intEvent4 -> this.onKeyEvent());
    }
}
