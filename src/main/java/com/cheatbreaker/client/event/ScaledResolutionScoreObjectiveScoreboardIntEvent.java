package com.cheatbreaker.client.event;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

public class ScaledResolutionScoreObjectiveScoreboardIntEvent extends CancellableEvent {
   public ScaledResolution scaledResolution;
   public int intValue;
   public Scoreboard scoreboard;
   public ScoreObjective scoreObjective;

   public int getIntValue() {
      return this.intValue;
   }

   public ScoreObjective getScoreObjective() {
      return this.scoreObjective;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public ScaledResolution getScaledResolution() {
      return this.scaledResolution;
   }

   public ScaledResolutionScoreObjectiveScoreboardIntEvent(ScaledResolution scaledResolution, int n, Scoreboard scoreboard, ScoreObjective scoreObjective) {
      this.scaledResolution = scaledResolution;
      this.intValue = n;
      this.scoreboard = scoreboard;
      this.scoreObjective = scoreObjective;
   }
}
