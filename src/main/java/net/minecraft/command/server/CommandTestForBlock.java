package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandTestForBlock extends CommandBase {
   @Override
   public String getCommandName() {
      return "testforblock";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.testforblock.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 4) {
         throw new WrongUsageException("commands.testforblock.usage");
      }

      sender.setCommandStat(Type.AFFECTED_BLOCKS, 0);
      BlockPos parseBlockPos2 = parseBlockPos(sender, args, 0, false);
      Block blockFromName = Block.getBlockFromName(args[3]);
      if (blockFromName == null) {
         throw new NumberInvalidException("commands.setblock.notFound", args[3]);
      }

      int n = -1;
      if (args.length >= 5) {
         n = parseInt(args[4], -1, 15);
      }

      World entityWorld = sender.getEntityWorld();
      if (!entityWorld.isBlockLoaded(parseBlockPos2)) {
         throw new CommandException("commands.testforblock.outOfWorld");
      }

      NBTTagCompound nbttagCompound = new NBTTagCompound();
      boolean flag = false;
      if (args.length >= 6 && blockFromName.hasTileEntity()) {
         String unformattedText = getChatComponentFromNthArg(sender, args, 5).getUnformattedText();

         try {
            nbttagCompound = JsonToNBT.getTagFromJson(unformattedText);
            flag = true;
         } catch (NBTException e) {
            throw new CommandException("commands.setblock.tagError", e.getMessage());
         }
      }

      IBlockState blockState = entityWorld.getBlockState(parseBlockPos2);
      Block block = blockState.getBlock();
      if (block != blockFromName) {
         throw new CommandException(
            "commands.testforblock.failed.tile", parseBlockPos2.getX(), parseBlockPos2.getY(), parseBlockPos2.getZ(), block.getLocalizedName(), blockFromName.getLocalizedName()
         );
      }

      if (n > -1) {
         int metaFromState = blockState.getBlock().getMetaFromState(blockState);
         if (metaFromState != n) {
            throw new CommandException("commands.testforblock.failed.data", parseBlockPos2.getX(), parseBlockPos2.getY(), parseBlockPos2.getZ(), metaFromState, n);
         }
      }

      if (flag) {
         TileEntity tileEntity = entityWorld.getTileEntity(parseBlockPos2);
         if (tileEntity == null) {
            throw new CommandException("commands.testforblock.failed.tileEntity", parseBlockPos2.getX(), parseBlockPos2.getY(), parseBlockPos2.getZ());
         }

         NBTTagCompound nbttagCompound2 = new NBTTagCompound();
         tileEntity.writeToNBT(nbttagCompound2);
         if (!NBTUtil.matches(nbttagCompound, nbttagCompound2, true)) {
            throw new CommandException("commands.testforblock.failed.nbt", parseBlockPos2.getX(), parseBlockPos2.getY(), parseBlockPos2.getZ());
         }
      }

      sender.setCommandStat(Type.AFFECTED_BLOCKS, 1);
      notifyOperators(sender, this, "commands.testforblock.success", parseBlockPos2.getX(), parseBlockPos2.getY(), parseBlockPos2.getZ());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length > 0 && args.length <= 3
         ? findList(args, 0, pos)
         : (args.length == 4 ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null);
   }
}
