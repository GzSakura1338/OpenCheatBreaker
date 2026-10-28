package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll extends CommandBase {
    @Override
    public String getCommandName() {
        return "save-all";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.save.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws java.io.IOException {
        MinecraftServer server = MinecraftServer.getServer();
        sender.addChatMessage(new ChatComponentTranslation("commands.save.start"));
        if (server.getConfigurationManager() != null) {
            server.getConfigurationManager().saveAllPlayerData();
        }

        try {
            for (int i = 0; i < server.worldServers.length; i++) {
                if (server.worldServers[i] != null) {
                    WorldServer worldServer = server.worldServers[i];
                    boolean disableLevelSaving = worldServer.disableLevelSaving;
                    worldServer.disableLevelSaving = false;
                    worldServer.saveAllChunks(true, null);
                    worldServer.disableLevelSaving = disableLevelSaving;
                }
            }

            if (args.length > 0 && "flush".equals(args[0])) {
                sender.addChatMessage(new ChatComponentTranslation("commands.save.flushStart"));

                for (int i = 0; i < server.worldServers.length; i++) {
                    if (server.worldServers[i] != null) {
                        WorldServer worldServer = server.worldServers[i];
                        boolean disableLevelSaving = worldServer.disableLevelSaving;
                        worldServer.disableLevelSaving = false;
                        worldServer.saveChunkData();
                        worldServer.disableLevelSaving = disableLevelSaving;
                    }
                }

                sender.addChatMessage(new ChatComponentTranslation("commands.save.flushEnd"));
            }
        } catch (MinecraftException e) {
            notifyOperators(sender, this, "commands.save.failed", e.getMessage());
            return;
        }

        notifyOperators(sender, this, "commands.save.success");
    }
}
