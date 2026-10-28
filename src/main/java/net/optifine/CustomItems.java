package net.optifine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class CustomItems {
    public static CustomItemProperties[][] itemProperties = (CustomItemProperties[][])null;
    public static Map mapPotionIds = null;

    public static void updateIcons(TextureMap textureMap) {
        itemProperties = (CustomItemProperties[][])null;
        if (Config.isCustomItems()) {
            IResourcePack[] resourcePacks = Config.getResourcePacks();

            for (int i = resourcePacks.length - 1; i >= 0; i--) {
                IResourcePack resourcePack = resourcePacks[i];
                updateIcons(textureMap, resourcePack);
            }

            updateIcons(textureMap, Config.getDefaultResourcePack());
        }
    }

    public static void updateIcons(TextureMap textureMap, IResourcePack resourcePack) {
        String[] collectFiles2 = collectFiles(resourcePack, "mcpatcher/cit/", ".properties");
        Map makeAutoImageProperties2 = makeAutoImageProperties(resourcePack);
        if (makeAutoImageProperties2.size() > 0) {
            Set keySet = makeAutoImageProperties2.keySet();
            String[] toArray = (String[])keySet.toArray(new String[keySet.size()]);
            collectFiles2 = (String[])Config.addObjectsToArray(collectFiles2, toArray);
        }

        Arrays.sort(collectFiles2);
        List makePropertyList2 = makePropertyList(itemProperties);

        for (int i = 0; i < collectFiles2.length; i++) {
            String str = collectFiles2[i];
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

                    Properties properties = new Properties();
                    properties.load(inputStream);
                    customItemProperties = new CustomItemProperties(properties, str);
                }

                if (customItemProperties.isValid(str)) {
                    customItemProperties.updateIcons(textureMap);
                    addToItemList(customItemProperties, makePropertyList2);
                }
            } catch (FileNotFoundException e) {
                Config.warn("CustomItems file not found: " + str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        itemProperties = propertyListToArray(makePropertyList2);
    }

    public static Map makeAutoImageProperties(IResourcePack resourcePack) {
        HashMap map = new HashMap();
        map.putAll(makePotionImageProperties(resourcePack, false));
        map.putAll(makePotionImageProperties(resourcePack, true));
        return map;
    }

    public static Map makePotionImageProperties(IResourcePack resourcePack, boolean flag) {
        HashMap map = new HashMap();
        String str = "mcpatcher/cit/potion/";
        if (flag) {
            str = str + "splash/";
        } else {
            str = str + "normal/";
        }

        String str2 = ".png";
        String[] collectFiles2 = collectFiles(resourcePack, str, str2);

        for (int i = 0; i < collectFiles2.length; i++) {
            String str3 = collectFiles2[i];
            if (str3.startsWith(str) && str3.endsWith(str2)) {
                String substring = str3.substring(str.length(), str3.length() - str2.length());
                Properties makePotionProperties2 = makePotionProperties(substring, flag, str3);
                if (makePotionProperties2 != null) {
                    String str4 = str3.substring(0, str3.length() - str2.length()) + ".properties";
                    CustomItemProperties customItemProperties = new CustomItemProperties(makePotionProperties2, str4);
                    map.put(str4, customItemProperties);
                }
            } else {
                Config.warn("Invalid potion name: " + str3);
            }
        }

        return map;
    }

    public static Properties makePotionProperties(String str, boolean flag, String str2) {
        if (str.equals("empty") && !flag) {
            int idFromItem = Item.getIdFromItem(Items.glass_bottle);
            Properties properties = new Properties();
            properties.put("type", "item");
            properties.put("items", "" + idFromItem);
            return properties;
        }

        int idFromItem = Item.getIdFromItem(Items.potionitem);
        int[] ints = (int[])getMapPotionIds().get(str);
        if (ints == null) {
            Config.warn("Potion not found for image: " + str2);
            return null;
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < ints.length; i++) {
            int n = ints[i];
            if (flag) {
                n |= 16384;
            }

            if (i > 0) {
                sb.append(" ");
            }

            sb.append(n);
        }

        short s = 16447;
        Properties properties = new Properties();
        properties.put("type", "item");
        properties.put("items", "" + idFromItem);
        properties.put("damage", "" + sb.toString());
        properties.put("damageMask", "" + s);
        return properties;
    }

    public static Map getMapPotionIds() {
        if (mapPotionIds == null) {
            mapPotionIds = new LinkedHashMap();
            mapPotionIds.put("water", new int[]{0});
            mapPotionIds.put("awkward", new int[]{16});
            mapPotionIds.put("thick", new int[]{32});
            mapPotionIds.put("potent", new int[]{48});
            mapPotionIds.put("regeneration", getPotionIds(1));
            mapPotionIds.put("moveSpeed", getPotionIds(2));
            mapPotionIds.put("fireResistance", getPotionIds(3));
            mapPotionIds.put("poison", getPotionIds(4));
            mapPotionIds.put("heal", getPotionIds(5));
            mapPotionIds.put("nightVision", getPotionIds(6));
            mapPotionIds.put("clear", getPotionIds(7));
            mapPotionIds.put("bungling", getPotionIds(23));
            mapPotionIds.put("charming", getPotionIds(39));
            mapPotionIds.put("rank", getPotionIds(55));
            mapPotionIds.put("weakness", getPotionIds(8));
            mapPotionIds.put("damageBoost", getPotionIds(9));
            mapPotionIds.put("moveSlowdown", getPotionIds(10));
            mapPotionIds.put("diffuse", getPotionIds(11));
            mapPotionIds.put("smooth", getPotionIds(27));
            mapPotionIds.put("refined", getPotionIds(43));
            mapPotionIds.put("acrid", getPotionIds(59));
            mapPotionIds.put("harm", getPotionIds(12));
            mapPotionIds.put("waterBreathing", getPotionIds(13));
            mapPotionIds.put("invisibility", getPotionIds(14));
            mapPotionIds.put("thin", getPotionIds(15));
            mapPotionIds.put("debonair", getPotionIds(31));
            mapPotionIds.put("sparkling", getPotionIds(47));
            mapPotionIds.put("stinky", getPotionIds(63));
        }

        return mapPotionIds;
    }

    public static int[] getPotionIds(int n) {
        return new int[]{n, n + 16, n + 32, n + 48};
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

    public static String[] collectFiles(IResourcePack resourcePack, String str, String str2) {
        if (resourcePack instanceof DefaultResourcePack) {
            return collectFilesDefault(resourcePack);
        }

        if (!(resourcePack instanceof AbstractResourcePack)) {
            return new String[0];
        }

        AbstractResourcePack abstractResourcePack = (AbstractResourcePack)resourcePack;
        File resourcePackFile = ResourceUtils.getResourcePackFile(abstractResourcePack);
        return resourcePackFile == null
            ? new String[0]
            : (resourcePackFile.isDirectory() ? collectFilesFolder(resourcePackFile, "", str, str2) : (resourcePackFile.isFile() ? collectFilesZIP(resourcePackFile, str, str2) : new String[0]));
    }

    public static String[] collectFilesDefault(IResourcePack resourcePack) {
        return new String[0];
    }

    public static String[] collectFilesFolder(File file, String str, String str2, String str3) {
        ArrayList list = new ArrayList();
        String str4 = "assets/minecraft/";
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return new String[0];
        }

        for (int i = 0; i < listFiles.length; i++) {
            File file2 = listFiles[i];
            if (file2.isFile()) {
                String str5 = str + file2.getName();
                if (str5.startsWith(str4)) {
                    str5 = str5.substring(str4.length());
                    if (str5.startsWith(str2) && str5.endsWith(str3)) {
                        list.add(str5);
                    }
                }
            } else if (file2.isDirectory()) {
                String str5 = str + file2.getName() + "/";
                String[] collectFilesFolder2 = collectFilesFolder(file2, str5, str2, str3);

                for (int j = 0; j < collectFilesFolder2.length; j++) {
                    String str6 = collectFilesFolder2[j];
                    list.add(str6);
                }
            }
        }

        return (String[])list.toArray(new String[list.size()]);
    }

    public static String[] collectFilesZIP(File file, String str, String str2) {
        ArrayList list = new ArrayList();
        String str3 = "assets/minecraft/";

        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry nextElement = (ZipEntry)entries.nextElement();
                String name = nextElement.getName();
                if (name.startsWith(str3)) {
                    name = name.substring(str3.length());
                    if (name.startsWith(str) && name.endsWith(str2)) {
                        list.add(name);
                    }
                }
            }

            zipFile.close();
            return (String[])list.toArray(new String[list.size()]);
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
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

    public static IIcon getCustomItemTexture(ItemStack itemStack, IIcon icon) {
        if (itemProperties == null) {
            return icon;
        }

        if (itemStack == null) {
            return icon;
        }

        Item item = itemStack.getItem();
        int idFromItem = Item.getIdFromItem(item);
        if (idFromItem >= 0 && idFromItem < itemProperties.length) {
            CustomItemProperties[] customItemPropertieses = itemProperties[idFromItem];
            if (customItemPropertieses != null) {
                for (int i = 0; i < customItemPropertieses.length; i++) {
                    CustomItemProperties customItemProperties = customItemPropertieses[i];
                    IIcon customItemTexture = getCustomItemTexture(customItemProperties, itemStack, icon);
                    if (customItemTexture != null) {
                        return customItemTexture;
                    }
                }
            }
        }

        return icon;
    }

    public static IIcon getCustomPotionTexture(ItemPotion itemPotion, int n) {
        if (itemProperties == null) {
            return null;
        }

        int idFromItem = Item.getIdFromItem(itemPotion);
        if (idFromItem >= 0 && idFromItem < itemProperties.length) {
            CustomItemProperties[] customItemPropertieses = itemProperties[idFromItem];
            if (customItemPropertieses != null) {
                for (int i = 0; i < customItemPropertieses.length; i++) {
                    CustomItemProperties customItemProperties = customItemPropertieses[i];
                    IIcon customPotionTexture = getCustomPotionTexture(customItemProperties, itemPotion, n);
                    if (customPotionTexture != null) {
                        return customPotionTexture;
                    }
                }
            }
        }

        return null;
    }

    public static IIcon getCustomPotionTexture(CustomItemProperties customItemProperties, ItemPotion itemPotion, int n) {
        if (customItemProperties.damage != null) {
            if (customItemProperties.damageMask != 0) {
                n &= customItemProperties.damageMask;
            }

            if (!customItemProperties.damage.isInRange(n)) {
                return null;
            }
        }

        return customItemProperties.textureIcon;
    }

    public static IIcon getCustomItemTexture(CustomItemProperties customItemProperties, ItemStack itemStack, IIcon icon) {
        Item item = itemStack.getItem();
        if (customItemProperties.damage != null) {
            int metadata = itemStack.getMetadata();
            if (customItemProperties.damageMask != 0) {
                metadata &= customItemProperties.damageMask;
            }

            int maxDurability = item.getMaxDurability();
            if (customItemProperties.damagePercent) {
                metadata = (int)((double)(metadata * 100) / maxDurability);
            }

            if (!customItemProperties.damage.isInRange(metadata)) {
                return null;
            }
        }

        if (customItemProperties.stackSize != null && !customItemProperties.stackSize.isInRange(itemStack.stackSize)) {
            return null;
        }

        if (customItemProperties.enchantmentIds != null) {
            int[] enchantmentIds = getEnchantmentIds(itemStack);
            boolean flag = false;

            for (int i = 0; i < enchantmentIds.length; i++) {
                int n = enchantmentIds[i];
                if (customItemProperties.enchantmentIds.isInRange(n)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return null;
            }
        }

        if (customItemProperties.enchantmentLevels != null) {
            int[] enchantmentLevels = getEnchantmentLevels(itemStack);
            boolean flag = false;

            for (int i = 0; i < enchantmentLevels.length; i++) {
                int n = enchantmentLevels[i];
                if (customItemProperties.enchantmentLevels.isInRange(n)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return null;
            }
        }

        if (customItemProperties.nbtTagValues != null) {
        }

        return customItemProperties.textureIcon;
    }

    public static int[] getEnchantmentIds(ItemStack itemStack) {
        Map enchantments = EnchantmentHelper.getEnchantments(itemStack);
        Set keySet = enchantments.keySet();
        int[] ints = new int[keySet.size()];
        int n = 0;

        for (Integer integer : (Iterable<Integer>)keySet) {
            ints[n] = integer;
            n++;
        }

        return ints;
    }

    public static int[] getEnchantmentLevels(ItemStack itemStack) {
        Map enchantments = EnchantmentHelper.getEnchantments(itemStack);
        Collection values = enchantments.values();
        int[] ints = new int[values.size()];
        int n = 0;

        for (Integer integer : (Iterable<Integer>)values) {
            ints[n] = integer;
            n++;
        }

        return ints;
    }

    public static ResourceLocation getLocationItemGlint(ItemStack itemStack, ResourceLocation resourceLocation) {
        return resourceLocation;
    }
}
