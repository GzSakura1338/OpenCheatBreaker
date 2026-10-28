package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
    public Set objectives = new HashSet();
    public MinecraftServer scoreboardMCServer;
    public ScoreboardSaveData state;

    public ServerScoreboard(MinecraftServer minecraftServer) {
        this.scoreboardMCServer = minecraftServer;
    }

    @Override
    public void updateScore(Score score) {
        super.updateScore(score);
        if (this.objectives.contains(score.getObjective())) {
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(score, 0));
        }

        this.markDirtyIfNull();
    }

    @Override
    public void handleString(String str) {
        super.handleString(str);
        this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(str));
        this.markDirtyIfNull();
    }

    @Override
    public void setObjectiveInDisplaySlot(int n, ScoreObjective scoreObjective) {
        ScoreObjective objectiveInDisplaySlot = this.getObjectiveInDisplaySlot(n);
        super.setObjectiveInDisplaySlot(n, scoreObjective);
        if (objectiveInDisplaySlot != scoreObjective && objectiveInDisplaySlot != null) {
            if (this.getSlot(objectiveInDisplaySlot) > 0) {
                this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(n, scoreObjective));
            } else {
                this.removeScoreboardObjective(objectiveInDisplaySlot);
            }
        }

        if (scoreObjective != null) {
            if (this.objectives.contains(scoreObjective)) {
                this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(n, scoreObjective));
            } else {
                this.addScoreboardObjective(scoreObjective);
            }
        }

        this.markDirtyIfNull();
    }

    @Override
    public boolean addPlayerToTeam(String str, String str2) {
        if (super.addPlayerToTeam(str, str2)) {
            ScorePlayerTeam team = this.getTeam(str2);
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(team, Arrays.asList(str), 3));
            this.markDirtyIfNull();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removePlayerFromTeam(String str, ScorePlayerTeam scorePlayerTeam) {
        super.removePlayerFromTeam(str, scorePlayerTeam);
        this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam, Arrays.asList(str), 4));
        this.markDirtyIfNull();
    }

    @Override
    public void handleScoreObjective(ScoreObjective scoreObjective) {
        super.handleScoreObjective(scoreObjective);
        this.markDirtyIfNull();
    }

    @Override
    public void handleScoreObjective2(ScoreObjective scoreObjective) {
        super.handleScoreObjective2(scoreObjective);
        if (this.objectives.contains(scoreObjective)) {
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3BPacketScoreboardObjective(scoreObjective, 2));
        }

        this.markDirtyIfNull();
    }

    @Override
    public void handleScoreObjective3(ScoreObjective scoreObjective) {
        super.handleScoreObjective3(scoreObjective);
        if (this.objectives.contains(scoreObjective)) {
            this.removeScoreboardObjective(scoreObjective);
        }

        this.markDirtyIfNull();
    }

    @Override
    public void broadcastTeamCreated(ScorePlayerTeam scorePlayerTeam) {
        super.broadcastTeamCreated(scorePlayerTeam);
        this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam, 0));
        this.markDirtyIfNull();
    }

    @Override
    public void broadcastTeamRemoved(ScorePlayerTeam scorePlayerTeam) {
        super.broadcastTeamRemoved(scorePlayerTeam);
        this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam, 2));
        this.markDirtyIfNull();
    }

    @Override
    public void updateRemovedTeam(ScorePlayerTeam scorePlayerTeam) {
        super.updateRemovedTeam(scorePlayerTeam);
        this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam, 1));
        this.markDirtyIfNull();
    }

    public void setScoreboardState(ScoreboardSaveData scoreboardSaveData) {
        this.state = scoreboardSaveData;
    }

    public void markDirtyIfNull() {
        if (this.state != null) {
            this.state.markDirty();
        }
    }

    public List createChangePackets(ScoreObjective scoreObjective) {
        ArrayList list = new ArrayList();
        list.add(new S3BPacketScoreboardObjective(scoreObjective, 0));

        for (int i = 0; i < 3; i++) {
            if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
                list.add(new S3DPacketDisplayScoreboard(i, scoreObjective));
            }
        }

        for (Score score : this.getSortedScores(scoreObjective)) {
            list.add(new S3CPacketUpdateScore(score, 0));
        }

        return list;
    }

    public void addScoreboardObjective(ScoreObjective scoreObjective) {
        List createChangePackets = this.createChangePackets(scoreObjective);

        for (EntityPlayerMP entityPlayerMP : (Iterable<EntityPlayerMP>)this.scoreboardMCServer.getConfigurationManager().playerEntityList) {
            for (Packet packet : (Iterable<Packet>)createChangePackets) {
                entityPlayerMP.playerNetServerHandler.sendPacket(packet);
            }
        }

        this.objectives.add(scoreObjective);
    }

    public List findList(ScoreObjective scoreObjective) {
        ArrayList list = new ArrayList();
        list.add(new S3BPacketScoreboardObjective(scoreObjective, 1));

        for (int i = 0; i < 3; i++) {
            if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
                list.add(new S3DPacketDisplayScoreboard(i, scoreObjective));
            }
        }

        return list;
    }

    public void removeScoreboardObjective(ScoreObjective scoreObjective) {
        List findList = this.findList(scoreObjective);

        for (EntityPlayerMP entityPlayerMP : (Iterable<EntityPlayerMP>)this.scoreboardMCServer.getConfigurationManager().playerEntityList) {
            for (Packet packet : (Iterable<Packet>)findList) {
                entityPlayerMP.playerNetServerHandler.sendPacket(packet);
            }
        }

        this.objectives.remove(scoreObjective);
    }

    public int getSlot(ScoreObjective scoreObjective) {
        int n = 0;

        for (int i = 0; i < 3; i++) {
            if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
                n++;
            }
        }

        return n;
    }
}
