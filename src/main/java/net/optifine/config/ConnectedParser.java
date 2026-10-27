package net.optifine.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.Config;
import net.optifine.ConnectedProperties;
import net.optifine.util.EntityUtils;

public class ConnectedParser {
   public String context = null;
   public static VillagerProfession[] PROFESSIONS_INVALID = new VillagerProfession[0];
   public static EnumDyeColor[] DYE_COLORS_INVALID = new EnumDyeColor[0];
   public static INameGetter<Enum> NAME_GETTER_ENUM = new INameGetter<Enum>() {
      public String getName(Enum enumValue) {
         return enumValue.name();
      }
   };
   public static INameGetter<EnumDyeColor> NAME_GETTER_DYE_COLOR = new INameGetter<EnumDyeColor>() {
      public String getName(EnumDyeColor enumDyeColor) {
         return enumDyeColor.getName();
      }
   };

   public ConnectedParser(String str) {
      this.context = str;
   }

   public String parseName(String str) {
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

   public String parseBasePath(String str) {
      int lastIndexOf = str.lastIndexOf(47);
      return lastIndexOf < 0 ? "" : str.substring(0, lastIndexOf);
   }

   public MatchBlock[] parseMatchBlocks(String str) {
      if (str == null) {
         return null;
      }

      ArrayList list = new ArrayList();
      String[] tokenize = Config.tokenize(str, " ");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         MatchBlock[] parseMatchBlock = this.parseMatchBlock(str2);
         if (parseMatchBlock != null) {
            list.addAll(Arrays.asList(parseMatchBlock));
         }
      }

      return (MatchBlock[])list.toArray(new MatchBlock[list.size()]);
   }

   public IBlockState parseBlockState(String str, IBlockState blockState) {
      MatchBlock[] parseMatchBlock = this.parseMatchBlock(str);
      if (parseMatchBlock == null) {
         return blockState;
      }

      if (parseMatchBlock.length != 1) {
         return blockState;
      }

      MatchBlock matchBlock = parseMatchBlock[0];
      int blockId = matchBlock.getBlockId();
      Block blockById = Block.getBlockById(blockId);
      return blockById.getDefaultState();
   }

   public MatchBlock[] parseMatchBlock(String str) {
      if (str == null) {
         return null;
      }

      str = str.trim();
      if (str.length() <= 0) {
         return null;
      }

      String[] tokenize = Config.tokenize(str, ":");
      String str2 = "minecraft";
      byte b = 0;
      if (tokenize.length > 1 && this.isFullBlockName(tokenize)) {
         str2 = tokenize[0];
         b = 1;
      } else {
         str2 = "minecraft";
         b = 0;
      }

      String str3 = tokenize[b];
      String[] copyOfRange = Arrays.copyOfRange(tokenize, b + 1, tokenize.length);
      Block[] parseBlockPart = this.parseBlockPart(str2, str3);
      if (parseBlockPart == null) {
         return null;
      }

      MatchBlock[] matchBlocks = new MatchBlock[parseBlockPart.length];

      for (int i = 0; i < parseBlockPart.length; i++) {
         Block block = parseBlockPart[i];
         int idFromBlock = Block.getIdFromBlock(block);
         int[] ints = null;
         if (copyOfRange.length > 0) {
            ints = this.parseBlockMetadatas(block, copyOfRange);
            if (ints == null) {
               return null;
            }
         }

         MatchBlock matchBlock = new MatchBlock(idFromBlock, ints);
         matchBlocks[i] = matchBlock;
      }

      return matchBlocks;
   }

   public boolean isFullBlockName(String[] strs) {
      if (strs.length < 2) {
         return false;
      }

      String str = strs[1];
      return str.length() < 1 ? false : (this.startsWithDigit(str) ? false : !str.contains("="));
   }

   public boolean startsWithDigit(String str) {
      if (str == null) {
         return false;
      }

      if (str.length() < 1) {
         return false;
      }

      char charAt = str.charAt(0);
      return Character.isDigit(charAt);
   }

