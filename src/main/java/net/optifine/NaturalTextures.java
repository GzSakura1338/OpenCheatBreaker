package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class NaturalTextures {
    public static NaturalProperties[] propertiesByIndex = new NaturalProperties[0];

    public static void update() {
        propertiesByIndex = new NaturalProperties[0];
        if (Config.isNaturalTextures()) {
            String str = "optifine/natural.properties";

            try {
                ResourceLocation resourceLocation = new ResourceLocation(str);
                if (!Config.hasResource(resourceLocation)) {
                    Config.dbg("NaturalTextures: configuration \"" + str + "\" not found");
                    propertiesByIndex = makeDefaultProperties();
                    return;
                }

                InputStream resourceStream = Config.getResourceStream(resourceLocation);
                ArrayList list = new ArrayList(256);
                String readInputStream = Config.readInputStream(resourceStream);
                resourceStream.close();
                String[] tokenize = Config.tokenize(readInputStream, "\n\r");
                Config.dbg("Natural Textures: Parsing configuration \"" + str + "\"");

                for (int i = 0; i < tokenize.length; i++) {
                    String trim = tokenize[i].trim();
                    if (!trim.startsWith("#")) {
                        String[] tokenize2 = Config.tokenize(trim, "=");
                        if (tokenize2.length != 2) {
                            Config.warn("Natural Textures: Invalid \"" + str + "\" line: " + trim);
                        } else {
                            String trim2 = tokenize2[0].trim();
                            String trim3 = tokenize2[1].trim();
                            TextureAtlasSprite iconSafe = TextureMap.textureMapBlocks.getIconSafe(trim2);
                            if (iconSafe == null) {
                                Config.warn("Natural Textures: Texture not found: \"" + str + "\" line: " + trim);
                            } else {
                                int indexInMap = iconSafe.getIndexInMap();
                                if (indexInMap < 0) {
                                    Config.warn("Natural Textures: Invalid \"" + str + "\" line: " + trim);
                                } else {
                                    NaturalProperties naturalProperties = new NaturalProperties(trim3);
                                    if (naturalProperties.isValid()) {
                                        while (list.size() <= indexInMap) {
                                            list.add(null);
                                        }

                                        list.set(indexInMap, naturalProperties);
                                        Config.dbg("NaturalTextures: " + trim2 + " = " + trim3);
                                    }
                                }
                            }
                        }
                    }
                }

                propertiesByIndex = (NaturalProperties[])list.toArray(new NaturalProperties[list.size()]);
            } catch (FileNotFoundException e) {
                Config.warn("NaturalTextures: configuration \"" + str + "\" not found");
                propertiesByIndex = makeDefaultProperties();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static NaturalProperties getNaturalProperties(IIcon icon) {
        if (!(icon instanceof TextureAtlasSprite)) {
            return null;
        }

        TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)icon;
        int indexInMap = textureAtlasSprite.getIndexInMap();
        return indexInMap >= 0 && indexInMap < propertiesByIndex.length ? propertiesByIndex[indexInMap] : null;
    }

    public static NaturalProperties[] makeDefaultProperties() {
        Config.dbg("NaturalTextures: Checking default configuration.");
        ArrayList list = new ArrayList();
        setIconProperties(list, "grass_top", "4F");
        setIconProperties(list, "stone", "2F");
        setIconProperties(list, "dirt", "4F");
        setIconProperties(list, "grass_side", "F");
        setIconProperties(list, "grass_side_overlay", "F");
        setIconProperties(list, "stone_slab_top", "F");
        setIconProperties(list, "bedrock", "2F");
        setIconProperties(list, "sand", "4F");
        setIconProperties(list, "gravel", "2");
        setIconProperties(list, "log_oak", "2F");
        setIconProperties(list, "log_oak_top", "4F");
        setIconProperties(list, "gold_ore", "2F");
        setIconProperties(list, "iron_ore", "2F");
        setIconProperties(list, "coal_ore", "2F");
        setIconProperties(list, "diamond_ore", "2F");
        setIconProperties(list, "redstone_ore", "2F");
        setIconProperties(list, "lapis_ore", "2F");
        setIconProperties(list, "obsidian", "4F");
        setIconProperties(list, "leaves_oak", "2F");
        setIconProperties(list, "leaves_oak_opaque", "2F");
        setIconProperties(list, "leaves_jungle", "2");
        setIconProperties(list, "leaves_jungle_opaque", "2");
        setIconProperties(list, "snow", "4F");
        setIconProperties(list, "grass_side_snowed", "F");
        setIconProperties(list, "cactus_side", "2F");
        setIconProperties(list, "clay", "4F");
        setIconProperties(list, "mycelium_side", "F");
        setIconProperties(list, "mycelium_top", "4F");
        setIconProperties(list, "farmland_wet", "2F");
        setIconProperties(list, "farmland_dry", "2F");
        setIconProperties(list, "netherrack", "4F");
        setIconProperties(list, "soul_sand", "4F");
        setIconProperties(list, "glowstone", "4");
        setIconProperties(list, "log_spruce", "2F");
        setIconProperties(list, "log_birch", "F");
        setIconProperties(list, "leaves_spruce", "2F");
        setIconProperties(list, "leaves_spruce_opaque", "2F");
        setIconProperties(list, "log_jungle", "2F");
        setIconProperties(list, "end_stone", "4");
        setIconProperties(list, "sandstone_top", "4");
        setIconProperties(list, "sandstone_bottom", "4F");
        setIconProperties(list, "redstone_lamp_on", "4F");
        return (NaturalProperties[])list.toArray(new NaturalProperties[list.size()]);
    }

    public static void setIconProperties(List list, String str, String str2) {
        TextureMap textureMapBlocks = TextureMap.textureMapBlocks;
        TextureAtlasSprite iconSafe = textureMapBlocks.getIconSafe(str);
        if (iconSafe == null) {
            Config.warn("*** NaturalProperties: Icon not found: " + str + " ***");
        } else if (!(iconSafe instanceof TextureAtlasSprite)) {
            Config.warn("*** NaturalProperties: Icon is not IconStitched: " + str + ": " + iconSafe.getClass().getName() + " ***");
        } else {
            TextureAtlasSprite textureAtlasSprite = iconSafe;
            int indexInMap = textureAtlasSprite.getIndexInMap();
            if (indexInMap < 0) {
                Config.warn("*** NaturalProperties: Invalid index for icon: " + str + ": " + indexInMap + " ***");
            } else if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/" + str + ".png"))) {
                while (indexInMap >= list.size()) {
                    list.add(null);
                }

                NaturalProperties naturalProperties = new NaturalProperties(str2);
                list.set(indexInMap, naturalProperties);
                Config.dbg("NaturalTextures: " + str + " = " + str2);
            }
        }
    }
}
