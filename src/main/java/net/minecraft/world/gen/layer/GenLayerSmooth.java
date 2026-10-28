package net.minecraft.world.gen.layer;

public class GenLayerSmooth extends GenLayer {
    public GenLayerSmooth(long l, GenLayer genLayer) {
        super(l);
        super.parent = genLayer;
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
                int n5 = ints[j + 0 + (i + 1) * n3];
                int n6 = ints[j + 2 + (i + 1) * n3];
                int n7 = ints[j + 1 + (i + 0) * n3];
                int n8 = ints[j + 1 + (i + 2) * n3];
                int n9 = ints[j + 1 + (i + 1) * n3];
                if (n5 == n6 && n7 == n8) {
                    this.initChunkSeed(j + areaX, i + areaY);
                    if (this.nextInt(2) == 0) {
                        n9 = n5;
                    } else {
                        n9 = n7;
                    }
                } else {
                    if (n5 == n6) {
                        n9 = n5;
                    }

                    if (n7 == n8) {
                        n9 = n7;
                    }
                }

                intCache[j + i * areaWidth] = n9;
            }
        }

        return intCache;
    }
}
