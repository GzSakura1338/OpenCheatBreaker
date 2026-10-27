package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.emote.RenderReflectionHelper;
import com.cheatbreaker.client.util.ReflectionUtils;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeGenBase;

public class ColorManager implements IColorManager {
   public static final int BLOCK_COLOR_UNSET = -65281;
   public Object lock = new Object();
   public Minecraft mc = null;
   public MinimapSettings minimapSettings = null;
   public List lastResourcePacks = null;
   public int mapImageTextureId = -1;
   public int[] blockColors = new int[86016];
   public int[] blockColorsWithDefaultTint = new int[86016];
   public Set<Integer> biomeTintedBlocks = new HashSet<>();
   public String blendMode;
   public BufferedImage colorPicker;
   public boolean mcpatcherInstalled = false;
   public boolean optifineInstalled = false;
   public HashMap<String, Integer[]> biomeColormaps = new HashMap<>();
   public Set<Integer> colormapBlocks = new HashSet<>();
   public HashMap<String, Integer> colormapColors = new HashMap<>();
   public IVoxelMap voxelMap;

   public int getAverageSpriteColor(TextureAtlasSprite textureAtlasSprite) {
      if (textureAtlasSprite != null && textureAtlasSprite.getFrameCount() != 0) {
         int[] ints = textureAtlasSprite.getFrameTextureData(0)[0];
         if (ints.length == 0) {
            return 0;
         }

         long l = 0L;
         long l2 = 0L;
         long l3 = 0L;
         long l4 = 0L;

         for (int n : ints) {
            l += n >>> 24 & 0xFF;
            l2 += n >> 16 & 0xFF;
            l3 += n >> 8 & 0xFF;
            l4 += n & 0xFF;
         }

         int length = ints.length;
         return (int)(l / length) << 24 | (int)(l2 / length) << 16 | (int)(l3 / length) << 8 | (int)(l4 / length);
      } else {
         return 0;
      }
   }

   public int getBlockColorWithAlpha(int n, int n2, boolean flag) {
      int computeBlockColor = this.computeBlockColor(n, n2);
      if (!flag) {
         computeBlockColor |= -16777216;
      }

      return computeBlockColor;
   }

   public int getBlockColorRaw(int n, int n2) {
      synchronized (this.lock) {
         byte b;
         try {
            if (this.blockColors[this.getBlockKey(n, n2)] == -65281) {
               this.blockColors[this.getBlockKey(n, n2)] = this.computeBlockColor(n, n2);
            }

            int n3 = this.blockColors[this.getBlockKey(n, n2)];
            if (n3 != -65025) {
               return n3;
            }

            if (this.blockColors[this.getBlockKey(n, 0)] == -65281) {
               this.blockColors[this.getBlockKey(n, 0)] = this.computeBlockColor(n, 0);
            }

            n3 = this.blockColors[this.getBlockKey(n, 0)];
            if (n3 != -65025) {
               return n3;
            }

            b = 0;
         } catch (ArrayIndexOutOfBoundsException e) {
            return -65025;
         }

         return b;
      }
   }

   public void loadColormaps() {
      ArrayList list = new ArrayList();
      list.addAll(this.findResourceLocations("minecraft", "/mcpatcher/colormap/blocks", ".png", true, false, true));
      Properties properties = new Properties();

      try {
         InputStream inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();
         if (inputStream != null) {
            properties.load(inputStream);
            inputStream.close();
         }
      } catch (IOException e) {
      }

      int n = 2129968;
      String property = properties.getProperty("lilypad");
      if (property != null) {
         n = Integer.parseInt(property, 16);
      }

      for (int i = 0; i < 16; i++) {
         this.blockColors[this.getBlockKey(BlockIdRepository.waterlilyId, i)] = this.colorMultiplier(
            this.getBlockColorRaw(BlockIdRepository.waterlilyId, i), n | 0xFF000000
         );
      }

      Enumeration propertyNames = properties.propertyNames();

      while (propertyNames.hasMoreElements()) {
         String nextElement = (String)propertyNames.nextElement();
         if (nextElement.startsWith("palette.block")) {
            String substring = nextElement.substring("palette.block.".length());
            substring = substring.replace("~", "mcpatcher");
            this.loadBiomeColormap(new ResourceLocation(substring), properties.getProperty(nextElement));
         }
      }

      for (ResourceLocation resourceLocation : this.findResourceLocations("minecraft", "/mcpatcher/colormap/blocks", ".properties", true, false, true)) {
         Properties properties2 = new Properties();

         try {
            InputStream inputStream = this.mc.getResourceManager().getResource(resourceLocation).getInputStream();
            if (inputStream != null) {
               properties.load(inputStream);
               inputStream.close();
            }
         } catch (IOException e) {
            break;
         }

         String property2 = properties2.getProperty("blocks");
         ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath().replace(".properties", ".png"));
         list.remove(resourceLocation2);
         this.loadBiomeColormap(resourceLocation2, property2);
      }

