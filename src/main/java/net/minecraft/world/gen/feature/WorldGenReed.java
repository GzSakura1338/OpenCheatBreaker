package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenReed extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        for (int i = 0; i < 20; i++) {
            int n4 = n + random.nextInt(4) - random.nextInt(4);
            int n5 = n2;
            int n6 = n3 + random.nextInt(4) - random.nextInt(4);
            if (world.isAirBlock(n4, n2, n6)
                && (
                    world.getBlock(n4 - 1, n2 - 1, n6).getMaterial() == Material.water
                        || world.getBlock(n4 + 1, n2 - 1, n6).getMaterial() == Material.water
                        || world.getBlock(n4, n2 - 1, n6 - 1).getMaterial() == Material.water
                        || world.getBlock(n4, n2 - 1, n6 + 1).getMaterial() == Material.water
                )) {
                int n7 = 2 + random.nextInt(random.nextInt(3) + 1);

                for (int j = 0; j < n7; j++) {
                    if (Blocks.reeds.canBlockStay(world, n4, n5 + j, n6)) {
                        world.setBlock(n4, n5 + j, n6, Blocks.reeds, 0, 2);
                    }
                }
            }
        }

        return true;
    }
}
