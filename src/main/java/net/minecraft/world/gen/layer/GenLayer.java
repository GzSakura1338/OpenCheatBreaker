package net.minecraft.world.gen.layer;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayerEdge.Mode;

public abstract class GenLayer {
    public long worldGenSeed;
    public GenLayer parent;
    public long chunkSeed;
    public long baseSeed;

    public static GenLayer[] initializeAllBiomeGenerators(long l, WorldType worldType) {
        boolean flag = false;
        GenLayerIsland genLayerIsland = new GenLayerIsland(1L);
        GenLayerFuzzyZoom genLayerFuzzyZoom = new GenLayerFuzzyZoom(2000L, genLayerIsland);
        GenLayerAddIsland genLayerAddIsland = new GenLayerAddIsland(1L, genLayerFuzzyZoom);
        GenLayerZoom genLayerZoom = new GenLayerZoom(2001L, genLayerAddIsland);
        genLayerAddIsland = new GenLayerAddIsland(2L, genLayerZoom);
        genLayerAddIsland = new GenLayerAddIsland(50L, genLayerAddIsland);
        genLayerAddIsland = new GenLayerAddIsland(70L, genLayerAddIsland);
        GenLayerRemoveTooMuchOcean genLayerRemoveTooMuchOcean = new GenLayerRemoveTooMuchOcean(2L, genLayerAddIsland);
        GenLayerAddSnow genLayerAddSnow = new GenLayerAddSnow(2L, genLayerRemoveTooMuchOcean);
        genLayerAddIsland = new GenLayerAddIsland(3L, genLayerAddSnow);
        GenLayerEdge genLayerEdge = new GenLayerEdge(2L, genLayerAddIsland, Mode.COOL_WARM);
        genLayerEdge = new GenLayerEdge(2L, genLayerEdge, Mode.HEAT_ICE);
        genLayerEdge = new GenLayerEdge(3L, genLayerEdge, Mode.SPECIAL);
        genLayerZoom = new GenLayerZoom(2002L, genLayerEdge);
        genLayerZoom = new GenLayerZoom(2003L, genLayerZoom);
        genLayerAddIsland = new GenLayerAddIsland(4L, genLayerZoom);
        GenLayerAddMushroomIsland genLayerAddMushroomIsland = new GenLayerAddMushroomIsland(5L, genLayerAddIsland);
        GenLayerDeepOcean genLayerDeepOcean = new GenLayerDeepOcean(4L, genLayerAddMushroomIsland);
        GenLayer magnify = GenLayerZoom.magnify(1000L, genLayerDeepOcean, 0);
        byte b = 4;
        if (worldType == WorldType.LARGE_BIOMES) {
            b = 6;
        }

        if (flag) {
            b = 4;
        }

        GenLayer magnify2 = GenLayerZoom.magnify(1000L, magnify, 0);
        GenLayerRiverInit genLayerRiverInit = new GenLayerRiverInit(100L, magnify2);
        GenLayer genLayer = new GenLayerBiome(200L, magnify, worldType);
        if (!flag) {
            GenLayer magnify3 = GenLayerZoom.magnify(1000L, genLayer, 2);
            genLayer = new GenLayerBiomeEdge(1000L, magnify3);
        }

        GenLayer magnify3 = GenLayerZoom.magnify(1000L, genLayerRiverInit, 2);
        GenLayerHills genLayerHills = new GenLayerHills(1000L, genLayer, magnify3);
        magnify2 = GenLayerZoom.magnify(1000L, genLayerRiverInit, 2);
        magnify2 = GenLayerZoom.magnify(1000L, magnify2, b);
        GenLayerRiver genLayerRiver = new GenLayerRiver(1L, magnify2);
        GenLayerSmooth genLayerSmooth = new GenLayerSmooth(1000L, genLayerRiver);
        genLayer = new GenLayerRareBiome(1001L, genLayerHills);

        for (int i = 0; i < b; i++) {
            genLayer = new GenLayerZoom(1000 + i, genLayer);
            if (i == 0) {
                genLayer = new GenLayerAddIsland(3L, genLayer);
            }

            if (i == 1) {
                genLayer = new GenLayerShore(1000L, genLayer);
            }
        }

        GenLayerSmooth genLayerSmooth2 = new GenLayerSmooth(1000L, genLayer);
        GenLayerRiverMix genLayerRiverMix = new GenLayerRiverMix(100L, genLayerSmooth2, genLayerSmooth);
        GenLayerVoronoiZoom genLayerVoronoiZoom = new GenLayerVoronoiZoom(10L, genLayerRiverMix);
        genLayerRiverMix.initWorldGenSeed(l);
        genLayerVoronoiZoom.initWorldGenSeed(l);
        return new GenLayer[]{genLayerRiverMix, genLayerVoronoiZoom, genLayerRiverMix};
    }

