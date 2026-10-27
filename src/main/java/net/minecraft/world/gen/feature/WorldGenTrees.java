package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLeaves;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;

public class WorldGenTrees extends WorldGenAbstractTree {
   public static IBlockState LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.OAK);
   public static IBlockState LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, EnumType.OAK)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   public int minTreeHeight;
   public IBlockState metaWood;
   public IBlockState metaLeaves;
   public boolean vinesGrow;

   public WorldGenTrees(boolean flag) {
      this(flag, 4, LOG, LEAVES, false);
   }

   public WorldGenTrees(boolean flag, int n, IBlockState blockState, IBlockState blockState2, boolean flag2) {
      super(flag);
      this.minTreeHeight = n;
      this.metaWood = blockState;
      this.metaLeaves = blockState2;
      this.vinesGrow = flag2;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int n = rand.nextInt(3) + this.minTreeHeight;
      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + n + 1 <= 256) {
         for (int i = position.getY(); i <= position.getY() + 1 + n; i++) {
            byte b = 1;
            if (i == position.getY()) {
               b = 0;
            }

            if (i >= position.getY() + 1 + n - 2) {
               b = 2;
            }

            MutableBlockPos mutableBlockPos = new MutableBlockPos();

            for (int j = position.getX() - b; j <= position.getX() + b && flag; j++) {
               for (int k = position.getZ() - b; k <= position.getZ() + b && flag; k++) {
                  if (i < 0 || i >= 256) {
                     flag = false;
                  } else if (!this.isBlockReplaceable(worldIn.getBlockState(mutableBlockPos.set(j, i, k)).getBlock())) {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         }

         Block block = worldIn.getBlockState(position.down()).getBlock();
         if ((block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland) && position.getY() < 256 - n - 1) {
            this.setDirt(worldIn, position.down());
            byte b = 3;
            byte b2 = 0;

            for (int i = position.getY() - b + n; i <= position.getY() + n; i++) {
               int n2 = i - (position.getY() + n);
               int n3 = b2 + 1 - n2 / 2;

               for (int j = position.getX() - n3; j <= position.getX() + n3; j++) {
                  int n4 = j - position.getX();

                  for (int k = position.getZ() - n3; k <= position.getZ() + n3; k++) {
                     int n5 = k - position.getZ();
                     if (Math.abs(n4) != n3 || Math.abs(n5) != n3 || rand.nextInt(2) != 0 && n2 != 0) {
                        BlockPos blockPos = new BlockPos(j, i, k);
                        Block block2 = worldIn.getBlockState(blockPos).getBlock();
                        if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves || block2.getMaterial() == Material.vine) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, this.metaLeaves);
                        }
                     }
                  }
               }
            }

            for (int i = 0; i < n; i++) {
               Block block2 = worldIn.getBlockState(position.up(i)).getBlock();
               if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves || block2.getMaterial() == Material.vine) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(i), this.metaWood);
                  if (this.vinesGrow && i > 0) {
                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(-1, i, 0))) {
                        this.setVines(worldIn, position.add(-1, i, 0), BlockVine.EAST);
                     }

                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(1, i, 0))) {
                        this.setVines(worldIn, position.add(1, i, 0), BlockVine.WEST);
                     }

                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, i, -1))) {
                        this.setVines(worldIn, position.add(0, i, -1), BlockVine.SOUTH);
                     }

                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, i, 1))) {
                        this.setVines(worldIn, position.add(0, i, 1), BlockVine.NORTH);
                     }
                  }
               }
            }

            if (this.vinesGrow) {
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

               if (rand.nextInt(5) == 0 && n > 5) {
                  for (int i = 0; i < 2; i++) {
                     for (EnumFacing enumFacing : Plane.HORIZONTAL) {
                        if (rand.nextInt(4 - i) == 0) {
                           EnumFacing opposite = enumFacing.getOpposite();
                           this.setCocoaBeans(worldIn, rand.nextInt(3), position.add(opposite.getFrontOffsetX(), n - 5 + i, opposite.getFrontOffsetZ()), enumFacing);
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

   public void setCocoaBeans(World world, int n, BlockPos blockPos, EnumFacing enumFacing) {
      this.setBlockAndNotifyAdequately(world, blockPos, Blocks.cocoa.getDefaultState().withProperty(BlockCocoa.AGE, n).withProperty(BlockCocoa.FACING, enumFacing));
   }

   public void setVines(World world, BlockPos blockPos, PropertyBool propertyBool) {
      this.setBlockAndNotifyAdequately(world, blockPos, Blocks.vine.getDefaultState().withProperty(propertyBool, true));
   }

   public void generateVines(World world, BlockPos blockPos, PropertyBool propertyBool) {
      this.setVines(world, blockPos, propertyBool);
      int n = 4;

      for (BlockPos down = blockPos.down(); world.getBlockState(down).getBlock().getMaterial() == Material.air && n > 0; n--) {
         this.setVines(world, down, propertyBool);
         down = down.down();
      }
   }
}
