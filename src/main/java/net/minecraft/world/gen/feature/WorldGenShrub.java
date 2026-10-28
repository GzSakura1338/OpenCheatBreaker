package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenShrub extends WorldGenTrees {
    public int intValue;
    public int intValue2;

    public WorldGenShrub(int n, int n2) {
        super(false);
        this.intValue = n;
        this.intValue2 = n2;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        Block block;
        while (((block = world.getBlock(n, n2, n3)).getMaterial() == Material.air || block.getMaterial() == Material.leaves) && n2 > 0) {
            n2--;
        }

        Block block2 = world.getBlock(n, n2, n3);
        if (block2 == Blocks.dirt || block2 == Blocks.grass) {
            this.setBlockAndNotifyAdequately(world, n, ++n2, n3, Blocks.log, this.intValue);

            for (int i = n2; i <= n2 + 2; i++) {
                int n4 = i - n2;
                int n5 = 2 - n4;

                for (int j = n - n5; j <= n + n5; j++) {
                    int n6 = j - n;

                    for (int k = n3 - n5; k <= n3 + n5; k++) {
                        int n7 = k - n3;
                        if ((Math.abs(n6) != n5 || Math.abs(n7) != n5 || random.nextInt(2) != 0) && !world.getBlock(j, i, k).isFullBlock()) {
                            this.setBlockAndNotifyAdequately(world, j, i, k, Blocks.leaves, this.intValue2);
                        }
                    }
                }
            }
        }

        return true;
    }
}
