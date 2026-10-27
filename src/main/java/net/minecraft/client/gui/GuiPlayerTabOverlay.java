package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.impl.PlayerListMod;
import com.cheatbreaker.client.network.PlayerDataManager;
import com.cheatbreaker.client.render.RenderUtils;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria.EnumRenderType;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings.GameType;
import org.lwjgl.opengl.GL11;

public class GuiPlayerTabOverlay extends Gui {
   public static Ordering<NetworkPlayerInfo> ENTRY_ORDERING = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
   public Minecraft mc;
   public GuiIngame guiIngame;
   public IChatComponent footer;
   public IChatComponent header;
   public long lastTimeOpened;
   public boolean isBeingRendered;

   public GuiPlayerTabOverlay(Minecraft mcIn, GuiIngame guiIngameIn) {
      this.mc = mcIn;
      this.guiIngame = guiIngameIn;
   }

   public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
      return networkPlayerInfoIn.getDisplayName() != null
         ? networkPlayerInfoIn.getDisplayName().getFormattedText()
         : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
   }

   public void updatePlayerList(boolean willBeRendered) {
      if (willBeRendered && !this.isBeingRendered) {
         this.lastTimeOpened = Minecraft.getSystemTime();
      }

      this.isBeingRendered = willBeRendered;
   }

   public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
      NetHandlerPlayClient sendQueue = this.mc.thePlayer.sendQueue;
      List sortedCopy = ENTRY_ORDERING.sortedCopy(sendQueue.getPlayerInfoMap());
      int n = 0;
      int n2 = 0;

      for (NetworkPlayerInfo networkPlayerInfo : (Iterable<NetworkPlayerInfo>)sortedCopy) {
         int stringWidth = this.mc.fontRendererObj.getStringWidth(this.getPlayerName(networkPlayerInfo));
         n = Math.max(n, stringWidth);
         if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != EnumRenderType.HEARTS) {
            stringWidth = this.mc
               .fontRendererObj
               .getStringWidth(" " + scoreboardIn.getValueFromObjective(networkPlayerInfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
            n2 = Math.max(n2, stringWidth);
         }
      }

      sortedCopy = sortedCopy.subList(0, Math.min(sortedCopy.size(), 80));
      int size = sortedCopy.size();
      int n3 = size;

      int n4;
      for (n4 = 1; n3 > 20; n3 = (size + n4 - 1) / n4) {
         n4++;
      }

      boolean flag = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
      int n5;
      if (scoreObjectiveIn != null) {
         if (scoreObjectiveIn.getRenderType() == EnumRenderType.HEARTS) {
            n5 = 90;
         } else {
            n5 = n2;
         }
      } else {
         n5 = 0;
      }

      int n6 = Math.min(n4 * ((flag ? 9 : 0) + n + n5 + 13), width - 50) / n4;
      int n7 = width / 2 - (n6 * n4 + (n4 - 1) * 5) / 2;
      int n8 = 10;
      int n9 = n6 * n4 + (n4 - 1) * 5;
      List list = null;
      List list2 = null;
      if (this.header != null) {
         list = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);

         for (String str : (Iterable<String>)list) {
            n9 = Math.max(n9, this.mc.fontRendererObj.getStringWidth(str));
         }
      }

      if (this.footer != null) {
         list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);

         for (String str : (Iterable<String>)list2) {
            n9 = Math.max(n9, this.mc.fontRendererObj.getStringWidth(str));
         }
      }

      if (list != null) {
         drawRect(
            width / 2 - n9 / 2 - 1, n8 - 1, width / 2 + n9 / 2 + 1, n8 + list.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE
         );

         for (String str : (Iterable<String>)list) {
            int stringWidth = this.mc.fontRendererObj.getStringWidth(str);
            this.mc.fontRendererObj.drawStringWithShadow(str, width / 2 - stringWidth / 2, n8, -1);
            n8 += this.mc.fontRendererObj.FONT_HEIGHT;
         }

         n8++;
      }

      drawRect(width / 2 - n9 / 2 - 1, n8 - 1, width / 2 + n9 / 2 + 1, n8 + n3 * 9, Integer.MIN_VALUE);

      for (int i = 0; i < size; i++) {
         int n10 = i / n3;
         int n11 = i % n3;
         int n12 = n7 + n10 * n6 + n10 * 5;
         int n13 = n8 + n11 * 9;
         drawRect(n12, n13, n12 + n6, n13 + 8, 553648127);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         if (i < sortedCopy.size()) {
            NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)sortedCopy.get(i);
            String playerName = this.getPlayerName(networkPlayerInfo);
            GameProfile gameProfile = networkPlayerInfo.getGameProfile();
            if (flag) {
               EntityPlayer playerEntityByUUID = this.mc.theWorld.getPlayerEntityByUUID(gameProfile.getId());
               boolean flag2 = playerEntityByUUID != null
                  && playerEntityByUUID.isWearing(EnumPlayerModelParts.CAPE)
                  && (gameProfile.getName().equals("Dinnerbone") || gameProfile.getName().equals("Grumm"));
               this.mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
               int n14 = 8 + (flag2 ? 8 : 0);
               int n15 = 8 * (flag2 ? -1 : 1);
               Gui.drawScaledCustomSizeModalRect(n12, n13, 8.0F, n14, 8, n15, 8, 8, 64.0F, 64.0F);
               if (playerEntityByUUID != null && playerEntityByUUID.isWearing(EnumPlayerModelParts.HAT)) {
                  int n16 = 8 + (flag2 ? 8 : 0);
                  int n17 = 8 * (flag2 ? -1 : 1);
                  Gui.drawScaledCustomSizeModalRect(n12, n13, 40.0F, n16, 8, n17, 8, 8, 64.0F, 64.0F);
               }

               n12 += 9;
            }

            if (networkPlayerInfo.getGameType() == GameType.SPECTATOR) {
               playerName = EnumChatFormatting.ITALIC + playerName;
               this.mc.fontRendererObj.drawStringWithShadow(playerName, n12, n13, -1862270977);
            } else {
               this.mc.fontRendererObj.drawStringWithShadow(playerName, n12, n13, -1);
            }

            if (scoreObjectiveIn != null && networkPlayerInfo.getGameType() != GameType.SPECTATOR) {
               int n14 = n12 + n + 1;
               int n15 = n14 + n5;
               if (n15 - n14 > 5) {
                  this.drawScoreboardValues(scoreObjectiveIn, n13, gameProfile.getName(), n14, n15, networkPlayerInfo);
               }
            }

            this.drawPing(n6, n12 - (flag ? 9 : 0), n13, networkPlayerInfo);
         }
      }

      if (list2 != null) {
         n8 = n8 + n3 * 9 + 1;
         drawRect(
            width / 2 - n9 / 2 - 1, n8 - 1, width / 2 + n9 / 2 + 1, n8 + list2.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE
         );

         for (String str : (Iterable<String>)list2) {
            int stringWidth = this.mc.fontRendererObj.getStringWidth(str);
            this.mc.fontRendererObj.drawStringWithShadow(str, width / 2 - stringWidth / 2, n8, -1);
            n8 += this.mc.fontRendererObj.FONT_HEIGHT;
         }
      }
   }

   public void drawPing(int n, int n2, int n3, NetworkPlayerInfo networkPlayerInfoIn) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
      byte b = 0;
      byte b2 = 0;
      if (networkPlayerInfoIn.getResponseTime() < 0) {
         b2 = 5;
      } else if (networkPlayerInfoIn.getResponseTime() < 150) {
         b2 = 0;
      } else if (networkPlayerInfoIn.getResponseTime() < 300) {
         b2 = 1;
      } else if (networkPlayerInfoIn.getResponseTime() < 600) {
         b2 = 2;
      } else if (networkPlayerInfoIn.getResponseTime() < 1000) {
         b2 = 3;
      } else {
         b2 = 4;
      }

      zLevel += 100.0F;
      this.drawTexturedModalRect(n2 + n - 11, n3, 0 + b * 10, 176 + b2 * 8, 10, 8);
      zLevel -= 100.0F;
   }

   public void drawScoreboardValues(ScoreObjective scoreObjective, int n, String str, int n2, int n3, NetworkPlayerInfo networkPlayerInfo) {
      int scorePoints = scoreObjective.getScoreboard().getValueFromObjective(str, scoreObjective).getScorePoints();
      if (scoreObjective.getRenderType() == EnumRenderType.HEARTS) {
         this.mc.getTextureManager().bindTexture(icons);
         if (this.lastTimeOpened == networkPlayerInfo.getShowTime()) {
            if (scorePoints < networkPlayerInfo.getLastHealth()) {
               networkPlayerInfo.setLastHealthTime(Minecraft.getSystemTime());
               networkPlayerInfo.setBlinkingHeartTime(this.guiIngame.getUpdateCounter() + 20);
            } else if (scorePoints > networkPlayerInfo.getLastHealth()) {
               networkPlayerInfo.setLastHealthTime(Minecraft.getSystemTime());
               networkPlayerInfo.setBlinkingHeartTime(this.guiIngame.getUpdateCounter() + 10);
            }
         }

         if (Minecraft.getSystemTime() - networkPlayerInfo.getLastHealthTime() > 1000L || this.lastTimeOpened != networkPlayerInfo.getShowTime()) {
            networkPlayerInfo.setLastHealth(scorePoints);
            networkPlayerInfo.setHealth(scorePoints);
            networkPlayerInfo.setLastHealthTime(Minecraft.getSystemTime());
         }

         networkPlayerInfo.setShowTime(this.lastTimeOpened);
         networkPlayerInfo.setLastHealth(scorePoints);
         int n4 = MathHelper.ceiling_float_int(Math.max(scorePoints, networkPlayerInfo.getHealth()) / 2.0F);
         int max = Math.max(MathHelper.ceiling_float_int(scorePoints / 2), Math.max(MathHelper.ceiling_float_int(networkPlayerInfo.getHealth() / 2), 10));
         boolean flag = networkPlayerInfo.getBlinkingHeartTime() > this.guiIngame.getUpdateCounter()
            && (networkPlayerInfo.getBlinkingHeartTime() - this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;
         if (n4 > 0) {
            float min = Math.min((float)(n3 - n2 - 4) / max, 9.0F);
            if (min > 3.0F) {
               for (int i = n4; i < max; i++) {
                  this.drawTexturedModalRect(n2 + i * min, n, flag ? 25 : 16, 0, 9, 9);
               }

               for (int i = 0; i < n4; i++) {
                  this.drawTexturedModalRect(n2 + i * min, n, flag ? 25 : 16, 0, 9, 9);
                  if (flag) {
                     if (i * 2 + 1 < networkPlayerInfo.getHealth()) {
                        this.drawTexturedModalRect(n2 + i * min, n, 70, 0, 9, 9);
                     }

                     if (i * 2 + 1 == networkPlayerInfo.getHealth()) {
                        this.drawTexturedModalRect(n2 + i * min, n, 79, 0, 9, 9);
                     }
                  }

                  if (i * 2 + 1 < scorePoints) {
                     this.drawTexturedModalRect(n2 + i * min, n, i >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (i * 2 + 1 == scorePoints) {
                     this.drawTexturedModalRect(n2 + i * min, n, i >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float f = MathHelper.clamp_float(scorePoints / 20.0F, 0.0F, 1.0F);
               int n5 = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
               String str2 = "" + scorePoints / 2.0F;
               if (n3 - this.mc.fontRendererObj.getStringWidth(str2 + "hp") >= n2) {
                  str2 = str2 + "hp";
               }

               this.mc.fontRendererObj.drawStringWithShadow(str2, (n3 + n2) / 2 - this.mc.fontRendererObj.getStringWidth(str2) / 2, n, n5);
            }
         }
      } else {
         String str2 = EnumChatFormatting.YELLOW + "" + scorePoints;
         this.mc.fontRendererObj.drawStringWithShadow(str2, n3 - this.mc.fontRendererObj.getStringWidth(str2), n, 16777215);
      }
   }

   public void setFooter(IChatComponent footerIn) {
      this.footer = footerIn;
   }

   public void setHeader(IChatComponent headerIn) {
      this.header = headerIn;
   }

   public void resetFooterHeader() {
      this.header = null;
      this.footer = null;
   }

   public void render(int n, Scoreboard scoreboard, ScoreObjective scoreObjective) {
      PlayerListMod playerListMod = CheatBreaker.getInstance().getModManager().playerListMod;
      boolean booleanValue = playerListMod.showBackground.getBooleanValue();
      boolean booleanValue2 = playerListMod.showRowBackground.getBooleanValue();
      boolean booleanValue3 = playerListMod.showPlayerHeads.getBooleanValue();
      boolean booleanValue4 = playerListMod.showHeader.getBooleanValue();
      boolean booleanValue5 = playerListMod.showFooter.getBooleanValue();
      boolean booleanValue6 = playerListMod.nameTextShadow.getBooleanValue();
      boolean booleanValue7 = playerListMod.headerTextShadow.getBooleanValue();
      boolean booleanValue8 = playerListMod.footerTextShadow.getBooleanValue();
      PlayerDataManager playerDataManager = CheatBreaker.getInstance().getPlayerDataManager();
      NetHandlerPlayClient sendQueue = this.mc.thePlayer.sendQueue;
      List sortedCopy = ENTRY_ORDERING.sortedCopy(sendQueue.getPlayerInfoMap());
      int n2 = 0;
      int n3 = 0;

      for (NetworkPlayerInfo networkPlayerInfo : (Iterable<NetworkPlayerInfo>)sortedCopy) {
         String playerName = this.getPlayerName(networkPlayerInfo);
         int stringWidth = this.mc.fontRendererObj.getStringWidth(playerName);
         n2 = Math.max(n2, stringWidth);
         if (scoreObjective != null && scoreObjective.getRenderType() != EnumRenderType.HEARTS) {
            stringWidth = this.mc.fontRendererObj.getStringWidth(" " + scoreboard.getValueFromObjective(networkPlayerInfo.getGameProfile().getName(), scoreObjective).getScorePoints());
            n3 = Math.max(n3, stringWidth);
         }
      }

      sortedCopy = sortedCopy.subList(0, Math.min(sortedCopy.size(), 80));
      int size = sortedCopy.size();
      int n4 = size;

      int n5;
      for (n5 = 1; n4 > 20; n4 = (size + n5 - 1) / n5) {
         n5++;
      }

      boolean flag = (this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted()) && booleanValue3;
      int n6;
      if (scoreObjective != null) {
         if (scoreObjective.getRenderType() == EnumRenderType.HEARTS) {
            n6 = 90;
         } else {
            n6 = n3;
         }
      } else {
         n6 = 0;
      }

      int n7 = Math.min(n5 * ((flag ? 9 : 0) + n2 + n6 + 13), n - 50) / n5;
      int n8 = n / 2 - (n7 * n5 + (n5 - 1) * 5) / 2;
      int n9 = 10;
      int n10 = n7 * n5 + (n5 - 1) * 5;
      List list = null;
      List list2 = null;
      if (this.header != null && booleanValue4) {
         list = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), n - 50);

         for (String str : (Iterable<String>)list) {
            n10 = Math.max(n10, this.mc.fontRendererObj.getStringWidth(str));
         }
      }

      if (this.footer != null && booleanValue5) {
         list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), n - 50);

         for (String str : (Iterable<String>)list2) {
            n10 = Math.max(n10, this.mc.fontRendererObj.getStringWidth(str));
         }
      }

      int n11 = 300 / n4;
      if (n11 > 150) {
         short s = 150;
      }

      if ((list != null || list2 != null) && (booleanValue4 || booleanValue5)) {
         GlStateManager.translate(-(n / 2 - n10 / 2 - 1), -n9 + 1, 0.0F);
      } else {
         GlStateManager.translate(-n2 + 1 + -n8 + n2, -n9 + 1, 0.0F);
      }

      if (list != null && booleanValue4) {
         if (booleanValue) {
            drawRect(
               n / 2 - n10 / 2 - 1,
               n9 - 1,
               n / 2 + n10 / 2 + 2,
               n9 + list.size() * this.mc.fontRendererObj.FONT_HEIGHT,
               playerListMod.headerBackgroundColor.getColorValue()
            );
         }

         for (String str : (Iterable<String>)list) {
            int stringWidth = this.mc.fontRendererObj.getStringWidth(str);
            this.mc.fontRendererObj.drawString(str, n / 2 - stringWidth / 2, n9, -1, booleanValue7);
            n9 += this.mc.fontRendererObj.FONT_HEIGHT;
         }

         n9++;
      }

      if (booleanValue) {
         drawRect(n / 2 - n10 / 2 - 1, n9 - 1, n / 2 + n10 / 2 + 2, n9 + n4 * 9, Integer.MIN_VALUE);
      }

      for (int i = 0; i < size; i++) {
         int n12 = i / n4;
         int n13 = i % n4;
         int n14 = n8 + n12 * n7 + n12 * 5;
         int n15 = n9 + n13 * 9;
         int colorValue = playerListMod.rowBackgroundColor.getColorValue();
         if (i < sortedCopy.size()) {
            NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)sortedCopy.get(i);
            if (networkPlayerInfo.getResponseTime() < -1) {
               colorValue = playerListMod.lowPingColor.getColorValue();
            } else if (networkPlayerInfo.getResponseTime() < 150) {
               colorValue = playerListMod.lowPingColor.getColorValue();
            } else if (networkPlayerInfo.getResponseTime() < 300) {
               colorValue = playerListMod.mediumPingColor.getColorValue();
            } else if (networkPlayerInfo.getResponseTime() < 600) {
               colorValue = playerListMod.highPingColor.getColorValue();
            } else if (networkPlayerInfo.getResponseTime() < 1000) {
               colorValue = playerListMod.extremelyHighPingColor.getColorValue();
            } else {
               colorValue = playerListMod.rowBackgroundColor.getColorValue();
            }
         }

         int min = Math.min(playerListMod.rowBackgroundColor.getColorValue() >> 24 & 0xFF, colorValue >> 24 & 0xFF);
         int n16 = colorValue >> 16 & 0xFF;
         int n17 = colorValue >> 8 & 0xFF;
         int n18 = colorValue & 0xFF;
         if (i < sortedCopy.size()) {
            NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)sortedCopy.get(i);
            String playerName = this.getPlayerName(networkPlayerInfo);
            if (booleanValue2) {
               if ((
                     playerName.contains(this.mc.getSession().getUsername())
                        || playerName.contains(CheatBreaker.getInstance().getModManager().nickHiderMod.customNameString.getStringValue())
                  )
                  && (Boolean)playerListMod.highlightOwnRow.getValue()) {
                  drawRect(n14, n15, n14 + n7, n15 + 8, playerListMod.selfRowColor.getColorValue());
               } else if (playerDataManager.checkString(playerName, true) && (Boolean)playerListMod.highlightCheatbreakerPlayersRow.getValue()) {
                  drawRect(n14, n15, n14 + n7, n15 + 8, playerListMod.cheatbreakerRowColor.getColorValue());
               } else {
                  drawRect(
                     n14,
                     n15,
                     n14 + n7,
                     n15 + 8,
                     (boolean)playerListMod.usePingColorForRowBackground.getValue() ? new Color(n16, n17, n18, min).getRGB() : playerListMod.rowBackgroundColor.getColorValue()
                  );
               }
            }

            playerListMod.setHudSize(n10 + 2, n9 + (n4 - 1) * 9);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GameProfile gameProfile = networkPlayerInfo.getGameProfile();
            if (flag) {
               EntityPlayer playerEntityByUUID = this.mc.theWorld.getPlayerEntityByUUID(gameProfile.getId());
               boolean flag2 = playerEntityByUUID != null
                  && playerEntityByUUID.isWearing(EnumPlayerModelParts.CAPE)
                  && (gameProfile.getName().equals("Dinnerbone") || gameProfile.getName().equals("Grumm"));
               this.mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
               int n19 = 8 + (flag2 ? 8 : 0);
               int n20 = 8 * (flag2 ? -1 : 1);
               Gui.drawScaledCustomSizeModalRect(n14, n15, 8.0F, n19, 8, n20, 8, 8, 64.0F, 64.0F);
               if (playerEntityByUUID != null && playerEntityByUUID.isWearing(EnumPlayerModelParts.HAT)) {
                  int n21 = 8 + (flag2 ? 8 : 0);
                  int n22 = 8 * (flag2 ? -1 : 1);
                  Gui.drawScaledCustomSizeModalRect(n14, n15, 40.0F, n21, 8, n22, 8, 8, 64.0F, 64.0F);
               }

               n14 += 9;
            }

            int n19 = playerDataManager.findPlayerData(playerName) == null ? 0 : playerDataManager.findPlayerData(playerName).getIntegerList().get(0);
            int n20 = playerDataManager.findPlayerData(playerName) == null ? 0 : playerDataManager.findPlayerData(playerName).getIntegerList().get(1);
            float f = (n19 >> 16 & 0xFF) / 255.0F;
            float f2 = (n19 >> 8 & 0xFF) / 255.0F;
            float f3 = (n19 & 0xFF) / 255.0F;
            float f4 = (n20 >> 16 & 0xFF) / 255.0F;
            float f5 = (n20 >> 8 & 0xFF) / 255.0F;
            float f6 = (n20 & 0xFF) / 255.0F;
            boolean flag2 = (Boolean)playerListMod.showCheatbreakerLogo.getValue() && playerDataManager.checkString(gameProfile.getId().toString(), false);
            float f7 = playerListMod.cheatbreakerLogoType.getBooleanValue() ? 9.7F : 15.7F;
            float f8 = flag2 ? (playerName.contains(" ") ? f7 + 1.0F : f7) : 0.0F;
            if (flag2) {
               if (booleanValue6) {
                  if (playerListMod.cheatbreakerLogoType.getBooleanValue()) {
                     GlStateManager.color(f / 4.0F, f2 / 4.0F, f3 / 4.0F, 1.0F);
                     RenderUtils.drawSquareTextureViaRenderEngine(RenderUtils.logoCircle1Png, 3.6F, n14 + 1.5F, n15 + 0.8F);
                  } else {
                     GlStateManager.color(f / 4.0F, f2 / 4.0F, f3 / 4.0F, 1.0F);
                     RenderUtils.drawTexture(RenderUtils.logoWhite1Png, n14 + 2.0F, n15 + 1.2F, 13.0F, 7.0F);
                     GlStateManager.color(f4 / 4.0F, f5 / 4.0F, f6 / 4.0F, 1.0F);
                     RenderUtils.drawTexture(RenderUtils.logoWhite2Png, n14 + 2.0F, n15 + 1.2F, 13.0F, 7.0F);
                  }
               }

               if (playerListMod.cheatbreakerLogoType.getBooleanValue()) {
                  GlStateManager.color(f, f2, f3, 1.0F);
                  RenderUtils.drawSquareTextureViaRenderEngine(RenderUtils.logoCircle1Png, 3.6F, n14 + 1.0F, n15 + 0.2F);
               } else {
                  GlStateManager.color(f, f2, f3, 1.0F);
                  RenderUtils.drawTexture(RenderUtils.logoWhite1Png, n14 + 1.0F, n15 + 0.2F, 13.0F, 7.0F);
                  GlStateManager.color(f4, f5, f6, 1.0F);
                  RenderUtils.drawTexture(RenderUtils.logoWhite2Png, n14 + 1.0F, n15 + 0.2F, 13.0F, 7.0F);
               }
            }

            if (networkPlayerInfo.getGameType() == GameType.SPECTATOR) {
               playerName = EnumChatFormatting.ITALIC + playerName;
               this.mc.fontRendererObj.drawString(playerName, n14, n15, -1862270977, booleanValue6);
            } else if ((
                  playerName.contains(this.mc.getSession().getUsername())
                     || playerName.contains(CheatBreaker.getInstance().getModManager().nickHiderMod.customNameString.getStringValue())
               )
               && (Boolean)playerListMod.highlightOwnName.getValue()) {
               this.mc.fontRendererObj.drawString(playerListMod.formatText(playerName), f8 + n14, n15, playerListMod.selfTextColor.getColorValue(), booleanValue6);
            } else if (playerDataManager.checkString(playerName, true) && (Boolean)playerListMod.highlightCheatbreakerPlayersName.getValue()) {
               this.mc
                  .fontRendererObj
                  .drawString(EnumChatFormatting.getTextWithoutFormattingCodes(playerName), f8 + n14, n15, playerListMod.cheatbreakerTextColor.getColorValue(), booleanValue6);
            } else {
               this.mc.fontRendererObj.drawString(playerName, f8 + n14, n15, 16777215, booleanValue6);
            }

            if (scoreObjective != null && networkPlayerInfo.getGameType() != GameType.SPECTATOR) {
               int n21 = n14 + n2 + 1;
               int n22 = n21 + n6;
               if (n22 - n21 > 5) {
                  this.drawScoreboardValues(scoreObjective, n15, gameProfile.getName(), n21, n22, networkPlayerInfo);
               }
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int n21;
            if (networkPlayerInfo.getResponseTime() < -1) {
               n21 = playerListMod.lowPingColor.getColorValue();
            } else if (networkPlayerInfo.getResponseTime() < 150) {
               n21 = playerListMod.lowPingColor.getColorValue();
            } else if (networkPlayerInfo.getResponseTime() < 300) {
               n21 = playerListMod.mediumPingColor.getColorValue();
            } else if (networkPlayerInfo.getResponseTime() < 600) {
               n21 = playerListMod.highPingColor.getColorValue();
            } else if (networkPlayerInfo.getResponseTime() < 1000) {
               n21 = playerListMod.extremelyHighPingColor.getColorValue();
            } else {
               n21 = -1;
            }

            if (!(Boolean)playerListMod.dynamicPingNumberColor.getValue()) {
               n21 = playerListMod.staticPingColor.getColorValue();
            }

            if (networkPlayerInfo.getResponseTime() != -1 && (Boolean)playerListMod.showPing.getValue()) {
               if (!(Boolean)playerListMod.showPingAsNumber.getValue()) {
                  this.drawPing(n7, n14 - (flag ? 9 : 0), n15, networkPlayerInfo);
               } else {
                  this.mc
                     .fontRendererObj
                     .drawString(
                        networkPlayerInfo.getResponseTime() + "",
                        (float)n14 + n7 - 9.0F - this.mc.fontRendererObj.getStringWidth(networkPlayerInfo.getResponseTime() + ""),
                        n15,
                        n21,
                        (Boolean)playerListMod.pingTextShadow.getValue()
                     );
               }
            }
         }
      }

      if (list2 != null) {
         n9 = n9 + n4 * 9 + 1;
         if (booleanValue && booleanValue5) {
            drawRect(
               n / 2 - n10 / 2 - 1,
               n9 - 1,
               n / 2 + n10 / 2 + 2,
               n9 + list2.size() * this.mc.fontRendererObj.FONT_HEIGHT,
               playerListMod.footerBackgroundColor.getColorValue()
            );
         }

         for (String str : (Iterable<String>)list2) {
            if (booleanValue5) {
               int stringWidth = this.mc.fontRendererObj.getStringWidth(str);
               this.mc.fontRendererObj.drawString(str, n / 2 - stringWidth / 2, n9, -1, booleanValue8);
               n9 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
         }

         playerListMod.setHudSize(n10 + 2, playerListMod.hudHeight + list2.size() * this.mc.fontRendererObj.FONT_HEIGHT + 1.0F);
      }
   }

   static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
      public PlayerComparator() {
      }

      public int compare(NetworkPlayerInfo networkPlayerInfo, NetworkPlayerInfo networkPlayerInfo2) {
         ScorePlayerTeam playerTeam = networkPlayerInfo.getPlayerTeam();
         ScorePlayerTeam playerTeam2 = networkPlayerInfo2.getPlayerTeam();
         return ComparisonChain.start()
            .compareTrueFirst(networkPlayerInfo.getGameType() != GameType.SPECTATOR, networkPlayerInfo2.getGameType() != GameType.SPECTATOR)
            .compare(playerTeam != null ? playerTeam.getRegisteredName() : "", playerTeam2 != null ? playerTeam2.getRegisteredName() : "")
            .compare(networkPlayerInfo.getGameProfile().getName(), networkPlayerInfo2.getGameProfile().getName())
            .result();
      }
   }
}
