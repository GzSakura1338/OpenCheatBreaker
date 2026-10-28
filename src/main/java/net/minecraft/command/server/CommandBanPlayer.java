package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;

public class CommandBanPlayer extends CommandBase {
    @Override
    public String getCommandName() {
        return "ban";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.ban.usage";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer() && super.canCommandSenderUseCommand(sender);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 1 && args[0].length() > 0) {
            MinecraftServer server = MinecraftServer.getServer();
            GameProfile gameProfileForUsername = server.getPlayerProfileCache().getGameProfileForUsername(args[0]);
            if (gameProfileForUsername == null) {
                throw new CommandException("commands.ban.failed", args[0]);
            }

            String str = null;
            if (args.length >= 2) {
                str = getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
            }

            UserListBansEntry userListBansEntry = new UserListBansEntry(gameProfileForUsername, null, sender.getCommandSenderName(), null, str);
            server.getConfigurationManager().getBannedPlayers().addEntry(userListBansEntry);
            EntityPlayerMP playerByUsername = server.getConfigurationManager().getPlayerByUsername(args[0]);
            if (playerByUsername != null) {
                playerByUsername.playerNetServerHandler.kickPlayerFromServer("You are banned from this server.");
            }

            notifyOperators(sender, this, "commands.ban.success", args[0]);
        } else {
            throw new WrongUsageException("commands.ban.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length >= 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
