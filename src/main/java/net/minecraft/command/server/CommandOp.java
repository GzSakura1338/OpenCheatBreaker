package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandOp extends CommandBase {
    @Override
    public String getCommandName() {
        return "op";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.op.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 1 && args[0].length() > 0) {
            MinecraftServer server = MinecraftServer.getServer();
            GameProfile gameProfileForUsername = server.getPlayerProfileCache().getGameProfileForUsername(args[0]);
            if (gameProfileForUsername == null) {
                throw new CommandException("commands.op.failed", args[0]);
            }

            server.getConfigurationManager().addOp(gameProfileForUsername);
            notifyOperators(sender, this, "commands.op.success", args[0]);
        } else {
            throw new WrongUsageException("commands.op.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            String str = args[args.length - 1];
            ArrayList list = new ArrayList();

            for (GameProfile gameProfile : MinecraftServer.getServer().getGameProfiles()) {
                if (!MinecraftServer.getServer().getConfigurationManager().canSendCommands(gameProfile) && doesStringStartWith(str, gameProfile.getName())) {
                    list.add(gameProfile.getName());
                }
            }

            return list;
        } else {
            return null;
        }
    }
}
