package net.minecraft.scoreboard;

import java.util.Comparator;
import java.util.List;

public class Score {
    public static Comparator scoreComparator = new Comparator() {
        public int compare(Score score, Score score2) {
            return score.getScorePoints() > score2.getScorePoints() ? 1 : (score.getScorePoints() < score2.getScorePoints() ? -1 : 0);
        }

        @Override
        public int compare(Object obj, Object obj2) {
            return this.compare((Score)obj, (Score)obj2);
        }
    };
    public Scoreboard theScoreboard;
    public ScoreObjective theScoreObjective;
    public String scorePlayerName;
    public int scorePoints;

    public Score(Scoreboard scoreboard, ScoreObjective scoreObjective, String str) {
        this.theScoreboard = scoreboard;
        this.theScoreObjective = scoreObjective;
        this.scorePlayerName = str;
    }

    public void increseScore(int n) {
        if (this.theScoreObjective.getCriteria().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        }

        this.setScorePoints(this.getScorePoints() + n);
    }

    public void decreaseScore(int n) {
        if (this.theScoreObjective.getCriteria().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        }

        this.setScorePoints(this.getScorePoints() - n);
    }

    public void incrementScore() {
        if (this.theScoreObjective.getCriteria().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        }

        this.increseScore(1);
    }

    public int getScorePoints() {
        return this.scorePoints;
    }

    public void setScorePoints(int n) {
        int scorePoints = this.scorePoints;
        this.scorePoints = n;
        if (scorePoints != n) {
            this.getScoreScoreboard().updateScore(this);
        }
    }

    public ScoreObjective getObjective() {
        return this.theScoreObjective;
    }

    public String getPlayerName() {
        return this.scorePlayerName;
    }

    public Scoreboard getScoreScoreboard() {
        return this.theScoreboard;
    }

    public void handleList(List list) {
        this.setScorePoints(this.theScoreObjective.getCriteria().computeInt(list));
    }
}
