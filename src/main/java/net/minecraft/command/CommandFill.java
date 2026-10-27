package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandFill extends CommandBase {
   @Override
   public String getCommandName() {
      return "fill";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.fill.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 7) {
         throw new WrongUsageException("commands.fill.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos parseBlockPos2 = parseBlockPos(sender, args, 0, false);
      BlockPos parseBlockPos3 = parseBlockPos(sender, args, 3, false);
      Block blockByText = CommandBase.getBlockByText(sender, args[6]);
      int n = 0;
      if (args.length >= 8) {
         n = parseInt(args[7], 0, 15);
      }

      BlockPos blockPos = new BlockPos(Math.min(parseBlockPos2.getX(), parseBlockPos3.getX()), Math.min(parseBlockPos2.getY(), parseBlockPos3.getY()), Math.min(parseBlockPos2.getZ(), parseBlockPos3.getZ()));
      BlockPos blockPos2 = new BlockPos(Math.max(parseBlockPos2.getX(), parseBlockPos3.getX()), Math.max(parseBlockPos2.getY(), parseBlockPos3.getY()), Math.max(parseBlockPos2.getZ(), parseBlockPos3.getZ()));
      int n2 = (blockPos2.getX() - blockPos.getX() + 1) * (blockPos2.getY() - blockPos.getY() + 1) * (blockPos2.getZ() - blockPos.getZ() + 1);
      if (n2 > 32768) {
         throw new CommandException("commands.fill.tooManyBlocks", n2, 32768);
      }

      if (blockPos.getY() >= 0 && blockPos2.getY() < 256) {
         World entityWorld = sender.getEntityWorld();

         for (int i = blockPos.getZ(); i < blockPos2.getZ() + 16; i += 16) {
            for (int j = blockPos.getX(); j < blockPos2.getX() + 16; j += 16) {
               if (!entityWorld.isBlockLoaded(new BlockPos(j, blockPos2.getY() - blockPos.getY(), i))) {
                  throw new CommandException("commands.fill.outOfWorld");
               }
            }
         }

         NBTTagCompound nbttagCompound = new NBTTagCompound();
         boolean flag = false;
         if (args.length >= 10 && blockByText.hasTileEntity()) {
            String unformattedText = getChatComponentFromNthArg(sender, args, 9).getUnformattedText();

            try {
               nbttagCompound = JsonToNBT.getTagFromJson(unformattedText);
               flag = true;
            } catch (NBTException e) {
               throw new CommandException("commands.fill.tagError", e.getMessage());
            }
         }

         ArrayList newArrayList = Lists.newArrayList();
         n2 = 0;

         for (int i = blockPos.getZ(); i <= blockPos2.getZ(); i++) {
            for (int j = blockPos.getY(); j <= blockPos2.getY(); j++) {
               for (int k = blockPos.getX(); k <= blockPos2.getX(); k++) {
                  BlockPos blockPos3 = new BlockPos(k, j, i);
                  if (args.length >= 9) {
                     if (!args[8].equals("outline") && !args[8].equals("hollow")) {
                        if (args[8].equals("destroy")) {
                           entityWorld.destroyBlock(blockPos3, true);
                        } else if (args[8].equals("keep")) {
                           if (!entityWorld.isAirBlock(blockPos3)) {
                              continue;
                           }
                        } else if (args[8].equals("replace") && !blockByText.hasTileEntity()) {
                           if (args.length > 9) {
                              Block blockByText2 = CommandBase.getBlockByText(sender, args[9]);
                              if (entityWorld.getBlockState(blockPos3).getBlock() != blockByText2) {
                                 continue;
                              }
                           }

                           if (args.length > 10) {
                              int parseInt = CommandBase.parseInt(args[10]);
                              IBlockState blockState = entityWorld.getBlockState(blockPos3);
                              if (blockState.getBlock().getMetaFromState(blockState) != parseInt) {
                                 continue;
                              }
                           }
                        }
                     } else if (k != blockPos.getX()
                        && k != blockPos2.getX()
                        && j != blockPos.getY()
                        && j != blockPos2.getY()
                        && i != blockPos.getZ()
                        && i != blockPos2.getZ()) {
                        if (args[8].equals("hollow")) {
                           entityWorld.setBlockState(blockPos3, Blocks.air.getDefaultState(), 2);
                           newArrayList.add(blockPos3);
                        }
                        continue;
                     }
                  }

                  TileEntity tileEntity = entityWorld.getTileEntity(blockPos3);
                  if (tileEntity != null) {
                     if (tileEntity instanceof IInventory) {
                        ((IInventory)tileEntity).clear();
                     }

                     entityWorld.setBlockState(blockPos3, Blocks.barrier.getDefaultState(), blockByText == Blocks.barrier ? 2 : 4);
                  }

                  IBlockState stateFromMeta = blockByText.getStateFromMeta(n);
                  if (entityWorld.setBlockState(blockPos3, stateFromMeta, 2)) {
                     newArrayList.add(blockPos3);
                     n2++;
                     if (flag) {
                        TileEntity tileEntity2 = entityWorld.getTileEntity(blockPos3);
                        if (tileEntity2 != null) {
                           nbttagCompound.setInteger("x", blockPos3.getX());
                           nbttagCompound.setInteger("y", blockPos3.getY());
                           nbttagCompound.setInteger("z", blockPos3.getZ());
                           tileEntity2.readFromNBT(nbttagCompound);
                        }
                     }
                  }
               }
            }
         }

         for (BlockPos blockPos3 : (Iterable<BlockPos>)newArrayList) {
            Block block = entityWorld.getBlockState(blockPos3).getBlock();
            entityWorld.notifyNeighborsRespectDebug(blockPos3, block);
         }

         if (n2 <= 0) {
            throw new CommandException("commands.fill.failed");
         }

         sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, n2);
         notifyOperators(sender, this, "commands.fill.success", n2);
      } else {
         throw new CommandException("commands.fill.outOfWorld");
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
                  args.length == 7
                     ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys())
                     : (
                        args.length == 9
                           ? getListOfStringsMatchingLastWord(args, "replace", "destroy", "keep", "hollow", "outline")
                           : (args.length == 10 && "replace".equals(args[8]) ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null)
                     )
               )
         );
   }
}
