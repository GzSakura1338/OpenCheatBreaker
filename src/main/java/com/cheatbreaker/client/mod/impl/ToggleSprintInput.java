package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.impl.ToggleSprintMod;
import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;

public class ToggleSprintInput
extends MovementInputFromOptions {
    public static boolean holdToSprint;
    public static long sneakToggleTime;
    public static boolean doubleTap;
    public static boolean sprintToggled;
    public static boolean sprintBeforeRiding;
    public static long sprintToggleTime;
    public static boolean sprintHeldLong;
    public static boolean sneakKeyDown;
    public static boolean sprintLocked;
    public static String statusText;
    public static boolean sneakWhileRidingOrFlying;
    public static boolean wasRiding;
    public static boolean sprintKeyDown;
    public static boolean sneakReleasedForContainer;

    public static void update(Minecraft minecraft, MovementInputFromOptions movementInputFromOptions, EntityPlayerSP entityPlayerSP) {
        movementInputFromOptions.moveStrafe = 0.0f;
        movementInputFromOptions.moveForward = 0.0f;
        GameSettings gameSettings = minecraft.gameSettings;
        if (gameSettings.keyBindForward.isKeyDown()) {
            movementInputFromOptions.moveForward += 1.0f;
        }
        if (gameSettings.keyBindBack.isKeyDown()) {
            movementInputFromOptions.moveForward -= 1.0f;
        }
        if (gameSettings.keyBindLeft.isKeyDown()) {
            movementInputFromOptions.moveStrafe += 1.0f;
        }
        if (gameSettings.keyBindRight.isKeyDown()) {
            movementInputFromOptions.moveStrafe -= 1.0f;
        }
        if (entityPlayerSP.isRiding() && !wasRiding) {
            wasRiding = true;
            sprintBeforeRiding = sprintToggled;
        } else if (wasRiding && !entityPlayerSP.isRiding()) {
            wasRiding = false;
            if (sprintBeforeRiding && !sprintToggled) {
                sprintToggled = true;
                sprintToggleTime = System.currentTimeMillis();
                sprintKeyDown = true;
                sprintHeldLong = false;
            }
        }
        movementInputFromOptions.jump = gameSettings.keyBindJump.isKeyDown();
        if (((Boolean)ToggleSprintMod.toggleSneak.getValue()).booleanValue() && CheatBreaker.getInstance().getModManager().toggleSprintMod.isEnabled()) {
            if (gameSettings.keyBindSneak.isKeyDown() && !sneakKeyDown) {
                if (entityPlayerSP.isRiding() || entityPlayerSP.capabilities.isFlying) {
                    movementInputFromOptions.sneak = true;
                    sneakWhileRidingOrFlying = entityPlayerSP.isRiding();
                } else {
                    movementInputFromOptions.sneak = !movementInputFromOptions.sneak;
                }
                sneakToggleTime = System.currentTimeMillis();
                sneakKeyDown = true;
            }
            if (!gameSettings.keyBindSneak.isKeyDown() && sneakKeyDown) {
                if (entityPlayerSP.capabilities.isFlying || sneakWhileRidingOrFlying) {
                    movementInputFromOptions.sneak = false;
                } else if (System.currentTimeMillis() - sneakToggleTime > 300L) {
                    movementInputFromOptions.sneak = false;
                }
                sneakKeyDown = false;
            }
            if (!ToggleSprintInput.isSneakInContainerAllowed()) {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiContainer && movementInputFromOptions.sneak) {
                    sneakReleasedForContainer = true;
                    movementInputFromOptions.sneak = false;
                } else if (sneakReleasedForContainer && !(Minecraft.getMinecraft().currentScreen instanceof GuiContainer)) {
                    sneakReleasedForContainer = false;
                    movementInputFromOptions.sneak = true;
                }
            }
        } else {
            movementInputFromOptions.sneak = gameSettings.keyBindSneak.isKeyDown();
        }
        if (movementInputFromOptions.sneak) {
            movementInputFromOptions.moveStrafe = (float)((double)movementInputFromOptions.moveStrafe * 0.3);
            movementInputFromOptions.moveForward = (float)((double)movementInputFromOptions.moveForward * 0.3);
        }
        boolean bl = (float)entityPlayerSP.getFoodStats().getFoodLevel() > 6.0f || entityPlayerSP.capabilities.isFlying;
        boolean bl2 = !movementInputFromOptions.sneak && !entityPlayerSP.capabilities.isFlying && bl;
        holdToSprint = (Boolean)ToggleSprintMod.toggleSprint.getValue() == false;
        doubleTap = (Boolean)ToggleSprintMod.doubleTap.getValue();
        if ((bl2 || holdToSprint) && gameSettings.keyBindSprint.isKeyDown() && !sprintKeyDown && !entityPlayerSP.capabilities.isFlying && !holdToSprint) {
            sprintToggled = !sprintToggled;
            sprintToggleTime = System.currentTimeMillis();
            sprintKeyDown = true;
            sprintHeldLong = false;
        }
        if ((bl2 || holdToSprint) && !gameSettings.keyBindSprint.isKeyDown() && sprintKeyDown) {
            if (System.currentTimeMillis() - sprintToggleTime > 300L) {
                sprintHeldLong = true;
            }
            sprintKeyDown = false;
        }
        ToggleSprintInput.updateStatusText(movementInputFromOptions, entityPlayerSP, gameSettings);
    }

    public ToggleSprintInput(GameSettings gameSettings) {
        super(gameSettings);
    }

    public void setSprintState(boolean bl, boolean bl2) {
        sprintToggled = bl;
        sprintLocked = bl2;
    }

    public static void updateStatusText(MovementInputFromOptions movementInputFromOptions, EntityPlayerSP entityPlayerSP, GameSettings gameSettings) {
        String string = "";
        boolean bl = entityPlayerSP.capabilities.isFlying;
        boolean bl2 = entityPlayerSP.isRiding();
        boolean bl3 = gameSettings.keyBindSneak.isKeyDown();
        boolean bl4 = gameSettings.keyBindSprint.isKeyDown();
        if (bl) {
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String string2 = string = (Boolean)ToggleSprintMod.flyBoost.getValue() != false && bl4 && entityPlayerSP.capabilities.isCreativeMode ? string + ((String)CheatBreaker.getInstance().getModManager().toggleSprintMod.flyBoostString.getValue()).replaceAll("%BOOST%", decimalFormat.format(ToggleSprintMod.flyBoostAmount.getValue())) : string + CheatBreaker.getInstance().getModManager().toggleSprintMod.flyString.getValue();
        }
        if (bl2) {
            string = string + CheatBreaker.getInstance().getModManager().toggleSprintMod.ridingString.getValue();
        }
        if (movementInputFromOptions.sneak) {
            string = bl ? CheatBreaker.getInstance().getModManager().toggleSprintMod.descendString.getValue().toString() : (bl2 ? CheatBreaker.getInstance().getModManager().toggleSprintMod.dismountString.getValue().toString() : (bl3 ? string + CheatBreaker.getInstance().getModManager().toggleSprintMod.sneakingString.getValue() : string + CheatBreaker.getInstance().getModManager().toggleSprintMod.sneakingToggleString.getValue()));
        } else if (sprintToggled && !bl && !bl2) {
            boolean bl5;
            boolean bl6 = bl5 = sprintHeldLong || holdToSprint || sprintLocked;
            string = bl4 ? string + CheatBreaker.getInstance().getModManager().toggleSprintMod.sprintingHeldString.getValue() : (bl5 ? string + CheatBreaker.getInstance().getModManager().toggleSprintMod.sprintingVanillaString.getValue() : string + CheatBreaker.getInstance().getModManager().toggleSprintMod.sprintingToggleString.getValue());
        }
        statusText = string;
    }

    static {
        sprintToggled = true;
        sprintHeldLong = false;
        sprintLocked = false;
        statusText = "";
        sneakReleasedForContainer = false;
    }

    public static boolean isSneakInContainerAllowed() {
        return !CheatBreaker.getInstance().getModManager().toggleSprintMod.isRestricted() && ToggleSprintMod.sneakInContainer.getBooleanValue();
    }
}
