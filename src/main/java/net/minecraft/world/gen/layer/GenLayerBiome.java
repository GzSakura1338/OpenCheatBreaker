package net.minecraft.world.gen.layer;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerBiome extends GenLayer {
    public BiomeGenBase[] warmBiomes = new BiomeGenBase[]{
        BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.savanna, BiomeGenBase.savanna, BiomeGenBase.plains
    };
    public BiomeGenBase[] biomeGenBaseArray = new BiomeGenBase[]{
        BiomeGenBase.forest, BiomeGenBase.roofedForest, BiomeGenBase.extremeHills, BiomeGenBase.plains, BiomeGenBase.birchForest, BiomeGenBase.swampland
    };
    public BiomeGenBase[] biomeGenBaseArray2 = new BiomeGenBase[]{BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.taiga, BiomeGenBase.plains};
    public BiomeGenBase[] coolBiomes = new BiomeGenBase[]{BiomeGenBase.icePlains, BiomeGenBase.icePlains, BiomeGenBase.icePlains, BiomeGenBase.coldTaiga};

    public GenLayerBiome(long l, GenLayer genLayer, WorldType worldType) {
        super(l);
        this.parent = genLayer;
        if (worldType == WorldType.DEFAULT_1_1) {
            this.warmBiomes = new BiomeGenBase[]{
                BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga
            };
        }
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] ints = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i = 0; i < areaHeight; i++) {
            for (int j = 0; j < areaWidth; j++) {
                this.initChunkSeed(j + areaX, i + areaY);
                int n = ints[j + i * areaWidth];
                int n2 = (n & 3840) >> 8;
                n &= -3841;
                if (isBiomeOceanic(n)) {
                    intCache[j + i * areaWidth] = n;
                } else if (n == BiomeGenBase.mushroomIsland.biomeID) {
                    intCache[j + i * areaWidth] = n;
                } else if (n == 1) {
                    if (n2 > 0) {
                        if (this.nextInt(3) == 0) {
                            intCache[j + i * areaWidth] = BiomeGenBase.mesaPlateau.biomeID;
                        } else {
                            intCache[j + i * areaWidth] = BiomeGenBase.mesaPlateau_F.biomeID;
                        }
                    } else {
                        intCache[j + i * areaWidth] = this.warmBiomes[this.nextInt(this.warmBiomes.length)].biomeID;
                    }
                } else if (n == 2) {
                    if (n2 > 0) {
                        intCache[j + i * areaWidth] = BiomeGenBase.jungle.biomeID;
                    } else {
                        intCache[j + i * areaWidth] = this.biomeGenBaseArray[this.nextInt(this.biomeGenBaseArray.length)].biomeID;
                    }
                } else if (n == 3) {
                    if (n2 > 0) {
                        intCache[j + i * areaWidth] = BiomeGenBase.megaTaiga.biomeID;
                    } else {
                        intCache[j + i * areaWidth] = this.biomeGenBaseArray2[this.nextInt(this.biomeGenBaseArray2.length)].biomeID;
                    }
                } else if (n == 4) {
                    intCache[j + i * areaWidth] = this.coolBiomes[this.nextInt(this.coolBiomes.length)].biomeID;
                } else {
                    intCache[j + i * areaWidth] = BiomeGenBase.mushroomIsland.biomeID;
                }
            }
        }

        return intCache;
    }
}
