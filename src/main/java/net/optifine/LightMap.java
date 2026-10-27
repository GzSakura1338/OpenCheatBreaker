package net.optifine;

import net.minecraft.world.World;

public class LightMap {
   public CustomColormap lightMapRgb = null;
   public float[][] sunRgbs = new float[16][3];
   public float[][] torchRgbs = new float[16][3];

   public LightMap(CustomColormap customColormap) {
      this.lightMapRgb = customColormap;
   }

   public CustomColormap getColormap() {
      return this.lightMapRgb;
   }

   public boolean updateLightmap(World world, float f, int[] ints, boolean flag) {
      if (this.lightMapRgb == null) {
         return false;
      }

      int height = this.lightMapRgb.getHeight();
      if (flag && height < 64) {
         return false;
      }

      int width = this.lightMapRgb.getWidth();
      if (width < 16) {
         warn("Invalid lightmap width: " + width);
         this.lightMapRgb = null;
         return false;
      }

      int n = 0;
      if (flag) {
         n = width * 16 * 2;
      }

      float f2 = 1.1666666F * (world.getSunBrightness(1.0F) - 0.2F);
      if (world.getLastLightningBolt() > 0) {
         f2 = 1.0F;
      }

      f2 = Config.limitTo1(f2);
      float f3 = f2 * (width - 1);
      float f4 = Config.limitTo1(f + 0.5F) * (width - 1);
      float limitTo1 = Config.limitTo1(Config.getGameSettings().gammaSetting);
      boolean flag2 = limitTo1 > 1.0E-4F;
      float[][] colorsRgb = this.lightMapRgb.getColorsRgb();
      this.getLightMapColumn(colorsRgb, f3, n, width, this.sunRgbs);
      this.getLightMapColumn(colorsRgb, f4, n + 16 * width, width, this.torchRgbs);
      float[] floats = new float[3];

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 3; k++) {
               float limitTo12 = Config.limitTo1(this.sunRgbs[i][k] + this.torchRgbs[j][k]);
               if (flag2) {
                  float f5 = 1.0F - limitTo12;
                  f5 = 1.0F - f5 * f5 * f5 * f5;
                  limitTo12 = limitTo1 * f5 + (1.0F - limitTo1) * limitTo12;
               }

               floats[k] = limitTo12;
            }

            int n2 = (int)(floats[0] * 255.0F);
            int n3 = (int)(floats[1] * 255.0F);
            int n4 = (int)(floats[2] * 255.0F);
            ints[i * 16 + j] = 0xFF000000 | n2 << 16 | n3 << 8 | n4;
         }
      }

      return true;
   }

   public void getLightMapColumn(float[][] floatses, float f, int n, int n2, float[][] floatses2) {
      int floor = (int)Math.floor(f);
      int ceil = (int)Math.ceil(f);
      if (floor == ceil) {
         for (int i = 0; i < 16; i++) {
            float[] floats = floatses[n + i * n2 + floor];
            float[] floats2 = floatses2[i];

            for (int j = 0; j < 3; j++) {
               floats2[j] = floats[j];
            }
         }
      } else {
         float f2 = 1.0F - (f - floor);
         float f3 = 1.0F - (ceil - f);

         for (int i = 0; i < 16; i++) {
            float[] floats = floatses[n + i * n2 + floor];
            float[] floats2 = floatses[n + i * n2 + ceil];
            float[] floats3 = floatses2[i];

            for (int j = 0; j < 3; j++) {
               floats3[j] = floats[j] * f2 + floats2[j] * f3;
            }
         }
      }
   }

   public static void dbg(String str) {
      Config.dbg("CustomColors: " + str);
   }

   public static void warn(String str) {
      Config.warn("CustomColors: " + str);
   }
}
