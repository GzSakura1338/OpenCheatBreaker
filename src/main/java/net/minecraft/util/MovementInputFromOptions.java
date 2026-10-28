package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
    public GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    @Override
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;
        if (this.gameSettings.keyBindForward.getIsKeyPressed()) {
            this.moveForward++;
        }

        if (this.gameSettings.keyBindBack.getIsKeyPressed()) {
            this.moveForward--;
        }

        if (this.gameSettings.keyBindLeft.getIsKeyPressed()) {
            this.moveStrafe++;
        }

        if (this.gameSettings.keyBindRight.getIsKeyPressed()) {
            this.moveStrafe--;
        }

        this.jump = this.gameSettings.keyBindJump.getIsKeyPressed();
        this.sneak = this.gameSettings.keyBindSneak.getIsKeyPressed();
        if (this.sneak) {
            this.moveStrafe = (float)(this.moveStrafe * 0.3);
            this.moveForward = (float)(this.moveForward * 0.3);
        }
    }
}