    public GenLayer(long l) {
        this.baseSeed = l;
        this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
        this.baseSeed += l;
        this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
        this.baseSeed += l;
        this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
        this.baseSeed += l;
    }

    public void initWorldGenSeed(long l) {
        this.worldGenSeed = l;
        if (this.parent != null) {
            this.parent.initWorldGenSeed(l);
        }

        this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
        this.worldGenSeed = this.worldGenSeed + this.baseSeed;
        this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
        this.worldGenSeed = this.worldGenSeed + this.baseSeed;
        this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
        this.worldGenSeed = this.worldGenSeed + this.baseSeed;
    }

    public void initChunkSeed(long l, long l2) {
        this.chunkSeed = this.worldGenSeed;
        this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += l;
        this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += l2;
        this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += l;
        this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += l2;
    }

    public int nextInt(int n) {
        int n2 = (int)((this.chunkSeed >> 24) % n);
        if (n2 < 0) {
            n2 += n;
        }

        this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed = this.chunkSeed + this.worldGenSeed;
        return n2;
    }

    public abstract int[] getInts(int n, int n2, int n3, int n4);

    public static boolean biomesEqualOrMesaPlateau(final int biomeIDA, final int biomeIDB) {
        if (biomeIDA == biomeIDB) {
            return true;
        }

        if (biomeIDA != BiomeGenBase.mesaPlateau_F.biomeID && biomeIDA != BiomeGenBase.mesaPlateau.biomeID) {
            try {
                return BiomeGenBase.getBiome(biomeIDA) != null
                    && BiomeGenBase.getBiome(biomeIDB) != null
                    && BiomeGenBase.getBiome(biomeIDA).isEqualTo(BiomeGenBase.getBiome(biomeIDB));
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Comparing biomes");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Biomes being compared");
                makeCategory.addCrashSection("Biome A ID", biomeIDA);
                makeCategory.addCrashSection("Biome B ID", biomeIDB);
                makeCategory.addCrashSectionCallable("Biome A", new Callable() {
                    public String call() {
                        return String.valueOf(BiomeGenBase.getBiome(biomeIDA));
                    }
                });
                makeCategory.addCrashSectionCallable("Biome B", new Callable() {
                    public String call() {
                        return String.valueOf(BiomeGenBase.getBiome(biomeIDB));
                    }
                });
                throw new ReportedException(makeCrashReport);
            }
        } else {
            return biomeIDB == BiomeGenBase.mesaPlateau_F.biomeID || biomeIDB == BiomeGenBase.mesaPlateau.biomeID;
        }
    }

    public static boolean isBiomeOceanic(int n) {
        return n == BiomeGenBase.ocean.biomeID || n == BiomeGenBase.deepOcean.biomeID || n == BiomeGenBase.frozenOcean.biomeID;
    }

    public int selectRandom(int... ints) {
        return ints[this.nextInt(ints.length)];
    }

    public int selectModeOrRandom(int n, int n2, int n3, int n4) {
        return n2 == n3 && n3 == n4
            ? n2
            : (
                n == n2 && n == n3
                    ? n
                    : (
                        n == n2 && n == n4
                            ? n
                            : (
                                n == n3 && n == n4
                                    ? n
                                    : (
                                        n == n2 && n3 != n4
                                            ? n
                                            : (
                                                n == n3 && n2 != n4
                                                    ? n
                                                    : (
                                                        n == n4 && n2 != n3
                                                            ? n
                                                            : (
                                                                n2 == n3 && n != n4
                                                                    ? n2
                                                                    : (
                                                                        n2 == n4 && n != n3
                                                                            ? n2
                                                                            : (n3 == n4 && n != n2 ? n3 : this.selectRandom(n, n2, n3, n4))
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );
    }
}
