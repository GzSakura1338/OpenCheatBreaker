package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class ChunkProviderHell implements IChunkProvider {
    public MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
    public double[] slowsandNoise = new double[256];
    public double[] gravelNoise = new double[256];
    public double[] netherrackExclusivityNoise = new double[256];
    public MapGenBase netherCaveGenerator = new MapGenCavesHell();
    public World worldObj;
    public Random hellRNG;
    public NoiseGeneratorOctaves netherNoiseGen1;
    public NoiseGeneratorOctaves netherNoiseGen2;
    public double[] noiseField;
    public NoiseGeneratorOctaves netherNoiseGen3;
    public NoiseGeneratorOctaves slowsandGravelNoiseGen;
    public NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
    public NoiseGeneratorOctaves netherNoiseGen6;
    public NoiseGeneratorOctaves netherNoiseGen7;
    public double[] noiseData1;
    public double[] noiseData2;
    public double[] noiseData3;
    public double[] noiseData4;
    public double[] noiseData5;

    public ChunkProviderHell(World world, long l) {
        this.worldObj = world;
        this.hellRNG = new Random(l);
        this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
        this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.netherNoiseGen6 = new NoiseGeneratorOctaves(this.hellRNG, 10);
        this.netherNoiseGen7 = new NoiseGeneratorOctaves(this.hellRNG, 16);
    }

    public void process(int n, int n2, Block[] blocks) {
        byte b = 4;
        byte b2 = 32;
        int n3 = b + 1;
        byte b3 = 17;
        int n4 = b + 1;
        this.noiseField = this.initializeNoiseField(this.noiseField, n * b, 0, n2 * b, n3, b3, n4);

        for (int i = 0; i < b; i++) {
            for (int j = 0; j < b; j++) {
                for (int k = 0; k < 16; k++) {
                    double d = 0.125;
                    double d2 = this.noiseField[((i + 0) * n4 + j + 0) * b3 + k + 0];
                    double d3 = this.noiseField[((i + 0) * n4 + j + 1) * b3 + k + 0];
                    double d4 = this.noiseField[((i + 1) * n4 + j + 0) * b3 + k + 0];
                    double d5 = this.noiseField[((i + 1) * n4 + j + 1) * b3 + k + 0];
                    double d6 = (this.noiseField[((i + 0) * n4 + j + 0) * b3 + k + 1] - d2) * d;
                    double d7 = (this.noiseField[((i + 0) * n4 + j + 1) * b3 + k + 1] - d3) * d;
                    double d8 = (this.noiseField[((i + 1) * n4 + j + 0) * b3 + k + 1] - d4) * d;
                    double d9 = (this.noiseField[((i + 1) * n4 + j + 1) * b3 + k + 1] - d5) * d;

                    for (int m = 0; m < 8; m++) {
                        double d10 = 0.25;
                        double d11 = d2;
                        double d12 = d3;
                        double d13 = (d4 - d2) * d10;
                        double d14 = (d5 - d3) * d10;

                        for (int n5 = 0; n5 < 4; n5++) {
                            int n6 = n5 + i * 4 << 11 | 0 + j * 4 << 7 | k * 8 + m;
                            short s = 128;
                            double d15 = 0.25;
                            double d16 = d11;
                            double d17 = (d12 - d11) * d15;

                            for (int i5 = 0; i5 < 4; i5++) {
                                Block block = null;
                                if (k * 8 + m < b2) {
                                    block = Blocks.lava;
                                }

                                if (d16 > 0.0) {
                                    block = Blocks.netherrack;
                                }

                                blocks[n6] = block;
                                n6 += s;
                                d16 += d17;
                            }

                            d11 += d13;
                            d12 += d14;
                        }

                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                        d5 += d9;
                    }
                }
            }
        }
    }

    public void process2(int n, int n2, Block[] blocks) {
        byte b = 64;
        double d = 0.03125;
        this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, n * 16, n2 * 16, 0, 16, 16, 1, d, d, 1.0);
        this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, n * 16, 109, n2 * 16, 16, 1, 16, d, 1.0, d);
        this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen
            .generateNoiseOctaves(this.netherrackExclusivityNoise, n * 16, n2 * 16, 0, 16, 16, 1, d * 2.0, d * 2.0, d * 2.0);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                boolean flag = this.slowsandNoise[i + j * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
                boolean flag2 = this.gravelNoise[i + j * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
                int n3 = (int)(this.netherrackExclusivityNoise[i + j * 16] / 3.0 + 3.0 + this.hellRNG.nextDouble() * 0.25);
                int n4 = -1;
                Block netherrack = Blocks.netherrack;
                Block netherrack2 = Blocks.netherrack;

                for (int k = 127; k >= 0; k--) {
                    int n5 = (j * 16 + i) * 128 + k;
                    if (k < 127 - this.hellRNG.nextInt(5) && k > 0 + this.hellRNG.nextInt(5)) {
                        Block block = blocks[n5];
                        if (block == null || block.getMaterial() == Material.air) {
                            n4 = -1;
                        } else if (block == Blocks.netherrack) {
                            if (n4 == -1) {
                                if (n3 <= 0) {
                                    netherrack = null;
                                    netherrack2 = Blocks.netherrack;
                                } else if (k >= b - 4 && k <= b + 1) {
                                    netherrack = Blocks.netherrack;
                                    netherrack2 = Blocks.netherrack;
                                    if (flag2) {
                                        netherrack = Blocks.gravel;
                                        netherrack2 = Blocks.netherrack;
                                    }

                                    if (flag) {
                                        netherrack = Blocks.soul_sand;
                                        netherrack2 = Blocks.soul_sand;
                                    }
                                }

                                if (k < b && (netherrack == null || netherrack.getMaterial() == Material.air)) {
                                    netherrack = Blocks.lava;
                                }

                                n4 = n3;
                                if (k >= b - 1) {
                                    blocks[n5] = netherrack;
                                } else {
                                    blocks[n5] = netherrack2;
                                }
                            } else if (n4 > 0) {
                                n4--;
                                blocks[n5] = netherrack2;
                            }
                        }
                    } else {
                        blocks[n5] = Blocks.bedrock;
                    }
                }
            }
        }
    }

    @Override
    public Chunk loadChunk(int n, int n2) {
        return this.provideChunk(n, n2);
    }

    @Override
    public Chunk provideChunk(int n, int n2) {
        this.hellRNG.setSeed(n * 341873128712L + n2 * 132897987541L);
        Block[] blocks = new Block[32768];
        this.process(n, n2, blocks);
        this.process2(n, n2, blocks);
        this.netherCaveGenerator.generate(this, this.worldObj, n, n2, blocks);
        this.genNetherBridge.generate(this, this.worldObj, n, n2, blocks);
        Chunk chunk = new Chunk(this.worldObj, blocks, n, n2);
        BiomeGenBase[] loadBlockGeneratorData = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, n * 16, n2 * 16, 16, 16);
        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte)loadBlockGeneratorData[i].biomeID;
        }

        chunk.resetRelightChecks();
        return chunk;
    }

    public double[] initializeNoiseField(double[] doubles, int n, int n2, int n3, int n4, int n5, int n6) {
        if (doubles == null) {
            doubles = new double[n4 * n5 * n6];
        }

        double d = 684.412;
        double d2 = 2053.236;
        this.noiseData4 = this.netherNoiseGen6.generateNoiseOctaves(this.noiseData4, n, n2, n3, n4, 1, n6, 1.0, 0.0, 1.0);
        this.noiseData5 = this.netherNoiseGen7.generateNoiseOctaves(this.noiseData5, n, n2, n3, n4, 1, n6, 100.0, 0.0, 100.0);
        this.noiseData1 = this.netherNoiseGen3.generateNoiseOctaves(this.noiseData1, n, n2, n3, n4, n5, n6, d / 80.0, d2 / 60.0, d / 80.0);
        this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, n, n2, n3, n4, n5, n6, d, d2, d);
        this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, n, n2, n3, n4, n5, n6, d, d2, d);
        int n7 = 0;
        int n8 = 0;
        double[] doubles2 = new double[n5];

        for (int i = 0; i < n5; i++) {
            doubles2[i] = Math.cos(i * Math.PI * 6.0 / n5) * 2.0;
            double d3 = i;
            if (i > n5 / 2) {
                d3 = n5 - 1 - i;
            }

            if (d3 < 4.0) {
                d3 = 4.0 - d3;
                doubles2[i] -= d3 * d3 * d3 * 10.0;
            }
        }

        for (int i = 0; i < n4; i++) {
            for (int j = 0; j < n6; j++) {
                double d3 = (this.noiseData4[n8] + 256.0) / 512.0;
                if (d3 > 1.0) {
                    d3 = 1.0;
                }

                double d4 = 0.0;
                double d5 = this.noiseData5[n8] / 8000.0;
                if (d5 < 0.0) {
                    d5 = -d5;
                }

                d5 = d5 * 3.0 - 3.0;
                if (d5 < 0.0) {
                    d5 /= 2.0;
                    if (d5 < -1.0) {
                        d5 = -1.0;
                    }

                    d5 /= 1.4;
                    d5 /= 2.0;
                    d3 = 0.0;
                } else {
                    if (d5 > 1.0) {
                        d5 = 1.0;
                    }

                    d5 /= 6.0;
                }

                d3 += 0.5;
                d5 = d5 * n5 / 16.0;
                n8++;

                for (int k = 0; k < n5; k++) {
                    double d6 = 0.0;
                    double d7 = doubles2[k];
                    double d8 = this.noiseData2[n7] / 512.0;
                    double d9 = this.noiseData3[n7] / 512.0;
                    double d10 = (this.noiseData1[n7] / 10.0 + 1.0) / 2.0;
                    if (d10 < 0.0) {
                        d6 = d8;
                    } else if (d10 > 1.0) {
                        d6 = d9;
                    } else {
                        d6 = d8 + (d9 - d8) * d10;
                    }

                    d6 -= d7;
                    if (k > n5 - 4) {
                        double d11 = (k - (n5 - 4)) / 3.0F;
                        d6 = d6 * (1.0 - d11) + -10.0 * d11;
                    }

                    if (k < d4) {
                        double d11 = (d4 - k) / 4.0;
                        if (d11 < 0.0) {
                            d11 = 0.0;
                        }

                        if (d11 > 1.0) {
                            d11 = 1.0;
                        }

                        d6 = d6 * (1.0 - d11) + -10.0 * d11;
                    }

                    doubles[n7] = d6;
                    n7++;
                }
            }
        }

        return doubles;
    }

    @Override
    public boolean chunkExists(int n, int n2) {
        return true;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int n, int n2) {
        BlockFalling.fallInstantly = true;
        int n3 = n * 16;
        int n4 = n2 * 16;
        this.genNetherBridge.generateStructuresInChunk(this.worldObj, this.hellRNG, n, n2);

        for (int i = 0; i < 8; i++) {
            int n5 = n3 + this.hellRNG.nextInt(16) + 8;
            int n6 = this.hellRNG.nextInt(120) + 4;
            int n7 = n4 + this.hellRNG.nextInt(16) + 8;
            new WorldGenHellLava(Blocks.flowing_lava, false).generate(this.worldObj, this.hellRNG, n5, n6, n7);
        }

        int n5 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1;

        for (int i = 0; i < n5; i++) {
            int n6 = n3 + this.hellRNG.nextInt(16) + 8;
            int n7 = this.hellRNG.nextInt(120) + 4;
            int n8 = n4 + this.hellRNG.nextInt(16) + 8;
            new WorldGenFire().generate(this.worldObj, this.hellRNG, n6, n7, n8);
        }

        n5 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1);

        for (int i = 0; i < n5; i++) {
            int n6 = n3 + this.hellRNG.nextInt(16) + 8;
            int n7 = this.hellRNG.nextInt(120) + 4;
            int n8 = n4 + this.hellRNG.nextInt(16) + 8;
            new WorldGenGlowStone1().generate(this.worldObj, this.hellRNG, n6, n7, n8);
        }

        for (int i = 0; i < 10; i++) {
            int n6 = n3 + this.hellRNG.nextInt(16) + 8;
            int nextInt = this.hellRNG.nextInt(128);
            int n7 = n4 + this.hellRNG.nextInt(16) + 8;
            new WorldGenGlowStone2().generate(this.worldObj, this.hellRNG, n6, nextInt, n7);
        }

        if (this.hellRNG.nextInt(1) == 0) {
            int n6 = n3 + this.hellRNG.nextInt(16) + 8;
            int nextInt = this.hellRNG.nextInt(128);
            int n7 = n4 + this.hellRNG.nextInt(16) + 8;
            new WorldGenFlowers(Blocks.brown_mushroom).generate(this.worldObj, this.hellRNG, n6, nextInt, n7);
        }

        if (this.hellRNG.nextInt(1) == 0) {
            int n6 = n3 + this.hellRNG.nextInt(16) + 8;
            int nextInt = this.hellRNG.nextInt(128);
            int n7 = n4 + this.hellRNG.nextInt(16) + 8;
            new WorldGenFlowers(Blocks.red_mushroom).generate(this.worldObj, this.hellRNG, n6, nextInt, n7);
        }

        WorldGenMinable worldGenMinable = new WorldGenMinable(Blocks.quartz_ore, 13, Blocks.netherrack);

        for (int i = 0; i < 16; i++) {
            int n6 = n3 + this.hellRNG.nextInt(16);
            int n7 = this.hellRNG.nextInt(108) + 10;
            int n8 = n4 + this.hellRNG.nextInt(16);
            worldGenMinable.generate(this.worldObj, this.hellRNG, n6, n7, n8);
        }

        for (int i = 0; i < 16; i++) {
            int n6 = n3 + this.hellRNG.nextInt(16);
            int n7 = this.hellRNG.nextInt(108) + 10;
            int n8 = n4 + this.hellRNG.nextInt(16);
            new WorldGenHellLava(Blocks.flowing_lava, true).generate(this.worldObj, this.hellRNG, n6, n7, n8);
        }

        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean saveChunks(boolean flag, IProgressUpdate progressUpdate) {
        return true;
    }

    @Override
    public void saveExtraData() {
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "HellRandomLevelSource";
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType enumCreatureType, int n, int n2, int n3) {
        if (enumCreatureType == EnumCreatureType.monster) {
            if (this.genNetherBridge.hasStructureAt(n, n2, n3)) {
                return this.genNetherBridge.getSpawnList();
            }

            if (this.genNetherBridge.check(n, n2, n3) && this.worldObj.getBlock(n, n2 - 1, n3) == Blocks.nether_brick) {
                return this.genNetherBridge.getSpawnList();
            }
        }

        BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(n, n3);
        return biomeGenForCoords.getSpawnableList(enumCreatureType);
    }

    @Override
    public ChunkPosition findClosestStructure(World world, String str, int n, int n2, int n3) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int n, int n2) {
        this.genNetherBridge.generate(this, this.worldObj, n, n2, null);
    }
}
