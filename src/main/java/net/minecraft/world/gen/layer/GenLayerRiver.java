package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiver extends GenLayer {
   public GenLayerRiver(long l, GenLayer genLayer) {
      super(l);
      super.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int n = areaX - 1;
      int n2 = areaY - 1;
      int n3 = areaWidth + 2;
      int n4 = areaHeight + 2;
      int[] ints = this.parent.getInts(n, n2, n3, n4);
      int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaHeight; i++) {
         for (int j = 0; j < areaWidth; j++) {
            int computeInt = this.computeInt(ints[j + 0 + (i + 1) * n3]);
            int computeInt2 = this.computeInt(ints[j + 2 + (i + 1) * n3]);
            int computeInt3 = this.computeInt(ints[j + 1 + (i + 0) * n3]);
            int computeInt4 = this.computeInt(ints[j + 1 + (i + 2) * n3]);
            int computeInt5 = this.computeInt(ints[j + 1 + (i + 1) * n3]);
            if (computeInt5 == computeInt && computeInt5 == computeInt3 && computeInt5 == computeInt2 && computeInt5 == computeInt4) {
               intCache[j + i * areaWidth] = -1;
            } else {
               intCache[j + i * areaWidth] = BiomeGenBase.river.biomeID;
            }
         }
      }

      return intCache;
   }

   public int computeInt(int n) {
      return n >= 2 ? 2 + (n & 1) : n;
   }
}
