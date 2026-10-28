package net.minecraft.command.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandScoreboard extends CommandBase {
    @Override
    public String getCommandName() {
        return "scoreboard";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.scoreboard.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("objectives")) {
                if (args.length == 1) {
                    throw new WrongUsageException("commands.scoreboard.objectives.usage");
                }

                if (args[1].equalsIgnoreCase("list")) {
                    this.listObjectives(sender);
                } else if (args[1].equalsIgnoreCase("add")) {
                    if (args.length < 4) {
                        throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
                    }

                    this.addObjective(sender, args, 2);
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length != 3) {
                        throw new WrongUsageException("commands.scoreboard.objectives.remove.usage");
                    }

                    this.removeObjective(sender, args[2]);
                } else {
                    if (!args[1].equalsIgnoreCase("setdisplay")) {
                        throw new WrongUsageException("commands.scoreboard.objectives.usage");
                    }

                    if (args.length != 3 && args.length != 4) {
                        throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage");
                    }

                    this.setObjectiveDisplay(sender, args, 2);
                }

                return;
            }

            if (args[0].equalsIgnoreCase("players")) {
                if (args.length == 1) {
                    throw new WrongUsageException("commands.scoreboard.players.usage");
                }

                if (args[1].equalsIgnoreCase("list")) {
                    if (args.length > 3) {
                        throw new WrongUsageException("commands.scoreboard.players.list.usage");
                    }

                    this.listPlayers(sender, args, 2);
                } else if (args[1].equalsIgnoreCase("add")) {
                    if (args.length != 5) {
                        throw new WrongUsageException("commands.scoreboard.players.add.usage");
                    }

                    this.setPlayer(sender, args, 2);
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length != 5) {
                        throw new WrongUsageException("commands.scoreboard.players.remove.usage");
                    }

                    this.setPlayer(sender, args, 2);
                } else if (args[1].equalsIgnoreCase("set")) {
                    if (args.length != 5) {
                        throw new WrongUsageException("commands.scoreboard.players.set.usage");
                    }

                    this.setPlayer(sender, args, 2);
                } else {
                    if (!args[1].equalsIgnoreCase("reset")) {
                        throw new WrongUsageException("commands.scoreboard.players.usage");
                    }

                    if (args.length != 3) {
                        throw new WrongUsageException("commands.scoreboard.players.reset.usage");
                    }

                    this.resetPlayers(sender, args, 2);
                }

                return;
            }

            if (args[0].equalsIgnoreCase("teams")) {
                if (args.length == 1) {
                    throw new WrongUsageException("commands.scoreboard.teams.usage");
                }

                if (args[1].equalsIgnoreCase("list")) {
                    if (args.length > 3) {
                        throw new WrongUsageException("commands.scoreboard.teams.list.usage");
                    }

                    this.listTeams(sender, args, 2);
                } else if (args[1].equalsIgnoreCase("add")) {
                    if (args.length < 3) {
                        throw new WrongUsageException("commands.scoreboard.teams.add.usage");
                    }

                    this.addTeam(sender, args, 2);
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length != 3) {
                        throw new WrongUsageException("commands.scoreboard.teams.remove.usage");
                    }

                    this.removeTeam(sender, args, 2);
                } else if (args[1].equalsIgnoreCase("empty")) {
                    if (args.length != 3) {
                        throw new WrongUsageException("commands.scoreboard.teams.empty.usage");
                    }

                    this.emptyTeam(sender, args, 2);
                } else if (args[1].equalsIgnoreCase("join")) {
                    if (args.length < 4 && (args.length != 3 || !(sender instanceof EntityPlayer))) {
                        throw new WrongUsageException("commands.scoreboard.teams.join.usage");
                    }

                    this.joinTeam(sender, args, 2);
                } else if (args[1].equalsIgnoreCase("leave")) {
                    if (args.length < 3 && !(sender instanceof EntityPlayer)) {
                        throw new WrongUsageException("commands.scoreboard.teams.leave.usage");
                    }

                    this.leaveTeam(sender, args, 2);
                } else {
                    if (!args[1].equalsIgnoreCase("option")) {
                        throw new WrongUsageException("commands.scoreboard.teams.usage");
                    }

                    if (args.length != 4 && args.length != 5) {
                        throw new WrongUsageException("commands.scoreboard.teams.option.usage");
                    }

                    this.setTeamOption(sender, args, 2);
                }

                return;
            }
        }

        throw new WrongUsageException("commands.scoreboard.usage");
    }

    public Scoreboard getScoreboard() {
        return MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
    }

    public ScoreObjective findScoreObjective(String name, boolean edit) {
        Scoreboard scoreboard = this.getScoreboard();
        ScoreObjective objective = scoreboard.getObjective(name);
        if (objective == null) {
            throw new CommandException("commands.scoreboard.objectiveNotFound", name);
        } else if (edit && objective.getCriteria().isReadOnly()) {
            throw new CommandException("commands.scoreboard.objectiveReadOnly", name);
        } else {
            return objective;
        }
    }

    public ScorePlayerTeam createCommandException(String name) {
        Scoreboard scoreboard = this.getScoreboard();
        ScorePlayerTeam team = scoreboard.getTeam(name);
        if (team == null) {
            throw new CommandException("commands.scoreboard.teamNotFound", name);
        } else {
            return team;
        }
    }

    public void addObjective(ICommandSender sender, String[] args, int index) {
        String str = args[index++];
        String str2 = args[index++];
        Scoreboard scoreboard = this.getScoreboard();
        IScoreObjectiveCriteria scoreObjectiveCriteria = (IScoreObjectiveCriteria)IScoreObjectiveCriteria.INSTANCES.get(str2);
        if (scoreObjectiveCriteria == null) {
            throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", str2);
        }

        if (scoreboard.getObjective(str) != null) {
            throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", str);
        }

        if (str.length() > 16) {
            throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", str, 16);
        }

        if (str.length() == 0) {
            throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
        }

        if (args.length > index) {
            String unformattedText = getChatComponentFromNthArg(sender, args, index).getUnformattedText();
            if (unformattedText.length() > 32) {
                throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", unformattedText, 32);
            }

            if (unformattedText.length() > 0) {
                scoreboard.addScoreObjective(str, scoreObjectiveCriteria).setDisplayName(unformattedText);
            } else {
                scoreboard.addScoreObjective(str, scoreObjectiveCriteria);
            }
        } else {
            scoreboard.addScoreObjective(str, scoreObjectiveCriteria);
        }

        notifyOperators(sender, this, "commands.scoreboard.objectives.add.success", str);
    }

    public void addTeam(ICommandSender commandSender, String[] strs, int n) {
        String str = strs[n++];
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard.getTeam(str) != null) {
            throw new CommandException("commands.scoreboard.teams.add.alreadyExists", str);
        }

        if (str.length() > 16) {
            throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", str, 16);
        }

        if (str.length() == 0) {
            throw new WrongUsageException("commands.scoreboard.teams.add.usage");
        }

        if (strs.length > n) {
            String unformattedText = getChatComponentFromNthArg(commandSender, strs, n).getUnformattedText();
            if (unformattedText.length() > 32) {
                throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", unformattedText, 32);
            }

            if (unformattedText.length() > 0) {
                scoreboard.createTeam(str).setTeamName(unformattedText);
            } else {
                scoreboard.createTeam(str);
            }
        } else {
            scoreboard.createTeam(str);
        }

        notifyOperators(commandSender, this, "commands.scoreboard.teams.add.success", str);
    }

    public void setTeamOption(ICommandSender commandSender, String[] strs, int n) {
        ScorePlayerTeam createCommandException = this.createCommandException(strs[n++]);
        if (createCommandException != null) {
            String toLowerCase = strs[n++].toLowerCase();
            if (!toLowerCase.equalsIgnoreCase("color") && !toLowerCase.equalsIgnoreCase("friendlyfire") && !toLowerCase.equalsIgnoreCase("seeFriendlyInvisibles")) {
                throw new WrongUsageException("commands.scoreboard.teams.option.usage");
            }

            if (strs.length == 4) {
                if (toLowerCase.equalsIgnoreCase("color")) {
                    throw new WrongUsageException(
                        "commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceStringFromCollection(EnumChatFormatting.getValidValues(true, false))
                    );
                }

                if (!toLowerCase.equalsIgnoreCase("friendlyfire") && !toLowerCase.equalsIgnoreCase("seeFriendlyInvisibles")) {
                    throw new WrongUsageException("commands.scoreboard.teams.option.usage");
                }

                throw new WrongUsageException("commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceStringFromCollection(Arrays.asList("true", "false")));
            }

            String str = strs[n++];
            if (toLowerCase.equalsIgnoreCase("color")) {
                EnumChatFormatting valueByName = EnumChatFormatting.getValueByName(str);
                if (valueByName == null || valueByName.isFancyStyling()) {
                    throw new WrongUsageException(
                        "commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceStringFromCollection(EnumChatFormatting.getValidValues(true, false))
                    );
                }

                createCommandException.setNamePrefix(valueByName.toString());
                createCommandException.setNameSuffix(EnumChatFormatting.RESET.toString());
            } else if (toLowerCase.equalsIgnoreCase("friendlyfire")) {
                if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false")) {
                    throw new WrongUsageException("commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceStringFromCollection(Arrays.asList("true", "false")));
                }

                createCommandException.setAllowFriendlyFire(str.equalsIgnoreCase("true"));
            } else if (toLowerCase.equalsIgnoreCase("seeFriendlyInvisibles")) {
                if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false")) {
                    throw new WrongUsageException("commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceStringFromCollection(Arrays.asList("true", "false")));
                }

                createCommandException.setSeeFriendlyInvisiblesEnabled(str.equalsIgnoreCase("true"));
            }

            notifyOperators(commandSender, this, "commands.scoreboard.teams.option.success", toLowerCase, createCommandException.getRegisteredName(), str);
        }
    }

    public void removeTeam(ICommandSender commandSender, String[] strs, int n) {
        Scoreboard scoreboard = this.getScoreboard();
        ScorePlayerTeam createCommandException = this.createCommandException(strs[n++]);
        if (createCommandException != null) {
            scoreboard.removeTeam(createCommandException);
            notifyOperators(commandSender, this, "commands.scoreboard.teams.remove.success", createCommandException.getRegisteredName());
        }
    }

    public void listTeams(ICommandSender commandSender, String[] strs, int n) {
        Scoreboard scoreboard = this.getScoreboard();
        if (strs.length > n) {
            ScorePlayerTeam createCommandException = this.createCommandException(strs[n++]);
            if (createCommandException == null) {
                return;
            }

            Collection membershipCollection = createCommandException.getMembershipCollection();
            if (membershipCollection.size() <= 0) {
                throw new CommandException("commands.scoreboard.teams.list.player.empty", createCommandException.getRegisteredName());
            }

            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.teams.list.player.count", membershipCollection.size(), createCommandException.getRegisteredName());
            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
            commandSender.addChatMessage(chatComponentTranslation);
            commandSender.addChatMessage(new ChatComponentText(joinNiceString(membershipCollection.toArray())));
        } else {
            Collection teams = scoreboard.getTeams();
            if (teams.size() <= 0) {
                throw new CommandException("commands.scoreboard.teams.list.empty");
            }

            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.teams.list.count", teams.size());
            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
            commandSender.addChatMessage(chatComponentTranslation);

            for (ScorePlayerTeam scorePlayerTeam : (Iterable<ScorePlayerTeam>)teams) {
                commandSender.addChatMessage(
                    new ChatComponentTranslation(
                        "commands.scoreboard.teams.list.entry", scorePlayerTeam.getRegisteredName(), scorePlayerTeam.getDisplayName(), scorePlayerTeam.getMembershipCollection().size()
                    )
                );
            }
        }
    }

    public void joinTeam(ICommandSender commandSender, String[] strs, int n) {
        Scoreboard scoreboard = this.getScoreboard();
        String str = strs[n++];
        HashSet set = new HashSet();
        HashSet set2 = new HashSet();
        if (commandSender instanceof EntityPlayer && n == strs.length) {
            String commandSenderName = getCommandSenderAsPlayer(commandSender).getCommandSenderName();
            if (scoreboard.addPlayerToTeam(commandSenderName, str)) {
                set.add(commandSenderName);
            } else {
                set2.add(commandSenderName);
            }
        } else {
            while (n < strs.length) {
                String playerName = getPlayerName(commandSender, strs[n++]);
                if (scoreboard.addPlayerToTeam(playerName, str)) {
                    set.add(playerName);
                } else {
                    set2.add(playerName);
                }
            }
        }

        if (!set.isEmpty()) {
            notifyOperators(commandSender, this, "commands.scoreboard.teams.join.success", set.size(), str, joinNiceString(set.toArray(new String[0])));
        }

        if (!set2.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.join.failure", set2.size(), str, joinNiceString(set2.toArray(new String[0])));
        }
    }

    public void leaveTeam(ICommandSender commandSender, String[] strs, int n) {
        Scoreboard scoreboard = this.getScoreboard();
        HashSet set = new HashSet();
        HashSet set2 = new HashSet();
        if (commandSender instanceof EntityPlayer && n == strs.length) {
            String commandSenderName = getCommandSenderAsPlayer(commandSender).getCommandSenderName();
            if (scoreboard.removePlayerFromTeams(commandSenderName)) {
                set.add(commandSenderName);
            } else {
                set2.add(commandSenderName);
            }
        } else {
            while (n < strs.length) {
                String playerName = getPlayerName(commandSender, strs[n++]);
                if (scoreboard.removePlayerFromTeams(playerName)) {
                    set.add(playerName);
                } else {
                    set2.add(playerName);
                }
            }
        }

        if (!set.isEmpty()) {
            notifyOperators(commandSender, this, "commands.scoreboard.teams.leave.success", set.size(), joinNiceString(set.toArray(new String[0])));
        }

        if (!set2.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.leave.failure", set2.size(), joinNiceString(set2.toArray(new String[0])));
        }
    }

    public void emptyTeam(ICommandSender commandSender, String[] strs, int n) {
        Scoreboard scoreboard = this.getScoreboard();
        ScorePlayerTeam createCommandException = this.createCommandException(strs[n++]);
        if (createCommandException != null) {
            ArrayList list = new ArrayList<>(createCommandException.getMembershipCollection());
            if (list.isEmpty()) {
                throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", createCommandException.getRegisteredName());
            }

            for (String str : (Iterable<String>)list) {
                scoreboard.removePlayerFromTeam(str, createCommandException);
            }

            notifyOperators(commandSender, this, "commands.scoreboard.teams.empty.success", list.size(), createCommandException.getRegisteredName());
        }
    }

    public void removeObjective(ICommandSender commandSender, String str) {
        Scoreboard scoreboard = this.getScoreboard();
        ScoreObjective findScoreObjective = this.findScoreObjective(str, false);
        scoreboard.removeObjective(findScoreObjective);
        notifyOperators(commandSender, this, "commands.scoreboard.objectives.remove.success", str);
    }

    public void listObjectives(ICommandSender commandSender) {
        Scoreboard scoreboard = this.getScoreboard();
        Collection scoreObjectives = scoreboard.getScoreObjectives();
        if (scoreObjectives.size() <= 0) {
            throw new CommandException("commands.scoreboard.objectives.list.empty");
        }

        ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.objectives.list.count", scoreObjectives.size());
        chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
        commandSender.addChatMessage(chatComponentTranslation);

        for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)scoreObjectives) {
            commandSender.addChatMessage(
                new ChatComponentTranslation("commands.scoreboard.objectives.list.entry", scoreObjective.getName(), scoreObjective.getDisplayName(), scoreObjective.getCriteria().getName())
            );
        }
    }

    public void setObjectiveDisplay(ICommandSender commandSender, String[] strs, int n) {
        Scoreboard scoreboard = this.getScoreboard();
        String str = strs[n++];
        int objectiveDisplaySlotNumber = Scoreboard.getObjectiveDisplaySlotNumber(str);
        ScoreObjective scoreObjective = null;
        if (strs.length == 4) {
            scoreObjective = this.findScoreObjective(strs[n++], false);
        }

        if (objectiveDisplaySlotNumber < 0) {
            throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", str);
        }

        scoreboard.setObjectiveInDisplaySlot(objectiveDisplaySlotNumber, scoreObjective);
        if (scoreObjective != null) {
            notifyOperators(commandSender, this, "commands.scoreboard.objectives.setdisplay.successSet", Scoreboard.getObjectiveDisplaySlot(objectiveDisplaySlotNumber), scoreObjective.getName());
        } else {
            notifyOperators(commandSender, this, "commands.scoreboard.objectives.setdisplay.successCleared", Scoreboard.getObjectiveDisplaySlot(objectiveDisplaySlotNumber));
        }
    }

    public void listPlayers(ICommandSender commandSender, String[] strs, int n) {
        Scoreboard scoreboard = this.getScoreboard();
        if (strs.length > n) {
            String playerName = getPlayerName(commandSender, strs[n++]);
            Map playerObjectives = scoreboard.getPlayerObjectives(playerName);
            if (playerObjectives.size() <= 0) {
                throw new CommandException("commands.scoreboard.players.list.player.empty", playerName);
            }

            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.players.list.player.count", playerObjectives.size(), playerName);
            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
            commandSender.addChatMessage(chatComponentTranslation);

            for (Score score : (Iterable<Score>)playerObjectives.values()) {
                commandSender.addChatMessage(
                    new ChatComponentTranslation(
                        "commands.scoreboard.players.list.player.entry", score.getScorePoints(), score.getObjective().getDisplayName(), score.getObjective().getName()
                    )
                );
            }
        } else {
            Collection objectiveNames = scoreboard.getObjectiveNames();
            if (objectiveNames.size() <= 0) {
                throw new CommandException("commands.scoreboard.players.list.empty");
            }

            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.players.list.count", objectiveNames.size());
            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
            commandSender.addChatMessage(chatComponentTranslation);
            commandSender.addChatMessage(new ChatComponentText(joinNiceString(objectiveNames.toArray())));
        }
    }

    public void setPlayer(ICommandSender commandSender, String[] strs, int n) {
        String str = strs[n - 1];
        String playerName = getPlayerName(commandSender, strs[n++]);
        ScoreObjective findScoreObjective = this.findScoreObjective(strs[n++], true);
        int n2 = str.equalsIgnoreCase("set") ? parseInt(commandSender, strs[n++]) : parseIntWithMin(commandSender, strs[n++], 1);
        Scoreboard scoreboard = this.getScoreboard();
        Score valueFromObjective = scoreboard.getValueFromObjective(playerName, findScoreObjective);
        if (str.equalsIgnoreCase("set")) {
            valueFromObjective.setScorePoints(n2);
        } else if (str.equalsIgnoreCase("add")) {
            valueFromObjective.increseScore(n2);
        } else {
            valueFromObjective.decreaseScore(n2);
        }

        notifyOperators(commandSender, this, "commands.scoreboard.players.set.success", findScoreObjective.getName(), playerName, valueFromObjective.getScorePoints());
    }

    public void resetPlayers(ICommandSender commandSender, String[] strs, int n) {
        Scoreboard scoreboard = this.getScoreboard();
        String playerName = getPlayerName(commandSender, strs[n++]);
        scoreboard.handleString2(playerName);
        notifyOperators(commandSender, this, "commands.scoreboard.players.reset.success", playerName);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "objectives", "players", "teams");
        }

        if (args[0].equalsIgnoreCase("objectives")) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "list", "add", "remove", "setdisplay");
            }

            if (args[1].equalsIgnoreCase("add")) {
                if (args.length == 4) {
                    Set keySet = IScoreObjectiveCriteria.INSTANCES.keySet();
                    return getListOfStringsFromIterableMatchingLastWord(args, keySet);
                }
            } else if (args[1].equalsIgnoreCase("remove")) {
                if (args.length == 3) {
                    return getListOfStringsFromIterableMatchingLastWord(args, this.createArrayList(false));
                }
            } else if (args[1].equalsIgnoreCase("setdisplay")) {
                if (args.length == 3) {
                    return getListOfStringsMatchingLastWord(args, "list", "sidebar", "belowName");
                }

                if (args.length == 4) {
                    return getListOfStringsFromIterableMatchingLastWord(args, this.createArrayList(false));
                }
            }
        } else if (args[0].equalsIgnoreCase("players")) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "set", "add", "remove", "reset", "list");
            }

            if (!args[1].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove")) {
                if ((args[1].equalsIgnoreCase("reset") || args[1].equalsIgnoreCase("list")) && args.length == 3) {
                    return getListOfStringsFromIterableMatchingLastWord(args, this.getScoreboard().getObjectiveNames());
                }
            } else {
                if (args.length == 3) {
                    return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                }

                if (args.length == 4) {
                    return getListOfStringsFromIterableMatchingLastWord(args, this.createArrayList(true));
                }
            }
        } else if (args[0].equalsIgnoreCase("teams")) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "add", "remove", "join", "leave", "empty", "list", "option");
            }

            if (args[1].equalsIgnoreCase("join")) {
                if (args.length == 3) {
                    return getListOfStringsFromIterableMatchingLastWord(args, this.getScoreboard().getTeamNames());
                }

                if (args.length >= 4) {
                    return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                }
            } else {
                if (args[1].equalsIgnoreCase("leave")) {
                    return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                }

                if (!args[1].equalsIgnoreCase("empty") && !args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("remove")) {
                    if (args[1].equalsIgnoreCase("option")) {
                        if (args.length == 3) {
                            return getListOfStringsFromIterableMatchingLastWord(args, this.getScoreboard().getTeamNames());
                        }

                        if (args.length == 4) {
                            return getListOfStringsMatchingLastWord(args, "color", "friendlyfire", "seeFriendlyInvisibles");
                        }

                        if (args.length == 5) {
                            if (args[3].equalsIgnoreCase("color")) {
                                return getListOfStringsFromIterableMatchingLastWord(args, EnumChatFormatting.getValidValues(true, false));
                            }

                            if (args[3].equalsIgnoreCase("friendlyfire") || args[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                                return getListOfStringsMatchingLastWord(args, "true", "false");
                            }
                        }
                    }
                } else if (args.length == 3) {
                    return getListOfStringsFromIterableMatchingLastWord(args, this.getScoreboard().getTeamNames());
                }
            }
        }

        return null;
    }

    public List createArrayList(boolean flag) {
        Collection scoreObjectives = this.getScoreboard().getScoreObjectives();
        ArrayList list = new ArrayList();

        for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)scoreObjectives) {
            if (!flag || !scoreObjective.getCriteria().isReadOnly()) {
                list.add(scoreObjective.getName());
            }
        }

        return list;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args[0].equalsIgnoreCase("players") ? index == 2 : args[0].equalsIgnoreCase("teams") && (index == 2 || index == 3);
    }
}
