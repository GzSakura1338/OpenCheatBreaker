package net.optifine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel.Builder;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.IParserInt;
import net.optifine.config.NbtTagValue;
import net.optifine.config.ParserEnchantmentId;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.reflect.Reflector;
import net.optifine.render.Blender;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

public class CustomItemProperties {
   public String name = null;
   public String basePath = null;
   public int type = 1;
   public int[] items = null;
   public String texture = null;
   public Map<String, String> mapTextures = null;
   public String model = null;
   public Map<String, String> mapModels = null;
   public RangeListInt damage = null;
   public boolean damagePercent = false;
   public int damageMask = 0;
   public RangeListInt stackSize = null;
   public RangeListInt enchantmentIds = null;
   public RangeListInt enchantmentLevels = null;
   public NbtTagValue[] nbtTagValues = null;
   public int hand = 0;
   public int blend = 1;
   public float speed = 0.0F;
   public float rotation = 0.0F;
   public int layer = 0;
   public float duration = 1.0F;
   public int weight = 0;
   public ResourceLocation textureLocation = null;
   public Map mapTextureLocations = null;
   public TextureAtlasSprite sprite = null;
   public Map mapSprites = null;
   public IBakedModel bakedModelTexture = null;
   public Map<String, IBakedModel> mapBakedModelsTexture = null;
   public IBakedModel bakedModelFull = null;
   public Map<String, IBakedModel> mapBakedModelsFull = null;
   public int textureWidth = 0;
   public int textureHeight = 0;
   public static final int TYPE_ARMOR = 3;
   public static final String INVENTORY = "inventory";
   public static final int CONSTANT_1 = 1;
   public static final int CONSTANT_1_2 = 1;
   public static final int CONSTANT_0 = 0;
   public static final int CONSTANT_2 = 2;
   public static final int CONSTANT_0_2 = 0;
   public static final int CONSTANT_2_2 = 2;

   public CustomItemProperties(Properties properties, String str) {
      this.name = parseName(str);
      this.basePath = parseBasePath(str);
      this.type = this.parseType(properties.getProperty("type"));
      this.items = this.parseItems(properties.getProperty("items"), properties.getProperty("matchItems"));
      this.mapModels = parseModels(properties, this.basePath);
      this.model = parseModel(properties.getProperty("model"), str, this.basePath, this.type, this.mapModels);
      this.mapTextures = parseTextures(properties, this.basePath);
      boolean flag = this.mapModels == null && this.model == null;
      this.texture = parseTexture(
         properties.getProperty("texture"), properties.getProperty("tile"), properties.getProperty("source"), str, this.basePath, this.type, this.mapTextures, flag
      );
      String property = properties.getProperty("damage");
      if (property != null) {
         this.damagePercent = property.contains("%");
         property = property.replace("%", "");
         this.damage = this.parseRangeListInt(property);
         this.damageMask = this.parseInt(properties.getProperty("damageMask"), 0);
      }

      this.stackSize = this.parseRangeListInt(properties.getProperty("stackSize"));
      this.enchantmentIds = this.parseRangeListInt(properties.getProperty("enchantmentIDs"), new ParserEnchantmentId());
      this.enchantmentLevels = this.parseRangeListInt(properties.getProperty("enchantmentLevels"));
      this.nbtTagValues = this.parseNbtTagValues(properties);
      this.hand = this.parseHand(properties.getProperty("hand"));
      this.blend = Blender.parseBlend(properties.getProperty("blend"));
      this.speed = this.parseFloat(properties.getProperty("speed"), 0.0F);
      this.rotation = this.parseFloat(properties.getProperty("rotation"), 0.0F);
      this.layer = this.parseInt(properties.getProperty("layer"), 0);
      this.weight = this.parseInt(properties.getProperty("weight"), 0);
      this.duration = this.parseFloat(properties.getProperty("duration"), 1.0F);
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

   public int parseType(String str) {
      if (str == null) {
         return 1;
      }

      if (str.equals("item")) {
         return 1;
      }

      if (str.equals("enchantment")) {
         return 2;
      }

      if (str.equals("armor")) {
         return 3;
      }

      Config.warn("Unknown method: " + str);
      return 0;
   }

   public int[] parseItems(String str, String str2) {
      if (str == null) {
         str = str2;
      }

      if (str == null) {
         return null;
      }

      str = str.trim();
      TreeSet treeSet = new TreeSet();
      String[] tokenize = Config.tokenize(str, " ");

      for (int i = 0; i < tokenize.length; i++) {
         String str3 = tokenize[i];
         int parseInt = Config.parseInt(str3, -1);
         if (parseInt >= 0) {
            treeSet.add(new Integer(parseInt));
         } else {
            if (str3.contains("-")) {
               String[] tokenize2 = Config.tokenize(str3, "-");
               if (tokenize2.length == 2) {
                  int parseInt2 = Config.parseInt(tokenize2[0], -1);
                  int parseInt3 = Config.parseInt(tokenize2[1], -1);
                  if (parseInt2 >= 0 && parseInt3 >= 0) {
                     int min = Math.min(parseInt2, parseInt3);
                     int max = Math.max(parseInt2, parseInt3);

                     for (int j = min; j <= max; j++) {
                        treeSet.add(new Integer(j));
                     }
                     continue;
                  }
               }
            }

            Item byNameOrId = Item.getByNameOrId(str3);
            if (byNameOrId == null) {
               Config.warn("Item not found: " + str3);
            } else {
               int idFromItem = Item.getIdFromItem(byNameOrId);
               if (idFromItem <= 0) {
                  Config.warn("Item not found: " + str3);
               } else {
                  treeSet.add(new Integer(idFromItem));
               }
            }
         }
      }

      Integer[] toArray = (Integer[])treeSet.toArray(new Integer[treeSet.size()]);
      int[] ints = new int[toArray.length];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = toArray[i];
      }

      return ints;
   }

