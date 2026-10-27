package net.optifine;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.config.Matches;
import net.optifine.util.TextureUtils;

public class CustomColormap implements CustomColors.IColorizer {
   public String name = null;
   public String basePath = null;
   public int format = -1;
   public MatchBlock[] matchBlocks = null;
   public String source = null;
   public int color = -1;
   public int yVariance = 0;
   public int yOffset = 0;
   public int width = 0;
   public int height = 0;
   public int[] colors = null;
   public float[][] colorsRgb = (float[][])null;
   public static final int FORMAT_UNKNOWN = -1;
   public static final int FORMAT_VANILLA = 0;
   public static final int FORMAT_GRID = 1;
   public static final int FORMAT_FIXED = 2;
   public static final String FORMAT_VANILLA_STRING = "vanilla";
   public static final String FORMAT_GRID_STRING = "grid";
   public static final String FORMAT_FIXED_STRING = "fixed";
   public static String[] FORMAT_STRINGS = new String[]{"vanilla", "grid", "fixed"};
   public static final String KEY_FORMAT = "format";
   public static final String KEY_BLOCKS = "blocks";
   public static final String KEY_SOURCE = "source";
   public static final String KEY_COLOR = "color";
   public static final String KEY_Y_VARIANCE = "yVariance";
   public static final String KEY_Y_OFFSET = "yOffset";

   public CustomColormap(Properties properties, String str, int n, int n2, String str2) {
      ConnectedParser connectedParser = new ConnectedParser("Colormap");
      this.name = connectedParser.parseName(str);
      this.basePath = connectedParser.parseBasePath(str);
      this.format = this.parseFormat(properties.getProperty("format", str2));
      this.matchBlocks = connectedParser.parseMatchBlocks(properties.getProperty("blocks"));
      this.source = parseTexture(properties.getProperty("source"), str, this.basePath);
      this.color = ConnectedParser.parseColor(properties.getProperty("color"), -1);
      this.yVariance = connectedParser.parseInt(properties.getProperty("yVariance"), 0);
      this.yOffset = connectedParser.parseInt(properties.getProperty("yOffset"), 0);
      this.width = n;
      this.height = n2;
   }

   public int parseFormat(String str) {
      if (str == null) {
         return 0;
      }

      str = str.trim();
      if (str.equals("vanilla")) {
         return 0;
      }

      if (str.equals("grid")) {
         return 1;
      }

      if (str.equals("fixed")) {
         return 2;
      }

      warn("Unknown format: " + str);
      return -1;
   }

   public boolean isValid(String str) {
      if (this.format != 0 && this.format != 1) {
         if (this.format != 2) {
            return false;
         }

         if (this.color < 0) {
            this.color = 16777215;
         }
      } else {
         if (this.source == null) {
            warn("Source not defined: " + str);
            return false;
         }

         this.readColors();
         if (this.colors == null) {
            return false;
         }

         if (this.color < 0) {
            if (this.format == 0) {
               this.color = this.getColor(127, 127);
            }

            if (this.format == 1) {
               this.color = this.getColorGrid(BiomeGenBase.plains, new BlockPos(0, 64, 0));
            }
         }
      }

      return true;
   }

   public boolean isValidMatchBlocks(String str) {
      if (this.matchBlocks == null) {
         this.matchBlocks = this.detectMatchBlocks();
         if (this.matchBlocks == null) {
            warn("Match blocks not defined: " + str);
            return false;
         }
      }

      return true;
   }

   public MatchBlock[] detectMatchBlocks() {
      Block blockFromName = Block.getBlockFromName(this.name);
      if (blockFromName != null) {
         return new MatchBlock[]{new MatchBlock(Block.getIdFromBlock(blockFromName))};
      }

      Pattern compile = Pattern.compile("^block([0-9]+).*$");
      Matcher matcher = compile.matcher(this.name);
      if (matcher.matches()) {
         String group = matcher.group(1);
         int parseInt = Config.parseInt(group, -1);
         if (parseInt >= 0) {
            return new MatchBlock[]{new MatchBlock(parseInt)};
         }
      }

      ConnectedParser connectedParser = new ConnectedParser("Colormap");
      MatchBlock[] parseMatchBlock = connectedParser.parseMatchBlock(this.name);
      return parseMatchBlock != null ? parseMatchBlock : null;
   }

