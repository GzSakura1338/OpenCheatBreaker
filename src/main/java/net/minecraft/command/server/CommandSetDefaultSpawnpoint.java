package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

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
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      BlockPos blockPos;
      if (args.length == 0) {
         blockPos = getCommandSenderAsPlayer(sender).getPosition();
      } else {
         if (args.length != 3 || sender.getEntityWorld() == null) {
            throw new WrongUsageException("commands.setworldspawn.usage");
         }

         blockPos = parseBlockPos(sender, args, 0, true);
      }

      sender.getEntityWorld().setSpawnPoint(blockPos);
      MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new S05PacketSpawnPosition(blockPos));
      notifyOperators(sender, this, "commands.setworldspawn.success", blockPos.getX(), blockPos.getY(), blockPos.getZ());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length > 0 && args.length <= 3 ? findList(args, 0, pos) : null;
   }
}
