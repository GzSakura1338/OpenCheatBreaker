package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenDesertWells extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        while (world.isAirBlock(n, n2, n3) && n2 > 2) {
            n2--;
        }

        if (world.getBlock(n, n2, n3) != Blocks.sand) {
            return false;
        }

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (world.isAirBlock(n + i, n2 - 1, n3 + j) && world.isAirBlock(n + i, n2 - 2, n3 + j)) {
                    return false;
                }
            }
        }

        for (int i = -1; i <= 0; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -2; k <= 2; k++) {
                    world.setBlock(n + j, n2 + i, n3 + k, Blocks.sandstone, 0, 2);
                }
            }
        }

        world.setBlock(n, n2, n3, Blocks.flowing_water, 0, 2);
        world.setBlock(n - 1, n2, n3, Blocks.flowing_water, 0, 2);
        world.setBlock(n + 1, n2, n3, Blocks.flowing_water, 0, 2);
        world.setBlock(n, n2, n3 - 1, Blocks.flowing_water, 0, 2);
        world.setBlock(n, n2, n3 + 1, Blocks.flowing_water, 0, 2);

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i == -2 || i == 2 || j == -2 || j == 2) {
                    world.setBlock(n + i, n2 + 1, n3 + j, Blocks.sandstone, 0, 2);
                }
            }
        }

        world.setBlock(n + 2, n2 + 1, n3, Blocks.stone_slab, 1, 2);
        world.setBlock(n - 2, n2 + 1, n3, Blocks.stone_slab, 1, 2);
        world.setBlock(n, n2 + 1, n3 + 2, Blocks.stone_slab, 1, 2);
        world.setBlock(n, n2 + 1, n3 - 2, Blocks.stone_slab, 1, 2);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    world.setBlock(n + i, n2 + 4, n3 + j, Blocks.sandstone, 0, 2);
                } else {
                    world.setBlock(n + i, n2 + 4, n3 + j, Blocks.stone_slab, 1, 2);
                }
            }
        }

        for (int i = 1; i <= 3; i++) {
            world.setBlock(n - 1, n2 + i, n3 - 1, Blocks.sandstone, 0, 2);
            world.setBlock(n - 1, n2 + i, n3 + 1, Blocks.sandstone, 0, 2);
            world.setBlock(n + 1, n2 + i, n3 - 1, Blocks.sandstone, 0, 2);
            world.setBlock(n + 1, n2 + i, n3 + 1, Blocks.sandstone, 0, 2);
        }

        return true;
    }
}
