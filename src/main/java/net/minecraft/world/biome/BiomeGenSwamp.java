package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenSwamp extends BiomeGenBase {
    public BiomeGenSwamp(int n) {
        super(n);
        this.theBiomeDecorator.treesPerChunk = 2;
        this.theBiomeDecorator.flowersPerChunk = 1;
        this.theBiomeDecorator.deadBushPerChunk = 1;
        this.theBiomeDecorator.mushroomsPerChunk = 8;
        this.theBiomeDecorator.reedsPerChunk = 10;
        this.theBiomeDecorator.clayPerChunk = 1;
        this.theBiomeDecorator.waterlilyPerChunk = 4;
        this.theBiomeDecorator.sandPerChunk2 = 0;
        this.theBiomeDecorator.sandPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 5;
        this.waterColorMultiplier = 14745518;
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 1, 1, 1));
    }

    @Override
    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
        return this.worldGeneratorSwamp;
    }

    @Override
    public int getBiomeGrassColor(int n, int n2, int n3) {
        double noise = plantNoise.noise(n * 0.0225, n3 * 0.0225);
        return noise < -0.1 ? 5011004 : 6975545;
    }

    @Override
    public int getBiomeFoliageColor(int n, int n2, int n3) {
        return 6975545;
    }

    @Override
    public String formatText(Random random, int n, int n2, int n3) {
        return BlockFlower.stringArray[1];
    }

    @Override
    public void genTerrainBlocks(World world, Random random, Block[] blocks, byte[] bytes, int n, int n2, double d) {
        double noise = plantNoise.noise(n * 0.25, n2 * 0.25);
        if (noise > 0.0) {
            int n3 = n & 15;
            int n4 = n2 & 15;
            int n5 = blocks.length / 256;

            for (int i = 255; i >= 0; i--) {
                int n6 = (n4 * 16 + n3) * n5 + i;
                if (blocks[n6] == null || blocks[n6].getMaterial() != Material.air) {
                    if (i == 62 && blocks[n6] != Blocks.water) {
                        blocks[n6] = Blocks.water;
                        if (noise < 0.12) {
                            blocks[n6 + 1] = Blocks.waterlily;
                        }
                    }
                    break;
                }
            }
        }

        this.genBiomeTerrain(world, random, blocks, bytes, n, n2, d);
    }
}
