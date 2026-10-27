package net.optifine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.util.MathUtils;
import net.optifine.util.TextureUtils;

public class ConnectedProperties {
   public String name = null;
   public String basePath = null;
   public MatchBlock[] matchBlocks = null;
   public int[] metadatas = null;
   public String[] matchTiles = null;
   public int method = 0;
   public String[] tiles = null;
   public int connect = 0;
   public int faces = 63;
   public BiomeGenBase[] biomes = null;
   public RangeListInt heights = null;
   public int renderPass = 0;
   public boolean innerSeams = false;
   public int[] ctmTileIndexes = null;
   public int width = 0;
   public int height = 0;
   public int[] weights = null;
   public int randomLoops = 0;
   public int symmetry = 1;
   public boolean linked = false;
   public NbtTagValue nbtName = null;
   public int[] sumWeights = null;
   public int sumAllWeights = 1;
   public TextureAtlasSprite[] matchTileIcons = null;
   public TextureAtlasSprite[] tileIcons = null;
   public MatchBlock[] connectBlocks = null;
   public String[] connectTiles = null;
   public TextureAtlasSprite[] connectTileIcons = null;
   public int tintIndex = -1;
   public IBlockState tintBlockState = Blocks.air.getDefaultState();
   public EnumWorldBlockLayer layer = null;
   public static final int METHOD_REPEAT = 5;
   public static final int METHOD_FIXED = 7;
   public static final int METHOD_VERTICAL_HORIZONTAL = 9;
   public static final int METHOD_CTM_COMPACT = 10;
   public static final int METHOD_OVERLAY = 11;
   public static final int METHOD_OVERLAY_FIXED = 12;
   public static final int METHOD_OVERLAY_RANDOM = 13;
   public static final int METHOD_OVERLAY_REPEAT = 14;
   public static final int METHOD_OVERLAY_CTM = 15;
   public static final int FACE_WEST = 16;
   public static final int FACE_EAST = 32;
   public static final int FACE_SIDES = 60;
   public static final int FACE_ALL = 63;
   public static final String TILE_SKIP_PNG = "<skip>.png";
   public static final String TILE_DEFAULT_PNG = "<default>.png";
   public static final int CONSTANT_2 = 2;
   public static final int CONSTANT_4 = 4;
   public static final int CONSTANT_0 = 0;
   public static final int CONSTANT_3 = 3;
   public static final int CONSTANT_8 = 8;
   public static final int CONSTANT_128 = 128;
   public static final int CONSTANT_1 = 1;
   public static final int CONSTANT_2_2 = 2;
   public static final int CONSTANT_6 = 6;
   public static final int CONSTANT_128_2 = 128;
   public static final int CONSTANT_1_2 = 1;
   public static final int CONSTANT_2_3 = 2;
   public static final int CONSTANT_1_3 = 1;
   public static final int CONSTANT_8_2 = 8;
   public static final int CONSTANT_3_2 = 3;
   public static final int CONSTANT_128_3 = 128;
   public static final int CONSTANT_0_2 = 0;
   public static final int CONSTANT_4_2 = 4;
   public static final int CONSTANT_6_2 = 6;
   public static final int CONSTANT_2_4 = 2;
   public static final int CONSTANT_1_4 = 1;

