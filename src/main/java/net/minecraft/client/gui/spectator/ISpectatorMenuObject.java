package net.minecraft.client.gui.spectator;

import net.minecraft.util.IChatComponent;

public interface ISpectatorMenuObject {
   void handleSpectatorMenu(SpectatorMenu spectatorMenu);

   IChatComponent getSpectatorName();

   void render(float f, int n);

   boolean isEnabled();
}
