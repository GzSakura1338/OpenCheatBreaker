package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenLakes extends WorldGenerator {
    public Block block;

    public WorldGenLakes(Block block) {
        this.block = block;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        n -= 8;
        n3 -= 8;

        while (n2 > 5 && world.isAirBlock(n, n2, n3)) {
            n2--;
        }

        if (n2 <= 4) {
            return false;
        }

        n2 -= 4;
        boolean[] flags = new boolean[2048];
        int n4 = random.nextInt(4) + 4;

        for (int i = 0; i < n4; i++) {
            double d = random.nextDouble() * 6.0 + 3.0;
            double d2 = random.nextDouble() * 4.0 + 2.0;
            double d3 = random.nextDouble() * 6.0 + 3.0;
            double d4 = random.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
            double d5 = random.nextDouble() * (8.0 - d2 - 4.0) + 2.0 + d2 / 2.0;
            double d6 = random.nextDouble() * (16.0 - d3 - 2.0) + 1.0 + d3 / 2.0;

            for (int j = 1; j < 15; j++) {
                for (int k = 1; k < 15; k++) {
                    for (int m = 1; m < 7; m++) {
                        double d7 = (j - d4) / (d / 2.0);
                        double d8 = (m - d5) / (d2 / 2.0);
                        double d9 = (k - d6) / (d3 / 2.0);
                        double d10 = d7 * d7 + d8 * d8 + d9 * d9;
                        if (d10 < 1.0) {
                            flags[(j * 16 + k) * 8 + m] = true;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 8; k++) {
                    boolean flag = !flags[(i * 16 + j) * 8 + k]
                        && (
                            i < 15 && flags[((i + 1) * 16 + j) * 8 + k]
                                || i > 0 && flags[((i - 1) * 16 + j) * 8 + k]
                                || j < 15 && flags[(i * 16 + j + 1) * 8 + k]
                                || j > 0 && flags[(i * 16 + (j - 1)) * 8 + k]
                                || k < 7 && flags[(i * 16 + j) * 8 + k + 1]
                                || k > 0 && flags[(i * 16 + j) * 8 + (k - 1)]
                        );
                    if (flag) {
                        Material material = world.getBlock(n + i, n2 + k, n3 + j).getMaterial();
                        if (k >= 4 && material.isLiquid()) {
                            return false;
                        }

                        if (k < 4 && !material.isSolid() && world.getBlock(n + i, n2 + k, n3 + j) != this.block) {
                            return false;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 8; k++) {
                    if (flags[(i * 16 + j) * 8 + k]) {
                        world.setBlock(n + i, n2 + k, n3 + j, k >= 4 ? Blocks.air : this.block, 0, 2);
                    }
                }
            }
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 4; k < 8; k++) {
                    if (flags[(i * 16 + j) * 8 + k]
                        && world.getBlock(n + i, n2 + k - 1, n3 + j) == Blocks.dirt
                        && world.getSavedLightValue(EnumSkyBlock.Sky, n + i, n2 + k, n3 + j) > 0) {
                        BiomeGenBase biomeGenForCoords = world.getBiomeGenForCoords(n + i, n3 + j);
                        if (biomeGenForCoords.topBlock == Blocks.mycelium) {
                            world.setBlock(n + i, n2 + k - 1, n3 + j, Blocks.mycelium, 0, 2);
                        } else {
                            world.setBlock(n + i, n2 + k - 1, n3 + j, Blocks.grass, 0, 2);
                        }
                    }
                }
            }
        }

        if (this.block.getMaterial() == Material.lava) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 8; k++) {
                        boolean flag = !flags[(i * 16 + j) * 8 + k]
                            && (
                                i < 15 && flags[((i + 1) * 16 + j) * 8 + k]
                                    || i > 0 && flags[((i - 1) * 16 + j) * 8 + k]
                                    || j < 15 && flags[(i * 16 + j + 1) * 8 + k]
                                    || j > 0 && flags[(i * 16 + (j - 1)) * 8 + k]
                                    || k < 7 && flags[(i * 16 + j) * 8 + k + 1]
                                    || k > 0 && flags[(i * 16 + j) * 8 + (k - 1)]
                            );
                        if (flag && (k < 4 || random.nextInt(2) != 0) && world.getBlock(n + i, n2 + k, n3 + j).getMaterial().isSolid()) {
                            world.setBlock(n + i, n2 + k, n3 + j, Blocks.stone, 0, 2);
                        }
                    }
                }
            }
        }

        if (this.block.getMaterial() == Material.water) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    byte b = 4;
                    if (world.isBlockFreezable(n + i, n2 + b, n3 + j)) {
                        world.setBlock(n + i, n2 + b, n3 + j, Blocks.ice, 0, 2);
                    }
                }
            }
        }

        return true;
    }
}
