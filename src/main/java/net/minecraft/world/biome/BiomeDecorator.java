package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeDecorator {
    public World currentWorld;
    public Random randomGenerator;
    public int chunk_X;
    public int chunk_Z;
    public WorldGenerator clayGen = new WorldGenClay(4);
    public WorldGenerator sandGen = new WorldGenSand(Blocks.sand, 7);
    public WorldGenerator gravelAsSandGen = new WorldGenSand(Blocks.gravel, 6);
    public WorldGenerator dirtGen = new WorldGenMinable(Blocks.dirt, 32);
    public WorldGenerator gravelGen = new WorldGenMinable(Blocks.gravel, 32);
    public WorldGenerator coalGen = new WorldGenMinable(Blocks.coal_ore, 16);
    public WorldGenerator ironGen = new WorldGenMinable(Blocks.iron_ore, 8);
    public WorldGenerator goldGen = new WorldGenMinable(Blocks.gold_ore, 8);
    public WorldGenerator redstoneGen = new WorldGenMinable(Blocks.redstone_ore, 7);
    public WorldGenerator diamondGen = new WorldGenMinable(Blocks.diamond_ore, 7);
    public WorldGenerator lapisGen = new WorldGenMinable(Blocks.lapis_ore, 6);
    public WorldGenFlowers yellowFlowerGen = new WorldGenFlowers(Blocks.yellow_flower);
    public WorldGenerator mushroomBrownGen = new WorldGenFlowers(Blocks.brown_mushroom);
    public WorldGenerator mushroomRedGen = new WorldGenFlowers(Blocks.red_mushroom);
    public WorldGenerator bigMushroomGen = new WorldGenBigMushroom();
    public WorldGenerator reedGen = new WorldGenReed();
    public WorldGenerator cactusGen = new WorldGenCactus();
    public WorldGenerator waterlilyGen = new WorldGenWaterlily();
    public int waterlilyPerChunk;
    public int treesPerChunk;
    public int flowersPerChunk = 2;
    public int grassPerChunk = 1;
    public int deadBushPerChunk;
    public int mushroomsPerChunk;
    public int reedsPerChunk;
    public int cactiPerChunk;
    public int sandPerChunk = 1;
    public int sandPerChunk2 = 3;
    public int clayPerChunk = 1;
    public int bigMushroomsPerChunk;
    public boolean generateLakes = true;

    public void decorateChunk(World world, Random random, BiomeGenBase biomeGenBase, int n, int n2) {
        if (this.currentWorld != null) {
            throw new RuntimeException("Already decorating!!");
        }

        this.currentWorld = world;
        this.randomGenerator = random;
        this.chunk_X = n;
        this.chunk_Z = n2;
        this.genDecorations(biomeGenBase);
        this.currentWorld = null;
        this.randomGenerator = null;
    }

    public void genDecorations(BiomeGenBase biomeGenBase) {
        this.generateOres();

        for (int i = 0; i < this.sandPerChunk2; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.sandGen.generate(this.currentWorld, this.randomGenerator, n, this.currentWorld.getTopSolidOrLiquidBlock(n, n2), n2);
        }

        for (int i = 0; i < this.clayPerChunk; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.clayGen.generate(this.currentWorld, this.randomGenerator, n, this.currentWorld.getTopSolidOrLiquidBlock(n, n2), n2);
        }

        for (int i = 0; i < this.sandPerChunk; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.gravelAsSandGen.generate(this.currentWorld, this.randomGenerator, n, this.currentWorld.getTopSolidOrLiquidBlock(n, n2), n2);
        }

        int treesPerChunk = this.treesPerChunk;
        if (this.randomGenerator.nextInt(10) == 0) {
            treesPerChunk++;
        }

        for (int i = 0; i < treesPerChunk; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int heightValue = this.currentWorld.getHeightValue(n, n2);
            WorldGenAbstractTree findWorldGenAbstractTree = biomeGenBase.findWorldGenAbstractTree(this.randomGenerator);
            findWorldGenAbstractTree.setScale(1.0, 1.0, 1.0);
            if (findWorldGenAbstractTree.generate(this.currentWorld, this.randomGenerator, n, heightValue, n2)) {
                findWorldGenAbstractTree.handleWorld(this.currentWorld, this.randomGenerator, n, heightValue, n2);
            }
        }

        for (int i = 0; i < this.bigMushroomsPerChunk; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator, n, this.currentWorld.getHeightValue(n, n2), n2);
        }

        for (int i = 0; i < this.flowersPerChunk; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) + 32);
            String formatText = biomeGenBase.formatText(this.randomGenerator, n, nextInt, n2);
            BlockFlower findBlockFlower = BlockFlower.findBlockFlower(formatText);
            if (findBlockFlower.getMaterial() != Material.air) {
                this.yellowFlowerGen.handleBlock(findBlockFlower, BlockFlower.computeInt(formatText));
                this.yellowFlowerGen.generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
            }
        }

        for (int i = 0; i < this.grassPerChunk; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) * 2);
            WorldGenerator randomWorldGenForGrass = biomeGenBase.getRandomWorldGenForGrass(this.randomGenerator);
            randomWorldGenForGrass.generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
        }

        for (int i = 0; i < this.deadBushPerChunk; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) * 2);
            new WorldGenDeadBush(Blocks.deadbush).generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
        }

        for (int i = 0; i < this.waterlilyPerChunk; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) * 2);

            while (nextInt > 0 && this.currentWorld.isAirBlock(n, nextInt - 1, n2)) {
                nextInt--;
            }

            this.waterlilyGen.generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
        }

        for (int i = 0; i < this.mushroomsPerChunk; i++) {
            if (this.randomGenerator.nextInt(4) == 0) {
                int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                int heightValue = this.currentWorld.getHeightValue(n, n2);
                this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, n, heightValue, n2);
            }

            if (this.randomGenerator.nextInt(8) == 0) {
                int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) * 2);
                this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
            }
        }

        if (this.randomGenerator.nextInt(4) == 0) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) * 2);
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
        }

        if (this.randomGenerator.nextInt(8) == 0) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) * 2);
            this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
        }

        for (int i = 0; i < this.reedsPerChunk; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) * 2);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
        }

        for (int i = 0; i < 10; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) * 2);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
        }

        if (this.randomGenerator.nextInt(32) == 0) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) * 2);
            new WorldGenPumpkin().generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
        }

        for (int i = 0; i < this.cactiPerChunk; i++) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(n, n2) * 2);
            this.cactusGen.generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
        }

        if (this.generateLakes) {
            for (int i = 0; i < 50; i++) {
                int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                int nextInt = this.randomGenerator.nextInt(this.randomGenerator.nextInt(248) + 8);
                int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                new WorldGenLiquids(Blocks.flowing_water).generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
            }

            for (int i = 0; i < 20; i++) {
                int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                int nextInt = this.randomGenerator.nextInt(this.randomGenerator.nextInt(this.randomGenerator.nextInt(240) + 8) + 8);
                int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                new WorldGenLiquids(Blocks.flowing_lava).generate(this.currentWorld, this.randomGenerator, n, nextInt, n2);
            }
        }
    }

    public void genStandardOre1(int n, WorldGenerator worldGenerator, int n2, int n3) {
        for (int i = 0; i < n; i++) {
            int n4 = this.chunk_X + this.randomGenerator.nextInt(16);
            int n5 = this.randomGenerator.nextInt(n3 - n2) + n2;
            int n6 = this.chunk_Z + this.randomGenerator.nextInt(16);
            worldGenerator.generate(this.currentWorld, this.randomGenerator, n4, n5, n6);
        }
    }

    public void genStandardOre2(int n, WorldGenerator worldGenerator, int n2, int n3) {
        for (int i = 0; i < n; i++) {
            int n4 = this.chunk_X + this.randomGenerator.nextInt(16);
            int n5 = this.randomGenerator.nextInt(n3) + this.randomGenerator.nextInt(n3) + (n2 - n3);
            int n6 = this.chunk_Z + this.randomGenerator.nextInt(16);
            worldGenerator.generate(this.currentWorld, this.randomGenerator, n4, n5, n6);
        }
    }

    public void generateOres() {
        this.genStandardOre1(20, this.dirtGen, 0, 256);
        this.genStandardOre1(10, this.gravelGen, 0, 256);
        this.genStandardOre1(20, this.coalGen, 0, 128);
        this.genStandardOre1(20, this.ironGen, 0, 64);
        this.genStandardOre1(2, this.goldGen, 0, 32);
        this.genStandardOre1(8, this.redstoneGen, 0, 16);
        this.genStandardOre1(1, this.diamondGen, 0, 16);
        this.genStandardOre2(1, this.lapisGen, 16, 16);
    }
}
