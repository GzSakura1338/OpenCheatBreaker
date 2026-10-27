package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenWaterlily extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int i = 0; i < 10; i++) {
         int n = position.getX() + rand.nextInt(8) - rand.nextInt(8);
         int n2 = position.getY() + rand.nextInt(4) - rand.nextInt(4);
         int n3 = position.getZ() + rand.nextInt(8) - rand.nextInt(8);
         if (worldIn.isAirBlock(new BlockPos(n, n2, n3)) && Blocks.waterlily.canPlaceBlockAt(worldIn, new BlockPos(n, n2, n3))) {
            worldIn.setBlockState(new BlockPos(n, n2, n3), Blocks.waterlily.getDefaultState(), 2);
         }
      }

      return true;
   }
}
