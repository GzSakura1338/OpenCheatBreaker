package net.optifine.util;

public class CacheLocal {
   public int maxX = 18;
   public int maxY = 128;
   public int maxZ = 18;
   public int offsetX = 0;
   public int offsetY = 0;
   public int offsetZ = 0;
   public int[][][] cache = (int[][][])null;
   public int[] lastZs = null;
   public int lastDz = 0;

   public CacheLocal(int n, int n2, int n3) {
      this.maxX = n;
      this.maxY = n2;
      this.maxZ = n3;
      this.cache = new int[n][n2][n3];
      this.resetCache();
   }

   public void resetCache() {
      for (int i = 0; i < this.maxX; i++) {
         int[][] intses = this.cache[i];

         for (int j = 0; j < this.maxY; j++) {
            int[] ints = intses[j];

            for (int k = 0; k < this.maxZ; k++) {
               ints[k] = -1;
            }
         }
      }
   }

   public void setOffset(int n, int n2, int n3) {
      this.offsetX = n;
      this.offsetY = n2;
      this.offsetZ = n3;
      this.resetCache();
   }

   public int get(int n, int n2, int n3) {
      try {
         this.lastZs = this.cache[n - this.offsetX][n2 - this.offsetY];
         this.lastDz = n3 - this.offsetZ;
         return this.lastZs[this.lastDz];
      } catch (ArrayIndexOutOfBoundsException e) {
         e.printStackTrace();
         return -1;
      }
   }

   public void setLast(int n) {
      try {
         this.lastZs[this.lastDz] = n;
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
