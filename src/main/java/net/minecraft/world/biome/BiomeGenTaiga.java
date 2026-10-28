package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.Height;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenTaiga extends BiomeGenBase {
    public static WorldGenTaiga1 worldGenTaiga1 = new WorldGenTaiga1();
    public static WorldGenTaiga2 worldGenTaiga2 = new WorldGenTaiga2(false);
    public static WorldGenMegaPineTree worldGenMegaPineTree = new WorldGenMegaPineTree(false, false);
    public int intValue2;
    public static WorldGenMegaPineTree worldGenMegaPineTree2 = new WorldGenMegaPineTree(false, true);
    public static WorldGenBlockBlob worldGenBlockBlob = new WorldGenBlockBlob(Blocks.mossy_cobblestone, 0);

    public BiomeGenTaiga(int n, int n2) {
        super(n);
        this.intValue2 = n2;
        this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 8, 4, 4));
        this.theBiomeDecorator.treesPerChunk = 10;
        if (n2 != 1 && n2 != 2) {
            this.theBiomeDecorator.grassPerChunk = 1;
            this.theBiomeDecorator.mushroomsPerChunk = 1;
        } else {
            this.theBiomeDecorator.grassPerChunk = 7;
            this.theBiomeDecorator.deadBushPerChunk = 1;
            this.theBiomeDecorator.mushroomsPerChunk = 3;
        }
    }

    @Override
    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
        return (this.intValue2 == 1 || this.intValue2 == 2) && random.nextInt(3) == 0
            ? (this.intValue2 != 2 && random.nextInt(13) != 0 ? worldGenMegaPineTree : worldGenMegaPineTree2)
            : (random.nextInt(3) == 0 ? worldGenTaiga1 : worldGenTaiga2);
    }

    @Override
    public WorldGenerator getRandomWorldGenForGrass(Random random) {
        return random.nextInt(5) > 0 ? new WorldGenTallGrass(Blocks.tallgrass, 2) : new WorldGenTallGrass(Blocks.tallgrass, 1);
    }

    @Override
    public void decorate(World world, Random random, int n, int n2) {
        if (this.intValue2 == 1 || this.intValue2 == 2) {
            int nextInt = random.nextInt(3);

            for (int i = 0; i < nextInt; i++) {
                int n3 = n + random.nextInt(16) + 8;
                int n4 = n2 + random.nextInt(16) + 8;
                int heightValue = world.getHeightValue(n3, n4);
                worldGenBlockBlob.generate(world, random, n3, heightValue, n4);
            }
        }

        genTallFlowers.setIntValue(3);

        for (int i = 0; i < 7; i++) {
            int n3 = n + random.nextInt(16) + 8;
            int n4 = n2 + random.nextInt(16) + 8;
            int nextInt = random.nextInt(world.getHeightValue(n3, n4) + 32);
            genTallFlowers.generate(world, random, n3, nextInt, n4);
        }

        super.decorate(world, random, n, n2);
    }

    @Override
    public void genTerrainBlocks(World world, Random random, Block[] blocks, byte[] bytes, int n, int n2, double d) {
        if (this.intValue2 == 1 || this.intValue2 == 2) {
            this.topBlock = Blocks.grass;
            this.topBlockMetadata = 0;
            this.fillerBlock = Blocks.dirt;
            if (d > 1.75) {
                this.topBlock = Blocks.dirt;
                this.topBlockMetadata = 1;
            } else if (d > -0.95) {
                this.topBlock = Blocks.dirt;
                this.topBlockMetadata = 2;
            }
        }

        this.genBiomeTerrain(world, random, blocks, bytes, n, n2, d);
    }

    @Override
    public BiomeGenBase createMutation() {
        return this.biomeID == BiomeGenBase.megaTaiga.biomeID
            ? new BiomeGenTaiga(this.biomeID + 128, 2)
                .findBiomeGenBase(5858897, true)
                .setBiomeName("Mega Spruce Taiga")
                .setFillerBlockMetadata(5159473)
                .setTemperatureRainfall(0.25F, 0.8F)
                .setHeight(new Height(this.minHeight, this.maxHeight))
            : super.createMutation();
    }
}
