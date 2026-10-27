package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings.GameType;

public class TeleportToPlayer implements ISpectatorMenuObject, ISpectatorMenuView {
   public static Ordering<NetworkPlayerInfo> ORDERING = Ordering.from(new Comparator<NetworkPlayerInfo>() {
      public int compare(NetworkPlayerInfo networkPlayerInfo, NetworkPlayerInfo networkPlayerInfo2) {
         return ComparisonChain.start().compare(networkPlayerInfo.getGameProfile().getId(), networkPlayerInfo2.getGameProfile().getId()).result();
      }
   });
   public List<ISpectatorMenuObject> elements = Lists.newArrayList();

   public TeleportToPlayer() {
      this(ORDERING.sortedCopy(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()));
   }

   public TeleportToPlayer(Collection<NetworkPlayerInfo> collection) {
      for (NetworkPlayerInfo networkPlayerInfo : ORDERING.sortedCopy(collection)) {
         if (networkPlayerInfo.getGameType() != GameType.SPECTATOR) {
            this.elements.add(new PlayerMenuObject(networkPlayerInfo.getGameProfile()));
         }
      }
   }

   @Override
   public List<ISpectatorMenuObject> getCommands() {
      return this.elements;
   }

   @Override
   public IChatComponent createChatComponentText() {
      return new ChatComponentText("Select a player to teleport to");
   }

   @Override
   public void handleSpectatorMenu(SpectatorMenu menu) {
      menu.selectElement(this);
   }

   @Override
   public IChatComponent getSpectatorName() {
      return new ChatComponentText("Teleport to player");
   }

   @Override
   public void render(float f, int alpha) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_TEXTURE);
      Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   @Override
   public boolean isEnabled() {
      return !this.elements.isEmpty();
   }
}
