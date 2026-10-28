package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.Height;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenIcePath;
import net.minecraft.world.gen.feature.WorldGenIceSpike;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

public class BiomeGenSnow extends BiomeGenBase {
    public WorldGenIceSpike iceSpikeFeature = new WorldGenIceSpike();
    public WorldGenIcePath worldGenIcePath = new WorldGenIcePath(4);
    public boolean flag;

    public BiomeGenSnow(int n, boolean flag) {
        super(n);
        this.flag = flag;
        if (flag) {
            this.topBlock = Blocks.snow;
        }

        this.spawnableCreatureList.clear();
    }

    @Override
    public void decorate(World world, Random random, int n, int n2) {
        if (this.flag) {
            for (int i = 0; i < 3; i++) {
                int n3 = n + random.nextInt(16) + 8;
                int n4 = n2 + random.nextInt(16) + 8;
                this.iceSpikeFeature.generate(world, random, n3, world.getHeightValue(n3, n4), n4);
            }

            for (int i = 0; i < 2; i++) {
                int n3 = n + random.nextInt(16) + 8;
                int n4 = n2 + random.nextInt(16) + 8;
                this.worldGenIcePath.generate(world, random, n3, world.getHeightValue(n3, n4), n4);
            }
        }

        super.decorate(world, random, n, n2);
    }

    @Override
    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
        return new WorldGenTaiga2(false);
    }

    @Override
    public BiomeGenBase createMutation() {
        BiomeGenBase setHeight = new BiomeGenSnow(this.biomeID + 128, true)
            .findBiomeGenBase(13828095, true)
            .setBiomeName(this.biomeName + " Spikes")
            .setEnableSnow()
            .setTemperatureRainfall(0.0F, 0.5F)
            .setHeight(new Height(this.minHeight + 0.1F, this.maxHeight + 0.1F));
        setHeight.minHeight = this.minHeight + 0.3F;
        setHeight.maxHeight = this.maxHeight + 0.4F;
        return setHeight;
    }
}
