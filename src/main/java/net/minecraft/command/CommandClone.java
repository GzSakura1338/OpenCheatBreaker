package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandClone extends CommandBase {
   @Override
   public String getCommandName() {
      return "clone";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.clone.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 9) {
         throw new WrongUsageException("commands.clone.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos parseBlockPos2 = parseBlockPos(sender, args, 0, false);
      BlockPos parseBlockPos3 = parseBlockPos(sender, args, 3, false);
      BlockPos parseBlockPos4 = parseBlockPos(sender, args, 6, false);
      StructureBoundingBox structureBoundingBox = new StructureBoundingBox(parseBlockPos2, parseBlockPos3);
      StructureBoundingBox structureBoundingBox2 = new StructureBoundingBox(parseBlockPos4, parseBlockPos4.add(structureBoundingBox.getDimensions()));
      int n = structureBoundingBox.getXSize() * structureBoundingBox.getYSize() * structureBoundingBox.getZSize();
      if (n > 32768) {
         throw new CommandException("commands.clone.tooManyBlocks", n, 32768);
      }

      boolean flag = false;
      Block block = null;
      int n2 = -1;
      if ((args.length < 11 || !args[10].equals("force") && !args[10].equals("move")) && structureBoundingBox.intersectsWith(structureBoundingBox2)) {
         throw new CommandException("commands.clone.noOverlap");
      }

      if (args.length >= 11 && args[10].equals("move")) {
         flag = true;
      }

      if (structureBoundingBox.minY >= 0 && structureBoundingBox.maxY < 256 && structureBoundingBox2.minY >= 0 && structureBoundingBox2.maxY < 256) {
         World entityWorld = sender.getEntityWorld();
         if (entityWorld.isAreaLoaded(structureBoundingBox) && entityWorld.isAreaLoaded(structureBoundingBox2)) {
            boolean flag2 = false;
            if (args.length >= 10) {
               if (args[9].equals("masked")) {
                  flag2 = true;
               } else if (args[9].equals("filtered")) {
                  if (args.length < 12) {
                     throw new WrongUsageException("commands.clone.usage");
                  }

                  block = getBlockByText(sender, args[11]);
                  if (args.length >= 13) {
                     n2 = parseInt(args[12], 0, 15);
                  }
               }
            }

            ArrayList newArrayList = Lists.newArrayList();
            ArrayList newArrayList2 = Lists.newArrayList();
            ArrayList newArrayList3 = Lists.newArrayList();
            LinkedList newLinkedList = Lists.newLinkedList();
            BlockPos blockPos = new BlockPos(structureBoundingBox2.minX - structureBoundingBox.minX, structureBoundingBox2.minY - structureBoundingBox.minY, structureBoundingBox2.minZ - structureBoundingBox.minZ);

            for (int minZ = structureBoundingBox.minZ; minZ <= structureBoundingBox.maxZ; minZ++) {
               for (int minY = structureBoundingBox.minY; minY <= structureBoundingBox.maxY; minY++) {
                  for (int minX = structureBoundingBox.minX; minX <= structureBoundingBox.maxX; minX++) {
                     BlockPos blockPos2 = new BlockPos(minX, minY, minZ);
                     BlockPos blockPos3 = blockPos2.add(blockPos);
                     IBlockState blockState = entityWorld.getBlockState(blockPos2);
                     if ((!flag2 || blockState.getBlock() != Blocks.air)
                        && (block == null || blockState.getBlock() == block && (n2 < 0 || blockState.getBlock().getMetaFromState(blockState) == n2))) {
                        TileEntity tileEntity = entityWorld.getTileEntity(blockPos2);
                        if (tileEntity != null) {
                           NBTTagCompound nbttagCompound = new NBTTagCompound();
                           tileEntity.writeToNBT(nbttagCompound);
                           newArrayList2.add(new CommandClone.StaticCloneData(blockPos3, blockState, nbttagCompound));
                           newLinkedList.addLast(blockPos2);
                        } else if (!blockState.getBlock().isFullBlock() && !blockState.getBlock().isFullCube()) {
                           newArrayList3.add(new CommandClone.StaticCloneData(blockPos3, blockState, (NBTTagCompound)null));
                           newLinkedList.addFirst(blockPos2);
                        } else {
                           newArrayList.add(new CommandClone.StaticCloneData(blockPos3, blockState, (NBTTagCompound)null));
                           newLinkedList.addLast(blockPos2);
                        }
                     }
                  }
               }
            }

            if (flag) {
               for (BlockPos blockPos2 : (Iterable<BlockPos>)newLinkedList) {
                  TileEntity tileEntity = entityWorld.getTileEntity(blockPos2);
                  if (tileEntity instanceof IInventory) {
                     ((IInventory)tileEntity).clear();
                  }

                  entityWorld.setBlockState(blockPos2, Blocks.barrier.getDefaultState(), 2);
               }

               for (BlockPos blockPos2 : (Iterable<BlockPos>)newLinkedList) {
                  entityWorld.setBlockState(blockPos2, Blocks.air.getDefaultState(), 3);
               }
            }

            ArrayList newArrayList4 = Lists.newArrayList();
            newArrayList4.addAll(newArrayList);
            newArrayList4.addAll(newArrayList2);
            newArrayList4.addAll(newArrayList3);
            List reverse = Lists.reverse(newArrayList4);

            for (CommandClone.StaticCloneData staticCloneData : (Iterable<CommandClone.StaticCloneData>)reverse) {
               TileEntity tileEntity = entityWorld.getTileEntity(staticCloneData.pos);
               if (tileEntity instanceof IInventory) {
                  ((IInventory)tileEntity).clear();
               }

               entityWorld.setBlockState(staticCloneData.pos, Blocks.barrier.getDefaultState(), 2);
            }

            n = 0;

            for (CommandClone.StaticCloneData staticCloneData : (Iterable<CommandClone.StaticCloneData>)newArrayList4) {
               if (entityWorld.setBlockState(staticCloneData.pos, staticCloneData.blockState, 2)) {
                  n++;
               }
            }

            for (CommandClone.StaticCloneData staticCloneData : (Iterable<CommandClone.StaticCloneData>)newArrayList2) {
               TileEntity tileEntity = entityWorld.getTileEntity(staticCloneData.pos);
               if (staticCloneData.compound != null && tileEntity != null) {
                  staticCloneData.compound.setInteger("x", staticCloneData.pos.getX());
                  staticCloneData.compound.setInteger("y", staticCloneData.pos.getY());
                  staticCloneData.compound.setInteger("z", staticCloneData.pos.getZ());
                  tileEntity.readFromNBT(staticCloneData.compound);
                  tileEntity.markDirty();
               }

               entityWorld.setBlockState(staticCloneData.pos, staticCloneData.blockState, 2);
            }

            for (CommandClone.StaticCloneData staticCloneData : (Iterable<CommandClone.StaticCloneData>)reverse) {
               entityWorld.notifyNeighborsRespectDebug(staticCloneData.pos, staticCloneData.blockState.getBlock());
            }

            List findList = entityWorld.findList(structureBoundingBox, false);
            if (findList != null) {
               for (NextTickListEntry nextTickListEntry : (Iterable<NextTickListEntry>)findList) {
                  if (structureBoundingBox.isVecInside(nextTickListEntry.position)) {
                     BlockPos blockPos2 = nextTickListEntry.position.add(blockPos);
                     entityWorld.scheduleBlockUpdate(blockPos2, nextTickListEntry.getBlock(), (int)(nextTickListEntry.scheduledTime - entityWorld.getWorldInfo().getWorldTotalTime()), nextTickListEntry.priority);
                  }
               }
            }

            if (n <= 0) {
               throw new CommandException("commands.clone.failed");
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, n);
            notifyOperators(sender, this, "commands.clone.success", n);
         } else {
            throw new CommandException("commands.clone.outOfWorld");
         }
      } else {
         throw new CommandException("commands.clone.outOfWorld");
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
                     : (
                        args.length == 10
                           ? getListOfStringsMatchingLastWord(args, "replace", "masked", "filtered")
                           : (
                              args.length == 11
                                 ? getListOfStringsMatchingLastWord(args, "normal", "force", "move")
                                 : (
                                    args.length == 12 && "filtered".equals(args[9])
                                       ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys())
                                       : null
                                 )
                           )
                     )
               )
         );
   }

   static class StaticCloneData {
      public BlockPos pos;
      public IBlockState blockState;
      public NBTTagCompound compound;

      public StaticCloneData(BlockPos posIn, IBlockState stateIn, NBTTagCompound compoundIn) {
         this.pos = posIn;
         this.blockState = stateIn;
         this.compound = compoundIn;
      }
   }
}
