package net.optifine;

import com.cheatbreaker.client.CheatBreaker;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.util.EntityUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CustomColors {
   public static String paletteFormatDefault = "vanilla";
   public static CustomColormap waterColors = null;
   public static CustomColormap foliagePineColors = null;
   public static CustomColormap foliageBirchColors = null;
   public static CustomColormap swampFoliageColors = null;
   public static CustomColormap swampGrassColors = null;
   public static CustomColormap[] colorsBlockColormaps = null;
   public static CustomColormap[][] blockColormaps = (CustomColormap[][])null;
   public static CustomColormap skyColors = null;
   public static CustomColorFader skyColorFader = new CustomColorFader();
   public static CustomColormap fogColors = null;
   public static CustomColorFader fogColorFader = new CustomColorFader();
   public static CustomColormap underwaterColors = null;
   public static CustomColorFader underwaterColorFader = new CustomColorFader();
   public static CustomColormap underlavaColors = null;
   public static CustomColorFader underlavaColorFader = new CustomColorFader();
   public static LightMapPack[] lightMapPacks = null;
   public static int lightmapMinDimensionId = 0;
   public static CustomColormap redstoneColors = null;
   public static CustomColormap xpOrbColors = null;
   public static int xpOrbTime = -1;
   public static CustomColormap durabilityColors = null;
   public static CustomColormap stemColors = null;
   public static CustomColormap stemMelonColors = null;
   public static CustomColormap stemPumpkinColors = null;
   public static CustomColormap myceliumParticleColors = null;
   public static boolean useDefaultGrassFoliageColors = true;
   public static int particleWaterColor = -1;
   public static int particlePortalColor = -1;
   public static int lilyPadColor = -1;
   public static int expBarTextColor = -1;
   public static int bossTextColor = -1;
   public static int signTextColor = -1;
   public static Vec3 fogColorNether = null;
   public static Vec3 fogColorEnd = null;
   public static Vec3 skyColorEnd = null;
   public static int[] spawnEggPrimaryColors = null;
   public static int[] spawnEggSecondaryColors = null;
   public static float[][] wolfCollarColors = (float[][])null;
   public static float[][] sheepColors = (float[][])null;
   public static int[] textColors = null;
   public static int[] mapColorsOriginal = null;
   public static int[] potionColors = null;
   public static IBlockState BLOCK_STATE_DIRT = Blocks.dirt.getDefaultState();
   public static IBlockState BLOCK_STATE_WATER = Blocks.water.getDefaultState();
   public static Random random = new Random();
   public static CustomColors.IColorizer COLORIZER_GRASS = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
         BiomeGenBase colorBiome = CustomColors.getColorBiome(blockAccess, blockPos);
         return CustomColors.swampGrassColors != null && colorBiome == BiomeGenBase.swampland
            ? CustomColors.swampGrassColors.getColor(colorBiome, blockPos)
            : colorBiome.getGrassColorAtPos(blockPos);
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };
   public static CustomColors.IColorizer COLORIZER_FOLIAGE = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
         BiomeGenBase colorBiome = CustomColors.getColorBiome(blockAccess, blockPos);
         return CustomColors.swampFoliageColors != null && colorBiome == BiomeGenBase.swampland
            ? CustomColors.swampFoliageColors.getColor(colorBiome, blockPos)
            : colorBiome.getFoliageColorAtPos(blockPos);
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };
   public static CustomColors.IColorizer COLORIZER_FOLIAGE_PINE = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
         return CustomColors.foliagePineColors != null ? CustomColors.foliagePineColors.getColor(blockAccess, blockPos) : ColorizerFoliage.getFoliageColorPine();
      }

      @Override
      public boolean isColorConstant() {
         return CustomColors.foliagePineColors == null;
      }
   };
   public static CustomColors.IColorizer COLORIZER_FOLIAGE_BIRCH = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
         return CustomColors.foliageBirchColors != null ? CustomColors.foliageBirchColors.getColor(blockAccess, blockPos) : ColorizerFoliage.getFoliageColorBirch();
      }

      @Override
      public boolean isColorConstant() {
         return CustomColors.foliageBirchColors == null;
      }
   };
   public static CustomColors.IColorizer COLORIZER_WATER = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
         BiomeGenBase colorBiome = CustomColors.getColorBiome(blockAccess, blockPos);
         return CustomColors.waterColors != null
            ? CustomColors.waterColors.getColor(colorBiome, blockPos)
            : (
               Reflector.ForgeBiome_getWaterColorMultiplier.exists()
                  ? Reflector.callInt(colorBiome, Reflector.ForgeBiome_getWaterColorMultiplier)
                  : colorBiome.waterColorMultiplier
            );
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };

   public static void update() {
      paletteFormatDefault = "vanilla";
      waterColors = null;
      foliageBirchColors = null;
      foliagePineColors = null;
      swampGrassColors = null;
      swampFoliageColors = null;
      skyColors = null;
      fogColors = null;
      underwaterColors = null;
      underlavaColors = null;
      redstoneColors = null;
      xpOrbColors = null;
      xpOrbTime = -1;
      durabilityColors = null;
      stemColors = null;
      myceliumParticleColors = null;
      lightMapPacks = null;
      particleWaterColor = -1;
      particlePortalColor = -1;
      lilyPadColor = -1;
      expBarTextColor = -1;
      bossTextColor = -1;
      signTextColor = -1;
      fogColorNether = null;
      fogColorEnd = null;
      skyColorEnd = null;
      colorsBlockColormaps = null;
      blockColormaps = (CustomColormap[][])null;
      useDefaultGrassFoliageColors = true;
      spawnEggPrimaryColors = null;
      spawnEggSecondaryColors = null;
      wolfCollarColors = (float[][])null;
      sheepColors = (float[][])null;
      textColors = null;
      setMapColors(mapColorsOriginal);
      potionColors = null;
      paletteFormatDefault = getValidProperty("mcpatcher/color.properties", "palette.format", CustomColormap.FORMAT_STRINGS, "vanilla");
      String str = "mcpatcher/colormap/";
      String[] strs = new String[]{"water.png", "watercolorX.png"};
      waterColors = getCustomColors(str, strs, 256, 256);
      updateUseDefaultGrassFoliageColors();
      if (Config.isCustomColors()) {
         String[] strs2 = new String[]{"pine.png", "pinecolor.png"};
         foliagePineColors = getCustomColors(str, strs2, 256, 256);
         String[] strs3 = new String[]{"birch.png", "birchcolor.png"};
         foliageBirchColors = getCustomColors(str, strs3, 256, 256);
         String[] strs4 = new String[]{"swampgrass.png", "swampgrasscolor.png"};
         swampGrassColors = getCustomColors(str, strs4, 256, 256);
         String[] strs5 = new String[]{"swampfoliage.png", "swampfoliagecolor.png"};
         swampFoliageColors = getCustomColors(str, strs5, 256, 256);
         String[] strs6 = new String[]{"sky0.png", "skycolor0.png"};
         skyColors = getCustomColors(str, strs6, 256, 256);
         String[] strs7 = new String[]{"fog0.png", "fogcolor0.png"};
         fogColors = getCustomColors(str, strs7, 256, 256);
         String[] strs8 = new String[]{"underwater.png", "underwatercolor.png"};
         underwaterColors = getCustomColors(str, strs8, 256, 256);
         String[] strs9 = new String[]{"underlava.png", "underlavacolor.png"};
         underlavaColors = getCustomColors(str, strs9, 256, 256);
         String[] strs10 = new String[]{"redstone.png", "redstonecolor.png"};
         redstoneColors = getCustomColors(str, strs10, 16, 1);
         xpOrbColors = getCustomColors(str + "xporb.png", -1, -1);
         durabilityColors = getCustomColors(str + "durability.png", -1, -1);
         String[] strs11 = new String[]{"stem.png", "stemcolor.png"};
         stemColors = getCustomColors(str, strs11, 8, 1);
         stemPumpkinColors = getCustomColors(str + "pumpkinstem.png", 8, 1);
         stemMelonColors = getCustomColors(str + "melonstem.png", 8, 1);
         String[] strs12 = new String[]{"myceliumparticle.png", "myceliumparticlecolor.png"};
         myceliumParticleColors = getCustomColors(str, strs12, -1, -1);
         Pair parseLightMapPacks2 = parseLightMapPacks();
         lightMapPacks = (LightMapPack[])parseLightMapPacks2.getLeft();
         lightmapMinDimensionId = (Integer)parseLightMapPacks2.getRight();
         readColorProperties("mcpatcher/color.properties");
         blockColormaps = readBlockColormaps(new String[]{str + "custom/", str + "blocks/"}, colorsBlockColormaps, 256, 256);
         updateUseDefaultGrassFoliageColors();
      }
   }

   public static String getValidProperty(String str, String str2, String[] strs, String str3) {
      try {
         ResourceLocation resourceLocation = new ResourceLocation(str);
         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return str3;
         } else {
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(resourceStream);
            resourceStream.close();
            String property = propertiesOrdered.getProperty(str2);
            if (property == null) {
               return str3;
            } else {
               List asList = Arrays.asList(strs);
               if (!asList.contains(property)) {
                  warn("Invalid value: " + str2 + "=" + property);
                  warn("Expected values: " + Config.arrayToString(strs));
                  return str3;
               } else {
                  dbg("" + str2 + "=" + property);
                  return property;
               }
            }
         }
      } catch (FileNotFoundException e) {
         return str3;
      } catch (IOException e) {
         e.printStackTrace();
         return str3;
      }
   }

   public static Pair<LightMapPack[], Integer> parseLightMapPacks() {
      String str = "mcpatcher/lightmap/world";
      String str2 = ".png";
      String[] collectFiles = ResUtils.collectFiles(str, str2);
      HashMap map = new HashMap();

      for (int i = 0; i < collectFiles.length; i++) {
         String str3 = collectFiles[i];
         String removePrefixSuffix = StrUtils.removePrefixSuffix(str3, str, str2);
         int parseInt = Config.parseInt(removePrefixSuffix, Integer.MIN_VALUE);
         if (parseInt == Integer.MIN_VALUE) {
            warn("Invalid dimension ID: " + removePrefixSuffix + ", path: " + str3);
         } else {
            map.put(parseInt, str3);
         }
      }

      Set keySet = map.keySet();
      Integer[] toArray = (Integer[])keySet.toArray(new Integer[keySet.size()]);
      Arrays.sort(toArray);
      if (toArray.length <= 0) {
         return new ImmutablePair(null, 0);
      }

      int n = toArray[0];
      int n2 = toArray[toArray.length - 1];
      int n3 = n2 - n + 1;
      CustomColormap[] customColormaps = new CustomColormap[n3];

      for (int i = 0; i < toArray.length; i++) {
         Integer integer = toArray[i];
         String str3 = (String)map.get(integer);
         CustomColormap customColors = getCustomColors(str3, -1, -1);
         if (customColors != null) {
            if (customColors.getWidth() < 16) {
               warn("Invalid lightmap width: " + customColors.getWidth() + ", path: " + str3);
            } else {
               int n4 = integer - n;
               customColormaps[n4] = customColors;
            }
         }
      }

      LightMapPack[] lightMapPacks = new LightMapPack[customColormaps.length];

      for (int i = 0; i < customColormaps.length; i++) {
         CustomColormap customColormap = customColormaps[i];
         if (customColormap != null) {
            String name = customColormap.name;
            String basePath = customColormap.basePath;
            CustomColormap customColors = getCustomColors(basePath + "/" + name + "_rain.png", -1, -1);
            CustomColormap customColors2 = getCustomColors(basePath + "/" + name + "_thunder.png", -1, -1);
            LightMap lightMap = new LightMap(customColormap);
            LightMap lightMap2 = customColors != null ? new LightMap(customColors) : null;
            LightMap lightMap3 = customColors2 != null ? new LightMap(customColors2) : null;
            LightMapPack lightMapPack = new LightMapPack(lightMap, lightMap2, lightMap3);
            lightMapPacks[i] = lightMapPack;
         }
      }

      return new ImmutablePair(lightMapPacks, n);
   }

   public static int getTextureHeight(String str, int n) {
      try {
         InputStream resourceStream = Config.getResourceStream(new ResourceLocation(str));
         if (resourceStream == null) {
            return n;
         }

         BufferedImage read = ImageIO.read(resourceStream);
         resourceStream.close();
         return read == null ? n : read.getHeight();
      } catch (IOException e) {
         return n;
      }
   }

   public static void readColorProperties(String str) {
      try {
         ResourceLocation resourceLocation = new ResourceLocation(str);
         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return;
         }

         dbg("Loading " + str);
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(resourceStream);
         resourceStream.close();
         particleWaterColor = readColor(propertiesOrdered, new String[]{"particle.water", "drop.water"});
         particlePortalColor = readColor(propertiesOrdered, "particle.portal");
         lilyPadColor = readColor(propertiesOrdered, "lilypad");
         expBarTextColor = readColor(propertiesOrdered, "text.xpbar");
         bossTextColor = readColor(propertiesOrdered, "text.boss");
         signTextColor = readColor(propertiesOrdered, "text.sign");
         fogColorNether = readColorVec3(propertiesOrdered, "fog.nether");
         fogColorEnd = readColorVec3(propertiesOrdered, "fog.end");
         skyColorEnd = readColorVec3(propertiesOrdered, "sky.end");
         colorsBlockColormaps = readCustomColormaps(propertiesOrdered, str);
         spawnEggPrimaryColors = readSpawnEggColors(propertiesOrdered, str, "egg.shell.", "Spawn egg shell");
         spawnEggSecondaryColors = readSpawnEggColors(propertiesOrdered, str, "egg.spots.", "Spawn egg spot");
         wolfCollarColors = readDyeColors(propertiesOrdered, str, "collar.", "Wolf collar");
         sheepColors = readDyeColors(propertiesOrdered, str, "sheep.", "Sheep");
         textColors = readTextColors(propertiesOrdered, str, "text.code.", "Text");
         int[] readMapColors2 = readMapColors(propertiesOrdered, str, "map.", "Map");
         if (readMapColors2 != null) {
            if (mapColorsOriginal == null) {
               mapColorsOriginal = getMapColors();
            }

            setMapColors(readMapColors2);
         }

         potionColors = readPotionColors(propertiesOrdered, str, "potion.", "Potion");
         xpOrbTime = Config.parseInt(propertiesOrdered.getProperty("xporb.time"), -1);
      } catch (FileNotFoundException e) {
         return;
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static CustomColormap[] readCustomColormaps(Properties properties, String str) {
      ArrayList list = new ArrayList();
      String str2 = "palette.block.";
      HashMap map = new HashMap();

      for (Object obj : properties.keySet()) {
         String str3 = (String)obj;
         String property = properties.getProperty(str3);
         if (str3.startsWith(str2)) {
            map.put(str3, property);
         }
      }

      String[] toArray = (String[])map.keySet().toArray(new String[map.size()]);

      for (int i = 0; i < toArray.length; i++) {
         String str3 = toArray[i];
         String property = properties.getProperty(str3);
         dbg("Block palette: " + str3 + " = " + property);
         String substring = str3.substring(str2.length());
         String basePath = TextureUtils.getBasePath(str);
         substring = TextureUtils.fixResourcePath(substring, basePath);
         CustomColormap customColors = getCustomColors(substring, 256, 256);
         if (customColors == null) {
            warn("Colormap not found: " + substring);
         } else {
            ConnectedParser connectedParser = new ConnectedParser("CustomColors");
            MatchBlock[] parseMatchBlocks = connectedParser.parseMatchBlocks(property);
            if (parseMatchBlocks != null && parseMatchBlocks.length > 0) {
               for (int j = 0; j < parseMatchBlocks.length; j++) {
                  MatchBlock matchBlock = parseMatchBlocks[j];
                  customColors.addMatchBlock(matchBlock);
               }

               list.add(customColors);
            } else {
               warn("Invalid match blocks: " + property);
            }
         }
      }

      return list.size() <= 0 ? null : (CustomColormap[])list.toArray(new CustomColormap[list.size()]);
   }

   public static CustomColormap[][] readBlockColormaps(String[] strs, CustomColormap[] customColormaps, int n, int n2) {
      String[] collectFiles = ResUtils.collectFiles(strs, new String[]{".properties"});
      Arrays.sort(collectFiles);
      ArrayList list = new ArrayList();

      for (int i = 0; i < collectFiles.length; i++) {
         String str = collectFiles[i];
         dbg("Block colormap: " + str);

         try {
            ResourceLocation resourceLocation = new ResourceLocation("minecraft", str);
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            if (resourceStream == null) {
               warn("File not found: " + str);
            } else {
               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(resourceStream);
               resourceStream.close();
               CustomColormap customColormap = new CustomColormap(propertiesOrdered, str, n, n2, paletteFormatDefault);
               if (customColormap.isValid(str) && customColormap.isValidMatchBlocks(str)) {
                  addToBlockList(customColormap, list);
               }
            }
         } catch (FileNotFoundException e) {
            warn("File not found: " + str);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      if (customColormaps != null) {
         for (int i = 0; i < customColormaps.length; i++) {
            CustomColormap customColormap = customColormaps[i];
            addToBlockList(customColormap, list);
         }
      }

      return list.size() <= 0 ? (CustomColormap[][])null : blockListToArray(list);
   }

   public static void addToBlockList(CustomColormap customColormap, List list) {
      int[] matchBlockIds = customColormap.getMatchBlockIds();
      if (matchBlockIds != null && matchBlockIds.length > 0) {
         for (int i = 0; i < matchBlockIds.length; i++) {
            int n = matchBlockIds[i];
            if (n < 0) {
               warn("Invalid block ID: " + n);
            } else {
               addToList(customColormap, list, n);
            }
         }
      } else {
         warn("No match blocks: " + Config.arrayToString(matchBlockIds));
      }
   }

   public static void addToList(CustomColormap customColormap, List list, int n) {
      while (n >= list.size()) {
         list.add(null);
      }

      List list2 = (List)list.get(n);
      if (list2 == null) {
         list2 = new ArrayList();
         list.set(n, list2);
      }

      list2.add(customColormap);
   }

   public static CustomColormap[][] blockListToArray(List list) {
      CustomColormap[][] customColormapses = new CustomColormap[list.size()][];

      for (int i = 0; i < list.size(); i++) {
         List list2 = (List)list.get(i);
         if (list2 != null) {
            CustomColormap[] toArray = (CustomColormap[])list2.toArray(new CustomColormap[list2.size()]);
            customColormapses[i] = toArray;
         }
      }

      return customColormapses;
   }

   public static int readColor(Properties properties, String[] strs) {
      for (int i = 0; i < strs.length; i++) {
         String str = strs[i];
         int readColor2 = readColor(properties, str);
         if (readColor2 >= 0) {
            return readColor2;
         }
      }

      return -1;
   }

   public static int readColor(Properties properties, String str) {
      String property = properties.getProperty(str);
      if (property == null) {
         return -1;
      } else {
         property = property.trim();
         int parseColor2 = parseColor(property);
         if (parseColor2 < 0) {
            warn("Invalid color: " + str + " = " + property);
            return parseColor2;
         } else {
            dbg(str + " = " + property);
            return parseColor2;
         }
      }
   }

   public static int parseColor(String str) {
      if (str == null) {
         return -1;
      }

      str = str.trim();

      try {
         return Integer.parseInt(str, 16) & 16777215;
      } catch (NumberFormatException e) {
         return -1;
      }
   }

   public static Vec3 readColorVec3(Properties properties, String str) {
      int readColor2 = readColor(properties, str);
      if (readColor2 < 0) {
         return null;
      }

      int n = readColor2 >> 16 & 0xFF;
      int n2 = readColor2 >> 8 & 0xFF;
      int n3 = readColor2 & 0xFF;
      float f = n / 255.0F;
      float f2 = n2 / 255.0F;
      float f3 = n3 / 255.0F;
      return new Vec3(f, f2, f3);
   }

   public static CustomColormap getCustomColors(String str, String[] strs, int n, int n2) {
      for (int i = 0; i < strs.length; i++) {
         String str2 = strs[i];
         str2 = str + str2;
         CustomColormap customColors = getCustomColors(str2, n, n2);
         if (customColors != null) {
            return customColors;
         }
      }

      return null;
   }

   public static CustomColormap getCustomColors(String str, int n, int n2) {
      try {
         ResourceLocation resourceLocation = new ResourceLocation(str);
         if (!Config.hasResource(resourceLocation)) {
            return null;
         }

         dbg("Colormap " + str);
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         String replaceSuffix = StrUtils.replaceSuffix(str, ".png", ".properties");
         ResourceLocation resourceLocation2 = new ResourceLocation(replaceSuffix);
         if (Config.hasResource(resourceLocation2)) {
            InputStream resourceStream = Config.getResourceStream(resourceLocation2);
            propertiesOrdered.load(resourceStream);
            resourceStream.close();
            dbg("Colormap properties: " + replaceSuffix);
         } else {
            propertiesOrdered.put("format", paletteFormatDefault);
            propertiesOrdered.put("source", str);
            replaceSuffix = str;
         }

         CustomColormap customColormap = new CustomColormap(propertiesOrdered, replaceSuffix, n, n2, paletteFormatDefault);
         return !customColormap.isValid(replaceSuffix) ? null : customColormap;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static void updateUseDefaultGrassFoliageColors() {
      useDefaultGrassFoliageColors = foliageBirchColors == null
         && foliagePineColors == null
         && swampGrassColors == null
         && swampFoliageColors == null
         && Config.isSwampColors()
         && Config.isSmoothBiomes();
   }

   public static int getColorMultiplier(BakedQuad bakedQuad, IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos, RenderEnv renderEnv) {
      Block block = blockState.getBlock();
      IBlockState blockState2 = renderEnv.getBlockState();
      if (blockColormaps != null) {
         if (!bakedQuad.hasTintIndex()) {
            if (block == Blocks.grass) {
               blockState2 = BLOCK_STATE_DIRT;
            }

            if (block == Blocks.redstone_wire) {
               return -1;
            }
         }

         if (block == Blocks.double_plant && renderEnv.getMetadata() >= 8) {
            blockPos = blockPos.down();
            blockState2 = blockAccess.getBlockState(blockPos);
         }

         CustomColormap blockColormap = getBlockColormap(blockState2);
         if (blockColormap != null) {
            if (Config.isSmoothBiomes() && !blockColormap.isColorConstant()) {
               return getSmoothColorMultiplier(blockState, blockAccess, blockPos, blockColormap, renderEnv.getColorizerBlockPosM());
            }

            return blockColormap.getColor(blockAccess, blockPos);
         }
      }

      if (!bakedQuad.hasTintIndex()) {
         return -1;
      }

      if (block == Blocks.waterlily) {
         return getLilypadColorMultiplier(blockAccess, blockPos);
      }

      if (block == Blocks.redstone_wire) {
         return getRedstoneColor(renderEnv.getBlockState());
      }

      if (block instanceof BlockStem) {
         return getStemColorMultiplier(block, blockAccess, blockPos, renderEnv);
      }

      if (useDefaultGrassFoliageColors) {
         return -1;
      }

      int metadata = renderEnv.getMetadata();
      CustomColors.IColorizer colorizer;
      if (block == Blocks.grass || block == Blocks.tallgrass || block == Blocks.double_plant) {
         colorizer = COLORIZER_GRASS;
      } else if (block == Blocks.double_plant) {
         colorizer = COLORIZER_GRASS;
         if (metadata >= 8) {
            blockPos = blockPos.down();
         }
      } else if (block == Blocks.leaves) {
         switch (metadata & 3) {
            case 0:
               colorizer = COLORIZER_FOLIAGE;
               break;
            case 1:
               colorizer = COLORIZER_FOLIAGE_PINE;
               break;
            case 2:
               colorizer = COLORIZER_FOLIAGE_BIRCH;
               break;
            default:
               colorizer = COLORIZER_FOLIAGE;
         }
      } else if (block == Blocks.leaves2) {
         colorizer = COLORIZER_FOLIAGE;
      } else {
         if (block != Blocks.vine) {
            return -1;
         }

         colorizer = COLORIZER_FOLIAGE;
      }

      return Config.isSmoothBiomes() && !colorizer.isColorConstant()
         ? getSmoothColorMultiplier(blockState, blockAccess, blockPos, colorizer, renderEnv.getColorizerBlockPosM())
         : colorizer.getColor(blockState2, blockAccess, blockPos);
   }

   public static BiomeGenBase getColorBiome(IBlockAccess blockAccess, BlockPos blockPos) {
      BiomeGenBase biomeGenForCoords = blockAccess.getBiomeGenForCoords(blockPos);
      if (biomeGenForCoords == BiomeGenBase.swampland && !Config.isSwampColors()) {
         biomeGenForCoords = BiomeGenBase.plains;
      }

      return biomeGenForCoords;
   }

   public static CustomColormap getBlockColormap(IBlockState blockState) {
      if (blockColormaps == null) {
         return null;
      }

      if (!(blockState instanceof BlockStateBase)) {
         return null;
      }

      BlockStateBase blockStateBase = (BlockStateBase)blockState;
      int blockId = blockStateBase.getBlockId();
      if (blockId >= 0 && blockId < blockColormaps.length) {
         CustomColormap[] customColormaps = blockColormaps[blockId];
         if (customColormaps == null) {
            return null;
         }

         for (int i = 0; i < customColormaps.length; i++) {
            CustomColormap customColormap = customColormaps[i];
            if (customColormap.matchesBlock(blockStateBase)) {
               return customColormap;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static int getSmoothColorMultiplier(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos, CustomColors.IColorizer colorizer, BlockPosM blockPosM) {
      int n = 0;
      int n2 = 0;
      int n3 = 0;
      int n4 = blockPos.getX();
      int n5 = blockPos.getY();
      int n6 = blockPos.getZ();
      BlockPosM blockPosM2 = blockPosM;

      for (int i = n4 - 1; i <= n4 + 1; i++) {
         for (int j = n6 - 1; j <= n6 + 1; j++) {
            blockPosM2.setXyz(i, n5, j);
            int color = colorizer.getColor(blockState, blockAccess, blockPosM2);
            n += color >> 16 & 0xFF;
            n2 += color >> 8 & 0xFF;
            n3 += color & 0xFF;
         }
      }

      int n7 = n / 9;
      int n8 = n2 / 9;
      int n9 = n3 / 9;
      return n7 << 16 | n8 << 8 | n9;
   }

   public static int getFluidColor(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, RenderEnv renderEnv) {
      Block block = blockState.getBlock();
      CustomColors.IColorizer blockColormap = getBlockColormap(blockState);
      if (blockColormap == null && blockState.getBlock().getMaterial() == Material.water) {
         blockColormap = COLORIZER_WATER;
      }

      return blockColormap == null
         ? block.colorMultiplier(blockAccess, blockPos, 0)
         : (
            Config.isSmoothBiomes() && !blockColormap.isColorConstant()
               ? getSmoothColorMultiplier(blockState, blockAccess, blockPos, blockColormap, renderEnv.getColorizerBlockPosM())
               : blockColormap.getColor(blockState, blockAccess, blockPos)
         );
   }

   public static void updatePortalFX(EntityFX entityFX) {
      if (particlePortalColor >= 0) {
         int particlePortalColor2 = particlePortalColor;
         int n = particlePortalColor2 >> 16 & 0xFF;
         int n2 = particlePortalColor2 >> 8 & 0xFF;
         int n3 = particlePortalColor2 & 0xFF;
         float f = n / 255.0F;
         float f2 = n2 / 255.0F;
         float f3 = n3 / 255.0F;
         entityFX.setRBGColorF(f, f2, f3);
      }
   }

   public static void updateMyceliumFX(EntityFX entityFX) {
      if (myceliumParticleColors != null) {
         int colorRandom = myceliumParticleColors.getColorRandom();
         int n = colorRandom >> 16 & 0xFF;
         int n2 = colorRandom >> 8 & 0xFF;
         int n3 = colorRandom & 0xFF;
         float f = n / 255.0F;
         float f2 = n2 / 255.0F;
         float f3 = n3 / 255.0F;
         entityFX.setRBGColorF(f, f2, f3);
      }
   }

   public static int getRedstoneColor(IBlockState blockState) {
      if (redstoneColors == null) {
         return -1;
      }

      int redstoneLevel = getRedstoneLevel(blockState, 15);
      return redstoneColors.getColor(redstoneLevel);
   }

   public static void updateReddustFX(EntityFX entityFX, IBlockAccess blockAccess, double d, double d2, double d3) {
      if (redstoneColors != null) {
         IBlockState blockState = blockAccess.getBlockState(new BlockPos(d, d2, d3));
         int redstoneLevel = getRedstoneLevel(blockState, 15);
         int color = redstoneColors.getColor(redstoneLevel);
         int n = color >> 16 & 0xFF;
         int n2 = color >> 8 & 0xFF;
         int n3 = color & 0xFF;
         float f = n / 255.0F;
         float f2 = n2 / 255.0F;
         float f3 = n3 / 255.0F;
         entityFX.setRBGColorF(f, f2, f3);
      }
   }

   public static int getRedstoneLevel(IBlockState blockState, int n) {
      Block block = blockState.getBlock();
      if (!(block instanceof BlockRedstoneWire)) {
         return n;
      }

      Comparable value = blockState.getValue(BlockRedstoneWire.POWER);
      if (!(value instanceof Integer)) {
         return n;
      }

      Integer integer = (Integer)value;
      return integer;
   }

   public static float getXpOrbTimer(float f) {
      if (xpOrbTime <= 0) {
         return f;
      }

      float f2 = 628.0F / xpOrbTime;
      return f * f2;
   }

   public static int getXpOrbColor(float f) {
      if (xpOrbColors == null) {
         return -1;
      }

      int round = (int)Math.round((MathHelper.sin(f) + 1.0F) * (xpOrbColors.getLength() - 1) / 2.0);
      return xpOrbColors.getColor(round);
   }

   public static int getDurabilityColor(int n) {
      if (durabilityColors == null) {
         return -1;
      }

      int n2 = n * durabilityColors.getLength() / 255;
      return durabilityColors.getColor(n2);
   }

   public static void updateWaterFX(EntityFX entityFX, IBlockAccess blockAccess, double d, double d2, double d3, RenderEnv renderEnv) {
      if (waterColors != null || blockColormaps != null || particleWaterColor >= 0) {
         BlockPos blockPos = new BlockPos(d, d2, d3);
         renderEnv.reset(BLOCK_STATE_WATER, blockPos);
         int fluidColor = getFluidColor(blockAccess, BLOCK_STATE_WATER, blockPos, renderEnv);
         int n = fluidColor >> 16 & 0xFF;
         int n2 = fluidColor >> 8 & 0xFF;
         int n3 = fluidColor & 0xFF;
         float f = n / 255.0F;
         float f2 = n2 / 255.0F;
         float f3 = n3 / 255.0F;
         if (particleWaterColor >= 0) {
            int n4 = particleWaterColor >> 16 & 0xFF;
            int n5 = particleWaterColor >> 8 & 0xFF;
            int n6 = particleWaterColor & 0xFF;
            f *= n4 / 255.0F;
            f2 *= n5 / 255.0F;
            f3 *= n6 / 255.0F;
         }

         entityFX.setRBGColorF(f, f2, f3);
      }
   }

   public static int getLilypadColorMultiplier(IBlockAccess blockAccess, BlockPos blockPos) {
      return lilyPadColor < 0 ? Blocks.waterlily.colorMultiplier(blockAccess, blockPos) : lilyPadColor;
   }

   public static Vec3 getFogColorNether(Vec3 vec3) {
      return fogColorNether == null ? vec3 : fogColorNether;
   }

   public static Vec3 getFogColorEnd(Vec3 vec3) {
      return fogColorEnd == null ? vec3 : fogColorEnd;
   }

   public static Vec3 getSkyColorEnd(Vec3 vec3) {
      return skyColorEnd == null ? vec3 : skyColorEnd;
   }

   public static Vec3 getSkyColor(Vec3 vec3, IBlockAccess blockAccess, double d, double d2, double d3) {
      if (skyColors == null) {
         return vec3;
      }

      int colorSmooth = skyColors.getColorSmooth(blockAccess, d, d2, d3, 3);
      int n = colorSmooth >> 16 & 0xFF;
      int n2 = colorSmooth >> 8 & 0xFF;
      int n3 = colorSmooth & 0xFF;
      float f = n / 255.0F;
      float f2 = n2 / 255.0F;
      float f3 = n3 / 255.0F;
      float f4 = (float)vec3.xCoord / 0.5F;
      float f5 = (float)vec3.yCoord / 0.66275F;
      float zCoord = (float)vec3.zCoord;
      f *= f4;
      f2 *= f5;
      f3 *= zCoord;
      return skyColorFader.getColor(f, f2, f3);
   }

   public static Vec3 getFogColor(Vec3 vec3, IBlockAccess blockAccess, double d, double d2, double d3) {
      if (fogColors == null) {
         return vec3;
      }

      int colorSmooth = fogColors.getColorSmooth(blockAccess, d, d2, d3, 3);
      int n = colorSmooth >> 16 & 0xFF;
      int n2 = colorSmooth >> 8 & 0xFF;
      int n3 = colorSmooth & 0xFF;
      float f = n / 255.0F;
      float f2 = n2 / 255.0F;
      float f3 = n3 / 255.0F;
      float f4 = (float)vec3.xCoord / 0.753F;
      float f5 = (float)vec3.yCoord / 0.8471F;
      float zCoord = (float)vec3.zCoord;
      f *= f4;
      f2 *= f5;
      f3 *= zCoord;
      return fogColorFader.getColor(f, f2, f3);
   }

   public static Vec3 getUnderwaterColor(IBlockAccess blockAccess, double d, double d2, double d3) {
      return getUnderFluidColor(blockAccess, d, d2, d3, underwaterColors, underwaterColorFader);
   }

   public static Vec3 getUnderlavaColor(IBlockAccess blockAccess, double d, double d2, double d3) {
      return getUnderFluidColor(blockAccess, d, d2, d3, underlavaColors, underlavaColorFader);
   }

   public static Vec3 getUnderFluidColor(IBlockAccess blockAccess, double d, double d2, double d3, CustomColormap customColormap, CustomColorFader customColorFader) {
      if (customColormap == null) {
         return null;
      }

      int colorSmooth = customColormap.getColorSmooth(blockAccess, d, d2, d3, 3);
      int n = colorSmooth >> 16 & 0xFF;
      int n2 = colorSmooth >> 8 & 0xFF;
      int n3 = colorSmooth & 0xFF;
      float f = n / 255.0F;
      float f2 = n2 / 255.0F;
      float f3 = n3 / 255.0F;
      return customColorFader.getColor(f, f2, f3);
   }

   public static int getStemColorMultiplier(Block block, IBlockAccess blockAccess, BlockPos blockPos, RenderEnv renderEnv) {
      CustomColormap stemColors2 = stemColors;
      if (block == Blocks.pumpkin_stem && stemPumpkinColors != null) {
         stemColors2 = stemPumpkinColors;
      }

      if (block == Blocks.melon_stem && stemMelonColors != null) {
         stemColors2 = stemMelonColors;
      }

      if (stemColors2 == null) {
         return -1;
      }

      int metadata = renderEnv.getMetadata();
      return stemColors2.getColor(metadata);
   }

   public static boolean updateLightmap(World world, float f, int[] ints, boolean flag, float f2) {
      if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().enableFpsBoost.getValue()
         && (Boolean)CheatBreaker.getInstance().getKeybindSettings().fullbright.getValue()) {
         return false;
      } else if (world == null) {
         return false;
      } else if (lightMapPacks == null) {
         return false;
      } else {
         int dimensionId = world.provider.getDimensionId();
         int n = dimensionId - lightmapMinDimensionId;
         if (n >= 0 && n < lightMapPacks.length) {
            LightMapPack lightMapPack = lightMapPacks[n];
            return lightMapPack != null && lightMapPack.updateLightmap(world, f, ints, flag, f2);
         } else {
            return false;
         }
      }
   }

   public static Vec3 getWorldFogColor(Vec3 vec3, World world, Entity entity, float f) {
      int dimensionId = world.provider.getDimensionId();
      switch (dimensionId) {
         case -1:
            vec3 = getFogColorNether(vec3);
            break;
         case 0:
            Minecraft minecraft = Minecraft.getMinecraft();
            vec3 = getFogColor(vec3, minecraft.theWorld, entity.posX, entity.posY + 1.0, entity.posZ);
            break;
         case 1:
            vec3 = getFogColorEnd(vec3);
      }

      return vec3;
   }

   public static Vec3 getWorldSkyColor(Vec3 vec3, World world, Entity entity, float f) {
      int dimensionId = world.provider.getDimensionId();
      switch (dimensionId) {
         case 0:
            Minecraft minecraft = Minecraft.getMinecraft();
            vec3 = getSkyColor(vec3, minecraft.theWorld, entity.posX, entity.posY + 1.0, entity.posZ);
            break;
         case 1:
            vec3 = getSkyColorEnd(vec3);
      }

      return vec3;
   }

   public static int[] readSpawnEggColors(Properties properties, String str, String str2, String str3) {
      ArrayList list = new ArrayList();
      Set keySet = properties.keySet();
      int n = 0;

      for (Object obj : keySet) {
         String str4 = (String)obj;
         String property = properties.getProperty(str4);
         if (str4.startsWith(str2)) {
            String removePrefix = StrUtils.removePrefix(str4, str2);
            int entityIdByName = EntityUtils.getEntityIdByName(removePrefix);
            if (entityIdByName < 0) {
               warn("Invalid spawn egg name: " + str4);
            } else {
               int parseColor2 = parseColor(property);
               if (parseColor2 < 0) {
                  warn("Invalid spawn egg color: " + str4 + " = " + property);
               } else {
                  while (list.size() <= entityIdByName) {
                     list.add(-1);
                  }

                  list.set(entityIdByName, parseColor2);
                  n++;
               }
            }
         }
      }

      if (n <= 0) {
         return null;
      }

      dbg(str3 + " colors: " + n);
      int[] ints = new int[list.size()];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = (Integer)list.get(i);
      }

      return ints;
   }

   public static int getSpawnEggColor(ItemMonsterPlacer itemMonsterPlacer, ItemStack itemStack, int n, int n2) {
      int metadata = itemStack.getMetadata();
      int[] ints = n == 0 ? spawnEggPrimaryColors : spawnEggSecondaryColors;
      if (ints == null) {
         return n2;
      } else if (metadata >= 0 && metadata < ints.length) {
         int n3 = ints[metadata];
         return n3 < 0 ? n2 : n3;
      } else {
         return n2;
      }
   }

   public static int getColorFromItemStack(ItemStack itemStack, int n, int n2) {
      if (itemStack == null) {
         return n2;
      }

      Item item = itemStack.getItem();
      return item == null ? n2 : (item instanceof ItemMonsterPlacer ? getSpawnEggColor((ItemMonsterPlacer)item, itemStack, n, n2) : n2);
   }

   public static float[][] readDyeColors(Properties properties, String str, String str2, String str3) {
      EnumDyeColor[] values = EnumDyeColor.values();
      HashMap map = new HashMap();

      for (int i = 0; i < values.length; i++) {
         EnumDyeColor enumDyeColor = values[i];
         map.put(enumDyeColor.getName(), enumDyeColor);
      }

      float[][] floatses = new float[values.length][];
      int n = 0;

      for (Object obj : properties.keySet()) {
         String str4 = (String)obj;
         String property = properties.getProperty(str4);
         if (str4.startsWith(str2)) {
            String removePrefix = StrUtils.removePrefix(str4, str2);
            if (removePrefix.equals("lightBlue")) {
               removePrefix = "light_blue";
            }

            EnumDyeColor enumDyeColor = (EnumDyeColor)map.get(removePrefix);
            int parseColor2 = parseColor(property);
            if (enumDyeColor != null && parseColor2 >= 0) {
               float[] floats = new float[]{(parseColor2 >> 16 & 0xFF) / 255.0F, (parseColor2 >> 8 & 0xFF) / 255.0F, (parseColor2 & 0xFF) / 255.0F};
               floatses[enumDyeColor.ordinal()] = floats;
               n++;
            } else {
               warn("Invalid color: " + str4 + " = " + property);
            }
         }
      }

      if (n <= 0) {
         return (float[][])null;
      }

      dbg(str3 + " colors: " + n);
      return floatses;
   }

   public static float[] getDyeColors(EnumDyeColor enumDyeColor, float[][] floatses, float[] floats) {
      if (floatses == null) {
         return floats;
      }

      if (enumDyeColor == null) {
         return floats;
      }

      float[] floats2 = floatses[enumDyeColor.ordinal()];
      return floats2 == null ? floats : floats2;
   }

   public static float[] getWolfCollarColors(EnumDyeColor enumDyeColor, float[] floats) {
      return getDyeColors(enumDyeColor, wolfCollarColors, floats);
   }

   public static float[] getSheepColors(EnumDyeColor enumDyeColor, float[] floats) {
      return getDyeColors(enumDyeColor, sheepColors, floats);
   }

   public static int[] readTextColors(Properties properties, String str, String str2, String str3) {
      int[] ints = new int[32];
      Arrays.fill(ints, -1);
      int n = 0;

      for (Object obj : properties.keySet()) {
         String str4 = (String)obj;
         String property = properties.getProperty(str4);
         if (str4.startsWith(str2)) {
            String removePrefix = StrUtils.removePrefix(str4, str2);
            int parseInt = Config.parseInt(removePrefix, -1);
            int parseColor2 = parseColor(property);
            if (parseInt >= 0 && parseInt < ints.length && parseColor2 >= 0) {
               ints[parseInt] = parseColor2;
               n++;
            } else {
               warn("Invalid color: " + str4 + " = " + property);
            }
         }
      }

      if (n <= 0) {
         return null;
      }

      dbg(str3 + " colors: " + n);
      return ints;
   }

   public static int getTextColor(int n, int n2) {
      if (textColors == null) {
         return n2;
      } else if (n >= 0 && n < textColors.length) {
         int n3 = textColors[n];
         return n3 < 0 ? n2 : n3;
      } else {
         return n2;
      }
   }

   public static int[] readMapColors(Properties properties, String str, String str2, String str3) {
      int[] ints = new int[MapColor.mapColorArray.length];
      Arrays.fill(ints, -1);
      int n = 0;

      for (Object obj : properties.keySet()) {
         String str4 = (String)obj;
         String property = properties.getProperty(str4);
         if (str4.startsWith(str2)) {
            String removePrefix = StrUtils.removePrefix(str4, str2);
            int mapColorIndex = getMapColorIndex(removePrefix);
            int parseColor2 = parseColor(property);
            if (mapColorIndex >= 0 && mapColorIndex < ints.length && parseColor2 >= 0) {
               ints[mapColorIndex] = parseColor2;
               n++;
            } else {
               warn("Invalid color: " + str4 + " = " + property);
            }
         }
      }

      if (n <= 0) {
         return null;
      }

      dbg(str3 + " colors: " + n);
      return ints;
   }

   public static int[] readPotionColors(Properties properties, String str, String str2, String str3) {
      int[] ints = new int[Potion.potionTypes.length];
      Arrays.fill(ints, -1);
      int n = 0;

      for (Object obj : properties.keySet()) {
         String str4 = (String)obj;
         String property = properties.getProperty(str4);
         if (str4.startsWith(str2)) {
            int potionId = getPotionId(str4);
            int parseColor2 = parseColor(property);
            if (potionId >= 0 && potionId < ints.length && parseColor2 >= 0) {
               ints[potionId] = parseColor2;
               n++;
            } else {
               warn("Invalid color: " + str4 + " = " + property);
            }
         }
      }

      if (n <= 0) {
         return null;
      }

      dbg(str3 + " colors: " + n);
      return ints;
   }

   public static int getPotionId(String str) {
      if (str.equals("potion.water")) {
         return 0;
      }

      Potion[] potionTypes = Potion.potionTypes;

      for (int i = 0; i < potionTypes.length; i++) {
         Potion potion = potionTypes[i];
         if (potion != null && potion.getName().equals(str)) {
            return potion.getId();
         }
      }

      return -1;
   }

   public static int getPotionColor(int n, int n2) {
      if (potionColors == null) {
         return n2;
      } else if (n >= 0 && n < potionColors.length) {
         int n3 = potionColors[n];
         return n3 < 0 ? n2 : n3;
      } else {
         return n2;
      }
   }

   public static int getMapColorIndex(String str) {
      return str == null
         ? -1
         : (
            str.equals("air")
               ? MapColor.airColor.colorIndex
               : (
                  str.equals("grass")
                     ? MapColor.grassColor.colorIndex
                     : (
                        str.equals("sand")
                           ? MapColor.sandColor.colorIndex
                           : (
                              str.equals("cloth")
                                 ? MapColor.clothColor.colorIndex
                                 : (
                                    str.equals("tnt")
                                       ? MapColor.tntColor.colorIndex
                                       : (
                                          str.equals("ice")
                                             ? MapColor.iceColor.colorIndex
                                             : (
                                                str.equals("iron")
                                                   ? MapColor.ironColor.colorIndex
                                                   : (
                                                      str.equals("foliage")
                                                         ? MapColor.foliageColor.colorIndex
                                                         : (
                                                            str.equals("clay")
                                                               ? MapColor.clayColor.colorIndex
                                                               : (
                                                                  str.equals("dirt")
                                                                     ? MapColor.dirtColor.colorIndex
                                                                     : (
                                                                        str.equals("stone")
                                                                           ? MapColor.stoneColor.colorIndex
                                                                           : (
                                                                              str.equals("water")
                                                                                 ? MapColor.waterColor.colorIndex
                                                                                 : (
                                                                                    str.equals("wood")
                                                                                       ? MapColor.woodColor.colorIndex
                                                                                       : (
                                                                                          str.equals("quartz")
                                                                                             ? MapColor.quartzColor.colorIndex
                                                                                             : (
                                                                                                str.equals("gold")
                                                                                                   ? MapColor.goldColor.colorIndex
                                                                                                   : (
                                                                                                      str.equals("diamond")
                                                                                                         ? MapColor.diamondColor.colorIndex
                                                                                                         : (
                                                                                                            str.equals("lapis")
                                                                                                               ? MapColor.lapisColor.colorIndex
                                                                                                               : (
                                                                                                                  str.equals("emerald")
                                                                                                                     ? MapColor.emeraldColor.colorIndex
                                                                                                                     : (
                                                                                                                        str.equals("podzol")
                                                                                                                           ? MapColor.obsidianColor.colorIndex
                                                                                                                           : (
                                                                                                                              str.equals("netherrack")
                                                                                                                                 ? MapColor.netherrackColor.colorIndex
                                                                                                                                 : (
                                                                                                                                    str.equals("snow")
                                                                                                                                          || str.equals(
                                                                                                                                             "white"
                                                                                                                                          )
                                                                                                                                       ? MapColor.snowColor.colorIndex
                                                                                                                                       : (
                                                                                                                                          str.equals("adobe")
                                                                                                                                                || str.equals(
                                                                                                                                                   "orange"
                                                                                                                                                )
                                                                                                                                             ? MapColor.adobeColor
                                                                                                                                                .colorIndex
                                                                                                                                             : (
                                                                                                                                                str.equals(
                                                                                                                                                      "magenta"
                                                                                                                                                   )
                                                                                                                                                   ? MapColor.magentaColor
                                                                                                                                                      .colorIndex
                                                                                                                                                   : (
                                                                                                                                                      !str.equals(
                                                                                                                                                               "light_blue"
                                                                                                                                                            )
                                                                                                                                                            && !str.equals(
                                                                                                                                                               "lightBlue"
                                                                                                                                                            )
                                                                                                                                                         ? (
                                                                                                                                                            str.equals(
                                                                                                                                                                  "yellow"
                                                                                                                                                               )
                                                                                                                                                               ? MapColor.yellowColor
                                                                                                                                                                  .colorIndex
                                                                                                                                                               : (
                                                                                                                                                                  str.equals(
                                                                                                                                                                        "lime"
                                                                                                                                                                     )
                                                                                                                                                                     ? MapColor.limeColor
                                                                                                                                                                        .colorIndex
                                                                                                                                                                     : (
                                                                                                                                                                        str.equals(
                                                                                                                                                                              "pink"
                                                                                                                                                                           )
                                                                                                                                                                           ? MapColor.pinkColor
                                                                                                                                                                              .colorIndex
                                                                                                                                                                           : (
                                                                                                                                                                              str.equals(
                                                                                                                                                                                    "gray"
                                                                                                                                                                                 )
                                                                                                                                                                                 ? MapColor.grayColor
                                                                                                                                                                                    .colorIndex
                                                                                                                                                                                 : (
                                                                                                                                                                                    str.equals(
                                                                                                                                                                                          "silver"
                                                                                                                                                                                       )
                                                                                                                                                                                       ? MapColor.silverColor
                                                                                                                                                                                          .colorIndex
                                                                                                                                                                                       : (
                                                                                                                                                                                          str.equals(
                                                                                                                                                                                                "cyan"
                                                                                                                                                                                             )
                                                                                                                                                                                             ? MapColor.cyanColor
                                                                                                                                                                                                .colorIndex
                                                                                                                                                                                             : (
                                                                                                                                                                                                str.equals(
                                                                                                                                                                                                      "purple"
                                                                                                                                                                                                   )
                                                                                                                                                                                                   ? MapColor.purpleColor
                                                                                                                                                                                                      .colorIndex
                                                                                                                                                                                                   : (
                                                                                                                                                                                                      str.equals(
                                                                                                                                                                                                            "blue"
                                                                                                                                                                                                         )
                                                                                                                                                                                                         ? MapColor.blueColor
                                                                                                                                                                                                            .colorIndex
                                                                                                                                                                                                         : (
                                                                                                                                                                                                            str.equals(
                                                                                                                                                                                                                  "brown"
                                                                                                                                                                                                               )
                                                                                                                                                                                                               ? MapColor.brownColor
                                                                                                                                                                                                                  .colorIndex
                                                                                                                                                                                                               : (
                                                                                                                                                                                                                  str.equals(
                                                                                                                                                                                                                        "green"
                                                                                                                                                                                                                     )
                                                                                                                                                                                                                     ? MapColor.greenColor
                                                                                                                                                                                                                        .colorIndex
                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                        str.equals(
                                                                                                                                                                                                                              "red"
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                           ? MapColor.redColor
                                                                                                                                                                                                                              .colorIndex
                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                              str.equals(
                                                                                                                                                                                                                                    "black"
                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                 ? MapColor.blackColor
                                                                                                                                                                                                                                    .colorIndex
                                                                                                                                                                                                                                 : -1
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                     )
                                                                                                                                                                                                               )
                                                                                                                                                                                                         )
                                                                                                                                                                                                   )
                                                                                                                                                                                             )
                                                                                                                                                                                       )
                                                                                                                                                                                 )
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                               )
                                                                                                                                                         )
                                                                                                                                                         : MapColor.lightBlueColor
                                                                                                                                                            .colorIndex
                                                                                                                                                   )
                                                                                                                                             )
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                           )
                                                                                                                     )
                                                                                                               )
                                                                                                         )
                                                                                                   )
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   public static int[] getMapColors() {
      MapColor[] mapColorArray = MapColor.mapColorArray;
      int[] ints = new int[mapColorArray.length];
      Arrays.fill(ints, -1);

      for (int i = 0; i < mapColorArray.length && i < ints.length; i++) {
         MapColor mapColor = mapColorArray[i];
         if (mapColor != null) {
            ints[i] = mapColor.colorValue;
         }
      }

      return ints;
   }

   public static void setMapColors(int[] ints) {
      if (ints != null) {
         MapColor[] mapColorArray = MapColor.mapColorArray;
         boolean flag = false;

         for (int i = 0; i < mapColorArray.length && i < ints.length; i++) {
            MapColor mapColor = mapColorArray[i];
            if (mapColor != null) {
               int n = ints[i];
               if (n >= 0 && mapColor.colorValue != n) {
                  mapColor.colorValue = n;
                  flag = true;
               }
            }
         }

         if (flag) {
            Minecraft.getMinecraft().getTextureManager().reloadBannerTextures();
         }
      }
   }

   public static void dbg(String str) {
      Config.dbg("CustomColors: " + str);
   }

   public static void warn(String str) {
      Config.warn("CustomColors: " + str);
   }

   public static int getExpBarTextColor(int n) {
      return expBarTextColor < 0 ? n : expBarTextColor;
   }

   public static int getBossTextColor(int n) {
      return bossTextColor < 0 ? n : bossTextColor;
   }

   public static int getSignTextColor(int n) {
      return signTextColor < 0 ? n : signTextColor;
   }

   public interface IColorizer {
      int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos);

      boolean isColorConstant();
   }
}
