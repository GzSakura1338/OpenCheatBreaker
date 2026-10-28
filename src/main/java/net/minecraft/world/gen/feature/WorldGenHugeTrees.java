package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public abstract class WorldGenHugeTrees extends WorldGenAbstractTree {
    public int baseHeight;
    public int extraHeight;
    public int woodMetadata;
    public int leavesMetadata;

    public WorldGenHugeTrees(boolean flag, int n, int n2, int n3, int n4) {
        super(flag);
        this.baseHeight = n;
        this.extraHeight = n2;
        this.woodMetadata = n3;
        this.leavesMetadata = n4;
    }

    public int calculateMaxHeight(Random random) {
        int n = random.nextInt(3) + this.baseHeight;
        if (this.extraHeight > 1) {
            n += random.nextInt(this.extraHeight);
        }

        return n;
    }

    public boolean checkWorld(World world, Random random, int n, int n2, int n3, int n4) {
        boolean flag = true;
        if (n2 >= 1 && n2 + n4 + 1 <= 256) {
            for (int i = n2; i <= n2 + 1 + n4; i++) {
                byte b = 2;
                if (i == n2) {
                    b = 1;
                }

                if (i >= n2 + 1 + n4 - 2) {
                    b = 2;
                }

                for (int j = n - b; j <= n + b && flag; j++) {
                    for (int k = n3 - b; k <= n3 + b && flag; k++) {
                        if (i >= 0 && i < 256) {
                            Block block = world.getBlock(j, i, k);
                            if (!this.isBlockReplaceable(block)) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    public boolean checkWorld(World world, Random random, int n, int n2, int n3) {
        Block block = world.getBlock(n, n2 - 1, n3);
        if ((block == Blocks.grass || block == Blocks.dirt) && n2 >= 2) {
            world.setBlock(n, n2 - 1, n3, Blocks.dirt, 0, 2);
            world.setBlock(n + 1, n2 - 1, n3, Blocks.dirt, 0, 2);
            world.setBlock(n, n2 - 1, n3 + 1, Blocks.dirt, 0, 2);
            world.setBlock(n + 1, n2 - 1, n3 + 1, Blocks.dirt, 0, 2);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkWorld2(World world, Random random, int n, int n2, int n3, int n4) {
        return this.checkWorld(world, random, n, n2, n3, n4) && this.checkWorld(world, random, n, n2, n3);
    }

    public void handleWorld(World world, int n, int n2, int n3, int n4, Random random) {
        int n5 = n4 * n4;

        for (int i = n - n4; i <= n + n4 + 1; i++) {
            int n6 = i - n;

            for (int j = n3 - n4; j <= n3 + n4 + 1; j++) {
                int n7 = j - n3;
                int n8 = n6 - 1;
                int n9 = n7 - 1;
                if (n6 * n6 + n7 * n7 <= n5
                    || n8 * n8 + n9 * n9 <= n5
                    || n6 * n6 + n9 * n9 <= n5
                    || n8 * n8 + n7 * n7 <= n5) {
                    Block block = world.getBlock(i, n2, j);
                    if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                        this.setBlockAndNotifyAdequately(world, i, n2, j, Blocks.leaves, this.leavesMetadata);
                    }
                }
            }
        }
    }

    public void handleWorld2(World world, int n, int n2, int n3, int n4, Random random) {
        int n5 = n4 * n4;

        for (int i = n - n4; i <= n + n4; i++) {
            int n6 = i - n;

            for (int j = n3 - n4; j <= n3 + n4; j++) {
                int n7 = j - n3;
                if (n6 * n6 + n7 * n7 <= n5) {
                    Block block = world.getBlock(i, n2, j);
                    if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                        this.setBlockAndNotifyAdequately(world, i, n2, j, Blocks.leaves, this.leavesMetadata);
                    }
                }
            }
        }
    }
}
