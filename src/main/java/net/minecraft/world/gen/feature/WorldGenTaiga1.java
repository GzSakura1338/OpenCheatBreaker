package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenTaiga1 extends WorldGenAbstractTree {
    public WorldGenTaiga1() {
        super(false);
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        int n4 = random.nextInt(5) + 7;
        int n5 = n4 - random.nextInt(2) - 3;
        int n6 = n4 - n5;
        int n7 = 1 + random.nextInt(n6 + 1);
        boolean flag = true;
        if (n2 >= 1 && n2 + n4 + 1 <= 256) {
            for (int i = n2; i <= n2 + 1 + n4 && flag; i++) {
                boolean flag2 = true;
                int n8;
                if (i - n2 < n5) {
                    n8 = 0;
                } else {
                    n8 = n7;
                }

                for (int j = n - n8; j <= n + n8 && flag; j++) {
                    for (int k = n3 - n8; k <= n3 + n8 && flag; k++) {
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

            if (!flag) {
                return false;
            }

            Block block = world.getBlock(n, n2 - 1, n3);
            if ((block == Blocks.grass || block == Blocks.dirt) && n2 < 256 - n4 - 1) {
                this.setBlockAndNotifyAdequately(world, n, n2 - 1, n3, Blocks.dirt);
                int n8 = 0;

                for (int i = n2 + n4; i >= n2 + n5; i--) {
                    for (int j = n - n8; j <= n + n8; j++) {
                        int n9 = j - n;

                        for (int k = n3 - n8; k <= n3 + n8; k++) {
                            int n10 = k - n3;
                            if ((Math.abs(n9) != n8 || Math.abs(n10) != n8 || n8 <= 0) && !world.getBlock(j, i, k).isFullBlock()) {
                                this.setBlockAndNotifyAdequately(world, j, i, k, Blocks.leaves, 1);
                            }
                        }
                    }

                    if (n8 >= 1 && i == n2 + n5 + 1) {
                        n8--;
                    } else if (n8 < n7) {
                        n8++;
                    }
                }

                for (int i = 0; i < n4 - 1; i++) {
                    Block block2 = world.getBlock(n, n2 + i, n3);
                    if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
                        this.setBlockAndNotifyAdequately(world, n, n2 + i, n3, Blocks.log, 1);
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
