package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenBigMushroom extends WorldGenerator {
    public int mushroomType = -1;

    public WorldGenBigMushroom(int n) {
        super(true);
        this.mushroomType = n;
    }

    public WorldGenBigMushroom() {
        super(false);
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        int nextInt = random.nextInt(2);
        if (this.mushroomType >= 0) {
            nextInt = this.mushroomType;
        }

        int n4 = random.nextInt(3) + 4;
        boolean flag = true;
        if (n2 >= 1 && n2 + n4 + 1 < 256) {
            for (int i = n2; i <= n2 + 1 + n4; i++) {
                byte b = 3;
                if (i <= n2 + 3) {
                    b = 0;
                }

                for (int j = n - b; j <= n + b && flag; j++) {
                    for (int k = n3 - b; k <= n3 + b && flag; k++) {
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
            if (block != Blocks.dirt && block != Blocks.grass && block != Blocks.mycelium) {
                return false;
            }

            int n5 = n2 + n4;
            if (nextInt == 1) {
                n5 = n2 + n4 - 3;
            }

            for (int i = n5; i <= n2 + n4; i++) {
                int n6 = 1;
                if (i < n2 + n4) {
                    n6++;
                }

                if (nextInt == 0) {
                    n6 = 3;
                }

                for (int j = n - n6; j <= n + n6; j++) {
                    for (int k = n3 - n6; k <= n3 + n6; k++) {
                        int n7 = 5;
                        if (j == n - n6) {
                            n7--;
                        }

                        if (j == n + n6) {
                            n7++;
                        }

                        if (k == n3 - n6) {
                            n7 -= 3;
                        }

                        if (k == n3 + n6) {
                            n7 += 3;
                        }

                        if (nextInt == 0 || i < n2 + n4) {
                            if ((j == n - n6 || j == n + n6) && (k == n3 - n6 || k == n3 + n6)) {
                                continue;
                            }

                            if (j == n - (n6 - 1) && k == n3 - n6) {
                                n7 = 1;
                            }

                            if (j == n - n6 && k == n3 - (n6 - 1)) {
                                n7 = 1;
                            }

                            if (j == n + (n6 - 1) && k == n3 - n6) {
                                n7 = 3;
                            }

                            if (j == n + n6 && k == n3 - (n6 - 1)) {
                                n7 = 3;
                            }

                            if (j == n - (n6 - 1) && k == n3 + n6) {
                                n7 = 7;
                            }

                            if (j == n - n6 && k == n3 + (n6 - 1)) {
                                n7 = 7;
                            }

                            if (j == n + (n6 - 1) && k == n3 + n6) {
                                n7 = 9;
                            }

                            if (j == n + n6 && k == n3 + (n6 - 1)) {
                                n7 = 9;
                            }
                        }

                        if (n7 == 5 && i < n2 + n4) {
                            n7 = 0;
                        }

                        if ((n7 != 0 || n2 >= n2 + n4 - 1) && !world.getBlock(j, i, k).isFullBlock()) {
                            this.setBlockAndNotifyAdequately(
                                world, j, i, k, Block.getBlockById(Block.getIdFromBlock(Blocks.brown_mushroom_block) + nextInt), n7
                            );
                        }
                    }
                }
            }

            for (int i = 0; i < n4; i++) {
                Block block2 = world.getBlock(n, n2 + i, n3);
                if (!block2.isFullBlock()) {
                    this.setBlockAndNotifyAdequately(
                        world, n, n2 + i, n3, Block.getBlockById(Block.getIdFromBlock(Blocks.brown_mushroom_block) + nextInt), 10
                    );
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
