package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiverMix extends GenLayer {
   public GenLayer biomePatternGeneratorChain;
   public GenLayer riverPatternGeneratorChain;

   public GenLayerRiverMix(long l, GenLayer genLayer, GenLayer genLayer2) {
      super(l);
      this.biomePatternGeneratorChain = genLayer;
      this.riverPatternGeneratorChain = genLayer2;
   }

   @Override
   public void initWorldGenSeed(long seed) {
      this.biomePatternGeneratorChain.initWorldGenSeed(seed);
      this.riverPatternGeneratorChain.initWorldGenSeed(seed);
      super.initWorldGenSeed(seed);
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] ints = this.biomePatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] ints2 = this.riverPatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaWidth * areaHeight; i++) {
         if (ints[i] == BiomeGenBase.ocean.biomeID || ints[i] == BiomeGenBase.deepOcean.biomeID) {
            intCache[i] = ints[i];
         } else if (ints2[i] == BiomeGenBase.river.biomeID) {
            if (ints[i] == BiomeGenBase.icePlains.biomeID) {
               intCache[i] = BiomeGenBase.frozenRiver.biomeID;
            } else if (ints[i] != BiomeGenBase.mushroomIsland.biomeID && ints[i] != BiomeGenBase.mushroomIslandShore.biomeID) {
               intCache[i] = ints2[i] & 0xFF;
            } else {
               intCache[i] = BiomeGenBase.mushroomIslandShore.biomeID;
            }
         } else {
            intCache[i] = ints[i];
         }
      }

      return intCache;
   }
}
