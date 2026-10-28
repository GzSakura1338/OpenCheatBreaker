package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator {
    public int intValue;

    public void setIntValue(int n) {
        this.intValue = n;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        boolean flag = false;

        for (int i = 0; i < 64; i++) {
            int n4 = n + random.nextInt(8) - random.nextInt(8);
            int n5 = n2 + random.nextInt(4) - random.nextInt(4);
            int n6 = n3 + random.nextInt(8) - random.nextInt(8);
            if (world.isAirBlock(n4, n5, n6) && (!world.provider.hasNoSky || n5 < 254) && Blocks.double_plant.canPlaceBlockAt(world, n4, n5, n6)) {
                Blocks.double_plant.handleWorld(world, n4, n5, n6, this.intValue, 2);
                flag = true;
            }
        }

        return flag;
    }
}
