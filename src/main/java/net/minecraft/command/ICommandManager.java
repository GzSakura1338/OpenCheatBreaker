package net.minecraft.command;

import java.util.List;
import java.util.Map;

public interface ICommandManager {
    int executeCommand(ICommandSender commandSender, String str);

    List getPossibleCommands(ICommandSender commandSender, String str);

    List getPossibleCommands(ICommandSender commandSender);

    Map getCommands();
}