   public ConnectedProperties(Properties properties, String str) {
      ConnectedParser connectedParser = new ConnectedParser("ConnectedTextures");
      this.name = connectedParser.parseName(str);
      this.basePath = connectedParser.parseBasePath(str);
      this.matchBlocks = connectedParser.parseMatchBlocks(properties.getProperty("matchBlocks"));
      this.metadatas = connectedParser.parseIntList(properties.getProperty("metadata"));
      this.matchTiles = this.parseMatchTiles(properties.getProperty("matchTiles"));
      this.method = parseMethod(properties.getProperty("method"));
      this.tiles = this.parseTileNames(properties.getProperty("tiles"));
      this.connect = parseConnect(properties.getProperty("connect"));
      this.faces = parseFaces(properties.getProperty("faces"));
      this.biomes = connectedParser.parseBiomes(properties.getProperty("biomes"));
      this.heights = connectedParser.parseRangeListInt(properties.getProperty("heights"));
      if (this.heights == null) {
         int parseInt = connectedParser.parseInt(properties.getProperty("minHeight"), -1);
         int parseInt2 = connectedParser.parseInt(properties.getProperty("maxHeight"), 1024);
         if (parseInt != -1 || parseInt2 != 1024) {
            this.heights = new RangeListInt(new RangeInt(parseInt, parseInt2));
         }
      }

      this.renderPass = connectedParser.parseInt(properties.getProperty("renderPass"), -1);
      this.innerSeams = connectedParser.parseBoolean(properties.getProperty("innerSeams"), false);
      this.ctmTileIndexes = this.parseCtmTileIndexes(properties);
      this.width = connectedParser.parseInt(properties.getProperty("width"), -1);
      this.height = connectedParser.parseInt(properties.getProperty("height"), -1);
      this.weights = connectedParser.parseIntList(properties.getProperty("weights"));
      this.randomLoops = connectedParser.parseInt(properties.getProperty("randomLoops"), 0);
      this.symmetry = parseSymmetry(properties.getProperty("symmetry"));
      this.linked = connectedParser.parseBoolean(properties.getProperty("linked"), false);
      this.nbtName = connectedParser.parseNbtTagValue("name", properties.getProperty("name"));
      this.connectBlocks = connectedParser.parseMatchBlocks(properties.getProperty("connectBlocks"));
      this.connectTiles = this.parseMatchTiles(properties.getProperty("connectTiles"));
      this.tintIndex = connectedParser.parseInt(properties.getProperty("tintIndex"), -1);
      this.tintBlockState = connectedParser.parseBlockState(properties.getProperty("tintBlock"), Blocks.air.getDefaultState());
      this.layer = connectedParser.parseBlockRenderLayer(properties.getProperty("layer"), EnumWorldBlockLayer.CUTOUT_MIPPED);
   }

   public int[] parseCtmTileIndexes(Properties properties) {
      if (this.tiles == null) {
         return null;
      }

      HashMap map = new HashMap();

      for (Object obj : properties.keySet()) {
         if (obj instanceof String) {
            String str = (String)obj;
            String str2 = "ctm.";
            if (str.startsWith(str2)) {
               String substring = str.substring(str2.length());
               String property = properties.getProperty(str);
               if (property != null) {
                  property = property.trim();
                  int parseInt = Config.parseInt(substring, -1);
                  if (parseInt >= 0 && parseInt <= 46) {
                     int parseInt2 = Config.parseInt(property, -1);
                     if (parseInt2 >= 0 && parseInt2 < this.tiles.length) {
                        map.put(parseInt, parseInt2);
                     } else {
                        Config.warn("Invalid CTM tile index: " + property);
                     }
                  } else {
                     Config.warn("Invalid CTM index: " + substring);
                  }
               }
            }
         }
      }

      if (map.isEmpty()) {
         return null;
      }

      int[] ints = new int[47];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = -1;
         if (map.containsKey(i)) {
            ints[i] = (Integer)map.get(i);
         }
      }

