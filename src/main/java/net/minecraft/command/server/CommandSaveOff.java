package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandSaveOff extends CommandBase {
    @Override
    public String getCommandName() {
        return "save-off";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.save-off.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        MinecraftServer server = MinecraftServer.getServer();
        boolean flag = false;

        for (int i = 0; i < server.worldServers.length; i++) {
            if (server.worldServers[i] != null) {
                WorldServer worldServer = server.worldServers[i];
                if (!worldServer.disableLevelSaving) {
                    worldServer.disableLevelSaving = true;
                    flag = true;
                }
            }
        }

        if (flag) {
            notifyOperators(sender, this, "commands.save.disabled");
        } else {
            throw new CommandException("commands.save-off.alreadyOff");
        }
    }
}
