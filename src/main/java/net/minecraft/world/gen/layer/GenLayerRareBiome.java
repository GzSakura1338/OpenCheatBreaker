package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRareBiome extends GenLayer {
   public GenLayerRareBiome(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] ints = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
      int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaHeight; i++) {
         for (int j = 0; j < areaWidth; j++) {
            this.initChunkSeed(j + areaX, i + areaY);
            int n = ints[j + 1 + (i + 1) * (areaWidth + 2)];
            if (this.nextInt(57) == 0) {
               if (n == BiomeGenBase.plains.biomeID) {
                  intCache[j + i * areaWidth] = BiomeGenBase.plains.biomeID + 128;
               } else {
                  intCache[j + i * areaWidth] = n;
               }
            } else {
               intCache[j + i * areaWidth] = n;
            }
         }
      }

      return intCache;
   }
}
