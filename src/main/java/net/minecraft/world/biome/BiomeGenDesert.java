package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class BiomeGenDesert extends BiomeGenBase {
    public BiomeGenDesert(int n) {
        super(n);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.sand;
        this.fillerBlock = Blocks.sand;
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 2;
        this.theBiomeDecorator.reedsPerChunk = 50;
        this.theBiomeDecorator.cactiPerChunk = 10;
        this.spawnableCreatureList.clear();
    }

    @Override
    public void decorate(World world, Random random, int n, int n2) {
        super.decorate(world, random, n, n2);
        if (random.nextInt(1000) == 0) {
            int n3 = n + random.nextInt(16) + 8;
            int n4 = n2 + random.nextInt(16) + 8;
            WorldGenDesertWells worldGenDesertWells = new WorldGenDesertWells();
            worldGenDesertWells.generate(world, random, n3, world.getHeightValue(n3, n4) + 1, n4);
        }
    }
}
