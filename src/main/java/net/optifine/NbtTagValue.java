package net.optifine;

import java.util.Arrays;
import java.util.regex.Pattern;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.StringEscapeUtils;

public class NbtTagValue {
    public String[] parents = null;
    public String name = null;
    public boolean negative = false;
    public int type = 0;
    public String value = null;
    public int valueFormat = 0;
    public static final int TYPE_IPATTERN = 2;
    public static final int TYPE_REGEX = 3;
    public static final int TYPE_IREGEX = 4;
    public static final String PREFIX_PATTERN = "pattern:";
    public static final String PREFIX_IPATTERN = "ipattern:";
    public static final String PREFIX_REGEX = "regex:";
    public static final String PREFIX_IREGEX = "iregex:";
    public static final String PREFIX_HEX_COLOR = "#";
    public static Pattern PATTERN_HEX_COLOR = Pattern.compile("^#[0-9a-f]{6}+$");
    public static final int CONSTANT_1 = 1;
    public static final int CONSTANT_1_2 = 1;
    public static final int CONSTANT_0 = 0;
    public static final int CONSTANT_0_2 = 0;

    public NbtTagValue(String str, String str2) {
        String[] tokenize = Config.tokenize(str, ".");
        this.parents = Arrays.copyOfRange(tokenize, 0, tokenize.length - 1);
        this.name = tokenize[tokenize.length - 1];
        if (str2.startsWith("!")) {
            this.negative = true;
            str2 = str2.substring(1);
        }

        if (str2.startsWith("pattern:")) {
            this.type = 1;
            str2 = str2.substring("pattern:".length());
        } else if (str2.startsWith("ipattern:")) {
            this.type = 2;
            str2 = str2.substring("ipattern:".length()).toLowerCase();
        } else if (str2.startsWith("regex:")) {
            this.type = 3;
            str2 = str2.substring("regex:".length());
        } else if (str2.startsWith("iregex:")) {
            this.type = 4;
            str2 = str2.substring("iregex:".length()).toLowerCase();
        } else {
            this.type = 0;
        }

        str2 = StringEscapeUtils.unescapeJava(str2);
        if (this.type == 0 && PATTERN_HEX_COLOR.matcher(str2).matches()) {
            this.valueFormat = 1;
        }

        this.value = str2;
    }

    public boolean matches(NBTTagCompound nbttagCompound) {
        return this.negative ? !this.matchesCompound(nbttagCompound) : this.matchesCompound(nbttagCompound);
    }

    public boolean matchesCompound(NBTTagCompound nbttagCompound) {
        if (nbttagCompound == null) {
            return false;
        }

        NBTBase nbtbase = nbttagCompound;

        for (int i = 0; i < this.parents.length; i++) {
            String str = this.parents[i];
            nbtbase = getChildTag(nbtbase, str);
            if (nbtbase == null) {
                return false;
            }
        }

        if (this.name.equals("*")) {
            return this.matchesAnyChild(nbtbase);
        }

        NBTBase childTag = getChildTag(nbtbase, this.name);
        return childTag == null ? false : this.matchesBase(childTag);
    }

    public boolean matchesAnyChild(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagCompound = (NBTTagCompound)nbtbase;

            for (String str : (Iterable<String>)nbttagCompound.getKeySet()) {
                NBTBase tag = nbttagCompound.getTag(str);
                if (this.matchesBase(tag)) {
                    return true;
                }
            }
        }

        if (nbtbase instanceof NBTTagList) {
            NBTTagList nbttagList = (NBTTagList)nbtbase;
            int tagCount = nbttagList.tagCount();

            for (int i = 0; i < tagCount; i++) {
                NBTTagCompound compoundTagAt = nbttagList.getCompoundTagAt(i);
                if (this.matchesBase(compoundTagAt)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static NBTBase getChildTag(NBTBase nbtbase, String str) {
        if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagCompound = (NBTTagCompound)nbtbase;
            return nbttagCompound.getTag(str);
        }

        if (nbtbase instanceof NBTTagList) {
            NBTTagList nbttagList = (NBTTagList)nbtbase;
            if (str.equals("count")) {
                return new NBTTagInt(nbttagList.tagCount());
            }

            int parseInt = Config.parseInt(str, -1);
            return parseInt < 0 ? null : nbttagList.getCompoundTagAt(parseInt);
        } else {
            return null;
        }
    }

    public boolean matchesBase(NBTBase nbtbase) {
        if (nbtbase == null) {
            return false;
        }

        String nbtString = getNbtString(nbtbase, this.valueFormat);
        return this.matchesValue(nbtString);
    }

    public boolean matchesValue(String str) {
        if (str == null) {
            return false;
        }

        switch (this.type) {
            case 0:
                return str.equals(this.value);
            case 1:
                return this.matchesPattern(str, this.value);
            case 2:
                return this.matchesPattern(str.toLowerCase(), this.value);
            case 3:
                return this.matchesRegex(str, this.value);
            case 4:
                return this.matchesRegex(str.toLowerCase(), this.value);
            default:
                throw new IllegalArgumentException("Unknown NbtTagValue type: " + this.type);
        }
    }

    public boolean matchesPattern(String str, String str2) {
        return StrUtils.equalsMask(str, str2, '*', '?');
    }

    public boolean matchesRegex(String str, String str2) {
        return str.matches(str2);
    }

    public static String getNbtString(NBTBase nbtbase, int n) {
        if (nbtbase == null) {
            return null;
        } else if (nbtbase instanceof NBTTagString) {
            NBTTagString nbttagString = (NBTTagString)nbtbase;
            return nbttagString.getString();
        } else if (nbtbase instanceof NBTTagInt) {
            NBTTagInt nbttagInt = (NBTTagInt)nbtbase;
            return n == 1 ? "#" + StrUtils.fillLeft(Integer.toHexString(nbttagInt.getInt()), 6, '0') : Integer.toString(nbttagInt.getInt());
        } else if (nbtbase instanceof NBTTagByte) {
            NBTTagByte nbttagByte = (NBTTagByte)nbtbase;
            return Byte.toString(nbttagByte.getByte());
        } else if (nbtbase instanceof NBTTagShort) {
            NBTTagShort nbttagShort = (NBTTagShort)nbtbase;
            return Short.toString(nbttagShort.getShort());
        } else if (nbtbase instanceof NBTTagLong) {
            NBTTagLong nbttagLong = (NBTTagLong)nbtbase;
            return Long.toString(nbttagLong.getLong());
        } else if (nbtbase instanceof NBTTagFloat) {
            NBTTagFloat nbttagFloat = (NBTTagFloat)nbtbase;
            return Float.toString(nbttagFloat.getFloat());
        } else if (nbtbase instanceof NBTTagDouble) {
            NBTTagDouble nbttagDouble = (NBTTagDouble)nbtbase;
            return Double.toString(nbttagDouble.getDouble());
        } else {
            return nbtbase.toString();
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < this.parents.length; i++) {
            String str = this.parents[i];
            if (i > 0) {
                sb.append(".");
            }

            sb.append(str);
        }

        if (sb.length() > 0) {
            sb.append(".");
        }

        sb.append(this.name);
        sb.append(" = ");
        sb.append(this.value);
        return sb.toString();
    }
}
