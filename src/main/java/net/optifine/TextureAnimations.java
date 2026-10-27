package net.optifine;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.TextureUtils;

public class TextureAnimations {
   public static TextureAnimation[] textureAnimations = null;
   public static int countAnimationsActive = 0;
   public static int frameCountAnimations = 0;

   public static void reset() {
      textureAnimations = null;
   }

   public static void update() {
      textureAnimations = null;
      countAnimationsActive = 0;
      IResourcePack[] resourcePacks = Config.getResourcePacks();
      textureAnimations = getTextureAnimations(resourcePacks);
      updateAnimations();
   }

   public static void updateAnimations() {
      if (textureAnimations != null && Config.isAnimatedTextures()) {
         int n = 0;

         for (int i = 0; i < textureAnimations.length; i++) {
            TextureAnimation textureAnimation = textureAnimations[i];
            textureAnimation.updateTexture();
            if (textureAnimation.isActive()) {
               n++;
            }
         }

         int frameCount = Config.getMinecraft().entityRenderer.frameCount;
         if (frameCount != frameCountAnimations) {
            countAnimationsActive = n;
            frameCountAnimations = frameCount;
         }

         if (SmartAnimations.isActive()) {
            SmartAnimations.resetTexturesRendered();
         }
      } else {
         countAnimationsActive = 0;
      }
   }

   public static TextureAnimation[] getTextureAnimations(IResourcePack[] resourcePacks) {
      ArrayList list = new ArrayList();

      for (int i = 0; i < resourcePacks.length; i++) {
         IResourcePack resourcePack = resourcePacks[i];
         TextureAnimation[] textureAnimations = getTextureAnimations(resourcePack);
         if (textureAnimations != null) {
            list.addAll(Arrays.asList(textureAnimations));
         }
      }

      return (TextureAnimation[])list.toArray(new TextureAnimation[list.size()]);
   }

