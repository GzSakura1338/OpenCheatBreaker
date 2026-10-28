package net.optifine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;

public class ConnectedParser {
    public String context = null;
    public static VillagerProfession[] PROFESSIONS_INVALID = new VillagerProfession[0];

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
        boolean flag = false;
        byte b;
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

        this.warn("Invalid block metadata: " + str);
        return null;
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

    public int parseInt(String str) {
        if (str == null) {
            return -1;
        }

        str = str.trim();
        int parseInt = Config.parseInt(str, -1);
        if (parseInt < 0) {
            this.warn("Invalid number: " + str);
        }

        return parseInt;
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

    public boolean parseBoolean(String str) {
        if (str == null) {
            return false;
        }

        String trim = str.toLowerCase().trim();
        return trim.equals("true");
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
}
