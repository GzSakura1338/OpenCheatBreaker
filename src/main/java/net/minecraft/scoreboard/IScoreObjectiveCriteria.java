package net.minecraft.scoreboard;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public interface IScoreObjectiveCriteria {
   Map<String, IScoreObjectiveCriteria> INSTANCES = Maps.newHashMap();
   IScoreObjectiveCriteria DUMMY = new ScoreDummyCriteria("dummy");
   IScoreObjectiveCriteria TRIGGER = new ScoreDummyCriteria("trigger");
   IScoreObjectiveCriteria deathCount = new ScoreDummyCriteria("deathCount");
   IScoreObjectiveCriteria playerKillCount = new ScoreDummyCriteria("playerKillCount");
   IScoreObjectiveCriteria totalKillCount = new ScoreDummyCriteria("totalKillCount");
   IScoreObjectiveCriteria health = new ScoreHealthCriteria("health");
   IScoreObjectiveCriteria[] TEAM_KILLS = new IScoreObjectiveCriteria[]{
      new GoalColor("teamkill.", EnumChatFormatting.BLACK),
      new GoalColor("teamkill.", EnumChatFormatting.DARK_BLUE),
      new GoalColor("teamkill.", EnumChatFormatting.DARK_GREEN),
      new GoalColor("teamkill.", EnumChatFormatting.DARK_AQUA),
      new GoalColor("teamkill.", EnumChatFormatting.DARK_RED),
      new GoalColor("teamkill.", EnumChatFormatting.DARK_PURPLE),
      new GoalColor("teamkill.", EnumChatFormatting.GOLD),
      new GoalColor("teamkill.", EnumChatFormatting.GRAY),
      new GoalColor("teamkill.", EnumChatFormatting.DARK_GRAY),
      new GoalColor("teamkill.", EnumChatFormatting.BLUE),
      new GoalColor("teamkill.", EnumChatFormatting.GREEN),
      new GoalColor("teamkill.", EnumChatFormatting.AQUA),
      new GoalColor("teamkill.", EnumChatFormatting.RED),
      new GoalColor("teamkill.", EnumChatFormatting.LIGHT_PURPLE),
      new GoalColor("teamkill.", EnumChatFormatting.YELLOW),
      new GoalColor("teamkill.", EnumChatFormatting.WHITE)
   };
   IScoreObjectiveCriteria[] KILLED_BY_TEAM = new IScoreObjectiveCriteria[]{
      new GoalColor("killedByTeam.", EnumChatFormatting.BLACK),
      new GoalColor("killedByTeam.", EnumChatFormatting.DARK_BLUE),
      new GoalColor("killedByTeam.", EnumChatFormatting.DARK_GREEN),
      new GoalColor("killedByTeam.", EnumChatFormatting.DARK_AQUA),
      new GoalColor("killedByTeam.", EnumChatFormatting.DARK_RED),
      new GoalColor("killedByTeam.", EnumChatFormatting.DARK_PURPLE),
      new GoalColor("killedByTeam.", EnumChatFormatting.GOLD),
      new GoalColor("killedByTeam.", EnumChatFormatting.GRAY),
      new GoalColor("killedByTeam.", EnumChatFormatting.DARK_GRAY),
      new GoalColor("killedByTeam.", EnumChatFormatting.BLUE),
      new GoalColor("killedByTeam.", EnumChatFormatting.GREEN),
      new GoalColor("killedByTeam.", EnumChatFormatting.AQUA),
      new GoalColor("killedByTeam.", EnumChatFormatting.RED),
      new GoalColor("killedByTeam.", EnumChatFormatting.LIGHT_PURPLE),
      new GoalColor("killedByTeam.", EnumChatFormatting.YELLOW),
      new GoalColor("killedByTeam.", EnumChatFormatting.WHITE)
   };

   String getName();

   int setScore(List<EntityPlayer> list);

   boolean isReadOnly();

   IScoreObjectiveCriteria.EnumRenderType getRenderType();

   enum EnumRenderType {
      INTEGER("integer"),
      HEARTS("hearts");

      public String name;
      public static Map<String, IScoreObjectiveCriteria.EnumRenderType> TYPES = Maps.newHashMap();

      EnumRenderType(String str) {
         this.name = str;
      }

      public String getName() {
         return this.name;
      }

      public static IScoreObjectiveCriteria.EnumRenderType getByName(String str) {
         IScoreObjectiveCriteria.EnumRenderType enumRenderType = TYPES.get(str);
         return enumRenderType == null ? INTEGER : enumRenderType;
      }

      static {
         for (IScoreObjectiveCriteria.EnumRenderType enumRenderType : values()) {
            TYPES.put(enumRenderType.getName(), enumRenderType);
         }
      }
   }
}
