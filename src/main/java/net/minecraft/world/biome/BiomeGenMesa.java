package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMesa extends BiomeGenBase {
    public byte[] byteArray;
    public long seed;
    public NoiseGeneratorPerlin heightCutoffNoise;
    public NoiseGeneratorPerlin heightNoise;
    public NoiseGeneratorPerlin layerNoise;
    public boolean flag;
    public boolean flag2;

    public BiomeGenMesa(int n, boolean flag, boolean flag2) {
        super(n);
        this.flag = flag;
        this.flag2 = flag2;
        this.setDisableRain();
        this.setTemperatureRainfall(2.0F, 0.0F);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.sand;
        this.topBlockMetadata = 1;
        this.fillerBlock = Blocks.stained_hardened_clay;
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 20;
        this.theBiomeDecorator.reedsPerChunk = 3;
        this.theBiomeDecorator.cactiPerChunk = 5;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.spawnableCreatureList.clear();
        if (flag2) {
            this.theBiomeDecorator.treesPerChunk = 5;
        }
    }

    @Override
    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
        return this.worldGeneratorTrees;
    }

    @Override
    public int getBiomeFoliageColor(int n, int n2, int n3) {
        return 10387789;
    }

    @Override
    public int getBiomeGrassColor(int n, int n2, int n3) {
        return 9470285;
    }

    @Override
    public void decorate(World world, Random random, int n, int n2) {
        super.decorate(world, random, n, n2);
    }

    @Override
    public void genTerrainBlocks(World world, Random random, Block[] blocks, byte[] bytes, int n, int n2, double d) {
        if (this.byteArray == null || this.seed != world.getSeed()) {
            this.initLayerBlocks(world.getSeed());
        }

        if (this.heightCutoffNoise == null || this.heightNoise == null || this.seed != world.getSeed()) {
            Random random2 = new Random(this.seed);
            this.heightCutoffNoise = new NoiseGeneratorPerlin(random2, 4);
            this.heightNoise = new NoiseGeneratorPerlin(random2, 1);
        }

        this.seed = world.getSeed();
        double d2 = 0.0;
        if (this.flag) {
            int n3 = (n & -16) + (n2 & 15);
            int n4 = (n2 & -16) + (n & 15);
            double min = Math.min(Math.abs(d), this.heightCutoffNoise.noise(n3 * 0.25, n4 * 0.25));
            if (min > 0.0) {
                double d3 = 0.001953125;
                double abs = Math.abs(this.heightNoise.noise(n3 * d3, n4 * d3));
                d2 = min * min * 2.5;
                double d4 = Math.ceil(abs * 50.0) + 14.0;
                if (d2 > d4) {
                    d2 = d4;
                }

                d2 += 64.0;
            }
        }

        int n3 = n & 15;
        int n4 = n2 & 15;
        boolean flag = true;
        Block block = Blocks.stained_hardened_clay;
        Block fillerBlock = this.fillerBlock;
        int n5 = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        boolean flag2 = Math.cos(d / 3.0 * Math.PI) > 0.0;
        int n6 = -1;
        boolean flag3 = false;
        int n7 = blocks.length / 256;

        for (int i = 255; i >= 0; i--) {
            int n8 = (n4 * 16 + n3) * n7 + i;
            if ((blocks[n8] == null || blocks[n8].getMaterial() == Material.air) && i < (int)d2) {
                blocks[n8] = Blocks.stone;
            }

            if (i <= 0 + random.nextInt(5)) {
                blocks[n8] = Blocks.bedrock;
            } else {
                Block block2 = blocks[n8];
                if (block2 == null || block2.getMaterial() == Material.air) {
                    n6 = -1;
                } else if (block2 == Blocks.stone) {
                    if (n6 == -1) {
                        flag3 = false;
                        if (n5 <= 0) {
                            block = null;
                            fillerBlock = Blocks.stone;
                        } else if (i >= 59 && i <= 64) {
                            block = Blocks.stained_hardened_clay;
                            fillerBlock = this.fillerBlock;
                        }

                        if (i < 63 && (block == null || block.getMaterial() == Material.air)) {
                            block = Blocks.water;
                        }

                        n6 = n5 + Math.max(0, i - 63);
                        if (i >= 62) {
                            if (this.flag2 && i > 86 + n5 * 2) {
                                if (flag2) {
                                    blocks[n8] = Blocks.dirt;
                                    bytes[n8] = 1;
                                } else {
                                    blocks[n8] = Blocks.grass;
                                }
                            } else if (i > 66 + n5) {
                                byte b = 16;
                                if (i < 64 || i > 127) {
                                    b = 1;
                                } else if (!flag2) {
                                    b = this.computeByte(n, i, n2);
                                }

                                if (b < 16) {
                                    blocks[n8] = Blocks.stained_hardened_clay;
                                    bytes[n8] = b;
                                } else {
                                    blocks[n8] = Blocks.hardened_clay;
                                }
                            } else {
                                blocks[n8] = this.topBlock;
                                bytes[n8] = (byte)this.topBlockMetadata;
                                flag3 = true;
                            }
                        } else {
                            blocks[n8] = fillerBlock;
                            if (fillerBlock == Blocks.stained_hardened_clay) {
                                bytes[n8] = 1;
                            }
                        }
                    } else if (n6 > 0) {
                        n6--;
                        if (flag3) {
                            blocks[n8] = Blocks.stained_hardened_clay;
                            bytes[n8] = 1;
                        } else {
                            byte computeByte = this.computeByte(n, i, n2);
                            if (computeByte < 16) {
                                blocks[n8] = Blocks.stained_hardened_clay;
                                bytes[n8] = computeByte;
                            } else {
                                blocks[n8] = Blocks.hardened_clay;
                            }
                        }
                    }
                }
            }
        }
    }

    public void initLayerBlocks(long l) {
        this.byteArray = new byte[64];
        Arrays.fill(this.byteArray, (byte)16);
        Random random = new Random(l);
        this.layerNoise = new NoiseGeneratorPerlin(random, 1);

        for (int i = 0; i < 64; i++) {
            i += random.nextInt(5) + 1;
            if (i < 64) {
                this.byteArray[i] = 1;
            }
        }

        int n = random.nextInt(4) + 2;

        for (int i = 0; i < n; i++) {
            int n2 = random.nextInt(3) + 1;
            int nextInt = random.nextInt(64);

            for (int j = 0; nextInt + j < 64 && j < n2; j++) {
                this.byteArray[nextInt + j] = 4;
            }
        }

        int n2 = random.nextInt(4) + 2;

        for (int i = 0; i < n2; i++) {
            int n3 = random.nextInt(3) + 2;
            int nextInt = random.nextInt(64);

            for (int j = 0; nextInt + j < 64 && j < n3; j++) {
                this.byteArray[nextInt + j] = 12;
            }
        }

        int n3 = random.nextInt(4) + 2;

        for (int i = 0; i < n3; i++) {
            int n4 = random.nextInt(3) + 1;
            int nextInt = random.nextInt(64);

            for (int j = 0; nextInt + j < 64 && j < n4; j++) {
                this.byteArray[nextInt + j] = 14;
            }
        }

        int n4 = random.nextInt(3) + 3;
        int n5 = 0;

        for (int i = 0; i < n4; i++) {
            byte b = 1;
            n5 += random.nextInt(16) + 4;

            for (int j = 0; n5 + j < 64 && j < b; j++) {
                this.byteArray[n5 + j] = 0;
                if (n5 + j > 1 && random.nextBoolean()) {
                    this.byteArray[n5 + j - 1] = 8;
                }

                if (n5 + j < 63 && random.nextBoolean()) {
                    this.byteArray[n5 + j + 1] = 8;
                }
            }
        }
    }

    public byte computeByte(int n, int n2, int n3) {
        int round = (int)Math.round(this.layerNoise.noise(n * 1.0 / 512.0, n * 1.0 / 512.0) * 2.0);
        return this.byteArray[(n2 + round + 64) % 64];
    }

    @Override
    public BiomeGenBase createMutation() {
        boolean flag = this.biomeID == BiomeGenBase.mesa.biomeID;
        BiomeGenMesa biomeGenMesa = new BiomeGenMesa(this.biomeID + 128, flag, this.flag2);
        if (!flag) {
            biomeGenMesa.setHeight(height_LowHills);
            biomeGenMesa.setBiomeName(this.biomeName + " M");
        } else {
            biomeGenMesa.setBiomeName(this.biomeName + " (Bryce)");
        }

        biomeGenMesa.findBiomeGenBase(this.color, true);
        return biomeGenMesa;
    }
}
