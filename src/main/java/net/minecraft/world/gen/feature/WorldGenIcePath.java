package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenIcePath extends WorldGenerator {
    public Block iceBlock = Blocks.packed_ice;
    public int width;

    public WorldGenIcePath(int n) {
        this.width = n;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        while (world.isAirBlock(n, n2, n3) && n2 > 2) {
            n2--;
        }

        if (world.getBlock(n, n2, n3) != Blocks.snow) {
            return false;
        }

        int n4 = random.nextInt(this.width - 2) + 2;
        byte b = 1;

        for (int i = n - n4; i <= n + n4; i++) {
            for (int j = n3 - n4; j <= n3 + n4; j++) {
                int n5 = i - n;
                int n6 = j - n3;
                if (n5 * n5 + n6 * n6 <= n4 * n4) {
                    for (int k = n2 - b; k <= n2 + b; k++) {
                        Block block = world.getBlock(i, k, j);
                        if (block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice) {
                            world.setBlock(i, k, j, this.iceBlock, 0, 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}
