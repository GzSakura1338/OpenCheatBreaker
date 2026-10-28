package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMinable extends WorldGenerator {
    public Block block;
    public int numberOfBlocks;
    public Block block2;

    public WorldGenMinable(Block block, int n) {
        this(block, n, Blocks.stone);
    }

    public WorldGenMinable(Block block, int n, Block block2) {
        this.block = block;
        this.numberOfBlocks = n;
        this.block2 = block2;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        float f = random.nextFloat() * (float) Math.PI;
        double d = n + 8 + MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
        double d2 = n + 8 - MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
        double d3 = n3 + 8 + MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
        double d4 = n3 + 8 - MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
        double d5 = n2 + random.nextInt(3) - 2;
        double d6 = n2 + random.nextInt(3) - 2;

        for (int i = 0; i <= this.numberOfBlocks; i++) {
            double d7 = d + (d2 - d) * i / this.numberOfBlocks;
            double d8 = d5 + (d6 - d5) * i / this.numberOfBlocks;
            double d9 = d3 + (d4 - d3) * i / this.numberOfBlocks;
            double d10 = random.nextDouble() * this.numberOfBlocks / 16.0;
            double d11 = (MathHelper.sin(i * (float) Math.PI / this.numberOfBlocks) + 1.0F) * d10 + 1.0;
            double d12 = (MathHelper.sin(i * (float) Math.PI / this.numberOfBlocks) + 1.0F) * d10 + 1.0;
            int n4 = MathHelper.floor_double(d7 - d11 / 2.0);
            int n5 = MathHelper.floor_double(d8 - d12 / 2.0);
            int n6 = MathHelper.floor_double(d9 - d11 / 2.0);
            int n7 = MathHelper.floor_double(d7 + d11 / 2.0);
            int n8 = MathHelper.floor_double(d8 + d12 / 2.0);
            int n9 = MathHelper.floor_double(d9 + d11 / 2.0);

            for (int j = n4; j <= n7; j++) {
                double d13 = (j + 0.5 - d7) / (d11 / 2.0);
                if (d13 * d13 < 1.0) {
                    for (int k = n5; k <= n8; k++) {
                        double d14 = (k + 0.5 - d8) / (d12 / 2.0);
                        if (d13 * d13 + d14 * d14 < 1.0) {
                            for (int m = n6; m <= n9; m++) {
                                double d15 = (m + 0.5 - d9) / (d11 / 2.0);
                                if (d13 * d13 + d14 * d14 + d15 * d15 < 1.0 && world.getBlock(j, k, m) == this.block2) {
                                    world.setBlock(j, k, m, this.block, 0, 2);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
