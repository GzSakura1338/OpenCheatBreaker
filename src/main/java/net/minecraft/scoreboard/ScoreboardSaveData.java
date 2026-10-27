package net.minecraft.scoreboard;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends WorldSavedData {
   public static Logger logger = LogManager.getLogger();
   public Scoreboard theScoreboard;
   public NBTTagCompound delayedInitNbt;

   public ScoreboardSaveData() {
      this("scoreboard");
   }

   public ScoreboardSaveData(String name) {
      super(name);
   }

   public void setScoreboard(Scoreboard scoreboardIn) {
      this.theScoreboard = scoreboardIn;
      if (this.delayedInitNbt != null) {
         this.readFromNBT(this.delayedInitNbt);
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      if (this.theScoreboard == null) {
         this.delayedInitNbt = nbt;
      } else {
         this.readObjectives(nbt.getTagList("Objectives", 10));
         this.readScores(nbt.getTagList("PlayerScores", 10));
         if (nbt.hasKey("DisplaySlots", 10)) {
            this.readDisplayConfig(nbt.getCompoundTag("DisplaySlots"));
         }

         if (nbt.hasKey("Teams", 9)) {
            this.readTeams(nbt.getTagList("Teams", 10));
         }
      }
   }

   public void readTeams(NBTTagList nbttagList) {
      for (int i = 0; i < nbttagList.tagCount(); i++) {
         NBTTagCompound compoundTagAt = nbttagList.getCompoundTagAt(i);
         String string = compoundTagAt.getString("Name");
         if (string.length() > 16) {
            string = string.substring(0, 16);
         }

         ScorePlayerTeam createTeam = this.theScoreboard.createTeam(string);
         String string2 = compoundTagAt.getString("DisplayName");
         if (string2.length() > 32) {
            string2 = string2.substring(0, 32);
         }

         createTeam.setTeamName(string2);
         if (compoundTagAt.hasKey("TeamColor", 8)) {
            createTeam.setChatFormat(EnumChatFormatting.getValueByName(compoundTagAt.getString("TeamColor")));
         }

         createTeam.setNamePrefix(compoundTagAt.getString("Prefix"));
         createTeam.setNameSuffix(compoundTagAt.getString("Suffix"));
         if (compoundTagAt.hasKey("AllowFriendlyFire", 99)) {
            createTeam.setAllowFriendlyFire(compoundTagAt.getBoolean("AllowFriendlyFire"));
         }

         if (compoundTagAt.hasKey("SeeFriendlyInvisibles", 99)) {
            createTeam.setSeeFriendlyInvisiblesEnabled(compoundTagAt.getBoolean("SeeFriendlyInvisibles"));
         }

         if (compoundTagAt.hasKey("NameTagVisibility", 8)) {
            Team.EnumVisible ruleByName = Team.EnumVisible.getRuleByName(compoundTagAt.getString("NameTagVisibility"));
            if (ruleByName != null) {
               createTeam.setNameTagVisibility(ruleByName);
            }
         }

         if (compoundTagAt.hasKey("DeathMessageVisibility", 8)) {
            Team.EnumVisible ruleByName = Team.EnumVisible.getRuleByName(compoundTagAt.getString("DeathMessageVisibility"));
            if (ruleByName != null) {
               createTeam.setDeathMessageVisibility(ruleByName);
            }
         }

         this.deserializeTeamPlayers(createTeam, compoundTagAt.getTagList("Players", 8));
      }
   }

   public void deserializeTeamPlayers(ScorePlayerTeam scorePlayerTeam, NBTTagList nbttagList) {
      for (int i = 0; i < nbttagList.tagCount(); i++) {
         this.theScoreboard.addPlayerToTeam(nbttagList.getStringTagAt(i), scorePlayerTeam.getRegisteredName());
      }
   }

   public void readDisplayConfig(NBTTagCompound nbttagCompound) {
      for (int i = 0; i < 19; i++) {
         if (nbttagCompound.hasKey("slot_" + i, 8)) {
            String string = nbttagCompound.getString("slot_" + i);
            ScoreObjective objective = this.theScoreboard.getObjective(string);
            this.theScoreboard.setObjectiveInDisplaySlot(i, objective);
         }
      }
   }

   public void readObjectives(NBTTagList nbt) {
      for (int i = 0; i < nbt.tagCount(); i++) {
         NBTTagCompound compoundTagAt = nbt.getCompoundTagAt(i);
         IScoreObjectiveCriteria scoreObjectiveCriteria = IScoreObjectiveCriteria.INSTANCES.get(compoundTagAt.getString("CriteriaName"));
         if (scoreObjectiveCriteria != null) {
            String string = compoundTagAt.getString("Name");
            if (string.length() > 16) {
               string = string.substring(0, 16);
            }

            ScoreObjective addScoreObjective = this.theScoreboard.addScoreObjective(string, scoreObjectiveCriteria);
            addScoreObjective.setDisplayName(compoundTagAt.getString("DisplayName"));
            addScoreObjective.setRenderType(IScoreObjectiveCriteria.EnumRenderType.getByName(compoundTagAt.getString("RenderType")));
         }
      }
   }

   public void readScores(NBTTagList nbt) {
      for (int i = 0; i < nbt.tagCount(); i++) {
         NBTTagCompound compoundTagAt = nbt.getCompoundTagAt(i);
         ScoreObjective objective = this.theScoreboard.getObjective(compoundTagAt.getString("Objective"));
         String string = compoundTagAt.getString("Name");
         if (string.length() > 40) {
            string = string.substring(0, 40);
         }

         Score valueFromObjective = this.theScoreboard.getValueFromObjective(string, objective);
         valueFromObjective.setScorePoints(compoundTagAt.getInteger("Score"));
         if (compoundTagAt.hasKey("Locked")) {
            valueFromObjective.setLocked(compoundTagAt.getBoolean("Locked"));
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound nbt) {
      if (this.theScoreboard == null) {
         logger.warn("Tried to save scoreboard without having a scoreboard...");
      } else {
         nbt.setTag("Objectives", this.objectivesToNbt());
         nbt.setTag("PlayerScores", this.scoresToNbt());
         nbt.setTag("Teams", this.serializeTeams());
         this.serializeSlots(nbt);
      }
   }

   public NBTTagList serializeTeams() {
      NBTTagList nbttagList = new NBTTagList();

      for (ScorePlayerTeam scorePlayerTeam : this.theScoreboard.getTeams()) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         nbttagCompound.setString("Name", scorePlayerTeam.getRegisteredName());
         nbttagCompound.setString("DisplayName", scorePlayerTeam.getTeamName());
         if (scorePlayerTeam.getChatFormat().getColorIndex() >= 0) {
            nbttagCompound.setString("TeamColor", scorePlayerTeam.getChatFormat().getFriendlyName());
         }

         nbttagCompound.setString("Prefix", scorePlayerTeam.getColorPrefix());
         nbttagCompound.setString("Suffix", scorePlayerTeam.getColorSuffix());
         nbttagCompound.setBoolean("AllowFriendlyFire", scorePlayerTeam.getAllowFriendlyFire());
         nbttagCompound.setBoolean("SeeFriendlyInvisibles", scorePlayerTeam.getSeeFriendlyInvisiblesEnabled());
         nbttagCompound.setString("NameTagVisibility", scorePlayerTeam.getNameTagVisibility().internalName);
         nbttagCompound.setString("DeathMessageVisibility", scorePlayerTeam.getDeathMessageVisibility().internalName);
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

      for (int i = 0; i < 19; i++) {
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

   public NBTTagList objectivesToNbt() {
      NBTTagList nbttagList = new NBTTagList();

      for (ScoreObjective scoreObjective : this.theScoreboard.getScoreObjectives()) {
         if (scoreObjective.getCriteria() != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setString("Name", scoreObjective.getName());
            nbttagCompound.setString("CriteriaName", scoreObjective.getCriteria().getName());
            nbttagCompound.setString("DisplayName", scoreObjective.getDisplayName());
            nbttagCompound.setString("RenderType", scoreObjective.getRenderType().getName());
            nbttagList.appendTag(nbttagCompound);
         }
      }

      return nbttagList;
   }

   public NBTTagList scoresToNbt() {
      NBTTagList nbttagList = new NBTTagList();

      for (Score score : this.theScoreboard.getScores()) {
         if (score.getObjective() != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setString("Name", score.getPlayerName());
            nbttagCompound.setString("Objective", score.getObjective().getName());
            nbttagCompound.setInteger("Score", score.getScorePoints());
            nbttagCompound.setBoolean("Locked", score.isLocked());
            nbttagList.appendTag(nbttagCompound);
         }
      }

      return nbttagList;
   }
}
