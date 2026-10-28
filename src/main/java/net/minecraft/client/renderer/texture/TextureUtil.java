package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Mipmaps;
import net.optifine.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class TextureUtil {
    public static Logger logger = LogManager.getLogger();
    public static IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
    public static DynamicTexture missingTexture = new DynamicTexture(16, 16);
    public static int[] missingTextureData = missingTexture.getTextureData();
    public static int intValue = -1;
    public static int intValue2 = -1;
    public static float floatValue = -1.0F;
    public static int[] intArray;

    public static int glGenTextures() {
        return GL11.glGenTextures();
    }

    public static void deleteTexture(int n) {
        GL11.glDeleteTextures(n);
    }

    public static int uploadTextureImage(int n, BufferedImage bufferedImage) {
        return uploadTextureImageAllocate(n, bufferedImage, false, false);
    }

    public static void uploadTexture(int n, int[] ints, int n2, int n3) {
        bindTexture(n);
        uploadTextureSub(0, ints, n2, n3, 0, 0, false, false, false);
    }

    public static int[][] generateMipmapData(int n, int n2, int[][] intses) {
        int[][] intses2 = new int[n + 1][];
        intses2[0] = intses[0];
        if (n > 0) {
            boolean flag = false;

            for (int i = 0; i < intses.length; i++) {
                if (intses[0][i] >> 24 == 0) {
                    flag = true;
                    break;
                }
            }

            for (int i = 1; i <= n; i++) {
                if (intses[i] != null) {
                    intses2[i] = intses[i];
                } else {
                    int[] ints = intses2[i - 1];
                    int[] ints2 = new int[ints.length >> 2];
                    int n3 = n2 >> i;
                    int n4 = ints2.length / n3;
                    int n5 = n3 << 1;

                    for (int j = 0; j < n3; j++) {
                        for (int k = 0; k < n4; k++) {
                            int n6 = 2 * (j + k * n5);
                            ints2[j + k * n3] = alphaBlend(ints[n6 + 0], ints[n6 + 1], ints[n6 + 0 + n5], ints[n6 + 1 + n5], flag);
                        }
                    }

                    intses2[i] = ints2;
                }
            }
        }

        return intses2;
    }

    public static int alphaBlend(int n, int n2, int n3, int n4, boolean flag) {
        return Mipmaps.alphaBlend(n, n2, n3, n4);
    }

    public static int computeInt(int n, int n2, int n3, int n4, int n5) {
        float pow = (float)Math.pow((n >> n5 & 0xFF) / 255.0F, 2.2);
        float pow2 = (float)Math.pow((n2 >> n5 & 0xFF) / 255.0F, 2.2);
        float pow3 = (float)Math.pow((n3 >> n5 & 0xFF) / 255.0F, 2.2);
        float pow4 = (float)Math.pow((n4 >> n5 & 0xFF) / 255.0F, 2.2);
        float pow5 = (float)Math.pow((pow + pow2 + pow3 + pow4) * 0.25, 0.45454545454545453);
        return (int)(pow5 * 255.0);
    }

    public static void uploadTextureMipmap(int[][] intses, int n, int n2, int n3, int n4, boolean flag, boolean flag2) {
        for (int i = 0; i < intses.length; i++) {
            int[] ints = intses[i];
            uploadTextureSub(i, ints, n >> i, n2 >> i, n3 >> i, n4 >> i, flag, flag2, intses.length > 1);
        }
    }

    public static void uploadTextureSub(int n, int[] ints, int n2, int n3, int n4, int n5, boolean flag, boolean flag2, boolean flag3) {
        int n6 = 4194304 / n2;
        setTextureScaling(flag, flag3);
        setTextureClamped(flag2);
        int n7 = 0;

        while (n7 < n2 * n3) {
            int n8 = n7 / n2;
            int min = Math.min(n6, n3 - n8);
            int n9 = n2 * min;
            copyToBufferPos(ints, n7, n9);
            GL11.glTexSubImage2D(3553, n, n4, n5 + n8, n2, min, 32993, 33639, dataBuffer);
            n7 += n2 * min;
        }
    }

    public static int uploadTextureImageAllocate(int n, BufferedImage bufferedImage, boolean flag, boolean flag2) {
        allocateTexture(n, bufferedImage.getWidth(), bufferedImage.getHeight());
        return uploadTextureImageSub(n, bufferedImage, 0, 0, flag, flag2);
    }

    public static void allocateTexture(int n, int n2, int n3) {
        allocateTextureImpl(n, 0, n2, n3, 1.0F);
    }

    public static void allocateTextureImpl(int n, int n2, int n3, int n4, float f) {
        Class<TextureUtil> class2 = TextureUtil.class;
        if (Reflector.SplashScreen.exists()) {
            class2 = Reflector.SplashScreen.getTargetClass();
        }

        synchronized (class2) {
            deleteTexture(n);
            bindTexture(n);
        }

        if (OpenGlHelper.anisotropicFilteringSupported) {
            GL11.glTexParameterf(3553, 34046, f);
        }

        if (n2 > 0) {
            GL11.glTexParameteri(3553, 33085, n2);
            GL11.glTexParameterf(3553, 33082, 0.0F);
            GL11.glTexParameterf(3553, 33083, n2);
            GL11.glTexParameterf(3553, 34049, 0.0F);
        }

        for (int i = 0; i <= n2; i++) {
            GL11.glTexImage2D(3553, i, 6408, n3 >> i, n4 >> i, 0, 32993, 33639, (IntBuffer)null);
        }
    }

    public static int uploadTextureImageSub(int n, BufferedImage bufferedImage, int n2, int n3, boolean flag, boolean flag2) {
        bindTexture(n);
        uploadTextureImageSubImpl(bufferedImage, n2, n3, flag, flag2);
        return n;
    }

    public static void uploadTextureImageSubImpl(BufferedImage bufferedImage, int n, int n2, boolean flag, boolean flag2) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int n3 = 4194304 / width;
        int[] ints = new int[n3 * width];
        setTextureBlurred(flag);
        setTextureClamped(flag2);

        for (int i = 0; i < width * height; i += width * n3) {
            int n4 = i / width;
            int min = Math.min(n3, height - n4);
            int n5 = width * min;
            bufferedImage.getRGB(0, n4, width, min, ints, 0, width);
            copyToBuffer(ints, n5);
            GL11.glTexSubImage2D(3553, 0, n, n2 + n4, width, min, 32993, 33639, dataBuffer);
        }
    }

    public static void setTextureClamped(boolean flag) {
        if (flag) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
    }

    public static void setTextureBlurred(boolean flag) {
        setTextureScaling(flag, false);
    }

    public static void saveAndSetTextureScaling(boolean flag, boolean flag2, float f) {
        intValue = GL11.glGetTexParameteri(3553, 10241);
        intValue2 = GL11.glGetTexParameteri(3553, 10240);
        floatValue = GL11.glGetTexParameterf(3553, 34046);
        setTextureScaling(flag, flag2);
        glTexParameterf(f);
    }

    public static void restoreTextureScaling() {
        if (intValue >= 0 && intValue2 >= 0 && floatValue >= 0.0F) {
            process(intValue, intValue2);
            glTexParameterf(floatValue);
            floatValue = -1.0F;
            intValue = -1;
            intValue2 = -1;
        }
    }

    public static void process(int n, int n2) {
        GL11.glTexParameteri(3553, 10241, n);
        GL11.glTexParameteri(3553, 10240, n2);
    }

    public static void glTexParameterf(float f) {
        GL11.glTexParameterf(3553, 34046, f);
    }

    public static void setTextureScaling(boolean flag, boolean flag2) {
        if (flag) {
            GL11.glTexParameteri(3553, 10241, flag2 ? 9987 : 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        } else {
            GL11.glTexParameteri(3553, 10241, flag2 ? 9986 : 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
        }
    }

    public static void copyToBuffer(int[] ints, int n) {
        copyToBufferPos(ints, 0, n);
    }

    public static void copyToBufferPos(int[] ints, int n, int n2) {
        int[] ints2 = ints;
        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            ints2 = updateAnaglyph(ints);
        }

        ((Buffer)dataBuffer).clear();
        dataBuffer.put(ints2, n, n2);
        ((Buffer)dataBuffer).position(0).limit(n2);
    }

    public static void bindTexture(int n) {
        GL11.glBindTexture(3553, n);
    }

    public static int[] readImageData(IResourceManager resourceManager, ResourceLocation resourceLocation) throws IOException {
        BufferedImage read = ImageIO.read(resourceManager.getResource(resourceLocation).getInputStream());
        if (read == null) {
            return null;
        }

        int width = read.getWidth();
        int height = read.getHeight();
        int[] ints = new int[width * height];
        read.getRGB(0, 0, width, height, ints, 0, width);
        return ints;
    }

    public static int[] updateAnaglyph(int[] ints) {
        int[] ints2 = new int[ints.length];

        for (int i = 0; i < ints.length; i++) {
            int n = ints[i] >> 24 & 0xFF;
            int n2 = ints[i] >> 16 & 0xFF;
            int n3 = ints[i] >> 8 & 0xFF;
            int n4 = ints[i] & 0xFF;
            int n5 = (n2 * 30 + n3 * 59 + n4 * 11) / 100;
            int n6 = (n2 * 30 + n3 * 70) / 100;
            int n7 = (n2 * 30 + n4 * 70) / 100;
            ints2[i] = n << 24 | n5 << 16 | n6 << 8 | n7;
        }

        return ints2;
    }

    public static int[] prepareAnisotropicData(int[] ints, int n, int n2, int n3) {
        int n4 = n + 2 * n3;

        for (int i = n2 - 1; i >= 0; i--) {
            int n5 = i * n;
            int n6 = n3 + (i + n3) * n4;

            for (int j = 0; j < n3; j += n) {
                int min = Math.min(n, n3 - j);
                System.arraycopy(ints, n5 + n - min, ints, n6 - j - min, min);
            }

            System.arraycopy(ints, n5, ints, n6, n);

            for (int j = 0; j < n3; j += n) {
                System.arraycopy(ints, n5, ints, n6 + n + j, Math.min(n, n3 - j));
            }
        }

        for (int i = 0; i < n3; i += n2) {
            int min = Math.min(n2, n3 - i);
            System.arraycopy(ints, (n3 + n2 - min) * n4, ints, (n3 - i - min) * n4, n4 * min);
        }

        for (int i = 0; i < n3; i += n2) {
            int min = Math.min(n2, n3 - i);
            System.arraycopy(ints, n3 * n4, ints, (n2 + n3 + i) * n4, n4 * min);
        }

        return ints;
    }

    public static void flipXY(int[] ints, int n, int n2) {
        int[] ints2 = new int[n];
        int n3 = n2 / 2;

        for (int i = 0; i < n3; i++) {
            System.arraycopy(ints, i * n, ints2, 0, n);
            System.arraycopy(ints, (n2 - 1 - i) * n, ints, i * n, n);
            System.arraycopy(ints2, 0, ints, (n2 - 1 - i) * n, n);
        }
    }

    public static void allocateTextureImpl(int n, int n2, int n3, int n4) {
        allocateTextureImpl(n, n2, n3, n4, 1.0F);
    }

    static {
        int n = -16777216;
        int n2 = -524040;
        int[] ints = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
        int[] ints2 = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
        int length = ints.length;

        for (int i = 0; i < 16; i++) {
            System.arraycopy(i < length ? ints : ints2, 0, missingTextureData, 16 * i, length);
            System.arraycopy(i < length ? ints2 : ints, 0, missingTextureData, 16 * i + length, length);
        }

        missingTexture.updateDynamicTexture();
        intArray = new int[4];
    }
}
