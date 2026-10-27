package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenSwamp extends WorldGenAbstractTree {
   public static IBlockState LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.OAK);
   public static IBlockState LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, EnumType.OAK)
      .withProperty(BlockOldLeaf.CHECK_DECAY, false);

   public WorldGenSwamp() {
      super(false);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int n = rand.nextInt(4) + 5;

      while (worldIn.getBlockState(position.down()).getBlock().getMaterial() == Material.water) {
         position = position.down();
      }

      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + n + 1 <= 256) {
         for (int i = position.getY(); i <= position.getY() + 1 + n; i++) {
            byte b = 1;
            if (i == position.getY()) {
               b = 0;
            }

            if (i >= position.getY() + 1 + n - 2) {
               b = 3;
            }

            MutableBlockPos mutableBlockPos = new MutableBlockPos();

            for (int j = position.getX() - b; j <= position.getX() + b && flag; j++) {
               for (int k = position.getZ() - b; k <= position.getZ() + b && flag; k++) {
                  if (i >= 0 && i < 256) {
                     Block block = worldIn.getBlockState(mutableBlockPos.set(j, i, k)).getBlock();
                     if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                        if (block != Blocks.water && block != Blocks.flowing_water) {
                           flag = false;
                        } else if (i > position.getY()) {
                           flag = false;
                        }
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         }

         Block block = worldIn.getBlockState(position.down()).getBlock();
         if ((block == Blocks.grass || block == Blocks.dirt) && position.getY() < 256 - n - 1) {
            this.setDirt(worldIn, position.down());

            for (int i = position.getY() - 3 + n; i <= position.getY() + n; i++) {
               int n2 = i - (position.getY() + n);
               int n3 = 2 - n2 / 2;

               for (int j = position.getX() - n3; j <= position.getX() + n3; j++) {
                  int n4 = j - position.getX();

                  for (int k = position.getZ() - n3; k <= position.getZ() + n3; k++) {
                     int n5 = k - position.getZ();
                     if (Math.abs(n4) != n3 || Math.abs(n5) != n3 || rand.nextInt(2) != 0 && n2 != 0) {
                        BlockPos blockPos = new BlockPos(j, i, k);
                        if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, LEAVES);
                        }
                     }
                  }
               }
            }

            for (int i = 0; i < n; i++) {
               Block block2 = worldIn.getBlockState(position.up(i)).getBlock();
               if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves || block2 == Blocks.flowing_water || block2 == Blocks.water) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(i), LOG);
               }
            }

            for (int i = position.getY() - 3 + n; i <= position.getY() + n; i++) {
               int n2 = i - (position.getY() + n);
               int n3 = 2 - n2 / 2;
               MutableBlockPos mutableBlockPos = new MutableBlockPos();

               for (int j = position.getX() - n3; j <= position.getX() + n3; j++) {
                  for (int k = position.getZ() - n3; k <= position.getZ() + n3; k++) {
                     mutableBlockPos.set(j, i, k);
                     if (worldIn.getBlockState(mutableBlockPos).getBlock().getMaterial() == Material.leaves) {
                        BlockPos west = mutableBlockPos.west();
                        BlockPos east = mutableBlockPos.east();
                        BlockPos north = mutableBlockPos.north();
                        BlockPos south = mutableBlockPos.south();
                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(west).getBlock().getMaterial() == Material.air) {
                           this.generateVines(worldIn, west, BlockVine.EAST);
                        }

                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(east).getBlock().getMaterial() == Material.air) {
                           this.generateVines(worldIn, east, BlockVine.WEST);
                        }

                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(north).getBlock().getMaterial() == Material.air) {
                           this.generateVines(worldIn, north, BlockVine.SOUTH);
                        }

                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(south).getBlock().getMaterial() == Material.air) {
                           this.generateVines(worldIn, south, BlockVine.NORTH);
                        }
                     }
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void generateVines(World world, BlockPos blockPos, PropertyBool propertyBool) {
      IBlockState withProperty = Blocks.vine.getDefaultState().withProperty(propertyBool, true);
      this.setBlockAndNotifyAdequately(world, blockPos, withProperty);
      int n = 4;

      for (BlockPos down = blockPos.down(); world.getBlockState(down).getBlock().getMaterial() == Material.air && n > 0; n--) {
         this.setBlockAndNotifyAdequately(world, down, withProperty);
         down = down.down();
      }
   }
}
