package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (worldIn.isAirBlock(position) && position.getY() > 2) {
         position = position.down();
      }

      if (worldIn.getBlockState(position).getBlock() != Blocks.snow) {
         return false;
      }

      position = position.up(rand.nextInt(4));
      int n = rand.nextInt(4) + 7;
      int n2 = n / 4 + rand.nextInt(2);
      if (n2 > 1 && rand.nextInt(60) == 0) {
         position = position.up(10 + rand.nextInt(30));
      }

      for (int i = 0; i < n; i++) {
         float f = (1.0F - (float)i / n) * n2;
         int n3 = MathHelper.ceiling_float_int(f);

         for (int j = -n3; j <= n3; j++) {
            float f2 = MathHelper.abs_int(j) - 0.25F;

            for (int k = -n3; k <= n3; k++) {
               float f3 = MathHelper.abs_int(k) - 0.25F;
               if ((j == 0 && k == 0 || f2 * f2 + f3 * f3 <= f * f)
                  && (j != -n3 && j != n3 && k != -n3 && k != n3 || rand.nextFloat() <= 0.75F)) {
                  Block block = worldIn.getBlockState(position.add(j, i, k)).getBlock();
                  if (block.getMaterial() == Material.air || block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice) {
                     this.setBlockAndNotifyAdequately(worldIn, position.add(j, i, k), Blocks.packed_ice.getDefaultState());
                  }

                  if (i != 0 && n3 > 1) {
                     block = worldIn.getBlockState(position.add(j, -i, k)).getBlock();
                     if (block.getMaterial() == Material.air || block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice) {
                        this.setBlockAndNotifyAdequately(worldIn, position.add(j, -i, k), Blocks.packed_ice.getDefaultState());
                     }
                  }
               }
            }
         }
      }

      int n3 = n2 - 1;
      if (n3 < 0) {
         n3 = 0;
      } else if (n3 > 1) {
         n3 = 1;
      }

      for (int i = -n3; i <= n3; i++) {
         for (int j = -n3; j <= n3; j++) {
            BlockPos blockPos = position.add(i, -1, j);
            int n4 = 50;
            if (Math.abs(i) == 1 && Math.abs(j) == 1) {
               n4 = rand.nextInt(5);
            }

            while (blockPos.getY() > 50) {
               Block block = worldIn.getBlockState(blockPos).getBlock();
               if (block.getMaterial() != Material.air && block != Blocks.dirt && block != Blocks.snow && block != Blocks.ice && block != Blocks.packed_ice) {
                  break;
               }

               this.setBlockAndNotifyAdequately(worldIn, blockPos, Blocks.packed_ice.getDefaultState());
               blockPos = blockPos.down();
               if (--n4 <= 0) {
                  blockPos = blockPos.down(rand.nextInt(5) + 1);
                  n4 = rand.nextInt(5);
               }
            }
         }
      }

      return true;
   }
}
