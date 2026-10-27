package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.util.BlockPos;
import net.minecraft.block.BlockFalling;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.util.MathHelper;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import java.util.Random;
import net.minecraft.world.WorldType;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderGenerate implements IChunkProvider
{
    public Block oceanBlockTmpl;
    public double[] stoneNoise;
    public MapGenBase caveGenerator;
    public MapGenStronghold strongholdGenerator;
    public MapGenVillage villageGenerator;
    public MapGenMineshaft mineshaftGenerator;
    public MapGenScatteredFeature scatteredFeatureGenerator;
    public MapGenBase ravineGenerator;
    public StructureOceanMonument oceanMonumentGenerator;
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
    public float[] parabolicField;
    public ChunkProviderSettings settings;
    public BiomeGenBase[] biomesForGeneration;
    public double[] mainNoiseArray;
    public double[] lowerLimitNoiseArray;
    public double[] upperLimitNoiseArray;
    public double[] depthNoiseArray;
    
    public ChunkProviderGenerate(final World worldIn, final long seed, final boolean generateStructures, final String structuresJson) {
        this.oceanBlockTmpl = Blocks.water;
        this.stoneNoise = new double[256];
        this.caveGenerator = new MapGenCaves();
        this.strongholdGenerator = new MapGenStronghold();
        this.villageGenerator = new MapGenVillage();
        this.mineshaftGenerator = new MapGenMineshaft();
        this.scatteredFeatureGenerator = new MapGenScatteredFeature();
        this.ravineGenerator = new MapGenRavine();
        this.oceanMonumentGenerator = new StructureOceanMonument();
        this.worldObj = worldIn;
        this.mapFeaturesEnabled = generateStructures;
        this.type = worldIn.getWorldInfo().getTerrainType();
        this.rand = new Random(seed);
        this.noiseGeneratorOctaves = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGeneratorOctaves2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGeneratorOctaves3 = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseGeneratorPerlin = new NoiseGeneratorPerlin(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.doubleArray = new double[825];
        this.parabolicField = new float[25];
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                this.parabolicField[i + 2 + (j + 2) * 5] = 10.0f / MathHelper.sqrt_float(i * i + j * j + 0.2f);
            }
        }
        if (structuresJson != null) {
            this.settings = ChunkProviderSettings.Factory.jsonToFactory(structuresJson).build();
            this.oceanBlockTmpl = (this.settings.useLavaOceans ? Blocks.lava : Blocks.water);
            worldIn.setSeaLevel(this.settings.seaLevel);
        }
    }
    
    public void setBlocksInChunk(final int x, final int z, final ChunkPrimer primer) {
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
        this.process(x * 4, 0, z * 4);
        for (int i = 0; i < 4; ++i) {
            final int n = i * 5;
            final int n2 = (i + 1) * 5;
            for (int j = 0; j < 4; ++j) {
                final int n3 = (n + j) * 33;
                final int n4 = (n + j + 1) * 33;
                final int n5 = (n2 + j) * 33;
                final int n6 = (n2 + j + 1) * 33;
                for (int k = 0; k < 32; ++k) {
                    final double n7 = 0.125;
                    double n8 = this.doubleArray[n3 + k];
                    double n9 = this.doubleArray[n4 + k];
                    double n10 = this.doubleArray[n5 + k];
                    double n11 = this.doubleArray[n6 + k];
                    final double n12 = (this.doubleArray[n3 + k + 1] - n8) * n7;
                    final double n13 = (this.doubleArray[n4 + k + 1] - n9) * n7;
                    final double n14 = (this.doubleArray[n5 + k + 1] - n10) * n7;
                    final double n15 = (this.doubleArray[n6 + k + 1] - n11) * n7;
                    for (int l = 0; l < 8; ++l) {
                        final double n16 = 0.25;
                        double n17 = n8;
                        double n18 = n9;
                        final double n19 = (n10 - n8) * n16;
                        final double n20 = (n11 - n9) * n16;
                        for (int n21 = 0; n21 < 4; ++n21) {
                            final double n22 = (n18 - n17) * 0.25;
                            double n23 = n17 - n22;
                            for (int n24 = 0; n24 < 4; ++n24) {
                                if ((n23 += n22) > 0.0) {
                                    primer.setBlockState(i * 4 + n21, k * 8 + l, j * 4 + n24, Blocks.stone.getDefaultState());
                                }
                                else if (k * 8 + l < this.settings.seaLevel) {
                                    primer.setBlockState(i * 4 + n21, k * 8 + l, j * 4 + n24, this.oceanBlockTmpl.getDefaultState());
                                }
                            }
                            n17 += n19;
                            n18 += n20;
                        }
                        n8 += n12;
                        n9 += n13;
                        n10 += n14;
                        n11 += n15;
                    }
                }
            }
        }
    }
    
    public void replaceBlocksForBiome(final int x, final int z, final ChunkPrimer primer, final BiomeGenBase[] biomeGens) {
        final double n = 0.03125;
        this.stoneNoise = this.noiseGeneratorPerlin.getDoubleArray(this.stoneNoise, x * 16, z * 16, 16, 16, n * 2.0, n * 2.0, 1.0);
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                biomeGens[j + i * 16].genTerrainBlocks(this.worldObj, this.rand, primer, x * 16 + i, z * 16 + j, this.stoneNoise[j + i * 16]);
            }
        }
    }
    
    @Override
    public Chunk provideChunk(final int x, final int z) {
        this.rand.setSeed(x * 341873128712L + z * 132897987541L);
        final ChunkPrimer chunkPrimer = new ChunkPrimer();
        this.setBlocksInChunk(x, z, chunkPrimer);
        this.replaceBlocksForBiome(x, z, chunkPrimer, this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16));
        if (this.settings.useCaves) {
            this.caveGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
        }
        if (this.settings.useRavines) {
            this.ravineGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
        }
        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
        }
        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            this.villageGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
        }
        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.strongholdGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
        }
        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.scatteredFeatureGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
        }
        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.oceanMonumentGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
        }
        final Chunk chunk = new Chunk(this.worldObj, chunkPrimer, x, z);
        final byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; ++i) {
            biomeArray[i] = (byte)this.biomesForGeneration[i].biomeID;
        }
        chunk.generateSkylightMap();
        return chunk;
    }
    
    public void process(int x, final int y, int z) {
        this.depthNoiseArray = this.noiseGen6.generateNoiseOctaves(this.depthNoiseArray, x, z, 5, 5, this.settings.depthNoiseScaleX, this.settings.depthNoiseScaleZ, this.settings.depthNoiseScaleExponent);
        final float coordinateScale = this.settings.coordinateScale;
        final float heightScale = this.settings.heightScale;
        this.mainNoiseArray = this.noiseGeneratorOctaves3.generateNoiseOctaves(this.mainNoiseArray, x, y, z, 5, 33, 5, coordinateScale / this.settings.mainNoiseScaleX, heightScale / this.settings.mainNoiseScaleY, coordinateScale / this.settings.mainNoiseScaleZ);
        this.lowerLimitNoiseArray = this.noiseGeneratorOctaves.generateNoiseOctaves(this.lowerLimitNoiseArray, x, y, z, 5, 33, 5, coordinateScale, heightScale, coordinateScale);
        this.upperLimitNoiseArray = this.noiseGeneratorOctaves2.generateNoiseOctaves(this.upperLimitNoiseArray, x, y, z, 5, 33, 5, coordinateScale, heightScale, coordinateScale);
        z = 0;
        x = 0;
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                float n3 = 0.0f;
                float n4 = 0.0f;
                float n5 = 0.0f;
                final int n6 = 2;
                final BiomeGenBase biomeGenBase = this.biomesForGeneration[i + 2 + (j + 2) * 10];
                for (int k = -n6; k <= n6; ++k) {
                    for (int l = -n6; l <= n6; ++l) {
                        final BiomeGenBase biomeGenBase2 = this.biomesForGeneration[i + k + 2 + (j + l + 2) * 10];
                        float n7 = this.settings.biomeDepthOffSet + biomeGenBase2.minHeight * this.settings.biomeDepthWeight;
                        float n8 = this.settings.biomeScaleOffset + biomeGenBase2.maxHeight * this.settings.biomeScaleWeight;
                        if (this.type == WorldType.AMPLIFIED && n7 > 0.0f) {
                            n7 = 1.0f + n7 * 2.0f;
                            n8 = 1.0f + n8 * 4.0f;
                        }
                        float n9 = this.parabolicField[k + 2 + (l + 2) * 5] / (n7 + 2.0f);
                        if (biomeGenBase2.minHeight > biomeGenBase.minHeight) {
                            n9 /= 2.0f;
                        }
                        n3 += n8 * n9;
                        n4 += n7 * n9;
                        n5 += n9;
                    }
                }
                final float n10 = n3 / n5;
                final float n11 = n4 / n5;
                final float n12 = n10 * 0.9f + 0.1f;
                final float n13 = (n11 * 4.0f - 1.0f) / 8.0f;
                double n14 = this.depthNoiseArray[n2] / 8000.0;
                if (n14 < 0.0) {
                    n14 = -n14 * 0.3;
                }
                double n15 = n14 * 3.0 - 2.0;
                double n17;
                if (n15 < 0.0) {
                    double n16 = n15 / 2.0;
                    if (n16 < -1.0) {
                        n16 = -1.0;
                    }
                    n17 = n16 / 1.4 / 2.0;
                }
                else {
                    if (n15 > 1.0) {
                        n15 = 1.0;
                    }
                    n17 = n15 / 8.0;
                }
                ++n2;
                final double n18 = n13;
                final double n19 = n12;
                final double n20 = this.settings.baseSize + (n18 + n17 * 0.2) * this.settings.baseSize / 8.0 * 4.0;
                for (int n21 = 0; n21 < 33; ++n21) {
                    double n22 = (n21 - n20) * this.settings.stretchY * 128.0 / 256.0 / n19;
                    if (n22 < 0.0) {
                        n22 *= 4.0;
                    }
                    double n23 = MathHelper.denormalizeClamp(this.lowerLimitNoiseArray[n] / this.settings.lowerLimitScale, this.upperLimitNoiseArray[n] / this.settings.upperLimitScale, (this.mainNoiseArray[n] / 10.0 + 1.0) / 2.0) - n22;
                    if (n21 > 29) {
                        final double n24 = (n21 - 29) / 3.0f;
                        n23 = n23 * (1.0 - n24) + -10.0 * n24;
                    }
                    this.doubleArray[n] = n23;
                    ++n;
                }
            }
        }
    }
    
    @Override
    public boolean chunkExists(final int x, final int z) {
        return true;
    }
    
    @Override
    public void populate(final IChunkProvider chunkProvider, final int x, final int z) {
        BlockFalling.fallInstantly = true;
        final int n = x * 16;
        final int n2 = z * 16;
        final BlockPos blockPos = new BlockPos(n, 0, n2);
        final BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(blockPos.add(16, 0, 16));
        this.rand.setSeed(this.worldObj.getSeed());
        this.rand.setSeed(x * (this.rand.nextLong() / 2L * 2L + 1L) + z * (this.rand.nextLong() / 2L * 2L + 1L) ^ this.worldObj.getSeed());
        boolean generateStructure = false;
        final ChunkCoordIntPair chunkCoord = new ChunkCoordIntPair(x, z);
        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, chunkCoord);
        }
        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            generateStructure = this.villageGenerator.generateStructure(this.worldObj, this.rand, chunkCoord);
        }
        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.strongholdGenerator.generateStructure(this.worldObj, this.rand, chunkCoord);
        }
        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, chunkCoord);
        }
        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, chunkCoord);
        }
        if (biomeGenForCoords != BiomeGenBase.desert && biomeGenForCoords != BiomeGenBase.desertHills && this.settings.useWaterLakes && !generateStructure && this.rand.nextInt(this.settings.waterLakeChance) == 0) {
            new WorldGenLakes(Blocks.water).generate(this.worldObj, this.rand, blockPos.add(this.rand.nextInt(16) + 8, this.rand.nextInt(256), this.rand.nextInt(16) + 8));
        }
        if (!generateStructure && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes) {
            final int x2 = this.rand.nextInt(16) + 8;
            final int nextInt = this.rand.nextInt(this.rand.nextInt(248) + 8);
            final int z2 = this.rand.nextInt(16) + 8;
            if (nextInt < this.worldObj.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0) {
                new WorldGenLakes(Blocks.lava).generate(this.worldObj, this.rand, blockPos.add(x2, nextInt, z2));
            }
        }
        if (this.settings.useDungeons) {
            for (int i = 0; i < this.settings.dungeonChance; ++i) {
                new WorldGenDungeons().generate(this.worldObj, this.rand, blockPos.add(this.rand.nextInt(16) + 8, this.rand.nextInt(256), this.rand.nextInt(16) + 8));
            }
        }
        biomeGenForCoords.decorate(this.worldObj, this.rand, new BlockPos(n, 0, n2));
        SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomeGenForCoords, n + 8, n2 + 8, 16, 16, this.rand);
        final BlockPos add = blockPos.add(8, 0, 8);
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                final BlockPos precipitationHeight = this.worldObj.getPrecipitationHeight(add.add(j, 0, k));
                final BlockPos down = precipitationHeight.down();
                if (this.worldObj.canBlockFreezeWater(down)) {
                    this.worldObj.setBlockState(down, Blocks.ice.getDefaultState(), 2);
                }
                if (this.worldObj.canSnowAt(precipitationHeight, true)) {
                    this.worldObj.setBlockState(precipitationHeight, Blocks.snow_layer.getDefaultState(), 2);
                }
            }
        }
        BlockFalling.fallInstantly = false;
    }
    
    @Override
    public boolean populateChunk(final IChunkProvider chunkProvider, final Chunk chunkIn, final int x, final int z) {
        boolean b = false;
        if (this.settings.useMonuments && this.mapFeaturesEnabled && chunkIn.getInhabitedTime() < 3600L) {
            b |= this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, new ChunkCoordIntPair(x, z));
        }
        return b;
    }
    
    @Override
    public boolean saveChunks(final boolean saveAllChunks, final IProgressUpdate progressCallback) {
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
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(final EnumCreatureType creatureType, final BlockPos pos) {
        final BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(pos);
        if (this.mapFeaturesEnabled) {
            if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.isSwampHut(pos)) {
                return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
            }
            if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.isPositionInStructure(this.worldObj, pos)) {
                return this.oceanMonumentGenerator.getScatteredFeatureSpawnList();
            }
        }
        return biomeGenForCoords.getSpawnableList(creatureType);
    }
    
    @Override
    public BlockPos getStrongholdGen(final World worldIn, final String structureName, final BlockPos position) {
        return ("Stronghold".equals(structureName) && this.strongholdGenerator != null) ? this.strongholdGenerator.getClosestStrongholdPos(worldIn, position) : null;
    }
    
    @Override
    public int getLoadedChunkCount() {
        return 0;
    }
    
    @Override
    public void recreateStructures(final Chunk chunkIn, final int x, final int z) {
        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generate(this, this.worldObj, x, z, null);
        }
        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            this.villageGenerator.generate(this, this.worldObj, x, z, null);
        }
        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.strongholdGenerator.generate(this, this.worldObj, x, z, null);
        }
        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.scatteredFeatureGenerator.generate(this, this.worldObj, x, z, null);
        }
        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.oceanMonumentGenerator.generate(this, this.worldObj, x, z, null);
        }
    }
    
    @Override
    public Chunk provideChunk(final BlockPos blockPosIn) {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
