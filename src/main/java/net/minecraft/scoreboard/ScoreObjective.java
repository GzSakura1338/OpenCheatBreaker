package net.minecraft.scoreboard;

public class ScoreObjective {
    public Scoreboard theScoreboard;
    public String name;
    public IScoreObjectiveCriteria objectiveCriteria;
    public String displayName;

    public ScoreObjective(Scoreboard scoreboard, String str, IScoreObjectiveCriteria scoreObjectiveCriteria) {
        this.theScoreboard = scoreboard;
        this.name = str;
        this.objectiveCriteria = scoreObjectiveCriteria;
        this.displayName = str;
    }

    public Scoreboard getScoreboard() {
        return this.theScoreboard;
    }

    public String getName() {
        return this.name;
    }

    public IScoreObjectiveCriteria getCriteria() {
        return this.objectiveCriteria;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String str) {
        this.displayName = str;
        this.theScoreboard.handleScoreObjective2(this);
    }
}
