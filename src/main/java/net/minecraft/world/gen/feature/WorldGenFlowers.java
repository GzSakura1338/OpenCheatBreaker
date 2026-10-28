package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenFlowers extends WorldGenerator {
    public Block block;
    public int intValue;

    public WorldGenFlowers(Block block) {
        this.block = block;
    }

    public void handleBlock(Block block, int n) {
        this.block = block;
        this.intValue = n;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        for (int i = 0; i < 64; i++) {
            int n4 = n + random.nextInt(8) - random.nextInt(8);
            int n5 = n2 + random.nextInt(4) - random.nextInt(4);
            int n6 = n3 + random.nextInt(8) - random.nextInt(8);
            if (world.isAirBlock(n4, n5, n6) && (!world.provider.hasNoSky || n5 < 255) && this.block.canBlockStay(world, n4, n5, n6)) {
                world.setBlock(n4, n5, n6, this.block, this.intValue, 2);
            }
        }

        return true;
    }
}
