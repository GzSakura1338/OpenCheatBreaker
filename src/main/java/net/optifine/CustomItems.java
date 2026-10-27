package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.NbtTagValue;
import net.optifine.render.Blender;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

public class CustomItems {
   public static CustomItemProperties[][] itemProperties = (CustomItemProperties[][])null;
   public static CustomItemProperties[][] enchantmentProperties = (CustomItemProperties[][])null;
   public static Map mapPotionIds = null;
   public static ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
   public static boolean useGlint = true;
   public static boolean renderOffHand = false;
   public static final int MASK_POTION_SPLASH = 16384;
   public static final int MASK_POTION_NAME = 63;
   public static final int MASK_POTION_EXTENDED = 64;
   public static final String KEY_TEXTURE_OVERLAY = "texture.potion_overlay";
   public static final String KEY_TEXTURE_SPLASH = "texture.potion_bottle_splash";
   public static final String KEY_TEXTURE_DRINKABLE = "texture.potion_bottle_drinkable";
   public static final String DEFAULT_TEXTURE_OVERLAY = "items/potion_overlay";
   public static final String DEFAULT_TEXTURE_SPLASH = "items/potion_bottle_splash";
   public static final String DEFAULT_TEXTURE_DRINKABLE = "items/potion_bottle_drinkable";
   public static int[][] EMPTY_INT2_ARRAY = new int[0][];
   public static final String TYPE_POTION_NORMAL = "normal";
   public static final String TYPE_POTION_SPLASH = "splash";
   public static final String TYPE_POTION_LINGER = "linger";

   public static void update() {
      itemProperties = (CustomItemProperties[][])null;
      enchantmentProperties = (CustomItemProperties[][])null;
      useGlint = true;
      if (Config.isCustomItems()) {
         readCitProperties("mcpatcher/cit.properties");
         IResourcePack[] resourcePacks = Config.getResourcePacks();

         for (int i = resourcePacks.length - 1; i >= 0; i--) {
            IResourcePack resourcePack = resourcePacks[i];
            update(resourcePack);
         }

         update(Config.getDefaultResourcePack());
         if (itemProperties.length <= 0) {
            itemProperties = (CustomItemProperties[][])null;
         }

         if (enchantmentProperties.length <= 0) {
            enchantmentProperties = (CustomItemProperties[][])null;
         }
      }
   }

