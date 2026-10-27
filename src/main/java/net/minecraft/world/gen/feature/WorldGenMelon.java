package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenMelon extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int i = 0; i < 64; i++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (Blocks.melon_block.canPlaceBlockAt(worldIn, blockPos) && worldIn.getBlockState(blockPos.down()).getBlock() == Blocks.grass) {
            worldIn.setBlockState(blockPos, Blocks.melon_block.getDefaultState(), 2);
         }
      }

      return true;
   }
}
