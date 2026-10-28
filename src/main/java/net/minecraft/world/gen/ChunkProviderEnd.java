package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderEnd implements IChunkProvider {
    public int[][] intMatrix = new int[32][32];
    public World endWorld;
    public Random endRNG;
    public NoiseGeneratorOctaves noiseGen1;
    public NoiseGeneratorOctaves noiseGen2;
    public NoiseGeneratorOctaves noiseGen3;
    public NoiseGeneratorOctaves noiseGen4;
    public double[] densities;
    public BiomeGenBase[] biomesForGeneration;
    public double[] noiseData1;
    public double[] noiseData2;
    public double[] noiseData3;
    public double[] noiseData4;
    public double[] noiseData5;
    public NoiseGeneratorOctaves noiseGen5;

    public ChunkProviderEnd(World world, long l) {
        this.endWorld = world;
        this.endRNG = new Random(l);
        this.noiseGen1 = new NoiseGeneratorOctaves(this.endRNG, 16);
        this.noiseGen2 = new NoiseGeneratorOctaves(this.endRNG, 16);
        this.noiseGen3 = new NoiseGeneratorOctaves(this.endRNG, 8);
        this.noiseGen4 = new NoiseGeneratorOctaves(this.endRNG, 10);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.endRNG, 16);
    }

    public void process(int n, int n2, Block[] blocks, BiomeGenBase[] biomeGenBases) {
        byte b = 2;
        int n3 = b + 1;
        byte b2 = 33;
        int n4 = b + 1;
        this.densities = this.initializeNoiseField(this.densities, n * b, 0, n2 * b, n3, b2, n4);

        for (int i = 0; i < b; i++) {
            for (int j = 0; j < b; j++) {
                for (int k = 0; k < 32; k++) {
                    double d = 0.25;
                    double d2 = this.densities[((i + 0) * n4 + j + 0) * b2 + k + 0];
                    double d3 = this.densities[((i + 0) * n4 + j + 1) * b2 + k + 0];
                    double d4 = this.densities[((i + 1) * n4 + j + 0) * b2 + k + 0];
                    double d5 = this.densities[((i + 1) * n4 + j + 1) * b2 + k + 0];
                    double d6 = (this.densities[((i + 0) * n4 + j + 0) * b2 + k + 1] - d2) * d;
                    double d7 = (this.densities[((i + 0) * n4 + j + 1) * b2 + k + 1] - d3) * d;
                    double d8 = (this.densities[((i + 1) * n4 + j + 0) * b2 + k + 1] - d4) * d;
                    double d9 = (this.densities[((i + 1) * n4 + j + 1) * b2 + k + 1] - d5) * d;

                    for (int m = 0; m < 4; m++) {
                        double d10 = 0.125;
                        double d11 = d2;
                        double d12 = d3;
                        double d13 = (d4 - d2) * d10;
                        double d14 = (d5 - d3) * d10;

                        for (int n5 = 0; n5 < 8; n5++) {
                            int n6 = n5 + i * 8 << 11 | 0 + j * 8 << 7 | k * 4 + m;
                            short s = 128;
                            double d15 = 0.125;
                            double d16 = d11;
                            double d17 = (d12 - d11) * d15;

                            for (int i5 = 0; i5 < 8; i5++) {
                                Block block = null;
                                if (d16 > 0.0) {
                                    block = Blocks.end_stone;
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

    public void getMaterial(int n, int n2, Block[] blocks, BiomeGenBase[] biomeGenBases) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                byte b = 1;
                int n3 = -1;
                Block block = Blocks.end_stone;
                Block block2 = Blocks.end_stone;

                for (int k = 127; k >= 0; k--) {
                    int n4 = (j * 16 + i) * 128 + k;
                    Block block3 = blocks[n4];
                    if (block3 == null || block3.getMaterial() == Material.air) {
                        n3 = -1;
                    } else if (block3 == Blocks.stone) {
                        if (n3 == -1) {
                            if (b <= 0) {
                                block = null;
                                block2 = Blocks.end_stone;
                            }

                            n3 = b;
                            if (k >= 0) {
                                blocks[n4] = block;
                            } else {
                                blocks[n4] = block2;
                            }
                        } else if (n3 > 0) {
                            n3--;
                            blocks[n4] = block2;
                        }
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
        this.endRNG.setSeed(n * 341873128712L + n2 * 132897987541L);
        Block[] blocks = new Block[32768];
        this.biomesForGeneration = this.endWorld.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, n * 16, n2 * 16, 16, 16);
        this.process(n, n2, blocks, this.biomesForGeneration);
        this.getMaterial(n, n2, blocks, this.biomesForGeneration);
        Chunk chunk = new Chunk(this.endWorld, blocks, n, n2);
        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte)this.biomesForGeneration[i].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    public double[] initializeNoiseField(double[] doubles, int n, int n2, int n3, int n4, int n5, int n6) {
        if (doubles == null) {
            doubles = new double[n4 * n5 * n6];
        }

        double d = 684.412;
        double d2 = 684.412;
        this.noiseData4 = this.noiseGen4.generateNoiseOctaves(this.noiseData4, n, n3, n4, n6, 1.121, 1.121, 0.5);
        this.noiseData5 = this.noiseGen5.generateNoiseOctaves(this.noiseData5, n, n3, n4, n6, 200.0, 200.0, 0.5);
        d *= 2.0;
        this.noiseData1 = this.noiseGen3.generateNoiseOctaves(this.noiseData1, n, n2, n3, n4, n5, n6, d / 80.0, d2 / 160.0, d / 80.0);
        this.noiseData2 = this.noiseGen1.generateNoiseOctaves(this.noiseData2, n, n2, n3, n4, n5, n6, d, d2, d);
        this.noiseData3 = this.noiseGen2.generateNoiseOctaves(this.noiseData3, n, n2, n3, n4, n5, n6, d, d2, d);
        int n7 = 0;
        int n8 = 0;

        for (int i = 0; i < n4; i++) {
            for (int j = 0; j < n6; j++) {
                double d3 = (this.noiseData4[n8] + 256.0) / 512.0;
                if (d3 > 1.0) {
                    d3 = 1.0;
                }

                double d4 = this.noiseData5[n8] / 8000.0;
                if (d4 < 0.0) {
                    d4 = -d4 * 0.3;
                }

                d4 = d4 * 3.0 - 2.0;
                float f = (i + n - 0) / 1.0F;
                float f2 = (j + n3 - 0) / 1.0F;
                float f3 = 100.0F - MathHelper.sqrt_float(f * f + f2 * f2) * 8.0F;
                if (f3 > 80.0F) {
                    f3 = 80.0F;
                }

                if (f3 < -100.0F) {
                    f3 = -100.0F;
                }

                if (d4 > 1.0) {
                    d4 = 1.0;
                }

                d4 /= 8.0;
                d4 = 0.0;
                if (d3 < 0.0) {
                    d3 = 0.0;
                }

                d3 += 0.5;
                d4 = d4 * n5 / 16.0;
                n8++;
                double d5 = n5 / 2.0;

                for (int k = 0; k < n5; k++) {
                    double d6 = 0.0;
                    double d7 = (k - d5) * 8.0 / d3;
                    if (d7 < 0.0) {
                        d7 *= -1.0;
                    }

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

                    d6 -= 8.0;
                    d6 += f3;
                    byte b = 2;
                    if (k > n5 / 2 - b) {
                        double d11 = (k - (n5 / 2 - b)) / 64.0F;
                        if (d11 < 0.0) {
                            d11 = 0.0;
                        }

                        if (d11 > 1.0) {
                            d11 = 1.0;
                        }

                        d6 = d6 * (1.0 - d11) + -3000.0 * d11;
                    }

                    b = 8;
                    if (k < b) {
                        double d11 = (b - k) / (b - 1.0F);
                        d6 = d6 * (1.0 - d11) + -30.0 * d11;
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
        BiomeGenBase biomeGenForCoords = this.endWorld.getBiomeGenForCoords(n3 + 16, n4 + 16);
        biomeGenForCoords.decorate(this.endWorld, this.endWorld.rand, n3, n4);
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
        return "RandomLevelSource";
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType enumCreatureType, int n, int n2, int n3) {
        BiomeGenBase biomeGenForCoords = this.endWorld.getBiomeGenForCoords(n, n3);
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
    }
}
