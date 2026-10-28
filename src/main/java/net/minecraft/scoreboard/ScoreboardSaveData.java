package net.minecraft.scoreboard;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends WorldSavedData {
    public static Logger logger = LogManager.getLogger();
    public Scoreboard theScoreboard;
    public NBTTagCompound nbt;

    public ScoreboardSaveData() {
        this("scoreboard");
    }

    public ScoreboardSaveData(String str) {
        super(str);
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.theScoreboard = scoreboard;
        if (this.nbt != null) {
            this.readFromNBT(this.nbt);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagCompound) {
        if (this.theScoreboard == null) {
            this.nbt = nbttagCompound;
        } else {
            this.handleNBTTagList(nbttagCompound.getTagList("Objectives", 10));
            this.handleNBTTagList2(nbttagCompound.getTagList("PlayerScores", 10));
            if (nbttagCompound.hasKey("DisplaySlots", 10)) {
                this.deserializeDisplaySlots(nbttagCompound.getCompoundTag("DisplaySlots"));
            }

            if (nbttagCompound.hasKey("Teams", 9)) {
                this.deserializeTeams(nbttagCompound.getTagList("Teams", 10));
            }
        }
    }

    public void deserializeTeams(NBTTagList nbttagList) {
        for (int i = 0; i < nbttagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = nbttagList.getCompoundTagAt(i);
            ScorePlayerTeam createTeam = this.theScoreboard.createTeam(compoundTagAt.getString("Name"));
            createTeam.setTeamName(compoundTagAt.getString("DisplayName"));
            createTeam.setNamePrefix(compoundTagAt.getString("Prefix"));
            createTeam.setNameSuffix(compoundTagAt.getString("Suffix"));
            if (compoundTagAt.hasKey("AllowFriendlyFire", 99)) {
                createTeam.setAllowFriendlyFire(compoundTagAt.getBoolean("AllowFriendlyFire"));
            }

            if (compoundTagAt.hasKey("SeeFriendlyInvisibles", 99)) {
                createTeam.setSeeFriendlyInvisiblesEnabled(compoundTagAt.getBoolean("SeeFriendlyInvisibles"));
            }

            this.deserializeTeamPlayers(createTeam, compoundTagAt.getTagList("Players", 8));
        }
    }

    public void deserializeTeamPlayers(ScorePlayerTeam scorePlayerTeam, NBTTagList nbttagList) {
        for (int i = 0; i < nbttagList.tagCount(); i++) {
            this.theScoreboard.addPlayerToTeam(nbttagList.getStringTagAt(i), scorePlayerTeam.getRegisteredName());
        }
    }

    public void deserializeDisplaySlots(NBTTagCompound nbttagCompound) {
        for (int i = 0; i < 3; i++) {
            if (nbttagCompound.hasKey("slot_" + i, 8)) {
                String string = nbttagCompound.getString("slot_" + i);
                ScoreObjective objective = this.theScoreboard.getObjective(string);
                this.theScoreboard.setObjectiveInDisplaySlot(i, objective);
            }
        }
    }

    public void handleNBTTagList(NBTTagList nbttagList) {
        for (int i = 0; i < nbttagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = nbttagList.getCompoundTagAt(i);
            IScoreObjectiveCriteria scoreObjectiveCriteria = (IScoreObjectiveCriteria)IScoreObjectiveCriteria.INSTANCES.get(compoundTagAt.getString("CriteriaName"));
            ScoreObjective addScoreObjective = this.theScoreboard.addScoreObjective(compoundTagAt.getString("Name"), scoreObjectiveCriteria);
            addScoreObjective.setDisplayName(compoundTagAt.getString("DisplayName"));
        }
    }

    public void handleNBTTagList2(NBTTagList nbttagList) {
        for (int i = 0; i < nbttagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = nbttagList.getCompoundTagAt(i);
            ScoreObjective objective = this.theScoreboard.getObjective(compoundTagAt.getString("Objective"));
            Score valueFromObjective = this.theScoreboard.getValueFromObjective(compoundTagAt.getString("Name"), objective);
            valueFromObjective.setScorePoints(compoundTagAt.getInteger("Score"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagCompound) {
        if (this.theScoreboard == null) {
            logger.warn("Tried to save scoreboard without having a scoreboard...");
        } else {
            nbttagCompound.setTag("Objectives", this.serializeObjectives());
            nbttagCompound.setTag("PlayerScores", this.getNBTTagList());
            nbttagCompound.setTag("Teams", this.serializeTeams());
            this.serializeSlots(nbttagCompound);
        }
    }

    public NBTTagList serializeTeams() {
        NBTTagList nbttagList = new NBTTagList();

        for (ScorePlayerTeam scorePlayerTeam : (Iterable<ScorePlayerTeam>)this.theScoreboard.getTeams()) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setString("Name", scorePlayerTeam.getRegisteredName());
            nbttagCompound.setString("DisplayName", scorePlayerTeam.getDisplayName());
            nbttagCompound.setString("Prefix", scorePlayerTeam.getColorPrefix());
            nbttagCompound.setString("Suffix", scorePlayerTeam.getColorSuffix());
            nbttagCompound.setBoolean("AllowFriendlyFire", scorePlayerTeam.getAllowFriendlyFire());
            nbttagCompound.setBoolean("SeeFriendlyInvisibles", scorePlayerTeam.shouldShowFriendlyInvisibles());
            NBTTagList nbttagList2 = new NBTTagList();

            for (String str : scorePlayerTeam.getMembershipCollection()) {
                nbttagList2.appendTag(new NBTTagString(str));
            }

            nbttagCompound.setTag("Players", nbttagList2);
            nbttagList.appendTag(nbttagCompound);
        }

        return nbttagList;
    }

    public void serializeSlots(NBTTagCompound nbttagCompound) {
        NBTTagCompound nbttagCompound2 = new NBTTagCompound();
        boolean flag = false;

        for (int i = 0; i < 3; i++) {
            ScoreObjective objectiveInDisplaySlot = this.theScoreboard.getObjectiveInDisplaySlot(i);
            if (objectiveInDisplaySlot != null) {
                nbttagCompound2.setString("slot_" + i, objectiveInDisplaySlot.getName());
                flag = true;
            }
        }

        if (flag) {
            nbttagCompound.setTag("DisplaySlots", nbttagCompound2);
        }
    }

    public NBTTagList serializeObjectives() {
        NBTTagList nbttagList = new NBTTagList();

        for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)this.theScoreboard.getScoreObjectives()) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setString("Name", scoreObjective.getName());
            nbttagCompound.setString("CriteriaName", scoreObjective.getCriteria().getName());
            nbttagCompound.setString("DisplayName", scoreObjective.getDisplayName());
            nbttagList.appendTag(nbttagCompound);
        }

        return nbttagList;
    }

    public NBTTagList getNBTTagList() {
        NBTTagList nbttagList = new NBTTagList();

        for (Score score : (Iterable<Score>)this.theScoreboard.createArrayList()) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setString("Name", score.getPlayerName());
            nbttagCompound.setString("Objective", score.getObjective().getName());
            nbttagCompound.setInteger("Score", score.getScorePoints());
            nbttagList.appendTag(nbttagCompound);
        }

        return nbttagList;
    }
}
