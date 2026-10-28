package shadersmod.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.ConnectedParser;
import net.optifine.MatchBlock;
import net.optifine.PropertiesOrdered;
import net.optifine.Reflector;
import net.optifine.ReflectorForge;
import net.optifine.StrUtils;

public class BlockAliases {
    public static BlockAlias[][] blockAliases = (BlockAlias[][])null;
    public static boolean updateOnResourcesReloaded;

    public static int getMappedBlockId(int n, int n2) {
        if (blockAliases == null) {
            return n;
        }

        if (n >= 0 && n < blockAliases.length) {
            BlockAlias[] blockAliases2 = blockAliases[n];
            if (blockAliases2 == null) {
                return n;
            }

            for (int i = 0; i < blockAliases2.length; i++) {
                BlockAlias blockAlias = blockAliases2[i];
                if (blockAlias.matches(n, n2)) {
                    return blockAlias.getBlockId();
                }
            }

            return n;
        } else {
            return n;
        }
    }

    public static void resourcesReloaded() {
        if (updateOnResourcesReloaded) {
            updateOnResourcesReloaded = false;
            update(Shaders.shaderPack);
        }
    }

    public static void update(IShaderPack shaderPack) {
        reset();
        if (shaderPack != null) {
            if (Reflector.Loader_getActiveModList.exists() && Minecraft.getMinecraft().getResourcePackRepository() == null) {
                Config.dbg("[Shaders] Delayed loading of block mappings after resources are loaded");
                updateOnResourcesReloaded = true;
            } else {
                ArrayList list = new ArrayList();
                String str = "/shaders/block.properties";
                InputStream resourceAsStream = shaderPack.getResourceAsStream(str);
                if (resourceAsStream != null) {
                    loadBlockAliases(resourceAsStream, str, list);
                }

                loadModBlockAliases(list);
                if (list.size() > 0) {
                    blockAliases = toArrays(list);
                }
            }
        }
    }

    public static void loadModBlockAliases(List<List<BlockAlias>> list) {
        String[] forgeModIds = ReflectorForge.getForgeModIds();

        for (int i = 0; i < forgeModIds.length; i++) {
            String str = forgeModIds[i];

            try {
                ResourceLocation resourceLocation = new ResourceLocation(str, "shaders/block.properties");
                InputStream resourceStream = Config.getResourceStream(resourceLocation);
                loadBlockAliases(resourceStream, resourceLocation.toString(), list);
            } catch (IOException e) {
            }
        }
    }

    public static void loadBlockAliases(InputStream inputStream, String str, List<List<BlockAlias>> list) {
        if (inputStream != null) {
            try {
                PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
                propertiesOrdered.load(inputStream);
                inputStream.close();
                Config.dbg("[Shaders] Parsing block mappings: " + str);
                ConnectedParser connectedParser = new ConnectedParser("Shaders");

                for (String str2 : (Iterable<String>)(Iterable)propertiesOrdered.keySet()) {
                    String property = propertiesOrdered.getProperty(str2);
                    String str3 = "block.";
                    if (!str2.startsWith(str3)) {
                        Config.warn("[Shaders] Invalid block ID: " + str2);
                    } else {
                        String removePrefix = StrUtils.removePrefix(str2, str3);
                        int parseInt = Config.parseInt(removePrefix, -1);
                        if (parseInt < 0) {
                            Config.warn("[Shaders] Invalid block ID: " + str2);
                        } else {
                            MatchBlock[] parseMatchBlocks = connectedParser.parseMatchBlocks(property);
                            if (parseMatchBlocks != null && parseMatchBlocks.length >= 1) {
                                BlockAlias blockAlias = new BlockAlias(parseInt, parseMatchBlocks);
                                addToList(list, blockAlias);
                            } else {
                                Config.warn("[Shaders] Invalid block ID mapping: " + str2 + "=" + property);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Config.warn("[Shaders] Error reading: " + str);
            }
        }
    }

    public static void addToList(List<List<BlockAlias>> list, BlockAlias blockAlias) {
        int[] matchBlockIds = blockAlias.getMatchBlockIds();

        for (int i = 0; i < matchBlockIds.length; i++) {
            int n = matchBlockIds[i];

            while (n >= list.size()) {
                list.add(null);
            }

            List list2 = (List)list.get(n);
            if (list2 == null) {
                list2 = new ArrayList();
                list.set(n, list2);
            }

            BlockAlias blockAlias2 = new BlockAlias(blockAlias.getBlockId(), blockAlias.getMatchBlocks(n));
            list2.add(blockAlias2);
        }
    }

    public static BlockAlias[][] toArrays(List<List<BlockAlias>> list) {
        BlockAlias[][] blockAliaseses = new BlockAlias[list.size()][];

        for (int i = 0; i < blockAliaseses.length; i++) {
            List list2 = (List)list.get(i);
            if (list2 != null) {
                blockAliaseses[i] = (BlockAlias[])list2.toArray(new BlockAlias[list2.size()]);
            }
        }

        return blockAliaseses;
    }

    public static void reset() {
        blockAliases = (BlockAlias[][])null;
    }
}
