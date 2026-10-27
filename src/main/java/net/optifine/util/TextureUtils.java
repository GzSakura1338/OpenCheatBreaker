package net.optifine.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerMooshroomMushroom;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.BetterGrass;
import net.optifine.BetterSnow;
import net.optifine.Config;
import net.optifine.CustomBlockLayers;
import net.optifine.CustomColors;
import net.optifine.CustomGuis;
import net.optifine.CustomItems;
import net.optifine.CustomLoadingScreens;
import net.optifine.CustomPanorama;
import net.optifine.CustomSky;
import net.optifine.Lang;
import net.optifine.NaturalTextures;
import net.optifine.RandomEntities;
import net.optifine.SmartLeaves;
import net.optifine.TextureAnimations;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.shaders.MultiTexID;
import net.optifine.shaders.Shaders;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class TextureUtils {
   public static final String texGrassTop = "grass_top";
   public static final String texStone = "stone";
   public static final String texDirt = "dirt";
   public static final String texCoarseDirt = "coarse_dirt";
   public static final String texGrassSide = "grass_side";
   public static final String texStoneslabSide = "stone_slab_side";
   public static final String texStoneslabTop = "stone_slab_top";
   public static final String texBedrock = "bedrock";
   public static final String texSand = "sand";
   public static final String texGravel = "gravel";
   public static final String texLogOak = "log_oak";
   public static final String texLogBigOak = "log_big_oak";
   public static final String texLogAcacia = "log_acacia";
   public static final String texLogSpruce = "log_spruce";
   public static final String texLogBirch = "log_birch";
   public static final String texLogJungle = "log_jungle";
   public static final String texLogOakTop = "log_oak_top";
   public static final String texLogBigOakTop = "log_big_oak_top";
   public static final String texLogAcaciaTop = "log_acacia_top";
   public static final String texLogSpruceTop = "log_spruce_top";
   public static final String texLogBirchTop = "log_birch_top";
   public static final String texLogJungleTop = "log_jungle_top";
   public static final String texLeavesOak = "leaves_oak";
   public static final String texLeavesBigOak = "leaves_big_oak";
   public static final String texLeavesAcacia = "leaves_acacia";
   public static final String texLeavesBirch = "leaves_birch";
   public static final String texLeavesJungle = "leaves_jungle";
   public static final String texGoldOre = "gold_ore";
   public static final String texIronOre = "iron_ore";
   public static final String texCoalOre = "coal_ore";
   public static final String texObsidian = "obsidian";
   public static final String texGrassSideOverlay = "grass_side_overlay";
   public static final String texSnow = "snow";
   public static final String texGrassSideSnowed = "grass_side_snowed";
   public static final String texMyceliumSide = "mycelium_side";
   public static final String texMyceliumTop = "mycelium_top";
   public static final String texDiamondOre = "diamond_ore";
   public static final String texRedstoneOre = "redstone_ore";
   public static final String texLapisOre = "lapis_ore";
   public static final String texCactusSide = "cactus_side";
   public static final String texClay = "clay";
   public static final String texFarmlandWet = "farmland_wet";
   public static final String texFarmlandDry = "farmland_dry";
   public static final String texNetherrack = "netherrack";
   public static final String texSoulSand = "soul_sand";
   public static final String texGlowstone = "glowstone";
   public static final String texLeavesSpruceOpaque = "leaves_spruce_opaque";
   public static final String texEndStone = "end_stone";
   public static final String texSandstoneTop = "sandstone_top";
   public static final String texSandstoneBottom = "sandstone_bottom";
   public static final String texRedstoneLampOff = "redstone_lamp_off";
   public static final String texRedstoneLampOn = "redstone_lamp_on";
   public static final String texWaterStill = "water_still";
   public static final String texWaterFlow = "water_flow";
   public static final String texLavaStill = "lava_still";
   public static final String texLavaFlow = "lava_flow";
   public static final String texFireLayer0 = "fire_layer_0";
   public static final String texFireLayer1 = "fire_layer_1";
   public static final String texPortal = "portal";
   public static final String texGlass = "glass";
   public static final String texGlassPaneTop = "glass_pane_top";
   public static final String texCompass = "compass";
   public static final String texClock = "clock";
   public static TextureAtlasSprite iconGrassTop;
   public static TextureAtlasSprite iconGrassSide;
   public static TextureAtlasSprite iconGrassSideOverlay;
   public static TextureAtlasSprite iconSnow;
   public static TextureAtlasSprite iconGrassSideSnowed;
   public static TextureAtlasSprite iconMyceliumSide;
   public static TextureAtlasSprite iconMyceliumTop;
   public static TextureAtlasSprite iconWaterStill;
   public static TextureAtlasSprite iconWaterFlow;
   public static TextureAtlasSprite iconLavaStill;
   public static TextureAtlasSprite iconLavaFlow;
   public static TextureAtlasSprite iconPortal;
   public static TextureAtlasSprite iconFireLayer0;
   public static TextureAtlasSprite iconFireLayer1;
   public static TextureAtlasSprite iconGlass;
   public static TextureAtlasSprite iconGlassPaneTop;
   public static TextureAtlasSprite iconCompass;
   public static TextureAtlasSprite iconClock;
   public static final String SPRITE_PREFIX_BLOCKS = "minecraft:blocks/";
   public static final String SPRITE_PREFIX_ITEMS = "minecraft:items/";
   public static IntBuffer staticBuffer = GLAllocation.createDirectIntBuffer(256);
   public static final String TEXT_LEAVES_SPRUCE = "leaves_spruce";
   public static final String TEXT_LEAVES_SPRUCE2 = "leaves_spruce";

   public static void update() {
      TextureMap textureMapBlocks = getTextureMapBlocks();
      if (textureMapBlocks != null) {
         String str = "minecraft:blocks/";
         iconGrassTop = textureMapBlocks.getSpriteSafe(str + "grass_top");
         iconGrassSide = textureMapBlocks.getSpriteSafe(str + "grass_side");
         iconGrassSideOverlay = textureMapBlocks.getSpriteSafe(str + "grass_side_overlay");
         iconSnow = textureMapBlocks.getSpriteSafe(str + "snow");
         iconGrassSideSnowed = textureMapBlocks.getSpriteSafe(str + "grass_side_snowed");
         iconMyceliumSide = textureMapBlocks.getSpriteSafe(str + "mycelium_side");
         iconMyceliumTop = textureMapBlocks.getSpriteSafe(str + "mycelium_top");
         iconWaterStill = textureMapBlocks.getSpriteSafe(str + "water_still");
         iconWaterFlow = textureMapBlocks.getSpriteSafe(str + "water_flow");
         iconLavaStill = textureMapBlocks.getSpriteSafe(str + "lava_still");
         iconLavaFlow = textureMapBlocks.getSpriteSafe(str + "lava_flow");
         iconFireLayer0 = textureMapBlocks.getSpriteSafe(str + "fire_layer_0");
         iconFireLayer1 = textureMapBlocks.getSpriteSafe(str + "fire_layer_1");
         iconPortal = textureMapBlocks.getSpriteSafe(str + "portal");
         iconGlass = textureMapBlocks.getSpriteSafe(str + "glass");
         iconGlassPaneTop = textureMapBlocks.getSpriteSafe(str + "glass_pane_top");
         String str2 = "minecraft:items/";
         iconCompass = textureMapBlocks.getSpriteSafe(str2 + "compass");
         iconClock = textureMapBlocks.getSpriteSafe(str2 + "clock");
      }
   }

   public static BufferedImage fixTextureDimensions(String str, BufferedImage bufferedImage) {
      if (str.startsWith("/mob/zombie") || str.startsWith("/mob/pigzombie")) {
         int width = bufferedImage.getWidth();
         int height = bufferedImage.getHeight();
         if (width == height * 2) {
            BufferedImage bufferedImage2 = new BufferedImage(width, height * 2, 2);
            Graphics2D createGraphics = bufferedImage2.createGraphics();
            createGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            createGraphics.drawImage(bufferedImage, 0, 0, width, height, (ImageObserver)null);
            return bufferedImage2;
         }
      }

      return bufferedImage;
   }

   public static int ceilPowerOfTwo(int n) {
      int n2 = 1;

      while (n2 < n) {
         n2 *= 2;
      }

      return n2;
   }

   public static ITextureObject getTexture(ResourceLocation resourceLocation) {
      ITextureObject texture = Config.getTextureManager().getTexture(resourceLocation);
      if (texture != null) {
         return texture;
      }

      if (!Config.hasResource(resourceLocation)) {
         return null;
      }

      SimpleTexture simpleTexture = new SimpleTexture(resourceLocation);
      Config.getTextureManager().loadTexture(resourceLocation, simpleTexture);
      return simpleTexture;
   }

   public static void resourcesReloaded(IResourceManager resourceManager) {
      if (getTextureMapBlocks() != null) {
         Config.dbg("*** Reloading custom textures ***");
         CustomSky.reset();
         TextureAnimations.reset();
         update();
         NaturalTextures.update();
         BetterGrass.update();
         BetterSnow.update();
         TextureAnimations.update();
         CustomColors.update();
         CustomSky.update();
         RandomEntities.update();
         CustomItems.updateModels();
         CustomEntityModels.update();
         Shaders.resourcesReloaded();
         Lang.resourcesReloaded();
         Config.updateTexturePackClouds();
         SmartLeaves.updateLeavesModels();
         CustomPanorama.update();
         CustomGuis.update();
         LayerMooshroomMushroom.update();
         CustomLoadingScreens.update();
         CustomBlockLayers.update();
         Config.getTextureManager().tick();
      }
   }

   public static TextureMap getTextureMapBlocks() {
      return Minecraft.getMinecraft().getTextureMapBlocks();
   }

   public static void registerResourceListener() {
      IResourceManager resourceManager = Config.getResourceManager();
      if (resourceManager instanceof IReloadableResourceManager) {
         IReloadableResourceManager reloadableResourceManager = (IReloadableResourceManager)resourceManager;
         IResourceManagerReloadListener resourceManagerReloadListener = new IResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager2) {
               TextureUtils.resourcesReloaded(resourceManager2);
            }
         };
         reloadableResourceManager.registerReloadListener(resourceManagerReloadListener);
      }

      ITickableTextureObject tickableTextureObject = new ITickableTextureObject() {
         @Override
         public void tick() {
            TextureAnimations.updateAnimations();
         }

         @Override
         public void loadTexture(IResourceManager resourceManager2) {
         }

         @Override
         public int getGlTextureId() {
            return 0;
         }

         @Override
         public void setBlurMipmap(boolean flag, boolean flag2) {
         }

         @Override
         public void restoreLastBlurMipmap() {
         }

         @Override
         public MultiTexID getMultiTexID() {
            return null;
         }
      };
      ResourceLocation resourceLocation = new ResourceLocation("optifine/TickableTextures");
      Config.getTextureManager().loadTickableTexture(resourceLocation, tickableTextureObject);
   }

   public static ResourceLocation fixResourceLocation(ResourceLocation resourceLocation, String str) {
      if (!resourceLocation.getResourceDomain().equals("minecraft")) {
         return resourceLocation;
      }

      String resourcePath = resourceLocation.getResourcePath();
      String fixResourcePath2 = fixResourcePath(resourcePath, str);
      if (fixResourcePath2 != resourcePath) {
         resourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), fixResourcePath2);
      }

      return resourceLocation;
   }

   public static String fixResourcePath(String str, String str2) {
      String str3 = "assets/minecraft/";
      if (str.startsWith(str3)) {
         return str.substring(str3.length());
      }

      if (str.startsWith("./")) {
         str = str.substring(2);
         if (!str2.endsWith("/")) {
            str2 = str2 + "/";
         }

         return str2 + str;
      } else {
         if (str.startsWith("/~")) {
            str = str.substring(1);
         }

         String str4 = "mcpatcher/";
         if (str.startsWith("~/")) {
            str = str.substring(2);
            return str4 + str;
         } else {
            return str.startsWith("/") ? str4 + str.substring(1) : str;
         }
      }
   }

   public static String getBasePath(String str) {
      int lastIndexOf = str.lastIndexOf(47);
      return lastIndexOf < 0 ? "" : str.substring(0, lastIndexOf);
   }

   public static void applyAnisotropicLevel() {
      if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
         float glGetFloat = GL11.glGetFloat(34047);
         float anisotropicFilterLevel = Config.getAnisotropicFilterLevel();
         anisotropicFilterLevel = Math.min(anisotropicFilterLevel, glGetFloat);
         GL11.glTexParameterf(3553, 34046, anisotropicFilterLevel);
      }
   }

   public static void bindTexture(int n) {
      GlStateManager.bindTexture(n);
   }

   public static boolean isPowerOfTwo(int n) {
      int roundUpToPowerOfTwo = MathHelper.roundUpToPowerOfTwo(n);
      return roundUpToPowerOfTwo == n;
   }

   public static BufferedImage scaleImage(BufferedImage bufferedImage, int n) {
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      int n2 = height * n / width;
      BufferedImage bufferedImage2 = new BufferedImage(n, n2, 2);
      Graphics2D createGraphics = bufferedImage2.createGraphics();
      Object obj = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      if (n < width || n % width != 0) {
         obj = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
      }

      createGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, obj);
      createGraphics.drawImage(bufferedImage, 0, 0, n, n2, (ImageObserver)null);
      return bufferedImage2;
   }

   public static int scaleToGrid(int n, int n2) {
      if (n == n2) {
         return n;
      }

      int n3 = n / n2 * n2;

      while (n3 < n) {
         n3 += n2;
      }

      return n3;
   }

   public static int scaleToMin(int n, int n2) {
      if (n >= n2) {
         return n;
      }

      int n3 = n2 / n * n;

      while (n3 < n2) {
         n3 += n;
      }

      return n3;
   }

   public static Dimension getImageSize(InputStream inputStream, String str) {
      Iterator imageReadersBySuffix = ImageIO.getImageReadersBySuffix(str);

      while (imageReadersBySuffix.hasNext()) {
         ImageReader imageReader = (ImageReader)imageReadersBySuffix.next();

         Dimension dimension;
         try {
            ImageInputStream createImageInputStream = ImageIO.createImageInputStream(inputStream);
            imageReader.setInput(createImageInputStream);
            int width = imageReader.getWidth(imageReader.getMinIndex());
            int height = imageReader.getHeight(imageReader.getMinIndex());
            dimension = new Dimension(width, height);
         } catch (IOException e) {
            continue;
         } finally {
            imageReader.dispose();
         }

         return dimension;
      }

      return null;
   }

   public static void dbgMipmaps(TextureAtlasSprite textureAtlasSprite) {
      int[][] frameTextureData = textureAtlasSprite.getFrameTextureData(0);

      for (int i = 0; i < frameTextureData.length; i++) {
         int[] ints = frameTextureData[i];
         if (ints == null) {
            Config.dbg("" + i + ": " + ints);
         } else {
            Config.dbg("" + i + ": " + ints.length);
         }
      }
   }

   public static void saveGlTexture(String str, int n, int n2, int n3, int n4) {
      bindTexture(n);
      GL11.glPixelStorei(3333, 1);
      GL11.glPixelStorei(3317, 1);
      File file = new File(str);
      File parentFile = file.getParentFile();
      if (parentFile != null) {
         parentFile.mkdirs();
      }

      for (int i = 0; i < 16; i++) {
         File file2 = new File(str + "_" + i + ".png");
         file2.delete();
      }

      for (int i = 0; i <= n2; i++) {
         File file2 = new File(str + "_" + i + ".png");
         int n5 = n3 >> i;
         int n6 = n4 >> i;
         int n7 = n5 * n6;
         IntBuffer createIntBuffer = BufferUtils.createIntBuffer(n7);
         int[] ints = new int[n7];
         GL11.glGetTexImage(3553, i, 32993, 33639, createIntBuffer);
         createIntBuffer.get(ints);
         BufferedImage bufferedImage = new BufferedImage(n5, n6, 2);
         bufferedImage.setRGB(0, 0, n5, n6, ints, 0, n5);

         try {
            ImageIO.write(bufferedImage, "png", file2);
            Config.dbg("Exported: " + file2);
         } catch (Exception e) {
            Config.warn("Error writing: " + file2);
            Config.warn("" + e.getClass().getName() + ": " + e.getMessage());
         }
      }
   }

   public static void generateCustomMipmaps(TextureAtlasSprite textureAtlasSprite, int n) {
      int iconWidth = textureAtlasSprite.getIconWidth();
      int iconHeight = textureAtlasSprite.getIconHeight();
      if (textureAtlasSprite.getFrameCount() < 1) {
         ArrayList list = new ArrayList();
         int[][] intses = new int[n + 1][];
         int[] ints = new int[iconWidth * iconHeight];
         intses[0] = ints;
         list.add(intses);
         textureAtlasSprite.setFramesTextureData(list);
      }

      ArrayList list = new ArrayList();
      int frameCount = textureAtlasSprite.getFrameCount();

      for (int i = 0; i < frameCount; i++) {
         int[] frameData = getFrameData(textureAtlasSprite, i, 0);
         if (frameData == null || frameData.length < 1) {
            frameData = new int[iconWidth * iconHeight];
         }

         if (frameData.length != iconWidth * iconHeight) {
            int round = (int)Math.round(Math.sqrt(frameData.length));
            if (round * round != frameData.length) {
               frameData = new int[1];
               round = 1;
            }

            BufferedImage bufferedImage = new BufferedImage(round, round, 2);
            bufferedImage.setRGB(0, 0, round, round, frameData, 0, round);
            BufferedImage scaleImage2 = scaleImage(bufferedImage, iconWidth);
            int[] ints = new int[iconWidth * iconHeight];
            scaleImage2.getRGB(0, 0, iconWidth, iconHeight, ints, 0, iconWidth);
            frameData = ints;
         }

         int[][] intses = new int[n + 1][];
         intses[0] = frameData;
         list.add(intses);
      }

      textureAtlasSprite.setFramesTextureData(list);
      textureAtlasSprite.generateMipmaps(n);
   }

   public static int[] getFrameData(TextureAtlasSprite textureAtlasSprite, int n, int n2) {
      List framesTextureData = textureAtlasSprite.getFramesTextureData();
      if (framesTextureData.size() <= n) {
         return null;
      }

      int[][] intses = (int[][])framesTextureData.get(n);
      return intses != null && intses.length > n2 ? intses[n2] : null;
   }

   public static int getGLMaximumTextureSize() {
      for (int i = 65536; i > 0; i >>= 1) {
         GlStateManager.glTexImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (IntBuffer)null);
         int glGetError = GL11.glGetError();
         int glGetTexLevelParameteri = GlStateManager.glGetTexLevelParameteri(32868, 0, 4096);
         if (glGetTexLevelParameteri != 0) {
            return i;
         }
      }

      return -1;
   }

   public static int computeInt(int n) {
      int n2 = 1;

      int n3;
      for (n3 = 0; n2 < n; n3++) {
         n2 *= 2;
      }

      return n3;
   }

   public static int computeInt2(int n) {
      int n2 = 1;

      for (int i = 0; i < n; i++) {
         n2 *= 2;
      }

      return n2;
   }
}
