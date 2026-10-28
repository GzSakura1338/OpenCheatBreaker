package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class WorldGenCanopyTree extends WorldGenAbstractTree {
    public WorldGenCanopyTree(boolean flag) {
        super(flag);
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        int n4 = random.nextInt(3) + random.nextInt(2) + 6;
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
            if ((block == Blocks.grass || block == Blocks.dirt) && n2 < 256 - n4 - 1) {
                this.setBlockAndNotifyAdequately(world, n, n2 - 1, n3, Blocks.dirt);
                this.setBlockAndNotifyAdequately(world, n + 1, n2 - 1, n3, Blocks.dirt);
                this.setBlockAndNotifyAdequately(world, n + 1, n2 - 1, n3 + 1, Blocks.dirt);
                this.setBlockAndNotifyAdequately(world, n, n2 - 1, n3 + 1, Blocks.dirt);
                int nextInt = random.nextInt(4);
                int n5 = n4 - random.nextInt(4);
                int n6 = 2 - random.nextInt(3);
                int n7 = n;
                int n8 = n3;
                int n9 = 0;

                for (int i = 0; i < n4; i++) {
                    int n10 = n2 + i;
                    if (i >= n5 && n6 > 0) {
                        n7 += Direction.offsetX[nextInt];
                        n8 += Direction.offsetZ[nextInt];
                        n6--;
                    }

                    Block block2 = world.getBlock(n7, n10, n8);
                    if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
                        this.setBlockAndNotifyAdequately(world, n7, n10, n8, Blocks.log2, 1);
                        this.setBlockAndNotifyAdequately(world, n7 + 1, n10, n8, Blocks.log2, 1);
                        this.setBlockAndNotifyAdequately(world, n7, n10, n8 + 1, Blocks.log2, 1);
                        this.setBlockAndNotifyAdequately(world, n7 + 1, n10, n8 + 1, Blocks.log2, 1);
                        n9 = n10;
                    }
                }

                for (int i = -2; i <= 0; i++) {
                    for (int j = -2; j <= 0; j++) {
                        byte b = -1;
                        this.setDarkOakLeaves(world, n7 + i, n9 + b, n8 + j);
                        this.setDarkOakLeaves(world, 1 + n7 - i, n9 + b, n8 + j);
                        this.setDarkOakLeaves(world, n7 + i, n9 + b, 1 + n8 - j);
                        this.setDarkOakLeaves(world, 1 + n7 - i, n9 + b, 1 + n8 - j);
                        if ((i > -2 || j > -1) && (i != -1 || j != -2)) {
                            byte b2 = 1;
                            this.setDarkOakLeaves(world, n7 + i, n9 + b2, n8 + j);
                            this.setDarkOakLeaves(world, 1 + n7 - i, n9 + b2, n8 + j);
                            this.setDarkOakLeaves(world, n7 + i, n9 + b2, 1 + n8 - j);
                            this.setDarkOakLeaves(world, 1 + n7 - i, n9 + b2, 1 + n8 - j);
                        }
                    }
                }

                if (random.nextBoolean()) {
                    this.setDarkOakLeaves(world, n7, n9 + 2, n8);
                    this.setDarkOakLeaves(world, n7 + 1, n9 + 2, n8);
                    this.setDarkOakLeaves(world, n7 + 1, n9 + 2, n8 + 1);
                    this.setDarkOakLeaves(world, n7, n9 + 2, n8 + 1);
                }

                for (int i = -3; i <= 4; i++) {
                    for (int j = -3; j <= 4; j++) {
                        if ((i != -3 || j != -3)
                            && (i != -3 || j != 4)
                            && (i != 4 || j != -3)
                            && (i != 4 || j != 4)
                            && (Math.abs(i) < 3 || Math.abs(j) < 3)) {
                            this.setDarkOakLeaves(world, n7 + i, n9, n8 + j);
                        }
                    }
                }

                for (int i = -1; i <= 2; i++) {
                    for (int j = -1; j <= 2; j++) {
                        if ((i < 0 || i > 1 || j < 0 || j > 1) && random.nextInt(3) <= 0) {
                            int n10 = random.nextInt(3) + 2;

                            for (int k = 0; k < n10; k++) {
                                this.setBlockAndNotifyAdequately(world, n + i, n9 - k - 1, n3 + j, Blocks.log2, 1);
                            }

                            for (int k = -1; k <= 1; k++) {
                                for (int m = -1; m <= 1; m++) {
                                    this.setDarkOakLeaves(world, n7 + i + k, n9 - 0, n8 + j + m);
                                }
                            }

                            for (int k = -2; k <= 2; k++) {
                                for (int m = -2; m <= 2; m++) {
                                    if (Math.abs(k) != 2 || Math.abs(m) != 2) {
                                        this.setDarkOakLeaves(world, n7 + i + k, n9 - 1, n8 + j + m);
                                    }
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

    public void setDarkOakLeaves(World world, int n, int n2, int n3) {
        Block block = world.getBlock(n, n2, n3);
        if (block.getMaterial() == Material.air) {
            this.setBlockAndNotifyAdequately(world, n, n2, n3, Blocks.leaves2, 1);
        }
    }
}
