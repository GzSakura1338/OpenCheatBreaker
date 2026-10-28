package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.TempCategory;

public class GenLayerBiomeEdge extends GenLayer {
    public GenLayerBiomeEdge(long l, GenLayer genLayer) {
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
                if (!this.replaceBiomeEdgeIfNecessary(
                        ints, intCache, j, i, areaWidth, n, BiomeGenBase.extremeHills.biomeID, BiomeGenBase.extremeHillsEdge.biomeID
                    )
                    && !this.replaceBiomeEdge(ints, intCache, j, i, areaWidth, n, BiomeGenBase.mesaPlateau_F.biomeID, BiomeGenBase.mesa.biomeID)
                    && !this.replaceBiomeEdge(ints, intCache, j, i, areaWidth, n, BiomeGenBase.mesaPlateau.biomeID, BiomeGenBase.mesa.biomeID)
                    && !this.replaceBiomeEdge(ints, intCache, j, i, areaWidth, n, BiomeGenBase.megaTaiga.biomeID, BiomeGenBase.taiga.biomeID)) {
                    if (n == BiomeGenBase.desert.biomeID) {
                        int n2 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                        int n3 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                        int n4 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                        int n5 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                        if (n2 != BiomeGenBase.icePlains.biomeID
                            && n3 != BiomeGenBase.icePlains.biomeID
                            && n4 != BiomeGenBase.icePlains.biomeID
                            && n5 != BiomeGenBase.icePlains.biomeID) {
                            intCache[j + i * areaWidth] = n;
                        } else {
                            intCache[j + i * areaWidth] = BiomeGenBase.extremeHillsPlus.biomeID;
                        }
                    } else if (n == BiomeGenBase.swampland.biomeID) {
                        int n2 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                        int n3 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                        int n4 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                        int n5 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                        if (n2 == BiomeGenBase.desert.biomeID
                            || n3 == BiomeGenBase.desert.biomeID
                            || n4 == BiomeGenBase.desert.biomeID
                            || n5 == BiomeGenBase.desert.biomeID
                            || n2 == BiomeGenBase.coldTaiga.biomeID
                            || n3 == BiomeGenBase.coldTaiga.biomeID
                            || n4 == BiomeGenBase.coldTaiga.biomeID
                            || n5 == BiomeGenBase.coldTaiga.biomeID
                            || n2 == BiomeGenBase.icePlains.biomeID
                            || n3 == BiomeGenBase.icePlains.biomeID
                            || n4 == BiomeGenBase.icePlains.biomeID
                            || n5 == BiomeGenBase.icePlains.biomeID) {
                            intCache[j + i * areaWidth] = BiomeGenBase.plains.biomeID;
                        } else if (n2 != BiomeGenBase.jungle.biomeID
                            && n5 != BiomeGenBase.jungle.biomeID
                            && n3 != BiomeGenBase.jungle.biomeID
                            && n4 != BiomeGenBase.jungle.biomeID) {
                            intCache[j + i * areaWidth] = n;
                        } else {
                            intCache[j + i * areaWidth] = BiomeGenBase.jungleEdge.biomeID;
                        }
                    } else {
                        intCache[j + i * areaWidth] = n;
                    }
                }
            }
        }

        return intCache;
    }

    public boolean replaceBiomeEdgeIfNecessary(int[] ints, int[] ints2, int n, int n2, int n3, int n4, int n5, int n6) {
        if (!biomesEqualOrMesaPlateau(n4, n5)) {
            return false;
        }

        int n7 = ints[n + 1 + (n2 + 1 - 1) * (n3 + 2)];
        int n8 = ints[n + 1 + 1 + (n2 + 1) * (n3 + 2)];
        int n9 = ints[n + 1 - 1 + (n2 + 1) * (n3 + 2)];
        int n10 = ints[n + 1 + (n2 + 1 + 1) * (n3 + 2)];
        if (this.canBiomesBeNeighbors(n7, n5)
            && this.canBiomesBeNeighbors(n8, n5)
            && this.canBiomesBeNeighbors(n9, n5)
            && this.canBiomesBeNeighbors(n10, n5)) {
            ints2[n + n2 * n3] = n4;
        } else {
            ints2[n + n2 * n3] = n6;
        }

        return true;
    }

    public boolean replaceBiomeEdge(int[] ints, int[] ints2, int n, int n2, int n3, int n4, int n5, int n6) {
        if (n4 != n5) {
            return false;
        }

        int n7 = ints[n + 1 + (n2 + 1 - 1) * (n3 + 2)];
        int n8 = ints[n + 1 + 1 + (n2 + 1) * (n3 + 2)];
        int n9 = ints[n + 1 - 1 + (n2 + 1) * (n3 + 2)];
        int n10 = ints[n + 1 + (n2 + 1 + 1) * (n3 + 2)];
        if (biomesEqualOrMesaPlateau(n7, n5)
            && biomesEqualOrMesaPlateau(n8, n5)
            && biomesEqualOrMesaPlateau(n9, n5)
            && biomesEqualOrMesaPlateau(n10, n5)) {
            ints2[n + n2 * n3] = n4;
        } else {
            ints2[n + n2 * n3] = n6;
        }

        return true;
    }

    public boolean canBiomesBeNeighbors(int n, int n2) {
        if (biomesEqualOrMesaPlateau(n, n2)) {
            return true;
        } else if (BiomeGenBase.getBiome(n) != null && BiomeGenBase.getBiome(n2) != null) {
            TempCategory tempCategory = BiomeGenBase.getBiome(n).getTempCategory();
            TempCategory tempCategory2 = BiomeGenBase.getBiome(n2).getTempCategory();
            return tempCategory == tempCategory2 || tempCategory == TempCategory.MEDIUM || tempCategory2 == TempCategory.MEDIUM;
        } else {
            return false;
        }
    }
}
