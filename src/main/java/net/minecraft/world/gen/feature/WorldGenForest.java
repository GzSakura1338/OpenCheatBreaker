package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenAbstractTree {
    public boolean generateTall;

    public WorldGenForest(boolean flag, boolean flag2) {
        super(flag);
        this.generateTall = flag2;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        int n4 = random.nextInt(3) + 5;
        if (this.generateTall) {
            n4 += random.nextInt(7);
        }

        boolean flag = true;
        if (n2 >= 1 && n2 + n4 + 1 <= 256) {
            for (int i = n2; i <= n2 + 1 + n4; i++) {
                byte b = 1;
                if (i == n2) {
                    b = 0;
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

            if (!flag) {
                return false;
            }

            Block block = world.getBlock(n, n2 - 1, n3);
            if ((block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland) && n2 < 256 - n4 - 1) {
                this.setBlockAndNotifyAdequately(world, n, n2 - 1, n3, Blocks.dirt);

                for (int i = n2 - 3 + n4; i <= n2 + n4; i++) {
                    int n5 = i - (n2 + n4);
                    int n6 = 1 - n5 / 2;

                    for (int j = n - n6; j <= n + n6; j++) {
                        int n7 = j - n;

                        for (int k = n3 - n6; k <= n3 + n6; k++) {
                            int n8 = k - n3;
                            if (Math.abs(n7) != n6 || Math.abs(n8) != n6 || random.nextInt(2) != 0 && n5 != 0) {
                                Block block2 = world.getBlock(j, i, k);
                                if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
                                    this.setBlockAndNotifyAdequately(world, j, i, k, Blocks.leaves, 2);
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < n4; i++) {
                    Block block2 = world.getBlock(n, n2 + i, n3);
                    if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
                        this.setBlockAndNotifyAdequately(world, n, n2 + i, n3, Blocks.log, 2);
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
