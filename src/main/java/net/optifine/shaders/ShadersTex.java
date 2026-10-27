package net.optifine.shaders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ShadersTex {
   public static final int initialBufferSize = 1048576;
   public static ByteBuffer byteBuffer = BufferUtils.createByteBuffer(4194304);
   public static IntBuffer intBuffer = byteBuffer.asIntBuffer();
   public static int[] intArray = new int[1048576];
   public static final int defNormTexColor = -8421377;
   public static Map<Integer, MultiTexID> multiTexMap = new HashMap<>();
   public static final int CONSTANT_0 = 0;
   public static final int CONSTANT_0_2 = 0;

   public static IntBuffer getIntBuffer(int n) {
      if (intBuffer.capacity() < n) {
         int roundUpPOT2 = roundUpPOT(n);
         byteBuffer = BufferUtils.createByteBuffer(roundUpPOT2 * 4);
         intBuffer = byteBuffer.asIntBuffer();
      }

      return intBuffer;
   }

   public static int[] getIntArray(int n) {
      if (intArray == null) {
         intArray = new int[1048576];
      }

      if (intArray.length < n) {
         intArray = new int[roundUpPOT(n)];
      }

      return intArray;
   }

   public static int roundUpPOT(int n) {
      int n2 = n - 1;
      n2 |= n2 >> 1;
      n2 |= n2 >> 2;
      n2 |= n2 >> 4;
      n2 |= n2 >> 8;
      n2 |= n2 >> 16;
      return n2 + 1;
   }

   public static int log2(int n) {
      int n2 = 0;
      if ((n & -65536) != 0) {
         n2 += 16;
         n >>= 16;
      }

      if ((n & 0xFF00) != 0) {
         n2 += 8;
         n >>= 8;
      }

      if ((n & 240) != 0) {
         n2 += 4;
         n >>= 4;
      }

      if ((n & 6) != 0) {
         n2 += 2;
         n >>= 2;
      }

      if ((n & 2) != 0) {
         n2++;
      }

      return n2;
   }

   public static IntBuffer fillIntBuffer(int n, int n2) {
      int[] intArray2 = getIntArray(n);
      IntBuffer intBuffer2 = getIntBuffer(n);
      Arrays.fill(intArray, 0, n, n2);
      intBuffer.put(intArray, 0, n);
      return intBuffer;
   }

   public static int[] createAIntImage(int n) {
      int[] ints = new int[n * 3];
      Arrays.fill(ints, 0, n, 0);
      Arrays.fill(ints, n, n * 2, -8421377);
      Arrays.fill(ints, n * 2, n * 3, 0);
      return ints;
   }

   public static int[] createAIntImage(int n, int n2) {
      int[] ints = new int[n * 3];
      Arrays.fill(ints, 0, n, n2);
      Arrays.fill(ints, n, n * 2, -8421377);
      Arrays.fill(ints, n * 2, n * 3, 0);
      return ints;
   }

   public static MultiTexID getMultiTexID(AbstractTexture abstractTexture) {
      MultiTexID multiTex = abstractTexture.multiTex;
      if (multiTex == null) {
         int glTextureId = abstractTexture.getGlTextureId();
         multiTex = multiTexMap.get(glTextureId);
         if (multiTex == null) {
            multiTex = new MultiTexID(glTextureId, GL11.glGenTextures(), GL11.glGenTextures());
            multiTexMap.put(glTextureId, multiTex);
         }

         abstractTexture.multiTex = multiTex;
      }

      return multiTex;
   }

   public static void deleteTextures(AbstractTexture abstractTexture, int n) {
      MultiTexID multiTex = abstractTexture.multiTex;
      if (multiTex != null) {
         abstractTexture.multiTex = null;
         multiTexMap.remove(multiTex.base);
         GlStateManager.deleteTexture(multiTex.norm);
         GlStateManager.deleteTexture(multiTex.spec);
         if (multiTex.base != n) {
            SMCLog.warning("Error : MultiTexID.base mismatch: " + multiTex.base + ", texid: " + n);
            GlStateManager.deleteTexture(multiTex.base);
         }
      }
   }

   public static void bindNSTextures(int n, int n2) {
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         GlStateManager.setActiveTexture(33986);
         GlStateManager.bindTexture(n);
         GlStateManager.setActiveTexture(33987);
         GlStateManager.bindTexture(n2);
         GlStateManager.setActiveTexture(33984);
      }
   }

   public static void bindNSTextures(MultiTexID multiTexID) {
      bindNSTextures(multiTexID.norm, multiTexID.spec);
   }

   public static void bindTextures(int n, int n2, int n3) {
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         GlStateManager.setActiveTexture(33986);
         GlStateManager.bindTexture(n2);
         GlStateManager.setActiveTexture(33987);
         GlStateManager.bindTexture(n3);
         GlStateManager.setActiveTexture(33984);
      }

      GlStateManager.bindTexture(n);
   }

   public static void bindTextures(MultiTexID multiTexID) {
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         if (Shaders.configNormalMap) {
            GlStateManager.setActiveTexture(33986);
            GlStateManager.bindTexture(multiTexID.norm);
         }

         if (Shaders.configSpecularMap) {
            GlStateManager.setActiveTexture(33987);
            GlStateManager.bindTexture(multiTexID.spec);
         }

         GlStateManager.setActiveTexture(33984);
      }

      GlStateManager.bindTexture(multiTexID.base);
   }

   public static void bindTexture(ITextureObject textureObject) {
      int glTextureId = textureObject.getGlTextureId();
      bindTextures(textureObject.getMultiTexID());
      if (GlStateManager.getActiveTextureUnit() == 33984) {
         int atlasSizeX = Shaders.atlasSizeX;
         int atlasSizeY = Shaders.atlasSizeY;
         if (textureObject instanceof TextureMap) {
            Shaders.atlasSizeX = ((TextureMap)textureObject).atlasWidth;
            Shaders.atlasSizeY = ((TextureMap)textureObject).atlasHeight;
         } else {
            Shaders.atlasSizeX = 0;
            Shaders.atlasSizeY = 0;
         }

         if (Shaders.atlasSizeX != atlasSizeX || Shaders.atlasSizeY != atlasSizeY) {
            Shaders.uniform_atlasSize.setValue(Shaders.atlasSizeX, Shaders.atlasSizeY);
         }
      }
   }

   public static void bindTextures(int n) {
      MultiTexID multiTexID = multiTexMap.get(n);
      bindTextures(multiTexID);
   }

   public static void initDynamicTexture(int n, int n2, int n3, DynamicTexture dynamicTexture) {
      MultiTexID multiTexID = dynamicTexture.getMultiTexID();
      int[] textureData = dynamicTexture.getTextureData();
      int n4 = n2 * n3;
      Arrays.fill(textureData, n4, n4 * 2, -8421377);
      Arrays.fill(textureData, n4 * 2, n4 * 3, 0);
      TextureUtil.allocateTexture(multiTexID.base, n2, n3);
      TextureUtil.setTextureBlurMipmap(false, false);
      TextureUtil.setTextureClamped(false);
      TextureUtil.allocateTexture(multiTexID.norm, n2, n3);
      TextureUtil.setTextureBlurMipmap(false, false);
      TextureUtil.setTextureClamped(false);
      TextureUtil.allocateTexture(multiTexID.spec, n2, n3);
      TextureUtil.setTextureBlurMipmap(false, false);
      TextureUtil.setTextureClamped(false);
      GlStateManager.bindTexture(multiTexID.base);
   }

   public static void updateDynamicTexture(int n, int[] ints, int n2, int n3, DynamicTexture dynamicTexture) {
      MultiTexID multiTexID = dynamicTexture.getMultiTexID();
      GlStateManager.bindTexture(multiTexID.base);
      updateDynTexSubImage1(ints, n2, n3, 0, 0, 0);
      GlStateManager.bindTexture(multiTexID.norm);
      updateDynTexSubImage1(ints, n2, n3, 0, 0, 1);
      GlStateManager.bindTexture(multiTexID.spec);
      updateDynTexSubImage1(ints, n2, n3, 0, 0, 2);
      GlStateManager.bindTexture(multiTexID.base);
   }

   public static void updateDynTexSubImage1(int[] ints, int n, int n2, int n3, int n4, int n5) {
      int n6 = n * n2;
      IntBuffer intBuffer = getIntBuffer(n6);
      ((Buffer)intBuffer).clear();
      int n7 = n5 * n6;
      if (ints.length >= n7 + n6) {
         ((Buffer)intBuffer.put(ints, n7, n6)).position(0).limit(n6);
         GL11.glTexSubImage2D(3553, 0, n3, n4, n, n2, 32993, 33639, intBuffer);
         ((Buffer)intBuffer).clear();
      }
   }

   public static ITextureObject createDefaultTexture() {
      DynamicTexture dynamicTexture = new DynamicTexture(1, 1);
      dynamicTexture.getTextureData()[0] = -1;
      dynamicTexture.updateDynamicTexture();
      return dynamicTexture;
   }

   public static void allocateTextureMap(int n, int n2, int n3, int n4, Stitcher stitcher, TextureMap textureMap) {
      SMCLog.info("allocateTextureMap " + n2 + " " + n3 + " " + n4 + " ");
      textureMap.atlasWidth = n3;
      textureMap.atlasHeight = n4;
      MultiTexID multiTexID = getMultiTexID(textureMap);
      TextureUtil.allocateTextureImpl(multiTexID.base, n2, n3, n4);
      if (Shaders.configNormalMap) {
         TextureUtil.allocateTextureImpl(multiTexID.norm, n2, n3, n4);
      }

      if (Shaders.configSpecularMap) {
         TextureUtil.allocateTextureImpl(multiTexID.spec, n2, n3, n4);
      }

      GlStateManager.bindTexture(n);
   }

   public static void uploadTexSubForLoadAtlas(TextureMap textureMap, String str, int[][] intses, int n, int n2, int n3, int n4, boolean flag, boolean flag2) {
      MultiTexID multiTex = textureMap.multiTex;
      TextureUtil.uploadTextureMipmap(intses, n, n2, n3, n4, flag, flag2);
      boolean flag3 = false;
      if (Shaders.configNormalMap) {
         int[][] readImageAndMipmaps2 = readImageAndMipmaps(textureMap, str + "_n", n, n2, intses.length, flag3, -8421377);
         GlStateManager.bindTexture(multiTex.norm);
         TextureUtil.uploadTextureMipmap(readImageAndMipmaps2, n, n2, n3, n4, flag, flag2);
      }

      if (Shaders.configSpecularMap) {
         int[][] readImageAndMipmaps2 = readImageAndMipmaps(textureMap, str + "_s", n, n2, intses.length, flag3, 0);
         GlStateManager.bindTexture(multiTex.spec);
         TextureUtil.uploadTextureMipmap(readImageAndMipmaps2, n, n2, n3, n4, flag, flag2);
      }

      GlStateManager.bindTexture(multiTex.base);
   }

   public static int[][] readImageAndMipmaps(TextureMap textureMap, String str, int n, int n2, int n3, boolean flag, int n4) {
      MultiTexID multiTex = textureMap.multiTex;
      int[][] intses = new int[n3][];
      int[] ints;
      intses[0] = ints = new int[n * n2];
      boolean flag2 = false;
      BufferedImage readImage2 = readImage(textureMap.completeResourceLocation(new ResourceLocation(str)));
      if (readImage2 != null) {
         int width = readImage2.getWidth();
         int height = readImage2.getHeight();
         if (width + (flag ? 16 : 0) == n) {
            flag2 = true;
            readImage2.getRGB(0, 0, width, width, ints, 0, width);
         }
      }

      if (!flag2) {
         Arrays.fill(ints, n4);
      }

      GlStateManager.bindTexture(multiTex.spec);
      return genMipmapsSimple(intses.length - 1, n, intses);
   }

   public static BufferedImage readImage(ResourceLocation resourceLocation) {
      try {
         if (!Config.hasResource(resourceLocation)) {
            return null;
         }

         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return null;
         }

         BufferedImage read = ImageIO.read(resourceStream);
         resourceStream.close();
         return read;
      } catch (IOException e) {
         return null;
      }
   }

   public static int[][] genMipmapsSimple(int n, int n2, int[][] intses) {
      for (int i = 1; i <= n; i++) {
         if (intses[i] == null) {
            int n3 = n2 >> i;
            int n4 = n3 * 2;
            int[] ints = intses[i - 1];
            int[] ints2 = intses[i] = new int[n3 * n3];

            for (int j = 0; j < n3; j++) {
               for (int k = 0; k < n3; k++) {
                  int n5 = j * 2 * n4 + k * 2;
                  ints2[j * n3 + k] = blend4Simple(ints[n5], ints[n5 + 1], ints[n5 + n4], ints[n5 + n4 + 1]);
               }
            }
         }
      }

      return intses;
   }

   public static void uploadTexSub1(int[][] intses, int n, int n2, int n3, int n4, int n5) {
      int n6 = n * n2;
      IntBuffer intBuffer = getIntBuffer(n6);
      int length = intses.length;
      int n7 = 0;
      int n8 = n;
      int n9 = n2;
      int n10 = n3;
      int n11 = n4;

      while (n8 > 0 && n9 > 0 && n7 < length) {
         int n12 = n8 * n9;
         int[] ints = intses[n7];
         ((Buffer)intBuffer).clear();
         if (ints.length >= n12 * (n5 + 1)) {
            ((Buffer)intBuffer.put(ints, n12 * n5, n12)).position(0).limit(n12);
            GL11.glTexSubImage2D(3553, n7, n10, n11, n8, n9, 32993, 33639, intBuffer);
         }

         n8 >>= 1;
         n9 >>= 1;
         n10 >>= 1;
         n11 >>= 1;
         n7++;
      }

      ((Buffer)intBuffer).clear();
   }

   public static int blend4Alpha(int n, int n2, int n3, int n4) {
      int n5 = n >>> 24 & 0xFF;
      int n6 = n2 >>> 24 & 0xFF;
      int n7 = n3 >>> 24 & 0xFF;
      int n8 = n4 >>> 24 & 0xFF;
      int n9 = n5 + n6 + n7 + n8;
      int n10 = (n9 + 2) / 4;
      int n11;
      if (n9 != 0) {
         n11 = n9;
      } else {
         n11 = 4;
         n5 = 1;
         n6 = 1;
         n7 = 1;
         n8 = 1;
      }

      int n12 = (n11 + 1) / 2;
      return n10 << 24
         | ((n >>> 16 & 0xFF) * n5 + (n2 >>> 16 & 0xFF) * n6 + (n3 >>> 16 & 0xFF) * n7 + (n4 >>> 16 & 0xFF) * n8 + n12) / n11 << 16
         | ((n >>> 8 & 0xFF) * n5 + (n2 >>> 8 & 0xFF) * n6 + (n3 >>> 8 & 0xFF) * n7 + (n4 >>> 8 & 0xFF) * n8 + n12) / n11 << 8
         | ((n >>> 0 & 0xFF) * n5 + (n2 >>> 0 & 0xFF) * n6 + (n3 >>> 0 & 0xFF) * n7 + (n4 >>> 0 & 0xFF) * n8 + n12) / n11 << 0;
   }

   public static int blend4Simple(int n, int n2, int n3, int n4) {
      return ((n >>> 24 & 0xFF) + (n2 >>> 24 & 0xFF) + (n3 >>> 24 & 0xFF) + (n4 >>> 24 & 0xFF) + 2) / 4 << 24
         | ((n >>> 16 & 0xFF) + (n2 >>> 16 & 0xFF) + (n3 >>> 16 & 0xFF) + (n4 >>> 16 & 0xFF) + 2) / 4 << 16
         | ((n >>> 8 & 0xFF) + (n2 >>> 8 & 0xFF) + (n3 >>> 8 & 0xFF) + (n4 >>> 8 & 0xFF) + 2) / 4 << 8
         | ((n >>> 0 & 0xFF) + (n2 >>> 0 & 0xFF) + (n3 >>> 0 & 0xFF) + (n4 >>> 0 & 0xFF) + 2) / 4 << 0;
   }

   public static boolean isSemiTransparent(int[] ints, int n, int n2) {
      int n3 = n * n2;
      if (ints[0] >>> 24 == 255 && ints[n3 - 1] == 0) {
         return true;
      }

      for (int i = 0; i < n3; i++) {
         int n4 = ints[i] >>> 24;
         if (n4 != 0 && n4 != 255) {
            return true;
         }
      }

      return false;
   }

   public static void updateSubTex1(int[] ints, int n, int n2, int n3, int n4) {
      int n5 = 0;
      int n6 = n;
      int n7 = n2;
      int n8 = n3;

      for (int i = n4; n6 > 0 && n7 > 0; i /= 2) {
         GL11.glCopyTexSubImage2D(3553, n5, n8, i, 0, 0, n6, n7);
         n5++;
         n6 /= 2;
         n7 /= 2;
         n8 /= 2;
      }
   }

   public static void setupTexture(MultiTexID multiTexID, int[] ints, int n, int n2, boolean flag, boolean flag2) {
      int n3 = flag ? 9729 : 9728;
      int n4 = flag2 ? 33071 : 10497;
      int n5 = n * n2;
      IntBuffer intBuffer = getIntBuffer(n5);
      ((Buffer)intBuffer).clear();
      ((Buffer)intBuffer.put(ints, 0, n5)).position(0).limit(n5);
      GlStateManager.bindTexture(multiTexID.base);
      GL11.glTexImage2D(3553, 0, 6408, n, n2, 0, 32993, 33639, intBuffer);
      GL11.glTexParameteri(3553, 10241, n3);
      GL11.glTexParameteri(3553, 10240, n3);
      GL11.glTexParameteri(3553, 10242, n4);
      GL11.glTexParameteri(3553, 10243, n4);
      ((Buffer)intBuffer.put(ints, n5, n5)).position(0).limit(n5);
      GlStateManager.bindTexture(multiTexID.norm);
      GL11.glTexImage2D(3553, 0, 6408, n, n2, 0, 32993, 33639, intBuffer);
      GL11.glTexParameteri(3553, 10241, n3);
      GL11.glTexParameteri(3553, 10240, n3);
      GL11.glTexParameteri(3553, 10242, n4);
      GL11.glTexParameteri(3553, 10243, n4);
      ((Buffer)intBuffer.put(ints, n5 * 2, n5)).position(0).limit(n5);
      GlStateManager.bindTexture(multiTexID.spec);
      GL11.glTexImage2D(3553, 0, 6408, n, n2, 0, 32993, 33639, intBuffer);
      GL11.glTexParameteri(3553, 10241, n3);
      GL11.glTexParameteri(3553, 10240, n3);
      GL11.glTexParameteri(3553, 10242, n4);
      GL11.glTexParameteri(3553, 10243, n4);
      GlStateManager.bindTexture(multiTexID.base);
   }

   public static void updateSubImage(MultiTexID multiTexID, int[] ints, int n, int n2, int n3, int n4, boolean flag, boolean flag2) {
      int n5 = n * n2;
      IntBuffer intBuffer = getIntBuffer(n5);
      ((Buffer)intBuffer).clear();
      intBuffer.put(ints, 0, n5);
      ((Buffer)intBuffer).position(0).limit(n5);
      GlStateManager.bindTexture(multiTexID.base);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, n3, n4, n, n2, 32993, 33639, intBuffer);
      if (ints.length == n5 * 3) {
         ((Buffer)intBuffer).clear();
         ((Buffer)intBuffer.put(ints, n5, n5)).position(0);
         ((Buffer)intBuffer).position(0).limit(n5);
      }

      GlStateManager.bindTexture(multiTexID.norm);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, n3, n4, n, n2, 32993, 33639, intBuffer);
      if (ints.length == n5 * 3) {
         ((Buffer)intBuffer).clear();
         intBuffer.put(ints, n5 * 2, n5);
         ((Buffer)intBuffer).position(0).limit(n5);
      }

      GlStateManager.bindTexture(multiTexID.spec);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, n3, n4, n, n2, 32993, 33639, intBuffer);
      GlStateManager.setActiveTexture(33984);
   }

   public static ResourceLocation getNSMapLocation(ResourceLocation resourceLocation, String str) {
      if (resourceLocation == null) {
         return null;
      }

      String resourcePath = resourceLocation.getResourcePath();
      String[] split = resourcePath.split(".png");
      String str2 = split[0];
      return new ResourceLocation(resourceLocation.getResourceDomain(), str2 + "_" + str + ".png");
   }

   public static void loadNSMap(IResourceManager resourceManager, ResourceLocation resourceLocation, int n, int n2, int[] ints) {
      if (Shaders.configNormalMap) {
         loadNSMap1(resourceManager, getNSMapLocation(resourceLocation, "n"), n, n2, ints, n * n2, -8421377);
      }

      if (Shaders.configSpecularMap) {
         loadNSMap1(resourceManager, getNSMapLocation(resourceLocation, "s"), n, n2, ints, n * n2 * 2, 0);
      }
   }

   public static void loadNSMap1(IResourceManager resourceManager, ResourceLocation resourceLocation, int n, int n2, int[] ints, int n3, int n4) {
      if (!loadNSMapFile(resourceManager, resourceLocation, n, n2, ints, n3)) {
         Arrays.fill(ints, n3, n3 + n * n2, n4);
      }
   }

   public static boolean loadNSMapFile(IResourceManager resourceManager, ResourceLocation resourceLocation, int n, int n2, int[] ints, int n3) {
      if (resourceLocation == null) {
         return false;
      }

      try {
         IResource resource = resourceManager.getResource(resourceLocation);
         BufferedImage read = ImageIO.read(resource.getInputStream());
         if (read == null) {
            return false;
         } else if (read.getWidth() == n && read.getHeight() == n2) {
            read.getRGB(0, 0, n, n2, ints, n3, n);
            return true;
         } else {
            return false;
         }
      } catch (IOException e) {
         return false;
      }
   }

   public static int loadSimpleTexture(int n, BufferedImage bufferedImage, boolean flag, boolean flag2, IResourceManager resourceManager, ResourceLocation resourceLocation, MultiTexID multiTexID) {
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      int n2 = width * height;
      int[] intArray = getIntArray(n2 * 3);
      bufferedImage.getRGB(0, 0, width, height, intArray, 0, width);
      loadNSMap(resourceManager, resourceLocation, width, height, intArray);
      setupTexture(multiTexID, intArray, width, height, flag, flag2);
      return n;
   }

   public static void mergeImage(int[] ints, int n, int n2, int n3) {
   }

   public static int blendColor(int n, int n2, int n3) {
      int n4 = 255 - n3;
      return ((n >>> 24 & 0xFF) * n3 + (n2 >>> 24 & 0xFF) * n4) / 255 << 24
         | ((n >>> 16 & 0xFF) * n3 + (n2 >>> 16 & 0xFF) * n4) / 255 << 16
         | ((n >>> 8 & 0xFF) * n3 + (n2 >>> 8 & 0xFF) * n4) / 255 << 8
         | ((n >>> 0 & 0xFF) * n3 + (n2 >>> 0 & 0xFF) * n4) / 255 << 0;
   }

   public static void loadLayeredTexture(LayeredTexture layeredTexture, IResourceManager resourceManager, List list) {
      int n = 0;
      int n2 = 0;
      int n3 = 0;
      int[] ints = null;

      for (Object obj : list) {
         if (obj != null) {
            try {
               ResourceLocation resourceLocation = new ResourceLocation((String)obj);
               InputStream inputStream = resourceManager.getResource(resourceLocation).getInputStream();
               BufferedImage read = ImageIO.read(inputStream);
               if (n3 == 0) {
                  n = read.getWidth();
                  n2 = read.getHeight();
                  n3 = n * n2;
                  ints = createAIntImage(n3, 0);
               }

               int[] intArray = getIntArray(n3 * 3);
               read.getRGB(0, 0, n, n2, intArray, 0, n);
               loadNSMap(resourceManager, resourceLocation, n, n2, intArray);

               for (int i = 0; i < n3; i++) {
                  int n4 = intArray[i] >>> 24 & 0xFF;
                  ints[n3 * 0 + i] = blendColor(intArray[n3 * 0 + i], ints[n3 * 0 + i], n4);
                  ints[n3 * 1 + i] = blendColor(intArray[n3 * 1 + i], ints[n3 * 1 + i], n4);
                  ints[n3 * 2 + i] = blendColor(intArray[n3 * 2 + i], ints[n3 * 2 + i], n4);
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }

      setupTexture(layeredTexture.getMultiTexID(), ints, n, n2, false, false);
   }

   public static void updateTextureMinMagFilter() {
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      ITextureObject texture = textureManager.getTexture(TextureMap.locationBlocksTexture);
      if (texture != null) {
         MultiTexID multiTexID = texture.getMultiTexID();
         GlStateManager.bindTexture(multiTexID.base);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilB]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilB]);
         GlStateManager.bindTexture(multiTexID.norm);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilN]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilN]);
         GlStateManager.bindTexture(multiTexID.spec);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilS]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilS]);
         GlStateManager.bindTexture(0);
      }
   }

   public static int[][] getFrameTexData(int[][] intses, int n, int n2, int n3) {
      int length = intses.length;
      int[][] intses2 = new int[length][];

      for (int i = 0; i < length; i++) {
         int[] ints = intses[i];
         if (ints != null) {
            int n4 = (n >> i) * (n2 >> i);
            int[] ints2 = new int[n4 * 3];
            intses2[i] = ints2;
            int n5 = ints.length / 3;
            int n6 = n4 * n3;
            int n7 = 0;
            System.arraycopy(ints, n6, ints2, n7, n4);
            n6 += n5;
            n7 += n4;
            System.arraycopy(ints, n6, ints2, n7, n4);
            n6 += n5;
            n7 += n4;
            System.arraycopy(ints, n6, ints2, n7, n4);
         }
      }

      return intses2;
   }

   public static int[][] prepareAF(TextureAtlasSprite textureAtlasSprite, int[][] intses, int n, int n2) {
      boolean flag = true;
      return intses;
   }

   public static void fixTransparentColor(TextureAtlasSprite textureAtlasSprite, int[] ints) {
   }

   public static void process(int[] ints, int n, int n2, int n3) {
      Math.min(n2, n3);
      int n4 = n;
      int n5 = n2;
      int n6 = n3;
      int n7 = 0;
      int n8 = 0;
      int n9 = 0;
      int n10 = 0;

      while (n5 > 1 && n6 > 1) {
         n7 = n4 + n5 * n6;
         n8 = n5 / 2;
         n9 = n6 / 2;

         for (int i = 0; i < n9; i++) {
            int n11 = n7 + i * n8;
            int n12 = n4 + i * 2 * n5;

            for (int j = 0; j < n8; j++) {
               ints[n11 + j] = blend4Alpha(
                  ints[n12 + j * 2], ints[n12 + j * 2 + 1], ints[n12 + n5 + j * 2], ints[n12 + n5 + j * 2 + 1]
               );
            }
         }

         n10++;
         n5 = n8;
         n6 = n9;
         n4 = n7;
      }

      while (n10 > 0) {
         n5 = n2 >> --n10;
         n6 = n3 >> n10;
         n4 = n7 - n5 * n6;
         int n11 = n4;

         for (int i = 0; i < n6; i++) {
            for (int j = 0; j < n5; j++) {
               if (ints[n11] == 0) {
                  ints[n11] = ints[n7 + i / 2 * n8 + j / 2] & 16777215;
               }

               n11++;
            }
         }

         n7 = n4;
         n8 = n5;
      }
   }

   public static void process2(int[] ints, int n, int n2, int n3) {
      Math.min(n2, n3);
      int n4 = n;
      int n5 = n2;
      int n6 = n3;
      int n7 = 0;
      int n8 = 0;
      int n9 = 0;
      int n10 = 0;

      while (n5 > 1 && n6 > 1) {
         n7 = n4 + n5 * n6;
         n8 = n5 / 2;
         n9 = n6 / 2;

         for (int i = 0; i < n9; i++) {
            int n11 = n7 + i * n8;
            int n12 = n4 + i * 2 * n5;

            for (int j = 0; j < n8; j++) {
               ints[n11 + j] = blend4Simple(
                  ints[n12 + j * 2], ints[n12 + j * 2 + 1], ints[n12 + n5 + j * 2], ints[n12 + n5 + j * 2 + 1]
               );
            }
         }

         n10++;
         n5 = n8;
         n6 = n9;
         n4 = n7;
      }

      while (n10 > 0) {
         n5 = n2 >> --n10;
         n6 = n3 >> n10;
         n4 = n7 - n5 * n6;
         int n11 = n4;

         for (int i = 0; i < n6; i++) {
            for (int j = 0; j < n5; j++) {
               if (ints[n11] == 0) {
                  ints[n11] = ints[n7 + i / 2 * n8 + j / 2] & 16777215;
               }

               n11++;
            }
         }

         n7 = n4;
         n8 = n5;
      }
   }
}
