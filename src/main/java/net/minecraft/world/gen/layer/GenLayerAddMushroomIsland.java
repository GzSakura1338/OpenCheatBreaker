package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerAddMushroomIsland extends GenLayer {
    public GenLayerAddMushroomIsland(long l, GenLayer genLayer) {
        super(l);
        this.parent = genLayer;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int n = areaX - 1;
        int n2 = areaY - 1;
        int n3 = areaWidth + 2;
        int n4 = areaHeight + 2;
        int[] ints = this.parent.getInts(n, n2, n3, n4);
        int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i = 0; i < areaHeight; i++) {
            for (int j = 0; j < areaWidth; j++) {
                int n5 = ints[j + 0 + (i + 0) * n3];
                int n6 = ints[j + 2 + (i + 0) * n3];
                int n7 = ints[j + 0 + (i + 2) * n3];
                int n8 = ints[j + 2 + (i + 2) * n3];
                int n9 = ints[j + 1 + (i + 1) * n3];
                this.initChunkSeed(j + areaX, i + areaY);
                if (n9 == 0 && n5 == 0 && n6 == 0 && n7 == 0 && n8 == 0 && this.nextInt(100) == 0) {
                    intCache[j + i * areaWidth] = BiomeGenBase.mushroomIsland.biomeID;
                } else {
                    intCache[j + i * areaWidth] = n9;
                }
            }
        }

        return intCache;
    }
}
