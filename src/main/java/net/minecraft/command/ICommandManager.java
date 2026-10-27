package net.minecraft.command;

import java.util.List;
import java.util.Map;
import net.minecraft.util.BlockPos;

public interface ICommandManager {
   int executeCommand(ICommandSender commandSender, String str);

   List<String> getTabCompletionOptions(ICommandSender commandSender, String str, BlockPos blockPos);

   List<ICommand> getPossibleCommands(ICommandSender commandSender);

   Map<String, ICommand> getCommands();
}
