package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerDeepOcean extends GenLayer {
    public GenLayerDeepOcean(long l, GenLayer genLayer) {
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
                int n5 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                int n6 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                int n7 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                int n8 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                int n9 = ints[j + 1 + (i + 1) * n3];
                int n10 = 0;
                if (n5 == 0) {
                    n10++;
                }

                if (n6 == 0) {
                    n10++;
                }

                if (n7 == 0) {
                    n10++;
                }

                if (n8 == 0) {
                    n10++;
                }

                if (n9 == 0 && n10 > 3) {
                    intCache[j + i * areaWidth] = BiomeGenBase.deepOcean.biomeID;
                } else {
                    intCache[j + i * areaWidth] = n9;
                }
            }
        }

        return intCache;
    }
}
