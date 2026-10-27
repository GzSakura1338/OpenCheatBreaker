package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandKill extends CommandBase {
   @Override
   public String getCommandName() {
      return "kill";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.kill.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         EntityPlayerMP commandSenderAsPlayer = getCommandSenderAsPlayer(sender);
         commandSenderAsPlayer.onKillCommand();
         notifyOperators(sender, this, "commands.kill.successful", commandSenderAsPlayer.getDisplayName());
      } else {
         Entity entity = getEntity(sender, args[0]);
         entity.onKillCommand();
         notifyOperators(sender, this, "commands.kill.successful", entity.getDisplayName());
      }
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
   }
}
