package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenTaiga2 extends WorldGenAbstractTree {
    public WorldGenTaiga2(boolean flag) {
        super(flag);
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        int n4 = random.nextInt(4) + 6;
        int n5 = 1 + random.nextInt(2);
        int n6 = n4 - n5;
        int n7 = 2 + random.nextInt(2);
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
                            if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
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
            if ((block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland) && n2 < 256 - n4 - 1) {
                this.setBlockAndNotifyAdequately(world, n, n2 - 1, n3, Blocks.dirt);
                int nextInt = random.nextInt(2);
                int n8 = 1;
                byte b = 0;

                for (int i = 0; i <= n6; i++) {
                    int n9 = n2 + n4 - i;

                    for (int j = n - nextInt; j <= n + nextInt; j++) {
                        int n10 = j - n;

                        for (int k = n3 - nextInt; k <= n3 + nextInt; k++) {
                            int n11 = k - n3;
                            if ((Math.abs(n10) != nextInt || Math.abs(n11) != nextInt || nextInt <= 0) && !world.getBlock(j, n9, k).isFullBlock()) {
                                this.setBlockAndNotifyAdequately(world, j, n9, k, Blocks.leaves, 1);
                            }
                        }
                    }

                    if (nextInt >= n8) {
                        nextInt = b;
                        b = 1;
                        if (++n8 > n7) {
                            n8 = n7;
                        }
                    } else {
                        nextInt++;
                    }
                }

                int nextInt2 = random.nextInt(3);

                for (int i = 0; i < n4 - nextInt2; i++) {
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
