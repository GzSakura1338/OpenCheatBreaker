package net.minecraft.command.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;

public class CommandAchievement extends CommandBase {
   @Override
   public String getCommandName() {
      return "achievement";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.achievement.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.achievement.usage");
      }

      final StatBase oneShotStat = StatList.getOneShotStat(args[1]);
      if (oneShotStat == null && !args[1].equals("*")) {
         throw new CommandException("commands.achievement.unknownAchievement", args[1]);
      }

      final EntityPlayerMP entityPlayerMP = args.length >= 3 ? getPlayer(sender, args[2]) : getCommandSenderAsPlayer(sender);
      boolean equalsIgnoreCase = args[0].equalsIgnoreCase("give");
      boolean equalsIgnoreCase2 = args[0].equalsIgnoreCase("take");
      if (equalsIgnoreCase || equalsIgnoreCase2) {
         if (oneShotStat == null) {
            if (equalsIgnoreCase) {
               for (Achievement achievement : AchievementList.achievementList) {
                  entityPlayerMP.triggerAchievement(achievement);
               }

               notifyOperators(sender, this, "commands.achievement.give.success.all", entityPlayerMP.getName());
            } else if (equalsIgnoreCase2) {
               for (Achievement achievement : Lists.reverse(AchievementList.achievementList)) {
                  entityPlayerMP.handleStatBase(achievement);
               }

               notifyOperators(sender, this, "commands.achievement.take.success.all", entityPlayerMP.getName());
            }
         } else {
            if (oneShotStat instanceof Achievement) {
               Achievement achievement = (Achievement)oneShotStat;
               if (equalsIgnoreCase) {
                  if (entityPlayerMP.getStatFile().hasAchievementUnlocked(achievement)) {
                     throw new CommandException("commands.achievement.alreadyHave", entityPlayerMP.getName(), oneShotStat.createChatComponent());
                  }

                  ArrayList newArrayList = Lists.newArrayList();

                  while (achievement.parentAchievement != null && !entityPlayerMP.getStatFile().hasAchievementUnlocked(achievement.parentAchievement)) {
                     newArrayList.add(achievement.parentAchievement);
                     achievement = achievement.parentAchievement;
                  }

                  for (Achievement achievement2 : (Iterable<Achievement>)Lists.reverse(newArrayList)) {
                     entityPlayerMP.triggerAchievement(achievement2);
                  }
               } else if (equalsIgnoreCase2) {
                  if (!entityPlayerMP.getStatFile().hasAchievementUnlocked(achievement)) {
                     throw new CommandException("commands.achievement.dontHave", entityPlayerMP.getName(), oneShotStat.createChatComponent());
                  }

                  ArrayList newArrayList = Lists.newArrayList(Iterators.filter(AchievementList.achievementList.iterator(), new Predicate<Achievement>() {
                     public boolean apply(Achievement achievement2) {
                        return entityPlayerMP.getStatFile().hasAchievementUnlocked(achievement2) && achievement2 != oneShotStat;
                     }
                  }));
                  ArrayList newArrayList2 = Lists.newArrayList(newArrayList);

                  for (Achievement achievement2 : (Iterable<Achievement>)newArrayList) {
                     Achievement achievement3 = achievement2;
                     boolean flag = false;

                     while (achievement3 != null) {
                        if (achievement3 == oneShotStat) {
                           flag = true;
                        }

                        achievement3 = achievement3.parentAchievement;
                     }

                     if (!flag) {
                        for (Achievement achievement4 = achievement2; achievement4 != null; achievement4 = achievement4.parentAchievement) {
                           newArrayList2.remove(achievement2);
                        }
                     }
                  }

                  for (Achievement achievement2 : (Iterable<Achievement>)newArrayList2) {
                     entityPlayerMP.handleStatBase(achievement2);
                  }
               }
            }

            if (equalsIgnoreCase) {
               entityPlayerMP.triggerAchievement(oneShotStat);
               notifyOperators(sender, this, "commands.achievement.give.success.one", entityPlayerMP.getName(), oneShotStat.createChatComponent());
            } else if (equalsIgnoreCase2) {
               entityPlayerMP.handleStatBase(oneShotStat);
               notifyOperators(sender, this, "commands.achievement.take.success.one", oneShotStat.createChatComponent(), entityPlayerMP.getName());
            }
         }
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, "give", "take");
      }

      if (args.length != 2) {
         return args.length == 3 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
      }

      ArrayList newArrayList = Lists.newArrayList();

      for (StatBase statBase : StatList.allStats) {
         newArrayList.add(statBase.statId);
      }

      return getListOfStringsMatchingLastWord(args, newArrayList);
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 2;
   }
}
