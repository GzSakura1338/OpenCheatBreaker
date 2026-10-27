package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenIcePath extends WorldGenerator {
   public Block block = Blocks.packed_ice;
   public int basePathWidth;

   public WorldGenIcePath(int n) {
      this.basePathWidth = n;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (worldIn.isAirBlock(position) && position.getY() > 2) {
         position = position.down();
      }

      if (worldIn.getBlockState(position).getBlock() != Blocks.snow) {
         return false;
      }

      int n = rand.nextInt(this.basePathWidth - 2) + 2;
      byte b = 1;

      for (int i = position.getX() - n; i <= position.getX() + n; i++) {
         for (int j = position.getZ() - n; j <= position.getZ() + n; j++) {
            int n2 = i - position.getX();
            int n3 = j - position.getZ();
            if (n2 * n2 + n3 * n3 <= n * n) {
               for (int k = position.getY() - b; k <= position.getY() + b; k++) {
                  BlockPos blockPos = new BlockPos(i, k, j);
                  Block block = worldIn.getBlockState(blockPos).getBlock();
                  if (block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice) {
                     worldIn.setBlockState(blockPos, this.block.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
