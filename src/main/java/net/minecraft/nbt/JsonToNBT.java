package net.minecraft.nbt;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonToNBT {
   public static Logger logger = LogManager.getLogger();
   public static Pattern VALID_LIST_PATTERN = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

   public static NBTTagCompound getTagFromJson(String jsonString) throws NBTException {
      jsonString = jsonString.trim();
      if (!jsonString.startsWith("{")) {
         throw new NBTException("Invalid tag encountered, expected '{' as first char.");
      } else if (getTopElementCount(jsonString) != 1) {
         throw new NBTException("Encountered multiple top tags, only one expected");
      } else {
         return (NBTTagCompound)createParser("tag", jsonString).parse();
      }
   }

   public static int getTopElementCount(String str) throws NBTException {
      int n = 0;
      boolean flag = false;
      Stack stack = new Stack();

      for (int i = 0; i < str.length(); i++) {
         char charAt = str.charAt(i);
         if (charAt == '"') {
            if (isEscaped(str, i)) {
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
      }

      if (!stack.isEmpty()) {
         throw new NBTException("Unbalanced brackets: " + str);
      }

      if (n == 0 && !str.isEmpty()) {
         n = 1;
      }

      return n;
   }

   public static JsonToNBT.Any createParserFromArray(String... strs) throws NBTException {
      return createParser(strs[0], strs[1]);
   }

   public static JsonToNBT.Any createParser(String str, String str2) throws NBTException {
      str2 = str2.trim();
      if (str2.startsWith("{")) {
         str2 = str2.substring(1, str2.length() - 1);
         JsonToNBT.Compound compound = new JsonToNBT.Compound(str);

         while (str2.length() > 0) {
            String firstElement = getFirstElement(str2, true);
            if (firstElement.length() > 0) {
               boolean flag = false;
               compound.parsers.add(createParser(firstElement, flag));
            }

            if (str2.length() < firstElement.length() + 1) {
               break;
            }

            char charAt = str2.charAt(firstElement.length());
            if (charAt != ',' && charAt != '{' && charAt != '}' && charAt != '[' && charAt != ']') {
               throw new NBTException("Unexpected token '" + charAt + "' at: " + str2.substring(firstElement.length()));
            }

            str2 = str2.substring(firstElement.length() + 1);
         }

         return compound;
      } else if (str2.startsWith("[") && !VALID_LIST_PATTERN.matcher(str2).matches()) {
         str2 = str2.substring(1, str2.length() - 1);
         JsonToNBT.List list = new JsonToNBT.List(str);

         while (str2.length() > 0) {
            String firstElement = getFirstElement(str2, false);
            if (firstElement.length() > 0) {
               boolean flag = true;
               list.parsers.add(createParser(firstElement, flag));
            }

            if (str2.length() < firstElement.length() + 1) {
               break;
            }

            char charAt = str2.charAt(firstElement.length());
            if (charAt != ',' && charAt != '{' && charAt != '}' && charAt != '[' && charAt != ']') {
               throw new NBTException("Unexpected token '" + charAt + "' at: " + str2.substring(firstElement.length()));
            }

            str2 = str2.substring(firstElement.length() + 1);
         }

         return list;
      } else {
         return new JsonToNBT.Primitive(str, str2);
      }
   }

   public static JsonToNBT.Any createParser(String str, boolean flag) throws NBTException {
      String key = getKey(str, flag);
      String value = getValue(str, flag);
      return createParserFromArray(key, value);
   }

   public static String getFirstElement(String str, boolean flag) throws NBTException {
      int indexOf2 = indexOf(str, ':');
      int indexOf3 = indexOf(str, ',');
      if (flag) {
         if (indexOf2 == -1) {
            throw new NBTException("Unable to locate name/value separator for string: " + str);
         }

         if (indexOf3 != -1 && indexOf3 < indexOf2) {
            throw new NBTException("Name error at: " + str);
         }
      } else if (indexOf2 == -1 || indexOf2 > indexOf3) {
         indexOf2 = -1;
      }

      return getFirstElement(str, indexOf2);
   }

   public static String getFirstElement(String str, int n) throws NBTException {
      Stack stack = new Stack();
      int n2 = n + 1;
      boolean flag = false;
      boolean flag2 = false;
      boolean flag3 = false;
      int n3 = 0;

      while (n2 < str.length()) {
         char charAt = str.charAt(n2);
         if (charAt == '"') {
            if (isEscaped(str, n2)) {
               if (!flag) {
                  throw new NBTException("Illegal use of \\\": " + str);
               }
            } else {
               flag = !flag;
               if (flag && !flag3) {
                  flag2 = true;
               }

               if (!flag) {
                  n3 = n2;
               }
            }
         } else if (!flag) {
            if (charAt != '{' && charAt != '[') {
               if (charAt == '}' && (stack.isEmpty() || (Character)stack.pop() != '{')) {
                  throw new NBTException("Unbalanced curly brackets {}: " + str);
               }

               if (charAt == ']' && (stack.isEmpty() || (Character)stack.pop() != '[')) {
                  throw new NBTException("Unbalanced square brackets []: " + str);
               }

               if (charAt == ',' && stack.isEmpty()) {
                  return str.substring(0, n2);
               }
            } else {
               stack.push(charAt);
            }
         }

         if (!Character.isWhitespace(charAt)) {
            if (!flag && flag2 && n3 != n2) {
               return str.substring(0, n3 + 1);
            }

            flag3 = true;
         }

         n2++;
      }

      return str.substring(0, n2);
   }

   public static String getKey(String str, boolean flag) throws NBTException {
      if (flag) {
         str = str.trim();
         if (str.startsWith("{") || str.startsWith("[")) {
            return "";
         }
      }

      int indexOf2 = indexOf(str, ':');
      if (indexOf2 != -1) {
         return str.substring(0, indexOf2).trim();
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

      int indexOf2 = indexOf(str, ':');
      if (indexOf2 != -1) {
         return str.substring(indexOf2 + 1).trim();
      } else if (flag) {
         return str;
      } else {
         throw new NBTException("Unable to locate name/value separator for string: " + str);
      }
   }

   public static int indexOf(String str, char c) {
      int n = 0;
      boolean flag = true;

      while (n < str.length()) {
         char charAt = str.charAt(n);
         if (charAt == '"') {
            if (!isEscaped(str, n)) {
               flag = !flag;
            }
         } else if (flag) {
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

   public static boolean isEscaped(String str, int n) {
      return n > 0 && str.charAt(n - 1) == '\\' && !isEscaped(str, n - 1);
   }

   abstract static class Any {
      public String json;

      public Any() {
      }

      public abstract NBTBase parse() throws NBTException;
   }

   static class Compound extends JsonToNBT.Any {
      public java.util.List<JsonToNBT.Any> parsers = Lists.newArrayList();

      public Compound(String str) {
         this.json = str;
      }

      @Override
      public NBTBase parse() throws NBTException {
         NBTTagCompound nbttagCompound = new NBTTagCompound();

         for (JsonToNBT.Any any : this.parsers) {
            nbttagCompound.setTag(any.json, any.parse());
         }

         return nbttagCompound;
      }
   }

   static class List extends JsonToNBT.Any {
      public java.util.List<JsonToNBT.Any> parsers = Lists.newArrayList();

      public List(String json) {
         this.json = json;
      }

      @Override
      public NBTBase parse() throws NBTException {
         NBTTagList nbttagList = new NBTTagList();

         for (JsonToNBT.Any any : this.parsers) {
            nbttagList.appendTag(any.parse());
         }

         return nbttagList;
      }
   }

   static class Primitive extends JsonToNBT.Any {
      public static Pattern DOUBLE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
      public static Pattern FLOAT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
      public static Pattern BYTE = Pattern.compile("[-+]?[0-9]+[b|B]");
      public static Pattern LONG = Pattern.compile("[-+]?[0-9]+[l|L]");
      public static Pattern SHORT = Pattern.compile("[-+]?[0-9]+[s|S]");
      public static Pattern INTEGER = Pattern.compile("[-+]?[0-9]+");
      public static Pattern DOUBLE_UNTYPED = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
      public static Splitter SPLITTER = Splitter.on(',').omitEmptyStrings();
      public String jsonValue;

      public Primitive(String str, String str2) {
         this.json = str;
         this.jsonValue = str2;
      }

      @Override
      public NBTBase parse() throws NBTException {
         try {
            if (DOUBLE.matcher(this.jsonValue).matches()) {
               return new NBTTagDouble(Double.parseDouble(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
            }

            if (FLOAT.matcher(this.jsonValue).matches()) {
               return new NBTTagFloat(Float.parseFloat(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
            }

            if (BYTE.matcher(this.jsonValue).matches()) {
               return new NBTTagByte(Byte.parseByte(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
            }

            if (LONG.matcher(this.jsonValue).matches()) {
               return new NBTTagLong(Long.parseLong(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
            }

            if (SHORT.matcher(this.jsonValue).matches()) {
               return new NBTTagShort(Short.parseShort(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
            }

            if (INTEGER.matcher(this.jsonValue).matches()) {
               return new NBTTagInt(Integer.parseInt(this.jsonValue));
            }

            if (DOUBLE_UNTYPED.matcher(this.jsonValue).matches()) {
               return new NBTTagDouble(Double.parseDouble(this.jsonValue));
            }

            if (this.jsonValue.equalsIgnoreCase("true") || this.jsonValue.equalsIgnoreCase("false")) {
               return new NBTTagByte((byte)(Boolean.parseBoolean(this.jsonValue) ? 1 : 0));
            }
         } catch (NumberFormatException e) {
            this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
            return new NBTTagString(this.jsonValue);
         }

         if (this.jsonValue.startsWith("[") && this.jsonValue.endsWith("]")) {
            String substring = this.jsonValue.substring(1, this.jsonValue.length() - 1);
            String[] toArray = (String[])Iterables.toArray(SPLITTER.split(substring), String.class);

            try {
               int[] ints = new int[toArray.length];

               for (int i = 0; i < toArray.length; i++) {
                  ints[i] = Integer.parseInt(toArray[i].trim());
               }

               return new NBTTagIntArray(ints);
            } catch (NumberFormatException e) {
               return new NBTTagString(this.jsonValue);
            }
         } else {
            if (this.jsonValue.startsWith("\"") && this.jsonValue.endsWith("\"")) {
               this.jsonValue = this.jsonValue.substring(1, this.jsonValue.length() - 1);
            }

            this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < this.jsonValue.length(); i++) {
               if (i < this.jsonValue.length() - 1 && this.jsonValue.charAt(i) == '\\' && this.jsonValue.charAt(i + 1) == '\\') {
                  sb.append('\\');
                  i++;
               } else {
                  sb.append(this.jsonValue.charAt(i));
               }
            }

            return new NBTTagString(sb.toString());
         }
      }
   }
}
