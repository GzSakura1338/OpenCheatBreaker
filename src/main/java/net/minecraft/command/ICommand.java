package net.minecraft.command;

import java.util.List;

public interface ICommand extends Comparable {
    String getCommandName();

    String getCommandUsage(ICommandSender commandSender);

    List getCommandAliases();

    void processCommand(ICommandSender commandSender, String[] strs) throws java.io.IOException;

    boolean canCommandSenderUseCommand(ICommandSender commandSender);

    List addTabCompletionOptions(ICommandSender commandSender, String[] strs);

    boolean isUsernameIndex(String[] strs, int n);
}
