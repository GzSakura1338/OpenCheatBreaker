package net.optifine;

import net.minecraft.world.World;

public class LightMapPack {
   public int[] colorBuffer1 = new int[0];
   public int[] colorBuffer2 = new int[0];
   public LightMap lightMap;
   public LightMap lightMapRain;
   public LightMap lightMapThunder;

   public LightMapPack(LightMap lightMap, LightMap lightMap2, LightMap lightMap3) {
      if (lightMap2 != null || lightMap3 != null) {
         if (lightMap2 == null) {
            lightMap2 = lightMap;
         }

         if (lightMap3 == null) {
            lightMap3 = lightMap2;
         }
      }

      this.lightMap = lightMap;
      this.lightMapRain = lightMap2;
      this.lightMapThunder = lightMap3;
   }

   public boolean updateLightmap(World world, float f, int[] ints, boolean flag, float f2) {
      if (this.lightMapRain == null && this.lightMapThunder == null) {
         return this.lightMap.updateLightmap(world, f, ints, flag);
      }

      int dimensionId = world.provider.getDimensionId();
      if (dimensionId != 1 && dimensionId != -1) {
         float rainStrength = world.getRainStrength(f2);
         float thunderStrength = world.getThunderStrength(f2);
         float f3 = 1.0E-4F;
         boolean flag2 = rainStrength > f3;
         boolean flag3 = thunderStrength > f3;
         if (!flag2 && !flag3) {
            return this.lightMap.updateLightmap(world, f, ints, flag);
         }

         if (rainStrength > 0.0F) {
            thunderStrength /= rainStrength;
         }

         float f4 = 1.0F - rainStrength;
         float f5 = rainStrength - thunderStrength;
         if (this.colorBuffer1.length != ints.length) {
            this.colorBuffer1 = new int[ints.length];
            this.colorBuffer2 = new int[ints.length];
         }

         int n = 0;
         int[][] intses = new int[][]{ints, this.colorBuffer1, this.colorBuffer2};
         float[] floats = new float[3];
         if (f4 > f3 && this.lightMap.updateLightmap(world, f, intses[n], flag)) {
            floats[n] = f4;
            n++;
         }

         if (f5 > f3 && this.lightMapRain != null && this.lightMapRain.updateLightmap(world, f, intses[n], flag)) {
            floats[n] = f5;
            n++;
         }

         if (thunderStrength > f3 && this.lightMapThunder != null && this.lightMapThunder.updateLightmap(world, f, intses[n], flag)) {
            floats[n] = thunderStrength;
            n++;
         }

         return n == 2
            ? this.blend(intses[0], floats[0], intses[1], floats[1])
            : (n == 3 ? this.blend(intses[0], floats[0], intses[1], floats[1], intses[2], floats[2]) : true);
      } else {
         return this.lightMap.updateLightmap(world, f, ints, flag);
      }
   }

   public boolean blend(int[] ints, float f, int[] ints2, float f2) {
      if (ints2.length != ints.length) {
         return false;
      }

      for (int i = 0; i < ints.length; i++) {
         int n = ints[i];
         int n2 = n >> 16 & 0xFF;
         int n3 = n >> 8 & 0xFF;
         int n4 = n & 0xFF;
         int n5 = ints2[i];
         int n6 = n5 >> 16 & 0xFF;
         int n7 = n5 >> 8 & 0xFF;
         int n8 = n5 & 0xFF;
         int n9 = (int)(n2 * f + n6 * f2);
         int n10 = (int)(n3 * f + n7 * f2);
         int n11 = (int)(n4 * f + n8 * f2);
         ints[i] = 0xFF000000 | n9 << 16 | n10 << 8 | n11;
      }

      return true;
   }

   public boolean blend(int[] ints, float f, int[] ints2, float f2, int[] ints3, float f3) {
      if (ints2.length == ints.length && ints3.length == ints.length) {
         for (int i = 0; i < ints.length; i++) {
            int n = ints[i];
            int n2 = n >> 16 & 0xFF;
            int n3 = n >> 8 & 0xFF;
            int n4 = n & 0xFF;
            int n5 = ints2[i];
            int n6 = n5 >> 16 & 0xFF;
            int n7 = n5 >> 8 & 0xFF;
            int n8 = n5 & 0xFF;
            int n9 = ints3[i];
            int n10 = n9 >> 16 & 0xFF;
            int n11 = n9 >> 8 & 0xFF;
            int n12 = n9 & 0xFF;
            int n13 = (int)(n2 * f + n6 * f2 + n10 * f3);
            int n14 = (int)(n3 * f + n7 * f2 + n11 * f3);
            int n15 = (int)(n4 * f + n8 * f2 + n12 * f3);
            ints[i] = 0xFF000000 | n13 << 16 | n14 << 8 | n15;
         }

         return true;
      } else {
         return false;
      }
   }
}
