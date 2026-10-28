package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.TempCategory;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMutated extends BiomeGenBase {
    public BiomeGenBase baseBiome;

    public BiomeGenMutated(int n, BiomeGenBase biomeGenBase) {
        super(n);
        this.baseBiome = biomeGenBase;
        this.findBiomeGenBase(biomeGenBase.color, true);
        this.biomeName = biomeGenBase.biomeName + " M";
        this.topBlock = biomeGenBase.topBlock;
        this.fillerBlock = biomeGenBase.fillerBlock;
        this.fillerBlockMetadata = biomeGenBase.fillerBlockMetadata;
        this.minHeight = biomeGenBase.minHeight;
        this.maxHeight = biomeGenBase.maxHeight;
        this.temperature = biomeGenBase.temperature;
        this.rainfall = biomeGenBase.rainfall;
        this.waterColorMultiplier = biomeGenBase.waterColorMultiplier;
        this.enableSnow = biomeGenBase.enableSnow;
        this.enableRain = biomeGenBase.enableRain;
        this.spawnableCreatureList = new ArrayList(biomeGenBase.spawnableCreatureList);
        this.spawnableMonsterList = new ArrayList(biomeGenBase.spawnableMonsterList);
        this.spawnableCaveCreatureList = new ArrayList(biomeGenBase.spawnableCaveCreatureList);
        this.spawnableWaterCreatureList = new ArrayList(biomeGenBase.spawnableWaterCreatureList);
        this.temperature = biomeGenBase.temperature;
        this.rainfall = biomeGenBase.rainfall;
        this.minHeight = biomeGenBase.minHeight + 0.1F;
        this.maxHeight = biomeGenBase.maxHeight + 0.2F;
    }

    @Override
    public void decorate(World world, Random random, int n, int n2) {
        this.baseBiome.theBiomeDecorator.decorateChunk(world, random, this, n, n2);
    }

    @Override
    public void genTerrainBlocks(World world, Random random, Block[] blocks, byte[] bytes, int n, int n2, double d) {
        this.baseBiome.genTerrainBlocks(world, random, blocks, bytes, n, n2, d);
    }

    @Override
    public float getSpawningChance() {
        return this.baseBiome.getSpawningChance();
    }

    @Override
    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
        return this.baseBiome.findWorldGenAbstractTree(random);
    }

    @Override
    public int getBiomeFoliageColor(int n, int n2, int n3) {
        return this.baseBiome.getBiomeFoliageColor(n, n2, n2);
    }

    @Override
    public int getBiomeGrassColor(int n, int n2, int n3) {
        return this.baseBiome.getBiomeGrassColor(n, n2, n2);
    }

    @Override
    public Class getBiomeClass() {
        return this.baseBiome.getBiomeClass();
    }

    @Override
    public boolean isEqualTo(BiomeGenBase biomeGenBase) {
        return this.baseBiome.isEqualTo(biomeGenBase);
    }

    @Override
    public TempCategory getTempCategory() {
        return this.baseBiome.getTempCategory();
    }
}
