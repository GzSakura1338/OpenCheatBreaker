package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees {
    public WorldGenMegaJungle(boolean flag, int n, int n2, int n3, int n4) {
        super(flag, n, n2, n3, n4);
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        int calculateMaxHeight = this.calculateMaxHeight(random);
        if (!this.checkWorld2(world, random, n, n2, n3, calculateMaxHeight)) {
            return false;
        }

        this.handleWorld3(world, n, n3, n2 + calculateMaxHeight, 2, random);

        for (int i = n2 + calculateMaxHeight - 2 - random.nextInt(4); i > n2 + calculateMaxHeight / 2; i -= 2 + random.nextInt(4)) {
            float f = random.nextFloat() * (float) Math.PI * 2.0F;
            int n4 = n + (int)(0.5F + MathHelper.cos(f) * 4.0F);
            int n5 = n3 + (int)(0.5F + MathHelper.sin(f) * 4.0F);

            for (int j = 0; j < 5; j++) {
                n4 = n + (int)(1.5F + MathHelper.cos(f) * j);
                n5 = n3 + (int)(1.5F + MathHelper.sin(f) * j);
                this.setBlockAndNotifyAdequately(world, n4, i - 3 + j / 2, n5, Blocks.log, this.woodMetadata);
            }

            int n6 = 1 + random.nextInt(2);
            int n7 = i;

            for (int j = i - n6; j <= n7; j++) {
                int n8 = j - n7;
                this.handleWorld2(world, n4, j, n5, 1 - n8, random);
            }
        }

        for (int i = 0; i < calculateMaxHeight; i++) {
            Block block = world.getBlock(n, n2 + i, n3);
            if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                this.setBlockAndNotifyAdequately(world, n, n2 + i, n3, Blocks.log, this.woodMetadata);
                if (i > 0) {
                    if (random.nextInt(3) > 0 && world.isAirBlock(n - 1, n2 + i, n3)) {
                        this.setBlockAndNotifyAdequately(world, n - 1, n2 + i, n3, Blocks.vine, 8);
                    }

                    if (random.nextInt(3) > 0 && world.isAirBlock(n, n2 + i, n3 - 1)) {
                        this.setBlockAndNotifyAdequately(world, n, n2 + i, n3 - 1, Blocks.vine, 1);
                    }
                }
            }

            if (i < calculateMaxHeight - 1) {
                block = world.getBlock(n + 1, n2 + i, n3);
                if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                    this.setBlockAndNotifyAdequately(world, n + 1, n2 + i, n3, Blocks.log, this.woodMetadata);
                    if (i > 0) {
                        if (random.nextInt(3) > 0 && world.isAirBlock(n + 2, n2 + i, n3)) {
                            this.setBlockAndNotifyAdequately(world, n + 2, n2 + i, n3, Blocks.vine, 2);
                        }

                        if (random.nextInt(3) > 0 && world.isAirBlock(n + 1, n2 + i, n3 - 1)) {
                            this.setBlockAndNotifyAdequately(world, n + 1, n2 + i, n3 - 1, Blocks.vine, 1);
                        }
                    }
                }

                block = world.getBlock(n + 1, n2 + i, n3 + 1);
                if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                    this.setBlockAndNotifyAdequately(world, n + 1, n2 + i, n3 + 1, Blocks.log, this.woodMetadata);
                    if (i > 0) {
                        if (random.nextInt(3) > 0 && world.isAirBlock(n + 2, n2 + i, n3 + 1)) {
                            this.setBlockAndNotifyAdequately(world, n + 2, n2 + i, n3 + 1, Blocks.vine, 2);
                        }

                        if (random.nextInt(3) > 0 && world.isAirBlock(n + 1, n2 + i, n3 + 2)) {
                            this.setBlockAndNotifyAdequately(world, n + 1, n2 + i, n3 + 2, Blocks.vine, 4);
                        }
                    }
                }

                block = world.getBlock(n, n2 + i, n3 + 1);
                if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                    this.setBlockAndNotifyAdequately(world, n, n2 + i, n3 + 1, Blocks.log, this.woodMetadata);
                    if (i > 0) {
                        if (random.nextInt(3) > 0 && world.isAirBlock(n - 1, n2 + i, n3 + 1)) {
                            this.setBlockAndNotifyAdequately(world, n - 1, n2 + i, n3 + 1, Blocks.vine, 8);
                        }

                        if (random.nextInt(3) > 0 && world.isAirBlock(n, n2 + i, n3 + 2)) {
                            this.setBlockAndNotifyAdequately(world, n, n2 + i, n3 + 2, Blocks.vine, 4);
                        }
                    }
                }
            }
        }

        return true;
    }

    public void handleWorld3(World world, int n, int n2, int n3, int n4, Random random) {
        byte b = 2;

        for (int i = n3 - b; i <= n3; i++) {
            int n5 = i - n3;
            this.handleWorld(world, n, i, n2, n4 + 1 - n5, random);
        }
    }
}
