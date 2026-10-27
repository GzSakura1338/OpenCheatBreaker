package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   public Set<ScoreObjective> objectives = Sets.newHashSet();
   public MinecraftServer scoreboardMCServer;
   public ScoreboardSaveData scoreboardSaveData;

   public ServerScoreboard(MinecraftServer mcServer) {
      this.scoreboardMCServer = mcServer;
   }

   @Override
   public void updateScore(Score score) {
      super.updateScore(score);
      if (this.objectives.contains(score.getObjective())) {
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(score));
      }

      this.markSaveDataDirty();
   }

   @Override
   public void handleString(String str) {
      super.handleString(str);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(str));
      this.markSaveDataDirty();
   }

   @Override
   public void handleString(String str, ScoreObjective scoreObjective) {
      super.handleString(str, scoreObjective);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(str, scoreObjective));
      this.markSaveDataDirty();
   }

   @Override
   public void setObjectiveInDisplaySlot(int n, ScoreObjective scoreObjective) {
      ScoreObjective objectiveInDisplaySlot = this.getObjectiveInDisplaySlot(n);
      super.setObjectiveInDisplaySlot(n, scoreObjective);
      if (objectiveInDisplaySlot != scoreObjective && objectiveInDisplaySlot != null) {
         if (this.getSlot(objectiveInDisplaySlot) > 0) {
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(n, scoreObjective));
         } else {
            this.sendDisplaySlotRemovalPackets(objectiveInDisplaySlot);
         }
      }

      if (scoreObjective != null) {
         if (this.objectives.contains(scoreObjective)) {
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(n, scoreObjective));
         } else {
            this.addScoreboardObjective(scoreObjective);
         }
      }

      this.markSaveDataDirty();
   }

   @Override
   public boolean addPlayerToTeam(String player, String newTeam) {
      if (super.addPlayerToTeam(player, newTeam)) {
         ScorePlayerTeam team = this.getTeam(newTeam);
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(team, Arrays.asList(player), 3));
         this.markSaveDataDirty();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void removePlayerFromTeam(String str, ScorePlayerTeam scorePlayerTeam) {
      super.removePlayerFromTeam(str, scorePlayerTeam);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam, Arrays.asList(str), 4));
      this.markSaveDataDirty();
   }

   @Override
   public void onScoreObjectiveAdded(ScoreObjective scoreObjectiveIn) {
      super.onScoreObjectiveAdded(scoreObjectiveIn);
      this.markSaveDataDirty();
   }

   @Override
   public void onObjectiveDisplayNameChanged(ScoreObjective scoreObjective) {
      super.onObjectiveDisplayNameChanged(scoreObjective);
      if (this.objectives.contains(scoreObjective)) {
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3BPacketScoreboardObjective(scoreObjective, 2));
      }

      this.markSaveDataDirty();
   }

   @Override
   public void onScoreObjectiveRemoved(ScoreObjective scoreObjective) {
      super.onScoreObjectiveRemoved(scoreObjective);
      if (this.objectives.contains(scoreObjective)) {
         this.sendDisplaySlotRemovalPackets(scoreObjective);
      }

      this.markSaveDataDirty();
   }

   @Override
   public void broadcastTeamCreated(ScorePlayerTeam playerTeam) {
      super.broadcastTeamCreated(playerTeam);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 0));
      this.markSaveDataDirty();
   }

   @Override
   public void sendTeamUpdate(ScorePlayerTeam playerTeam) {
      super.sendTeamUpdate(playerTeam);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 2));
      this.markSaveDataDirty();
   }

   @Override
   public void updateRemovedTeam(ScorePlayerTeam playerTeam) {
      super.updateRemovedTeam(playerTeam);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 1));
      this.markSaveDataDirty();
   }

   public void setScoreboardState(ScoreboardSaveData scoreboardSaveData) {
      this.scoreboardSaveData = scoreboardSaveData;
   }

   public void markSaveDataDirty() {
      if (this.scoreboardSaveData != null) {
         this.scoreboardSaveData.markDirty();
      }
   }

   public List<Packet> createChangePackets(ScoreObjective scoreObjective) {
      ArrayList newArrayList = Lists.newArrayList();
      newArrayList.add(new S3BPacketScoreboardObjective(scoreObjective, 0));

      for (int i = 0; i < 19; i++) {
         if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
            newArrayList.add(new S3DPacketDisplayScoreboard(i, scoreObjective));
         }
      }

      for (Score score : this.getSortedScores(scoreObjective)) {
         newArrayList.add(new S3CPacketUpdateScore(score));
      }

      return newArrayList;
   }

   public void addScoreboardObjective(ScoreObjective scoreObjective) {
      List createChangePackets = this.createChangePackets(scoreObjective);

      for (EntityPlayerMP entityPlayerMP : this.scoreboardMCServer.getConfigurationManager().getPlayerList()) {
         for (Packet packet : (Iterable<Packet>)createChangePackets) {
            entityPlayerMP.playerNetServerHandler.sendPacket(packet);
         }
      }

      this.objectives.add(scoreObjective);
   }

   public List<Packet> findList(ScoreObjective scoreObjective) {
      ArrayList newArrayList = Lists.newArrayList();
      newArrayList.add(new S3BPacketScoreboardObjective(scoreObjective, 1));

      for (int i = 0; i < 19; i++) {
         if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
            newArrayList.add(new S3DPacketDisplayScoreboard(i, scoreObjective));
         }
      }

      return newArrayList;
   }

   public void sendDisplaySlotRemovalPackets(ScoreObjective scoreObjective) {
      List findList = this.findList(scoreObjective);

      for (EntityPlayerMP entityPlayerMP : this.scoreboardMCServer.getConfigurationManager().getPlayerList()) {
         for (Packet packet : (Iterable<Packet>)findList) {
            entityPlayerMP.playerNetServerHandler.sendPacket(packet);
         }
      }

      this.objectives.remove(scoreObjective);
   }

   public int getSlot(ScoreObjective scoreObjective) {
      int n = 0;

      for (int i = 0; i < 19; i++) {
         if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
            n++;
         }
      }

      return n;
   }
}