   public static String parseTexture(String str, String str2, String str3, String str4, String str5, int n, Map<String, String> map, boolean flag) {
      if (str == null) {
         str = str2;
      }

      if (str == null) {
         str = str3;
      }

      if (str != null) {
         String str6 = ".png";
         if (str.endsWith(str6)) {
            str = str.substring(0, str.length() - str6.length());
         }

         return fixTextureName(str, str5);
      } else {
         if (n == 3) {
            return null;
         }

         if (map != null) {
            String str6 = (String)map.get("texture.bow_standby");
            if (str6 != null) {
               return str6;
            }
         }

         if (!flag) {
            return null;
         }

         String str6 = str4;
         int lastIndexOf = str4.lastIndexOf(47);
         if (lastIndexOf >= 0) {
            str6 = str4.substring(lastIndexOf + 1);
         }

         int lastIndexOf2 = str6.lastIndexOf(46);
         if (lastIndexOf2 >= 0) {
            str6 = str6.substring(0, lastIndexOf2);
         }

         return fixTextureName(str6, str5);
      }
   }

   public static Map parseTextures(Properties properties, String str) {
      String str2 = "texture.";
      Map matchingProperties = getMatchingProperties(properties, str2);
      if (matchingProperties.size() <= 0) {
         return null;
      }

      Set keySet = matchingProperties.keySet();
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (Object obj : keySet) {
         String str3 = (String)obj;
         String str4 = (String)matchingProperties.get(str3);
         str4 = fixTextureName(str4, str);
         linkedHashMap.put(str3, str4);
      }

      return linkedHashMap;
   }

   public static String fixTextureName(String str, String str2) {
      str = TextureUtils.fixResourcePath(str, str2);
      if (!str.startsWith(str2) && !str.startsWith("textures/") && !str.startsWith("mcpatcher/")) {
         str = str2 + "/" + str;
      }

      if (str.endsWith(".png")) {
         str = str.substring(0, str.length() - 4);
      }

      if (str.startsWith("/")) {
         str = str.substring(1);
      }

      return str;
   }

   public static String parseModel(String str, String str2, String str3, int n, Map<String, String> map) {
      if (str != null) {
         String str4 = ".json";
         if (str.endsWith(str4)) {
            str = str.substring(0, str.length() - str4.length());
         }

         return fixModelName(str, str3);
      } else {
         if (n == 3) {
            return null;
         }

         if (map != null) {
            String str4 = (String)map.get("model.bow_standby");
            if (str4 != null) {
               return str4;
            }
         }

         return str;
      }
   }

