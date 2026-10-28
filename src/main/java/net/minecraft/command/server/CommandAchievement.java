package net.minecraft.command.server;

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
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 2) {
            StatBase oneShotStat = StatList.getOneShotStat(args[1]);
            if (oneShotStat == null && !args[1].equals("*")) {
                throw new CommandException("commands.achievement.unknownAchievement", args[1]);
            }

            EntityPlayerMP entityPlayerMP;
            if (args.length >= 3) {
                entityPlayerMP = getPlayer(sender, args[2]);
            } else {
                entityPlayerMP = getCommandSenderAsPlayer(sender);
            }

            if (args[0].equalsIgnoreCase("give")) {
                if (oneShotStat == null) {
                    for (Achievement achievement : (Iterable<Achievement>)AchievementList.achievementList) {
                        entityPlayerMP.triggerAchievement(achievement);
                    }

                    notifyOperators(sender, this, "commands.achievement.give.success.all", entityPlayerMP.getCommandSenderName());
                } else {
                    if (oneShotStat instanceof Achievement) {
                        Achievement achievement = (Achievement)oneShotStat;
                        ArrayList newArrayList = Lists.newArrayList();

                        while (achievement.parentAchievement != null && !entityPlayerMP.getStatFile().hasAchievementUnlocked(achievement.parentAchievement)) {
                            newArrayList.add(achievement.parentAchievement);
                            achievement = achievement.parentAchievement;
                        }

                        for (Achievement achievement2 : (Iterable<Achievement>)Lists.reverse(newArrayList)) {
                            entityPlayerMP.triggerAchievement(achievement2);
                        }
                    }

                    entityPlayerMP.triggerAchievement(oneShotStat);
                    notifyOperators(sender, this, "commands.achievement.give.success.one", entityPlayerMP.getCommandSenderName(), oneShotStat.createChatComponentText());
                }

                return;
            }
        }

        throw new WrongUsageException("commands.achievement.usage");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "give");
        }

        if (args.length != 2) {
            return args.length == 3 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
        }

        ArrayList newArrayList = Lists.newArrayList();

        for (StatBase statBase : (Iterable<StatBase>)StatList.allStats) {
            newArrayList.add(statBase.statId);
        }

        return getListOfStringsFromIterableMatchingLastWord(args, newArrayList);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 2;
    }
}
