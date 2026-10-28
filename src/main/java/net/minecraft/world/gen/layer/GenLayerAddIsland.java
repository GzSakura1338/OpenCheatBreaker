package net.minecraft.world.gen.layer;

public class GenLayerAddIsland extends GenLayer {
    public GenLayerAddIsland(long l, GenLayer genLayer) {
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
                if (n9 != 0 || n5 == 0 && n6 == 0 && n7 == 0 && n8 == 0) {
                    if (n9 > 0 && (n5 == 0 || n6 == 0 || n7 == 0 || n8 == 0)) {
                        if (this.nextInt(5) == 0) {
                            if (n9 == 4) {
                                intCache[j + i * areaWidth] = 4;
                            } else {
                                intCache[j + i * areaWidth] = 0;
                            }
                        } else {
                            intCache[j + i * areaWidth] = n9;
                        }
                    } else {
                        intCache[j + i * areaWidth] = n9;
                    }
                } else {
                    int n10 = 1;
                    int n11 = 1;
                    if (n5 != 0 && this.nextInt(n10++) == 0) {
                        n11 = n5;
                    }

                    if (n6 != 0 && this.nextInt(n10++) == 0) {
                        n11 = n6;
                    }

                    if (n7 != 0 && this.nextInt(n10++) == 0) {
                        n11 = n7;
                    }

                    if (n8 != 0 && this.nextInt(n10++) == 0) {
                        n11 = n8;
                    }

                    if (this.nextInt(3) == 0) {
                        intCache[j + i * areaWidth] = n11;
                    } else if (n11 == 4) {
                        intCache[j + i * areaWidth] = 4;
                    } else {
                        intCache[j + i * areaWidth] = 0;
                    }
                }
            }
        }

        return intCache;
    }
}
