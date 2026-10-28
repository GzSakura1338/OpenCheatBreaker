package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;

public class CommandGameMode extends CommandBase {
    @Override
    public String getCommandName() {
        return "gamemode";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.gamemode.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            GameType gameModeFromCommand = this.getGameModeFromCommand(sender, args[0]);
            EntityPlayerMP entityPlayerMP = args.length >= 2 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
            entityPlayerMP.setGameType(gameModeFromCommand);
            entityPlayerMP.fallDistance = 0.0F;
            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("gameMode." + gameModeFromCommand.getName());
            if (entityPlayerMP != sender) {
                notifyOperators(sender, this, 1, "commands.gamemode.success.other", entityPlayerMP.getCommandSenderName(), chatComponentTranslation);
            } else {
                notifyOperators(sender, this, 1, "commands.gamemode.success.self", chatComponentTranslation);
            }
        } else {
            throw new WrongUsageException("commands.gamemode.usage");
        }
    }

    public GameType getGameModeFromCommand(ICommandSender commandSender, String str) {
        return str.equalsIgnoreCase(GameType.SURVIVAL.getName()) || str.equalsIgnoreCase("s")
            ? GameType.SURVIVAL
            : (
                str.equalsIgnoreCase(GameType.CREATIVE.getName()) || str.equalsIgnoreCase("c")
                    ? GameType.CREATIVE
                    : (
                        !str.equalsIgnoreCase(GameType.ADVENTURE.getName()) && !str.equalsIgnoreCase("a")
                            ? WorldSettings.getGameTypeById(parseIntBounded(commandSender, str, 0, GameType.values().length - 2))
                            : GameType.ADVENTURE
                    )
            );
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1
            ? getListOfStringsMatchingLastWord(args, "survival", "creative", "adventure")
            : (args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayerUsernames()) : null);
    }

    public String[] getListOfPlayerUsernames() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }
}
