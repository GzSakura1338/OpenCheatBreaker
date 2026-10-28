package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MapGenRavine extends MapGenBase {
    public float[] heightToHorizontalStretchFactor = new float[1024];

    public void process(
        long l,
        int n,
        int n2,
        Block[] blocks,
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
                                int n11 = (i * 16 + j) * 256 + k;
                                if (k >= 0 && k < 256) {
                                    Block block = blocks[n11];
                                    if (block == Blocks.flowing_water || block == Blocks.water) {
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
                        for (int i = n5; i < n6; i++) {
                            double d13 = (i + n * 16 + 0.5 - d) / d7;

                            for (int j = n9; j < n10; j++) {
                                double d14 = (j + n2 * 16 + 0.5 - d3) / d7;
                                int n11 = (i * 16 + j) * 256 + n8;
                                boolean flag3 = false;
                                if (d13 * d13 + d14 * d14 < 1.0) {
                                    for (int k = n8 - 1; k >= n7; k--) {
                                        double d15 = (k + 0.5 - d2) / d8;
                                        if ((d13 * d13 + d14 * d14) * this.heightToHorizontalStretchFactor[k] + d15 * d15 / 6.0 < 1.0) {
                                            Block block = blocks[n11];
                                            if (block == Blocks.grass) {
                                                flag3 = true;
                                            }

                                            if (block == Blocks.stone || block == Blocks.dirt || block == Blocks.grass) {
                                                if (k < 10) {
                                                    blocks[n11] = Blocks.flowing_lava;
                                                } else {
                                                    blocks[n11] = null;
                                                    if (flag3 && blocks[n11 - 1] == Blocks.dirt) {
                                                        blocks[n11 - 1] = this.worldObj.getBiomeGenForCoords(i + n * 16, j + n2 * 16).topBlock;
                                                    }
                                                }
                                            }
                                        }

                                        n11--;
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
    public void handleWorld(World worldIn, int n, int n2, int n3, int n4, Block[] blocks) {
        if (this.rand.nextInt(50) == 0) {
            double d = n * 16 + this.rand.nextInt(16);
            double d2 = this.rand.nextInt(this.rand.nextInt(40) + 8) + 20;
            double d3 = n2 * 16 + this.rand.nextInt(16);
            byte b = 1;

            for (int i = 0; i < b; i++) {
                float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float f2 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f3 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
                this.process(this.rand.nextLong(), n3, n4, blocks, d, d2, d3, f3, f, f2, 0, 0, 3.0);
            }
        }
    }
}