   public static TextureAnimation[] getTextureAnimations(IResourcePack resourcePack) {
      String[] collectFiles = ResUtils.collectFiles(resourcePack, "mcpatcher/anim/", ".properties", (String[])null);
      if (collectFiles.length <= 0) {
         return null;
      }

      ArrayList list = new ArrayList();

      for (int i = 0; i < collectFiles.length; i++) {
         String str = collectFiles[i];
         Config.dbg("Texture animation: " + str);

         try {
            ResourceLocation resourceLocation = new ResourceLocation(str);
            InputStream inputStream = resourcePack.getInputStream(resourceLocation);
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(inputStream);
            inputStream.close();
            TextureAnimation makeTextureAnimation2 = makeTextureAnimation(propertiesOrdered, resourceLocation);
            if (makeTextureAnimation2 != null) {
               ResourceLocation resourceLocation2 = new ResourceLocation(makeTextureAnimation2.getDstTex());
               if (Config.getDefiningResourcePack(resourceLocation2) != resourcePack) {
                  Config.dbg("Skipped: " + str + ", target texture not loaded from same resource pack");
               } else {
                  list.add(makeTextureAnimation2);
               }
            }
         } catch (FileNotFoundException e) {
            Config.warn("File not found: " + e.getMessage());
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      return (TextureAnimation[])list.toArray(new TextureAnimation[list.size()]);
   }

   public static TextureAnimation makeTextureAnimation(Properties properties, ResourceLocation resourceLocation) {
      String property = properties.getProperty("from");
      String property2 = properties.getProperty("to");
      int parseInt = Config.parseInt(properties.getProperty("x"), -1);
      int parseInt2 = Config.parseInt(properties.getProperty("y"), -1);
      int parseInt3 = Config.parseInt(properties.getProperty("w"), -1);
      int parseInt4 = Config.parseInt(properties.getProperty("h"), -1);
      if (property == null || property2 == null) {
         Config.warn("TextureAnimation: Source or target texture not specified");
         return null;
      }

      if (parseInt >= 0 && parseInt2 >= 0 && parseInt3 >= 0 && parseInt4 >= 0) {
         property = property.trim();
         property2 = property2.trim();
         String basePath = TextureUtils.getBasePath(resourceLocation.getResourcePath());
         property = TextureUtils.fixResourcePath(property, basePath);
         property2 = TextureUtils.fixResourcePath(property2, basePath);
         byte[] customTextureData = getCustomTextureData(property, parseInt3);
         if (customTextureData == null) {
            Config.warn("TextureAnimation: Source texture not found: " + property2);
            return null;
         }

         int n = customTextureData.length / 4;
         int n2 = n / (parseInt3 * parseInt4);
         int n3 = n2 * parseInt3 * parseInt4;
         if (n != n3) {
            Config.warn("TextureAnimation: Source texture has invalid number of frames: " + property + ", frames: " + (float)n / (parseInt3 * parseInt4));
            return null;
         }

         ResourceLocation resourceLocation2 = new ResourceLocation(property2);

         try {
            InputStream resourceStream = Config.getResourceStream(resourceLocation2);
            if (resourceStream == null) {
               Config.warn("TextureAnimation: Target texture not found: " + property2);
               return null;
            }

            BufferedImage readTextureImage2 = readTextureImage(resourceStream);
            if (parseInt + parseInt3 <= readTextureImage2.getWidth() && parseInt2 + parseInt4 <= readTextureImage2.getHeight()) {
               return new TextureAnimation(property, customTextureData, property2, resourceLocation2, parseInt, parseInt2, parseInt3, parseInt4, properties);
            }

            Config.warn("TextureAnimation: Animation coordinates are outside the target texture: " + property2);
            return null;
         } catch (IOException e) {
            Config.warn("TextureAnimation: Target texture not found: " + property2);
            return null;
         }
      } else {
         Config.warn("TextureAnimation: Invalid coordinates");
         return null;
      }
   }

   public static byte[] getCustomTextureData(String str, int n) {
      byte[] loadImage2 = loadImage(str, n);
      if (loadImage2 == null) {
         loadImage2 = loadImage("/anim" + str, n);
      }

      return loadImage2;
   }

   public static byte[] loadImage(String str, int n) {
      GameSettings gameSettings = Config.getGameSettings();

      try {
         ResourceLocation resourceLocation = new ResourceLocation(str);
         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return null;
         }

         BufferedImage readTextureImage2 = readTextureImage(resourceStream);
         resourceStream.close();
         if (readTextureImage2 == null) {
            return null;
         }

         if (n > 0 && readTextureImage2.getWidth() != n) {
            double d = readTextureImage2.getHeight() / readTextureImage2.getWidth();
            int n2 = (int)(n * d);
            readTextureImage2 = scaleBufferedImage(readTextureImage2, n, n2);
         }

         int width = readTextureImage2.getWidth();
         int height = readTextureImage2.getHeight();
         int[] ints = new int[width * height];
         byte[] bytes = new byte[width * height * 4];
         readTextureImage2.getRGB(0, 0, width, height, ints, 0, width);

         for (int i = 0; i < ints.length; i++) {
            int n2 = ints[i] >> 24 & 0xFF;
            int n3 = ints[i] >> 16 & 0xFF;
            int n4 = ints[i] >> 8 & 0xFF;
            int n5 = ints[i] & 0xFF;
            if (gameSettings != null && gameSettings.anaglyph) {
               int n6 = (n3 * 30 + n4 * 59 + n5 * 11) / 100;
               int n7 = (n3 * 30 + n4 * 70) / 100;
               int n8 = (n3 * 30 + n5 * 70) / 100;
               n3 = n6;
               n4 = n7;
               n5 = n8;
            }

            bytes[i * 4 + 0] = (byte)n3;
            bytes[i * 4 + 1] = (byte)n4;
            bytes[i * 4 + 2] = (byte)n5;
            bytes[i * 4 + 3] = (byte)n2;
         }

         return bytes;
      } catch (FileNotFoundException e) {
         return null;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static BufferedImage readTextureImage(InputStream inputStream) throws IOException {
      BufferedImage read = ImageIO.read(inputStream);
      inputStream.close();
      return read;
   }

   public static BufferedImage scaleBufferedImage(BufferedImage bufferedImage, int n, int n2) {
      BufferedImage bufferedImage2 = new BufferedImage(n, n2, 2);
      Graphics2D createGraphics = bufferedImage2.createGraphics();
      createGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      createGraphics.drawImage(bufferedImage, 0, 0, n, n2, (ImageObserver)null);
      return bufferedImage2;
   }

   public static int getCountAnimations() {
      return textureAnimations == null ? 0 : textureAnimations.length;
   }

   public static int getCountAnimationsActive() {
      return countAnimationsActive;
   }
}