      for (ResourceLocation resourceLocation : (Iterable<ResourceLocation>)list) {
         String resourcePath = resourceLocation.getResourcePath();
         System.out.println("processing name: " + resourcePath);
         resourcePath = resourcePath.substring(resourcePath.lastIndexOf("/") + 1, resourcePath.lastIndexOf(".png"));
         System.out.println("processed name: " + resourcePath);
         this.loadBiomeColormap(resourceLocation, "minecraft:" + resourcePath);
      }
   }

   public BufferedImage getSpriteImage(TextureAtlasSprite textureAtlasSprite) {
      if (textureAtlasSprite == null) {
         return null;
      }

      int iconWidth = textureAtlasSprite.getIconWidth();
      int iconHeight = textureAtlasSprite.getIconHeight();
      int[] ints = textureAtlasSprite.getFrameTextureData(0)[0];
      BufferedImage bufferedImage = new BufferedImage(iconWidth, iconHeight, 2);
      bufferedImage.setRGB(0, 0, iconWidth, iconHeight, ints, 0, iconWidth);
      return bufferedImage;
   }

   public void loadBlendMode() {
      this.blendMode = "alpha";
      Properties properties = new Properties();
      ResourceLocation resourceLocation = new ResourceLocation("minecraft", "mcpatcher/renderpass.properties");

      try {
         InputStream inputStream = this.mc.getResourceManager().getResource(resourceLocation).getInputStream();
         if (inputStream != null) {
            properties.load(inputStream);
            inputStream.close();
            this.blendMode = properties.getProperty("blend.3");
         }
      } catch (IOException e) {
         this.blendMode = "alpha";
      }

      String str = "minecraft";

      for (ResourceLocation resourceLocation2 : this.findResourceLocations(str, "/mcpatcher/ctm", ".properties", true, false, true)) {
         try {
            this.loadColormapDefinition(resourceLocation2);
         } catch (NumberFormatException e) {
         } catch (IllegalArgumentException e) {
         }
      }

      for (int i = 0; i < this.blockColors.length; i++) {
         if (this.blockColors[i] != -65025 && this.blockColors[i] != -65281 && (this.blockColors[i] >> 24 & 0xFF) < 27) {
            this.blockColors[i] = this.blockColors[i] | 452984832;
         }
      }
   }

   public int parseBlockId(String str) {
      if (str.matches("^\\d+$")) {
         return Integer.parseInt(str);
      }

      for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
         if (BiomeGenBase.getBiomeGenArray()[i] != null
            && BiomeGenBase.getBiomeGenArray()[i].biomeName.toLowerCase().replace(" ", "").equalsIgnoreCase(str)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public int getBlockColorWithDefaultTint(int n, int n2, int n3) {
      int n4 = this.blockColorsWithDefaultTint[this.getBlockKey(n, n2)];
      return n4 != -65281 ? n4 : this.getBlockColorRaw(n, n2);
   }

   @Override
   public boolean isOptifineInstalled() {
      return this.optifineInstalled;
   }

   public static void findResourceFiles(File file, String str, String str2, String str3, boolean flag, boolean flag2, Collection<ResourceLocation> collection) {
      File file2 = new File(file, str2);
      String[] list = file2.list();
      if (list != null) {
         String str4 = str2 + "/";

         for (String str5 : list) {
            File file3 = new File(file2, str5);
            String str6 = str4 + str5;
            if (file3.isDirectory()) {
               if (flag2 && str5.endsWith(str3)) {
                  collection.add(new ResourceLocation(str, str6));
               }

               if (flag) {
                  findResourceFiles(file, str, str4 + str5, str3, flag, flag2, collection);
               }
            } else if (str5.endsWith(str3) && !flag2) {
               collection.add(new ResourceLocation(str, str6));
            }
         }
      }
   }

   public void loadColorPicker() {
      try {
         InputStream inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("voxelmap/images/colorPicker.png")).getInputStream();
         BufferedImage read = ImageIO.read(inputStream);
         inputStream.close();
         this.colorPicker = new BufferedImage(read.getWidth(null), read.getHeight(null), 2);
         Graphics2D createGraphics = this.colorPicker.createGraphics();
         createGraphics.drawImage(read, 0, 0, null);
         createGraphics.dispose();
      } catch (Exception e) {
         System.err.println("Error loading color picker: " + e.getLocalizedMessage());
      }
   }

   public int getBlockIdByName(String str) {
      Block blockFromName = Block.getBlockFromName(str);
      return blockFromName != null ? Block.getIdFromBlock(blockFromName) : -1;
   }

   public void loadWaterColors(boolean flag) {
      int n = -1;
      n = this.getBlockColorRaw(BlockIdRepository.waterId, 0);
      InputStream inputStream = null;
      int n2 = -1;
      BufferedImage bufferedImage = null;

      try {
         inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("mcpatcher/colormap/water.png")).getInputStream();
      } catch (IOException e) {
         inputStream = null;
      }

      if (inputStream != null) {
         try {
            BufferedImage read = ImageIO.read(inputStream);
            inputStream.close();
            bufferedImage = new BufferedImage(read.getWidth(null), read.getHeight(null), 1);
            Graphics2D createGraphics = bufferedImage.createGraphics();
            createGraphics.drawImage(read, 0, 0, null);
            createGraphics.dispose();
            BiomeGenBase forest = BiomeGenBase.forest;
            double d = MathHelper.clamp_float(forest.getFloatTemperature(new BlockPos(0, 64, 0)), 0.0F, 1.0F);
            double d2 = MathHelper.clamp_float(forest.getFloatRainfall(), 0.0F, 1.0F);
            d2 *= d;
            d = 1.0 - d;
            d2 = 1.0 - d2;
            n2 = bufferedImage.getRGB((int)((bufferedImage.getWidth() - 1) * d), (int)((bufferedImage.getHeight() - 1) * d2)) & 16777215;
         } catch (Exception e) {
         }
      }

      if (n2 != -1 && n2 != 0) {
         n = this.colorMultiplier(n, n2 | 0xFF000000);
      } else {
         n = this.colorMultiplier(n, BiomeGenBase.forest.waterColorMultiplier | 0xFF000000);
      }

      for (int i = 0; i < 16; i++) {
         this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.flowingWaterId, i)] = n;
         this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.waterId, i)] = n;
      }
   }

   public void loadSpecialColors() {
      this.blockColors[this.getBlockKey(BlockIdRepository.tallgrassId, 0)] = this.colorMultiplier(this.computeBlockColor(BlockIdRepository.tallgrassId, 0), -1);

      for (int i = 0; i < 16; i++) {
         this.blockColors[this.getBlockKey(BlockIdRepository.webId, i)] = this.getBlockColorWithAlpha(BlockIdRepository.webId, i, false);
      }

      RenderReflectionHelper.handleBlock(Block.getBlockFromName("minecraft:flowing_lava"), 1);
      RenderReflectionHelper.handleBlock(Block.getBlockFromName("minecraft:lava"), 1);
   }

   @Override
   public int getAirColor() {
      return this.blockColors[BlockIdRepository.airId];
   }

   public int getBlockKey(int n, int n2) {
      return n | n2 << 12;
   }

   public String[] parseStringList(String str) {
      ArrayList list = new ArrayList();

      for (String str2 : str.replace(',', ' ').split("\\s+")) {
         str2 = str2.trim();

         try {
            if (str2.matches("^\\d+$")) {
               list.add("" + Integer.parseInt(str2));
            } else if (str2.matches("^\\d+-\\d+$")) {
               String[] split = str2.split("-");
               int parseInt = Integer.parseInt(split[0]);
               int parseInt2 = Integer.parseInt(split[1]);

               for (int i = parseInt; i <= parseInt2; i++) {
                  list.add("" + i);
               }
            } else if (str2 != null && str2 != "") {
               list.add(str2);
            }
         } catch (NumberFormatException e) {
         }
      }

      String[] strs = new String[list.size()];

      for (int i = 0; i < strs.length; i++) {
         strs[i] = (String)list.get(i);
      }

      return strs;
   }

   public void loadMapImage() {
      if (this.mapImageTextureId != -1) {
         GlUtils.deleteTexture(this.mapImageTextureId);
      }

      try {
         InputStream inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("voxelmap/images/squaremap.png")).getInputStream();
         BufferedImage read = ImageIO.read(inputStream);
         BufferedImage bufferedImage = new BufferedImage(read.getWidth(null), read.getHeight(null), 2);
         Graphics2D createGraphics = bufferedImage.createGraphics();
         createGraphics.drawImage(read, 0, 0, null);
         this.mapImageTextureId = GlUtils.uploadTexture(bufferedImage);
      } catch (Exception e) {
         try {
            InputStream inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("textures/map/map_background.png")).getInputStream();
            BufferedImage read = ImageIO.read(inputStream);
            inputStream.close();
            BufferedImage bufferedImage = new BufferedImage(read.getWidth(null), read.getHeight(null), 2);
            Graphics2D createGraphics = bufferedImage.createGraphics();
            if (!GlUtils.framebufferSupported && !GlUtils.stencilSupported) {
               createGraphics.setColor(Color.DARK_GRAY);
               createGraphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            }

            createGraphics.drawImage(read, 0, 0, null);
            int n = bufferedImage.getWidth() * 8 / 128;
            createGraphics.setComposite(AlphaComposite.Clear);
            createGraphics.fillRect(n, n, bufferedImage.getWidth() - n * 2, bufferedImage.getHeight() - n * 2);
            createGraphics.dispose();
            this.mapImageTextureId = GlUtils.uploadTexture(bufferedImage);
         } catch (Exception e2) {
            System.err.println("Error loading texture pack's map image: " + e2.getLocalizedMessage());
         }
      }
   }

   public void applyDefaultBiomeTints(boolean flag) {
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.grassId, 0)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.grassId, 0), ColorizerGrass.getGrassColor(0.7, 0.8) | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 0)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 0), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 1)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 1), ColorizerFoliage.getFoliageColorPine() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 2)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 2), ColorizerFoliage.getFoliageColorBirch() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 3)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 3), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 4)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 4), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 5)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 5), ColorizerFoliage.getFoliageColorPine() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 6)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 6), ColorizerFoliage.getFoliageColorBirch() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 7)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 7), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 8)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 8), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 9)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 9), ColorizerFoliage.getFoliageColorPine() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 10)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 10), ColorizerFoliage.getFoliageColorBirch() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 11)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 11), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 12)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 12), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 13)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 13), ColorizerFoliage.getFoliageColorPine() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 14)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 14), ColorizerFoliage.getFoliageColorBirch() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leavesId, 15)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leavesId, 15), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leaves2Id, 0)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leaves2Id, 0), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leaves2Id, 1)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leaves2Id, 1), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leaves2Id, 4)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leaves2Id, 4), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.leaves2Id, 5)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.leaves2Id, 5), ColorizerFoliage.getFoliageColorBasic() | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.tallgrassId, 1)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.tallgrassId, 1), ColorizerGrass.getGrassColor(0.7, 0.8) | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.tallgrassId, 2)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.tallgrassId, 2), ColorizerGrass.getGrassColor(0.7, 0.8) | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.doublePlantId, 2)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.doublePlantId, 2), ColorizerGrass.getGrassColor(0.7, 0.8) | 0xFF000000
      );
      this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.doublePlantId, 3)] = this.colorMultiplier(
         this.getBlockColorRaw(BlockIdRepository.doublePlantId, 3), ColorizerGrass.getGrassColor(0.7, 0.8) | 0xFF000000
      );

      for (int i = 0; i < 16; i++) {
         this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.reedsId, 0)] = this.colorMultiplier(
            this.getBlockColorRaw(BlockIdRepository.reedsId, 0), ColorizerGrass.getGrassColor(0.7, 0.8) | 0xFF000000
         );
         this.blockColorsWithDefaultTint[this.getBlockKey(BlockIdRepository.vineId, i)] = this.colorMultiplier(
            this.getBlockColorRaw(BlockIdRepository.vineId, i), ColorizerFoliage.getFoliageColor(0.7, 0.8) | 0xFF000000
         );
      }

      this.loadWaterColors(flag);
   }

   @Override
   public int colorAdder(int n, int n2) {
      float f = (n >> 24 & 0xFF) / 255.0F;
      float f2 = (n >> 16 & 0xFF) * f;
      float f3 = (n >> 8 & 0xFF) * f;
      float f4 = (n >> 0 & 0xFF) * f;
      float f5 = (n2 >> 24 & 0xFF) / 255.0F;
      float f6 = (n2 >> 16 & 0xFF) * f5 * (1.0F - f);
      float f7 = (n2 >> 8 & 0xFF) * f5 * (1.0F - f);
      float f8 = (n2 >> 0 & 0xFF) * f5 * (1.0F - f);
      float f9 = f + f5 * (1.0F - f);
      float f10 = (f2 + f6) / f9;
      float f11 = (f3 + f7) / f9;
      float f12 = (f4 + f8) / f9;
      return ((int)(f9 * 255.0F) & 0xFF) << 24 | ((int)f10 & 0xFF) << 16 | ((int)f11 & 0xFF) << 8 | (int)f12 & 0xFF;
   }

   @Override
   public boolean getResourcePackChanged() {
      if (this.lastResourcePacks != null && this.lastResourcePacks.equals(this.mc.gameSettings.resourcePacks)) {
         return false;
      }

      this.reload();
      return true;
   }

   public void reload() {
      this.lastResourcePacks = new ArrayList<>(this.mc.gameSettings.resourcePacks);
      BlockIdRepository.loadBlockIds();
      this.loadColorPicker();
      this.loadMapImage();
      this.onReloadFinished();
      synchronized (this.lock) {
         try {
            new Thread(
                  new Runnable() {
                     @Override
                     public void run() {
                        Arrays.fill(ColorManager.this.blockColors, -65281);
                        Arrays.fill(ColorManager.this.blockColorsWithDefaultTint, -65281);
                        ColorManager.this.loadSpecialColors();
                        ColorManager.this.biomeTintedBlocks.clear();
                        ColorManager.this.colormapBlocks.clear();
                        ColorManager.this.colormapColors.clear();
                        if (ColorManager.this.mcpatcherInstalled || ColorManager.this.optifineInstalled) {
                           try {
                              ColorManager.this.loadBlendMode();
                           } catch (Exception e) {
                              System.err.println("error loading CTM " + e.getLocalizedMessage());
                              e.printStackTrace();
                           }
                        }

                        try {
                           ColorManager.this.applyDefaultBiomeTints(ColorManager.this.minimapSettings.biomes);
                        } catch (Exception e) {
                           System.err.println("error setting default biome shading " + e.getLocalizedMessage());
                        }

                        if (ColorManager.this.mcpatcherInstalled || ColorManager.this.optifineInstalled) {
                           ColorManager.this.biomeColormaps.clear();

                           try {
                              ColorManager.this.loadColormaps();
                              if (ColorManager.this.optifineInstalled) {
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/water.png"),
                                    "" + BlockIdRepository.flowingWaterId + " " + BlockIdRepository.waterId
                                 );
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/watercolor.png"),
                                    "" + BlockIdRepository.flowingWaterId + " " + BlockIdRepository.waterId
                                 );
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/watercolorX.png"),
                                    "" + BlockIdRepository.flowingWaterId + " " + BlockIdRepository.waterId
                                 );
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/swampgrass.png"),
                                    "" + BlockIdRepository.grassId + " " + BlockIdRepository.tallgrassId + ":1,2 " + BlockIdRepository.doublePlantId + ":2,3"
                                 );
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/swampgrasscolor.png"),
                                    "" + BlockIdRepository.grassId + " " + BlockIdRepository.tallgrassId + ":1,2 " + BlockIdRepository.doublePlantId + ":2,3"
                                 );
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/swampfoliage.png"),
                                    "" + BlockIdRepository.leavesId + ":0,4,8,12 " + BlockIdRepository.vineId
                                 );
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/swampfoliagecolor.png"),
                                    "" + BlockIdRepository.leavesId + ":0,4,8,12 " + BlockIdRepository.vineId
                                 );
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/pine.png"), "" + BlockIdRepository.leavesId + ":1,5,9,13"
                                 );
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/pinecolor.png"), "" + BlockIdRepository.leavesId + ":1,5,9,13"
                                 );
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/birch.png"), "" + BlockIdRepository.leavesId + ":2,6,10,14"
                                 );
                                 ColorManager.this.loadBiomeColormap(
                                    new ResourceLocation("mcpatcher/colormap/birchcolor.png"), "" + BlockIdRepository.leavesId + ":2,6,10,14"
                                 );
                              }
                           } catch (Exception e) {
                              System.err.println("error loading custom color properties " + e.getLocalizedMessage());
                              e.printStackTrace();
                           }
                        }

                        ColorManager.this.voxelMap.getMap().forceFullRender(true);
                     }
                  },
                  "Voxelmap Load Resourcepack Thread"
               )
               .start();
         } catch (Exception e) {
            System.err.println("error loading pack");
            e.printStackTrace();
         }
      }

      if (this.voxelMap.getRadar() != null) {
         this.voxelMap.getRadar().run();
      }
   }

   public List<ResourceLocation> findResourceLocations(String str, String str2, String str3, boolean flag, boolean flag2, boolean flag3) {
      if (str2 == null) {
         str2 = "";
      }

      if (str2.startsWith("/")) {
         str2 = str2.substring(1);
      }

      if (str3 == null) {
         str3 = "";
      }

      ArrayList list = new ArrayList();

      for (IResourcePack resourcePack : this.getResourcePacksForDomain(str)) {
         if (!(resourcePack instanceof DefaultResourcePack)) {
            if (resourcePack instanceof FileResourcePack) {
               ZipFile resourcePackZipFile2 = ((FileResourcePack)resourcePack).getResourcePackZipFile2();
               if (resourcePackZipFile2 == null) {
                  return list;
               }

               this.findZipResources(resourcePackZipFile2, str, "assets/" + str, str2, str3, flag, flag2, list);
            } else if (resourcePack instanceof AbstractResourcePack) {
               File resourcePackFile = ((AbstractResourcePack)resourcePack).resourcePackFile;
               if (resourcePackFile != null && resourcePackFile.isDirectory()) {
                  resourcePackFile = new File(resourcePackFile, "assets/" + str);
                  if (resourcePackFile.isDirectory()) {
                     findResourceFiles(resourcePackFile, str, str2, str3, flag, flag2, list);
                  }
               }
            }
         }
      }

      if (flag3) {
         Collections.sort(list, new Comparator<ResourceLocation>() {
            public int compare(ResourceLocation resourceLocation, ResourceLocation resourceLocation2) {
               String replaceFirst = resourceLocation.getResourcePath().replaceAll(".*/", "").replaceFirst("\\.properties", "");
               String replaceFirst2 = resourceLocation2.getResourcePath().replaceAll(".*/", "").replaceFirst("\\.properties", "");
               int compareTo = replaceFirst.compareTo(replaceFirst2);
               return compareTo != 0 ? compareTo : resourceLocation.getResourcePath().compareTo(resourceLocation2.getResourcePath());
            }
         });
      } else {
         Collections.sort(list, new Comparator<ResourceLocation>() {
            public int compare(ResourceLocation resourceLocation, ResourceLocation resourceLocation2) {
               return resourceLocation.getResourcePath().compareTo(resourceLocation2.getResourcePath());
            }
         });
      }

      return list;
   }

   @Override
   public void setSkyColor(int n) {
      for (int i = 0; i < 16; i++) {
         this.blockColors[this.getBlockKey(BlockIdRepository.airId, i)] = n;
      }
   }

   @Override
   public int colorMultiplier(int n, int n2) {
      int n3 = n >> 24 & 0xFF;
      int n4 = n >> 16 & 0xFF;
      int n5 = n >> 8 & 0xFF;
      int n6 = n >> 0 & 0xFF;
      int n7 = n2 >> 24 & 0xFF;
      int n8 = n2 >> 16 & 0xFF;
      int n9 = n2 >> 8 & 0xFF;
      int n10 = n2 >> 0 & 0xFF;
      int n11 = n3 * n7 / 255;
      int n12 = n4 * n8 / 255;
      int n13 = n5 * n9 / 255;
      int n14 = n6 * n10 / 255;
      return (n11 & 0xFF) << 24 | (n12 & 0xFF) << 16 | (n13 & 0xFF) << 8 | n14 & 0xFF;
   }

   public void findZipResources(ZipFile zipFile, String str, String str2, String str3, String str4, boolean flag, boolean flag2, Collection<ResourceLocation> collection) {
      String str5 = str2 + "/" + str3;

      for (ZipEntry zipEntry : Collections.list(zipFile.entries())) {
         if (zipEntry.isDirectory() == flag2) {
            String replaceFirst = zipEntry.getName().replaceFirst("^/", "");
            if (replaceFirst.startsWith(str5) && replaceFirst.endsWith(str4)) {
               if (str3.equals("")) {
                  if (flag || !replaceFirst.contains("/")) {
                     collection.add(new ResourceLocation(str, replaceFirst));
                  }
               } else {
                  String substring = replaceFirst.substring(str5.length());
                  if ((substring.equals("") || substring.startsWith("/")) && (flag || substring.equals("") || !substring.substring(1).contains("/"))) {
                     collection.add(new ResourceLocation(str, replaceFirst.substring(str2.length() + 1)));
                  }
               }
            }
         }
      }
   }

   @Override
   public int getMapImageTextureId() {
      return this.mapImageTextureId;
   }

   @Override
   public int getBlockColor(int n, int n2, int n3) {
      if ((this.mcpatcherInstalled || this.optifineInstalled) && this.colormapBlocks.contains(n)) {
         Integer integer = this.colormapColors.get("" + this.getBlockKey(n, n2) + " " + n3);
         if (integer != null) {
            return integer;
         }
      }

      return this.getBlockColorRaw(n, n2);
   }

   public ColorManager(IVoxelMap voxelMap) {
      this.voxelMap = voxelMap;
      this.minimapSettings = voxelMap.getMapOptions();
      this.mc = Minecraft.getMinecraft();
      this.optifineInstalled = false;
      Field field = null;

      try {
         field = GameSettings.class.getDeclaredField("ofProfiler");
      } catch (SecurityException e) {
      } catch (NoSuchFieldException e) {
      } finally {
         if (field != null) {
            this.optifineInstalled = true;
         }
      }

      this.mcpatcherInstalled = ReflectionUtils.forName("com.prupe.mcpatcher.ctm.CTMUtils");
   }

   @Override
   public Set<Integer> getBiomeTintedBlocks() {
      return this.biomeTintedBlocks;
   }

   public int computeBlockColor(int n, int n2) {
      try {
         if (n == BlockIdRepository.intValue) {
            return 419430400 | (30 + n2 * 15 & 0xFF) << 16 | 0 | 0;
         }

         int averageSpriteColor = this.getAverageSpriteColor(this.getBlockSprite(n, n2));
         if (Arrays.asList(BlockIdRepository.integerArray).contains(n)) {
            averageSpriteColor = this.blendColor(n, n2, averageSpriteColor);
         }

         if ((averageSpriteColor >> 24 & 0xFF) < 27) {
            averageSpriteColor |= 452984832;
         }

         if (n != BlockIdRepository.grassId
            && n != BlockIdRepository.leavesId
            && n != BlockIdRepository.leaves2Id
            && n != BlockIdRepository.tallgrassId
            && n != BlockIdRepository.reedsId
            && n != BlockIdRepository.vineId
            && n != BlockIdRepository.doublePlantId
            && n != BlockIdRepository.waterId
            && n != BlockIdRepository.flowingWaterId) {
            int n3 = Block.getBlockById(n)
                  .colorMultiplier(this.mc.theWorld, new BlockPos(this.mc.thePlayer.serverPosX, 78, (int)this.mc.thePlayer.posZ), 0)
               | 0xFF000000;
            if (n3 != 16777215 && n3 != -1) {
               this.biomeTintedBlocks.add(n);
               this.blockColorsWithDefaultTint[this.getBlockKey(n, n2)] = this.colorMultiplier(averageSpriteColor, n3);
            }
         }

         return averageSpriteColor;
      } catch (Exception e) {
         System.err.println("failed getting color: " + n + " " + n2);
         e.printStackTrace();
         return -65025;
      }
   }

   @Override
   public BufferedImage getBlockIconImage(int n, int n2) {
      try {
         return this.getSpriteImage(this.getBlockSprite(n, n2));
      } catch (Exception e) {
         return null;
      }
   }

   public List<IResourcePack> getResourcePacksForDomain(String str) {
      ArrayList list = new ArrayList();
      IResourceManager resourceManager = this.mc.getResourceManager();
      if (resourceManager instanceof SimpleReloadableResourceManager) {
         Map domainResourceManagers = ((SimpleReloadableResourceManager)resourceManager).getDomainResourceManagers();

         for (Entry entry : (Iterable<Entry>)domainResourceManagers.entrySet()) {
            if (str == null || str.equals(entry.getKey())) {
               FallbackResourceManager value = (FallbackResourceManager)entry.getValue();
               list.addAll(value.getResourcePacks());
            }
         }
      }

      Collections.reverse(list);
      return list;
   }

   public TextureAtlasSprite getBlockSprite(int n, int n2) {
      IBlockState stateFromMeta = Block.getBlockById(n).getStateFromMeta(n2);
      return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(stateFromMeta);
   }

   public void onReloadFinished() {
   }

   public int[] parseIntRanges(String str, int n, int n2) {
      ArrayList list = new ArrayList();

      for (String str2 : str.replace(',', ' ').split("\\s+")) {
         str2 = str2.trim();

         try {
            if (str2.matches("^\\d+$")) {
               list.add(Integer.parseInt(str2));
            } else if (str2.matches("^\\d+-\\d+$")) {
               String[] split = str2.split("-");
               int parseInt = Integer.parseInt(split[0]);
               int parseInt2 = Integer.parseInt(split[1]);

               for (int i = parseInt; i <= parseInt2; i++) {
                  list.add(i);
               }
            } else if (str2.matches("^\\d+:\\d+$")) {
               String[] split = str2.split(":");
               int parseInt = Integer.parseInt(split[0]);
               int parseInt2 = Integer.parseInt(split[1]);
               list.add(parseInt);
            }
         } catch (NumberFormatException e) {
         }
      }

      if (n <= n2) {
         int n3 = 0;

         while (n3 < list.size()) {
            if ((Integer)list.get(n3) >= n && (Integer)list.get(n3) <= n2) {
               n3++;
            } else {
               list.remove(n3);
            }
         }
      }

      int[] ints = new int[list.size()];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = (Integer)list.get(i);
      }

      return ints;
   }

   public void loadBiomeColormap(ResourceLocation resourceLocation, String str) {
      Integer[] integers = new Integer[BiomeGenBase.getBiomeGenArray().length];
      boolean flag = resourceLocation.getResourcePath().contains("/swampgrass") || resourceLocation.getResourcePath().contains("/swampfoliage");
      BufferedImage bufferedImage = null;

      try {
         InputStream inputStream = this.mc.getResourceManager().getResource(resourceLocation).getInputStream();
         bufferedImage = ImageIO.read(inputStream);
         inputStream.close();
      } catch (IOException e) {
         return;
      }

      for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
         integers[i] = -1;
      }

      BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getWidth(null), bufferedImage.getHeight(null), 1);
      Graphics2D createGraphics = bufferedImage2.createGraphics();
      createGraphics.drawImage(bufferedImage, 0, 0, null);
      createGraphics.dispose();

      for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
         if (BiomeGenBase.getBiomeGenArray()[i] != null) {
            BiomeGenBase biomeGenBase = BiomeGenBase.getBiomeGenArray()[i];
            double d = MathHelper.clamp_float(biomeGenBase.getFloatTemperature(new BlockPos(0, 64, 0)), 0.0F, 1.0F);
            double d2 = MathHelper.clamp_float(biomeGenBase.getFloatRainfall(), 0.0F, 1.0F);
            d2 *= d;
            d = 1.0 - d;
            d2 = 1.0 - d2;
            int n = bufferedImage2.getRGB((int)((bufferedImage2.getWidth() - 1) * d), (int)((bufferedImage2.getHeight() - 1) * d2)) & 16777215;
            if (n != 0 && (!flag || i == BiomeGenBase.swampland.biomeID)) {
               integers[i] = n;
            }
         }
      }

      for (String str2 : str.split("\\s+")) {
         str2 = str2.trim();
         String str3 = "";
         int n = -1;
         int[] ints = new int[0];

         try {
            String str4;
            if (str2.matches(".*:[-0-9, ]+")) {
               int lastIndexOf = str2.lastIndexOf(58);
               str3 = str2.substring(lastIndexOf + 1);
               str4 = str2.substring(0, lastIndexOf);
            } else {
               str4 = str2;
            }

            n = this.getBlockIdByName(str4);
            if (n > 0) {
               this.biomeTintedBlocks.add(n);
               ints = this.parseIntRanges(str3, 0, 15);
               if (ints.length == 0) {
                  ints = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
               }

               for (int i = 0; i < ints.length; i++) {
                  Integer[] integers2 = this.biomeColormaps.get(n + " " + ints[i]);
                  if (flag && integers2 == null) {
                     ResourceLocation resourceLocation2;
                     if (resourceLocation.getResourcePath().endsWith("/swampgrass.png")) {
                        resourceLocation2 = new ResourceLocation("textures/colormap/grass.png");
                     } else {
                        resourceLocation2 = new ResourceLocation("textures/colormap/foliage.png");
                     }

                     this.loadBiomeColormap(resourceLocation2, "" + n + ":" + ints[i]);
                     integers2 = this.biomeColormaps.get(n + " " + ints[i]);
                  }

                  if (integers2 != null) {
                     for (int j = 0; j < BiomeGenBase.getBiomeGenArray().length; j++) {
                        if (integers[j] == -1) {
                           integers[j] = integers2[j];
                        }
                     }
                  }

                  this.blockColorsWithDefaultTint[this.getBlockKey(n, ints[i])] = this.colorMultiplier(
                     this.getBlockColorRaw(n, ints[i]), integers[4] | 0xFF000000
                  );
                  this.biomeColormaps.put(n + " " + ints[i], integers);
               }
            }
         } catch (NumberFormatException e) {
         }
      }
   }

   public int blendColor(int n, int n2, int n3) {
      int n4 = n3 >> 24 & 0xFF;
      int n5 = n3 >> 16 & 0xFF;
      int n6 = n3 >> 8 & 0xFF;
      int n7 = n3 >> 0 & 0xFF;
      if (n == BlockIdRepository.standingSignId || n == BlockIdRepository.wallSignId) {
         n4 = 31;
      } else if (n == BlockIdRepository.woodenDoorId || n == BlockIdRepository.ironDoorId) {
         n4 = 47;
      } else if (n == BlockIdRepository.ladderId || n == BlockIdRepository.vineId) {
         n4 = 15;
      } else if (n == BlockIdRepository.stoneButtonId || n == BlockIdRepository.woodenButtonId) {
         n4 = 11;
      } else if (n == BlockIdRepository.fenceId || n == BlockIdRepository.netherBrickFenceId) {
         n4 = 95;
      } else if (n == BlockIdRepository.fenceGateId) {
         n4 = 92;
      } else if (n == BlockIdRepository.cobblestoneWallId) {
         n4 = 153;
      }

      return (n4 & 0xFF) << 24 | (n5 & 0xFF) << 16 | (n6 & 0xFF) << 8 | n7 & 0xFF;
   }

   public void loadColormapDefinition(ResourceLocation resourceLocation) {
      if (resourceLocation != null) {
         Properties properties = new Properties();

         try {
            InputStream inputStream = this.mc.getResourceManager().getResource(resourceLocation).getInputStream();
            if (inputStream != null) {
               properties.load(inputStream);
               inputStream.close();
            }
         } catch (IOException e) {
            return;
         }

         String resourcePath = resourceLocation.getResourcePath();
         String toLowerCase = properties.getProperty("method", "").trim().toLowerCase();
         String toLowerCase2 = properties.getProperty("faces", "").trim().toLowerCase();
         String toLowerCase3 = properties.getProperty("matchBlocks", "").trim().toLowerCase();
         String toLowerCase4 = properties.getProperty("matchTiles", "").trim().toLowerCase();
         String toLowerCase5 = properties.getProperty("metadata", "").trim().toLowerCase();
         String trim = properties.getProperty("tiles", "").trim();
         String toLowerCase6 = properties.getProperty("biomes", "").trim().toLowerCase();
         String toLowerCase7 = properties.getProperty("renderPass", "").trim().toLowerCase();
         String[] parseStringList = this.parseStringList(toLowerCase3);
         int[] ints = new int[parseStringList.length];

         for (int i = 0; i < parseStringList.length; i++) {
            ints[i] = this.getBlockIdByName(parseStringList[i]);
         }

         int[] parseIntRanges = this.parseIntRanges(toLowerCase5, 0, 255);
         String substring = resourcePath.substring(0, resourcePath.lastIndexOf("/") + 1);
         String[] parseStringList2 = this.parseStringList(trim);
         String str = substring + "0";
         if (parseStringList2.length > 0) {
            str = parseStringList2[0].trim();
         }

         if (str.startsWith("~")) {
            str = str.replace("~", "mcpatcher");
         } else {
            str = substring + str;
         }

         if (!str.toLowerCase().endsWith(".png")) {
            str = str + ".png";
         }

         String[] split = toLowerCase6.split(" ");
         if (ints.length == 0) {
            int n = -1;
            Pattern compile = Pattern.compile(".*/block([\\d]+)[a-zA-Z]*.properties");
            Matcher matcher = compile.matcher(resourcePath);
            if (matcher.find()) {
               n = Integer.parseInt(matcher.group(1));
            } else {
               String substring2 = resourcePath.substring(resourcePath.lastIndexOf("/") + 1, resourcePath.lastIndexOf(".properties"));

               for (int i = 0; i < 4096; i++) {
                  Block blockById = Block.getBlockById(i);
                  if (blockById != null) {
                     String str2 = "";
                     if (parseIntRanges.length > 0) {
                        for (int j = 0; j < parseIntRanges.length; j++) {
                           try {
                              str2 = this.getBlockSprite(i, parseIntRanges[j]).getIconName();
                           } catch (Exception e) {
                              str2 = "";
                           }

                           if (str2.equals(substring2)) {
                              n = i;
                           }
                        }
                     } else {
                        ArrayList list = new ArrayList();

                        for (int j = 0; j < 16; j++) {
                           try {
                              str2 = this.getBlockSprite(i, j).getIconName();
                           } catch (Exception e) {
                              str2 = "";
                           }

                           if (str2.equals(substring2)) {
                              n = i;
                              list.add(j);
                           }
                        }

                        parseIntRanges = new int[list.size()];

                        for (int j = 0; j < parseIntRanges.length; j++) {
                           parseIntRanges[j] = (Integer)list.get(j);
                        }
                     }
                  }
               }
            }

            if (n != -1) {
               ints = new int[]{n};
            }
         }

         if (parseIntRanges.length == 0) {
            parseIntRanges = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
         }

         if (ints.length != 0) {
            if (!toLowerCase.equals("horizontal")
               && (toLowerCase.equals("sandstone") || toLowerCase.equals("top") || toLowerCase2.contains("top") || toLowerCase2.contains("all") || toLowerCase2.length() == 0)) {
               try {
                  for (int i = 0; i < ints.length; i++) {
                     ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getResourceDomain(), str);
                     InputStream inputStream = this.mc.getResourceManager().getResource(resourceLocation2).getInputStream();
                     BufferedImage read = ImageIO.read(inputStream);
                     inputStream.close();
                     Image scaledInstance = read.getScaledInstance(1, 1, 4);
                     BufferedImage bufferedImage = new BufferedImage(scaledInstance.getWidth(null), scaledInstance.getHeight(null), 6);
                     Graphics2D createGraphics = bufferedImage.createGraphics();
                     createGraphics.drawImage(scaledInstance, 0, 0, null);
                     createGraphics.dispose();
                     int rGB = bufferedImage.getRGB(0, 0);
                     if (ints[i] == BlockIdRepository.webId) {
                        rGB |= -16777216;
                     }

                     if (toLowerCase7.equals("3")) {
                        rGB = this.applyBlendMode(rGB);
                        int n = this.blockColors[this.getBlockKey(ints[i], parseIntRanges[0])];
                        if (n != -65025 && n != -65281) {
                           rGB = this.colorMultiplier(n, rGB);
                        }
                     }

                     if (Arrays.asList(BlockIdRepository.integerArray).contains(ints[i])) {
                        rGB = this.blendColor(ints[i], parseIntRanges[0], rGB);
                     }

                     for (int j = 0; j < parseIntRanges.length; j++) {
                        try {
                           if (!toLowerCase6.equals("")) {
                              this.colormapBlocks.add(ints[i]);

                              for (int k = 0; k < split.length; k++) {
                                 int parseBlockId = this.parseBlockId(split[k]);
                                 if (parseBlockId != -1) {
                                    this.colormapColors.put("" + this.getBlockKey(ints[i], parseIntRanges[j]) + " " + parseBlockId, rGB);
                                 }
                              }
                           } else {
                              this.blockColors[this.getBlockKey(ints[i], parseIntRanges[j])] = rGB;
                           }
                        } catch (Exception e) {
                           System.err.println("blockID + metadata (" + ints[i] + ", " + parseIntRanges[j] + ") out of range");
                        }
                     }
                  }
               } catch (IOException e) {
                  System.err.println("error getting CTM block: " + resourcePath + " " + ints[0] + " " + parseIntRanges[0] + " " + str);
               }
            }
         }
      }
   }

   public int applyBlendMode(int n) {
      if (this.blendMode.equals("color") || this.blendMode.equals("overlay")) {
         int n2 = n >> 24 & 0xFF;
         int n3 = n >> 16 & 0xFF;
         int n4 = n >> 8 & 0xFF;
         int n5 = n >> 0 & 0xFF;
         float f = (n3 + n5 + n4) / 3.0F;
         float f2 = (f - 127.5F) * 2.0F;
         n3 += (int)(n3 * (f2 / 255.0F));
         n5 += (int)(n3 * (f2 / 255.0F));
         n4 += (int)(n3 * (f2 / 255.0F));
         int abs = (int)Math.abs(f2);
         n = abs << 24 | (n3 & 0xFF) << 16 | (n4 & 0xFF) << 8 | n5 & 0xFF;
      }

      return n;
   }

   @Override
   public BufferedImage getColorPicker() {
      return this.colorPicker;
   }

   @Override
   public HashMap<String, Integer[]> getBiomeColormaps() {
      return this.biomeColormaps;
   }
}
