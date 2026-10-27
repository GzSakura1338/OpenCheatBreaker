package net.minecraft.command;

import java.util.List;
import net.minecraft.util.BlockPos;

public interface ICommand extends Comparable<ICommand> {
   String getCommandName();

   String getCommandUsage(ICommandSender commandSender);

   List<String> getCommandAliases();

   void processCommand(ICommandSender commandSender, String[] strs) throws CommandException;

   boolean canCommandSenderUseCommand(ICommandSender commandSender);

   List<String> addTabCompletionOptions(ICommandSender commandSender, String[] strs, BlockPos blockPos);

   boolean isUsernameIndex(String[] strs, int n);
}
