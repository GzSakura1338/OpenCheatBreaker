package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenJungle extends BiomeGenBase {
    public boolean flag;

    public BiomeGenJungle(int n, boolean flag) {
        super(n);
        this.flag = flag;
        if (flag) {
            this.theBiomeDecorator.treesPerChunk = 2;
        } else {
            this.theBiomeDecorator.treesPerChunk = 50;
        }

        this.theBiomeDecorator.grassPerChunk = 25;
        this.theBiomeDecorator.flowersPerChunk = 4;
        if (!flag) {
            this.spawnableMonsterList.add(new SpawnListEntry(EntityOcelot.class, 2, 1, 1));
        }

        this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 10, 4, 4));
    }

    @Override
    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
        return random.nextInt(10) == 0
            ? this.worldGeneratorBigTree
            : (
                random.nextInt(2) == 0
                    ? new WorldGenShrub(3, 0)
                    : (!this.flag && random.nextInt(3) == 0 ? new WorldGenMegaJungle(false, 10, 20, 3, 3) : new WorldGenTrees(false, 4 + random.nextInt(7), 3, 3, true))
            );
    }

    @Override
    public WorldGenerator getRandomWorldGenForGrass(Random random) {
        return random.nextInt(4) == 0 ? new WorldGenTallGrass(Blocks.tallgrass, 2) : new WorldGenTallGrass(Blocks.tallgrass, 1);
    }

    @Override
    public void decorate(World world, Random random, int n, int n2) {
        super.decorate(world, random, n, n2);
        int n3 = n + random.nextInt(16) + 8;
        int n4 = n2 + random.nextInt(16) + 8;
        int nextInt = random.nextInt(world.getHeightValue(n3, n4) * 2);
        new WorldGenMelon().generate(world, random, n3, nextInt, n4);
        WorldGenVines worldGenVines = new WorldGenVines();

        for (int i = 0; i < 50; i++) {
            nextInt = n + random.nextInt(16) + 8;
            short s = 128;
            int n5 = n2 + random.nextInt(16) + 8;
            worldGenVines.generate(world, random, nextInt, s, n5);
        }
    }
}
