package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenSwamp extends WorldGenAbstractTree {
    public WorldGenSwamp() {
        super(false);
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        int n4 = random.nextInt(4) + 5;

        while (world.getBlock(n, n2 - 1, n3).getMaterial() == Material.water) {
            n2--;
        }

        boolean flag = true;
        if (n2 >= 1 && n2 + n4 + 1 <= 256) {
            for (int i = n2; i <= n2 + 1 + n4; i++) {
                byte b = 1;
                if (i == n2) {
                    b = 0;
                }

                if (i >= n2 + 1 + n4 - 2) {
                    b = 3;
                }

                for (int j = n - b; j <= n + b && flag; j++) {
                    for (int k = n3 - b; k <= n3 + b && flag; k++) {
                        if (i >= 0 && i < 256) {
                            Block block = world.getBlock(j, i, k);
                            if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                                if (block != Blocks.water && block != Blocks.flowing_water) {
                                    flag = false;
                                } else if (i > n2) {
                                    flag = false;
                                }
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

                for (int i = n2 - 3 + n4; i <= n2 + n4; i++) {
                    int n5 = i - (n2 + n4);
                    int n6 = 2 - n5 / 2;

                    for (int j = n - n6; j <= n + n6; j++) {
                        int n7 = j - n;

                        for (int k = n3 - n6; k <= n3 + n6; k++) {
                            int n8 = k - n3;
                            if ((Math.abs(n7) != n6 || Math.abs(n8) != n6 || random.nextInt(2) != 0 && n5 != 0)
                                && !world.getBlock(j, i, k).isFullBlock()) {
                                this.setBlockAndNotifyAdequately(world, j, i, k, Blocks.leaves);
                            }
                        }
                    }
                }

                for (int i = 0; i < n4; i++) {
                    Block block2 = world.getBlock(n, n2 + i, n3);
                    if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves || block2 == Blocks.flowing_water || block2 == Blocks.water) {
                        this.setBlockAndNotifyAdequately(world, n, n2 + i, n3, Blocks.log);
                    }
                }

                for (int i = n2 - 3 + n4; i <= n2 + n4; i++) {
                    int n5 = i - (n2 + n4);
                    int n6 = 2 - n5 / 2;

                    for (int j = n - n6; j <= n + n6; j++) {
                        for (int k = n3 - n6; k <= n3 + n6; k++) {
                            if (world.getBlock(j, i, k).getMaterial() == Material.leaves) {
                                if (random.nextInt(4) == 0 && world.getBlock(j - 1, i, k).getMaterial() == Material.air) {
                                    this.generateVines(world, j - 1, i, k, 8);
                                }

                                if (random.nextInt(4) == 0 && world.getBlock(j + 1, i, k).getMaterial() == Material.air) {
                                    this.generateVines(world, j + 1, i, k, 2);
                                }

                                if (random.nextInt(4) == 0 && world.getBlock(j, i, k - 1).getMaterial() == Material.air) {
                                    this.generateVines(world, j, i, k - 1, 1);
                                }

                                if (random.nextInt(4) == 0 && world.getBlock(j, i, k + 1).getMaterial() == Material.air) {
                                    this.generateVines(world, j, i, k + 1, 4);
                                }
                            }
                        }
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

    public void generateVines(World world, int n, int n2, int n3, int n4) {
        this.setBlockAndNotifyAdequately(world, n, n2, n3, Blocks.vine, n4);

        for (int i = 4; world.getBlock(n, --n2, n3).getMaterial() == Material.air && i > 0; i--) {
            this.setBlockAndNotifyAdequately(world, n, n2, n3, Blocks.vine, n4);
        }
    }
}