      return ints;
   }

   public String[] parseMatchTiles(String str) {
      if (str == null) {
         return null;
      }

      String[] tokenize = Config.tokenize(str, " ");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         if (str2.endsWith(".png")) {
            str2 = str2.substring(0, str2.length() - 4);
         }

         str2 = TextureUtils.fixResourcePath(str2, this.basePath);
         tokenize[i] = str2;
      }

      return tokenize;
   }

   public static String parseName(String str) {
      String str2 = str;
      int lastIndexOf = str.lastIndexOf(47);
      if (lastIndexOf >= 0) {
         str2 = str.substring(lastIndexOf + 1);
      }

      int lastIndexOf2 = str2.lastIndexOf(46);
      if (lastIndexOf2 >= 0) {
         str2 = str2.substring(0, lastIndexOf2);
      }

      return str2;
   }

   public static String parseBasePath(String str) {
      int lastIndexOf = str.lastIndexOf(47);
      return lastIndexOf < 0 ? "" : str.substring(0, lastIndexOf);
   }

   public String[] parseTileNames(String str) {
      if (str == null) {
         return null;
      }

      ArrayList list = new ArrayList();
      String[] tokenize = Config.tokenize(str, " ,");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         if (str2.contains("-")) {
            String[] tokenize2 = Config.tokenize(str2, "-");
            if (tokenize2.length == 2) {
               int parseInt = Config.parseInt(tokenize2[0], -1);
               int parseInt2 = Config.parseInt(tokenize2[1], -1);
               if (parseInt >= 0 && parseInt2 >= 0) {
                  if (parseInt > parseInt2) {
                     Config.warn("Invalid interval: " + str2 + ", when parsing: " + str);
                     continue;
                  }

                  for (int j = parseInt; j <= parseInt2; j++) {
                     list.add(String.valueOf(j));
                  }
                  continue;
               }
            }
         }

         list.add(str2);
      }

      String[] toArray = (String[])list.toArray(new String[list.size()]);

      for (int i = 0; i < toArray.length; i++) {
         String str2 = toArray[i];
         str2 = TextureUtils.fixResourcePath(str2, this.basePath);
         if (!str2.startsWith(this.basePath) && !str2.startsWith("textures/") && !str2.startsWith("mcpatcher/")) {
            str2 = this.basePath + "/" + str2;
         }

         if (str2.endsWith(".png")) {
            str2 = str2.substring(0, str2.length() - 4);
         }

         if (str2.startsWith("/")) {
            str2 = str2.substring(1);
         }

         toArray[i] = str2;
      }

      return toArray;
   }

   public static int parseSymmetry(String str) {
      if (str == null) {
         return 1;
      }

      str = str.trim();
      if (str.equals("opposite")) {
         return 2;
      }

      if (str.equals("all")) {
         return 6;
      }

      Config.warn("Unknown symmetry: " + str);
      return 1;
   }

   public static int parseFaces(String str) {
      if (str == null) {
         return 63;
      }

      String[] tokenize = Config.tokenize(str, " ,");
      int n = 0;

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         int parseFace2 = parseFace(str2);
         n |= parseFace2;
      }

      return n;
   }

   public static int parseFace(String str) {
      str = str.toLowerCase();
      if (str.equals("bottom") || str.equals("down")) {
         return 1;
      }

      if (str.equals("top") || str.equals("up")) {
         return 2;
      }

      if (str.equals("north")) {
         return 4;
      }

      if (str.equals("south")) {
         return 8;
      }

      if (str.equals("east")) {
         return 32;
      }

      if (str.equals("west")) {
         return 16;
      }

      if (str.equals("sides")) {
         return 60;
      }

      if (str.equals("all")) {
         return 63;
      }

      Config.warn("Unknown face: " + str);
      return 128;
   }

   public static int parseConnect(String str) {
      if (str == null) {
         return 0;
      }

      str = str.trim();
      if (str.equals("block")) {
         return 1;
      }

      if (str.equals("tile")) {
         return 2;
      }

      if (str.equals("material")) {
         return 3;
      }

      Config.warn("Unknown connect: " + str);
      return 128;
   }

   public static IProperty getProperty(String str, Collection collection) {
      for (Object obj : collection) {
         IProperty property = (IProperty)obj;
         if (str.equals(property.getName())) {
            return property;
         }
      }

      return null;
   }

   public static int parseMethod(String str) {
      if (str == null) {
         return 1;
      }

      str = str.trim();
      if (str.equals("ctm") || str.equals("glass")) {
         return 1;
      }

      if (str.equals("ctm_compact")) {
         return 10;
      }

      if (str.equals("horizontal") || str.equals("bookshelf")) {
         return 2;
      }

      if (str.equals("vertical")) {
         return 6;
      }

      if (str.equals("top")) {
         return 3;
      }

      if (str.equals("random")) {
         return 4;
      }

      if (str.equals("repeat")) {
         return 5;
      }

      if (str.equals("fixed")) {
         return 7;
      }

      if (str.equals("horizontal+vertical") || str.equals("h+v")) {
         return 8;
      }

      if (str.equals("vertical+horizontal") || str.equals("v+h")) {
         return 9;
      }

      if (str.equals("overlay")) {
         return 11;
      }

      if (str.equals("overlay_fixed")) {
         return 12;
      }

      if (str.equals("overlay_random")) {
         return 13;
      }

      if (str.equals("overlay_repeat")) {
         return 14;
      }

      if (str.equals("overlay_ctm")) {
         return 15;
      }

      Config.warn("Unknown method: " + str);
      return 0;
   }

   public boolean isValid(String str) {
      if (this.name == null || this.name.length() <= 0) {
         Config.warn("No name found: " + str);
         return false;
      }

      if (this.basePath == null) {
         Config.warn("No base path found: " + str);
         return false;
      }

      if (this.matchBlocks == null) {
         this.matchBlocks = this.detectMatchBlocks();
      }

      if (this.matchTiles == null && this.matchBlocks == null) {
         this.matchTiles = this.detectMatchTiles();
      }

      if (this.matchBlocks == null && this.matchTiles == null) {
         Config.warn("No matchBlocks or matchTiles specified: " + str);
         return false;
      }

      if (this.method == 0) {
         Config.warn("No method: " + str);
         return false;
      }

      if (this.tiles != null && this.tiles.length > 0) {
         if (this.connect == 0) {
            this.connect = this.detectConnect();
         }

         if (this.connect == 128) {
            Config.warn("Invalid connect in: " + str);
            return false;
         }

         if (this.renderPass > 0) {
            Config.warn("Render pass not supported: " + this.renderPass);
            return false;
         }

         if ((this.faces & 128) != 0) {
            Config.warn("Invalid faces in: " + str);
            return false;
         }

         if ((this.symmetry & 128) != 0) {
            Config.warn("Invalid symmetry in: " + str);
            return false;
         }

         switch (this.method) {
            case 1:
               return this.isValidCtm(str);
            case 2:
               return this.isValidCtmCompact(str);
            case 3:
               return this.isValidHorizontal(str);
            case 4:
               return this.isValidRandom(str);
            case 5:
               return this.isValidTop(str);
            case 6:
               return this.isValidVertical(str);
            case 7:
               return this.isValidRepeat(str);
            case 8:
               return this.isValidFixed(str);
            case 9:
               return this.isValidHorizontalVertical(str);
            case 10:
               return this.isValidVerticalHorizontal(str);
            case 11:
               return this.isValidOverlay(str);
            case 12:
               return this.isValidOverlayFixed(str);
            case 13:
               return this.isValidOverlayRandom(str);
            case 14:
               return this.isValidOverlayRepeat(str);
            case 15:
               return this.isValidOverlayCtm(str);
            default:
               Config.warn("Unknown method: " + str);
               return false;
         }
      } else {
         Config.warn("No tiles specified: " + str);
         return false;
      }
   }

   public int detectConnect() {
      return this.matchBlocks != null ? 1 : (this.matchTiles != null ? 2 : 128);
   }

   public MatchBlock[] detectMatchBlocks() {
      int[] detectMatchBlockIds = this.detectMatchBlockIds();
      if (detectMatchBlockIds == null) {
         return null;
      }

      MatchBlock[] matchBlocks = new MatchBlock[detectMatchBlockIds.length];

      for (int i = 0; i < matchBlocks.length; i++) {
         matchBlocks[i] = new MatchBlock(detectMatchBlockIds[i]);
      }

      return matchBlocks;
   }

   public int[] detectMatchBlockIds() {
      if (!this.name.startsWith("block")) {
         return null;
      }

      int n = "block".length();

      int n2;
      for (n2 = n; n2 < this.name.length(); n2++) {
         char charAt = this.name.charAt(n2);
         if (charAt < '0' || charAt > '9') {
            break;
         }
      }

      if (n2 == n) {
         return null;
      }

      String substring = this.name.substring(n, n2);
      int parseInt = Config.parseInt(substring, -1);
      return parseInt < 0 ? null : new int[]{parseInt};
   }

   public String[] detectMatchTiles() {
      TextureAtlasSprite icon = getIcon(this.name);
      return icon == null ? null : new String[]{this.name};
   }

   public static TextureAtlasSprite getIcon(String str) {
      TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
      TextureAtlasSprite spriteSafe = textureMapBlocks.getSpriteSafe(str);
      return spriteSafe != null ? spriteSafe : textureMapBlocks.getSpriteSafe("blocks/" + str);
   }

   public boolean isValidCtm(String str) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("0-11 16-27 32-43 48-58");
      }

      if (this.tiles.length < 47) {
         Config.warn("Invalid tiles, must be at least 47: " + str);
         return false;
      } else {
         return true;
      }
   }

   public boolean isValidCtmCompact(String str) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("12-15");
      }

      if (this.tiles.length != 4) {
         Config.warn("Invalid tiles, must be exactly 4: " + str);
         return false;
      } else {
         return true;
      }
   }

   public boolean isValidOverlay(String str) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("0-16");
      }

      if (this.tiles.length < 17) {
         Config.warn("Invalid tiles, must be at least 17: " + str);
         return false;
      }

      if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
         return true;
      }

      Config.warn("Invalid overlay layer: " + this.layer);
      return false;
   }

   public boolean isValidOverlayFixed(String str) {
      if (!this.isValidRepeat(str)) {
         return false;
      }

      if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
         return true;
      }

      Config.warn("Invalid overlay layer: " + this.layer);
      return false;
   }

   public boolean isValidOverlayRandom(String str) {
      if (!this.isValidRandom(str)) {
         return false;
      }

      if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
         return true;
      }

      Config.warn("Invalid overlay layer: " + this.layer);
      return false;
   }

   public boolean isValidOverlayRepeat(String str) {
      if (!this.isValidTop(str)) {
         return false;
      }

      if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
         return true;
      }

      Config.warn("Invalid overlay layer: " + this.layer);
      return false;
   }

   public boolean isValidOverlayCtm(String str) {
      if (!this.isValidCtm(str)) {
         return false;
      }

      if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
         return true;
      }

      Config.warn("Invalid overlay layer: " + this.layer);
      return false;
   }

   public boolean isValidHorizontal(String str) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("66");
      }

      if (this.tiles.length != 1) {
         Config.warn("Invalid tiles, must be exactly 1: " + str);
         return false;
      } else {
         return true;
      }
   }

   public boolean isValidVertical(String str) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for vertical: " + str);
         return false;
      } else if (this.tiles.length != 4) {
         Config.warn("Invalid tiles, must be exactly 4: " + str);
         return false;
      } else {
         return true;
      }
   }

   public boolean isValidHorizontalVertical(String str) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for vertical+horizontal: " + str);
         return false;
      } else if (this.tiles.length != 7) {
         Config.warn("Invalid tiles, must be exactly 7: " + str);
         return false;
      } else {
         return true;
      }
   }

   public boolean isValidVerticalHorizontal(String str) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("0-4");
      }

      if (this.tiles.length < 5) {
         Config.warn("Invalid tiles, must be at least 5: " + str);
         return false;
      } else {
         return true;
      }
   }

   public boolean isValidRandom(String str) {
      if (this.tiles != null && this.tiles.length > 0) {
         if (this.weights != null) {
            if (this.weights.length > this.tiles.length) {
               Config.warn("More weights defined than tiles, trimming weights: " + str);
               int[] ints = new int[this.tiles.length];
               System.arraycopy(this.weights, 0, ints, 0, ints.length);
               this.weights = ints;
            }

            if (this.weights.length < this.tiles.length) {
               Config.warn("Less weights defined than tiles, expanding weights: " + str);
               int[] ints = new int[this.tiles.length];
               System.arraycopy(this.weights, 0, ints, 0, this.weights.length);
               int average = MathUtils.getAverage(this.weights);

               for (int length = this.weights.length; length < ints.length; length++) {
                  ints[length] = average;
               }

               this.weights = ints;
            }

            this.sumWeights = new int[this.weights.length];
            int n = 0;

            for (int i = 0; i < this.weights.length; i++) {
               n += this.weights[i];
               this.sumWeights[i] = n;
            }

            this.sumAllWeights = n;
            if (this.sumAllWeights <= 0) {
               Config.warn("Invalid sum of all weights: " + n);
               this.sumAllWeights = 1;
            }
         }

         if (this.randomLoops >= 0 && this.randomLoops <= 9) {
            return true;
         }

         Config.warn("Invalid randomLoops: " + this.randomLoops);
         return false;
      } else {
         Config.warn("Tiles not defined: " + str);
         return false;
      }
   }

   public boolean isValidRepeat(String str) {
      if (this.tiles == null) {
         Config.warn("Tiles not defined: " + str);
         return false;
      } else if (this.tiles.length != 1) {
         Config.warn("Number of tiles should be 1 for method: fixed.");
         return false;
      } else {
         return true;
      }
   }

   public boolean isValidFixed(String str) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for horizontal+vertical: " + str);
         return false;
      } else if (this.tiles.length != 7) {
         Config.warn("Invalid tiles, must be exactly 7: " + str);
         return false;
      } else {
         return true;
      }
   }

   public boolean isValidTop(String str) {
      if (this.tiles == null) {
         Config.warn("Tiles not defined: " + str);
         return false;
      } else if (this.width <= 0) {
         Config.warn("Invalid width: " + str);
         return false;
      } else if (this.height <= 0) {
         Config.warn("Invalid height: " + str);
         return false;
      } else if (this.tiles.length != this.width * this.height) {
         Config.warn("Number of tiles does not equal width x height: " + str);
         return false;
      } else {
         return true;
      }
   }

   public void updateIcons(TextureMap textureMap) {
      if (this.matchTiles != null) {
         this.matchTileIcons = registerIcons(this.matchTiles, textureMap, false, false);
      }

      if (this.connectTiles != null) {
         this.connectTileIcons = registerIcons(this.connectTiles, textureMap, false, false);
      }

      if (this.tiles != null) {
         this.tileIcons = registerIcons(this.tiles, textureMap, true, !isMethodOverlay(this.method));
      }
   }

   public static boolean isMethodOverlay(int n) {
      switch (n) {
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
            return true;
         default:
            return false;
      }
   }

   public static TextureAtlasSprite[] registerIcons(String[] strs, TextureMap textureMap, boolean flag, boolean flag2) {
      if (strs == null) {
         return null;
      }

      ArrayList list = new ArrayList();

      for (int i = 0; i < strs.length; i++) {
         String str = strs[i];
         ResourceLocation resourceLocation = new ResourceLocation(str);
         String resourceDomain = resourceLocation.getResourceDomain();
         String resourcePath = resourceLocation.getResourcePath();
         if (!resourcePath.contains("/")) {
            resourcePath = "textures/blocks/" + resourcePath;
         }

         String str2 = resourcePath + ".png";
         if (flag && str2.endsWith("<skip>.png")) {
            list.add(null);
         } else if (flag2 && str2.endsWith("<default>.png")) {
            list.add(ConnectedTextures.SPRITE_DEFAULT);
         } else {
            ResourceLocation resourceLocation2 = new ResourceLocation(resourceDomain, str2);
            boolean hasResource = Config.hasResource(resourceLocation2);
            if (!hasResource) {
               Config.warn("File not found: " + str2);
            }

            String str3 = "textures/";
            String str4 = resourcePath;
            if (resourcePath.startsWith(str3)) {
               str4 = resourcePath.substring(str3.length());
            }

            ResourceLocation resourceLocation3 = new ResourceLocation(resourceDomain, str4);
            TextureAtlasSprite registerSprite = textureMap.registerSprite(resourceLocation3);
            list.add(registerSprite);
         }
      }

      return (TextureAtlasSprite[])list.toArray(new TextureAtlasSprite[list.size()]);
   }

   public boolean matchesBlockId(int n) {
      return Matches.blockId(n, this.matchBlocks);
   }

   public boolean matchesBlock(int n, int n2) {
      return !Matches.block(n, n2, this.matchBlocks) ? false : Matches.metadata(n2, this.metadatas);
   }

   public boolean matchesIcon(TextureAtlasSprite textureAtlasSprite) {
      return Matches.sprite(textureAtlasSprite, this.matchTileIcons);
   }

   @Override
   public String toString() {
      return "CTM name: "
         + this.name
         + ", basePath: "
         + this.basePath
         + ", matchBlocks: "
         + Config.arrayToString(this.matchBlocks)
         + ", matchTiles: "
         + Config.arrayToString(this.matchTiles);
   }

   public boolean matchesBiome(BiomeGenBase biomeGenBase) {
      return Matches.biome(biomeGenBase, this.biomes);
   }

   public int getMetadataMax() {
      int n = -1;
      n = this.getMax(this.metadatas, n);
      if (this.matchBlocks != null) {
         for (int i = 0; i < this.matchBlocks.length; i++) {
            MatchBlock matchBlock = this.matchBlocks[i];
            n = this.getMax(matchBlock.getMetadatas(), n);
         }
      }

      return n;
   }

   public int getMax(int[] ints, int n) {
      if (ints == null) {
         return n;
      }

      for (int i = 0; i < ints.length; i++) {
         int n2 = ints[i];
         if (n2 > n) {
            n = n2;
         }
      }

      return n;
   }
}
