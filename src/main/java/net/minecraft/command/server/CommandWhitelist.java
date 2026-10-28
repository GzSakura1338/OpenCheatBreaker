package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandWhitelist extends CommandBase {
    @Override
    public String getCommandName() {
        return "whitelist";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.whitelist.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 1) {
            MinecraftServer server = MinecraftServer.getServer();
            if (args[0].equals("on")) {
                server.getConfigurationManager().setWhiteListEnabled(true);
                notifyOperators(sender, this, "commands.whitelist.enabled");
                return;
            }

            if (args[0].equals("off")) {
                server.getConfigurationManager().setWhiteListEnabled(false);
                notifyOperators(sender, this, "commands.whitelist.disabled");
                return;
            }

            if (args[0].equals("list")) {
                sender.addChatMessage(
                    new ChatComponentTranslation(
                        "commands.whitelist.list",
                        server.getConfigurationManager().getWhitelistedPlayerNames().length,
                        server.getConfigurationManager().getAvailablePlayerDat().length
                    )
                );
                String[] whitelistedPlayerNames = server.getConfigurationManager().getWhitelistedPlayerNames();
                sender.addChatMessage(new ChatComponentText(joinNiceString(whitelistedPlayerNames)));
                return;
            }

            if (args[0].equals("add")) {
                if (args.length < 2) {
                    throw new WrongUsageException("commands.whitelist.add.usage");
                }

                GameProfile gameProfileForUsername = server.getPlayerProfileCache().getGameProfileForUsername(args[1]);
                if (gameProfileForUsername == null) {
                    throw new CommandException("commands.whitelist.add.failed", args[1]);
                }

                server.getConfigurationManager().addWhitelistedPlayer(gameProfileForUsername);
                notifyOperators(sender, this, "commands.whitelist.add.success", args[1]);
                return;
            }

            if (args[0].equals("remove")) {
                if (args.length < 2) {
                    throw new WrongUsageException("commands.whitelist.remove.usage");
                }

                GameProfile profile = server.getConfigurationManager().getWhitelistedPlayers().getProfile(args[1]);
                if (profile == null) {
                    throw new CommandException("commands.whitelist.remove.failed", args[1]);
                }

                server.getConfigurationManager().removePlayerFromWhitelist(profile);
                notifyOperators(sender, this, "commands.whitelist.remove.success", args[1]);
                return;
            }

            if (args[0].equals("reload")) {
                server.getConfigurationManager().loadWhiteList();
                notifyOperators(sender, this, "commands.whitelist.reloaded");
                return;
            }
        }

        throw new WrongUsageException("commands.whitelist.usage");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "on", "off", "list", "add", "remove", "reload");
        }

        if (args.length == 2) {
            if (args[0].equals("remove")) {
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getWhitelistedPlayerNames());
            }

            if (args[0].equals("add")) {
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getPlayerProfileCache().getNames());
            }
        }

        return null;
    }
}
