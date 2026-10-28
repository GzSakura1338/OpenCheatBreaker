package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings.GameType;

public class CommandDefaultGameMode extends CommandGameMode {
    @Override
    public String getCommandName() {
        return "defaultgamemode";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.defaultgamemode.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            GameType gameModeFromCommand = this.getGameModeFromCommand(sender, args[0]);
            this.setGameType(gameModeFromCommand);
            notifyOperators(sender, this, "commands.defaultgamemode.success", new ChatComponentTranslation("gameMode." + gameModeFromCommand.getName()));
        } else {
            throw new WrongUsageException("commands.defaultgamemode.usage");
        }
    }

    public void setGameType(GameType gameType) {
        MinecraftServer server = MinecraftServer.getServer();
        server.setGameType(gameType);
        if (server.getForceGamemode()) {
            for (EntityPlayerMP entityPlayerMP : (Iterable<EntityPlayerMP>)MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                entityPlayerMP.setGameType(gameType);
                entityPlayerMP.fallDistance = 0.0F;
            }
        }
    }
}
