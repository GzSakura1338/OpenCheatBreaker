package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.Mipmaps;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class TextureUtil {
   public static Logger logger = LogManager.getLogger();
   public static IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
   public static DynamicTexture missingTexture = new DynamicTexture(16, 16);
   public static int[] missingTextureData = missingTexture.getTextureData();
   public static int[] dataArray = new int[4194304];
   public static int[] mipmapBuffer;

   public static int glGenTextures() {
      return GlStateManager.generateTexture();
   }

   public static void deleteTexture(int textureId) {
      GlStateManager.deleteTexture(textureId);
   }

   public static int uploadTextureImage(int n, BufferedImage bufferedImage) {
      return uploadTextureImageAllocate(n, bufferedImage, false, false);
   }

   public static void uploadTexture(int textureId, int[] ints, int n, int n2) {
      bindTexture(textureId);
      uploadTextureSub(0, ints, n, n2, 0, 0, false, false, false);
   }

   public static int[][] generateMipmapData(int n, int n2, int[][] intses) {
      int[][] intses2 = new int[n + 1][];
      intses2[0] = intses[0];
      if (n > 0) {
         boolean flag = false;

         for (int i = 0; i < intses[0].length; i++) {
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
                     ints2[j + k * n3] = blendColors(ints[n6 + 0], ints[n6 + 1], ints[n6 + 0 + n5], ints[n6 + 1 + n5], flag);
                  }
               }

               intses2[i] = ints2;
            }
         }
      }

      return intses2;
   }

   public static int blendColors(int n, int n2, int n3, int n4, boolean flag) {
      return Mipmaps.alphaBlend(n, n2, n3, n4);
   }

   public static int blendColorComponent(int n, int n2, int n3, int n4, int n5) {
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
      setTextureBlurMipmap(flag, flag3);
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
      allocateTextureImpl(n, 0, n2, n3);
   }

   public static void allocateTextureImpl(int n, int n2, int n3, int n4) {
      Class<TextureUtil> class2 = TextureUtil.class;
      if (Reflector.SplashScreen.exists()) {
         class2 = Reflector.SplashScreen.getTargetClass();
      }

      synchronized (class2) {
         deleteTexture(n);
         bindTexture(n);
      }

      if (n2 >= 0) {
         GL11.glTexParameteri(3553, 33085, n2);
         GL11.glTexParameterf(3553, 33082, 0.0F);
         GL11.glTexParameterf(3553, 33083, n2);
         GL11.glTexParameterf(3553, 34049, 0.0F);
      }

      for (int i = 0; i <= n2; i++) {
         GL11.glTexImage2D(3553, i, 6408, n3 >> i, n4 >> i, 0, 32993, 33639, (IntBuffer)null);
      }
   }

   public static int uploadTextureImageSub(int textureId, BufferedImage bufferedImage, int n, int n2, boolean flag, boolean flag2) {
      bindTexture(textureId);
      uploadTextureImageSubImpl(bufferedImage, n, n2, flag, flag2);
      return textureId;
   }

   public static void uploadTextureImageSubImpl(BufferedImage bufferedImage, int n, int n2, boolean flag, boolean flag2) {
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      int n3 = 4194304 / width;
      int[] dataArray2 = dataArray;
      setTextureBlurred(flag);
      setTextureClamped(flag2);

      for (int i = 0; i < width * height; i += width * n3) {
         int n4 = i / width;
         int min = Math.min(n3, height - n4);
         int n5 = width * min;
         bufferedImage.getRGB(0, n4, width, min, dataArray2, 0, width);
         copyToBuffer(dataArray2, n5);
         GL11.glTexSubImage2D(3553, 0, n, n2 + n4, width, min, 32993, 33639, dataBuffer);
      }
   }

   public static void setTextureClamped(boolean flag) {
      if (flag) {
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
      } else {
         GL11.glTexParameteri(3553, 10242, 10497);
         GL11.glTexParameteri(3553, 10243, 10497);
      }
   }

   public static void setTextureBlurred(boolean flag) {
      setTextureBlurMipmap(flag, false);
   }

   public static void setTextureBlurMipmap(boolean flag, boolean flag2) {
      if (flag) {
         GL11.glTexParameteri(3553, 10241, flag2 ? 9987 : 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
      } else {
         int mipmapType = Config.getMipmapType();
         GL11.glTexParameteri(3553, 10241, flag2 ? mipmapType : 9728);
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
      GlStateManager.bindTexture(n);
   }

   public static int[] readImageData(IResourceManager resourceManager, ResourceLocation imageLocation) throws IOException {
      BufferedImage readBufferedImage2 = readBufferedImage(resourceManager.getResource(imageLocation).getInputStream());
      if (readBufferedImage2 == null) {
         return null;
      }

      int width = readBufferedImage2.getWidth();
      int height = readBufferedImage2.getHeight();
      int[] ints = new int[width * height];
      readBufferedImage2.getRGB(0, 0, width, height, ints, 0, width);
      return ints;
   }

   public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException {
      if (imageStream == null) {
         return null;
      }

      BufferedImage bufferedImage;
      try {
         bufferedImage = ImageIO.read(imageStream);
      } finally {
         IOUtils.closeQuietly(imageStream);
      }

      return bufferedImage;
   }

   public static int[] updateAnaglyph(int[] ints) {
      int[] ints2 = new int[ints.length];

      for (int i = 0; i < ints.length; i++) {
         ints2[i] = anaglyphColor(ints[i]);
      }

      return ints2;
   }

   public static int anaglyphColor(int n) {
      int n2 = n >> 24 & 0xFF;
      int n3 = n >> 16 & 0xFF;
      int n4 = n >> 8 & 0xFF;
      int n5 = n & 0xFF;
      int n6 = (n3 * 30 + n4 * 59 + n5 * 11) / 100;
      int n7 = (n3 * 30 + n4 * 70) / 100;
      int n8 = (n3 * 30 + n5 * 70) / 100;
      return n2 << 24 | n6 << 16 | n7 << 8 | n8;
   }

   public static void processPixelValues(int[] ints, int n, int n2) {
      int[] ints2 = new int[n];
      int n3 = n2 / 2;

      for (int i = 0; i < n3; i++) {
         System.arraycopy(ints, i * n, ints2, 0, n);
         System.arraycopy(ints, (n2 - 1 - i) * n, ints, i * n, n);
         System.arraycopy(ints2, 0, ints, (n2 - 1 - i) * n, n);
      }
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
      mipmapBuffer = new int[4];
   }
}