   public static Map parseModels(Properties properties, String str) {
      String str2 = "model.";
      Map matchingProperties = getMatchingProperties(properties, str2);
      if (matchingProperties.size() <= 0) {
         return null;
      }

      Set keySet = matchingProperties.keySet();
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (Object obj : keySet) {
         String str3 = (String)obj;
         String str4 = (String)matchingProperties.get(str3);
         str4 = fixModelName(str4, str);
         linkedHashMap.put(str3, str4);
      }

      return linkedHashMap;
   }

   public static String fixModelName(String str, String str2) {
      str = TextureUtils.fixResourcePath(str, str2);
      boolean flag = str.startsWith("block/") || str.startsWith("item/");
      if (!str.startsWith(str2) && !flag && !str.startsWith("mcpatcher/")) {
         str = str2 + "/" + str;
      }

      String str3 = ".json";
      if (str.endsWith(str3)) {
         str = str.substring(0, str.length() - str3.length());
      }

      if (str.startsWith("/")) {
         str = str.substring(1);
      }

      return str;
   }

   public int parseInt(String str, int n) {
      if (str == null) {
         return n;
      } else {
         str = str.trim();
         int parseInt = Config.parseInt(str, Integer.MIN_VALUE);
         if (parseInt == Integer.MIN_VALUE) {
            Config.warn("Invalid integer: " + str);
            return n;
         } else {
            return parseInt;
         }
      }
   }

   public float parseFloat(String str, float f) {
      if (str == null) {
         return f;
      } else {
         str = str.trim();
         float parseFloat = Config.parseFloat(str, Float.MIN_VALUE);
         if (parseFloat == Float.MIN_VALUE) {
            Config.warn("Invalid float: " + str);
            return f;
         } else {
            return parseFloat;
         }
      }
   }

   public RangeListInt parseRangeListInt(String str) {
      return this.parseRangeListInt(str, (IParserInt)null);
   }

   public RangeListInt parseRangeListInt(String str, IParserInt parserInt) {
      if (str == null) {
         return null;
      }

      String[] tokenize = Config.tokenize(str, " ");
      RangeListInt rangeListInt = new RangeListInt();

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         if (parserInt != null) {
            int parse = parserInt.parse(str2, Integer.MIN_VALUE);
            if (parse != Integer.MIN_VALUE) {
               rangeListInt.addRange(new RangeInt(parse, parse));
               continue;
            }
         }

         RangeInt parseRangeInt = this.parseRangeInt(str2);
         if (parseRangeInt == null) {
            Config.warn("Invalid range list: " + str);
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

      str = str.trim();
      int n = str.length() - str.replace("-", "").length();
      if (n > 1) {
         Config.warn("Invalid range: " + str);
         return null;
      }

      String[] tokenize = Config.tokenize(str, "- ");
      int[] ints = new int[tokenize.length];

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         int parseInt = Config.parseInt(str2, -1);
         if (parseInt < 0) {
            Config.warn("Invalid range: " + str);
            return null;
         }

         ints[i] = parseInt;
      }

      if (ints.length == 1) {
         int n2 = ints[0];
         if (str.startsWith("-")) {
            return new RangeInt(0, n2);
         } else {
            return str.endsWith("-") ? new RangeInt(n2, 65535) : new RangeInt(n2, n2);
         }
      } else if (ints.length == 2) {
         int min = Math.min(ints[0], ints[1]);
         int max = Math.max(ints[0], ints[1]);
         return new RangeInt(min, max);
      } else {
         Config.warn("Invalid range: " + str);
         return null;
      }
   }

   public NbtTagValue[] parseNbtTagValues(Properties properties) {
      String str = "nbt.";
      Map matchingProperties = getMatchingProperties(properties, str);
      if (matchingProperties.size() <= 0) {
         return null;
      }

      ArrayList list = new ArrayList();

      for (Object obj : matchingProperties.keySet()) {
         String str2 = (String)obj;
         String str3 = (String)matchingProperties.get(str2);
         String substring = str2.substring(str.length());
         NbtTagValue nbtTagValue = new NbtTagValue(substring, str3);
         list.add(nbtTagValue);
      }

      return (NbtTagValue[])list.toArray(new NbtTagValue[list.size()]);
   }

