package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;

public class CommandSetSpawnpoint extends CommandBase {
    @Override
    public String getCommandName() {
        return "spawnpoint";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.spawnpoint.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP entityPlayerMP = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(sender, args[0]);
        if (args.length == 4) {
            if (entityPlayerMP.worldObj != null) {
                byte b = 1;
                int n = 30000000;
                int n2 = b + 1;
                int parseIntBounded2 = parseIntBounded(sender, args[b], -n, n);
                int parseIntBounded3 = parseIntBounded(sender, args[n2++], 0, 256);
                int parseIntBounded4 = parseIntBounded(sender, args[n2++], -n, n);
                entityPlayerMP.setSpawnChunk(new ChunkCoordinates(parseIntBounded2, parseIntBounded3, parseIntBounded4), true);
                notifyOperators(sender, this, "commands.spawnpoint.success", entityPlayerMP.getCommandSenderName(), parseIntBounded2, parseIntBounded3, parseIntBounded4);
            }
        } else {
            if (args.length > 1) {
                throw new WrongUsageException("commands.spawnpoint.usage");
            }

            ChunkCoordinates commandSenderPosition = entityPlayerMP.getCommandSenderPosition();
            entityPlayerMP.setSpawnChunk(commandSenderPosition, true);
            notifyOperators(sender, this, "commands.spawnpoint.success", entityPlayerMP.getCommandSenderName(), commandSenderPosition.posX, commandSenderPosition.posY, commandSenderPosition.posZ);
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
