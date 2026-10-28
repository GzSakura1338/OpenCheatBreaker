package net.minecraft.world.gen.layer;

public class GenLayerAddSnow extends GenLayer {
    public GenLayerAddSnow(long l, GenLayer genLayer) {
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
                int n5 = ints[j + 1 + (i + 1) * n3];
                this.initChunkSeed(j + areaX, i + areaY);
                if (n5 == 0) {
                    intCache[j + i * areaWidth] = 0;
                } else {
                    int nextInt = this.nextInt(6);
                    byte b;
                    if (nextInt == 0) {
                        b = 4;
                    } else if (nextInt <= 1) {
                        b = 3;
                    } else {
                        b = 1;
                    }

                    intCache[j + i * areaWidth] = b;
                }
            }
        }

        return intCache;
    }
}
