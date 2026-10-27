package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.util.BlockPos;

public class WorldChunkManagerHell extends WorldChunkManager {
   public BiomeGenBase biomeGenerator;
   public float rainfall;

   public WorldChunkManagerHell(BiomeGenBase biomeGenBase, float f) {
      this.biomeGenerator = biomeGenBase;
      this.rainfall = f;
   }

   @Override
   public BiomeGenBase getBiomeGenerator(BlockPos pos) {
      return this.biomeGenerator;
   }

   @Override
   public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int height) {
      if (biomes == null || biomes.length < width * height) {
         biomes = new BiomeGenBase[width * height];
      }

      Arrays.fill(biomes, 0, width * height, this.biomeGenerator);
      return biomes;
   }

   @Override
   public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length) {
      if (listToReuse == null || listToReuse.length < width * length) {
         listToReuse = new float[width * length];
      }

      Arrays.fill(listToReuse, 0, width * length, this.rainfall);
      return listToReuse;
   }

   @Override
   public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x, int z, int width, int depth) {
      if (oldBiomeList == null || oldBiomeList.length < width * depth) {
         oldBiomeList = new BiomeGenBase[width * depth];
      }

      Arrays.fill(oldBiomeList, 0, width * depth, this.biomeGenerator);
      return oldBiomeList;
   }

   @Override
   public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
      return this.loadBlockGeneratorData(listToReuse, x, z, width, length);
   }

   @Override
   public BlockPos findBiomePosition(int x, int z, int range, List<BiomeGenBase> biomes, Random random) {
      return biomes.contains(this.biomeGenerator)
         ? new BlockPos(x - range + random.nextInt(range * 2 + 1), 0, z - range + random.nextInt(range * 2 + 1))
         : null;
   }

   @Override
   public boolean areBiomesViable(int n, int n2, int n3, List<BiomeGenBase> list) {
      return list.contains(this.biomeGenerator);
   }
}
