package net.minecraft.client.gui.spectator.categories;

import com.google.common.base.MoreObjects;
import java.util.List;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;

public class SpectatorDetails {
   public ISpectatorMenuView group;
   public List<ISpectatorMenuObject> commands;
   public int selectedSlot;

   public SpectatorDetails(ISpectatorMenuView spectatorMenuView, List<ISpectatorMenuObject> list, int n) {
      this.group = spectatorMenuView;
      this.commands = list;
      this.selectedSlot = n;
   }

   public ISpectatorMenuObject getCommand(int n) {
      return n >= 0 && n < this.commands.size()
         ? (ISpectatorMenuObject)MoreObjects.firstNonNull(this.commands.get(n), SpectatorMenu.BLANK_COMMAND)
         : SpectatorMenu.BLANK_COMMAND;
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }
}
