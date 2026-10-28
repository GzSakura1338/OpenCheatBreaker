package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaPineTree extends WorldGenHugeTrees {
    public boolean useBaseHeight;

    public WorldGenMegaPineTree(boolean flag, boolean flag2) {
        super(flag, 13, 15, 1, 1);
        this.useBaseHeight = flag2;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        int calculateMaxHeight = this.calculateMaxHeight(random);
        if (!this.checkWorld2(world, random, n, n2, n3, calculateMaxHeight)) {
            return false;
        }

        this.generateTopLeaves(world, n, n3, n2 + calculateMaxHeight, 0, random);

        for (int i = 0; i < calculateMaxHeight; i++) {
            Block block = world.getBlock(n, n2 + i, n3);
            if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                this.setBlockAndNotifyAdequately(world, n, n2 + i, n3, Blocks.log, this.woodMetadata);
            }

            if (i < calculateMaxHeight - 1) {
                block = world.getBlock(n + 1, n2 + i, n3);
                if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                    this.setBlockAndNotifyAdequately(world, n + 1, n2 + i, n3, Blocks.log, this.woodMetadata);
                }

                block = world.getBlock(n + 1, n2 + i, n3 + 1);
                if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                    this.setBlockAndNotifyAdequately(world, n + 1, n2 + i, n3 + 1, Blocks.log, this.woodMetadata);
                }

                block = world.getBlock(n, n2 + i, n3 + 1);
                if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                    this.setBlockAndNotifyAdequately(world, n, n2 + i, n3 + 1, Blocks.log, this.woodMetadata);
                }
            }
        }

        return true;
    }

    public void generateTopLeaves(World world, int n, int n2, int n3, int n4, Random random) {
        int nextInt = random.nextInt(5);
        if (this.useBaseHeight) {
            nextInt += this.baseHeight;
        } else {
            nextInt += 3;
        }

        int n5 = 0;

        for (int i = n3 - nextInt; i <= n3; i++) {
            int n6 = n3 - i;
            int n7 = n4 + MathHelper.floor_float((float)n6 / nextInt * 3.5F);
            this.handleWorld(world, n, i, n2, n7 + (n6 > 0 && n7 == n5 && (i & 1) == 0 ? 1 : 0), random);
            n5 = n7;
        }
    }

    @Override
    public void handleWorld(World world, Random random, int n, int n2, int n3) {
        this.handleWorld2(world, random, n - 1, n2, n3 - 1);
        this.handleWorld2(world, random, n + 2, n2, n3 - 1);
        this.handleWorld2(world, random, n - 1, n2, n3 + 2);
        this.handleWorld2(world, random, n + 2, n2, n3 + 2);

        for (int i = 0; i < 5; i++) {
            int nextInt = random.nextInt(64);
            int n4 = nextInt % 8;
            int n5 = nextInt / 8;
            if (n4 == 0 || n4 == 7 || n5 == 0 || n5 == 7) {
                this.handleWorld2(world, random, n - 3 + n4, n2, n3 - 3 + n5);
            }
        }
    }

    public void handleWorld2(World world, Random random, int n, int n2, int n3) {
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                    this.handleWorld(world, n + i, n2, n3 + j);
                }
            }
        }
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        for (int i = n2 + 2; i >= n2 - 3; i--) {
            Block block = world.getBlock(n, i, n3);
            if (block == Blocks.grass || block == Blocks.dirt) {
                this.setBlockAndNotifyAdequately(world, n, i, n3, Blocks.dirt, 2);
                break;
            }

            if (block.getMaterial() != Material.air && i < n2) {
                break;
            }
        }
    }
}
