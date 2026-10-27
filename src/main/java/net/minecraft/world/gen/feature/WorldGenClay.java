package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenClay extends WorldGenerator {
   public Block clay;
   public int numberOfBlocks;

   public WorldGenClay(int n) {
      this.clay = Blocks.clay;
      this.numberOfBlocks = n;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.water) {
         return false;
      }

      int n = rand.nextInt(this.numberOfBlocks - 2) + 2;
      byte b = 1;

      for (int i = position.getX() - n; i <= position.getX() + n; i++) {
         for (int j = position.getZ() - n; j <= position.getZ() + n; j++) {
            int n2 = i - position.getX();
            int n3 = j - position.getZ();
            if (n2 * n2 + n3 * n3 <= n * n) {
               for (int k = position.getY() - b; k <= position.getY() + b; k++) {
                  BlockPos blockPos = new BlockPos(i, k, j);
                  Block block = worldIn.getBlockState(blockPos).getBlock();
                  if (block == Blocks.dirt || block == Blocks.clay) {
                     worldIn.setBlockState(blockPos, this.clay.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
