package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class GeneratorBushFeature extends WorldGenerator {
   public BlockBush block;

   public GeneratorBushFeature(BlockBush blockBush) {
      this.block = blockBush;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int i = 0; i < 64; i++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockPos)
            && (!worldIn.provider.getHasNoSky() || blockPos.getY() < 255)
            && this.block.canBlockStay(worldIn, blockPos, this.block.getDefaultState())) {
            worldIn.setBlockState(blockPos, this.block.getDefaultState(), 2);
         }
      }

      return true;
   }
}
