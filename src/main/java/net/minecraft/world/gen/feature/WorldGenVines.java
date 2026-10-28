package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class WorldGenVines extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        int n4 = n;
        int n5 = n3;

        while (n2 < 128) {
            if (world.isAirBlock(n, n2, n3)) {
                for (int i = 2; i <= 5; i++) {
                    if (Blocks.vine.canPlaceBlockOnSide(world, n, n2, n3, i)) {
                        world.setBlock(n, n2, n3, Blocks.vine, 1 << Direction.facingToDirection[Facing.oppositeSide[i]], 2);
                        break;
                    }
                }
            } else {
                n = n4 + random.nextInt(4) - random.nextInt(4);
                n3 = n5 + random.nextInt(4) - random.nextInt(4);
            }

            n2++;
        }

        return true;
    }
}
