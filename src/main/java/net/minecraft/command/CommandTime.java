package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandTime extends CommandBase {
    @Override
    public String getCommandName() {
        return "time";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.time.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 1) {
            if (args[0].equals("set")) {
                int n;
                if (args[1].equals("day")) {
                    n = 1000;
                } else if (args[1].equals("night")) {
                    n = 13000;
                } else {
                    n = parseIntWithMin(sender, args[1], 0);
                }

                this.setTime(sender, n);
                notifyOperators(sender, this, "commands.time.set", n);
                return;
            }

            if (args[0].equals("add")) {
                int parseIntWithMin2 = parseIntWithMin(sender, args[1], 0);
                this.addTime(sender, parseIntWithMin2);
                notifyOperators(sender, this, "commands.time.added", parseIntWithMin2);
                return;
            }
        }

        throw new WrongUsageException("commands.time.usage");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1
            ? getListOfStringsMatchingLastWord(args, "set", "add")
            : (args.length == 2 && args[0].equals("set") ? getListOfStringsMatchingLastWord(args, "day", "night") : null);
    }

    public void setTime(ICommandSender commandSender, int n) {
        for (int i = 0; i < MinecraftServer.getServer().worldServers.length; i++) {
            MinecraftServer.getServer().worldServers[i].setWorldTime(n);
        }
    }

    public void addTime(ICommandSender commandSender, int n) {
        for (int i = 0; i < MinecraftServer.getServer().worldServers.length; i++) {
            WorldServer worldServer = MinecraftServer.getServer().worldServers[i];
            worldServer.setWorldTime(worldServer.getWorldTime() + n);
        }
    }
}
