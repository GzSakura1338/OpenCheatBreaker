package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings.GameType;

public class CommandPublishLocalServer extends CommandBase {
    @Override
    public String getCommandName() {
        return "publish";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.publish.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        String shareToLAN = MinecraftServer.getServer().shareToLAN(GameType.SURVIVAL, false);
        if (shareToLAN != null) {
            notifyOperators(sender, this, "commands.publish.started", shareToLAN);
        } else {
            notifyOperators(sender, this, "commands.publish.failed");
        }
    }
}
