package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenMesa;

public class GenLayerShore extends GenLayer {
    public GenLayerShore(long l, GenLayer genLayer) {
        super(l);
        this.parent = genLayer;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] ints = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i = 0; i < areaHeight; i++) {
            for (int j = 0; j < areaWidth; j++) {
                this.initChunkSeed(j + areaX, i + areaY);
                int n = ints[j + 1 + (i + 1) * (areaWidth + 2)];
                BiomeGenBase biome = BiomeGenBase.getBiome(n);
                if (n == BiomeGenBase.mushroomIsland.biomeID) {
                    int n2 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                    int n3 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                    int n4 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                    int n5 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                    if (n2 != BiomeGenBase.ocean.biomeID
                        && n3 != BiomeGenBase.ocean.biomeID
                        && n4 != BiomeGenBase.ocean.biomeID
                        && n5 != BiomeGenBase.ocean.biomeID) {
                        intCache[j + i * areaWidth] = n;
                    } else {
                        intCache[j + i * areaWidth] = BiomeGenBase.mushroomIslandShore.biomeID;
                    }
                } else if (biome != null && biome.getBiomeClass() == BiomeGenJungle.class) {
                    int n2 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                    int n3 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                    int n4 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                    int n5 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                    if (!this.check(n2) || !this.check(n3) || !this.check(n4) || !this.check(n5)) {
                        intCache[j + i * areaWidth] = BiomeGenBase.jungleEdge.biomeID;
                    } else if (!isBiomeOceanic(n2) && !isBiomeOceanic(n3) && !isBiomeOceanic(n4) && !isBiomeOceanic(n5)) {
                        intCache[j + i * areaWidth] = n;
                    } else {
                        intCache[j + i * areaWidth] = BiomeGenBase.beach.biomeID;
                    }
                } else if (n == BiomeGenBase.extremeHills.biomeID
                    || n == BiomeGenBase.extremeHillsPlus.biomeID
                    || n == BiomeGenBase.extremeHillsEdge.biomeID) {
                    this.process(ints, intCache, j, i, areaWidth, n, BiomeGenBase.stoneBeach.biomeID);
                } else if (biome != null && biome.isMutatedBiome()) {
                    this.process(ints, intCache, j, i, areaWidth, n, BiomeGenBase.coldBeach.biomeID);
                } else if (n == BiomeGenBase.mesa.biomeID || n == BiomeGenBase.mesaPlateau_F.biomeID) {
                    int n2 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                    int n3 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                    int n4 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                    int n5 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                    if (isBiomeOceanic(n2) || isBiomeOceanic(n3) || isBiomeOceanic(n4) || isBiomeOceanic(n5)) {
                        intCache[j + i * areaWidth] = n;
                    } else if (this.check2(n2) && this.check2(n3) && this.check2(n4) && this.check2(n5)) {
                        intCache[j + i * areaWidth] = n;
                    } else {
                        intCache[j + i * areaWidth] = BiomeGenBase.desert.biomeID;
                    }
                } else if (n != BiomeGenBase.ocean.biomeID
                    && n != BiomeGenBase.deepOcean.biomeID
                    && n != BiomeGenBase.river.biomeID
                    && n != BiomeGenBase.swampland.biomeID) {
                    int n2 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                    int n3 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                    int n4 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                    int n5 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                    if (!isBiomeOceanic(n2) && !isBiomeOceanic(n3) && !isBiomeOceanic(n4) && !isBiomeOceanic(n5)) {
                        intCache[j + i * areaWidth] = n;
                    } else {
                        intCache[j + i * areaWidth] = BiomeGenBase.beach.biomeID;
                    }
                } else {
                    intCache[j + i * areaWidth] = n;
                }
            }
        }

        return intCache;
    }

    public void process(int[] ints, int[] ints2, int n, int n2, int n3, int n4, int n5) {
        if (isBiomeOceanic(n4)) {
            ints2[n + n2 * n3] = n4;
        } else {
            int n6 = ints[n + 1 + (n2 + 1 - 1) * (n3 + 2)];
            int n7 = ints[n + 1 + 1 + (n2 + 1) * (n3 + 2)];
            int n8 = ints[n + 1 - 1 + (n2 + 1) * (n3 + 2)];
            int n9 = ints[n + 1 + (n2 + 1 + 1) * (n3 + 2)];
            if (!isBiomeOceanic(n6) && !isBiomeOceanic(n7) && !isBiomeOceanic(n8) && !isBiomeOceanic(n9)) {
                ints2[n + n2 * n3] = n4;
            } else {
                ints2[n + n2 * n3] = n5;
            }
        }
    }

    public boolean check(int n) {
        return BiomeGenBase.getBiome(n) != null && BiomeGenBase.getBiome(n).getBiomeClass() == BiomeGenJungle.class
            || n == BiomeGenBase.jungleEdge.biomeID
            || n == BiomeGenBase.jungle.biomeID
            || n == BiomeGenBase.jungleHills.biomeID
            || n == BiomeGenBase.forest.biomeID
            || n == BiomeGenBase.taiga.biomeID
            || isBiomeOceanic(n);
    }

    public boolean check2(int n) {
        return BiomeGenBase.getBiome(n) != null && BiomeGenBase.getBiome(n) instanceof BiomeGenMesa;
    }
}
