package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;

public class WorldGenSavannaTree extends WorldGenAbstractTree {
   public static IBlockState LOG = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, EnumType.ACACIA);
   public static IBlockState LEAVES = Blocks.leaves2
      .getDefaultState()
      .withProperty(BlockNewLeaf.VARIANT, EnumType.ACACIA)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   public WorldGenSavannaTree(boolean flag) {
      super(flag);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int n = rand.nextInt(3) + rand.nextInt(3) + 5;
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
         if ((block == Blocks.grass || block == Blocks.dirt) && position.getY() < 256 - n - 1) {
            this.setDirt(worldIn, position.down());
            EnumFacing random = Plane.HORIZONTAL.random(rand);
            int n2 = n - rand.nextInt(4) - 1;
            int n3 = 3 - rand.nextInt(3);
            int n4 = position.getX();
            int n5 = position.getZ();
            int n6 = 0;

            for (int i = 0; i < n; i++) {
               int n7 = position.getY() + i;
               if (i >= n2 && n3 > 0) {
                  n4 += random.getFrontOffsetX();
                  n5 += random.getFrontOffsetZ();
                  n3--;
               }

               BlockPos blockPos = new BlockPos(n4, n7, n5);
               Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.setAcaciaLog(worldIn, blockPos);
                  n6 = n7;
               }
            }

            BlockPos blockPos = new BlockPos(n4, n6, n5);

            for (int i = -3; i <= 3; i++) {
               for (int j = -3; j <= 3; j++) {
                  if (Math.abs(i) != 3 || Math.abs(j) != 3) {
                     this.setAcaciaLeaves(worldIn, blockPos.add(i, 0, j));
                  }
               }
            }

            blockPos = blockPos.up();

            for (int i = -1; i <= 1; i++) {
               for (int j = -1; j <= 1; j++) {
                  this.setAcaciaLeaves(worldIn, blockPos.add(i, 0, j));
               }
            }

            this.setAcaciaLeaves(worldIn, blockPos.east(2));
            this.setAcaciaLeaves(worldIn, blockPos.west(2));
            this.setAcaciaLeaves(worldIn, blockPos.south(2));
            this.setAcaciaLeaves(worldIn, blockPos.north(2));
            n4 = position.getX();
            n5 = position.getZ();
            EnumFacing random2 = Plane.HORIZONTAL.random(rand);
            if (random2 != random) {
               int n7 = n2 - rand.nextInt(2) - 1;
               int n8 = 1 + rand.nextInt(3);
               n6 = 0;

               for (int i = n7; i < n && n8 > 0; n8--) {
                  if (i >= 1) {
                     int n9 = position.getY() + i;
                     n4 += random2.getFrontOffsetX();
                     n5 += random2.getFrontOffsetZ();
                     BlockPos blockPos2 = new BlockPos(n4, n9, n5);
                     Material material = worldIn.getBlockState(blockPos2).getBlock().getMaterial();
                     if (material == Material.air || material == Material.leaves) {
                        this.setAcaciaLog(worldIn, blockPos2);
                        n6 = n9;
                     }
                  }

                  i++;
               }

               if (n6 > 0) {
                  BlockPos blockPos2 = new BlockPos(n4, n6, n5);

                  for (int i = -2; i <= 2; i++) {
                     for (int j = -2; j <= 2; j++) {
                        if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                           this.setAcaciaLeaves(worldIn, blockPos2.add(i, 0, j));
                        }
                     }
                  }

                  blockPos2 = blockPos2.up();

                  for (int i = -1; i <= 1; i++) {
                     for (int j = -1; j <= 1; j++) {
                        this.setAcaciaLeaves(worldIn, blockPos2.add(i, 0, j));
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

   public void setAcaciaLog(World world, BlockPos blockPos) {
      this.setBlockAndNotifyAdequately(world, blockPos, LOG);
   }

   public void setAcaciaLeaves(World worldIn, BlockPos blockPos) {
      Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
      if (material == Material.air || material == Material.leaves) {
         this.setBlockAndNotifyAdequately(worldIn, blockPos, LEAVES);
      }
   }
}
