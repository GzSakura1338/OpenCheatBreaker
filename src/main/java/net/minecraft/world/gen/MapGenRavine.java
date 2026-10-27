package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenRavine extends MapGenBase {
   public float[] heightToHorizontalStretchFactor = new float[1024];

   public void carveRavine(
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
      Random random = new Random(l);
      double d5 = n * 16 + 8;
      double d6 = n2 * 16 + 8;
      float f4 = 0.0F;
      float f5 = 0.0F;
      if (n4 <= 0) {
         int n5 = this.range * 16 - 16;
         n4 = n5 - random.nextInt(n5 / 4);
      }

      boolean flag = false;
      if (n3 == -1) {
         n3 = n4 / 2;
         flag = true;
      }

      float f6 = 1.0F;

      for (int i = 0; i < 256; i++) {
         if (i == 0 || random.nextInt(3) == 0) {
            f6 = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
         }

         this.heightToHorizontalStretchFactor[i] = f6 * f6;
      }

      for (; n3 < n4; n3++) {
         double d7 = 1.5 + MathHelper.sin(n3 * (float) Math.PI / n4) * f * 1.0F;
         double d8 = d7 * d4;
         d7 *= random.nextFloat() * 0.25 + 0.75;
         d8 *= random.nextFloat() * 0.25 + 0.75;
         float cos = MathHelper.cos(f3);
         float sin = MathHelper.sin(f3);
         d += MathHelper.cos(f2) * cos;
         d2 += sin;
         d3 += MathHelper.sin(f2) * cos;
         f3 *= 0.7F;
         f3 += f5 * 0.05F;
         f2 += f4 * 0.05F;
         f5 *= 0.8F;
         f4 *= 0.5F;
         f5 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
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
               int n5 = MathHelper.floor_double(d - d7) - n * 16 - 1;
               int n6 = MathHelper.floor_double(d + d7) - n * 16 + 1;
               int n7 = MathHelper.floor_double(d2 - d8) - 1;
               int n8 = MathHelper.floor_double(d2 + d8) + 1;
               int n9 = MathHelper.floor_double(d3 - d7) - n2 * 16 - 1;
               int n10 = MathHelper.floor_double(d3 + d7) - n2 * 16 + 1;
               if (n5 < 0) {
                  n5 = 0;
               }

               if (n6 > 16) {
                  n6 = 16;
               }

               if (n7 < 1) {
                  n7 = 1;
               }

               if (n8 > 248) {
                  n8 = 248;
               }

               if (n9 < 0) {
                  n9 = 0;
               }

               if (n10 > 16) {
                  n10 = 16;
               }

               boolean flag2 = false;

               for (int i = n5; !flag2 && i < n6; i++) {
                  for (int j = n9; !flag2 && j < n10; j++) {
                     for (int k = n8 + 1; !flag2 && k >= n7 - 1; k--) {
                        if (k >= 0 && k < 256) {
                           IBlockState blockState = chunkPrimer.getBlockState(i, k, j);
                           if (blockState.getBlock() == Blocks.flowing_water || blockState.getBlock() == Blocks.water) {
                              flag2 = true;
                           }

                           if (k != n7 - 1 && i != n5 && i != n6 - 1 && j != n9 && j != n10 - 1) {
                              k = n7;
                           }
                        }
                     }
                  }
               }

               if (!flag2) {
                  MutableBlockPos mutableBlockPos = new MutableBlockPos();

                  for (int i = n5; i < n6; i++) {
                     double d13 = (i + n * 16 + 0.5 - d) / d7;

                     for (int j = n9; j < n10; j++) {
                        double d14 = (j + n2 * 16 + 0.5 - d3) / d7;
                        boolean flag3 = false;
                        if (d13 * d13 + d14 * d14 < 1.0) {
                           for (int k = n8; k > n7; k--) {
                              double d15 = (k - 1 + 0.5 - d2) / d8;
                              if ((d13 * d13 + d14 * d14) * this.heightToHorizontalStretchFactor[k - 1] + d15 * d15 / 6.0 < 1.0) {
                                 IBlockState blockState = chunkPrimer.getBlockState(i, k, j);
                                 if (blockState.getBlock() == Blocks.grass) {
                                    flag3 = true;
                                 }

                                 if (blockState.getBlock() == Blocks.stone || blockState.getBlock() == Blocks.dirt || blockState.getBlock() == Blocks.grass) {
                                    if (k - 1 < 10) {
                                       chunkPrimer.setBlockState(i, k, j, Blocks.flowing_lava.getDefaultState());
                                    } else {
                                       chunkPrimer.setBlockState(i, k, j, Blocks.air.getDefaultState());
                                       if (flag3 && chunkPrimer.getBlockState(i, k - 1, j).getBlock() == Blocks.dirt) {
                                          mutableBlockPos.set(i + n * 16, 0, j + n2 * 16);
                                          chunkPrimer.setBlockState(i, k - 1, j, this.worldObj.getBiomeGenForCoords(mutableBlockPos).topBlock);
                                       }
                                    }
                                 }
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
      }
   }

   @Override
   public void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int n, int n2, ChunkPrimer chunkPrimerIn) {
      if (this.rand.nextInt(50) == 0) {
         double d = chunkX * 16 + this.rand.nextInt(16);
         double d2 = this.rand.nextInt(this.rand.nextInt(40) + 8) + 20;
         double d3 = chunkZ * 16 + this.rand.nextInt(16);
         byte b = 1;

         for (int i = 0; i < b; i++) {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float f3 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
            this.carveRavine(this.rand.nextLong(), n, n2, chunkPrimerIn, d, d2, d3, f3, f, f2, 0, 0, 3.0);
         }
      }
   }
}
