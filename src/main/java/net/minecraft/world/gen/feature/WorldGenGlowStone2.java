package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenGlowStone2 extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        if (!world.isAirBlock(n, n2, n3)) {
            return false;
        }

        if (world.getBlock(n, n2 + 1, n3) != Blocks.netherrack) {
            return false;
        }

        world.setBlock(n, n2, n3, Blocks.glowstone, 0, 2);

        for (int i = 0; i < 1500; i++) {
            int n4 = n + random.nextInt(8) - random.nextInt(8);
            int n5 = n2 - random.nextInt(12);
            int n6 = n3 + random.nextInt(8) - random.nextInt(8);
            if (world.getBlock(n4, n5, n6).getMaterial() == Material.air) {
                int n7 = 0;

                for (int j = 0; j < 6; j++) {
                    Block block = null;
                    if (j == 0) {
                        block = world.getBlock(n4 - 1, n5, n6);
                    }

                    if (j == 1) {
                        block = world.getBlock(n4 + 1, n5, n6);
                    }

                    if (j == 2) {
                        block = world.getBlock(n4, n5 - 1, n6);
                    }

                    if (j == 3) {
                        block = world.getBlock(n4, n5 + 1, n6);
                    }

                    if (j == 4) {
                        block = world.getBlock(n4, n5, n6 - 1);
                    }

                    if (j == 5) {
                        block = world.getBlock(n4, n5, n6 + 1);
                    }

                    if (block == Blocks.glowstone) {
                        n7++;
                    }
                }

                if (n7 == 1) {
                    world.setBlock(n4, n5, n6, Blocks.glowstone, 0, 2);
                }
            }
        }

        return true;
    }
}
