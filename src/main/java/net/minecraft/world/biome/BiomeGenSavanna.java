package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;

public class BiomeGenSavanna extends BiomeGenBase {
    public static WorldGenSavannaTree worldGenSavannaTree = new WorldGenSavannaTree(false);

    public BiomeGenSavanna(int n) {
        super(n);
        this.spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 1, 2, 6));
        this.theBiomeDecorator.treesPerChunk = 1;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.grassPerChunk = 20;
    }

    @Override
    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
        return random.nextInt(5) > 0 ? worldGenSavannaTree : this.worldGeneratorTrees;
    }

    @Override
    public BiomeGenBase createMutation() {
        BiomeGenSavanna.Mutated mutated = new BiomeGenSavanna.Mutated(this.biomeID + 128, this);
        mutated.temperature = (this.temperature + 1.0F) * 0.5F;
        mutated.minHeight = this.minHeight * 0.5F + 0.3F;
        mutated.maxHeight = this.maxHeight * 0.5F + 1.2F;
        return mutated;
    }

    @Override
    public void decorate(World world, Random random, int n, int n2) {
        genTallFlowers.setIntValue(2);

        for (int i = 0; i < 7; i++) {
            int n3 = n + random.nextInt(16) + 8;
            int n4 = n2 + random.nextInt(16) + 8;
            int nextInt = random.nextInt(world.getHeightValue(n3, n4) + 32);
            genTallFlowers.generate(world, random, n3, nextInt, n4);
        }

        super.decorate(world, random, n, n2);
    }

    public static class Mutated extends BiomeGenMutated {
        public Mutated(int n, BiomeGenBase biomeGenBase) {
            super(n, biomeGenBase);
            this.theBiomeDecorator.treesPerChunk = 2;
            this.theBiomeDecorator.flowersPerChunk = 2;
            this.theBiomeDecorator.grassPerChunk = 5;
        }

        @Override
        public void genTerrainBlocks(World world, Random random, Block[] blocks, byte[] bytes, int n, int n2, double d) {
            this.topBlock = Blocks.grass;
            this.topBlockMetadata = 0;
            this.fillerBlock = Blocks.dirt;
            if (d > 1.75) {
                this.topBlock = Blocks.stone;
                this.fillerBlock = Blocks.stone;
            } else if (d > -0.5) {
                this.topBlock = Blocks.dirt;
                this.topBlockMetadata = 1;
            }

            this.genBiomeTerrain(world, random, blocks, bytes, n, n2, d);
        }

        @Override
        public void decorate(World world, Random random, int n, int n2) {
            this.theBiomeDecorator.decorateChunk(world, random, this, n, n2);
        }
    }
}
