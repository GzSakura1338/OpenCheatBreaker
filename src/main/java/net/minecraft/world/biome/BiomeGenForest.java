package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.Height;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;

public class BiomeGenForest extends BiomeGenBase {
    public int intValue2;
    public static WorldGenForest worldGenForest = new WorldGenForest(false, true);
    public static WorldGenForest worldGenForest2 = new WorldGenForest(false, false);
    public static WorldGenCanopyTree worldGenCanopyTree = new WorldGenCanopyTree(false);

    public BiomeGenForest(int n, int n2) {
        super(n);
        this.intValue2 = n2;
        this.theBiomeDecorator.treesPerChunk = 10;
        this.theBiomeDecorator.grassPerChunk = 2;
        if (this.intValue2 == 1) {
            this.theBiomeDecorator.treesPerChunk = 6;
            this.theBiomeDecorator.flowersPerChunk = 100;
            this.theBiomeDecorator.grassPerChunk = 1;
        }

        this.setFillerBlockMetadata(5159473);
        this.setTemperatureRainfall(0.7F, 0.8F);
        if (this.intValue2 == 2) {
            this.intValue = 353825;
            this.color = 3175492;
            this.setTemperatureRainfall(0.6F, 0.6F);
        }

        if (this.intValue2 == 0) {
            this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 5, 4, 4));
        }

        if (this.intValue2 == 3) {
            this.theBiomeDecorator.treesPerChunk = -999;
        }
    }

    @Override
    public BiomeGenBase findBiomeGenBase(int n, boolean flag) {
        if (this.intValue2 == 2) {
            this.intValue = 353825;
            this.color = n;
            if (flag) {
                this.intValue = (this.intValue & 16711422) >> 1;
            }

            return this;
        } else {
            return super.findBiomeGenBase(n, flag);
        }
    }

    @Override
    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
        return this.intValue2 == 3 && random.nextInt(3) > 0
            ? worldGenCanopyTree
            : (this.intValue2 != 2 && random.nextInt(5) != 0 ? this.worldGeneratorTrees : worldGenForest2);
    }

    @Override
    public String formatText(Random random, int n, int n2, int n3) {
        if (this.intValue2 == 1) {
            double d = MathHelper.clamp_double((1.0 + plantNoise.noise(n / 48.0, n3 / 48.0)) / 2.0, 0.0, 0.9999);
            int n4 = (int)(d * BlockFlower.stringArray.length);
            if (n4 == 1) {
                n4 = 0;
            }

            return BlockFlower.stringArray[n4];
        } else {
            return super.formatText(random, n, n2, n3);
        }
    }

    @Override
    public void decorate(World world, Random random, int n, int n2) {
        if (this.intValue2 == 3) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int n3 = n + i * 4 + 1 + 8 + random.nextInt(3);
                    int n4 = n2 + j * 4 + 1 + 8 + random.nextInt(3);
                    int heightValue = world.getHeightValue(n3, n4);
                    if (random.nextInt(20) == 0) {
                        WorldGenBigMushroom worldGenBigMushroom = new WorldGenBigMushroom();
                        worldGenBigMushroom.generate(world, random, n3, heightValue, n4);
                    } else {
                        WorldGenAbstractTree findWorldGenAbstractTree = this.findWorldGenAbstractTree(random);
                        findWorldGenAbstractTree.setScale(1.0, 1.0, 1.0);
                        if (findWorldGenAbstractTree.generate(world, random, n3, heightValue, n4)) {
                            findWorldGenAbstractTree.handleWorld(world, random, n3, heightValue, n4);
                        }
                    }
                }
            }
        }

        int n3 = random.nextInt(5) - 3;
        if (this.intValue2 == 1) {
            n3 += 2;
        }

        for (int i = 0; i < n3; i++) {
            int nextInt = random.nextInt(3);
            if (nextInt == 0) {
                genTallFlowers.setIntValue(1);
            } else if (nextInt == 1) {
                genTallFlowers.setIntValue(4);
            } else if (nextInt == 2) {
                genTallFlowers.setIntValue(5);
            }

            for (int j = 0; j < 5; j++) {
                int n4 = n + random.nextInt(16) + 8;
                int n5 = n2 + random.nextInt(16) + 8;
                int nextInt2 = random.nextInt(world.getHeightValue(n4, n5) + 32);
                if (genTallFlowers.generate(world, random, n4, nextInt2, n5)) {
                    break;
                }
            }
        }

        super.decorate(world, random, n, n2);
    }

    @Override
    public int getBiomeGrassColor(int n, int n2, int n3) {
        int biomeGrassColor = super.getBiomeGrassColor(n, n2, n3);
        return this.intValue2 == 3 ? (biomeGrassColor & 16711422) + 2634762 >> 1 : biomeGrassColor;
    }

    @Override
    public BiomeGenBase createMutation() {
        if (this.biomeID == BiomeGenBase.forest.biomeID) {
            BiomeGenForest biomeGenForest = new BiomeGenForest(this.biomeID + 128, 1);
            biomeGenForest.setHeight(new Height(this.minHeight, this.maxHeight + 0.2F));
            biomeGenForest.setBiomeName("Flower Forest");
            biomeGenForest.findBiomeGenBase(6976549, true);
            biomeGenForest.setFillerBlockMetadata(8233509);
            return biomeGenForest;
        } else {
            return this.biomeID != BiomeGenBase.birchForest.biomeID && this.biomeID != BiomeGenBase.birchForestHills.biomeID
                ? new BiomeGenMutated(this.biomeID + 128, this) {
                    @Override
                    public void decorate(World world, Random random, int n, int n2) {
                        this.baseBiome.decorate(world, random, n, n2);
                    }
                }
                : new BiomeGenMutated(this.biomeID + 128, this) {
                    @Override
                    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
                        return random.nextBoolean() ? BiomeGenForest.worldGenForest : BiomeGenForest.worldGenForest2;
                    }
                };
        }
    }
}
