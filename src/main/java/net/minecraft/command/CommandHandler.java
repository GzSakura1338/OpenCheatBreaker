package net.minecraft.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandHandler implements ICommandManager {
    public static Logger logger = LogManager.getLogger();
    public Map commandMap = new HashMap();
    public Set commandSet = new HashSet();

    @Override
    public int executeCommand(ICommandSender commandSender, String str) {
        str = str.trim();
        if (str.startsWith("/")) {
            str = str.substring(1);
        }

        String[] split = str.split(" ");
        String str2 = split[0];
        split = dropFirstString(split);
        ICommand command = (ICommand)this.commandMap.get(str2);
        int usernameIndex = this.getUsernameIndex(command, split);
        int n = 0;

        try {
            if (command == null) {
                throw new CommandNotFoundException();
            }

            if (command.canCommandSenderUseCommand(commandSender)) {
                if (usernameIndex > -1) {
                    EntityPlayerMP[] matchPlayers = PlayerSelector.matchPlayers(commandSender, split[usernameIndex]);
                    String str3 = split[usernameIndex];
                    EntityPlayerMP[] entityPlayerMPs = matchPlayers;
                    int length = matchPlayers.length;

                    for (int i = 0; i < length; i++) {
                        EntityPlayerMP entityPlayerMP = entityPlayerMPs[i];
                        split[usernameIndex] = entityPlayerMP.getCommandSenderName();

                        try {
                            command.processCommand(commandSender, split);
                            n++;
                        } catch (CommandException e) {
                            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(e.getMessage(), e.getErrorOjbects());
                            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
                            commandSender.addChatMessage(chatComponentTranslation);
                        }
                    }

                    split[usernameIndex] = str3;
                } else {
                    try {
                        command.processCommand(commandSender, split);
                        n++;
                    } catch (CommandException e) {
                        ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(e.getMessage(), e.getErrorOjbects());
                        chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
                        commandSender.addChatMessage(chatComponentTranslation);
                    }
                }
            } else {
                ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.permission");
                chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
                commandSender.addChatMessage(chatComponentTranslation);
            }
        } catch (WrongUsageException e) {
            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(
                "commands.generic.usage", new ChatComponentTranslation(e.getMessage(), e.getErrorOjbects())
            );
            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            commandSender.addChatMessage(chatComponentTranslation);
        } catch (CommandException e) {
            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(e.getMessage(), e.getErrorOjbects());
            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            commandSender.addChatMessage(chatComponentTranslation);
        } catch (Throwable e) {
            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.exception");
            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            commandSender.addChatMessage(chatComponentTranslation);
            logger.error("Couldn't process command: '" + str + "'", e);
        }

        return n;
    }

    public ICommand registerCommand(ICommand command) {
        List commandAliases = command.getCommandAliases();
        this.commandMap.put(command.getCommandName(), command);
        this.commandSet.add(command);
        if (commandAliases != null) {
            for (String str : (Iterable<String>)commandAliases) {
                ICommand command2 = (ICommand)this.commandMap.get(str);
                if (command2 == null || !command2.getCommandName().equals(str)) {
                    this.commandMap.put(str, command);
                }
            }
        }

        return command;
    }

    public static String[] dropFirstString(String[] strs) {
        String[] strs2 = new String[strs.length - 1];

        for (int i = 1; i < strs.length; i++) {
            strs2[i - 1] = strs[i];
        }

        return strs2;
    }

    @Override
    public List getPossibleCommands(ICommandSender commandSender, String str) {
        String[] split = str.split(" ", -1);
        String str2 = split[0];
        if (split.length == 1) {
            ArrayList list = new ArrayList();

            for (Entry entry : (Iterable<Entry>)this.commandMap.entrySet()) {
                if (CommandBase.doesStringStartWith(str2, (String)entry.getKey()) && ((ICommand)entry.getValue()).canCommandSenderUseCommand(commandSender)) {
                    list.add(entry.getKey());
                }
            }

            return list;
        } else {
            if (split.length > 1) {
                ICommand command = (ICommand)this.commandMap.get(str2);
                if (command != null) {
                    return command.addTabCompletionOptions(commandSender, dropFirstString(split));
                }
            }

            return null;
        }
    }

    @Override
    public List getPossibleCommands(ICommandSender commandSender) {
        ArrayList list = new ArrayList();

        for (ICommand command : (Iterable<ICommand>)this.commandSet) {
            if (command.canCommandSenderUseCommand(commandSender)) {
                list.add(command);
            }
        }

        return list;
    }

    @Override
    public Map getCommands() {
        return this.commandMap;
    }

    public int getUsernameIndex(ICommand command, String[] strs) {
        if (command == null) {
            return -1;
        }

        for (int i = 0; i < strs.length; i++) {
            if (command.isUsernameIndex(strs, i) && PlayerSelector.matchesMultiplePlayers(strs[i])) {
                return i;
            }
        }

        return -1;
    }
}
