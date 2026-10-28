package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        while (world.isAirBlock(n, n2, n3) && n2 > 2) {
            n2--;
        }

        if (world.getBlock(n, n2, n3) != Blocks.snow) {
            return false;
        }

        n2 += random.nextInt(4);
        int n4 = random.nextInt(4) + 7;
        int n5 = n4 / 4 + random.nextInt(2);
        if (n5 > 1 && random.nextInt(60) == 0) {
            n2 += 10 + random.nextInt(30);
        }

        for (int i = 0; i < n4; i++) {
            float f = (1.0F - (float)i / n4) * n5;
            int n6 = MathHelper.ceiling_float_int(f);

            for (int j = -n6; j <= n6; j++) {
                float f2 = MathHelper.abs_int(j) - 0.25F;

                for (int k = -n6; k <= n6; k++) {
                    float f3 = MathHelper.abs_int(k) - 0.25F;
                    if ((j == 0 && k == 0 || f2 * f2 + f3 * f3 <= f * f)
                        && (j != -n6 && j != n6 && k != -n6 && k != n6 || random.nextFloat() <= 0.75F)) {
                        Block block = world.getBlock(n + j, n2 + i, n3 + k);
                        if (block.getMaterial() == Material.air || block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice) {
                            this.setBlockAndNotifyAdequately(world, n + j, n2 + i, n3 + k, Blocks.packed_ice);
                        }

                        if (i != 0 && n6 > 1) {
                            block = world.getBlock(n + j, n2 - i, n3 + k);
                            if (block.getMaterial() == Material.air || block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice) {
                                this.setBlockAndNotifyAdequately(world, n + j, n2 - i, n3 + k, Blocks.packed_ice);
                            }
                        }
                    }
                }
            }
        }

        int n6 = n5 - 1;
        if (n6 < 0) {
            n6 = 0;
        } else if (n6 > 1) {
            n6 = 1;
        }

        for (int i = -n6; i <= n6; i++) {
            for (int j = -n6; j <= n6; j++) {
                int n7 = n2 - 1;
                int n8 = 50;
                if (Math.abs(i) == 1 && Math.abs(j) == 1) {
                    n8 = random.nextInt(5);
                }

                while (n7 > 50) {
                    Block block = world.getBlock(n + i, n7, n3 + j);
                    if (block.getMaterial() != Material.air && block != Blocks.dirt && block != Blocks.snow && block != Blocks.ice && block != Blocks.packed_ice) {
                        break;
                    }

                    this.setBlockAndNotifyAdequately(world, n + i, n7, n3 + j, Blocks.packed_ice);
                    n7--;
                    if (--n8 <= 0) {
                        n7 -= random.nextInt(5) + 1;
                        n8 = random.nextInt(5);
                    }
                }
            }
        }

        return true;
    }
}
