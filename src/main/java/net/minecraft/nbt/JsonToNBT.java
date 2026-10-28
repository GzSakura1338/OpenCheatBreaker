package net.minecraft.nbt;

import java.util.ArrayList;
import java.util.Stack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonToNBT {
    public static Logger logger = LogManager.getLogger();

    public static NBTBase createNBTException(String str) throws NBTException {
        str = str.trim();
        int topElementCount = getTopElementCount(str);
        if (topElementCount != 1) {
            throw new NBTException("Encountered multiple top tags, only one expected");
        }

        JsonToNBT.Any any = null;
        if (str.startsWith("{")) {
            any = findAny("tag", str);
        } else {
            any = findAny(getKey(str, false), getValue(str, false));
        }

        return any.getNBTBase();
    }

    public static int getTopElementCount(String str) throws NBTException {
        int n = 0;
        boolean flag = false;
        Stack stack = new Stack();

        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '"') {
                if (i > 0 && str.charAt(i - 1) == '\\') {
                    if (!flag) {
                        throw new NBTException("Illegal use of \\\": " + str);
                    }
                } else {
                    flag = !flag;
                }
            } else if (!flag) {
                if (charAt != '{' && charAt != '[') {
                    if (charAt == '}' && (stack.isEmpty() || (Character)stack.pop() != '{')) {
                        throw new NBTException("Unbalanced curly brackets {}: " + str);
                    }

                    if (charAt == ']' && (stack.isEmpty() || (Character)stack.pop() != '[')) {
                        throw new NBTException("Unbalanced square brackets []: " + str);
                    }
                } else {
                    if (stack.isEmpty()) {
                        n++;
                    }

                    stack.push(charAt);
                }
            }
        }

        if (flag) {
            throw new NBTException("Unbalanced quotation: " + str);
        } else if (!stack.isEmpty()) {
            throw new NBTException("Unbalanced brackets: " + str);
        } else {
            return n == 0 && !str.isEmpty() ? 1 : n;
        }
    }

    public static JsonToNBT.Any findAny(String str, String str2) throws NBTException {
        str2 = str2.trim();
        getTopElementCount(str2);
        if (str2.startsWith("{")) {
            if (!str2.endsWith("}")) {
                throw new NBTException("Unable to locate ending bracket for: " + str2);
            }

            str2 = str2.substring(1, str2.length() - 1);
            JsonToNBT.Compound compound = new JsonToNBT.Compound(str);

            while (str2.length() > 0) {
                String firstElement = getFirstElement(str2, false);
                if (firstElement.length() > 0) {
                    String key = getKey(firstElement, false);
                    String value = getValue(firstElement, false);
                    compound.parsers.add(findAny(key, value));
                    if (str2.length() < firstElement.length() + 1) {
                        break;
                    }

                    char charAt = str2.charAt(firstElement.length());
                    if (charAt != ',' && charAt != '{' && charAt != '}' && charAt != '[' && charAt != ']') {
                        throw new NBTException("Unexpected token '" + charAt + "' at: " + str2.substring(firstElement.length()));
                    }

                    str2 = str2.substring(firstElement.length() + 1);
                }
            }

            return compound;
        } else {
            if (!str2.startsWith("[") || str2.matches("\\[[-\\d|,\\s]+\\]")) {
                return new JsonToNBT.Primitive(str, str2);
            }

            if (!str2.endsWith("]")) {
                throw new NBTException("Unable to locate ending bracket for: " + str2);
            }

            str2 = str2.substring(1, str2.length() - 1);
            JsonToNBT.List list = new JsonToNBT.List(str);

            while (str2.length() > 0) {
                String firstElement = getFirstElement(str2, true);
                if (firstElement.length() > 0) {
                    String key = getKey(firstElement, true);
                    String value = getValue(firstElement, true);
                    list.parsers.add(findAny(key, value));
                    if (str2.length() < firstElement.length() + 1) {
                        break;
                    }

                    char charAt = str2.charAt(firstElement.length());
                    if (charAt != ',' && charAt != '{' && charAt != '}' && charAt != '[' && charAt != ']') {
                        throw new NBTException("Unexpected token '" + charAt + "' at: " + str2.substring(firstElement.length()));
                    }

                    str2 = str2.substring(firstElement.length() + 1);
                } else {
                    logger.debug(str2);
                }
            }

            return list;
        }
    }

    public static String getFirstElement(String str, boolean flag) throws NBTException {
        int indexOf2 = indexOf(str, ':');
        if (indexOf2 < 0 && !flag) {
            throw new NBTException("Unable to locate name/value separator for string: " + str);
        }

        int indexOf3 = indexOf(str, ',');
        if (indexOf3 >= 0 && indexOf3 < indexOf2 && !flag) {
            throw new NBTException("Name error at: " + str);
        }

        if (flag && (indexOf2 < 0 || indexOf2 > indexOf3)) {
            indexOf2 = -1;
        }

        Stack stack = new Stack();
        int n = indexOf2 + 1;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        int n2 = 0;

        while (n < str.length()) {
            char charAt = str.charAt(n);
            if (charAt == '"') {
                if (n > 0 && str.charAt(n - 1) == '\\') {
                    if (!flag2) {
                        throw new NBTException("Illegal use of \\\": " + str);
                    }
                } else {
                    flag2 = !flag2;
                    if (flag2 && !flag4) {
                        flag3 = true;
                    }

                    if (!flag2) {
                        n2 = n;
                    }
                }
            } else if (!flag2) {
                if (charAt != '{' && charAt != '[') {
                    if (charAt == '}' && (stack.isEmpty() || (Character)stack.pop() != '{')) {
                        throw new NBTException("Unbalanced curly brackets {}: " + str);
                    }

                    if (charAt == ']' && (stack.isEmpty() || (Character)stack.pop() != '[')) {
                        throw new NBTException("Unbalanced square brackets []: " + str);
                    }

                    if (charAt == ',' && stack.isEmpty()) {
                        return str.substring(0, n);
                    }
                } else {
                    stack.push(charAt);
                }
            }

            if (!Character.isWhitespace(charAt)) {
                if (!flag2 && flag3 && n2 != n) {
                    return str.substring(0, n2 + 1);
                }

                flag4 = true;
            }

            n++;
        }

        return str.substring(0, n);
    }

    public static String getKey(String str, boolean flag) throws NBTException {
        if (flag) {
            str = str.trim();
            if (str.startsWith("{") || str.startsWith("[")) {
                return "";
            }
        }

        int indexOf = str.indexOf(58);
        if (indexOf >= 0) {
            return str.substring(0, indexOf).trim();
        } else if (flag) {
            return "";
        } else {
            throw new NBTException("Unable to locate name/value separator for string: " + str);
        }
    }

    public static String getValue(String str, boolean flag) throws NBTException {
        if (flag) {
            str = str.trim();
            if (str.startsWith("{") || str.startsWith("[")) {
                return str;
            }
        }

        int indexOf = str.indexOf(58);
        if (indexOf >= 0) {
            return str.substring(indexOf + 1).trim();
        } else if (flag) {
            return str;
        } else {
            throw new NBTException("Unable to locate name/value separator for string: " + str);
        }
    }

    public static int indexOf(String str, char c) {
        int n = 0;
        boolean flag = false;

        while (n < str.length()) {
            char charAt = str.charAt(n);
            if (charAt == '"') {
                if (n <= 0 || str.charAt(n - 1) != '\\') {
                    flag = !flag;
                }
            } else if (!flag) {
                if (charAt == c) {
                    return n;
                }

                if (charAt == '{' || charAt == '[') {
                    return -1;
                }
            }

            n++;
        }

        return -1;
    }

    public abstract static class Any {
        public String key;

        public abstract NBTBase getNBTBase();
    }

    public static class Compound extends JsonToNBT.Any {
        public ArrayList parsers = new ArrayList();

        public Compound(String str) {
            this.key = str;
        }

        @Override
        public NBTBase getNBTBase() {
            NBTTagCompound nbttagCompound = new NBTTagCompound();

            for (JsonToNBT.Any any : (Iterable<JsonToNBT.Any>)this.parsers) {
                nbttagCompound.setTag(any.key, any.getNBTBase());
            }

            return nbttagCompound;
        }
    }

    public static class List extends JsonToNBT.Any {
        public ArrayList parsers = new ArrayList();

        public List(String str) {
            this.key = str;
        }

        @Override
        public NBTBase getNBTBase() {
            NBTTagList nbttagList = new NBTTagList();

            for (JsonToNBT.Any any : (Iterable<JsonToNBT.Any>)this.parsers) {
                nbttagList.appendTag(any.getNBTBase());
            }

            return nbttagList;
        }
    }

    public static class Primitive extends JsonToNBT.Any {
        public String value;

        public Primitive(String str, String str2) {
            this.key = str;
            this.value = str2;
        }

        @Override
        public NBTBase getNBTBase() {
            try {
                if (this.value.matches("[-+]?[0-9]*\\.?[0-9]+[d|D]")) {
                    return new NBTTagDouble(Double.parseDouble(this.value.substring(0, this.value.length() - 1)));
                }

                if (this.value.matches("[-+]?[0-9]*\\.?[0-9]+[f|F]")) {
                    return new NBTTagFloat(Float.parseFloat(this.value.substring(0, this.value.length() - 1)));
                }

                if (this.value.matches("[-+]?[0-9]+[b|B]")) {
                    return new NBTTagByte(Byte.parseByte(this.value.substring(0, this.value.length() - 1)));
                }

                if (this.value.matches("[-+]?[0-9]+[l|L]")) {
                    return new NBTTagLong(Long.parseLong(this.value.substring(0, this.value.length() - 1)));
                }

                if (this.value.matches("[-+]?[0-9]+[s|S]")) {
                    return new NBTTagShort(Short.parseShort(this.value.substring(0, this.value.length() - 1)));
                }

                if (this.value.matches("[-+]?[0-9]+")) {
                    return new NBTTagInt(Integer.parseInt(this.value));
                }

                if (this.value.matches("[-+]?[0-9]*\\.?[0-9]+")) {
                    return new NBTTagDouble(Double.parseDouble(this.value));
                }

                if (this.value.equalsIgnoreCase("true") || this.value.equalsIgnoreCase("false")) {
                    return new NBTTagByte((byte)(Boolean.parseBoolean(this.value) ? 1 : 0));
                }

                if (this.value.startsWith("[") && this.value.endsWith("]")) {
                    if (this.value.length() > 2) {
                        String substring = this.value.substring(1, this.value.length() - 1);
                        String[] split = substring.split(",");

                        try {
                            if (split.length <= 1) {
                                return new NBTTagIntArray(new int[]{Integer.parseInt(substring.trim())});
                            }

                            int[] ints = new int[split.length];

                            for (int i = 0; i < split.length; i++) {
                                ints[i] = Integer.parseInt(split[i].trim());
                            }

                            return new NBTTagIntArray(ints);
                        } catch (NumberFormatException e) {
                            return new NBTTagString(this.value);
                        }
                    } else {
                        return new NBTTagIntArray();
                    }
                } else {
                    if (this.value.startsWith("\"") && this.value.endsWith("\"") && this.value.length() > 2) {
                        this.value = this.value.substring(1, this.value.length() - 1);
                    }

                    this.value = this.value.replaceAll("\\\\\"", "\"");
                    return new NBTTagString(this.value);
                }
            } catch (NumberFormatException e) {
                this.value = this.value.replaceAll("\\\\\"", "\"");
                return new NBTTagString(this.value);
            }
        }
    }
}
