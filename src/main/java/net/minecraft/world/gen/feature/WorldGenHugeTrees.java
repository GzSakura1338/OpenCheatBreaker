package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenHugeTrees extends WorldGenAbstractTree {
   public int baseHeight;
   public int extraRandomHeight;
   public IBlockState woodMetadata;
   public IBlockState leavesMetadata;

   public WorldGenHugeTrees(boolean flag, int n, int n2, IBlockState blockState, IBlockState blockState2) {
      super(flag);
      this.baseHeight = n;
      this.extraRandomHeight = n2;
      this.woodMetadata = blockState;
      this.leavesMetadata = blockState2;
   }

   public int calculateMaxHeight(Random random) {
      int n = random.nextInt(3) + this.baseHeight;
      if (this.extraRandomHeight > 1) {
         n += random.nextInt(this.extraRandomHeight);
      }

      return n;
   }

   public boolean canGenerate(World worldIn, BlockPos blockPos, int n) {
      boolean flag = true;
      if (blockPos.getY() >= 1 && blockPos.getY() + n + 1 <= 256) {
         for (int i = 0; i <= 1 + n; i++) {
            byte b = 2;
            if (i == 0) {
               b = 1;
            } else if (i >= 1 + n - 2) {
               b = 2;
            }

            for (int j = -b; j <= b && flag; j++) {
               for (int k = -b; k <= b && flag; k++) {
                  if (blockPos.getY() + i < 0
                     || blockPos.getY() + i >= 256
                     || !this.isBlockReplaceable(worldIn.getBlockState(blockPos.add(j, i, k)).getBlock())) {
                     flag = false;
                  }
               }
            }
         }

         return flag;
      } else {
         return false;
      }
   }

   public boolean checkAndSetDirt(BlockPos blockPos, World worldIn) {
      BlockPos down = blockPos.down();
      Block block = worldIn.getBlockState(down).getBlock();
      if ((block == Blocks.grass || block == Blocks.dirt) && blockPos.getY() >= 2) {
         this.setDirt(worldIn, down);
         this.setDirt(worldIn, down.east());
         this.setDirt(worldIn, down.south());
         this.setDirt(worldIn, down.south().east());
         return true;
      } else {
         return false;
      }
   }

   public boolean canGenerate(World worldIn, Random random, BlockPos blockPos, int n) {
      return this.canGenerate(worldIn, blockPos, n) && this.checkAndSetDirt(blockPos, worldIn);
   }

   public void generateLeavesLimited(World worldIn, BlockPos blockPos, int n) {
      int n2 = n * n;

      for (int i = -n; i <= n + 1; i++) {
         for (int j = -n; j <= n + 1; j++) {
            int n3 = i - 1;
            int n4 = j - 1;
            if (i * i + j * j <= n2
               || n3 * n3 + n4 * n4 <= n2
               || i * i + n4 * n4 <= n2
               || n3 * n3 + j * j <= n2) {
               BlockPos blockPos2 = blockPos.add(i, 0, j);
               Material material = worldIn.getBlockState(blockPos2).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, blockPos2, this.leavesMetadata);
               }
            }
         }
      }
   }

   public void generateLeaves(World worldIn, BlockPos blockPos, int n) {
      int n2 = n * n;

      for (int i = -n; i <= n; i++) {
         for (int j = -n; j <= n; j++) {
            if (i * i + j * j <= n2) {
               BlockPos blockPos2 = blockPos.add(i, 0, j);
               Material material = worldIn.getBlockState(blockPos2).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, blockPos2, this.leavesMetadata);
               }
            }
         }
      }
   }
}
