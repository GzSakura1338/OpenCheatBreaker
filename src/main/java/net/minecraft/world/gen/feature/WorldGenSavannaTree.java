package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class WorldGenSavannaTree extends WorldGenAbstractTree {
    public WorldGenSavannaTree(boolean flag) {
        super(flag);
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        int n4 = random.nextInt(3) + random.nextInt(3) + 5;
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
                int nextInt = random.nextInt(4);
                int n5 = n4 - random.nextInt(4) - 1;
                int n6 = 3 - random.nextInt(3);
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
                        this.setBlockAndNotifyAdequately(world, n7, n10, n8, Blocks.log2, 0);
                        n9 = n10;
                    }
                }

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        this.handleWorld(world, n7 + i, n9 + 1, n8 + j);
                    }
                }

                this.handleWorld(world, n7 + 2, n9 + 1, n8);
                this.handleWorld(world, n7 - 2, n9 + 1, n8);
                this.handleWorld(world, n7, n9 + 1, n8 + 2);
                this.handleWorld(world, n7, n9 + 1, n8 - 2);

                for (int i = -3; i <= 3; i++) {
                    for (int j = -3; j <= 3; j++) {
                        if (Math.abs(i) != 3 || Math.abs(j) != 3) {
                            this.handleWorld(world, n7 + i, n9, n8 + j);
                        }
                    }
                }

                n7 = n;
                n8 = n3;
                int nextInt2 = random.nextInt(4);
                if (nextInt2 != nextInt) {
                    int n10 = n5 - random.nextInt(2) - 1;
                    int n11 = 1 + random.nextInt(3);
                    n9 = 0;

                    for (int i = n10; i < n4 && n11 > 0; n11--) {
                        if (i >= 1) {
                            int n12 = n2 + i;
                            n7 += Direction.offsetX[nextInt2];
                            n8 += Direction.offsetZ[nextInt2];
                            Block block2 = world.getBlock(n7, n12, n8);
                            if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
                                this.setBlockAndNotifyAdequately(world, n7, n12, n8, Blocks.log2, 0);
                                n9 = n12;
                            }
                        }

                        i++;
                    }

                    if (n9 > 0) {
                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                this.handleWorld(world, n7 + i, n9 + 1, n8 + j);
                            }
                        }

                        for (int i = -2; i <= 2; i++) {
                            for (int j = -2; j <= 2; j++) {
                                if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                                    this.handleWorld(world, n7 + i, n9, n8 + j);
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

    public void handleWorld(World world, int n, int n2, int n3) {
        Block block = world.getBlock(n, n2, n3);
        if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
            this.setBlockAndNotifyAdequately(world, n, n2, n3, Blocks.leaves2, 0);
        }
    }
}
