package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChunkCoordinates;

public class CommandSetDefaultSpawnpoint extends CommandBase {
    @Override
    public String getCommandName() {
        return "setworldspawn";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.setworldspawn.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 3) {
            if (sender.getEntityWorld() == null) {
                throw new WrongUsageException("commands.setworldspawn.usage");
            }

            byte b = 0;
            int n = b + 1;
            int parseIntBounded2 = parseIntBounded(sender, args[b], -30000000, 30000000);
            int parseIntBounded3 = parseIntBounded(sender, args[n++], 0, 256);
            int parseIntBounded4 = parseIntBounded(sender, args[n++], -30000000, 30000000);
            sender.getEntityWorld().setSpawnLocation(parseIntBounded2, parseIntBounded3, parseIntBounded4);
            notifyOperators(sender, this, "commands.setworldspawn.success", parseIntBounded2, parseIntBounded3, parseIntBounded4);
        } else {
            if (args.length != 0) {
                throw new WrongUsageException("commands.setworldspawn.usage");
            }

            ChunkCoordinates commandSenderPosition = getCommandSenderAsPlayer(sender).getCommandSenderPosition();
            sender.getEntityWorld().setSpawnLocation(commandSenderPosition.posX, commandSenderPosition.posY, commandSenderPosition.posZ);
            notifyOperators(sender, this, "commands.setworldspawn.success", commandSenderPosition.posX, commandSenderPosition.posY, commandSenderPosition.posZ);
        }
    }
}