   public Block[] parseBlockPart(String str, String str2) {
      if (this.startsWithDigit(str2)) {
         int[] parseIntList = this.parseIntList(str2);
         if (parseIntList == null) {
            return null;
         }

         Block[] blocks = new Block[parseIntList.length];

         for (int i = 0; i < parseIntList.length; i++) {
            int n = parseIntList[i];
            Block blockById = Block.getBlockById(n);
            if (blockById == null) {
               this.warn("Block not found for id: " + n);
               return null;
            }

            blocks[i] = blockById;
         }

         return blocks;
      } else {
         String str3 = str + ":" + str2;
         Block blockFromName = Block.getBlockFromName(str3);
         if (blockFromName == null) {
            this.warn("Block not found for name: " + str3);
            return null;
         } else {
            return new Block[]{blockFromName};
         }
      }
   }

   public int[] parseBlockMetadatas(Block block, String[] strs) {
      if (strs.length <= 0) {
         return null;
      }

      String str = strs[0];
      if (this.startsWithDigit(str)) {
         return this.parseIntList(str);
      }

      IBlockState defaultState = block.getDefaultState();
      Collection propertyNames = defaultState.getPropertyNames();
      HashMap map = new HashMap();

      for (int i = 0; i < strs.length; i++) {
         String str2 = strs[i];
         if (str2.length() > 0) {
            String[] tokenize = Config.tokenize(str2, "=");
            if (tokenize.length != 2) {
               this.warn("Invalid block property: " + str2);
               return null;
            }

            String str3 = tokenize[0];
            String str4 = tokenize[1];
            IProperty property = ConnectedProperties.getProperty(str3, propertyNames);
            if (property == null) {
               this.warn("Property not found: " + str3 + ", block: " + block);
               return null;
            }

            List list = (List)map.get(str3);
            if (list == null) {
               list = new ArrayList();
               map.put(property, list);
            }

            String[] tokenize2 = Config.tokenize(str4, ",");

            for (int j = 0; j < tokenize2.length; j++) {
               String str5 = tokenize2[j];
               Comparable parsePropertyValue2 = parsePropertyValue(property, str5);
               if (parsePropertyValue2 == null) {
                  this.warn("Property value not found: " + str5 + ", property: " + str3 + ", block: " + block);
                  return null;
               }

               list.add(parsePropertyValue2);
            }
         }
      }

      if (map.isEmpty()) {
         return null;
      }

      ArrayList list = new ArrayList();

      for (int i = 0; i < 16; i++) {
         int n = i;

         try {
            IBlockState stateFromMeta = this.getStateFromMeta(block, n);
            if (this.matchState(stateFromMeta, map)) {
               list.add(n);
            }
         } catch (IllegalArgumentException e) {
         }
      }

      if (list.size() == 16) {
         return null;
      }

      int[] ints = new int[list.size()];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = (Integer)list.get(i);
      }

