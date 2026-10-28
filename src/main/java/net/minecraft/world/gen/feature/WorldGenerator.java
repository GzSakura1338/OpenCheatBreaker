package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class WorldGenerator {
    public boolean doBlockNotify;

    public WorldGenerator() {
        this.doBlockNotify = false;
    }

    public WorldGenerator(boolean flag) {
        this.doBlockNotify = flag;
    }

    public abstract boolean generate(World world, Random random, int n, int n2, int n3);

    public void setScale(double d, double d2, double d3) {
    }

    public void setBlockAndNotifyAdequately(World world, int n, int n2, int n3, Block block) {
        this.setBlockAndNotifyAdequately(world, n, n2, n3, block, 0);
    }

    public void setBlockAndNotifyAdequately(World world, int n, int n2, int n3, Block block, int n4) {
        if (this.doBlockNotify) {
            world.setBlock(n, n2, n3, block, n4, 3);
        } else {
            world.setBlock(n, n2, n3, block, n4, 2);
        }
    }
}
