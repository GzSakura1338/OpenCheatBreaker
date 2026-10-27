package com.cheatbreaker.client.network;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.BooleanIntEvent;
import com.cheatbreaker.client.event.CBPacketEvent1;
import com.cheatbreaker.client.event.CBPacketEvent2;
import com.cheatbreaker.client.event.S01PacketJoinGameEvent;
import com.cheatbreaker.client.event.SimpleEvent26;
import com.cheatbreaker.client.event.SimpleEvent3;
import com.cheatbreaker.client.event.StringByteArrayEvent;
import com.cheatbreaker.client.event.WorldClientEvent;
import com.cheatbreaker.client.minimap.MinimapSettings;
import com.cheatbreaker.client.minimap.OldReiWaypointLoader;
import com.cheatbreaker.client.minimap.VoxelMap;
import com.cheatbreaker.client.minimap.Waypoint;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.AbstractNamedMod;
import com.cheatbreaker.client.mod.ModForceState;
import com.cheatbreaker.client.mod.impl.ClickRateLimiter;
import com.cheatbreaker.client.mod.impl.CooldownsMod;
import com.cheatbreaker.client.mod.impl.ScoreboardMod;
import com.cheatbreaker.client.mod.impl.StaffNametagsMod;
import com.cheatbreaker.client.mod.impl.TeamMember;
import com.cheatbreaker.client.mod.impl.TitleType;
import com.cheatbreaker.client.mod.impl.ZansMinimapMod;
import com.cheatbreaker.client.render.WorldLabel;
import com.cheatbreaker.client.title.Title;
import com.cheatbreaker.client.util.Logging;
import com.cheatbreaker.client.util.RateLimiter;
import com.cheatbreaker.client.voice.VoiceChannel;
import com.cheatbreaker.client.voice.VoiceChatPlayer;
import com.cheatbreaker.client.voice.VoiceEngine;
import com.cheatbreaker.nethandler.CBPacket;
import com.cheatbreaker.nethandler.client.CBPacketVoiceMute;
import com.cheatbreaker.nethandler.client.ICBNetHandlerClient;
import com.cheatbreaker.nethandler.server.CBPacketAddHologram;
import com.cheatbreaker.nethandler.server.CBPacketCooldown;
import com.cheatbreaker.nethandler.server.CBPacketDeleteVoiceChannel;
import com.cheatbreaker.nethandler.server.CBPacketNotification;
import com.cheatbreaker.nethandler.server.CBPacketOverrideNametags;
import com.cheatbreaker.nethandler.server.CBPacketRemoveHologram;
import com.cheatbreaker.nethandler.server.CBPacketServerRule;
import com.cheatbreaker.nethandler.server.CBPacketServerUpdate;
import com.cheatbreaker.nethandler.server.CBPacketStaffModState;
import com.cheatbreaker.nethandler.server.CBPacketSwingInhibitorUpdate;
import com.cheatbreaker.nethandler.server.CBPacketTeammates;
import com.cheatbreaker.nethandler.server.CBPacketTitle;
import com.cheatbreaker.nethandler.server.CBPacketUpdateHologram;
import com.cheatbreaker.nethandler.server.CBPacketUpdateNametags;
import com.cheatbreaker.nethandler.server.CBPacketUpdateWorld;
import com.cheatbreaker.nethandler.server.CBPacketVoice;
import com.cheatbreaker.nethandler.server.CBPacketVoiceChannel;
import com.cheatbreaker.nethandler.server.CBPacketVoiceChannelUpdate;
import com.cheatbreaker.nethandler.server.CBPacketWorldBorder;
import com.cheatbreaker.nethandler.server.CBPacketWorldBorderRemove;
import com.cheatbreaker.nethandler.server.CBPacketWorldBorderUpdate;
import com.cheatbreaker.nethandler.shared.CBPacketAddWaypoint;
import com.cheatbreaker.nethandler.shared.CBPacketRemoveWaypoint;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.Unpooled;
import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class ClientNetHandler implements ICBNetHandlerClient {
   public boolean serverHandlesWaypoints;
   public boolean legacyCombat;
   public boolean competitiveGamemode;
   public CheatBreaker cheatBreaker = CheatBreaker.getInstance();
   public List<UUID> mutedPlayers = new ArrayList<>();
   public List<UUID> autoMutedPlayers = new ArrayList<>();
   public Map<UUID, List<String>> nametagOverrides = new HashMap<>();
   public boolean pluginChannelRegistered = false;
   public VoiceChannel voiceChannel;
   public boolean binaryChannelRegistered = false;
   public List<VoiceChannel> voiceChannels;
   public String activeChannel = "";
   public boolean voiceEnabled = false;
   public String worldName = "";
   public boolean forceDisableFastMath;

   public VoiceChannel getVoiceChannel() {
      return this.voiceChannel;
   }

   public void handleUpdateHologram(CBPacketUpdateHologram cbpacketUpdateHologram) {
      WorldLabel.getLabels()
         .stream()
         .filter(worldLabel -> worldLabel.getPlayerId().equals(cbpacketUpdateHologram.getUuid()))
         .forEach(worldLabel -> worldLabel.setLines(cbpacketUpdateHologram.getLines().toArray(new String[0])));
   }

   public void handleAddHologram(CBPacketAddHologram cbpacketAddHologram) {
      WorldLabel worldLabel = new WorldLabel(cbpacketAddHologram.getUuid(), cbpacketAddHologram.getX(), cbpacketAddHologram.getY(), cbpacketAddHologram.getZ());
      WorldLabel.getLabels().add(worldLabel);
      worldLabel.setLines(cbpacketAddHologram.getLines().toArray(new String[0]));
   }

   public void handleTitle(CBPacketTitle cbpacketTitle) {
      TitleType titleType = TitleType.SUBTITLE;
      if (cbpacketTitle.getType().equalsIgnoreCase("subtitle")) {
         titleType = TitleType.TITLE;
      }

      this.cheatBreaker
         .getTitleManager()
         .getObjectArrayList()
         .add(new Title(cbpacketTitle.getMessage(), titleType, cbpacketTitle.getScale(), cbpacketTitle.getDisplayTimeMs(), cbpacketTitle.getFadeInTimeMs(), cbpacketTitle.getFadeOutTimeMs()));
   }

   public void onSimpleEvent26(SimpleEvent26 simpleEvent26) {
      this.binaryChannelRegistered = false;
   }

   public void handleAddWaypoint(CBPacketAddWaypoint cbpacketAddWaypoint) {
      int n = cbpacketAddWaypoint.getX();
      int n2 = cbpacketAddWaypoint.getY();
      int n3 = cbpacketAddWaypoint.getZ();
      VoxelMap voxelMap = this.cheatBreaker.getModManager().zansMinimapMod.getVoxelMap();
      if (voxelMap.getWaypointManager().getWaypoints().stream().noneMatch(waypoint -> waypoint.name.equals(cbpacketAddWaypoint.getName()) && waypoint.world_6.equals(cbpacketAddWaypoint.getWorld()))) {
         Color color = new Color(cbpacketAddWaypoint.getColor());
         float f = color.getRed() / 255.0F;
         float f2 = color.getGreen() / 255.0F;
         float f3 = color.getBlue() / 255.0F;
         TreeSet treeSet = new TreeSet();
         treeSet.add(-1);
         treeSet.add(0);
         treeSet.add(1);
         Logging.debug("Received waypoint (" + cbpacketAddWaypoint.getName() + ")[x" + n + ",y" + n2 + ",z" + n3 + "][r" + f + ",g" + f2 + ",b" + f3 + "]");
         Waypoint waypoint = new Waypoint(
            cbpacketAddWaypoint.getName(), n, n3, n2, true, f, f2, f3, "", voxelMap.getWaypointManager().getCurrentSubworld(), treeSet, true, true
         );
         waypoint.forced = cbpacketAddWaypoint.isForced();
         waypoint.world_6 = cbpacketAddWaypoint.getWorld();
         waypoint.fromServer = true;
         voxelMap.getWaypointManager().addWaypoint(waypoint);
      }
   }

   public void handleWorldBorderRemove(CBPacketWorldBorderRemove cbpacketWorldBorderRemove) {
      this.cheatBreaker.getWorldBorderManager().removeIf(cbpacketWorldBorderRemove.getId());
   }

   public void clearServerData() {
      this.cheatBreaker.getTitleManager().getObjectArrayList().clear();
      StaffNametagsMod.setPlayerTags(null);
   }

   public void handleDeleteVoiceChannel(CBPacketDeleteVoiceChannel cbpacketDeleteVoiceChannel) {
      this.log("Deleted channel: " + cbpacketDeleteVoiceChannel.getUuid().toString());
      if (this.voiceChannels != null) {
         this.voiceChannels.removeIf(voiceChannel -> voiceChannel.getUuid().equals(cbpacketDeleteVoiceChannel.getUuid()));
      }

      if (this.voiceChannel != null && this.voiceChannel.getUuid().equals(cbpacketDeleteVoiceChannel.getUuid())) {
         this.voiceChannel = null;
      }
   }

   public String getWorldName() {
      return this.worldName;
   }

   public void handleUpdateWorld(CBPacketUpdateWorld cbpacketUpdateWorld) {
      this.log("World Update: " + cbpacketUpdateWorld.getWorld());
      this.worldName = cbpacketUpdateWorld.getWorld();
   }

   public Map<UUID, List<String>> getNametagOverrides() {
      return this.nametagOverrides;
   }

   public VoiceChatPlayer getVoicePlayer(UUID uuid) {
      if (this.voiceChannels != null && this.voiceChannel != null) {
         Iterator iterator = this.voiceChannel.getVoiceChatPlayerList().iterator();

         while (iterator.hasNext()) {
            VoiceChatPlayer voiceChatPlayer;
            if ((voiceChatPlayer = (VoiceChatPlayer)iterator.next()).getUuid().equals(uuid)) {
               return voiceChatPlayer;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public void onJoinGame(S01PacketJoinGameEvent s01PacketJoinGameEvent) {
      this.nametagOverrides.clear();
   }

   public boolean isForceDisableFastMath() {
      return this.forceDisableFastMath;
   }

   public boolean isCompetitiveGamemode() {
      return this.competitiveGamemode;
   }

   public void handleVoice(CBPacketVoice cbpacketVoice) {
      this.cheatBreaker.getModManager().voicePushToTalkHandler.handleUUID(cbpacketVoice.getUuid());
      VoiceEngine.getInstance().play(cbpacketVoice.getUuid(), cbpacketVoice.getData());
   }

   public boolean isBinaryChannelRegistered() {
      return this.binaryChannelRegistered;
   }

   public CheatBreaker getCheatBreaker() {
      return this.cheatBreaker;
   }

   public void handleTeammates(CBPacketTeammates cbpacketTeammates) {
      Map players = cbpacketTeammates.getPlayers();
      UUID leader = cbpacketTeammates.getLeader();
      long lastMs = cbpacketTeammates.getLastMs();
      if ((Boolean)this.cheatBreaker.getKeybindSettings().enableTeamView.getValue()
         && players != null
         && !players.isEmpty()
         && (players.size() != 1 || !players.containsKey(Minecraft.getMinecraft().thePlayer.getUniqueID()))) {
         int n = 0;

         for (Entry entry : (Iterable<Entry>)players.entrySet()) {
            TeamMember findTeamMember = this.cheatBreaker.getModManager().teamManager.findTeamMember(((UUID)entry.getKey()).toString());
            if (findTeamMember == null) {
               findTeamMember = new TeamMember(((UUID)entry.getKey()).toString(), leader != null && leader.equals(entry.getKey()));
               this.cheatBreaker.getModManager().teamManager.getTeamMemberList().add(findTeamMember);
               Random random = new Random();
               if (n < this.cheatBreaker.getModManager().teamManager.getIntArray().length) {
                  findTeamMember.setColor(new Color(this.cheatBreaker.getModManager().teamManager.getIntArray()[n]));
               } else {
                  float nextFloat = random.nextFloat();
                  float nextFloat2 = random.nextFloat();
                  float f = random.nextFloat() / 2.0F;
                  findTeamMember.setColor(new Color(nextFloat, nextFloat2, f));
               }
            }

            try {
               double d = (Double)((Map)entry.getValue()).get("x");
               double d2 = (Double)((Map)entry.getValue()).get("y") + 2.0;
               double d3 = (Double)((Map)entry.getValue()).get("z");
               findTeamMember.process(d, d2, d3, lastMs);
            } catch (Exception e) {
               e.printStackTrace();
            }

            n++;
         }

         this.cheatBreaker.getModManager().teamManager.getTeamMemberList().removeIf(teamMember -> !players.containsKey(UUID.fromString(teamMember.getPlayerId())));
      } else {
         this.cheatBreaker.getModManager().teamManager.getTeamMemberList().clear();
      }
   }

   public void handleOverrideNametags(CBPacketOverrideNametags cbpacketOverrideNametags) {
      if (cbpacketOverrideNametags.getTags() == null) {
         this.nametagOverrides.remove(cbpacketOverrideNametags.getPlayer());
      } else {
         Collections.reverse(cbpacketOverrideNametags.getTags());
         this.nametagOverrides.put(cbpacketOverrideNametags.getPlayer(), cbpacketOverrideNametags.getTags());
      }
   }

   public boolean isPluginChannelRegistered() {
      return this.pluginChannelRegistered;
   }

   public String getActiveChannel() {
      return this.activeChannel;
   }

   public void handleVoiceChannelUpdate(CBPacketVoiceChannelUpdate cbpacketVoiceChannelUpdate) {
      this.log("Channel Update: " + cbpacketVoiceChannelUpdate.getName() + " (" + cbpacketVoiceChannelUpdate.getStatus() + ")");
      if (this.voiceChannels != null) {
         VoiceChannel voiceChannel = this.getVoiceChannel(cbpacketVoiceChannelUpdate.getChannelUuid());
         if (voiceChannel == null) {
            this.log(cbpacketVoiceChannelUpdate.getChannelUuid().toString());
         } else {
            switch (cbpacketVoiceChannelUpdate.getStatus()) {
               case 0:
                  VoiceChatPlayer createVoiceChatPlayer = voiceChannel.createVoiceChatPlayer(cbpacketVoiceChannelUpdate.getUuid(), cbpacketVoiceChannelUpdate.getName());
                  if (createVoiceChatPlayer != null) {
                     this.muteMissingPlayers(ImmutableList.of(createVoiceChatPlayer));
                  }
                  break;
               case 1:
                  voiceChannel.removeIf(cbpacketVoiceChannelUpdate.getUuid());
                  break;
               case 2:
                  if (!cbpacketVoiceChannelUpdate.getUuid().toString().equals(Minecraft.getMinecraft().getSession().getPlayerID())) {
                     if (this.voiceChannel == voiceChannel) {
                        ChatComponentText chatComponentText = new ChatComponentText(
                           EnumChatFormatting.AQUA
                              + cbpacketVoiceChannelUpdate.getName()
                              + EnumChatFormatting.AQUA
                              + " joined "
                              + voiceChannel.getText()
                              + " channel. Press '"
                              + Keyboard.getKeyName(this.cheatBreaker.getKeybindSettings().openVoiceMenu.getKeyCode())
                              + "'!"
                              + EnumChatFormatting.RESET
                        );
                        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(chatComponentText);
                     }
                  } else {
                     this.voiceChannel = voiceChannel;

                     for (VoiceChannel voiceChannel2 : this.voiceChannels) {
                        voiceChannel2.removeIf2(cbpacketVoiceChannelUpdate.getUuid());
                     }

                     ChatComponentText chatComponentText = new ChatComponentText(
                        EnumChatFormatting.AQUA
                           + "Joined "
                           + voiceChannel.getText()
                           + " channel. Press '"
                           + Keyboard.getKeyName(this.cheatBreaker.getKeybindSettings().voiceChat.getKeyCode())
                           + "' to talk!"
                           + EnumChatFormatting.RESET
                     );
                     Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(chatComponentText);
                  }

                  voiceChannel.handleUUID(cbpacketVoiceChannelUpdate.getUuid(), cbpacketVoiceChannelUpdate.getName());
                  break;
               case 3:
                  if (this.voiceChannel == voiceChannel && !cbpacketVoiceChannelUpdate.getUuid().toString().equals(Minecraft.getMinecraft().getSession().getPlayerID())) {
                     ChatComponentText chatComponentText = new ChatComponentText(
                        EnumChatFormatting.AQUA
                           + cbpacketVoiceChannelUpdate.getName()
                           + EnumChatFormatting.AQUA
                           + " left "
                           + voiceChannel.getText()
                           + " channel. Press '"
                           + Keyboard.getKeyName(this.cheatBreaker.getKeybindSettings().openVoiceMenu.getKeyCode())
                           + "'!"
                           + EnumChatFormatting.RESET
                     );
                     Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(chatComponentText);
                  }

                  voiceChannel.removeIf2(cbpacketVoiceChannelUpdate.getUuid());
            }
         }
      }
   }

   public boolean hasVoiceChannel(UUID uuid) {
      return this.getVoiceChannel(uuid) != null;
   }

   public void handleRemoveWaypoint(CBPacketRemoveWaypoint cbpacketRemoveWaypoint) {
      try {
         String name = cbpacketRemoveWaypoint.getName();
         VoxelMap voxelMap = this.cheatBreaker.getModManager().zansMinimapMod.getVoxelMap();
         voxelMap.getWaypointManager()
            .getWaypoints()
            .removeIf(waypoint -> waypoint.world_6.equals(cbpacketRemoveWaypoint.getWorld()) && waypoint.deathpoint && waypoint.name.equalsIgnoreCase(name));
         ((OldReiWaypointLoader)voxelMap.getWaypointManager())
            .getDeathpoints()
            .removeIf(waypoint -> waypoint.world_6.equals(cbpacketRemoveWaypoint.getWorld()) && waypoint.deathpoint && waypoint.name.equalsIgnoreCase(name));
         ((OldReiWaypointLoader)voxelMap.getWaypointManager())
            .getEntityWaypointContainer()
            .waypoints
            .removeIf(waypoint -> waypoint.world_6.equals(cbpacketRemoveWaypoint.getWorld()) && waypoint.deathpoint && waypoint.name.equalsIgnoreCase(name));
         MinimapSettings.instance.save();
         voxelMap.getWaypointManager().updateDeathpoints();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void handleServerRule(CBPacketServerRule cbpacketServerRule) {
      switch (cbpacketServerRule.getRule()) {
         case VOICE_ENABLED:
            this.log("Voice is: " + (cbpacketServerRule.isBooleanValue() ? "enabled" : "disabled"));
            this.voiceEnabled = cbpacketServerRule.isBooleanValue();
            break;
         case MINIMAP_STATUS:
            switch (cbpacketServerRule.getStringValue()) {
               case "NEUTRAL":
                  ZansMinimapMod.modForceState = ModForceState.NEUTRAL;
                  return;
               case "FORCED_OFF":
                  ZansMinimapMod.modForceState = ModForceState.FORCED_OFF;
                  return;
               default:
                  return;
            }
         case SERVER_HANDLES_WAYPOINTS:
            this.serverHandlesWaypoints = cbpacketServerRule.isBooleanValue();
            break;
         case COMPETITIVE_GAMEMODE:
            this.competitiveGamemode = cbpacketServerRule.isBooleanValue();
            break;
         case LEGACY_COMBAT:
            this.legacyCombat = cbpacketServerRule.isBooleanValue();
            break;
         case FORCE_DISABLE_FAST_MATH:
            this.forceDisableFastMath = cbpacketServerRule.isBooleanValue();
      }
   }

   public void reset() {
      this.voiceEnabled = false;
      this.serverHandlesWaypoints = false;
      this.voiceChannels = null;
      this.voiceChannel = null;
      this.competitiveGamemode = false;
      this.forceDisableFastMath = false;
      this.legacyCombat = false;
      this.worldName = "";
      this.autoMutedPlayers.clear();

      for (AbstractNamedMod abstractNamedMod : this.cheatBreaker.getModManager().abstractNamedModList) {
         abstractNamedMod.run();
      }

      this.cheatBreaker.getWorldBorderManager().clear();
      this.clearServerData();
      ZansMinimapMod.modForceState = ModForceState.FORCED_OFF;
      ScoreboardMod.modForceState = ModForceState.NEUTRAL;
      this.cheatBreaker.getModManager().teamManager.getTeamMemberList().clear();
      WorldLabel.getLabels().clear();
      VoxelMap voxelMap = this.cheatBreaker.getModManager().zansMinimapMod.getVoxelMap();
      if (Minecraft.getMinecraft().thePlayer != null && voxelMap.getWaypointManager() != null) {
         voxelMap.getWaypointManager().getWaypoints().removeIf(waypoint -> waypoint.fromServer);
         ((OldReiWaypointLoader)voxelMap.getWaypointManager()).getDeathpoints().removeIf(waypoint -> waypoint.fromServer);
         if (((OldReiWaypointLoader)voxelMap.getWaypointManager()).getEntityWaypointContainer() != null) {
            ((OldReiWaypointLoader)voxelMap.getWaypointManager()).getEntityWaypointContainer().waypoints.removeIf(waypoint -> waypoint.fromServer);
            MinimapSettings.instance.save();
            voxelMap.getWaypointManager().updateDeathpoints();
         }
      }
   }

   public void handleWorldBorderUpdate(CBPacketWorldBorderUpdate cbpacketWorldBorderUpdate) {
      this.cheatBreaker
         .getWorldBorderManager()
         .handleString(cbpacketWorldBorderUpdate.getId(), cbpacketWorldBorderUpdate.getMinX(), cbpacketWorldBorderUpdate.getMinZ(), cbpacketWorldBorderUpdate.getMaxX(), cbpacketWorldBorderUpdate.getMaxZ(), cbpacketWorldBorderUpdate.getDurationTicks());
   }

   public List<VoiceChannel> getVoiceChannels() {
      return this.voiceChannels;
   }

   public void onSimpleEvent3(SimpleEvent3 simpleEvent3) {
      this.pluginChannelRegistered = false;
      this.binaryChannelRegistered = false;
      this.activeChannel = "";
   }

   public void onWorldClientEvent(WorldClientEvent worldClientEvent) {
      this.nametagOverrides.clear();
   }

   public void handleWorldBorder(CBPacketWorldBorder cbpacketWorldBorder) {
      this.cheatBreaker
         .getWorldBorderManager()
         .handleString(
            cbpacketWorldBorder.getId(),
            cbpacketWorldBorder.getWorld(),
            cbpacketWorldBorder.getColor(),
            cbpacketWorldBorder.getMinX(),
            cbpacketWorldBorder.getMinZ(),
            cbpacketWorldBorder.getMaxX(),
            cbpacketWorldBorder.getMaxZ(),
            cbpacketWorldBorder.isCanShrinkExpand(),
            cbpacketWorldBorder.isCancelsExit()
         );
   }

   public void sendPacket(CBPacket cbpacket) {
      if (cbpacket != null) {
         this.cheatBreaker.getEventBus().handleEvent(new CBPacketEvent2(cbpacket));
      }

      String str = !this.activeChannel.isEmpty() ? this.activeChannel : this.cheatBreaker.getPluginMessageChannel();
      net.minecraft.network.PacketBuffer packetBuffer = new net.minecraft.network.PacketBuffer(Unpooled.buffer());
      packetBuffer.writeBytes(CBPacket.getPacketData(Objects.requireNonNull(cbpacket)));
      C17PacketCustomPayload c17PacketCustomPayload = new C17PacketCustomPayload(str, packetBuffer);
      Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(c17PacketCustomPayload);
   }

   public void handleVoiceChannels(CBPacketVoiceChannel cbpacketVoiceChannel) {
      this.log("Voice Channel Received: " + cbpacketVoiceChannel.getName());
      this.log("Channel has " + cbpacketVoiceChannel.getPlayers().size() + " members");
      if (!this.hasVoiceChannel(cbpacketVoiceChannel.getUuid())) {
         if (this.voiceChannels == null) {
            this.voiceChannels = new ArrayList<>();
         }

         VoiceChannel voiceChannel = new VoiceChannel(cbpacketVoiceChannel.getUuid(), cbpacketVoiceChannel.getName());
         this.voiceChannels.add(voiceChannel);
         ArrayList list = new ArrayList();

         for (Entry entry : cbpacketVoiceChannel.getPlayers().entrySet()) {
            this.log("Added member [" + (String)entry.getValue() + "]");
            VoiceChatPlayer createVoiceChatPlayer = voiceChannel.createVoiceChatPlayer((UUID)entry.getKey(), (String)entry.getValue());
            if (createVoiceChatPlayer != null) {
               list.add(createVoiceChatPlayer);
            }
         }

         this.muteMissingPlayers(list);

         for (Entry entry : cbpacketVoiceChannel.getListening().entrySet()) {
            this.log("Added listener [" + (String)entry.getValue() + "]");
            voiceChannel.handleUUID((UUID)entry.getKey(), (String)entry.getValue());
         }
      }
   }

   public List<UUID> getMutedPlayers() {
      return this.mutedPlayers;
   }

   public void muteMissingPlayers(List<VoiceChatPlayer> list) {
      for (VoiceChatPlayer voiceChatPlayer : list) {
         if (voiceChatPlayer != null && this.mutedPlayers.contains(voiceChatPlayer.getUuid()) && !this.autoMutedPlayers.contains(voiceChatPlayer.getUuid())) {
            this.autoMutedPlayers.add(voiceChatPlayer.getUuid());
            this.sendPacket(new CBPacketVoiceMute(voiceChatPlayer.getUuid()));
         }
      }
   }

   public List<UUID> getAutoMutedPlayers() {
      return this.autoMutedPlayers;
   }

   public void handleStaffModState(CBPacketStaffModState cbpacketStaffModState) {
      for (AbstractMod abstractMod : this.cheatBreaker.getModManager().abstractNamedModList) {
         if (abstractMod.getName().equals(cbpacketStaffModState.getMod().replaceAll("_", "").toLowerCase())) {
            abstractMod.setStaffModEnabled(cbpacketStaffModState.isState());
         }
      }
   }

   public void log(String str) {
      Logging.info(str);
   }

   public boolean isServerHandlesWaypoints() {
      return this.serverHandlesWaypoints;
   }

   public void handleServerUpdate(CBPacketServerUpdate cbpacketServerUpdate) {
      this.log("Retrieved " + cbpacketServerUpdate.getServer());
      this.cheatBreaker.getDiscordRpcManager().handleString(cbpacketServerUpdate.getServer());
   }

   public VoiceChannel getVoiceChannel(UUID uuid) {
      if (this.voiceChannels == null) {
         return null;
      }

      Iterator iterator = this.voiceChannels.iterator();

      while (iterator.hasNext()) {
         VoiceChannel voiceChannel;
         if ((voiceChannel = (VoiceChannel)iterator.next()).getUuid().equals(uuid)) {
            return voiceChannel;
         }
      }

      return null;
   }

   public void handleCooldown(CBPacketCooldown cbpacketCooldown) {
      CooldownsMod.handleString(cbpacketCooldown.getMessage(), cbpacketCooldown.getDurationMs(), cbpacketCooldown.getIconId());
   }

   public void handleNametagsUpdate(CBPacketUpdateNametags cbpacketUpdateNametags) {
      if (cbpacketUpdateNametags.getPlayersMap() != null) {
         StaffNametagsMod.setPlayerTags(new HashMap<>());

         for (Entry entry : cbpacketUpdateNametags.getPlayersMap().entrySet()) {
            StaffNametagsMod.getPlayerTags().put(((UUID)entry.getKey()).toString(), (List<String>)entry.getValue());
         }
      } else {
         StaffNametagsMod.setPlayerTags(null);
      }
   }

   public boolean isLegacyCombat() {
      return this.legacyCombat;
   }

   public void handleRemoveHologram(CBPacketRemoveHologram cbpacketRemoveHologram) {
      WorldLabel.getLabels().removeIf(worldLabel -> worldLabel.getPlayerId().equals(cbpacketRemoveHologram.getUuid()));
   }

   public void handleNotification(CBPacketNotification cbpacketNotification) {
      this.cheatBreaker.getModManager().notificationsMod.handleString(cbpacketNotification.getLevel(), cbpacketNotification.getMessage(), cbpacketNotification.getDurationMs());
   }

   public void onStringByteArrayEvent(StringByteArrayEvent stringByteArrayEvent) {
      try {
         Logging.debug("Custom Payload: " + stringByteArrayEvent.getText());
         if (stringByteArrayEvent.getText().equals("REGISTER")) {
            String str = new String(stringByteArrayEvent.getBytes(), Charsets.UTF_8);
            if (!this.pluginChannelRegistered && str.contains(this.cheatBreaker.getPluginMessageChannel())) {
               this.activeChannel = this.cheatBreaker.getPluginMessageChannel();
               this.pluginChannelRegistered = true;
            }

            this.binaryChannelRegistered = str.contains(this.cheatBreaker.getBinaryChannel());
            ArrayList list = new ArrayList();
            net.minecraft.network.PacketBuffer packetBuffer = new net.minecraft.network.PacketBuffer(Unpooled.buffer());
            list.add(!this.activeChannel.isEmpty() ? this.activeChannel : this.cheatBreaker.getPluginMessageChannel());
            boolean flag = true;

            for (String str2 : (Iterable<String>)list) {
               if (!flag) {
                  packetBuffer.writeBytes(new byte[]{0});
               }

               packetBuffer.writeBytes(str2.getBytes(StandardCharsets.UTF_8));
               flag = false;
            }

            if (Minecraft.getMinecraft().getNetHandler() != null && this.pluginChannelRegistered) {
               Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C17PacketCustomPayload("REGISTER", packetBuffer));
            }

            this.reset();
         } else if (stringByteArrayEvent.getText().equals(this.cheatBreaker.getPluginMessageChannel())) {
            Logging.debug(stringByteArrayEvent.getText());
            CBPacket handle = CBPacket.handle(this, stringByteArrayEvent.getBytes());
            if (handle != null) {
               handle.process(this);
            }

            this.cheatBreaker.getEventBus().handleEvent(new CBPacketEvent1(handle));
         }
      } catch (AssertionError | Exception e) {
         e.printStackTrace();
      }
   }

   public void onBooleanIntEvent(BooleanIntEvent booleanIntEvent) {
   }

   public boolean isVoiceEnabled() {
      return this.voiceEnabled;
   }

   public void handleSwingInhibitorUpdate(CBPacketSwingInhibitorUpdate cbpacketSwingInhibitorUpdate) {
      ClickRateLimiter clickRateLimiter = this.cheatBreaker.getClickRateLimiter();
      RateLimiter limiter = clickRateLimiter.getLimiter(cbpacketSwingInhibitorUpdate.getType());
      limiter.setMinInterval(cbpacketSwingInhibitorUpdate.getFrequencyMs());
      limiter.setBurstRate(cbpacketSwingInhibitorUpdate.getDecayRate());
      limiter.setMaxPerSecond(cbpacketSwingInhibitorUpdate.getCpsCap());
   }
}
