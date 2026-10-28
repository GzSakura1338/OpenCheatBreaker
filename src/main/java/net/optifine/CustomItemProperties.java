package net.optifine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class CustomItemProperties {
    public String name = null;
    public String basePath = null;
    public int type = 1;
    public int[] items = null;
    public String texture = null;
    public Map mapTextures = null;
    public RangeListInt damage = null;
    public boolean damagePercent = false;
    public int damageMask = 0;
    public RangeListInt stackSize = null;
    public RangeListInt enchantmentIds = null;
    public RangeListInt enchantmentLevels = null;
    public NbtTagValue[] nbtTagValues = null;
    public int blend = 1;
    public int speed = 0;
    public int rotation = 0;
    public int layer = 0;
    public int duration = 1;
    public int weight = 0;
    public IIcon textureIcon = null;
    public Map mapTextureIcons = null;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_ENCHANTMENT = 2;
    public static final int TYPE_ARMOR = 3;

    public CustomItemProperties(Properties properties, String str) {
        this.name = parseName(str);
        this.basePath = parseBasePath(str);
        this.type = this.parseType(properties.getProperty("type"));
        this.items = this.parseItems(properties.getProperty("items"), properties.getProperty("matchItems"));
        this.texture = parseTexture(properties.getProperty("texture"), properties.getProperty("tile"), properties.getProperty("source"), str, this.basePath);
        this.mapTextures = parseTextures(properties, this.basePath);
        String property = properties.getProperty("damage");
        if (property != null) {
            this.damagePercent = property.contains("%");
            property.replace("%", "");
            this.damage = this.parseRangeListInt(property);
            this.damageMask = this.parseInt(properties.getProperty("damageMask"), 0);
        }

        this.stackSize = this.parseRangeListInt(properties.getProperty("stackSize"));
        this.enchantmentIds = this.parseRangeListInt(properties.getProperty("enchantmentIDs"));
        this.enchantmentLevels = this.parseRangeListInt(properties.getProperty("enchantmentLevels"));
        this.nbtTagValues = this.parseNbtTagValues(properties);
        this.blend = Blender.parseBlend(properties.getProperty("blend"));
        this.speed = this.parseInt(properties.getProperty("speed"), 0);
        this.rotation = this.parseInt(properties.getProperty("rotation"), 0);
        this.layer = this.parseInt(properties.getProperty("layer"), 0);
        this.weight = this.parseInt(properties.getProperty("weight"), 0);
        this.duration = this.parseInt(properties.getProperty("duration"), 1);
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

                Object object = Item.itemRegistry.getObject(str3);
                if (!(object instanceof Item)) {
                    Config.dbg("Item not found: " + str3);
                } else {
                    Item item = (Item)object;
                    int idFromItem = Item.getIdFromItem(item);
                    if (idFromItem < 0) {
                        Config.dbg("Item not found: " + str3);
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

    public static String parseTexture(String str, String str2, String str3, String str4, String str5) {
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

        for (String str3 : (Iterable<String>)keySet) {
            String str4 = (String)matchingProperties.get(str3);
            str4 = fixTextureName(str4, str);
            if (str3.startsWith(str2)) {
                str3 = str3.substring(str2.length());
            }

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

        String str3 = "textures/blocks/";
        if (str.startsWith(str3)) {
            str = str.substring(str3.length());
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

    public RangeListInt parseRangeListInt(String str) {
        if (str == null) {
            return null;
        }

        String[] tokenize = Config.tokenize(str, " ");
        RangeListInt rangeListInt = new RangeListInt();

        for (int i = 0; i < tokenize.length; i++) {
            String str2 = tokenize[i];
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
                return new RangeInt(-1, n2);
            } else {
                return str.endsWith("-") ? new RangeInt(n2, -1) : new RangeInt(n2, n2);
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
        Map matchingProperties = getMatchingProperties(properties, "nbt.");
        if (matchingProperties.size() <= 0) {
            return null;
        }

        ArrayList list = new ArrayList();

        for (String str : (Iterable<String>)matchingProperties.keySet()) {
            String str2 = (String)matchingProperties.get(str);
            NbtTagValue nbtTagValue = new NbtTagValue(str, str2);
            list.add(nbtTagValue);
        }

        return (NbtTagValue[])list.toArray(new NbtTagValue[list.size()]);
    }

    public static Map getMatchingProperties(Properties properties, String str) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();

        for (String str2 : (Iterable<String>)(Iterable)properties.keySet()) {
            String property = properties.getProperty(str2);
            if (str2.startsWith(str)) {
                linkedHashMap.put(str2, property);
            }
        }

        return linkedHashMap;
    }

    public boolean isValid(String str) {
        if (this.name == null || this.name.length() <= 0) {
            Config.warn("No name found: " + str);
            return false;
        } else if (this.basePath == null) {
            Config.warn("No base path found: " + str);
            return false;
        } else if (this.type == 0) {
            Config.warn("No type defined: " + str);
            return false;
        } else if ((this.type == 1 || this.type == 3) && this.items == null) {
            Config.warn("No items defined: " + str);
            return false;
        } else if (this.texture == null && this.mapTextures == null) {
            Config.warn("No texture specified: " + str);
            return false;
        } else {
            return true;
        }
    }

    public void updateIcons(TextureMap textureMap) {
        if (this.texture != null) {
            this.textureIcon = registerIcon(this.texture, textureMap);
        }

        if (this.mapTextures != null) {
            this.mapTextureIcons = new LinkedHashMap();

            for (String str : (Iterable<String>)this.mapTextures.keySet()) {
                String str2 = (String)this.mapTextures.get(str);
                IIcon registerIcon2 = registerIcon(str2, textureMap);
                this.mapTextureIcons.put(str, registerIcon2);
            }
        }
    }

    public static IIcon registerIcon(String str, TextureMap textureMap) {
        if (str == null) {
            return null;
        }

        String str2 = str;
        if (!str.contains("/")) {
            str2 = "textures/blocks/" + str;
        }

        String str3 = str2 + ".png";
        ResourceLocation resourceLocation = new ResourceLocation(str3);
        boolean hasResource = Config.hasResource(resourceLocation);
        if (!hasResource) {
            Config.warn("File not found: " + str3);
        }

        return textureMap.registerIcon(str);
    }
}
