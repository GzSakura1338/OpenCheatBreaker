package net.minecraft.world.gen.layer;

public class GenLayerZoom extends GenLayer {
    public GenLayerZoom(long l, GenLayer genLayer) {
        super(l);
        super.parent = genLayer;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int n = areaX >> 1;
        int n2 = areaY >> 1;
        int n3 = (areaWidth >> 1) + 2;
        int n4 = (areaHeight >> 1) + 2;
        int[] ints = this.parent.getInts(n, n2, n3, n4);
        int n5 = n3 - 1 << 1;
        int n6 = n4 - 1 << 1;
        int[] intCache = IntCache.getIntCache(n5 * n6);

        for (int i = 0; i < n4 - 1; i++) {
            int n7 = (i << 1) * n5;
            int n8 = 0;
            int n9 = ints[n8 + 0 + (i + 0) * n3];
            int n10 = ints[n8 + 0 + (i + 1) * n3];

            while (n8 < n3 - 1) {
                this.initChunkSeed(n8 + n << 1, i + n2 << 1);
                int n11 = ints[n8 + 1 + (i + 0) * n3];
                int n12 = ints[n8 + 1 + (i + 1) * n3];
                intCache[n7] = n9;
                intCache[n7++ + n5] = this.selectRandom(n9, n10);
                intCache[n7] = this.selectRandom(n9, n11);
                intCache[n7++ + n5] = this.selectModeOrRandom(n9, n11, n10, n12);
                n9 = n11;
                n10 = n12;
                n8++;
            }
        }

        int[] intCache2 = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i = 0; i < areaHeight; i++) {
            System.arraycopy(intCache, (i + (areaY & 1)) * n5 + (areaX & 1), intCache2, i * areaWidth, areaWidth);
        }

        return intCache2;
    }

    public static GenLayer magnify(long l, GenLayer genLayer, int n) {
        GenLayer genLayer2 = genLayer;

        for (int i = 0; i < n; i++) {
            genLayer2 = new GenLayerZoom(l + i, genLayer2);
        }

        return genLayer2;
    }
}
