package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorImproved extends NoiseGenerator {
   public int[] permutations = new int[512];
   public double xCoord;
   public double yCoord;
   public double zCoord;
   public static double[] doubleArray = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   public static double[] doubleArray2 = new double[]{1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0};
   public static double[] doubleArray3 = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
   public static double[] doubleArray4 = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   public static double[] doubleArray5 = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};

   public NoiseGeneratorImproved() {
      this(new Random());
   }

   public NoiseGeneratorImproved(Random random) {
      this.xCoord = random.nextDouble() * 256.0;
      this.yCoord = random.nextDouble() * 256.0;
      this.zCoord = random.nextDouble() * 256.0;
      int n = 0;

      while (n < 256) {
         this.permutations[n] = n++;
      }

      for (int i = 0; i < 256; i++) {
         int n2 = random.nextInt(256 - i) + i;
         int n3 = this.permutations[i];
         this.permutations[i] = this.permutations[n2];
         this.permutations[n2] = n3;
         this.permutations[i + 256] = this.permutations[i];
      }
   }

   public double lerp(double d, double d2, double d3) {
      return d2 + d * (d3 - d2);
   }

   public double computeDouble(int n, double d, double d2) {
      int n2 = n & 15;
      return doubleArray4[n2] * d + doubleArray5[n2] * d2;
   }

   public double grad(int n, double d, double d2, double d3) {
      int n2 = n & 15;
      return doubleArray[n2] * d + doubleArray2[n2] * d2 + doubleArray3[n2] * d3;
   }

   public void populateNoiseArray(
      double[] noiseArray,
      double xOffset,
      double yOffset,
      double zOffset,
      int xSize,
      int ySize,
      int zSize,
      double xScale,
      double yScale,
      double zScale,
      double noiseScale
   ) {
      if (ySize == 1) {
         int n = 0;
         int n2 = 0;
         int n3 = 0;
         int n4 = 0;
         double d = 0.0;
         double d2 = 0.0;
         int n5 = 0;
         double d3 = 1.0 / noiseScale;

         for (int i = 0; i < xSize; i++) {
            double d4 = xOffset + i * xScale + this.xCoord;
            int n6 = (int)d4;
            if (d4 < n6) {
               n6--;
            }

            int n7 = n6 & 0xFF;
            d4 -= n6;
            double d5 = d4 * d4 * d4 * (d4 * (d4 * 6.0 - 15.0) + 10.0);

            for (int j = 0; j < zSize; j++) {
               double d6 = zOffset + j * zScale + this.zCoord;
               int n8 = (int)d6;
               if (d6 < n8) {
                  n8--;
               }

               int n9 = n8 & 0xFF;
               d6 -= n8;
               double d7 = d6 * d6 * d6 * (d6 * (d6 * 6.0 - 15.0) + 10.0);
               n = this.permutations[n7] + 0;
               n2 = this.permutations[n] + n9;
               n3 = this.permutations[n7 + 1] + 0;
               n4 = this.permutations[n3] + n9;
               d = this.lerp(
                  d5, this.computeDouble(this.permutations[n2], d4, d6), this.grad(this.permutations[n4], d4 - 1.0, 0.0, d6)
               );
               d2 = this.lerp(
                  d5,
                  this.grad(this.permutations[n2 + 1], d4, 0.0, d6 - 1.0),
                  this.grad(this.permutations[n4 + 1], d4 - 1.0, 0.0, d6 - 1.0)
               );
               double lerp = this.lerp(d7, d, d2);
               int n10 = n5++;
               noiseArray[n10] += lerp * d3;
            }
         }
      } else {
         int n = 0;
         double d = 1.0 / noiseScale;
         int n2 = -1;
         int n3 = 0;
         int n4 = 0;
         int n5 = 0;
         int n6 = 0;
         int n7 = 0;
         int n8 = 0;
         double d2 = 0.0;
         double d3 = 0.0;
         double d4 = 0.0;
         double d5 = 0.0;

         for (int i = 0; i < xSize; i++) {
            double d6 = xOffset + i * xScale + this.xCoord;
            int n9 = (int)d6;
            if (d6 < n9) {
               n9--;
            }

            int n10 = n9 & 0xFF;
            d6 -= n9;
            double d7 = d6 * d6 * d6 * (d6 * (d6 * 6.0 - 15.0) + 10.0);

            for (int j = 0; j < zSize; j++) {
               double d8 = zOffset + j * zScale + this.zCoord;
               int n11 = (int)d8;
               if (d8 < n11) {
                  n11--;
               }

               int n12 = n11 & 0xFF;
               d8 -= n11;
               double d9 = d8 * d8 * d8 * (d8 * (d8 * 6.0 - 15.0) + 10.0);

               for (int k = 0; k < ySize; k++) {
                  double d10 = yOffset + k * yScale + this.yCoord;
                  int n13 = (int)d10;
                  if (d10 < n13) {
                     n13--;
                  }

                  int n14 = n13 & 0xFF;
                  d10 -= n13;
                  double d11 = d10 * d10 * d10 * (d10 * (d10 * 6.0 - 15.0) + 10.0);
                  if (k == 0 || n14 != n2) {
                     n2 = n14;
                     n3 = this.permutations[n10] + n14;
                     n4 = this.permutations[n3] + n12;
                     n5 = this.permutations[n3 + 1] + n12;
                     n6 = this.permutations[n10 + 1] + n14;
                     n7 = this.permutations[n6] + n12;
                     n8 = this.permutations[n6 + 1] + n12;
                     d2 = this.lerp(
                        d7, this.grad(this.permutations[n4], d6, d10, d8), this.grad(this.permutations[n7], d6 - 1.0, d10, d8)
                     );
                     d3 = this.lerp(
                        d7,
                        this.grad(this.permutations[n5], d6, d10 - 1.0, d8),
                        this.grad(this.permutations[n8], d6 - 1.0, d10 - 1.0, d8)
                     );
                     d4 = this.lerp(
                        d7,
                        this.grad(this.permutations[n4 + 1], d6, d10, d8 - 1.0),
                        this.grad(this.permutations[n7 + 1], d6 - 1.0, d10, d8 - 1.0)
                     );
                     d5 = this.lerp(
                        d7,
                        this.grad(this.permutations[n5 + 1], d6, d10 - 1.0, d8 - 1.0),
                        this.grad(this.permutations[n8 + 1], d6 - 1.0, d10 - 1.0, d8 - 1.0)
                     );
                  }

                  double lerp = this.lerp(d11, d2, d3);
                  double lerp2 = this.lerp(d11, d4, d5);
                  double lerp3 = this.lerp(d9, lerp, lerp2);
                  int n15 = n++;
                  noiseArray[n15] += lerp3 * d;
               }
            }
         }
      }
   }
}
