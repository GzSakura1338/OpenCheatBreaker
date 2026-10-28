package net.optifine;

import java.awt.Dimension;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL11;

public class Mipmaps {
    public String iconName;
    public int width;
    public int height;
    public int[] data;
    public boolean direct;
    public Dimension[] mipmapDimensions;
    public int[][] mipmapDatas;
    public IntBuffer[] mipmapBuffers;

    public Mipmaps(String str, int n, int n2, int[] ints, boolean flag) {
        this.iconName = str;
        this.width = n;
        this.height = n2;
        this.data = ints;
        this.direct = flag;
        this.mipmapDimensions = makeMipmapDimensions(n, n2, str);
        this.mipmapDatas = generateMipMapData(ints, n, n2, this.mipmapDimensions);
        if (flag) {
            this.mipmapBuffers = makeMipmapBuffers(this.mipmapDimensions, this.mipmapDatas);
        }
    }

    public static Dimension[] makeMipmapDimensions(int n, int n2, String str) {
        int ceilPowerOfTwo = TextureUtils.ceilPowerOfTwo(n);
        int ceilPowerOfTwo2 = TextureUtils.ceilPowerOfTwo(n2);
        if (ceilPowerOfTwo == n && ceilPowerOfTwo2 == n2) {
            ArrayList list = new ArrayList();
            int n3 = ceilPowerOfTwo;
            int n4 = ceilPowerOfTwo2;

            while (true) {
                n3 /= 2;
                n4 /= 2;
                if (n3 <= 0 && n4 <= 0) {
                    return (Dimension[])list.toArray(new Dimension[list.size()]);
                }

                if (n3 <= 0) {
                    n3 = 1;
                }

                if (n4 <= 0) {
                    n4 = 1;
                }

                int n5 = n3 * n4 * 4;
                Dimension dimension = new Dimension(n3, n4);
                list.add(dimension);
            }
        } else {
            Config.warn("Mipmaps not possible (power of 2 dimensions needed), texture: " + str + ", dim: " + n + "x" + n2);
            return new Dimension[0];
        }
    }

    public static int[][] generateMipMapData(int[] ints, int n, int n2, Dimension[] dimensions) {
        int[] ints2 = ints;
        int n3 = n;
        boolean flag = true;
        int[][] intses = new int[dimensions.length][];

        for (int i = 0; i < dimensions.length; i++) {
            Dimension dimension = dimensions[i];
            int width = dimension.width;
            int height = dimension.height;
            int[] ints3 = new int[width * height];
            intses[i] = ints3;
            int n4 = i + 1;
            if (flag) {
                for (int j = 0; j < width; j++) {
                    for (int k = 0; k < height; k++) {
                        int n5 = ints2[j * 2 + 0 + (k * 2 + 0) * n3];
                        int n6 = ints2[j * 2 + 1 + (k * 2 + 0) * n3];
                        int n7 = ints2[j * 2 + 1 + (k * 2 + 1) * n3];
                        int n8 = ints2[j * 2 + 0 + (k * 2 + 1) * n3];
                        int alphaBlend2 = alphaBlend(n5, n6, n7, n8);
                        ints3[j + k * width] = alphaBlend2;
                    }
                }
            }

            ints2 = ints3;
            n3 = width;
            if (width <= 1 || height <= 1) {
                flag = false;
            }
        }

        return intses;
    }

    public static int alphaBlend(int n, int n2, int n3, int n4) {
        int alphaBlend2 = alphaBlend(n, n2);
        int alphaBlend3 = alphaBlend(n3, n4);
        return alphaBlend(alphaBlend2, alphaBlend3);
    }

    public static int alphaBlend(int n, int n2) {
        int n3 = (n & 0xFF000000) >> 24 & 0xFF;
        int n4 = (n2 & 0xFF000000) >> 24 & 0xFF;
        int n5 = (n3 + n4) / 2;
        if (n3 == 0 && n4 == 0) {
            n3 = 1;
            n4 = 1;
        } else {
            if (n3 == 0) {
                n = n2;
                n5 /= 2;
            }

            if (n4 == 0) {
                n2 = n;
                n5 /= 2;
            }
        }

        int n6 = (n >> 16 & 0xFF) * n3;
        int n7 = (n >> 8 & 0xFF) * n3;
        int n8 = (n & 0xFF) * n3;
        int n9 = (n2 >> 16 & 0xFF) * n4;
        int n10 = (n2 >> 8 & 0xFF) * n4;
        int n11 = (n2 & 0xFF) * n4;
        int n12 = (n6 + n9) / (n3 + n4);
        int n13 = (n7 + n10) / (n3 + n4);
        int n14 = (n8 + n11) / (n3 + n4);
        return n5 << 24 | n12 << 16 | n13 << 8 | n14;
    }

    public int averageColor(int n, int n2) {
        int n3 = (n & 0xFF000000) >> 24 & 0xFF;
        int n4 = (n2 & 0xFF000000) >> 24 & 0xFF;
        return (n3 + n4 >> 1 << 24) + ((n & 16711422) + (n2 & 16711422) >> 1);
    }

    public static IntBuffer[] makeMipmapBuffers(Dimension[] dimensions, int[][] intses) {
        if (dimensions == null) {
            return null;
        }

        IntBuffer[] intBuffers = new IntBuffer[dimensions.length];

        for (int i = 0; i < dimensions.length; i++) {
            Dimension dimension = dimensions[i];
            int n = dimension.width * dimension.height;
            IntBuffer createDirectIntBuffer = GLAllocation.createDirectIntBuffer(n);
            int[] ints = intses[i];
            ((Buffer)createDirectIntBuffer).clear();
            createDirectIntBuffer.put(ints);
            ((Buffer)createDirectIntBuffer).clear();
            intBuffers[i] = createDirectIntBuffer;
        }

        return intBuffers;
    }

    public static void allocateMipmapTextures(int n, int n2, String str) {
        Dimension[] makeMipmapDimensions2 = makeMipmapDimensions(n, n2, str);

        for (int i = 0; i < makeMipmapDimensions2.length; i++) {
            Dimension dimension = makeMipmapDimensions2[i];
            int width = dimension.width;
            int height = dimension.height;
            int n3 = i + 1;
            GL11.glTexImage2D(3553, n3, 6408, width, height, 0, 32993, 33639, (IntBuffer)null);
        }
    }
}
