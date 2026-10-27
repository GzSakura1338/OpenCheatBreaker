package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenCactus extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int i = 0; i < 10; i++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockPos)) {
            int n = 1 + rand.nextInt(rand.nextInt(3) + 1);

            for (int j = 0; j < n; j++) {
               if (Blocks.cactus.canBlockStay(worldIn, blockPos)) {
                  worldIn.setBlockState(blockPos.up(j), Blocks.cactus.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }
}
