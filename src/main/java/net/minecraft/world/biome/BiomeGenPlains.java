package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

public class BiomeGenPlains extends BiomeGenBase {
    public boolean flag;

    public BiomeGenPlains(int n) {
        super(n);
        this.setTemperatureRainfall(0.8F, 0.4F);
        this.setHeight(height_LowPlains);
        this.spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 5, 2, 6));
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.grassPerChunk = 10;
    }

    @Override
    public String formatText(Random random, int n, int n2, int n3) {
        double noise = plantNoise.noise(n / 200.0, n3 / 200.0);
        if (noise < -0.8) {
            int nextInt = random.nextInt(4);
            return BlockFlower.stringArray[4 + nextInt];
        } else if (random.nextInt(3) > 0) {
            int nextInt = random.nextInt(3);
            return nextInt == 0 ? BlockFlower.stringArray[0] : (nextInt == 1 ? BlockFlower.stringArray[3] : BlockFlower.stringArray[8]);
        } else {
            return BlockFlower.stringArray2[0];
        }
    }

    @Override
    public void decorate(World world, Random random, int n, int n2) {
        double noise = plantNoise.noise((n + 8) / 200.0, (n2 + 8) / 200.0);
        if (noise < -0.8) {
            this.theBiomeDecorator.flowersPerChunk = 15;
            this.theBiomeDecorator.grassPerChunk = 5;
        } else {
            this.theBiomeDecorator.flowersPerChunk = 4;
            this.theBiomeDecorator.grassPerChunk = 10;
            genTallFlowers.setIntValue(2);

            for (int i = 0; i < 7; i++) {
                int n3 = n + random.nextInt(16) + 8;
                int n4 = n2 + random.nextInt(16) + 8;
                int nextInt = random.nextInt(world.getHeightValue(n3, n4) + 32);
                genTallFlowers.generate(world, random, n3, nextInt, n4);
            }
        }

        if (this.flag) {
            genTallFlowers.setIntValue(0);

            for (int i = 0; i < 10; i++) {
                int n3 = n + random.nextInt(16) + 8;
                int n4 = n2 + random.nextInt(16) + 8;
                int nextInt = random.nextInt(world.getHeightValue(n3, n4) + 32);
                genTallFlowers.generate(world, random, n3, nextInt, n4);
            }
        }

        super.decorate(world, random, n, n2);
    }

    @Override
    public BiomeGenBase createMutation() {
        BiomeGenPlains biomeGenPlains = new BiomeGenPlains(this.biomeID + 128);
        biomeGenPlains.setBiomeName("Sunflower Plains");
        biomeGenPlains.flag = true;
        biomeGenPlains.setColor(9286496);
        biomeGenPlains.intValue = 14273354;
        return biomeGenPlains;
    }
}