   public static Map getMatchingProperties(Properties properties, String str) {
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (Object obj : properties.keySet()) {
         String str2 = (String)obj;
         String property = properties.getProperty(str2);
         if (str2.startsWith(str)) {
            linkedHashMap.put(str2, property);
         }
      }

      return linkedHashMap;
   }

   public int parseHand(String str) {
      if (str == null) {
         return 0;
      }

      str = str.toLowerCase();
      if (str.equals("any")) {
         return 0;
      }

      if (str.equals("main")) {
         return 1;
      }

      if (str.equals("off")) {
         return 2;
      }

      Config.warn("Invalid hand: " + str);
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

      if (this.type == 0) {
         Config.warn("No type defined: " + str);
         return false;
      }

      if (this.type == 1 || this.type == 3) {
         if (this.items == null) {
            this.items = this.detectItems();
         }

         if (this.items == null) {
            Config.warn("No items defined: " + str);
            return false;
         }
      }

      if (this.texture == null && this.mapTextures == null && this.model == null && this.mapModels == null) {
         Config.warn("No texture or model specified: " + str);
         return false;
      } else if (this.type == 2 && this.enchantmentIds == null) {
         Config.warn("No enchantmentIDs specified: " + str);
         return false;
      } else {
         return true;
      }
   }

   public int[] detectItems() {
      Item byNameOrId = Item.getByNameOrId(this.name);
      if (byNameOrId == null) {
         return null;
      }

      int idFromItem = Item.getIdFromItem(byNameOrId);
      return idFromItem <= 0 ? null : new int[]{idFromItem};
   }

   public void updateIcons(TextureMap textureMap) {
      if (this.texture != null) {
         this.textureLocation = this.getTextureLocation(this.texture);
         if (this.type == 1) {
            ResourceLocation spriteLocation = this.getSpriteLocation(this.textureLocation);
            this.sprite = textureMap.registerSprite(spriteLocation);
         }
      }

      if (this.mapTextures != null) {
         this.mapTextureLocations = new HashMap();
         this.mapSprites = new HashMap();

         for (String str : this.mapTextures.keySet()) {
            String str2 = this.mapTextures.get(str);
            ResourceLocation textureLocation = this.getTextureLocation(str2);
            this.mapTextureLocations.put(str, textureLocation);
            if (this.type == 1) {
               ResourceLocation spriteLocation = this.getSpriteLocation(textureLocation);
               TextureAtlasSprite registerSprite = textureMap.registerSprite(spriteLocation);
               this.mapSprites.put(str, registerSprite);
            }
         }
      }
   }

   public ResourceLocation getTextureLocation(String str) {
      if (str == null) {
         return null;
      }

      ResourceLocation resourceLocation = new ResourceLocation(str);
      String resourceDomain = resourceLocation.getResourceDomain();
      String resourcePath = resourceLocation.getResourcePath();
      if (!resourcePath.contains("/")) {
         resourcePath = "textures/items/" + resourcePath;
      }

      String str2 = resourcePath + ".png";
      ResourceLocation resourceLocation2 = new ResourceLocation(resourceDomain, str2);
      boolean hasResource = Config.hasResource(resourceLocation2);
      if (!hasResource) {
         Config.warn("File not found: " + str2);
      }

      return resourceLocation2;
   }

   public ResourceLocation getSpriteLocation(ResourceLocation resourceLocation) {
      String resourcePath = resourceLocation.getResourcePath();
      resourcePath = StrUtils.removePrefix(resourcePath, "textures/");
      resourcePath = StrUtils.removeSuffix(resourcePath, ".png");
      return new ResourceLocation(resourceLocation.getResourceDomain(), resourcePath);
   }

