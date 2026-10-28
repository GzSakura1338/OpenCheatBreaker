package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.world.ChunkPosition;

public class WorldChunkManagerHell extends WorldChunkManager {
    public BiomeGenBase biomeGenerator;
    public float rainfall;

    public WorldChunkManagerHell(BiomeGenBase biomeGenBase, float f) {
        this.biomeGenerator = biomeGenBase;
        this.rainfall = f;
    }

    @Override
    public BiomeGenBase getBiomeGenAt(int n, int n2) {
        return this.biomeGenerator;
    }

    @Override
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomeGenBases, int n, int n2, int n3, int n4) {
        if (biomeGenBases == null || biomeGenBases.length < n3 * n4) {
            biomeGenBases = new BiomeGenBase[n3 * n4];
        }

        Arrays.fill(biomeGenBases, 0, n3 * n4, this.biomeGenerator);
        return biomeGenBases;
    }

    @Override
    public float[] getRainfall(float[] floats, int n, int n2, int n3, int n4) {
        if (floats == null || floats.length < n3 * n4) {
            floats = new float[n3 * n4];
        }

        Arrays.fill(floats, 0, n3 * n4, this.rainfall);
        return floats;
    }

    @Override
    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomeGenBases, int n, int n2, int n3, int n4) {
        if (biomeGenBases == null || biomeGenBases.length < n3 * n4) {
            biomeGenBases = new BiomeGenBase[n3 * n4];
        }

        Arrays.fill(biomeGenBases, 0, n3 * n4, this.biomeGenerator);
        return biomeGenBases;
    }

    @Override
    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomeGenBases, int n, int n2, int n3, int n4, boolean flag) {
        return this.loadBlockGeneratorData(biomeGenBases, n, n2, n3, n4);
    }

    @Override
    public ChunkPosition findBiomePosition(int n, int n2, int n3, List list, Random random) {
        return list.contains(this.biomeGenerator)
            ? new ChunkPosition(n - n3 + random.nextInt(n3 * 2 + 1), 0, n2 - n3 + random.nextInt(n3 * 2 + 1))
            : null;
    }

    @Override
    public boolean areBiomesViable(int n, int n2, int n3, List list) {
        return list.contains(this.biomeGenerator);
    }
}
