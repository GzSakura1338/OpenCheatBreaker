package net.minecraft.client.gui.spectator;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayer;
import net.minecraft.client.gui.spectator.categories.TeleportToTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class BaseSpectatorGroup implements ISpectatorMenuView {
   public List<ISpectatorMenuObject> elements = Lists.newArrayList();

   public BaseSpectatorGroup() {
      this.elements.add(new TeleportToPlayer());
      this.elements.add(new TeleportToTeam());
   }

   @Override
   public List<ISpectatorMenuObject> getCommands() {
      return this.elements;
   }

   @Override
   public IChatComponent createChatComponentText() {
      return new ChatComponentText("Press a key to select a command, and again to use it.");
   }
}