   public static void readCitProperties(String str) {
      try {
         ResourceLocation resourceLocation = new ResourceLocation(str);
         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return;
         }

         Config.dbg("CustomItems: Loading " + str);
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(resourceStream);
         resourceStream.close();
         useGlint = Config.parseBoolean(propertiesOrdered.getProperty("useGlint"), true);
      } catch (FileNotFoundException e) {
         return;
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void update(IResourcePack resourcePack) {
      String[] collectFiles = ResUtils.collectFiles(resourcePack, "mcpatcher/cit/", ".properties", (String[])null);
      Map makeAutoImageProperties2 = makeAutoImageProperties(resourcePack);
      if (makeAutoImageProperties2.size() > 0) {
         Set keySet = makeAutoImageProperties2.keySet();
         String[] toArray = (String[])keySet.toArray(new String[keySet.size()]);
         collectFiles = (String[])Config.addObjectsToArray(collectFiles, toArray);
      }

      Arrays.sort(collectFiles);
      List makePropertyList2 = makePropertyList(itemProperties);
      List makePropertyList3 = makePropertyList(enchantmentProperties);

      for (int i = 0; i < collectFiles.length; i++) {
         String str = collectFiles[i];
         Config.dbg("CustomItems: " + str);

         try {
            CustomItemProperties customItemProperties = null;
            if (makeAutoImageProperties2.containsKey(str)) {
               customItemProperties = (CustomItemProperties)makeAutoImageProperties2.get(str);
            }

            if (customItemProperties == null) {
               ResourceLocation resourceLocation = new ResourceLocation(str);
               InputStream inputStream = resourcePack.getInputStream(resourceLocation);
               if (inputStream == null) {
                  Config.warn("CustomItems file not found: " + str);
                  continue;
               }

               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(inputStream);
               inputStream.close();
               customItemProperties = new CustomItemProperties(propertiesOrdered, str);
            }

            if (customItemProperties.isValid(str)) {
               addToItemList(customItemProperties, makePropertyList2);
               addToEnchantmentList(customItemProperties, makePropertyList3);
            }
         } catch (FileNotFoundException e) {
            Config.warn("CustomItems file not found: " + str);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      itemProperties = propertyListToArray(makePropertyList2);
      enchantmentProperties = propertyListToArray(makePropertyList3);
      Comparator propertiesComparator = getPropertiesComparator();

      for (int i = 0; i < itemProperties.length; i++) {
         CustomItemProperties[] customItemPropertieses = itemProperties[i];
         if (customItemPropertieses != null) {
            Arrays.sort(customItemPropertieses, propertiesComparator);
         }
      }

      for (int i = 0; i < enchantmentProperties.length; i++) {
         CustomItemProperties[] customItemPropertieses = enchantmentProperties[i];
         if (customItemPropertieses != null) {
            Arrays.sort(customItemPropertieses, propertiesComparator);
         }
      }
   }

   public static Comparator getPropertiesComparator() {
      return new Comparator() {
         @Override
         public int compare(Object obj, Object obj2) {
            CustomItemProperties customItemProperties = (CustomItemProperties)obj;
            CustomItemProperties customItemProperties2 = (CustomItemProperties)obj2;
            return customItemProperties.layer != customItemProperties2.layer
               ? customItemProperties.layer - customItemProperties2.layer
               : (
                  customItemProperties.weight != customItemProperties2.weight
                     ? customItemProperties2.weight - customItemProperties.weight
                     : (!customItemProperties.basePath.equals(customItemProperties2.basePath) ? customItemProperties.basePath.compareTo(customItemProperties2.basePath) : customItemProperties.name.compareTo(customItemProperties2.name))
               );
         }
      };
   }

   public static void updateIcons(TextureMap textureMap) {
      for (CustomItemProperties customItemProperties : getAllProperties()) {
         customItemProperties.updateIcons(textureMap);
      }
   }

   public static void loadModels(ModelBakery modelBakery) {
      for (CustomItemProperties customItemProperties : getAllProperties()) {
         customItemProperties.loadModels(modelBakery);
      }
   }

   public static void updateModels() {
      for (CustomItemProperties customItemProperties : getAllProperties()) {
         if (customItemProperties.type == 1) {
            TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
            customItemProperties.updateModelTexture(textureMapBlocks, itemModelGenerator);
            customItemProperties.updateModelsFull();
         }
      }
   }

   public static List<CustomItemProperties> getAllProperties() {
      ArrayList list = new ArrayList();
      addAll(itemProperties, list);
      addAll(enchantmentProperties, list);
      return list;
   }

   public static void addAll(CustomItemProperties[][] customItemPropertieseses, List<CustomItemProperties> list) {
      if (customItemPropertieseses != null) {
         for (int i = 0; i < customItemPropertieseses.length; i++) {
            CustomItemProperties[] customItemPropertieses = customItemPropertieseses[i];
            if (customItemPropertieses != null) {
               for (int j = 0; j < customItemPropertieses.length; j++) {
                  CustomItemProperties customItemProperties = customItemPropertieses[j];
                  if (customItemProperties != null) {
                     list.add(customItemProperties);
                  }
               }
            }
         }
      }
   }

   public static Map makeAutoImageProperties(IResourcePack resourcePack) {
      HashMap map = new HashMap();
      map.putAll(makePotionImageProperties(resourcePack, "normal", Item.getIdFromItem(Items.potionitem)));
      map.putAll(makePotionImageProperties(resourcePack, "splash", Item.getIdFromItem(Items.potionitem)));
      map.putAll(makePotionImageProperties(resourcePack, "linger", Item.getIdFromItem(Items.potionitem)));
      return map;
   }

   public static Map makePotionImageProperties(IResourcePack resourcePack, String str, int n) {
      HashMap map = new HashMap();
      String str2 = str + "/";
      String[] strs = new String[]{"mcpatcher/cit/potion/" + str2, "mcpatcher/cit/Potion/" + str2};
      String[] strs2 = new String[]{".png"};
      String[] collectFiles = ResUtils.collectFiles(resourcePack, strs, strs2);

      for (int i = 0; i < collectFiles.length; i++) {
         String str3 = collectFiles[i];
         String removePrefixSuffix = StrUtils.removePrefixSuffix(str3, strs, strs2);
         Properties makePotionProperties2 = makePotionProperties(removePrefixSuffix, str, n, str3);
         if (makePotionProperties2 != null) {
            String str4 = StrUtils.removeSuffix(str3, strs2) + ".properties";
            CustomItemProperties customItemProperties = new CustomItemProperties(makePotionProperties2, str4);
            map.put(str4, customItemProperties);
         }
      }

      return map;
   }

   public static Properties makePotionProperties(String str, String str2, int n, String str3) {
      if (StrUtils.endsWith(str, new String[]{"_n", "_s"})) {
         return null;
      }

      if (str.equals("empty") && str2.equals("normal")) {
         n = Item.getIdFromItem(Items.glass_bottle);
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.put("type", "item");
         propertiesOrdered.put("items", "" + n);
         return propertiesOrdered;
      }

      int[] ints = (int[])getMapPotionIds().get(str);
      if (ints == null) {
         Config.warn("Potion not found for image: " + str3);
         return null;
      }

      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < ints.length; i++) {
         int n2 = ints[i];
         if (str2.equals("splash")) {
            n2 |= 16384;
         }

         if (i > 0) {
            sb.append(" ");
         }

         sb.append(n2);
      }

      int n2 = 16447;
      if (str.equals("water") || str.equals("mundane")) {
         n2 |= 64;
      }

      PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
      propertiesOrdered.put("type", "item");
      propertiesOrdered.put("items", "" + n);
      propertiesOrdered.put("damage", "" + sb.toString());
      propertiesOrdered.put("damageMask", "" + n2);
      if (str2.equals("splash")) {
         propertiesOrdered.put("texture.potion_bottle_splash", str);
      } else {
         propertiesOrdered.put("texture.potion_bottle_drinkable", str);
      }

      return propertiesOrdered;
   }

   public static Map getMapPotionIds() {
      if (mapPotionIds == null) {
         mapPotionIds = new LinkedHashMap();
         mapPotionIds.put("water", getPotionId(0, 0));
         mapPotionIds.put("awkward", getPotionId(0, 1));
         mapPotionIds.put("thick", getPotionId(0, 2));
         mapPotionIds.put("potent", getPotionId(0, 3));
         mapPotionIds.put("regeneration", getPotionIds(1));
         mapPotionIds.put("movespeed", getPotionIds(2));
         mapPotionIds.put("fireresistance", getPotionIds(3));
         mapPotionIds.put("poison", getPotionIds(4));
         mapPotionIds.put("heal", getPotionIds(5));
         mapPotionIds.put("nightvision", getPotionIds(6));
         mapPotionIds.put("clear", getPotionId(7, 0));
         mapPotionIds.put("bungling", getPotionId(7, 1));
         mapPotionIds.put("charming", getPotionId(7, 2));
         mapPotionIds.put("rank", getPotionId(7, 3));
         mapPotionIds.put("weakness", getPotionIds(8));
         mapPotionIds.put("damageboost", getPotionIds(9));
         mapPotionIds.put("moveslowdown", getPotionIds(10));
         mapPotionIds.put("leaping", getPotionIds(11));
         mapPotionIds.put("harm", getPotionIds(12));
         mapPotionIds.put("waterbreathing", getPotionIds(13));
         mapPotionIds.put("invisibility", getPotionIds(14));
         mapPotionIds.put("thin", getPotionId(15, 0));
         mapPotionIds.put("debonair", getPotionId(15, 1));
         mapPotionIds.put("sparkling", getPotionId(15, 2));
         mapPotionIds.put("stinky", getPotionId(15, 3));
         mapPotionIds.put("mundane", getPotionId(0, 4));
         mapPotionIds.put("speed", mapPotionIds.get("movespeed"));
         mapPotionIds.put("fire_resistance", mapPotionIds.get("fireresistance"));
         mapPotionIds.put("instant_health", mapPotionIds.get("heal"));
         mapPotionIds.put("night_vision", mapPotionIds.get("nightvision"));
         mapPotionIds.put("strength", mapPotionIds.get("damageboost"));
         mapPotionIds.put("slowness", mapPotionIds.get("moveslowdown"));
         mapPotionIds.put("instant_damage", mapPotionIds.get("harm"));
         mapPotionIds.put("water_breathing", mapPotionIds.get("waterbreathing"));
      }

      return mapPotionIds;
   }

   public static int[] getPotionIds(int n) {
      return new int[]{n, n + 16, n + 32, n + 48};
   }

   public static int[] getPotionId(int n, int n2) {
      return new int[]{n + n2 * 16};
   }

   public static int getPotionNameDamage(String str) {
      String str2 = "potion." + str;
      Potion[] potionTypes = Potion.potionTypes;

      for (int i = 0; i < potionTypes.length; i++) {
         Potion potion = potionTypes[i];
         if (potion != null) {
            String name = potion.getName();
            if (str2.equals(name)) {
               return potion.getId();
            }
         }
      }

      return -1;
   }

   public static List makePropertyList(CustomItemProperties[][] customItemPropertieseses) {
      ArrayList list = new ArrayList();
      if (customItemPropertieseses != null) {
         for (int i = 0; i < customItemPropertieseses.length; i++) {
            CustomItemProperties[] customItemPropertieses = customItemPropertieseses[i];
            ArrayList list2 = null;
            if (customItemPropertieses != null) {
               list2 = new ArrayList<>(Arrays.asList(customItemPropertieses));
            }

            list.add(list2);
         }
      }

      return list;
   }

   public static CustomItemProperties[][] propertyListToArray(List list) {
      CustomItemProperties[][] customItemPropertieseses = new CustomItemProperties[list.size()][];

      for (int i = 0; i < list.size(); i++) {
         List list2 = (List)list.get(i);
         if (list2 != null) {
            CustomItemProperties[] toArray = (CustomItemProperties[])list2.toArray(new CustomItemProperties[list2.size()]);
            Arrays.sort(toArray, new CustomItemsComparator());
            customItemPropertieseses[i] = toArray;
         }
      }

      return customItemPropertieseses;
   }

   public static void addToItemList(CustomItemProperties customItemProperties, List list) {
      if (customItemProperties.items != null) {
         for (int i = 0; i < customItemProperties.items.length; i++) {
            int n = customItemProperties.items[i];
            if (n <= 0) {
               Config.warn("Invalid item ID: " + n);
            } else {
               addToList(customItemProperties, list, n);
            }
         }
      }
   }

   public static void addToEnchantmentList(CustomItemProperties customItemProperties, List list) {
      if (customItemProperties.type == 2 && customItemProperties.enchantmentIds != null) {
         for (int i = 0; i < 256; i++) {
            if (customItemProperties.enchantmentIds.isInRange(i)) {
               addToList(customItemProperties, list, i);
            }
         }
      }
   }

   public static void addToList(CustomItemProperties customItemProperties, List list, int n) {
      while (n >= list.size()) {
         list.add(null);
      }

      List list2 = (List)list.get(n);
      if (list2 == null) {
         list2 = new ArrayList();
         list.set(n, list2);
      }

      list2.add(customItemProperties);
   }

   public static IBakedModel getCustomItemModel(ItemStack itemStack, IBakedModel bakedModel, ResourceLocation resourceLocation, boolean flag) {
      if (!flag && bakedModel.isGui3d()) {
         return bakedModel;
      }

      if (itemProperties == null) {
         return bakedModel;
      }

      CustomItemProperties customItemProperties = getCustomItemProperties(itemStack, 1);
      if (customItemProperties == null) {
         return bakedModel;
      }

      IBakedModel bakedModel2 = customItemProperties.getBakedModel(resourceLocation, flag);
      return bakedModel2 != null ? bakedModel2 : bakedModel;
   }

   public static boolean bindCustomArmorTexture(ItemStack itemStack, int n, String str) {
      if (itemProperties == null) {
         return false;
      }

      ResourceLocation customArmorLocation = getCustomArmorLocation(itemStack, n, str);
      if (customArmorLocation == null) {
         return false;
      }

      Config.getTextureManager().bindTexture(customArmorLocation);
      return true;
   }

   public static ResourceLocation getCustomArmorLocation(ItemStack itemStack, int n, String str) {
      CustomItemProperties customItemProperties = getCustomItemProperties(itemStack, 3);
      if (customItemProperties == null) {
         return null;
      }

      if (customItemProperties.mapTextureLocations == null) {
         return customItemProperties.textureLocation;
      }

      Item item = itemStack.getItem();
      if (!(item instanceof ItemArmor)) {
         return null;
      }

      ItemArmor itemArmor = (ItemArmor)item;
      String name = itemArmor.getArmorMaterial().getName();
      StringBuffer sb = new StringBuffer();
      sb.append("texture.");
      sb.append(name);
      sb.append("_layer_");
      sb.append(n);
      if (str != null) {
         sb.append("_");
         sb.append(str);
      }

      String str2 = sb.toString();
      ResourceLocation resourceLocation = (ResourceLocation)customItemProperties.mapTextureLocations.get(str2);
      return resourceLocation == null ? customItemProperties.textureLocation : resourceLocation;
   }

   public static CustomItemProperties getCustomItemProperties(ItemStack itemStack, int n) {
      if (itemProperties == null) {
         return null;
      }

      if (itemStack == null) {
         return null;
      }

      Item item = itemStack.getItem();
      int idFromItem = Item.getIdFromItem(item);
      if (idFromItem >= 0 && idFromItem < itemProperties.length) {
         CustomItemProperties[] customItemPropertieses = itemProperties[idFromItem];
         if (customItemPropertieses != null) {
            for (int i = 0; i < customItemPropertieses.length; i++) {
               CustomItemProperties customItemProperties = customItemPropertieses[i];
               if (customItemProperties.type == n && matchesProperties(customItemProperties, itemStack, (int[][])null)) {
                  return customItemProperties;
               }
            }
         }
      }

      return null;
   }

   public static boolean matchesProperties(CustomItemProperties customItemProperties, ItemStack itemStack, int[][] intses) {
      Item item = itemStack.getItem();
      if (customItemProperties.damage != null) {
         int itemDamage = itemStack.getItemDamage();
         if (customItemProperties.damageMask != 0) {
            itemDamage &= customItemProperties.damageMask;
         }

         if (customItemProperties.damagePercent) {
            int maxDamage = item.getMaxDamage();
            itemDamage = (int)((double)(itemDamage * 100) / maxDamage);
         }

         if (!customItemProperties.damage.isInRange(itemDamage)) {
            return false;
         }
      }

      if (customItemProperties.stackSize != null && !customItemProperties.stackSize.isInRange(itemStack.stackSize)) {
         return false;
      }

      int[][] intses2 = intses;
      if (customItemProperties.enchantmentIds != null) {
         if (intses == null) {
            intses2 = getEnchantmentIdLevels(itemStack);
         }

         boolean flag = false;

         for (int i = 0; i < intses2.length; i++) {
            int n = intses2[i][0];
            if (customItemProperties.enchantmentIds.isInRange(n)) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return false;
         }
      }

      if (customItemProperties.enchantmentLevels != null) {
         if (intses2 == null) {
            intses2 = getEnchantmentIdLevels(itemStack);
         }

         boolean flag = false;

         for (int i = 0; i < intses2.length; i++) {
            int n = intses2[i][1];
            if (customItemProperties.enchantmentLevels.isInRange(n)) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return false;
         }
      }

      if (customItemProperties.nbtTagValues != null) {
         NBTTagCompound tagCompound = itemStack.getTagCompound();

         for (int i = 0; i < customItemProperties.nbtTagValues.length; i++) {
            NbtTagValue nbtTagValue = customItemProperties.nbtTagValues[i];
            if (!nbtTagValue.matches(tagCompound)) {
               return false;
            }
         }
      }

      if (customItemProperties.hand != 0) {
         if (customItemProperties.hand == 1 && renderOffHand) {
            return false;
         }

         if (customItemProperties.hand == 2 && !renderOffHand) {
            return false;
         }
      }

      return true;
   }

   public static int[][] getEnchantmentIdLevels(ItemStack itemStack) {
      Item item = itemStack.getItem();
      NBTTagList nbttagList = item == Items.enchanted_book ? Items.enchanted_book.getEnchantments(itemStack) : itemStack.getEnchantmentTagList();
      if (nbttagList != null && nbttagList.tagCount() > 0) {
         int[][] intses = new int[nbttagList.tagCount()][2];

         for (int i = 0; i < nbttagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = nbttagList.getCompoundTagAt(i);
            short short2 = compoundTagAt.getShort("id");
            short short3 = compoundTagAt.getShort("lvl");
            intses[i][0] = short2;
            intses[i][1] = short3;
         }

         return intses;
      } else {
         return EMPTY_INT2_ARRAY;
      }
   }

   public static boolean renderCustomEffect(RenderItem renderItem, ItemStack itemStack, IBakedModel bakedModel) {
      if (enchantmentProperties == null) {
         return false;
      }

      if (itemStack == null) {
         return false;
      }

      int[][] enchantmentIdLevels = getEnchantmentIdLevels(itemStack);
      if (enchantmentIdLevels.length <= 0) {
         return false;
      }

      HashSet set = null;
      boolean flag = false;
      TextureManager textureManager = Config.getTextureManager();

      for (int i = 0; i < enchantmentIdLevels.length; i++) {
         int n = enchantmentIdLevels[i][0];
         if (n >= 0 && n < enchantmentProperties.length) {
            CustomItemProperties[] customItemPropertieses = enchantmentProperties[n];
            if (customItemPropertieses != null) {
               for (int j = 0; j < customItemPropertieses.length; j++) {
                  CustomItemProperties customItemProperties = customItemPropertieses[j];
                  if (set == null) {
                     set = new HashSet();
                  }

                  if (set.add(n) && matchesProperties(customItemProperties, itemStack, enchantmentIdLevels) && customItemProperties.textureLocation != null) {
                     textureManager.bindTexture(customItemProperties.textureLocation);
                     float textureWidth = customItemProperties.getTextureWidth(textureManager);
                     if (!flag) {
                        flag = true;
                        GlStateManager.depthMask(false);
                        GlStateManager.depthFunc(514);
                        GlStateManager.disableLighting();
                        GlStateManager.matrixMode(5890);
                     }

                     Blender.setupBlend(customItemProperties.blend, 1.0F);
                     GlStateManager.pushMatrix();
                     GlStateManager.scale(textureWidth / 2.0F, textureWidth / 2.0F, textureWidth / 2.0F);
                     float f = customItemProperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                     GlStateManager.translate(f, 0.0F, 0.0F);
                     GlStateManager.rotate(customItemProperties.rotation, 0.0F, 0.0F, 1.0F);
                     renderItem.renderModel(bakedModel, -1);
                     GlStateManager.popMatrix();
                  }
               }
            }
         }
      }

      if (flag) {
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         textureManager.bindTexture(TextureMap.locationBlocksTexture);
      }

      return flag;
   }

   public static boolean renderCustomArmorEffect(
      EntityLivingBase entityLivingBase, ItemStack itemStack, ModelBase modelBase, float f, float f2, float f3, float f4, float f5, float f6, float f7
   ) {
      if (enchantmentProperties == null) {
         return false;
      }

      if (Config.isShaders() && Shaders.isShadowPass) {
         return false;
      }

      if (itemStack == null) {
         return false;
      }

      int[][] enchantmentIdLevels = getEnchantmentIdLevels(itemStack);
      if (enchantmentIdLevels.length <= 0) {
         return false;
      }

      HashSet set = null;
      boolean flag = false;
      TextureManager textureManager = Config.getTextureManager();

      for (int i = 0; i < enchantmentIdLevels.length; i++) {
         int n = enchantmentIdLevels[i][0];
         if (n >= 0 && n < enchantmentProperties.length) {
            CustomItemProperties[] customItemPropertieses = enchantmentProperties[n];
            if (customItemPropertieses != null) {
               for (int j = 0; j < customItemPropertieses.length; j++) {
                  CustomItemProperties customItemProperties = customItemPropertieses[j];
                  if (set == null) {
                     set = new HashSet();
                  }

                  if (set.add(n) && matchesProperties(customItemProperties, itemStack, enchantmentIdLevels) && customItemProperties.textureLocation != null) {
                     textureManager.bindTexture(customItemProperties.textureLocation);
                     float textureWidth = customItemProperties.getTextureWidth(textureManager);
                     if (!flag) {
                        flag = true;
                        if (Config.isShaders()) {
                           ShadersRender.renderEnchantedGlintBegin();
                        }

                        GlStateManager.enableBlend();
                        GlStateManager.depthFunc(514);
                        GlStateManager.depthMask(false);
                     }

                     Blender.setupBlend(customItemProperties.blend, 1.0F);
                     GlStateManager.disableLighting();
                     GlStateManager.matrixMode(5890);
                     GlStateManager.loadIdentity();
                     GlStateManager.rotate(customItemProperties.rotation, 0.0F, 0.0F, 1.0F);
                     float f8 = textureWidth / 8.0F;
                     GlStateManager.scale(f8, f8 / 2.0F, f8);
                     float f9 = customItemProperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                     GlStateManager.translate(0.0F, f9, 0.0F);
                     GlStateManager.matrixMode(5888);
                     modelBase.render(entityLivingBase, f, f2, f4, f5, f6, f7);
                  }
               }
            }
         }
      }

      if (flag) {
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.depthMask(true);
         GlStateManager.depthFunc(515);
         GlStateManager.disableBlend();
         if (Config.isShaders()) {
            ShadersRender.renderEnchantedGlintEnd();
         }
      }

      return flag;
   }

   public static boolean isUseGlint() {
      return useGlint;
   }
}