   public void readColors() {
      try {
         this.colors = null;
         if (this.source == null) {
            return;
         }

         String str = this.source + ".png";
         ResourceLocation resourceLocation = new ResourceLocation(str);
         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return;
         }

         BufferedImage readBufferedImage = TextureUtil.readBufferedImage(resourceStream);
         if (readBufferedImage == null) {
            return;
         }

         int width = readBufferedImage.getWidth();
         int height = readBufferedImage.getHeight();
         boolean flag = this.width < 0 || this.width == width;
         boolean flag2 = this.height < 0 || this.height == height;
         if (!flag || !flag2) {
            dbg("Non-standard palette size: " + width + "x" + height + ", should be: " + this.width + "x" + this.height + ", path: " + str);
         }

         this.width = width;
         this.height = height;
         if (this.width <= 0 || this.height <= 0) {
            warn("Invalid palette size: " + width + "x" + height + ", path: " + str);
            return;
         }

         this.colors = new int[width * height];
         readBufferedImage.getRGB(0, 0, width, height, this.colors, 0, width);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void dbg(String str) {
      Config.dbg("CustomColors: " + str);
   }

   public static void warn(String str) {
      Config.warn("CustomColors: " + str);
   }

   public static String parseTexture(String str, String str2, String str3) {
      if (str != null) {
         str = str.trim();
         String str4 = ".png";
         if (str.endsWith(str4)) {
            str = str.substring(0, str.length() - str4.length());
         }

         return fixTextureName(str, str3);
      } else {
         String str4 = str2;
         int lastIndexOf = str2.lastIndexOf(47);
         if (lastIndexOf >= 0) {
            str4 = str2.substring(lastIndexOf + 1);
         }

         int lastIndexOf2 = str4.lastIndexOf(46);
         if (lastIndexOf2 >= 0) {
            str4 = str4.substring(0, lastIndexOf2);
         }

         return fixTextureName(str4, str3);
      }
   }

   public static String fixTextureName(String str, String str2) {
      str = TextureUtils.fixResourcePath(str, str2);
      if (!str.startsWith(str2) && !str.startsWith("textures/") && !str.startsWith("mcpatcher/")) {
         str = str2 + "/" + str;
      }

      if (str.endsWith(".png")) {
         str = str.substring(0, str.length() - 4);
      }

      String str3 = "textures/blocks/";
      if (str.startsWith(str3)) {
         str = str.substring(str3.length());
      }

      if (str.startsWith("/")) {
         str = str.substring(1);
      }

      return str;
   }

   public boolean matchesBlock(BlockStateBase blockStateBase) {
      return Matches.block(blockStateBase, this.matchBlocks);
   }

   public int getColorRandom() {
      if (this.format == 2) {
         return this.color;
      }

      int nextInt = CustomColors.random.nextInt(this.colors.length);
      return this.colors[nextInt];
   }

   public int getColor(int n) {
      n = Config.limit(n, 0, this.colors.length - 1);
      return this.colors[n] & 16777215;
   }

   public int getColor(int n, int n2) {
      n = Config.limit(n, 0, this.width - 1);
      n2 = Config.limit(n2, 0, this.height - 1);
      return this.colors[n2 * this.width + n] & 16777215;
   }

   public float[][] getColorsRgb() {
      if (this.colorsRgb == null) {
         this.colorsRgb = toRgb(this.colors);
      }

      return this.colorsRgb;
   }

   @Override
   public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
      return this.getColor(blockAccess, blockPos);
   }

   public int getColor(IBlockAccess blockAccess, BlockPos blockPos) {
      BiomeGenBase colorBiome = CustomColors.getColorBiome(blockAccess, blockPos);
      return this.getColor(colorBiome, blockPos);
   }

   @Override
   public boolean isColorConstant() {
      return this.format == 2;
   }

   public int getColor(BiomeGenBase biomeGenBase, BlockPos blockPos) {
      return this.format == 0 ? this.getColorVanilla(biomeGenBase, blockPos) : (this.format == 1 ? this.getColorGrid(biomeGenBase, blockPos) : this.color);
   }

