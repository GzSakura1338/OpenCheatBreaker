package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class GoalColor implements IScoreObjectiveCriteria {
   public String goalName;

   public GoalColor(String str, EnumChatFormatting enumChatFormatting) {
      this.goalName = str + enumChatFormatting.getFriendlyName();
      IScoreObjectiveCriteria.INSTANCES.put(this.goalName, this);
   }

   @Override
   public String getName() {
      return this.goalName;
   }

   @Override
   public int setScore(List<EntityPlayer> list) {
      return 0;
   }

   @Override
   public boolean isReadOnly() {
      return false;
   }

   @Override
   public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
      return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
   }
}
