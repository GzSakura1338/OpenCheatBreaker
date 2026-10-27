package net.minecraft.command;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandCompare extends CommandBase {
   @Override
   public String getCommandName() {
      return "testforblocks";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.compare.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 9) {
         throw new WrongUsageException("commands.compare.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos parseBlockPos2 = parseBlockPos(sender, args, 0, false);
      BlockPos parseBlockPos3 = parseBlockPos(sender, args, 3, false);
      BlockPos parseBlockPos4 = parseBlockPos(sender, args, 6, false);
      StructureBoundingBox structureBoundingBox = new StructureBoundingBox(parseBlockPos2, parseBlockPos3);
      StructureBoundingBox structureBoundingBox2 = new StructureBoundingBox(parseBlockPos4, parseBlockPos4.add(structureBoundingBox.getDimensions()));
      int n = structureBoundingBox.getXSize() * structureBoundingBox.getYSize() * structureBoundingBox.getZSize();
      if (n > 524288) {
         throw new CommandException("commands.compare.tooManyBlocks", n, 524288);
      }

      if (structureBoundingBox.minY >= 0 && structureBoundingBox.maxY < 256 && structureBoundingBox2.minY >= 0 && structureBoundingBox2.maxY < 256) {
         World entityWorld = sender.getEntityWorld();
         if (entityWorld.isAreaLoaded(structureBoundingBox) && entityWorld.isAreaLoaded(structureBoundingBox2)) {
            boolean flag = false;
            if (args.length > 9 && args[9].equals("masked")) {
               flag = true;
            }

            n = 0;
            BlockPos blockPos = new BlockPos(structureBoundingBox2.minX - structureBoundingBox.minX, structureBoundingBox2.minY - structureBoundingBox.minY, structureBoundingBox2.minZ - structureBoundingBox.minZ);
            MutableBlockPos mutableBlockPos = new MutableBlockPos();
            MutableBlockPos mutableBlockPos2 = new MutableBlockPos();

            for (int minZ = structureBoundingBox.minZ; minZ <= structureBoundingBox.maxZ; minZ++) {
               for (int minY = structureBoundingBox.minY; minY <= structureBoundingBox.maxY; minY++) {
                  for (int minX = structureBoundingBox.minX; minX <= structureBoundingBox.maxX; minX++) {
                     mutableBlockPos.set(minX, minY, minZ);
                     mutableBlockPos2.set(minX + blockPos.getX(), minY + blockPos.getY(), minZ + blockPos.getZ());
                     boolean flag2 = false;
                     IBlockState blockState = entityWorld.getBlockState(mutableBlockPos);
                     if (!flag || blockState.getBlock() != Blocks.air) {
                        if (blockState == entityWorld.getBlockState(mutableBlockPos2)) {
                           TileEntity tileEntity = entityWorld.getTileEntity(mutableBlockPos);
                           TileEntity tileEntity2 = entityWorld.getTileEntity(mutableBlockPos2);
                           if (tileEntity != null && tileEntity2 != null) {
                              NBTTagCompound nbttagCompound = new NBTTagCompound();
                              tileEntity.writeToNBT(nbttagCompound);
                              nbttagCompound.removeTag("x");
                              nbttagCompound.removeTag("y");
                              nbttagCompound.removeTag("z");
                              NBTTagCompound nbttagCompound2 = new NBTTagCompound();
                              tileEntity2.writeToNBT(nbttagCompound2);
                              nbttagCompound2.removeTag("x");
                              nbttagCompound2.removeTag("y");
                              nbttagCompound2.removeTag("z");
                              if (!nbttagCompound.equals(nbttagCompound2)) {
                                 flag2 = true;
                              }
                           } else if (tileEntity != null) {
                              flag2 = true;
                           }
                        } else {
                           flag2 = true;
                        }

                        n++;
                        if (flag2) {
                           throw new CommandException("commands.compare.failed");
                        }
                     }
                  }
               }
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, n);
            notifyOperators(sender, this, "commands.compare.success", n);
         } else {
            throw new CommandException("commands.compare.outOfWorld");
         }
      } else {
         throw new CommandException("commands.compare.outOfWorld");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length > 0 && args.length <= 3
         ? findList(args, 0, pos)
         : (
            args.length > 3 && args.length <= 6
               ? findList(args, 3, pos)
               : (
                  args.length > 6 && args.length <= 9
                     ? findList(args, 6, pos)
                     : (args.length == 10 ? getListOfStringsMatchingLastWord(args, "masked", "all") : null)
               )
         );
   }
}
