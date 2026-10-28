package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.TempCategory;

public class BiomeGenOcean extends BiomeGenBase {
    public BiomeGenOcean(int n) {
        super(n);
        this.spawnableCreatureList.clear();
    }

    @Override
    public TempCategory getTempCategory() {
        return TempCategory.OCEAN;
    }

    @Override
    public void genTerrainBlocks(World world, Random random, Block[] blocks, byte[] bytes, int n, int n2, double d) {
        super.genTerrainBlocks(world, random, blocks, bytes, n, n2, d);
    }
}
