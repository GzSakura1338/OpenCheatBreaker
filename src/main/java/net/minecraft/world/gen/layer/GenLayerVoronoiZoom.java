package net.minecraft.world.gen.layer;

public class GenLayerVoronoiZoom extends GenLayer {
   public GenLayerVoronoiZoom(long l, GenLayer genLayer) {
      super(l);
      super.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      areaX -= 2;
      areaY -= 2;
      int n = areaX >> 2;
      int n2 = areaY >> 2;
      int n3 = (areaWidth >> 2) + 2;
      int n4 = (areaHeight >> 2) + 2;
      int[] ints = this.parent.getInts(n, n2, n3, n4);
      int n5 = n3 - 1 << 2;
      int n6 = n4 - 1 << 2;
      int[] intCache = IntCache.getIntCache(n5 * n6);

      for (int i = 0; i < n4 - 1; i++) {
         int n7 = 0;
         int n8 = ints[n7 + 0 + (i + 0) * n3];
         int n9 = ints[n7 + 0 + (i + 1) * n3];

         while (n7 < n3 - 1) {
            double d = 3.6;
            this.initChunkSeed(n7 + n << 2, i + n2 << 2);
            double d2 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            double d3 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            this.initChunkSeed(n7 + n + 1 << 2, i + n2 << 2);
            double d4 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            double d5 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            this.initChunkSeed(n7 + n << 2, i + n2 + 1 << 2);
            double d6 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            double d7 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            this.initChunkSeed(n7 + n + 1 << 2, i + n2 + 1 << 2);
            double d8 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            double d9 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            int n10 = ints[n7 + 1 + (i + 0) * n3] & 0xFF;
            int n11 = ints[n7 + 1 + (i + 1) * n3] & 0xFF;

            for (int j = 0; j < 4; j++) {
               int n12 = ((i << 2) + j) * n5 + (n7 << 2);

               for (int k = 0; k < 4; k++) {
                  double d10 = (j - d3) * (j - d3) + (k - d2) * (k - d2);
                  double d11 = (j - d5) * (j - d5) + (k - d4) * (k - d4);
                  double d12 = (j - d7) * (j - d7) + (k - d6) * (k - d6);
                  double d13 = (j - d9) * (j - d9) + (k - d8) * (k - d8);
                  if (d10 < d11 && d10 < d12 && d10 < d13) {
                     intCache[n12++] = n8;
                  } else if (d11 < d10 && d11 < d12 && d11 < d13) {
                     intCache[n12++] = n10;
                  } else if (d12 < d10 && d12 < d11 && d12 < d13) {
                     intCache[n12++] = n9;
                  } else {
                     intCache[n12++] = n11;
                  }
               }
            }

            n8 = n10;
            n9 = n11;
            n7++;
         }
      }

      int[] intCache2 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaHeight; i++) {
         System.arraycopy(intCache, (i + (areaY & 3)) * n5 + (areaX & 3), intCache2, i * areaWidth, areaWidth);
      }

      return intCache2;
   }
}