      return ints;
   }

   public IBlockState getStateFromMeta(Block block, int n) {
      try {
         IBlockState stateFromMeta = block.getStateFromMeta(n);
         if (block == Blocks.double_plant && n > 7) {
            IBlockState stateFromMeta2 = block.getStateFromMeta(n & 7);
            stateFromMeta = stateFromMeta.withProperty(BlockDoublePlant.VARIANT, stateFromMeta2.getValue(BlockDoublePlant.VARIANT));
         }

         return stateFromMeta;
      } catch (IllegalArgumentException e) {
         return block.getDefaultState();
      }
   }

   public static Comparable parsePropertyValue(IProperty property, String str) {
      Class valueClass = property.getValueClass();
      Comparable parseValue2 = parseValue(str, valueClass);
      if (parseValue2 == null) {
         Collection allowedValues = property.getAllowedValues();
         parseValue2 = getPropertyValue(str, allowedValues);
      }

      return parseValue2;
   }

   public static Comparable getPropertyValue(String str, Collection collection) {
      for (Object obj : collection) {
         Comparable comparable = (Comparable)obj;
         if (getValueName(comparable).equals(str)) {
            return comparable;
         }
      }

      return null;
   }

   public static Object getValueName(Comparable comparable) {
      if (comparable instanceof IStringSerializable) {
         IStringSerializable stringSerializable = (IStringSerializable)comparable;
         return stringSerializable.getName();
      } else {
         return comparable.toString();
      }
   }

   public static Comparable parseValue(String str, Class clazz) {
      if (clazz == String.class) {
         return str;
      } else if (clazz == Boolean.class) {
         return Boolean.valueOf(str);
      } else if (clazz == Float.class) {
         return Float.valueOf(str);
      } else if (clazz == Double.class) {
         return Double.valueOf(str);
      } else if (clazz == Integer.class) {
         return Integer.valueOf(str);
      } else {
         return clazz == Long.class ? Long.valueOf(str) : null;
      }
   }

   public boolean matchState(IBlockState blockState, Map<IProperty, List<Comparable>> map) {
      for (IProperty property : map.keySet()) {
         List list = (List)map.get(property);
         Comparable value = blockState.getValue(property);
         if (value == null) {
            return false;
         }

         if (!list.contains(value)) {
            return false;
         }
      }

      return true;
   }

   public BiomeGenBase[] parseBiomes(String str) {
      if (str == null) {
         return null;
      }

      str = str.trim();
      boolean flag = false;
      if (str.startsWith("!")) {
         flag = true;
         str = str.substring(1);
      }

      String[] tokenize = Config.tokenize(str, " ");
      ArrayList list = new ArrayList();

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         BiomeGenBase findBiome = this.findBiome(str2);
         if (findBiome == null) {
            this.warn("Biome not found: " + str2);
         } else {
            list.add(findBiome);
         }
      }

      if (flag) {
         ArrayList list2 = new ArrayList<>(Arrays.asList(BiomeGenBase.getBiomeGenArray()));
         list2.removeAll(list);
         list = list2;
      }

      return (BiomeGenBase[])list.toArray(new BiomeGenBase[list.size()]);
   }

   public BiomeGenBase findBiome(String str) {
      str = str.toLowerCase();
      if (str.equals("nether")) {
         return BiomeGenBase.hell;
      }

      BiomeGenBase[] biomeGenArray = BiomeGenBase.getBiomeGenArray();

      for (int i = 0; i < biomeGenArray.length; i++) {
         BiomeGenBase biomeGenBase = biomeGenArray[i];
         if (biomeGenBase != null) {
            String toLowerCase = biomeGenBase.biomeName.replace(" ", "").toLowerCase();
            if (toLowerCase.equals(str)) {
               return biomeGenBase;
            }
         }
      }

      return null;
   }

   public int parseInt(String str, int n) {
      if (str == null) {
         return n;
      } else {
         str = str.trim();
         int parseInt = Config.parseInt(str, -1);
         if (parseInt < 0) {
            this.warn("Invalid number: " + str);
            return n;
         } else {
            return parseInt;
         }
      }
   }

   public int[] parseIntList(String str) {
      if (str == null) {
         return null;
      }

      ArrayList list = new ArrayList();
      String[] tokenize = Config.tokenize(str, " ,");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         if (str2.contains("-")) {
            String[] tokenize2 = Config.tokenize(str2, "-");
            if (tokenize2.length != 2) {
               this.warn("Invalid interval: " + str2 + ", when parsing: " + str);
            } else {
               int parseInt = Config.parseInt(tokenize2[0], -1);
               int parseInt2 = Config.parseInt(tokenize2[1], -1);
               if (parseInt >= 0 && parseInt2 >= 0 && parseInt <= parseInt2) {
                  for (int j = parseInt; j <= parseInt2; j++) {
                     list.add(j);
                  }
               } else {
                  this.warn("Invalid interval: " + str2 + ", when parsing: " + str);
               }
            }
         } else {
            int parseInt = Config.parseInt(str2, -1);
            if (parseInt < 0) {
               this.warn("Invalid number: " + str2 + ", when parsing: " + str);
            } else {
               list.add(parseInt);
            }
         }
      }

      int[] ints = new int[list.size()];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = (Integer)list.get(i);
      }

      return ints;
   }

   public boolean[] parseFaces(String str, boolean[] flags) {
      if (str == null) {
         return flags;
      }

      EnumSet allOf = EnumSet.allOf(EnumFacing.class);
      String[] tokenize = Config.tokenize(str, " ,");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         if (str2.equals("sides")) {
            allOf.add(EnumFacing.NORTH);
            allOf.add(EnumFacing.SOUTH);
            allOf.add(EnumFacing.WEST);
            allOf.add(EnumFacing.EAST);
         } else if (str2.equals("all")) {
            allOf.addAll(Arrays.asList(EnumFacing.VALUES));
         } else {
            EnumFacing parseFace = this.parseFace(str2);
            if (parseFace != null) {
               allOf.add(parseFace);
            }
         }
      }

      boolean[] flags2 = new boolean[EnumFacing.VALUES.length];

      for (int i = 0; i < flags2.length; i++) {
         flags2[i] = allOf.contains(EnumFacing.VALUES[i]);
      }

      return flags2;
   }

   public EnumFacing parseFace(String str) {
      str = str.toLowerCase();
      if (str.equals("bottom") || str.equals("down")) {
         return EnumFacing.DOWN;
      }

      if (str.equals("top") || str.equals("up")) {
         return EnumFacing.UP;
      }

      if (str.equals("north")) {
         return EnumFacing.NORTH;
      }

      if (str.equals("south")) {
         return EnumFacing.SOUTH;
      }

      if (str.equals("east")) {
         return EnumFacing.EAST;
      }

      if (str.equals("west")) {
         return EnumFacing.WEST;
      }

      Config.warn("Unknown face: " + str);
      return null;
   }

   public void dbg(String str) {
      Config.dbg("" + this.context + ": " + str);
   }

   public void warn(String str) {
      Config.warn("" + this.context + ": " + str);
   }

   public RangeListInt parseRangeListInt(String str) {
      if (str == null) {
         return null;
      }

      RangeListInt rangeListInt = new RangeListInt();
      String[] tokenize = Config.tokenize(str, " ,");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         RangeInt parseRangeInt = this.parseRangeInt(str2);
         if (parseRangeInt == null) {
            return null;
         }

         rangeListInt.addRange(parseRangeInt);
      }

      return rangeListInt;
   }

   public RangeInt parseRangeInt(String str) {
      if (str == null) {
         return null;
      }

      if (str.indexOf(45) >= 0) {
         String[] tokenize = Config.tokenize(str, "-");
         if (tokenize.length != 2) {
            this.warn("Invalid range: " + str);
            return null;
         }

         int parseInt = Config.parseInt(tokenize[0], -1);
         int parseInt2 = Config.parseInt(tokenize[1], -1);
         if (parseInt >= 0 && parseInt2 >= 0) {
            return new RangeInt(parseInt, parseInt2);
         }

         this.warn("Invalid range: " + str);
         return null;
      } else {
         int parseInt = Config.parseInt(str, -1);
         if (parseInt < 0) {
            this.warn("Invalid integer: " + str);
            return null;
         } else {
            return new RangeInt(parseInt, parseInt);
         }
      }
   }

   public boolean parseBoolean(String str, boolean flag) {
      if (str == null) {
         return flag;
      }

      String trim = str.toLowerCase().trim();
      if (trim.equals("true")) {
         return true;
      }

      if (trim.equals("false")) {
         return false;
      }

      this.warn("Invalid boolean: " + str);
      return flag;
   }

   public Boolean parseBooleanObject(String str) {
      if (str == null) {
         return null;
      }

      String trim = str.toLowerCase().trim();
      if (trim.equals("true")) {
         return Boolean.TRUE;
      }

      if (trim.equals("false")) {
         return Boolean.FALSE;
      }

      this.warn("Invalid boolean: " + str);
      return null;
   }

   public static int parseColor(String str, int n) {
      if (str == null) {
         return n;
      }

      str = str.trim();

      try {
         return Integer.parseInt(str, 16) & 16777215;
      } catch (NumberFormatException e) {
         return n;
      }
   }

   public static int parseColor4(String str, int n) {
      if (str == null) {
         return n;
      }

      str = str.trim();

      try {
         return (int)(Long.parseLong(str, 16) & -1L);
      } catch (NumberFormatException e) {
         return n;
      }
   }

   public EnumWorldBlockLayer parseBlockRenderLayer(String str, EnumWorldBlockLayer enumWorldBlockLayer) {
      if (str == null) {
         return enumWorldBlockLayer;
      }

      str = str.toLowerCase().trim();
      EnumWorldBlockLayer[] values = EnumWorldBlockLayer.values();

      for (int i = 0; i < values.length; i++) {
         EnumWorldBlockLayer enumWorldBlockLayer2 = values[i];
         if (str.equals(enumWorldBlockLayer2.name().toLowerCase())) {
            return enumWorldBlockLayer2;
         }
      }

      return enumWorldBlockLayer;
   }

   public <T> T parseObject(String str, T[] values, INameGetter nameGetter, String str2) {
      if (str == null) {
         return null;
      }

      String trim = str.toLowerCase().trim();

      for (int i = 0; i < values.length; i++) {
         Object obj = values[i];
         String name = nameGetter.getName(obj);
         if (name != null && name.toLowerCase().equals(trim)) {
            return (T)obj;
         }
      }

      this.warn("Invalid " + str2 + ": " + str);
      return null;
   }

   public <T> T[] parseObjects(String str, T[] values, INameGetter nameGetter, String str2, T[] values2) {
      if (str == null) {
         return null;
      }

      str = str.toLowerCase().trim();
      String[] tokenize = Config.tokenize(str, " ");
      Object[] newInstance = (Object[])Array.newInstance(values.getClass().getComponentType(), tokenize.length);

      for (int i = 0; i < tokenize.length; i++) {
         String str3 = tokenize[i];
         Object parseObject = this.parseObject(str3, values, nameGetter, str2);
         if (parseObject == null) {
            return (T[])values2;
         }

         newInstance[i] = parseObject;
      }

      return (T[])newInstance;
   }

   public Enum parseEnum(String str, Enum[] enumValues, String str2) {
      return this.parseObject(str, enumValues, NAME_GETTER_ENUM, str2);
   }

   public Enum[] parseEnums(String str, Enum[] enumValues, String str2, Enum[] enumValues2) {
      return this.parseObjects(str, enumValues, NAME_GETTER_ENUM, str2, enumValues2);
   }

   public EnumDyeColor[] parseDyeColors(String str, String str2, EnumDyeColor[] enumDyeColors) {
      return this.parseObjects(str, EnumDyeColor.values(), NAME_GETTER_DYE_COLOR, str2, enumDyeColors);
   }

   public Weather[] parseWeather(String str, String str2, Weather[] weathers) {
      return this.parseObjects(str, Weather.values(), NAME_GETTER_ENUM, str2, weathers);
   }

   public NbtTagValue parseNbtTagValue(String str, String str2) {
      return str != null && str2 != null ? new NbtTagValue(str, str2) : null;
   }

   public VillagerProfession[] parseProfessions(String str) {
      if (str == null) {
         return null;
      }

      ArrayList list = new ArrayList();
      String[] tokenize = Config.tokenize(str, " ");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         VillagerProfession parseProfession = this.parseProfession(str2);
         if (parseProfession == null) {
            this.warn("Invalid profession: " + str2);
            return PROFESSIONS_INVALID;
         }

         list.add(parseProfession);
      }

      return list.isEmpty() ? null : (VillagerProfession[])list.toArray(new VillagerProfession[list.size()]);
   }

   public VillagerProfession parseProfession(String str) {
      str = str.toLowerCase();
      String[] tokenize = Config.tokenize(str, ":");
      if (tokenize.length > 2) {
         return null;
      }

      String str2 = tokenize[0];
      String str3 = null;
      if (tokenize.length > 1) {
         str3 = tokenize[1];
      }

      int parseProfessionId2 = parseProfessionId(str2);
      if (parseProfessionId2 < 0) {
         return null;
      }

      int[] ints = null;
      if (str3 != null) {
         ints = parseCareerIds(parseProfessionId2, str3);
         if (ints == null) {
            return null;
         }
      }

      return new VillagerProfession(parseProfessionId2, ints);
   }

   public static int parseProfessionId(String str) {
      int parseInt = Config.parseInt(str, -1);
      return parseInt >= 0
         ? parseInt
         : (
            str.equals("farmer")
               ? 0
               : (
                  str.equals("librarian")
                     ? 1
                     : (str.equals("priest") ? 2 : (str.equals("blacksmith") ? 3 : (str.equals("butcher") ? 4 : (str.equals("nitwit") ? 5 : -1))))
               )
         );
   }

   public static int[] parseCareerIds(int n, String str) {
      HashSet set = new HashSet();
      String[] tokenize = Config.tokenize(str, ",");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         int parseCareerId2 = parseCareerId(n, str2);
         if (parseCareerId2 < 0) {
            return null;
         }

         set.add(parseCareerId2);
      }

      Integer[] toArray = (Integer[])set.toArray(new Integer[set.size()]);
      int[] ints = new int[toArray.length];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = toArray[i];
      }

      return ints;
   }

   public static int parseCareerId(int n, String str) {
      int parseInt = Config.parseInt(str, -1);
      if (parseInt >= 0) {
         return parseInt;
      }

      if (n == 0) {
         if (str.equals("farmer")) {
            return 1;
         }

         if (str.equals("fisherman")) {
            return 2;
         }

         if (str.equals("shepherd")) {
            return 3;
         }

         if (str.equals("fletcher")) {
            return 4;
         }
      }

      if (n == 1) {
         if (str.equals("librarian")) {
            return 1;
         }

         if (str.equals("cartographer")) {
            return 2;
         }
      }

      if (n == 2 && str.equals("cleric")) {
         return 1;
      }

      if (n == 3) {
         if (str.equals("armor")) {
            return 1;
         }

         if (str.equals("weapon")) {
            return 2;
         }

         if (str.equals("tool")) {
            return 3;
         }
      }

      if (n == 4) {
         if (str.equals("butcher")) {
            return 1;
         }

         if (str.equals("leather")) {
            return 2;
         }
      }

      return n == 5 && str.equals("nitwit") ? 1 : -1;
   }

   public int[] parseItems(String str) {
      str = str.trim();
      TreeSet treeSet = new TreeSet();
      String[] tokenize = Config.tokenize(str, " ");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         ResourceLocation resourceLocation = new ResourceLocation(str2);
         Item object = Item.itemRegistry.getObject(resourceLocation);
         if (object == null) {
            this.warn("Item not found: " + str2);
         } else {
            int idFromItem = Item.getIdFromItem(object);
            if (idFromItem < 0) {
               this.warn("Item has no ID: " + object + ", name: " + str2);
            } else {
               treeSet.add(new Integer(idFromItem));
            }
         }
      }

      Integer[] toArray = (Integer[])treeSet.toArray(new Integer[treeSet.size()]);
      return Config.toPrimitive(toArray);
   }

   public int[] parseEntities(String str) {
      str = str.trim();
      TreeSet treeSet = new TreeSet();
      String[] tokenize = Config.tokenize(str, " ");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         int entityIdByName = EntityUtils.getEntityIdByName(str2);
         if (entityIdByName < 0) {
            this.warn("Entity not found: " + str2);
         } else {
            treeSet.add(new Integer(entityIdByName));
         }
      }

      Integer[] toArray = (Integer[])treeSet.toArray(new Integer[treeSet.size()]);
      return Config.toPrimitive(toArray);
   }
}
