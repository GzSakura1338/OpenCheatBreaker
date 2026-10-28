package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.Height;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenHills extends BiomeGenBase {
    public WorldGenerator theWorldGenerator = new WorldGenMinable(Blocks.monster_egg, 8);
    public WorldGenTaiga2 worldGenTaiga2 = new WorldGenTaiga2(false);
    public int intValue5 = 0;
    public int intValue2 = 1;
    public int intValue3 = 2;
    public int intValue4 = this.intValue5;

    public BiomeGenHills(int n, boolean flag) {
        super(n);
        if (flag) {
            this.theBiomeDecorator.treesPerChunk = 3;
            this.intValue4 = this.intValue2;
        }
    }

    @Override
    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
        return random.nextInt(3) > 0 ? this.worldGenTaiga2 : super.findWorldGenAbstractTree(random);
    }

    @Override
    public void decorate(World world, Random random, int n, int n2) {
        super.decorate(world, random, n, n2);
        int n3 = 3 + random.nextInt(6);

        for (int i = 0; i < n3; i++) {
            int n4 = n + random.nextInt(16);
            int n5 = random.nextInt(28) + 4;
            int n6 = n2 + random.nextInt(16);
            if (world.getBlock(n4, n5, n6) == Blocks.stone) {
                world.setBlock(n4, n5, n6, Blocks.emerald_ore, 0, 2);
            }
        }

        for (int i = 0; i < 7; i++) {
            int n4 = n + random.nextInt(16);
            int nextInt = random.nextInt(64);
            int n5 = n2 + random.nextInt(16);
            this.theWorldGenerator.generate(world, random, n4, nextInt, n5);
        }
    }

    @Override
    public void genTerrainBlocks(World world, Random random, Block[] blocks, byte[] bytes, int n, int n2, double d) {
        this.topBlock = Blocks.grass;
        this.topBlockMetadata = 0;
        this.fillerBlock = Blocks.dirt;
        if ((d < -1.0 || d > 2.0) && this.intValue4 == this.intValue3) {
            this.topBlock = Blocks.gravel;
            this.fillerBlock = Blocks.gravel;
        } else if (d > 1.0 && this.intValue4 != this.intValue2) {
            this.topBlock = Blocks.stone;
            this.fillerBlock = Blocks.stone;
        }

        this.genBiomeTerrain(world, random, blocks, bytes, n, n2, d);
    }

    public BiomeGenHills mutateHills(BiomeGenBase biomeGenBase) {
        this.intValue4 = this.intValue3;
        this.findBiomeGenBase(biomeGenBase.color, true);
        this.setBiomeName(biomeGenBase.biomeName + " M");
        this.setHeight(new Height(biomeGenBase.minHeight, biomeGenBase.maxHeight));
        this.setTemperatureRainfall(biomeGenBase.temperature, biomeGenBase.rainfall);
        return this;
    }

    @Override
    public BiomeGenBase createMutation() {
        return new BiomeGenHills(this.biomeID + 128, false).mutateHills(this);
    }
}
