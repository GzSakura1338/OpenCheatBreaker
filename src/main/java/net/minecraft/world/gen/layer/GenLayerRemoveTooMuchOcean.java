package net.minecraft.world.gen.layer;

public class GenLayerRemoveTooMuchOcean extends GenLayer {
   public GenLayerRemoveTooMuchOcean(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
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
            int n5 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
            int n6 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
            int n7 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
            int n8 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
            int n9 = ints[j + 1 + (i + 1) * n3];
            intCache[j + i * areaWidth] = n9;
            this.initChunkSeed(j + areaX, i + areaY);
            if (n9 == 0 && n5 == 0 && n6 == 0 && n7 == 0 && n8 == 0 && this.nextInt(2) == 0) {
               intCache[j + i * areaWidth] = 1;
            }
         }
      }

      return intCache;
   }
}
