package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenCavesHell extends MapGenBase {
   public void carveCave(long l, int n, int n2, ChunkPrimer chunkPrimer, double d, double d2, double d3) {
      this.carveCave(l, n, n2, chunkPrimer, d, d2, d3, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
   }

   public void carveCave(
      long l,
      int n,
      int n2,
      ChunkPrimer chunkPrimer,
      double d,
      double d2,
      double d3,
      float f,
      float f2,
      float f3,
      int n3,
      int n4,
      double d4
   ) {
      double d5 = n * 16 + 8;
      double d6 = n2 * 16 + 8;
      float f4 = 0.0F;
      float f5 = 0.0F;
      Random random = new Random(l);
      if (n4 <= 0) {
         int n5 = this.range * 16 - 16;
         n4 = n5 - random.nextInt(n5 / 4);
      }

      boolean flag = false;
      if (n3 == -1) {
         n3 = n4 / 2;
         flag = true;
      }

      int n5 = random.nextInt(n4 / 2) + n4 / 4;
      boolean flag2 = random.nextInt(6) == 0;

      while (n3 < n4) {
         double d7 = 1.5 + MathHelper.sin(n3 * (float) Math.PI / n4) * f * 1.0F;
         double d8 = d7 * d4;
         float cos = MathHelper.cos(f3);
         float sin = MathHelper.sin(f3);
         d += MathHelper.cos(f2) * cos;
         d2 += sin;
         d3 += MathHelper.sin(f2) * cos;
         if (flag2) {
            f3 *= 0.92F;
         } else {
            f3 *= 0.7F;
         }

         f3 += f5 * 0.1F;
         f2 += f4 * 0.1F;
         f5 *= 0.9F;
         f4 *= 0.75F;
         f5 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (!flag && n3 == n5 && f > 1.0F) {
            this.carveCave(
               random.nextLong(),
               n,
               n2,
               chunkPrimer,
               d,
               d2,
               d3,
               random.nextFloat() * 0.5F + 0.5F,
               f2 - (float) (Math.PI / 2),
               f3 / 3.0F,
               n3,
               n4,
               1.0
            );
            this.carveCave(
               random.nextLong(),
               n,
               n2,
               chunkPrimer,
               d,
               d2,
               d3,
               random.nextFloat() * 0.5F + 0.5F,
               f2 + (float) (Math.PI / 2),
               f3 / 3.0F,
               n3,
               n4,
               1.0
            );
            return;
         }

         if (flag || random.nextInt(4) != 0) {
            double d9 = d - d5;
            double d10 = d3 - d6;
            double d11 = n4 - n3;
            double d12 = f + 2.0F + 16.0F;
            if (d9 * d9 + d10 * d10 - d11 * d11 > d12 * d12) {
               return;
            }

            if (d >= d5 - 16.0 - d7 * 2.0
               && d3 >= d6 - 16.0 - d7 * 2.0
               && d <= d5 + 16.0 + d7 * 2.0
               && d3 <= d6 + 16.0 + d7 * 2.0) {
               int n6 = MathHelper.floor_double(d - d7) - n * 16 - 1;
               int n7 = MathHelper.floor_double(d + d7) - n * 16 + 1;
               int n8 = MathHelper.floor_double(d2 - d8) - 1;
               int n9 = MathHelper.floor_double(d2 + d8) + 1;
               int n10 = MathHelper.floor_double(d3 - d7) - n2 * 16 - 1;
               int n11 = MathHelper.floor_double(d3 + d7) - n2 * 16 + 1;
               if (n6 < 0) {
                  n6 = 0;
               }

               if (n7 > 16) {
                  n7 = 16;
               }

               if (n8 < 1) {
                  n8 = 1;
               }

               if (n9 > 120) {
                  n9 = 120;
               }

               if (n10 < 0) {
                  n10 = 0;
               }

               if (n11 > 16) {
                  n11 = 16;
               }

               boolean flag3 = false;

               for (int i = n6; !flag3 && i < n7; i++) {
                  for (int j = n10; !flag3 && j < n11; j++) {
                     for (int k = n9 + 1; !flag3 && k >= n8 - 1; k--) {
                        if (k >= 0 && k < 128) {
                           IBlockState blockState = chunkPrimer.getBlockState(i, k, j);
                           if (blockState.getBlock() == Blocks.flowing_lava || blockState.getBlock() == Blocks.lava) {
                              flag3 = true;
                           }

                           if (k != n8 - 1 && i != n6 && i != n7 - 1 && j != n10 && j != n11 - 1) {
                              k = n8;
                           }
                        }
                     }
                  }
               }

               if (!flag3) {
                  for (int i = n6; i < n7; i++) {
                     double d13 = (i + n * 16 + 0.5 - d) / d7;

                     for (int j = n10; j < n11; j++) {
                        double d14 = (j + n2 * 16 + 0.5 - d3) / d7;

                        for (int k = n9; k > n8; k--) {
                           double d15 = (k - 1 + 0.5 - d2) / d8;
                           if (d15 > -0.7 && d13 * d13 + d15 * d15 + d14 * d14 < 1.0) {
                              IBlockState blockState = chunkPrimer.getBlockState(i, k, j);
                              if (blockState.getBlock() == Blocks.netherrack || blockState.getBlock() == Blocks.dirt || blockState.getBlock() == Blocks.grass) {
                                 chunkPrimer.setBlockState(i, k, j, Blocks.air.getDefaultState());
                              }
                           }
                        }
                     }
                  }

                  if (flag) {
                     break;
                  }
               }
            }
         }

         n3++;
      }
   }

   @Override
   public void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int n, int n2, ChunkPrimer chunkPrimerIn) {
      int nextInt = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(10) + 1) + 1);
      if (this.rand.nextInt(5) != 0) {
         nextInt = 0;
      }

      for (int i = 0; i < nextInt; i++) {
         double d = chunkX * 16 + this.rand.nextInt(16);
         double nextInt2 = this.rand.nextInt(128);
         double d2 = chunkZ * 16 + this.rand.nextInt(16);
         int n3 = 1;
         if (this.rand.nextInt(4) == 0) {
            this.carveCave(this.rand.nextLong(), n, n2, chunkPrimerIn, d, nextInt2, d2);
            n3 += this.rand.nextInt(4);
         }

         for (int j = 0; j < n3; j++) {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float f3 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
            this.carveCave(this.rand.nextLong(), n, n2, chunkPrimerIn, d, nextInt2, d2, f3 * 2.0F, f, f2, 0, 0, 0.5);
         }
      }
   }
}
