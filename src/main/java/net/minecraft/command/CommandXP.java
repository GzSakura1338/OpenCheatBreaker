package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

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
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         throw new WrongUsageException("commands.xp.usage");
      }

      String str = args[0];
      boolean flag = str.endsWith("l") || str.endsWith("L");
      if (flag && str.length() > 1) {
         str = str.substring(0, str.length() - 1);
      }

      int parseInt2 = parseInt(str);
      boolean flag2 = parseInt2 < 0;
      if (flag2) {
         parseInt2 *= -1;
      }

      EntityPlayerMP entityPlayerMP = args.length > 1 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
      if (flag) {
         sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, entityPlayerMP.experienceLevel);
         if (flag2) {
            entityPlayerMP.addExperienceLevel(-parseInt2);
            notifyOperators(sender, this, "commands.xp.success.negative.levels", parseInt2, entityPlayerMP.getName());
         } else {
            entityPlayerMP.addExperienceLevel(parseInt2);
            notifyOperators(sender, this, "commands.xp.success.levels", parseInt2, entityPlayerMP.getName());
         }
      } else {
         sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, entityPlayerMP.experienceTotal);
         if (flag2) {
            throw new CommandException("commands.xp.failure.widthdrawXp");
         }

         entityPlayerMP.addExperience(parseInt2);
         notifyOperators(sender, this, "commands.xp.success", parseInt2, entityPlayerMP.getName());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
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
