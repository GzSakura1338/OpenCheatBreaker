package net.minecraft.scoreboard;

import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class Score {
   public static Comparator<Score> scoreComparator = new Comparator<Score>() {
      public int compare(Score score, Score score2) {
         return score.getScorePoints() > score2.getScorePoints()
            ? 1
            : (score.getScorePoints() < score2.getScorePoints() ? -1 : score2.getPlayerName().compareToIgnoreCase(score.getPlayerName()));
      }
   };
   public Scoreboard theScoreboard;
   public ScoreObjective theScoreObjective;
   public String scorePlayerName;
   public int scorePoints;
   public boolean locked;
   public boolean forceUpdate;

   public Score(Scoreboard theScoreboardIn, ScoreObjective theScoreObjectiveIn, String scorePlayerNameIn) {
      this.theScoreboard = theScoreboardIn;
      this.theScoreObjective = theScoreObjectiveIn;
      this.scorePlayerName = scorePlayerNameIn;
      this.forceUpdate = true;
   }

   public void increseScore(int amount) {
      if (this.theScoreObjective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      }

      this.setScorePoints(this.getScorePoints() + amount);
   }

   public void decreaseScore(int amount) {
      if (this.theScoreObjective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      }

      this.setScorePoints(this.getScorePoints() - amount);
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

   public void setScorePoints(int points) {
      int scorePoints = this.scorePoints;
      this.scorePoints = points;
      if (scorePoints != points || this.forceUpdate) {
         this.forceUpdate = false;
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

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   public void handleList(List<EntityPlayer> list) {
      this.setScorePoints(this.theScoreObjective.getCriteria().setScore(list));
   }
}
