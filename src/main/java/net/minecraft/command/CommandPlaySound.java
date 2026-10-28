package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public class CommandPlaySound extends CommandBase {
    @Override
    public String getCommandName() {
        return "playsound";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.playsound.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        byte b = 0;
        int n = b + 1;
        String str = args[b];
        EntityPlayerMP player = getPlayer(sender, args[n++]);
        double posX = player.getCommandSenderPosition().posX;
        double posY = player.getCommandSenderPosition().posY;
        double posZ = player.getCommandSenderPosition().posZ;
        double d = 1.0;
        double d2 = 1.0;
        double d3 = 0.0;
        if (args.length > n) {
            posX = clamp_coord(sender, posX, args[n++]);
        }

        if (args.length > n) {
            posY = clamp_double(sender, posY, args[n++], 0, 0);
        }

        if (args.length > n) {
            posZ = clamp_coord(sender, posZ, args[n++]);
        }

        if (args.length > n) {
            d = parseDouble(sender, args[n++], 0.0, Float.MAX_VALUE);
        }

        if (args.length > n) {
            d2 = parseDouble(sender, args[n++], 0.0, 2.0);
        }

        if (args.length > n) {
            d3 = parseDouble(sender, args[n++], 0.0, 1.0);
        }

        double d4 = d > 1.0 ? d * 16.0 : 16.0;
        double distance = player.getDistance(posX, posY, posZ);
        if (distance > d4) {
            if (d3 <= 0.0) {
                throw new CommandException("commands.playsound.playerTooFar", player.getCommandSenderName());
            }

            double d5 = posX - player.posX;
            double d6 = posY - player.posY;
            double d7 = posZ - player.posZ;
            double sqrt = Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
            double posX2 = player.posX;
            double posY2 = player.posY;
            double posZ2 = player.posZ;
            if (sqrt > 0.0) {
                posX2 += d5 / sqrt * 2.0;
                posY2 += d6 / sqrt * 2.0;
                posZ2 += d7 / sqrt * 2.0;
            }

            player.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(str, posX2, posY2, posZ2, (float)d3, (float)d2));
        } else {
            player.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(str, posX, posY, posZ, (float)d, (float)d2));
        }

        notifyOperators(sender, this, "commands.playsound.success", str, player.getCommandSenderName());
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }
}
