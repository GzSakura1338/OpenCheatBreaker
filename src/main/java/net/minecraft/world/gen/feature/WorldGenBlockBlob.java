package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenBlockBlob extends WorldGenerator {
    public Block block;
    public int radius;

    public WorldGenBlockBlob(Block block, int n) {
        super(false);
        this.block = block;
        this.radius = n;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        while (n2 > 3) {
            if (!world.isAirBlock(n, n2 - 1, n3)) {
                Block block = world.getBlock(n, n2 - 1, n3);
                if (block == Blocks.grass || block == Blocks.dirt || block == Blocks.stone) {
                    break;
                }
            }

            n2--;
        }

        if (n2 <= 3) {
            return false;
        }

        int radius = this.radius;

        for (int i = 0; radius >= 0 && i < 3; i++) {
            int n4 = radius + random.nextInt(2);
            int n5 = radius + random.nextInt(2);
            int n6 = radius + random.nextInt(2);
            float f = (n4 + n5 + n6) * 0.333F + 0.5F;

            for (int j = n - n4; j <= n + n4; j++) {
                for (int k = n3 - n6; k <= n3 + n6; k++) {
                    for (int m = n2 - n5; m <= n2 + n5; m++) {
                        float f2 = j - n;
                        float f3 = k - n3;
                        float f4 = m - n2;
                        if (f2 * f2 + f3 * f3 + f4 * f4 <= f * f) {
                            world.setBlock(j, m, k, this.block, 0, 4);
                        }
                    }
                }
            }

            n += -(radius + 1) + random.nextInt(2 + radius * 2);
            n3 += -(radius + 1) + random.nextInt(2 + radius * 2);
            n2 += 0 - random.nextInt(2);
        }

        return true;
    }
}
