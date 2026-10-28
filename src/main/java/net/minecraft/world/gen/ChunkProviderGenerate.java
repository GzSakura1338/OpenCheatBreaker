package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;

public class ChunkProviderGenerate implements IChunkProvider {
    public double[] stoneNoise = new double[256];
    public MapGenBase caveGenerator = new MapGenCaves();
    public MapGenStronghold strongholdGenerator = new MapGenStronghold();
    public MapGenVillage villageGenerator = new MapGenVillage();
    public MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    public MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
    public MapGenBase ravineGenerator = new MapGenRavine();
    public int[][] intMatrix = new int[32][32];
    public World worldObj;
    public boolean mapFeaturesEnabled;
    public WorldType type;
    public Random rand;
    public NoiseGeneratorOctaves noiseGeneratorOctaves;
    public NoiseGeneratorOctaves noiseGeneratorOctaves2;
    public NoiseGeneratorOctaves noiseGeneratorOctaves3;
    public NoiseGeneratorPerlin noiseGeneratorPerlin;
    public NoiseGeneratorOctaves noiseGen5;
    public NoiseGeneratorOctaves noiseGen6;
    public NoiseGeneratorOctaves mobSpawnerNoise;
    public double[] doubleArray;
    public BiomeGenBase[] biomesForGeneration;
    public double[] doubleArray2;
    public double[] doubleArray3;
    public double[] doubleArray4;
    public double[] doubleArray5;
    public float[] parabolicField;

