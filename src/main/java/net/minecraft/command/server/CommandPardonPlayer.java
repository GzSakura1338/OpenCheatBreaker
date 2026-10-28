package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandPardonPlayer extends CommandBase {
    @Override
    public String getCommandName() {
        return "pardon";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.unban.usage";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer() && super.canCommandSenderUseCommand(sender);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 1 && args[0].length() > 0) {
            MinecraftServer server = MinecraftServer.getServer();
            GameProfile isUsernameBanned = server.getConfigurationManager().getBannedPlayers().isUsernameBanned(args[0]);
            if (isUsernameBanned == null) {
                throw new CommandException("commands.unban.failed", args[0]);
            }

            server.getConfigurationManager().getBannedPlayers().removeEntry(isUsernameBanned);
            notifyOperators(sender, this, "commands.unban.success", args[0]);
        } else {
            throw new WrongUsageException("commands.unban.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1
            ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getKeys())
            : null;
    }
}
