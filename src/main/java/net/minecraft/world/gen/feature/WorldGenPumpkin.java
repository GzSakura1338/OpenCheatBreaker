package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenPumpkin extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        for (int i = 0; i < 64; i++) {
            int n4 = n + random.nextInt(8) - random.nextInt(8);
            int n5 = n2 + random.nextInt(4) - random.nextInt(4);
            int n6 = n3 + random.nextInt(8) - random.nextInt(8);
            if (world.isAirBlock(n4, n5, n6) && world.getBlock(n4, n5 - 1, n6) == Blocks.grass && Blocks.pumpkin.canPlaceBlockAt(world, n4, n5, n6)
                )
             {
                world.setBlock(n4, n5, n6, Blocks.pumpkin, random.nextInt(4), 2);
            }
        }

        return true;
    }
}
