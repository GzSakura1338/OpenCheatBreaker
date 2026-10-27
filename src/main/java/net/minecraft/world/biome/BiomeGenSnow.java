package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenIcePath;
import net.minecraft.world.gen.feature.WorldGenIceSpike;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

public class BiomeGenSnow extends BiomeGenBase {
   public WorldGenIceSpike iceSpikeFeature = new WorldGenIceSpike();
   public WorldGenIcePath worldGenIcePath = new WorldGenIcePath(4);
   public boolean flag;

   public BiomeGenSnow(int id, boolean flag) {
      super(id);
      this.flag = flag;
      if (flag) {
         this.topBlock = Blocks.snow.getDefaultState();
      }

      this.spawnableCreatureList.clear();
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      if (this.flag) {
         for (int i = 0; i < 3; i++) {
            int n = rand.nextInt(16) + 8;
            int n2 = rand.nextInt(16) + 8;
            this.iceSpikeFeature.generate(worldIn, rand, worldIn.getHeight(pos.add(n, 0, n2)));
         }

         for (int i = 0; i < 2; i++) {
            int n = rand.nextInt(16) + 8;
            int n2 = rand.nextInt(16) + 8;
            this.worldGenIcePath.generate(worldIn, rand, worldIn.getHeight(pos.add(n, 0, n2)));
         }
      }

      super.decorate(worldIn, rand, pos);
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return new WorldGenTaiga2(false);
   }

   @Override
   public BiomeGenBase createMutatedBiome(int n) {
      BiomeGenBase setHeight = new BiomeGenSnow(n, true)
         .findBiomeGenBase(13828095, true)
         .setBiomeName(this.biomeName + " Spikes")
         .setEnableSnow()
         .setTemperatureRainfall(0.0F, 0.5F)
         .setHeight(new BiomeGenBase.Height(this.minHeight + 0.1F, this.maxHeight + 0.1F));
      setHeight.minHeight = this.minHeight + 0.3F;
      setHeight.maxHeight = this.maxHeight + 0.4F;
      return setHeight;
   }
}
