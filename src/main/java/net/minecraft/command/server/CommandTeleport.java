package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandTeleport extends CommandBase {
    @Override
    public String getCommandName() {
        return "tp";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.tp.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new WrongUsageException("commands.tp.usage");
        }

        EntityPlayerMP entityPlayerMP;
        if (args.length != 2 && args.length != 4) {
            entityPlayerMP = getCommandSenderAsPlayer(sender);
        } else {
            entityPlayerMP = getPlayer(sender, args[0]);
            if (entityPlayerMP == null) {
                throw new PlayerNotFoundException();
            }
        }

        if (args.length != 3 && args.length != 4) {
            if (args.length == 1 || args.length == 2) {
                EntityPlayerMP player = getPlayer(sender, args[args.length - 1]);
                if (player == null) {
                    throw new PlayerNotFoundException();
                }

                if (player.worldObj != entityPlayerMP.worldObj) {
                    notifyOperators(sender, this, "commands.tp.notSameDimension");
                    return;
                }

                entityPlayerMP.mountEntity(null);
                entityPlayerMP.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                notifyOperators(sender, this, "commands.tp.success", entityPlayerMP.getCommandSenderName(), player.getCommandSenderName());
            }
        } else if (entityPlayerMP.worldObj != null) {
            int n = args.length - 3;
            double d = clamp_coord(sender, entityPlayerMP.posX, args[n++]);
            double d2 = clamp_double(sender, entityPlayerMP.posY, args[n++], 0, 0);
            double d3 = clamp_coord(sender, entityPlayerMP.posZ, args[n++]);
            entityPlayerMP.mountEntity(null);
            entityPlayerMP.setPositionAndUpdate(d, d2, d3);
            notifyOperators(sender, this, "commands.tp.success.coordinates", entityPlayerMP.getCommandSenderName(), d, d2, d3);
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length != 1 && args.length != 2 ? null : getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
