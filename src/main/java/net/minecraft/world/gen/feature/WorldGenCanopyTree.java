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

public class WorldGenCanopyTree extends WorldGenAbstractTree {
   public static IBlockState LOG = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, EnumType.DARK_OAK);
   public static IBlockState LEAVES = Blocks.leaves2
      .getDefaultState()
      .withProperty(BlockNewLeaf.VARIANT, EnumType.DARK_OAK)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   public WorldGenCanopyTree(boolean flag) {
      super(flag);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int n = rand.nextInt(3) + rand.nextInt(2) + 6;
      int n2 = position.getX();
      int n3 = position.getY();
      int n4 = position.getZ();
      if (n3 >= 1 && n3 + n + 1 < 256) {
         BlockPos down = position.down();
         Block block = worldIn.getBlockState(down).getBlock();
         if (block != Blocks.grass && block != Blocks.dirt) {
            return false;
         }

         if (!this.isAreaAboveClear(worldIn, position, n)) {
            return false;
         }

         this.setDirt(worldIn, down);
         this.setDirt(worldIn, down.east());
         this.setDirt(worldIn, down.south());
         this.setDirt(worldIn, down.south().east());
         EnumFacing random = Plane.HORIZONTAL.random(rand);
         int n5 = n - rand.nextInt(4);
         int n6 = 2 - rand.nextInt(3);
         int n7 = n2;
         int n8 = n4;
         int n9 = n3 + n - 1;

         for (int i = 0; i < n; i++) {
            if (i >= n5 && n6 > 0) {
               n7 += random.getFrontOffsetX();
               n8 += random.getFrontOffsetZ();
               n6--;
            }

            int n10 = n3 + i;
            BlockPos blockPos = new BlockPos(n7, n10, n8);
            Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
            if (material == Material.air || material == Material.leaves) {
               this.setDarkOakLog(worldIn, blockPos);
               this.setDarkOakLog(worldIn, blockPos.east());
               this.setDarkOakLog(worldIn, blockPos.south());
               this.setDarkOakLog(worldIn, blockPos.east().south());
            }
         }

         for (int i = -2; i <= 0; i++) {
            for (int j = -2; j <= 0; j++) {
               byte b = -1;
               this.setDarkOakLeaves(worldIn, n7 + i, n9 + b, n8 + j);
               this.setDarkOakLeaves(worldIn, 1 + n7 - i, n9 + b, n8 + j);
               this.setDarkOakLeaves(worldIn, n7 + i, n9 + b, 1 + n8 - j);
               this.setDarkOakLeaves(worldIn, 1 + n7 - i, n9 + b, 1 + n8 - j);
               if ((i > -2 || j > -1) && (i != -1 || j != -2)) {
                  b = 1;
                  this.setDarkOakLeaves(worldIn, n7 + i, n9 + b, n8 + j);
                  this.setDarkOakLeaves(worldIn, 1 + n7 - i, n9 + b, n8 + j);
                  this.setDarkOakLeaves(worldIn, n7 + i, n9 + b, 1 + n8 - j);
                  this.setDarkOakLeaves(worldIn, 1 + n7 - i, n9 + b, 1 + n8 - j);
               }
            }
         }

         if (rand.nextBoolean()) {
            this.setDarkOakLeaves(worldIn, n7, n9 + 2, n8);
            this.setDarkOakLeaves(worldIn, n7 + 1, n9 + 2, n8);
            this.setDarkOakLeaves(worldIn, n7 + 1, n9 + 2, n8 + 1);
            this.setDarkOakLeaves(worldIn, n7, n9 + 2, n8 + 1);
         }

         for (int i = -3; i <= 4; i++) {
            for (int j = -3; j <= 4; j++) {
               if ((i != -3 || j != -3)
                  && (i != -3 || j != 4)
                  && (i != 4 || j != -3)
                  && (i != 4 || j != 4)
                  && (Math.abs(i) < 3 || Math.abs(j) < 3)) {
                  this.setDarkOakLeaves(worldIn, n7 + i, n9, n8 + j);
               }
            }
         }

         for (int i = -1; i <= 2; i++) {
            for (int j = -1; j <= 2; j++) {
               if ((i < 0 || i > 1 || j < 0 || j > 1) && rand.nextInt(3) <= 0) {
                  int n10 = rand.nextInt(3) + 2;

                  for (int k = 0; k < n10; k++) {
                     this.setDarkOakLog(worldIn, new BlockPos(n2 + i, n9 - k - 1, n4 + j));
                  }

                  for (int k = -1; k <= 1; k++) {
                     for (int m = -1; m <= 1; m++) {
                        this.setDarkOakLeaves(worldIn, n7 + i + k, n9, n8 + j + m);
                     }
                  }

                  for (int k = -2; k <= 2; k++) {
                     for (int m = -2; m <= 2; m++) {
                        if (Math.abs(k) != 2 || Math.abs(m) != 2) {
                           this.setDarkOakLeaves(worldIn, n7 + i + k, n9 - 1, n8 + j + m);
                        }
                     }
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isAreaAboveClear(World world, BlockPos blockPos, int n) {
      int n2 = blockPos.getX();
      int n3 = blockPos.getY();
      int n4 = blockPos.getZ();
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = 0; i <= n + 1; i++) {
         byte b = 1;
         if (i == 0) {
            b = 0;
         }

         if (i >= n - 1) {
            b = 2;
         }

         for (int j = -b; j <= b; j++) {
            for (int k = -b; k <= b; k++) {
               if (!this.isBlockReplaceable(world.getBlockState(mutableBlockPos.set(n2 + j, n3 + i, n4 + k)).getBlock())) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public void setDarkOakLog(World world, BlockPos blockPos) {
      if (this.isBlockReplaceable(world.getBlockState(blockPos).getBlock())) {
         this.setBlockAndNotifyAdequately(world, blockPos, LOG);
      }
   }

   public void setDarkOakLeaves(World worldIn, int n, int n2, int n3) {
      BlockPos blockPos = new BlockPos(n, n2, n3);
      Block block = worldIn.getBlockState(blockPos).getBlock();
      if (block.getMaterial() == Material.air) {
         this.setBlockAndNotifyAdequately(worldIn, blockPos, LEAVES);
      }
   }
}