   public void updateModelTexture(TextureMap textureMap, ItemModelGenerator itemModelGenerator) {
      if (this.texture != null || this.mapTextures != null) {
         String[] modelTextures = this.getModelTextures();
         boolean isUseTint = this.isUseTint();
         this.bakedModelTexture = makeBakedModel(textureMap, itemModelGenerator, modelTextures, isUseTint);
         if (this.type == 1 && this.mapTextures != null) {
            for (String str : this.mapTextures.keySet()) {
               String str2 = this.mapTextures.get(str);
               String removePrefix = StrUtils.removePrefix(str, "texture.");
               if (removePrefix.startsWith("bow") || removePrefix.startsWith("fishing_rod") || removePrefix.startsWith("shield")) {
                  String[] strs = new String[]{str2};
                  IBakedModel makeBakedModel2 = makeBakedModel(textureMap, itemModelGenerator, strs, isUseTint);
                  if (this.mapBakedModelsTexture == null) {
                     this.mapBakedModelsTexture = new HashMap<>();
                  }

                  this.mapBakedModelsTexture.put(removePrefix, makeBakedModel2);
               }
            }
         }
      }
   }

   public boolean isUseTint() {
      return true;
   }

   public static IBakedModel makeBakedModel(TextureMap textureMap, ItemModelGenerator itemModelGenerator, String[] strs, boolean flag) {
      String[] strs2 = new String[strs.length];

      for (int i = 0; i < strs2.length; i++) {
         String str = strs[i];
         strs2[i] = StrUtils.removePrefix(str, "textures/");
      }

      ModelBlock makeModelBlock2 = makeModelBlock(strs2);
      ModelBlock makeItemModel = itemModelGenerator.makeItemModel(textureMap, makeModelBlock2);
      return bakeModel(textureMap, makeItemModel, flag);
   }

   public String[] getModelTextures() {
      if (this.type == 1 && this.items.length == 1) {
         Item itemById = Item.getItemById(this.items[0]);
         if (itemById == Items.potionitem && this.damage != null && this.damage.getCountRanges() > 0) {
            RangeInt range = this.damage.getRange(0);
            int min = range.getMin();
            boolean flag = (min & 16384) != 0;
            String mapTexture = this.getMapTexture(this.mapTextures, "texture.potion_overlay", "items/potion_overlay");
            String str = null;
            if (flag) {
               str = this.getMapTexture(this.mapTextures, "texture.potion_bottle_splash", "items/potion_bottle_splash");
            } else {
               str = this.getMapTexture(this.mapTextures, "texture.potion_bottle_drinkable", "items/potion_bottle_drinkable");
            }

            return new String[]{mapTexture, str};
         }

         if (itemById instanceof ItemArmor) {
            ItemArmor itemArmor = (ItemArmor)itemById;
            if (itemArmor.getArmorMaterial() == ArmorMaterial.LEATHER) {
               String str = "leather";
               String str2 = "helmet";
               if (itemArmor.armorType == 0) {
                  str2 = "helmet";
               }

               if (itemArmor.armorType == 1) {
                  str2 = "chestplate";
               }

               if (itemArmor.armorType == 2) {
                  str2 = "leggings";
               }

               if (itemArmor.armorType == 3) {
                  str2 = "boots";
               }

               String str3 = str + "_" + str2;
               String mapTexture = this.getMapTexture(this.mapTextures, "texture." + str3, "items/" + str3);
               String mapTexture2 = this.getMapTexture(this.mapTextures, "texture." + str3 + "_overlay", "items/" + str3 + "_overlay");
               return new String[]{mapTexture, mapTexture2};
            }
         }
      }

      return new String[]{this.texture};
   }

   public String getMapTexture(Map<String, String> map, String str, String str2) {
      if (map == null) {
         return str2;
      }

      String str3 = (String)map.get(str);
      return str3 == null ? str2 : str3;
   }

   public static ModelBlock makeModelBlock(String[] strs) {
      StringBuffer sb = new StringBuffer();
      sb.append("{\"parent\": \"builtin/generated\",\"textures\": {");

      for (int i = 0; i < strs.length; i++) {
         String str = strs[i];
         if (i > 0) {
            sb.append(", ");
         }

         sb.append("\"layer" + i + "\": \"" + str + "\"");
      }

      sb.append("}}");
      String str = sb.toString();
      return ModelBlock.deserialize(str);
   }

