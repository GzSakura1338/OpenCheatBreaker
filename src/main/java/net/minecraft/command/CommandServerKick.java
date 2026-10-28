package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandServerKick extends CommandBase {
    @Override
    public String getCommandName() {
        return "kick";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.kick.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0 && args[0].length() > 1) {
            EntityPlayerMP playerByUsername = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(args[0]);
            String str = "Kicked by an operator.";
            boolean flag = false;
            if (playerByUsername == null) {
                throw new PlayerNotFoundException();
            }

            if (args.length >= 2) {
                str = getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
                flag = true;
            }

            playerByUsername.playerNetServerHandler.kickPlayerFromServer(str);
            if (flag) {
                notifyOperators(sender, this, "commands.kick.success.reason", playerByUsername.getCommandSenderName(), str);
            } else {
                notifyOperators(sender, this, "commands.kick.success", playerByUsername.getCommandSenderName());
            }
        } else {
            throw new WrongUsageException("commands.kick.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length >= 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
