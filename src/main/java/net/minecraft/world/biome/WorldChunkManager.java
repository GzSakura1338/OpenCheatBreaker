package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManager {
    public GenLayer genBiomes;
    public GenLayer biomeIndexLayer;
    public BiomeCache biomeCache = new BiomeCache(this);
    public List biomesToSpawnIn = new ArrayList();

    public WorldChunkManager() {
        this.biomesToSpawnIn.add(BiomeGenBase.forest);
        this.biomesToSpawnIn.add(BiomeGenBase.plains);
        this.biomesToSpawnIn.add(BiomeGenBase.taiga);
        this.biomesToSpawnIn.add(BiomeGenBase.taigaHills);
        this.biomesToSpawnIn.add(BiomeGenBase.forestHills);
        this.biomesToSpawnIn.add(BiomeGenBase.jungle);
        this.biomesToSpawnIn.add(BiomeGenBase.jungleHills);
    }

    public WorldChunkManager(long l, WorldType worldType) {
        this();
        GenLayer[] initializeAllBiomeGenerators = GenLayer.initializeAllBiomeGenerators(l, worldType);
        this.genBiomes = initializeAllBiomeGenerators[0];
        this.biomeIndexLayer = initializeAllBiomeGenerators[1];
    }

    public WorldChunkManager(World world) {
        this(world.getSeed(), world.getWorldInfo().getTerrainType());
    }

    public List getBiomesToSpawnIn() {
        return this.biomesToSpawnIn;
    }

    public BiomeGenBase getBiomeGenAt(int n, int n2) {
        return this.biomeCache.getBiomeGenAt(n, n2);
    }

    public float[] getRainfall(float[] floats, int n, int n2, int n3, int n4) {
        IntCache.resetIntCache();
        if (floats == null || floats.length < n3 * n4) {
            floats = new float[n3 * n4];
        }

        int[] ints = this.biomeIndexLayer.getInts(n, n2, n3, n4);

        for (int i = 0; i < n3 * n4; i++) {
            try {
                float f = BiomeGenBase.getBiome(ints[i]).getIntRainfall() / 65536.0F;
                if (f > 1.0F) {
                    f = 1.0F;
                }

                floats[i] = f;
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Invalid Biome id");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("DownfallBlock");
                makeCategory.addCrashSection("biome id", i);
                makeCategory.addCrashSection("downfalls[] size", floats.length);
                makeCategory.addCrashSection("x", n);
                makeCategory.addCrashSection("z", n2);
                makeCategory.addCrashSection("w", n3);
                makeCategory.addCrashSection("h", n4);
                throw new ReportedException(makeCrashReport);
            }
        }

        return floats;
    }

    public float getTemperatureAtHeight(float f, int n) {
        return f;
    }

    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomeGenBases, int n, int n2, int n3, int n4) {
        IntCache.resetIntCache();
        if (biomeGenBases == null || biomeGenBases.length < n3 * n4) {
            biomeGenBases = new BiomeGenBase[n3 * n4];
        }

        int[] ints = this.genBiomes.getInts(n, n2, n3, n4);

        try {
            for (int i = 0; i < n3 * n4; i++) {
                biomeGenBases[i] = BiomeGenBase.getBiome(ints[i]);
            }

            return biomeGenBases;
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Invalid Biome id");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("RawBiomeBlock");
            makeCategory.addCrashSection("biomes[] size", biomeGenBases.length);
            makeCategory.addCrashSection("x", n);
            makeCategory.addCrashSection("z", n2);
            makeCategory.addCrashSection("w", n3);
            makeCategory.addCrashSection("h", n4);
            throw new ReportedException(makeCrashReport);
        }
    }

    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomeGenBases, int n, int n2, int n3, int n4) {
        return this.getBiomeGenAt(biomeGenBases, n, n2, n3, n4, true);
    }

    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomeGenBases, int n, int n2, int n3, int n4, boolean flag) {
        IntCache.resetIntCache();
        if (biomeGenBases == null || biomeGenBases.length < n3 * n4) {
            biomeGenBases = new BiomeGenBase[n3 * n4];
        }

        if (flag && n3 == 16 && n4 == 16 && (n & 15) == 0 && (n2 & 15) == 0) {
            BiomeGenBase[] cachedBiomes = this.biomeCache.getCachedBiomes(n, n2);
            System.arraycopy(cachedBiomes, 0, biomeGenBases, 0, n3 * n4);
            return biomeGenBases;
        }

        int[] ints = this.biomeIndexLayer.getInts(n, n2, n3, n4);

        for (int i = 0; i < n3 * n4; i++) {
            biomeGenBases[i] = BiomeGenBase.getBiome(ints[i]);
        }

        return biomeGenBases;
    }

    public boolean areBiomesViable(int n, int n2, int n3, List list) {
        IntCache.resetIntCache();
        int n4 = n - n3 >> 2;
        int n5 = n2 - n3 >> 2;
        int n6 = n + n3 >> 2;
        int n7 = n2 + n3 >> 2;
        int n8 = n6 - n4 + 1;
        int n9 = n7 - n5 + 1;
        int[] ints = this.genBiomes.getInts(n4, n5, n8, n9);

        try {
            for (int i = 0; i < n8 * n9; i++) {
                BiomeGenBase biome = BiomeGenBase.getBiome(ints[i]);
                if (!list.contains(biome)) {
                    return false;
                }
            }

            return true;
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Invalid Biome id");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Layer");
            makeCategory.addCrashSection("Layer", this.genBiomes.toString());
            makeCategory.addCrashSection("x", n);
            makeCategory.addCrashSection("z", n2);
            makeCategory.addCrashSection("radius", n3);
            makeCategory.addCrashSection("allowed", list);
            throw new ReportedException(makeCrashReport);
        }
    }

    public ChunkPosition findBiomePosition(int n, int n2, int n3, List list, Random random) {
        IntCache.resetIntCache();
        int n4 = n - n3 >> 2;
        int n5 = n2 - n3 >> 2;
        int n6 = n + n3 >> 2;
        int n7 = n2 + n3 >> 2;
        int n8 = n6 - n4 + 1;
        int n9 = n7 - n5 + 1;
        int[] ints = this.genBiomes.getInts(n4, n5, n8, n9);
        ChunkPosition chunkPosition = null;
        int n10 = 0;

        for (int i = 0; i < n8 * n9; i++) {
            int n11 = n4 + i % n8 << 2;
            int n12 = n5 + i / n8 << 2;
            BiomeGenBase biome = BiomeGenBase.getBiome(ints[i]);
            if (list.contains(biome) && (chunkPosition == null || random.nextInt(n10 + 1) == 0)) {
                chunkPosition = new ChunkPosition(n11, 0, n12);
                n10++;
            }
        }

        return chunkPosition;
    }

    public void cleanupCache() {
        this.biomeCache.cleanupCache();
    }
}