   public static IBakedModel bakeModel(TextureMap textureMap, ModelBlock modelBlock, boolean flag) {
      ModelRotation modelRotation = ModelRotation.X0_Y0;
      boolean flag2 = false;
      String resolveTextureName = modelBlock.resolveTextureName("particle");
      TextureAtlasSprite atlasSprite = textureMap.getAtlasSprite(new ResourceLocation(resolveTextureName).toString());
      Builder setTexture = new Builder(modelBlock).setTexture(atlasSprite);

      for (BlockPart blockPart : modelBlock.getElements()) {
         for (EnumFacing enumFacing : blockPart.mapFaces.keySet()) {
            BlockPartFace blockPartFace = blockPart.mapFaces.get(enumFacing);
            if (!flag) {
               blockPartFace = new BlockPartFace(blockPartFace.cullFace, -1, blockPartFace.texture, blockPartFace.blockFaceUV);
            }

            String resolveTextureName2 = modelBlock.resolveTextureName(blockPartFace.texture);
            TextureAtlasSprite atlasSprite2 = textureMap.getAtlasSprite(new ResourceLocation(resolveTextureName2).toString());
            BakedQuad makeBakedQuad2 = makeBakedQuad(blockPart, blockPartFace, atlasSprite2, enumFacing, modelRotation, flag2);
            if (blockPartFace.cullFace == null) {
               setTexture.addGeneralQuad(makeBakedQuad2);
            } else {
               setTexture.addFaceQuad(modelRotation.rotateFace(blockPartFace.cullFace), makeBakedQuad2);
            }
         }
      }

      return setTexture.makeBakedModel();
   }

   public static BakedQuad makeBakedQuad(BlockPart blockPart, BlockPartFace blockPartFace, TextureAtlasSprite textureAtlasSprite, EnumFacing enumFacing, ModelRotation modelRotation, boolean flag) {
      FaceBakery faceBakery = new FaceBakery();
      return faceBakery.makeBakedQuad(blockPart.positionFrom, blockPart.positionTo, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, blockPart.partRotation, flag, blockPart.shade);
   }

   @Override
   public String toString() {
      return "" + this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
   }

   public float getTextureWidth(TextureManager textureManager) {
      if (this.textureWidth <= 0) {
         if (this.textureLocation != null) {
            ITextureObject texture = textureManager.getTexture(this.textureLocation);
            int glTextureId = texture.getGlTextureId();
            int boundTexture = GlStateManager.getBoundTexture();
            GlStateManager.bindTexture(glTextureId);
            this.textureWidth = GL11.glGetTexLevelParameteri(3553, 0, 4096);
            GlStateManager.bindTexture(boundTexture);
         }

         if (this.textureWidth <= 0) {
            this.textureWidth = 16;
         }
      }

      return this.textureWidth;
   }

   public float getTextureHeight(TextureManager textureManager) {
      if (this.textureHeight <= 0) {
         if (this.textureLocation != null) {
            ITextureObject texture = textureManager.getTexture(this.textureLocation);
            int glTextureId = texture.getGlTextureId();
            int boundTexture = GlStateManager.getBoundTexture();
            GlStateManager.bindTexture(glTextureId);
            this.textureHeight = GL11.glGetTexLevelParameteri(3553, 0, 4097);
            GlStateManager.bindTexture(boundTexture);
         }

         if (this.textureHeight <= 0) {
            this.textureHeight = 16;
         }
      }

      return this.textureHeight;
   }

   public IBakedModel getBakedModel(ResourceLocation resourceLocation, boolean flag) {
      IBakedModel bakedModel;
      Map map;
      if (flag) {
         bakedModel = this.bakedModelFull;
         map = this.mapBakedModelsFull;
      } else {
         bakedModel = this.bakedModelTexture;
         map = this.mapBakedModelsTexture;
      }

      if (resourceLocation != null && map != null) {
         String resourcePath = resourceLocation.getResourcePath();
         IBakedModel bakedModel2 = (IBakedModel)map.get(resourcePath);
         if (bakedModel2 != null) {
            return bakedModel2;
         }
      }

      return bakedModel;
   }

