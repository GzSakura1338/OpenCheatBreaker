package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandXP extends CommandBase {
    @Override
    public String getCommandName() {
        return "xp";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.xp.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length <= 0) {
            throw new WrongUsageException("commands.xp.usage");
        }

        String str = args[0];
        boolean flag = str.endsWith("l") || str.endsWith("L");
        if (flag && str.length() > 1) {
            str = str.substring(0, str.length() - 1);
        }

        int parseInt2 = parseInt(sender, str);
        boolean flag2 = parseInt2 < 0;
        if (flag2) {
            parseInt2 *= -1;
        }

        EntityPlayerMP entityPlayerMP;
        if (args.length > 1) {
            entityPlayerMP = getPlayer(sender, args[1]);
        } else {
            entityPlayerMP = getCommandSenderAsPlayer(sender);
        }

        if (flag) {
            if (flag2) {
                entityPlayerMP.addExperienceLevel(-parseInt2);
                notifyOperators(sender, this, "commands.xp.success.negative.levels", parseInt2, entityPlayerMP.getCommandSenderName());
            } else {
                entityPlayerMP.addExperienceLevel(parseInt2);
                notifyOperators(sender, this, "commands.xp.success.levels", parseInt2, entityPlayerMP.getCommandSenderName());
            }
        } else {
            if (flag2) {
                throw new WrongUsageException("commands.xp.failure.widthdrawXp");
            }

            entityPlayerMP.addExperience(parseInt2);
            notifyOperators(sender, this, "commands.xp.success", parseInt2, entityPlayerMP.getCommandSenderName());
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : null;
    }

    public String[] getAllUsernames() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }
}
