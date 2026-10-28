package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenMelon extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        for (int i = 0; i < 64; i++) {
            int n4 = n + random.nextInt(8) - random.nextInt(8);
            int n5 = n2 + random.nextInt(4) - random.nextInt(4);
            int n6 = n3 + random.nextInt(8) - random.nextInt(8);
            if (Blocks.melon_block.canPlaceBlockAt(world, n4, n5, n6) && world.getBlock(n4, n5 - 1, n6) == Blocks.grass) {
                world.setBlock(n4, n5, n6, Blocks.melon_block, 0, 2);
            }
        }

        return true;
    }
}