   public void loadModels(ModelBakery modelBakery) {
      if (this.model != null) {
         loadItemModel(modelBakery, this.model);
      }

      if (this.type == 1 && this.mapModels != null) {
         for (String str : this.mapModels.keySet()) {
            String str2 = this.mapModels.get(str);
            String removePrefix = StrUtils.removePrefix(str, "model.");
            if (removePrefix.startsWith("bow") || removePrefix.startsWith("fishing_rod") || removePrefix.startsWith("shield")) {
               loadItemModel(modelBakery, str2);
            }
         }
      }
   }

   public void updateModelsFull() {
      ModelManager modelManager = Config.getModelManager();
      IBakedModel missingModel = modelManager.getMissingModel();
      if (this.model != null) {
         ResourceLocation modelLocation = getModelLocation(this.model);
         ModelResourceLocation modelResourceLocation = new ModelResourceLocation(modelLocation, "inventory");
         this.bakedModelFull = modelManager.getModel(modelResourceLocation);
         if (this.bakedModelFull == missingModel) {
            Config.warn("Custom Items: Model not found " + modelResourceLocation.getResourcePath());
            this.bakedModelFull = null;
         }
      }

      if (this.type == 1 && this.mapModels != null) {
         for (String str : this.mapModels.keySet()) {
            String str2 = this.mapModels.get(str);
            String removePrefix = StrUtils.removePrefix(str, "model.");
            if (removePrefix.startsWith("bow") || removePrefix.startsWith("fishing_rod") || removePrefix.startsWith("shield")) {
               ResourceLocation modelLocation = getModelLocation(str2);
               ModelResourceLocation modelResourceLocation = new ModelResourceLocation(modelLocation, "inventory");
               IBakedModel model = modelManager.getModel(modelResourceLocation);
               if (model == missingModel) {
                  Config.warn("Custom Items: Model not found " + modelResourceLocation.getResourcePath());
               } else {
                  if (this.mapBakedModelsFull == null) {
                     this.mapBakedModelsFull = new HashMap<>();
                  }

                  this.mapBakedModelsFull.put(removePrefix, model);
               }
            }
         }
      }
   }

   public static void loadItemModel(ModelBakery modelBakery, String str) {
      ResourceLocation modelLocation = getModelLocation(str);
      ModelResourceLocation modelResourceLocation = new ModelResourceLocation(modelLocation, "inventory");
      if (Reflector.ModelLoader.exists()) {
         try {
            Object value = Reflector.ModelLoader_VanillaLoader_INSTANCE.getValue();
            checkNull(value, "vanillaLoader is null");
            Object obj = Reflector.call(value, Reflector.ModelLoader_VanillaLoader_loadModel, modelResourceLocation);
            checkNull(obj, "iModel is null");
            Map fieldValue = (Map)Reflector.getFieldValue(modelBakery, Reflector.ModelLoader_stateModels);
            checkNull(fieldValue, "stateModels is null");
            fieldValue.put(modelResourceLocation, obj);
            Set fieldValue2 = (Set)Reflector.getFieldValue(modelBakery, Reflector.ModelLoader_textures);
            checkNull(fieldValue2, "registryTextures is null");
            Collection collection = (Collection)Reflector.call(obj, Reflector.IModel_getTextures);
            checkNull(collection, "modelTextures is null");
            fieldValue2.addAll(collection);
         } catch (Exception e) {
            Config.warn("Error registering model with ModelLoader: " + modelResourceLocation + ", " + e.getClass().getName() + ": " + e.getMessage());
         }
      } else {
         modelBakery.loadItemModel(modelLocation.toString(), modelResourceLocation, modelLocation);
      }
   }

   public static void checkNull(Object obj, String str) throws NullPointerException {
      if (obj == null) {
         throw new NullPointerException(str);
      }
   }

   public static ResourceLocation getModelLocation(String str) {
      return Reflector.ModelLoader.exists() && !str.startsWith("mcpatcher/") && !str.startsWith("optifine/")
         ? new ResourceLocation("models/" + str)
         : new ResourceLocation(str);
   }
}
