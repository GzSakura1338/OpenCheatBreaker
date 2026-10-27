package net.minecraft.world.gen.layer;

public class GenLayerIsland extends GenLayer {
   public GenLayerIsland(long l) {
      super(l);
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaHeight; i++) {
         for (int j = 0; j < areaWidth; j++) {
            this.initChunkSeed(areaX + j, areaY + i);
            intCache[j + i * areaWidth] = this.nextInt(10) == 0 ? 1 : 0;
         }
      }

      if (areaX > -areaWidth && areaX <= 0 && areaY > -areaHeight && areaY <= 0) {
         intCache[-areaX + -areaY * areaWidth] = 1;
      }

      return intCache;
   }
}
