package net.minecraft.command;

import java.util.List;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandBlockData extends CommandBase {
   @Override
   public String getCommandName() {
      return "blockdata";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.blockdata.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 4) {
         throw new WrongUsageException("commands.blockdata.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos parseBlockPos2 = parseBlockPos(sender, args, 0, false);
      World entityWorld = sender.getEntityWorld();
      if (!entityWorld.isBlockLoaded(parseBlockPos2)) {
         throw new CommandException("commands.blockdata.outOfWorld");
      }

      TileEntity tileEntity = entityWorld.getTileEntity(parseBlockPos2);
      if (tileEntity == null) {
         throw new CommandException("commands.blockdata.notValid");
      }

      NBTTagCompound nbttagCompound = new NBTTagCompound();
      tileEntity.writeToNBT(nbttagCompound);
      NBTTagCompound nbttagCompound2 = (NBTTagCompound)nbttagCompound.copy();

      NBTTagCompound nbttagCompound3;
      try {
         nbttagCompound3 = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(sender, args, 3).getUnformattedText());
      } catch (NBTException e) {
         throw new CommandException("commands.blockdata.tagError", e.getMessage());
      }

      nbttagCompound.merge(nbttagCompound3);
      nbttagCompound.setInteger("x", parseBlockPos2.getX());
      nbttagCompound.setInteger("y", parseBlockPos2.getY());
      nbttagCompound.setInteger("z", parseBlockPos2.getZ());
      if (nbttagCompound.equals(nbttagCompound2)) {
         throw new CommandException("commands.blockdata.failed", nbttagCompound.toString());
      }

      tileEntity.readFromNBT(nbttagCompound);
      tileEntity.markDirty();
      entityWorld.markBlockForUpdate(parseBlockPos2);
      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
      notifyOperators(sender, this, "commands.blockdata.success", nbttagCompound.toString());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length > 0 && args.length <= 3 ? findList(args, 0, pos) : null;
   }
}
