package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenCactus extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        for (int i = 0; i < 10; i++) {
            int n4 = n + random.nextInt(8) - random.nextInt(8);
            int n5 = n2 + random.nextInt(4) - random.nextInt(4);
            int n6 = n3 + random.nextInt(8) - random.nextInt(8);
            if (world.isAirBlock(n4, n5, n6)) {
                int n7 = 1 + random.nextInt(random.nextInt(3) + 1);

                for (int j = 0; j < n7; j++) {
                    if (Blocks.cactus.canBlockStay(world, n4, n5 + j, n6)) {
                        world.setBlock(n4, n5 + j, n6, Blocks.cactus, 0, 2);
                    }
                }
            }
        }

        return true;
    }
}
