package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills extends GenLayer {
    public static Logger logger = LogManager.getLogger();
    public GenLayer genLayer;

    public GenLayerHills(long l, GenLayer genLayer, GenLayer genLayer2) {
        super(l);
        this.parent = genLayer;
        this.genLayer = genLayer2;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] ints = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] ints2 = this.genLayer.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i = 0; i < areaHeight; i++) {
            for (int j = 0; j < areaWidth; j++) {
                this.initChunkSeed(j + areaX, i + areaY);
                int n = ints[j + 1 + (i + 1) * (areaWidth + 2)];
                int n2 = ints2[j + 1 + (i + 1) * (areaWidth + 2)];
                boolean flag = (n2 - 2) % 29 == 0;
                if (n > 255) {
                    logger.debug("old! " + n);
                }

                if (n != 0 && n2 >= 2 && (n2 - 2) % 29 == 1 && n < 128) {
                    if (BiomeGenBase.getBiome(n + 128) != null) {
                        intCache[j + i * areaWidth] = n + 128;
                    } else {
                        intCache[j + i * areaWidth] = n;
                    }
                } else if (this.nextInt(3) != 0 && !flag) {
                    intCache[j + i * areaWidth] = n;
                } else {
                    int n3 = n;
                    if (n == BiomeGenBase.desert.biomeID) {
                        n3 = BiomeGenBase.desertHills.biomeID;
                    } else if (n == BiomeGenBase.forest.biomeID) {
                        n3 = BiomeGenBase.forestHills.biomeID;
                    } else if (n == BiomeGenBase.birchForest.biomeID) {
                        n3 = BiomeGenBase.birchForestHills.biomeID;
                    } else if (n == BiomeGenBase.roofedForest.biomeID) {
                        n3 = BiomeGenBase.plains.biomeID;
                    } else if (n == BiomeGenBase.taiga.biomeID) {
                        n3 = BiomeGenBase.taigaHills.biomeID;
                    } else if (n == BiomeGenBase.megaTaiga.biomeID) {
                        n3 = BiomeGenBase.megaTaigaHills.biomeID;
                    } else if (n == BiomeGenBase.coldTaiga.biomeID) {
                        n3 = BiomeGenBase.coldTaigaHills.biomeID;
                    } else if (n == BiomeGenBase.plains.biomeID) {
                        if (this.nextInt(3) == 0) {
                            n3 = BiomeGenBase.forestHills.biomeID;
                        } else {
                            n3 = BiomeGenBase.forest.biomeID;
                        }
                    } else if (n == BiomeGenBase.icePlains.biomeID) {
                        n3 = BiomeGenBase.iceMountains.biomeID;
                    } else if (n == BiomeGenBase.jungle.biomeID) {
                        n3 = BiomeGenBase.jungleHills.biomeID;
                    } else if (n == BiomeGenBase.ocean.biomeID) {
                        n3 = BiomeGenBase.deepOcean.biomeID;
                    } else if (n == BiomeGenBase.extremeHills.biomeID) {
                        n3 = BiomeGenBase.extremeHillsPlus.biomeID;
                    } else if (n == BiomeGenBase.savanna.biomeID) {
                        n3 = BiomeGenBase.savannaPlateau.biomeID;
                    } else if (biomesEqualOrMesaPlateau(n, BiomeGenBase.mesaPlateau_F.biomeID)) {
                        n3 = BiomeGenBase.mesa.biomeID;
                    } else if (n == BiomeGenBase.deepOcean.biomeID && this.nextInt(3) == 0) {
                        int nextInt = this.nextInt(2);
                        if (nextInt == 0) {
                            n3 = BiomeGenBase.plains.biomeID;
                        } else {
                            n3 = BiomeGenBase.forest.biomeID;
                        }
                    }

                    if (flag && n3 != n) {
                        if (BiomeGenBase.getBiome(n3 + 128) != null) {
                            n3 += 128;
                        } else {
                            n3 = n;
                        }
                    }

                    if (n3 == n) {
                        intCache[j + i * areaWidth] = n;
                    } else {
                        int n4 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                        int n5 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                        int n6 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                        int n7 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                        int n8 = 0;
                        if (biomesEqualOrMesaPlateau(n4, n)) {
                            n8++;
                        }

                        if (biomesEqualOrMesaPlateau(n5, n)) {
                            n8++;
                        }

                        if (biomesEqualOrMesaPlateau(n6, n)) {
                            n8++;
                        }

                        if (biomesEqualOrMesaPlateau(n7, n)) {
                            n8++;
                        }

                        if (n8 >= 3) {
                            intCache[j + i * areaWidth] = n3;
                        } else {
                            intCache[j + i * areaWidth] = n;
                        }
                    }
                }
            }
        }

        return intCache;
    }
}
