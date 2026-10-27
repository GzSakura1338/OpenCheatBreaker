package net.minecraft.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandExecuteAt extends CommandBase {
   @Override
   public String getCommandName() {
      return "execute";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.execute.usage";
   }

   @Override
   public void processCommand(final ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 5) {
         throw new WrongUsageException("commands.execute.usage");
      }

      final Entity entity = getEntity(sender, args[0], Entity.class);
      final double parseDouble2 = parseDouble(entity.posX, args[1], false);
      final double parseDouble3 = parseDouble(entity.posY, args[2], false);
      final double parseDouble4 = parseDouble(entity.posZ, args[3], false);
      final BlockPos blockPos = new BlockPos(parseDouble2, parseDouble3, parseDouble4);
      byte b = 4;
      if ("detect".equals(args[4]) && args.length > 10) {
         World entityWorld = entity.getEntityWorld();
         double parseDouble5 = parseDouble(parseDouble2, args[5], false);
         double parseDouble6 = parseDouble(parseDouble3, args[6], false);
         double parseDouble7 = parseDouble(parseDouble4, args[7], false);
         Block blockByText = getBlockByText(sender, args[8]);
         int parseInt2 = parseInt(args[9], -1, 15);
         BlockPos blockPos2 = new BlockPos(parseDouble5, parseDouble6, parseDouble7);
         IBlockState blockState = entityWorld.getBlockState(blockPos2);
         if (blockState.getBlock() != blockByText || parseInt2 >= 0 && blockState.getBlock().getMetaFromState(blockState) != parseInt2) {
            throw new CommandException("commands.execute.failed", "detect", entity.getName());
         }

         b = 10;
      }

      String buildString2 = buildString(args, b);
      ICommandSender commandSender = new ICommandSender() {
         @Override
         public String getName() {
            return entity.getName();
         }

         @Override
         public IChatComponent getDisplayName() {
            return entity.getDisplayName();
         }

         @Override
         public void addChatMessage(IChatComponent component) {
            sender.addChatMessage(component);
         }

         @Override
         public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
            return sender.canCommandSenderUseCommand(permLevel, commandName);
         }

         @Override
         public BlockPos getPosition() {
            return blockPos;
         }

         @Override
         public Vec3 getPositionVector() {
            return new Vec3(parseDouble2, parseDouble3, parseDouble4);
         }

         @Override
         public World getEntityWorld() {
            return entity.worldObj;
         }

         @Override
         public Entity getCommandSenderEntity() {
            return entity;
         }

         @Override
         public boolean sendCommandFeedback() {
            MinecraftServer server = MinecraftServer.getServer();
            return server == null || server.worldServers[0].getGameRules().getBoolean("commandBlockOutput");
         }

         @Override
         public void setCommandStat(Type type, int amount) {
            entity.setCommandStat(type, amount);
         }
      };
      ICommandManager commandManager = MinecraftServer.getServer().getCommandManager();

      try {
         int executeCommand = commandManager.executeCommand(commandSender, buildString2);
         if (executeCommand < 1) {
            throw new CommandException("commands.execute.allInvocationsFailed", buildString2);
         }
      } catch (Throwable e) {
         throw new CommandException("commands.execute.failed", buildString2, entity.getName());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames())
         : (
            args.length > 1 && args.length <= 4
               ? findList(args, 1, pos)
               : (
                  args.length > 5 && args.length <= 8 && "detect".equals(args[4])
                     ? findList(args, 5, pos)
                     : (args.length == 9 && "detect".equals(args[4]) ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null)
               )
         );
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
