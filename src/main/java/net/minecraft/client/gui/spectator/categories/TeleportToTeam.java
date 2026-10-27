package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TeleportToTeam implements ISpectatorMenuObject, ISpectatorMenuView {
   public List<ISpectatorMenuObject> commands = Lists.newArrayList();

   public TeleportToTeam() {
      Minecraft minecraft = Minecraft.getMinecraft();

      for (ScorePlayerTeam scorePlayerTeam : minecraft.theWorld.getScoreboard().getTeams()) {
         this.commands.add(new TeleportToTeam.TeamSelectionObject(scorePlayerTeam));
      }
   }

   @Override
   public List<ISpectatorMenuObject> getCommands() {
      return this.commands;
   }

   @Override
   public IChatComponent createChatComponentText() {
      return new ChatComponentText("Select a team to teleport to");
   }

   @Override
   public void handleSpectatorMenu(SpectatorMenu menu) {
      menu.selectElement(this);
   }

   @Override
   public IChatComponent getSpectatorName() {
      return new ChatComponentText("Teleport to team member");
   }

   @Override
   public void render(float f, int alpha) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_TEXTURE);
      Gui.drawModalRectWithCustomSizedTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   @Override
   public boolean isEnabled() {
      for (ISpectatorMenuObject spectatorMenuObject : this.commands) {
         if (spectatorMenuObject.isEnabled()) {
            return true;
         }
      }

      return false;
   }

   class TeamSelectionObject implements ISpectatorMenuObject {
      public ScorePlayerTeam team;
      public List<NetworkPlayerInfo> scoreboardEntries;
      public ResourceLocation skinId;

      public TeamSelectionObject(ScorePlayerTeam scorePlayerTeam) {
         this.team = scorePlayerTeam;
         this.scoreboardEntries = Lists.newArrayList();

         for (String str : scorePlayerTeam.getMembershipCollection()) {
            NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(str);
            if (playerInfo != null) {
               this.scoreboardEntries.add(playerInfo);
            }
         }

         if (!this.scoreboardEntries.isEmpty()) {
            String name = this.scoreboardEntries.get(new Random().nextInt(this.scoreboardEntries.size())).getGameProfile().getName();
            this.skinId = AbstractClientPlayer.getLocationSkin(name);
            AbstractClientPlayer.getDownloadImageSkin(this.skinId, name);
         } else {
            this.skinId = DefaultPlayerSkin.getDefaultSkinLegacy();
         }
      }

      @Override
      public void handleSpectatorMenu(SpectatorMenu menu) {
         menu.selectElement(new TeleportToPlayer(this.scoreboardEntries));
      }

      @Override
      public IChatComponent getSpectatorName() {
         return new ChatComponentText(this.team.getTeamName());
      }

      @Override
      public void render(float f, int alpha) {
         int n = -1;
         String formatFromString = FontRenderer.getFormatFromString(this.team.getColorPrefix());
         if (formatFromString.length() >= 2) {
            n = Minecraft.getMinecraft().fontRendererObj.getColorCode(formatFromString.charAt(1));
         }

         if (n >= 0) {
            float f2 = (n >> 16 & 0xFF) / 255.0F;
            float f3 = (n >> 8 & 0xFF) / 255.0F;
            float f4 = (n & 0xFF) / 255.0F;
            Gui.drawRect(1, 1, 15, 15, MathHelper.packRgb(f2 * f, f3 * f, f4 * f) | alpha << 24);
         }

         Minecraft.getMinecraft().getTextureManager().bindTexture(this.skinId);
         GlStateManager.color(f, f, f, alpha / 255.0F);
         Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
         Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
      }

      @Override
      public boolean isEnabled() {
         return !this.scoreboardEntries.isEmpty();
      }
   }
}
