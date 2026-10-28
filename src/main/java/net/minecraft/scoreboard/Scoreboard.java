package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scoreboard {
    public Map scoreObjectives = new HashMap();
    public Map scoreObjectiveCriterias = new HashMap();
    public Map playerObjectives = new HashMap();
    public ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[3];
    public Map teams = new HashMap();
    public Map teamMemberships = new HashMap();

    public ScoreObjective getObjective(String str) {
        return (ScoreObjective)this.scoreObjectives.get(str);
    }

    public ScoreObjective addScoreObjective(String str, IScoreObjectiveCriteria scoreObjectiveCriteria) {
        try {
            ScoreObjective scoreObjective = new ScoreObjective(this, str, scoreObjectiveCriteria);
            Object obj = this.scoreObjectiveCriterias.get(scoreObjectiveCriteria);
            if (obj == null) {
                obj = new ArrayList();
                this.scoreObjectiveCriterias.put(scoreObjectiveCriteria, obj);
            }

            ((List)obj).add(scoreObjective);
            this.scoreObjectives.put(str, scoreObjective);
            this.handleScoreObjective(scoreObjective);
            return scoreObjective;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Collection getObjective(IScoreObjectiveCriteria scoreObjectiveCriteria) {
        Collection collection = (Collection)this.scoreObjectiveCriterias.get(scoreObjectiveCriteria);
        return collection == null ? new ArrayList() : new ArrayList(collection);
    }

    public Score getValueFromObjective(String str, ScoreObjective scoreObjective) {
        Object obj = this.playerObjectives.get(str);
        if (obj == null) {
            obj = new HashMap();
            this.playerObjectives.put(str, obj);
        }

        Score score = (Score)((Map)obj).get(scoreObjective);
        if (score == null) {
            score = new Score(this, scoreObjective, str);
            ((Map)obj).put(scoreObjective, score);
        }

        return score;
    }

    public Collection<Score> getSortedScores(ScoreObjective scoreObjective) {
        ArrayList list = new ArrayList();

        for (Map map : (Iterable<Map>)this.playerObjectives.values()) {
            Score score = (Score)map.get(scoreObjective);
            if (score != null) {
                list.add(score);
            }
        }

        Collections.sort(list, Score.scoreComparator);
        return list;
    }

    public Collection getScoreObjectives() {
        return this.scoreObjectives.values();
    }

    public Collection getObjectiveNames() {
        return this.playerObjectives.keySet();
    }

    public void handleString2(String str) {
        Map map = (Map)this.playerObjectives.remove(str);
        if (map != null) {
            this.handleString(str);
        }
    }

    public Collection createArrayList() {
        Collection values = this.playerObjectives.values();
        ArrayList list = new ArrayList();

        for (Map map : (Iterable<Map>)values) {
            list.addAll(map.values());
        }

        return list;
    }

    public Map getPlayerObjectives(String str) {
        Object obj = this.playerObjectives.get(str);
        if (obj == null) {
            obj = new HashMap();
        }

        return (Map)obj;
    }

    public void removeObjective(ScoreObjective scoreObjective) {
        if (scoreObjective != null) {
            if (this.scoreObjectives.containsValue(scoreObjective.getName())) {
                this.scoreObjectives.remove(scoreObjective.getName());
            }

            for (int i = 0; i < 3; i++) {
                if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
                    this.setObjectiveInDisplaySlot(i, null);
                }
            }

            List list = (List)this.scoreObjectiveCriterias.get(scoreObjective.getCriteria());
            if (list != null) {
                list.remove(scoreObjective);
            }

            for (Map map : (Iterable<Map>)this.playerObjectives.values()) {
                map.remove(scoreObjective);
            }

            this.handleScoreObjective3(scoreObjective);
        }
    }

    public void setObjectiveInDisplaySlot(int n, ScoreObjective scoreObjective) {
        this.objectiveDisplaySlots[n] = scoreObjective;
    }

    public ScoreObjective getObjectiveInDisplaySlot(int n) {
        return this.objectiveDisplaySlots[n];
    }

    public ScorePlayerTeam getTeam(String str) {
        return (ScorePlayerTeam)this.teams.get(str);
    }

    public ScorePlayerTeam createTeam(String str) {
        ScorePlayerTeam team = this.getTeam(str);
        if (team != null) {
            throw new IllegalArgumentException("A team with the name '" + str + "' already exists!");
        }

        team = new ScorePlayerTeam(this, str);
        this.teams.put(str, team);
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

    public boolean addPlayerToTeam(String str, String str2) {
        if (!this.teams.containsKey(str2)) {
            return false;
        }

        ScorePlayerTeam team = this.getTeam(str2);
        if (this.getPlayersTeam(str) != null) {
            this.removePlayerFromTeams(str);
        }

        this.teamMemberships.put(str, team);
        team.getMembershipCollection().add(str);
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
            System.out.println("Player is either on another team or not on any team. Cannot remove from team '" + scorePlayerTeam.getRegisteredName() + "'.");
        } else {
            this.teamMemberships.remove(str);
            scorePlayerTeam.getMembershipCollection().remove(str);
        }
    }

    public Collection getTeamNames() {
        return this.teams.keySet();
    }

    public Collection getTeams() {
        return this.teams.values();
    }

    public ScorePlayerTeam getPlayersTeam(String str) {
        return (ScorePlayerTeam)this.teamMemberships.get(str);
    }

    public void handleScoreObjective(ScoreObjective scoreObjective) {
    }

    public void handleScoreObjective2(ScoreObjective scoreObjective) {
    }

    public void handleScoreObjective3(ScoreObjective scoreObjective) {
    }

    public void updateScore(Score score) {
    }

    public void handleString(String str) {
    }

    public void broadcastTeamCreated(ScorePlayerTeam scorePlayerTeam) {
    }

    public void broadcastTeamRemoved(ScorePlayerTeam scorePlayerTeam) {
    }

    public void updateRemovedTeam(ScorePlayerTeam scorePlayerTeam) {
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
                return null;
        }
    }

    public static int getObjectiveDisplaySlotNumber(String str) {
        return str.equalsIgnoreCase("list") ? 0 : (str.equalsIgnoreCase("sidebar") ? 1 : (str.equalsIgnoreCase("belowName") ? 2 : -1));
    }
}
