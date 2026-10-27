package net.minecraft.util;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class ChatComponentScore extends ChatComponentStyle {
   public String value = "";
   public String name;
   public String objective;

   public ChatComponentScore(String nameIn, String objectiveIn) {
      this.name = nameIn;
      this.objective = objectiveIn;
   }

   public String getName() {
      return this.name;
   }

   public String getObjective() {
      return this.objective;
   }

   public void setValue(String valueIn) {
      this.value = valueIn;
   }

   @Override
   public String getUnformattedTextForChat() {
      MinecraftServer server = MinecraftServer.getServer();
      if (server != null && server.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value)) {
         Scoreboard scoreboard = server.worldServerForDimension(0).getScoreboard();
         ScoreObjective objective = scoreboard.getObjective(this.objective);
         if (scoreboard.entityHasObjective(this.name, objective)) {
            Score valueFromObjective = scoreboard.getValueFromObjective(this.name, objective);
            this.setValue(String.format("%d", valueFromObjective.getScorePoints()));
         } else {
            this.value = "";
         }
      }

      return this.value;
   }

   public ChatComponentScore createCopy() {
      ChatComponentScore chatComponentScore = new ChatComponentScore(this.name, this.objective);
      chatComponentScore.setValue(this.value);
      chatComponentScore.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent chatComponent : this.getSiblings()) {
         chatComponentScore.appendSibling(chatComponent.createCopy());
      }

      return chatComponentScore;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof ChatComponentScore)) {
         return false;
      }

      ChatComponentScore chatComponentScore = (ChatComponentScore)obj;
      return this.name.equals(chatComponentScore.name) && this.objective.equals(chatComponentScore.objective) && super.equals(obj);
   }

   @Override
   public String toString() {
      return "ScoreComponent{name='"
         + this.name
         + '\''
         + "objective='"
         + this.objective
         + '\''
         + ", siblings="
         + this.siblings
         + ", style="
         + this.getChatStyle()
         + '}';
   }
}
