package net.optifine;

import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class ConnectedProperties {
    public String name = null;
    public String basePath = null;
    public int[] matchBlocks = null;
    public String[] matchTiles = null;
    public int method = 0;
    public String[] tiles = null;
    public int connect = 0;
    public int faces = 63;
    public int[] metadatas = null;
    public BiomeGenBase[] biomes = null;
    public RangeListInt heights = null;
    public int renderPass = 0;
    public boolean innerSeams = false;
    public int width = 0;
    public int height = 0;
    public int[] weights = null;
    public int symmetry = 1;
    public boolean linked = false;
    public int[] sumWeights = null;
    public int sumAllWeights = 1;
    public IIcon[] matchTileIcons = null;
    public IIcon[] tileIcons = null;
    public static final int METHOD_REPEAT = 5;
    public static final int METHOD_FIXED = 7;
    public static final int METHOD_VERTICAL_HORIZONTAL = 9;
    public static final int FACE_NORTH = 16;
    public static final int FACE_SOUTH = 32;
    public static final int FACE_SIDES = 60;
    public static final int FACE_ALL = 63;
    public static final int CONSTANT_1 = 1;
    public static final int CONSTANT_3 = 3;
    public static final int CONSTANT_6 = 6;
    public static final int CONSTANT_8 = 8;
    public static final int CONSTANT_2 = 2;
    public static final int CONSTANT_6_2 = 6;
    public static final int CONSTANT_3_2 = 3;
    public static final int CONSTANT_2_2 = 2;
    public static final int CONSTANT_1_2 = 1;
    public static final int CONSTANT_2_3 = 2;
    public static final int CONSTANT_128 = 128;
    public static final int CONSTANT_4 = 4;
    public static final int CONSTANT_2_4 = 2;
    public static final int CONSTANT_128_2 = 128;
    public static final int CONSTANT_4_2 = 4;
    public static final int CONSTANT_0 = 0;
    public static final int CONSTANT_0_2 = 0;
    public static final int CONSTANT_128_3 = 128;
    public static final int CONSTANT_1_3 = 1;
    public static final int CONSTANT_1_4 = 1;
    public static final int CONSTANT_8_2 = 8;

    public ConnectedProperties(Properties properties, String str) {
        ConnectedParser connectedParser = new ConnectedParser("ConnectedTextures");
        this.name = parseName(str);
        this.basePath = parseBasePath(str);
        this.matchBlocks = parseBlockIds(properties.getProperty("matchBlocks"));
        this.matchTiles = this.parseMatchTiles(properties.getProperty("matchTiles"));
        this.method = parseMethod(properties.getProperty("method"));
        this.tiles = this.parseTileNames(properties.getProperty("tiles"));
        this.connect = parseConnect(properties.getProperty("connect"));
        this.faces = parseFaces(properties.getProperty("faces"));
        this.metadatas = parseInts(properties.getProperty("metadata"));
        this.biomes = connectedParser.parseBiomes(properties.getProperty("biomes"));
        this.heights = connectedParser.parseRangeListInt(properties.getProperty("heights"));
        if (this.heights == null) {
            int parseInt = connectedParser.parseInt(properties.getProperty("minHeight"), -1);
            int parseInt2 = connectedParser.parseInt(properties.getProperty("maxHeight"), 1024);
            if (parseInt != -1 || parseInt2 != 1024) {
                this.heights = new RangeListInt(new RangeInt(parseInt, parseInt2));
            }
        }

        this.renderPass = parseInt(properties.getProperty("renderPass"));
        this.innerSeams = parseBoolean(properties.getProperty("innerSeams"));
        this.width = parseInt(properties.getProperty("width"));
        this.height = parseInt(properties.getProperty("height"));
        this.weights = parseInts(properties.getProperty("weights"));
        this.symmetry = parseSymmetry(properties.getProperty("symmetry"));
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

    public static BiomeGenBase[] parseBiomes(String str) {
        if (str == null) {
            return null;
        }

        String[] tokenize = Config.tokenize(str, " ");
        ArrayList list = new ArrayList();

        for (int i = 0; i < tokenize.length; i++) {
            String str2 = tokenize[i];
            BiomeGenBase findBiome2 = findBiome(str2);
            if (findBiome2 == null) {
                Config.warn("Biome not found: " + str2);
            } else {
                list.add(findBiome2);
            }
        }

        return (BiomeGenBase[])list.toArray(new BiomeGenBase[list.size()]);
    }

    public static BiomeGenBase findBiome(String str) {
        str = str.toLowerCase();
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
                        if (parseInt <= parseInt2) {
                            for (int j = parseInt; j <= parseInt2; j++) {
                                list.add(String.valueOf(j));
                            }
                            continue;
                        }

                        Config.warn("Invalid interval: " + str2 + ", when parsing: " + str);
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

            String str3 = "textures/blocks/";
            if (str2.startsWith(str3)) {
                str2 = str2.substring(str3.length());
            }

            if (str2.startsWith("/")) {
                str2 = str2.substring(1);
            }

            toArray[i] = str2;
        }

        return toArray;
    }

    public static int parseInt(String str) {
        if (str == null) {
            return -1;
        }

        int parseInt = Config.parseInt(str, -1);
        if (parseInt < 0) {
            Config.warn("Invalid number: " + str);
        }

        return parseInt;
    }

    public static int parseInt(String str, int n) {
        if (str == null) {
            return n;
        } else {
            int parseInt = Config.parseInt(str, -1);
            if (parseInt < 0) {
                Config.warn("Invalid number: " + str);
                return n;
            } else {
                return parseInt;
            }
        }
    }

    public static boolean parseBoolean(String str) {
        return str == null ? false : str.toLowerCase().equals("true");
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
        if (str.equals("bottom")) {
            return 1;
        }

        if (str.equals("top")) {
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

    public static int[] parseInts(String str) {
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
                    Config.warn("Invalid interval: " + str2 + ", when parsing: " + str);
                } else {
                    int parseInt = Config.parseInt(tokenize2[0], -1);
                    int parseInt2 = Config.parseInt(tokenize2[1], -1);
                    if (parseInt >= 0 && parseInt2 >= 0 && parseInt <= parseInt2) {
                        for (int j = parseInt; j <= parseInt2; j++) {
                            list.add(j);
                        }
                    } else {
                        Config.warn("Invalid interval: " + str2 + ", when parsing: " + str);
                    }
                }
            } else {
                int parseInt = Config.parseInt(str2, -1);
                if (parseInt < 0) {
                    Config.warn("Invalid number: " + str2 + ", when parsing: " + str);
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

    public static int[] parseBlockIds(String str) {
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
                    Config.warn("Invalid interval: " + str2 + ", when parsing: " + str);
                } else {
                    int parseBlockId2 = parseBlockId(tokenize2[0]);
                    int parseBlockId3 = parseBlockId(tokenize2[1]);
                    if (parseBlockId2 >= 0 && parseBlockId3 >= 0 && parseBlockId2 <= parseBlockId3) {
                        for (int j = parseBlockId2; j <= parseBlockId3; j++) {
                            list.add(j);
                        }
                    } else {
                        Config.warn("Invalid interval: " + str2 + ", when parsing: " + str);
                    }
                }
            } else {
                int parseBlockId2 = parseBlockId(str2);
                if (parseBlockId2 < 0) {
                    Config.warn("Invalid block ID: " + str2 + ", when parsing: " + str);
                } else {
                    list.add(parseBlockId2);
                }
            }
        }

        int[] ints = new int[list.size()];

        for (int i = 0; i < ints.length; i++) {
            ints[i] = (Integer)list.get(i);
        }

        return ints;
    }

    public static int parseBlockId(String str) {
        int parseInt = Config.parseInt(str, -1);
        if (parseInt >= 0) {
            return parseInt;
        }

        Block blockFromName = Block.getBlockFromName(str);
        return blockFromName != null ? Block.getIdFromBlock(blockFromName) : -1;
    }

    public static int parseMethod(String str) {
        if (str == null) {
            return 1;
        } else {
            str = str.trim();
            if (str.equals("ctm") || str.equals("glass")) {
                return 1;
            } else if (str.equals("horizontal") || str.equals("bookshelf")) {
                return 2;
            } else if (str.equals("vertical")) {
                return 6;
            } else if (str.equals("top")) {
                return 3;
            } else if (str.equals("random")) {
                return 4;
            } else if (str.equals("repeat")) {
                return 5;
            } else if (str.equals("fixed")) {
                return 7;
            } else if (str.equals("horizontal+vertical") || str.equals("h+v")) {
                return 8;
            } else if (!str.equals("vertical+horizontal") && !str.equals("v+h")) {
                Config.warn("Unknown method: " + str);
                return 0;
            } else {
                return 9;
            }
        }
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
                    return this.isValidHorizontal(str);
                case 3:
                    return this.isValidVertical(str);
                case 4:
                    return this.isValidTop(str);
                case 5:
                    return this.isValidRandom(str);
                case 6:
                    return this.isValidRepeat(str);
                case 7:
                    return this.isValidFixed(str);
                case 8:
                    return this.isValidHorizontalVertical(str);
                case 9:
                    return this.isValidVerticalHorizontal(str);
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

    public int[] detectMatchBlocks() {
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
        IIcon icon = getIcon(this.name);
        return icon == null ? null : new String[]{this.name};
    }

    public static IIcon getIcon(String str) {
        return TextureMap.textureMapBlocks.getIconSafe(str);
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

    public boolean isValidHorizontal(String str) {
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

    public boolean isValidVertical(String str) {
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

    public boolean isValidHorizontalVertical(String str) {
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

    public boolean isValidVerticalHorizontal(String str) {
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

    public boolean isValidRandom(String str) {
        if (this.tiles == null) {
            Config.warn("Tiles not defined: " + str);
            return false;
        }

        if (this.width <= 0 || this.width > 16) {
            Config.warn("Invalid width: " + str);
            return false;
        }

        if (this.height > 0 && this.height <= 16) {
            if (this.tiles.length != this.width * this.height) {
                Config.warn("Number of tiles does not equal width x height: " + str);
                return false;
            } else {
                return true;
            }
        } else {
            Config.warn("Invalid height: " + str);
            return false;
        }
    }

    public int getAverage(int[] ints) {
        if (ints.length <= 0) {
            return 0;
        }

        int n = 0;

        for (int i = 0; i < ints.length; i++) {
            int n2 = ints[i];
            n += n2;
        }

        return n / ints.length;
    }

    public boolean isValidRepeat(String str) {
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

    public boolean isValidFixed(String str) {
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

    public boolean isValidTop(String str) {
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
                    int average = this.getAverage(this.weights);

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

            return true;
        } else {
            Config.warn("Tiles not defined: " + str);
            return false;
        }
    }

    public void updateIcons(TextureMap textureMap) {
        if (this.matchTiles != null) {
            this.matchTileIcons = registerIcons(this.matchTiles, textureMap);
        }

        if (this.tiles != null) {
            this.tileIcons = registerIcons(this.tiles, textureMap);
        }
    }

    public static IIcon[] registerIcons(String[] strs, TextureMap textureMap) {
        if (strs == null) {
            return null;
        }

        ArrayList list = new ArrayList();

        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
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

            IIcon registerIcon = textureMap.registerIcon(str);
            list.add(registerIcon);
        }

        return (IIcon[])list.toArray(new IIcon[list.size()]);
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
}
