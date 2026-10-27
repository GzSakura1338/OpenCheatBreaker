package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class BiomeGenDesert extends BiomeGenBase {
   public BiomeGenDesert(int id) {
      super(id);
      this.spawnableCreatureList.clear();
      this.topBlock = Blocks.sand.getDefaultState();
      this.fillerBlock = Blocks.sand.getDefaultState();
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.deadBushPerChunk = 2;
      this.theBiomeDecorator.reedsPerChunk = 50;
      this.theBiomeDecorator.cactiPerChunk = 10;
      this.spawnableCreatureList.clear();
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
      if (rand.nextInt(1000) == 0) {
         int n = rand.nextInt(16) + 8;
         int n2 = rand.nextInt(16) + 8;
         BlockPos up = worldIn.getHeight(pos.add(n, 0, n2)).up();
         new WorldGenDesertWells().generate(worldIn, rand, up);
      }
   }
}
