package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandSetPlayerTimeout extends CommandBase {
    @Override
    public String getCommandName() {
        return "setidletimeout";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.setidletimeout.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            int parseIntWithMin2 = parseIntWithMin(sender, args[0], 0);
            MinecraftServer.getServer().setPlayerIdleTimeout(parseIntWithMin2);
            notifyOperators(sender, this, "commands.setidletimeout.success", parseIntWithMin2);
        } else {
            throw new WrongUsageException("commands.setidletimeout.usage");
        }
    }
}
