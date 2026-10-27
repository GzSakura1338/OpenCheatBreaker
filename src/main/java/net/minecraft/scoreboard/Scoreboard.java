package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class Scoreboard {
   public Map<String, ScoreObjective> scoreObjectives = Maps.newHashMap();
   public Map<IScoreObjectiveCriteria, List<ScoreObjective>> scoreObjectiveCriterias = Maps.newHashMap();
   public Map<String, Map<ScoreObjective, Score>> entitiesScoreObjectives = Maps.newHashMap();
   public ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[19];
   public Map<String, ScorePlayerTeam> teams = Maps.newHashMap();
   public Map<String, ScorePlayerTeam> teamMemberships = Maps.newHashMap();
   public static String[] names = null;

   public ScoreObjective getObjective(String name) {
      return this.scoreObjectives.get(name);
   }

   public ScoreObjective addScoreObjective(String name, IScoreObjectiveCriteria criteria) {
      if (name.length() > 16) {
         throw new IllegalArgumentException("The objective name '" + name + "' is too long!");
      }

      ScoreObjective objective = this.getObjective(name);
      if (objective != null) {
         throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
      }

      objective = new ScoreObjective(this, name, criteria);
      List list = this.scoreObjectiveCriterias.get(criteria);
      if (list == null) {
         list = Lists.newArrayList();
         this.scoreObjectiveCriterias.put(criteria, list);
      }

      list.add(objective);
      this.scoreObjectives.put(name, objective);
      this.onScoreObjectiveAdded(objective);
      return objective;
   }

   public Collection<ScoreObjective> getObjectivesFromCriteria(IScoreObjectiveCriteria criteria) {
      Collection collection = this.scoreObjectiveCriterias.get(criteria);
      return collection == null ? Lists.newArrayList() : Lists.newArrayList(collection);
   }

   public boolean entityHasObjective(String name, ScoreObjective scoreObjective) {
      Map map = this.entitiesScoreObjectives.get(name);
      if (map == null) {
         return false;
      }

      Score score = (Score)map.get(scoreObjective);
      return score != null;
   }

   public Score getValueFromObjective(String name, ScoreObjective objective) {
      if (name.length() > 40) {
         throw new IllegalArgumentException("The player name '" + name + "' is too long!");
      }

      Map map = this.entitiesScoreObjectives.get(name);
      if (map == null) {
         map = Maps.newHashMap();
         this.entitiesScoreObjectives.put(name, map);
      }

      Score score = (Score)map.get(objective);
      if (score == null) {
         score = new Score(this, objective, name);
         map.put(objective, score);
      }

      return score;
   }

   public Collection<Score> getSortedScores(ScoreObjective objective) {
      ArrayList newArrayList = Lists.newArrayList();

      for (Map map : this.entitiesScoreObjectives.values()) {
         Score score = (Score)map.get(objective);
         if (score != null) {
            newArrayList.add(score);
         }
      }

      Collections.sort(newArrayList, Score.scoreComparator);
      return newArrayList;
   }

   public Collection<ScoreObjective> getScoreObjectives() {
      return this.scoreObjectives.values();
   }

   public Collection<String> getObjectiveNames() {
      return this.entitiesScoreObjectives.keySet();
   }

   public void removeObjectiveFromEntity(String name, ScoreObjective objective) {
      if (objective == null) {
         Map map = this.entitiesScoreObjectives.remove(name);
         if (map != null) {
            this.handleString(name);
         }
      } else {
         Map map = this.entitiesScoreObjectives.get(name);
         if (map != null) {
            Score score = (Score)map.remove(objective);
            if (map.size() < 1) {
               Map map2 = this.entitiesScoreObjectives.remove(name);
               if (map2 != null) {
                  this.handleString(name);
               }
            } else if (score != null) {
               this.handleString(name, objective);
            }
         }
      }
   }

   public Collection<Score> getScores() {
      Collection values = this.entitiesScoreObjectives.values();
      ArrayList newArrayList = Lists.newArrayList();

      for (Map map : (Iterable<Map>)values) {
         newArrayList.addAll(map.values());
      }

      return newArrayList;
   }

   public Map<ScoreObjective, Score> getObjectivesForEntity(String name) {
      Map map = this.entitiesScoreObjectives.get(name);
      if (map == null) {
         map = Maps.newHashMap();
      }

      return map;
   }

   public void removeObjective(ScoreObjective scoreObjective) {
      if (scoreObjective != null) {
         this.scoreObjectives.remove(scoreObjective.getName());

         for (int i = 0; i < 19; i++) {
            if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
               this.setObjectiveInDisplaySlot(i, null);
            }
         }

         List list = this.scoreObjectiveCriterias.get(scoreObjective.getCriteria());
         if (list != null) {
            list.remove(scoreObjective);
         }

         for (Map map : this.entitiesScoreObjectives.values()) {
            map.remove(scoreObjective);
         }

         this.onScoreObjectiveRemoved(scoreObjective);
      }
   }

   public void setObjectiveInDisplaySlot(int n, ScoreObjective scoreObjective) {
      this.objectiveDisplaySlots[n] = scoreObjective;
   }

   public ScoreObjective getObjectiveInDisplaySlot(int n) {
      return this.objectiveDisplaySlots[n];
   }

   public ScorePlayerTeam getTeam(String str) {
      return this.teams.get(str);
   }

   public ScorePlayerTeam createTeam(String name) {
      if (name.length() > 16) {
         throw new IllegalArgumentException("The team name '" + name + "' is too long!");
      }

      ScorePlayerTeam team = this.getTeam(name);
      if (team != null) {
         return null;
      }

      team = new ScorePlayerTeam(this, name);
      this.teams.put(name, team);
      this.broadcastTeamCreated(team);
      return team;
   }

   public void removeTeam(ScorePlayerTeam scorePlayerTeam) {
      if (scorePlayerTeam != null) {
         this.teams.remove(scorePlayerTeam.getRegisteredName());

         for (String str : scorePlayerTeam.getMembershipCollection()) {
            this.teamMemberships.remove(str);
         }

         this.updateRemovedTeam(scorePlayerTeam);
      }
   }

   public boolean addPlayerToTeam(String player, String newTeam) {
      if (player.length() > 40) {
         throw new IllegalArgumentException("The player name '" + player + "' is too long!");
      }

      if (!this.teams.containsKey(newTeam)) {
         return false;
      }

      ScorePlayerTeam team = this.getTeam(newTeam);
      if (this.getPlayersTeam(player) != null) {
         this.removePlayerFromTeams(player);
      }

      this.teamMemberships.put(player, team);
      team.getMembershipCollection().add(player);
      return true;
   }

   public boolean removePlayerFromTeams(String str) {
      ScorePlayerTeam playersTeam = this.getPlayersTeam(str);
      if (playersTeam != null) {
         this.removePlayerFromTeam(str, playersTeam);
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String str, ScorePlayerTeam scorePlayerTeam) {
      if (this.getPlayersTeam(str) != scorePlayerTeam) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + scorePlayerTeam.getRegisteredName() + "'.");
      }

      this.teamMemberships.remove(str);
      scorePlayerTeam.getMembershipCollection().remove(str);
   }

   public Collection<String> getTeamNames() {
      return this.teams.keySet();
   }

   public Collection<ScorePlayerTeam> getTeams() {
      return this.teams.values();
   }

   public ScorePlayerTeam getPlayersTeam(String str) {
      return this.teamMemberships.get(str);
   }

   public void onScoreObjectiveAdded(ScoreObjective scoreObjectiveIn) {
   }

   public void onObjectiveDisplayNameChanged(ScoreObjective scoreObjective) {
   }

   public void onScoreObjectiveRemoved(ScoreObjective scoreObjective) {
   }

   public void updateScore(Score score) {
   }

   public void handleString(String str) {
   }

   public void handleString(String str, ScoreObjective scoreObjective) {
   }

   public void broadcastTeamCreated(ScorePlayerTeam playerTeam) {
   }

   public void sendTeamUpdate(ScorePlayerTeam playerTeam) {
   }

   public void updateRemovedTeam(ScorePlayerTeam playerTeam) {
   }

   public static String getObjectiveDisplaySlot(int n) {
      switch (n) {
         case 0:
            return "list";
         case 1:
            return "sidebar";
         case 2:
            return "belowName";
         default:
            if (n >= 3 && n <= 18) {
               EnumChatFormatting byColorIndex = EnumChatFormatting.byColorIndex(n - 3);
               if (byColorIndex != null && byColorIndex != EnumChatFormatting.RESET) {
                  return "sidebar.team." + byColorIndex.getFriendlyName();
               }
            }

            return null;
      }
   }

   public static int getObjectiveDisplaySlotNumber(String str) {
      if (str.equalsIgnoreCase("list")) {
         return 0;
      }

      if (str.equalsIgnoreCase("sidebar")) {
         return 1;
      }

      if (str.equalsIgnoreCase("belowName")) {
         return 2;
      }

      if (str.startsWith("sidebar.team.")) {
         String substring = str.substring("sidebar.team.".length());
         EnumChatFormatting valueByName = EnumChatFormatting.getValueByName(substring);
         if (valueByName != null && valueByName.getColorIndex() >= 0) {
            return valueByName.getColorIndex() + 3;
         }
      }

      return -1;
   }

   public static String[] getDisplaySlotStrings() {
      if (names == null) {
         names = new String[19];

         for (int i = 0; i < 19; i++) {
            names[i] = getObjectiveDisplaySlot(i);
         }
      }

      return names;
   }

   public void resetEntityScore(Entity entity) {
      if (entity != null && !(entity instanceof EntityPlayer) && !entity.isEntityAlive()) {
         String str = entity.getUniqueID().toString();
         this.removeObjectiveFromEntity(str, (ScoreObjective)null);
         this.removePlayerFromTeams(str);
      }
   }
}