    public ChunkProviderGenerate(World world, long l, boolean flag) {
        this.worldObj = world;
        this.mapFeaturesEnabled = flag;
        this.type = world.getWorldInfo().getTerrainType();
        this.rand = new Random(l);
        this.noiseGeneratorOctaves = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGeneratorOctaves2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGeneratorOctaves3 = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseGeneratorPerlin = new NoiseGeneratorPerlin(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.doubleArray = new double[825];
        this.parabolicField = new float[25];

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                float f = 10.0F / MathHelper.sqrt_float(i * i + j * j + 0.2F);
                this.parabolicField[i + 2 + (j + 2) * 5] = f;
            }
        }
    }

    public void process(int n, int n2, Block[] blocks) {
        byte b = 63;
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, n * 4 - 2, n2 * 4 - 2, 10, 10);
        this.process(n * 4, 0, n2 * 4);

        for (int i = 0; i < 4; i++) {
            int n3 = i * 5;
            int n4 = (i + 1) * 5;

            for (int j = 0; j < 4; j++) {
                int n5 = (n3 + j) * 33;
                int n6 = (n3 + j + 1) * 33;
                int n7 = (n4 + j) * 33;
                int n8 = (n4 + j + 1) * 33;

                for (int k = 0; k < 32; k++) {
                    double d = 0.125;
                    double d2 = this.doubleArray[n5 + k];
                    double d3 = this.doubleArray[n6 + k];
                    double d4 = this.doubleArray[n7 + k];
                    double d5 = this.doubleArray[n8 + k];
                    double d6 = (this.doubleArray[n5 + k + 1] - d2) * d;
                    double d7 = (this.doubleArray[n6 + k + 1] - d3) * d;
                    double d8 = (this.doubleArray[n7 + k + 1] - d4) * d;
                    double d9 = (this.doubleArray[n8 + k + 1] - d5) * d;

                    for (int m = 0; m < 8; m++) {
                        double d10 = 0.25;
                        double d11 = d2;
                        double d12 = d3;
                        double d13 = (d4 - d2) * d10;
                        double d14 = (d5 - d3) * d10;

                        for (int n9 = 0; n9 < 4; n9++) {
                            int n10 = n9 + i * 4 << 12 | 0 + j * 4 << 8 | k * 8 + m;
                            short s = 256;
                            n10 -= s;
                            double d15 = 0.25;
                            double d16 = (d12 - d11) * d15;
                            double d17 = d11 - d16;

                            for (int i5 = 0; i5 < 4; i5++) {
                                if ((d17 += d16) > 0.0) {
                                    blocks[n10 += s] = Blocks.stone;
                                } else if (k * 8 + m < b) {
                                    blocks[n10 += s] = Blocks.water;
                                } else {
                                    blocks[n10 += s] = null;
                                }
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

    public void replaceBlocksForBiome(int n, int n2, Block[] blocks, byte[] bytes, BiomeGenBase[] biomeGenBases) {
        double d = 0.03125;
        this.stoneNoise = this.noiseGeneratorPerlin.getDoubleArray(this.stoneNoise, n * 16, n2 * 16, 16, 16, d * 2.0, d * 2.0, 1.0);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                BiomeGenBase biomeGenBase = biomeGenBases[j + i * 16];
                biomeGenBase.genTerrainBlocks(this.worldObj, this.rand, blocks, bytes, n * 16 + i, n2 * 16 + j, this.stoneNoise[j + i * 16]);
            }
        }
    }

    @Override
    public Chunk loadChunk(int n, int n2) {
        return this.provideChunk(n, n2);
    }

    @Override
    public Chunk provideChunk(int n, int n2) {
        this.rand.setSeed(n * 341873128712L + n2 * 132897987541L);
        Block[] blocks = new Block[65536];
        byte[] bytes = new byte[65536];
        this.process(n, n2, blocks);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, n * 16, n2 * 16, 16, 16);
        this.replaceBlocksForBiome(n, n2, blocks, bytes, this.biomesForGeneration);
        this.caveGenerator.generate(this, this.worldObj, n, n2, blocks);
        this.ravineGenerator.generate(this, this.worldObj, n, n2, blocks);
        if (this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generate(this, this.worldObj, n, n2, blocks);
            this.villageGenerator.generate(this, this.worldObj, n, n2, blocks);
            this.strongholdGenerator.generate(this, this.worldObj, n, n2, blocks);
            this.scatteredFeatureGenerator.generate(this, this.worldObj, n, n2, blocks);
        }

        Chunk chunk = new Chunk(this.worldObj, blocks, bytes, n, n2);
        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte)this.biomesForGeneration[i].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    public void process(int n, int n2, int n3) {
        double d = 684.412;
        double d2 = 684.412;
        double d3 = 512.0;
        double d4 = 512.0;
        this.doubleArray5 = this.noiseGen6.generateNoiseOctaves(this.doubleArray5, n, n3, 5, 5, 200.0, 200.0, 0.5);
        this.doubleArray2 = this.noiseGeneratorOctaves3
            .generateNoiseOctaves(this.doubleArray2, n, n2, n3, 5, 33, 5, 8.555150000000001, 4.277575000000001, 8.555150000000001);
        this.doubleArray3 = this.noiseGeneratorOctaves.generateNoiseOctaves(this.doubleArray3, n, n2, n3, 5, 33, 5, 684.412, 684.412, 684.412);
        this.doubleArray4 = this.noiseGeneratorOctaves2.generateNoiseOctaves(this.doubleArray4, n, n2, n3, 5, 33, 5, 684.412, 684.412, 684.412);
        boolean flag = false;
        boolean flag2 = false;
        int n4 = 0;
        int n5 = 0;
        double d5 = 8.5;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                float f = 0.0F;
                float f2 = 0.0F;
                float f3 = 0.0F;
                byte b = 2;
                BiomeGenBase biomeGenBase = this.biomesForGeneration[i + 2 + (j + 2) * 10];

                for (int k = -b; k <= b; k++) {
                    for (int m = -b; m <= b; m++) {
                        BiomeGenBase biomeGenBase2 = this.biomesForGeneration[i + k + 2 + (j + m + 2) * 10];
                        float minHeight = biomeGenBase2.minHeight;
                        float maxHeight = biomeGenBase2.maxHeight;
                        if (this.type == WorldType.AMPLIFIED && minHeight > 0.0F) {
                            minHeight = 1.0F + minHeight * 2.0F;
                            maxHeight = 1.0F + maxHeight * 4.0F;
                        }

                        float f4 = this.parabolicField[k + 2 + (m + 2) * 5] / (minHeight + 2.0F);
                        if (biomeGenBase2.minHeight > biomeGenBase.minHeight) {
                            f4 /= 2.0F;
                        }

                        f += maxHeight * f4;
                        f2 += minHeight * f4;
                        f3 += f4;
                    }
                }

                f /= f3;
                f2 /= f3;
                f = f * 0.9F + 0.1F;
                f2 = (f2 * 4.0F - 1.0F) / 8.0F;
                double d6 = this.doubleArray5[n5] / 8000.0;
                if (d6 < 0.0) {
                    d6 = -d6 * 0.3;
                }

                d6 = d6 * 3.0 - 2.0;
                if (d6 < 0.0) {
                    d6 /= 2.0;
                    if (d6 < -1.0) {
                        d6 = -1.0;
                    }

                    d6 /= 1.4;
                    d6 /= 2.0;
                } else {
                    if (d6 > 1.0) {
                        d6 = 1.0;
                    }

                    d6 /= 8.0;
                }

                n5++;
                double d7 = f2;
                double d8 = f;
                d7 += d6 * 0.2;
                d7 = d7 * 8.5 / 8.0;
                double d9 = 8.5 + d7 * 4.0;

                for (int k = 0; k < 33; k++) {
                    double d10 = (k - d9) * 12.0 * 128.0 / 256.0 / d8;
                    if (d10 < 0.0) {
                        d10 *= 4.0;
                    }

                    double d11 = this.doubleArray3[n4] / 512.0;
                    double d12 = this.doubleArray4[n4] / 512.0;
                    double d13 = (this.doubleArray2[n4] / 10.0 + 1.0) / 2.0;
                    double d14 = MathHelper.denormalizeClamp(d11, d12, d13) - d10;
                    if (k > 29) {
                        double d15 = (k - 29) / 3.0F;
                        d14 = d14 * (1.0 - d15) + -10.0 * d15;
                    }

                    this.doubleArray[n4] = d14;
                    n4++;
                }
            }
        }
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
        BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(n3 + 16, n4 + 16);
        this.rand.setSeed(this.worldObj.getSeed());
        long l = this.rand.nextLong() / 2L * 2L + 1L;
        long l2 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(n * l + n2 * l2 ^ this.worldObj.getSeed());
        boolean flag = false;
        if (this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generateStructuresInChunk(this.worldObj, this.rand, n, n2);
            flag = this.villageGenerator.generateStructuresInChunk(this.worldObj, this.rand, n, n2);
            this.strongholdGenerator.generateStructuresInChunk(this.worldObj, this.rand, n, n2);
            this.scatteredFeatureGenerator.generateStructuresInChunk(this.worldObj, this.rand, n, n2);
        }

        if (biomeGenForCoords != BiomeGenBase.desert && biomeGenForCoords != BiomeGenBase.desertHills && !flag && this.rand.nextInt(4) == 0) {
            int n5 = n3 + this.rand.nextInt(16) + 8;
            int nextInt = this.rand.nextInt(256);
            int n6 = n4 + this.rand.nextInt(16) + 8;
            new WorldGenLakes(Blocks.water).generate(this.worldObj, this.rand, n5, nextInt, n6);
        }

        if (!flag && this.rand.nextInt(8) == 0) {
            int n5 = n3 + this.rand.nextInt(16) + 8;
            int nextInt = this.rand.nextInt(this.rand.nextInt(248) + 8);
            int n6 = n4 + this.rand.nextInt(16) + 8;
            if (nextInt < 63 || this.rand.nextInt(10) == 0) {
                new WorldGenLakes(Blocks.lava).generate(this.worldObj, this.rand, n5, nextInt, n6);
            }
        }

        for (int i = 0; i < 8; i++) {
            int n5 = n3 + this.rand.nextInt(16) + 8;
            int nextInt = this.rand.nextInt(256);
            int n6 = n4 + this.rand.nextInt(16) + 8;
            new WorldGenDungeons().generate(this.worldObj, this.rand, n5, nextInt, n6);
        }

        biomeGenForCoords.decorate(this.worldObj, this.rand, n3, n4);
        SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomeGenForCoords, n3 + 8, n4 + 8, 16, 16, this.rand);
        n3 += 8;
        n4 += 8;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int precipitationHeight = this.worldObj.getPrecipitationHeight(n3 + i, n4 + j);
                if (this.worldObj.isBlockFreezable(i + n3, precipitationHeight - 1, j + n4)) {
                    this.worldObj.setBlock(i + n3, precipitationHeight - 1, j + n4, Blocks.ice, 0, 2);
                }

                if (this.worldObj.canSnowAt(i + n3, precipitationHeight, j + n4, true)) {
                    this.worldObj.setBlock(i + n3, precipitationHeight, j + n4, Blocks.snow_layer, 0, 2);
                }
            }
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
        return "RandomLevelSource";
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType enumCreatureType, int n, int n2, int n3) {
        BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(n, n3);
        return enumCreatureType == EnumCreatureType.monster && this.scatteredFeatureGenerator.check3(n, n2, n3)
            ? this.scatteredFeatureGenerator.getScatteredFeatureSpawnList()
            : biomeGenForCoords.getSpawnableList(enumCreatureType);
    }

    @Override
    public ChunkPosition findClosestStructure(World world, String str, int n, int n2, int n3) {
        return "Stronghold".equals(str) && this.strongholdGenerator != null ? this.strongholdGenerator.getNearestInstance(world, n, n2, n3) : null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int n, int n2) {
        if (this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generate(this, this.worldObj, n, n2, null);
            this.villageGenerator.generate(this, this.worldObj, n, n2, null);
            this.strongholdGenerator.generate(this, this.worldObj, n, n2, null);
            this.scatteredFeatureGenerator.generate(this, this.worldObj, n, n2, null);
        }
    }
}
