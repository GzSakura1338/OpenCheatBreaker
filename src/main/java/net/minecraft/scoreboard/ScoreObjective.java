package net.minecraft.scoreboard;

public class ScoreObjective {
   public Scoreboard theScoreboard;
   public String name;
   public IScoreObjectiveCriteria objectiveCriteria;
   public String displayName;
   public IScoreObjectiveCriteria.EnumRenderType renderType;

   public ScoreObjective(Scoreboard theScoreboardIn, String nameIn, IScoreObjectiveCriteria objectiveCriteriaIn) {
      this.theScoreboard = theScoreboardIn;
      this.name = nameIn;
      this.objectiveCriteria = objectiveCriteriaIn;
      this.displayName = nameIn;
      this.renderType = objectiveCriteriaIn.getRenderType();
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

   public void setDisplayName(String nameIn) {
      this.displayName = nameIn;
      this.theScoreboard.onObjectiveDisplayNameChanged(this);
   }

   public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
      return this.renderType;
   }

   public void setRenderType(IScoreObjectiveCriteria.EnumRenderType type) {
      this.renderType = type;
      this.theScoreboard.onObjectiveDisplayNameChanged(this);
   }
}
