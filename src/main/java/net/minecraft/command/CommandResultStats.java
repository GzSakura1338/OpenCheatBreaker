package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandResultStats {
   public static int NUM_RESULT_TYPES = CommandResultStats.Type.values().length;
   public static String[] STRING_RESULT_TYPES = new String[NUM_RESULT_TYPES];
   public String[] entitiesID = STRING_RESULT_TYPES;
   public String[] objectives = STRING_RESULT_TYPES;

   public void setCommandStatScore(final ICommandSender sender, CommandResultStats.Type resultTypeIn, int scorePoint) {
      String str = this.entitiesID[resultTypeIn.getTypeID()];
      if (str != null) {
         ICommandSender commandSender = new ICommandSender() {
            @Override
            public String getName() {
               return sender.getName();
            }

            @Override
            public IChatComponent getDisplayName() {
               return sender.getDisplayName();
            }

            @Override
            public void addChatMessage(IChatComponent component) {
               sender.addChatMessage(component);
            }

            @Override
            public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
               return true;
            }

            @Override
            public BlockPos getPosition() {
               return sender.getPosition();
            }

            @Override
            public Vec3 getPositionVector() {
               return sender.getPositionVector();
            }

            @Override
            public World getEntityWorld() {
               return sender.getEntityWorld();
            }

            @Override
            public Entity getCommandSenderEntity() {
               return sender.getCommandSenderEntity();
            }

            @Override
            public boolean sendCommandFeedback() {
               return sender.sendCommandFeedback();
            }

            @Override
            public void setCommandStat(CommandResultStats.Type type, int amount) {
               sender.setCommandStat(type, amount);
            }
         };

         String str2;
         try {
            str2 = CommandBase.getEntityName(commandSender, str);
         } catch (EntityNotFoundException e) {
            return;
         }

         String str3 = this.objectives[resultTypeIn.getTypeID()];
         if (str3 != null) {
            Scoreboard scoreboard = sender.getEntityWorld().getScoreboard();
            ScoreObjective objective = scoreboard.getObjective(str3);
            if (objective != null && scoreboard.entityHasObjective(str2, objective)) {
               Score valueFromObjective = scoreboard.getValueFromObjective(str2, objective);
               valueFromObjective.setScorePoints(scorePoint);
            }
         }
      }
   }

   public void readStatsFromNBT(NBTTagCompound tagcompound) {
      if (tagcompound.hasKey("CommandStats", 10)) {
         NBTTagCompound compoundTag = tagcompound.getCompoundTag("CommandStats");

         for (CommandResultStats.Type type : CommandResultStats.Type.values()) {
            String str = type.getTypeName() + "Name";
            String str2 = type.getTypeName() + "Objective";
            if (compoundTag.hasKey(str, 8) && compoundTag.hasKey(str2, 8)) {
               String string = compoundTag.getString(str);
               String string2 = compoundTag.getString(str2);
               setScoreBoardStat(this, type, string, string2);
            }
         }
      }
   }

   public void writeStatsToNBT(NBTTagCompound tagcompound) {
      NBTTagCompound nbttagCompound = new NBTTagCompound();

      for (CommandResultStats.Type type : CommandResultStats.Type.values()) {
         String str = this.entitiesID[type.getTypeID()];
         String str2 = this.objectives[type.getTypeID()];
         if (str != null && str2 != null) {
            nbttagCompound.setString(type.getTypeName() + "Name", str);
            nbttagCompound.setString(type.getTypeName() + "Objective", str2);
         }
      }

      if (!nbttagCompound.hasNoTags()) {
         tagcompound.setTag("CommandStats", nbttagCompound);
      }
   }

   public static void setScoreBoardStat(CommandResultStats stats, CommandResultStats.Type resultType, String entityID, String objectiveName) {
      if (entityID != null && entityID.length() != 0 && objectiveName != null && objectiveName.length() != 0) {
         if (stats.entitiesID == STRING_RESULT_TYPES || stats.objectives == STRING_RESULT_TYPES) {
            stats.entitiesID = new String[NUM_RESULT_TYPES];
            stats.objectives = new String[NUM_RESULT_TYPES];
         }

         stats.entitiesID[resultType.getTypeID()] = entityID;
         stats.objectives[resultType.getTypeID()] = objectiveName;
      } else {
         removeScoreBoardStat(stats, resultType);
      }
   }

   public static void removeScoreBoardStat(CommandResultStats resultStatsIn, CommandResultStats.Type resultTypeIn) {
      if (resultStatsIn.entitiesID != STRING_RESULT_TYPES && resultStatsIn.objectives != STRING_RESULT_TYPES) {
         resultStatsIn.entitiesID[resultTypeIn.getTypeID()] = null;
         resultStatsIn.objectives[resultTypeIn.getTypeID()] = null;
         boolean flag = true;

         for (CommandResultStats.Type type : CommandResultStats.Type.values()) {
            if (resultStatsIn.entitiesID[type.getTypeID()] != null && resultStatsIn.objectives[type.getTypeID()] != null) {
               flag = false;
               break;
            }
         }

         if (flag) {
            resultStatsIn.entitiesID = STRING_RESULT_TYPES;
            resultStatsIn.objectives = STRING_RESULT_TYPES;
         }
      }
   }

   public void addAllStats(CommandResultStats resultStatsIn) {
      for (CommandResultStats.Type type : CommandResultStats.Type.values()) {
         setScoreBoardStat(this, type, resultStatsIn.entitiesID[type.getTypeID()], resultStatsIn.objectives[type.getTypeID()]);
      }
   }

   public enum Type {
      SUCCESS_COUNT(0, "SuccessCount"),
      AFFECTED_BLOCKS(1, "AffectedBlocks"),
      AFFECTED_ENTITIES(2, "AffectedEntities"),
      AFFECTED_ITEMS(3, "AffectedItems"),
      QUERY_RESULT(4, "QueryResult");

      public int typeID;
      public String typeName;

      Type(int id, String name) {
         this.typeID = id;
         this.typeName = name;
      }

      public int getTypeID() {
         return this.typeID;
      }

      public String getTypeName() {
         return this.typeName;
      }

      public static String[] getTypeNames() {
         String[] strs = new String[values().length];
         int n = 0;

         for (CommandResultStats.Type type : values()) {
            strs[n++] = type.getTypeName();
         }

         return strs;
      }

      public static CommandResultStats.Type getTypeByName(String name) {
         for (CommandResultStats.Type type : values()) {
            if (type.getTypeName().equals(name)) {
               return type;
            }
         }

         return null;
      }
   }
}
