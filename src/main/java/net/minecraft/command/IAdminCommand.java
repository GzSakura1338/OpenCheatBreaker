package net.minecraft.command;

public interface IAdminCommand {
    void notifyOperators(ICommandSender commandSender, ICommand command, int n, String str, Object... objs);
}
