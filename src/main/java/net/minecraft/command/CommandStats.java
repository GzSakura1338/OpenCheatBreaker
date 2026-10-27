package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandStats extends CommandBase {
   @Override
   public String getCommandName() {
      return "stats";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.stats.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.stats.usage");
      }

      boolean flag;
      if (args[0].equals("entity")) {
         flag = false;
      } else {
         if (!args[0].equals("block")) {
            throw new WrongUsageException("commands.stats.usage");
         }

         flag = true;
      }

      int n;
      if (flag) {
         if (args.length < 5) {
            throw new WrongUsageException("commands.stats.block.usage");
         }

         n = 4;
      } else {
         if (args.length < 3) {
            throw new WrongUsageException("commands.stats.entity.usage");
         }

         n = 2;
      }

      String str = args[n++];
      if ("set".equals(str)) {
         if (args.length < n + 3) {
            if (n == 5) {
               throw new WrongUsageException("commands.stats.block.set.usage");
            }

            throw new WrongUsageException("commands.stats.entity.set.usage");
         }
      } else {
         if (!"clear".equals(str)) {
            throw new WrongUsageException("commands.stats.usage");
         }

         if (args.length < n + 1) {
            if (n == 5) {
               throw new WrongUsageException("commands.stats.block.clear.usage");
            }

            throw new WrongUsageException("commands.stats.entity.clear.usage");
         }
      }

      CommandResultStats.Type typeByName = CommandResultStats.Type.getTypeByName(args[n++]);
      if (typeByName == null) {
         throw new CommandException("commands.stats.failed");
      }

      World entityWorld = sender.getEntityWorld();
      CommandResultStats commandResultStats;
      if (flag) {
         BlockPos parseBlockPos2 = parseBlockPos(sender, args, 1, false);
         TileEntity tileEntity = entityWorld.getTileEntity(parseBlockPos2);
         if (tileEntity == null) {
            throw new CommandException("commands.stats.noCompatibleBlock", parseBlockPos2.getX(), parseBlockPos2.getY(), parseBlockPos2.getZ());
         }

         if (tileEntity instanceof TileEntityCommandBlock) {
            commandResultStats = ((TileEntityCommandBlock)tileEntity).getCommandResultStats();
         } else {
            if (!(tileEntity instanceof TileEntitySign)) {
               throw new CommandException("commands.stats.noCompatibleBlock", parseBlockPos2.getX(), parseBlockPos2.getY(), parseBlockPos2.getZ());
            }

            commandResultStats = ((TileEntitySign)tileEntity).getStats();
         }
      } else {
         Entity entity = getEntity(sender, args[1]);
         commandResultStats = entity.getCommandStats();
      }

      if ("set".equals(str)) {
         String str2 = args[n++];
         String str3 = args[n];
         if (str2.length() == 0 || str3.length() == 0) {
            throw new CommandException("commands.stats.failed");
         }

         CommandResultStats.setScoreBoardStat(commandResultStats, typeByName, str2, str3);
         notifyOperators(sender, this, "commands.stats.success", typeByName.getTypeName(), str3, str2);
      } else if ("clear".equals(str)) {
         CommandResultStats.setScoreBoardStat(commandResultStats, typeByName, (String)null, (String)null);
         notifyOperators(sender, this, "commands.stats.cleared", typeByName.getTypeName());
      }

      if (flag) {
         BlockPos parseBlockPos2 = parseBlockPos(sender, args, 1, false);
         TileEntity tileEntity = entityWorld.getTileEntity(parseBlockPos2);
         tileEntity.markDirty();
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, "entity", "block")
         : (
            args.length == 2 && args[0].equals("entity")
               ? getListOfStringsMatchingLastWord(args, this.getStringArray())
               : (
                  args.length >= 2 && args.length <= 4 && args[0].equals("block")
                     ? findList(args, 1, pos)
                     : (
                        (args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block"))
                           ? (
                              (args.length != 4 || !args[0].equals("entity")) && (args.length != 6 || !args[0].equals("block"))
                                 ? (
                                    args.length == 6 && args[0].equals("entity") || args.length == 8 && args[0].equals("block")
                                       ? getListOfStringsMatchingLastWord(args, this.getList())
                                       : null
                                 )
                                 : getListOfStringsMatchingLastWord(args, CommandResultStats.Type.getTypeNames())
                           )
                           : getListOfStringsMatchingLastWord(args, "set", "clear")
                     )
               )
         );
   }

   public String[] getStringArray() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   public List<String> getList() {
      Collection scoreObjectives = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard().getScoreObjectives();
      ArrayList newArrayList = Lists.newArrayList();

      for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)scoreObjectives) {
         if (!scoreObjective.getCriteria().isReadOnly()) {
            newArrayList.add(scoreObjective.getName());
         }
      }

      return newArrayList;
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return args.length > 0 && args[0].equals("entity") && index == 1;
   }
}