   public int getColorSmooth(IBlockAccess blockAccess, double d, double d2, double d3, int n) {
      if (this.format == 2) {
         return this.color;
      }

      int n2 = MathHelper.floor_double(d);
      int n3 = MathHelper.floor_double(d2);
      int n4 = MathHelper.floor_double(d3);
      int n5 = 0;
      int n6 = 0;
      int n7 = 0;
      int n8 = 0;
      BlockPosM blockPosM = new BlockPosM(0, 0, 0);

      for (int i = n2 - n; i <= n2 + n; i++) {
         for (int j = n4 - n; j <= n4 + n; j++) {
            blockPosM.setXyz(i, n3, j);
            int color = this.getColor(blockAccess, blockPosM);
            n5 += color >> 16 & 0xFF;
            n6 += color >> 8 & 0xFF;
            n7 += color & 0xFF;
            n8++;
         }
      }

      int n9 = n5 / n8;
      int n10 = n6 / n8;
      int n11 = n7 / n8;
      return n9 << 16 | n10 << 8 | n11;
   }

   public int getColorVanilla(BiomeGenBase biomeGenBase, BlockPos blockPos) {
      double d = MathHelper.clamp_float(biomeGenBase.getFloatTemperature(blockPos), 0.0F, 1.0F);
      double d2 = MathHelper.clamp_float(biomeGenBase.getFloatRainfall(), 0.0F, 1.0F);
      d2 *= d;
      int n = (int)((1.0 - d) * (this.width - 1));
      int n2 = (int)((1.0 - d2) * (this.height - 1));
      return this.getColor(n, n2);
   }

   public int getColorGrid(BiomeGenBase biomeGenBase, BlockPos blockPos) {
      int biomeID = biomeGenBase.biomeID;
      int n = blockPos.getY() - this.yOffset;
      if (this.yVariance > 0) {
         int n2 = blockPos.getX() << 16 + blockPos.getZ();
         int intHash = Config.intHash(n2);
         int n3 = this.yVariance * 2 + 1;
         int n4 = (intHash & 0xFF) % n3 - this.yVariance;
         n += n4;
      }

      return this.getColor(biomeID, n);
   }

   public int getLength() {
      return this.format == 2 ? 1 : this.colors.length;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public static float[][] toRgb(int[] ints) {
      float[][] floatses = new float[ints.length][3];

      for (int i = 0; i < ints.length; i++) {
         int n = ints[i];
         float f = (n >> 16 & 0xFF) / 255.0F;
         float f2 = (n >> 8 & 0xFF) / 255.0F;
         float f3 = (n & 0xFF) / 255.0F;
         float[] floats = floatses[i];
         floats[0] = f;
         floats[1] = f2;
         floats[2] = f3;
      }

      return floatses;
   }

   public void addMatchBlock(MatchBlock matchBlock) {
      if (this.matchBlocks == null) {
         this.matchBlocks = new MatchBlock[0];
      }

      this.matchBlocks = (MatchBlock[])Config.addObjectToArray(this.matchBlocks, matchBlock);
   }

   public void addMatchBlock(int n, int n2) {
      MatchBlock matchBlock = this.getMatchBlock(n);
      if (matchBlock != null) {
         if (n2 >= 0) {
            matchBlock.addMetadata(n2);
         }
      } else {
         this.addMatchBlock(new MatchBlock(n, n2));
      }
   }

   public MatchBlock getMatchBlock(int n) {
      if (this.matchBlocks == null) {
         return null;
      }

      for (int i = 0; i < this.matchBlocks.length; i++) {
         MatchBlock matchBlock = this.matchBlocks[i];
         if (matchBlock.getBlockId() == n) {
            return matchBlock;
         }
      }

      return null;
   }

   public int[] getMatchBlockIds() {
      if (this.matchBlocks == null) {
         return null;
      }

      HashSet set = new HashSet();

      for (int i = 0; i < this.matchBlocks.length; i++) {
         MatchBlock matchBlock = this.matchBlocks[i];
         if (matchBlock.getBlockId() >= 0) {
            set.add(matchBlock.getBlockId());
         }
      }

      Integer[] toArray = (Integer[])set.toArray(new Integer[set.size()]);
      int[] ints = new int[toArray.length];

      for (int i = 0; i < toArray.length; i++) {
         ints[i] = toArray[i];
      }

      return ints;
   }

   @Override
   public String toString() {
      return "" + this.basePath + "/" + this.name + ", blocks: " + Config.arrayToString(this.matchBlocks) + ", source: " + this.source;
   }
}
