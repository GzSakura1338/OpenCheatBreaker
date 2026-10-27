package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum EnumChatFormatting {
   BLACK("BLACK", '0', 0),
   DARK_BLUE("DARK_BLUE", '1', 1),
   DARK_GREEN("DARK_GREEN", '2', 2),
   DARK_AQUA("DARK_AQUA", '3', 3),
   DARK_RED("DARK_RED", '4', 4),
   DARK_PURPLE("DARK_PURPLE", '5', 5),
   GOLD("GOLD", '6', 6),
   GRAY("GRAY", '7', 7),
   DARK_GRAY("DARK_GRAY", '8', 8),
   BLUE("BLUE", '9', 9),
   GREEN("GREEN", 'a', 10),
   AQUA("AQUA", 'b', 11),
   RED("RED", 'c', 12),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
   YELLOW("YELLOW", 'e', 14),
   WHITE("WHITE", 'f', 15),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1);

   public static Map<String, EnumChatFormatting> nameMapping = Maps.newHashMap();
   public String name;
   public char formattingCode;
   public boolean fancyStyling;
   public int colorIndex;
   public String controlString;
   public static Pattern pattern = Pattern.compile("(?i)§[0-9A-FK-OR]");
   public static Pattern formattingCodePattern = Pattern.compile("(?i)§[0-9A-FR]");

   public static String sanitize(String str) {
      return str.toLowerCase().replaceAll("[^a-z]", "");
   }

   EnumChatFormatting(String formattingName, char formattingCodeIn, int colorIndex) {
      this(formattingName, formattingCodeIn, false, colorIndex);
   }

   EnumChatFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn) {
      this(formattingName, formattingCodeIn, fancyStylingIn, -1);
   }

   EnumChatFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn, int colorIndex) {
      this.name = formattingName;
      this.formattingCode = formattingCodeIn;
      this.fancyStyling = fancyStylingIn;
      this.colorIndex = colorIndex;
      this.controlString = "§" + formattingCodeIn;
   }

   public int getColorIndex() {
      return this.colorIndex;
   }

   public boolean isFancyStyling() {
      return this.fancyStyling;
   }

   public boolean isColor() {
      return !this.fancyStyling && this != RESET;
   }

   public String getFriendlyName() {
      return this.name().toLowerCase();
   }

   @Override
   public String toString() {
      return this.controlString;
   }

   public static String getTextWithoutFormattingCodes(String text) {
      return text == null ? null : pattern.matcher(text).replaceAll("");
   }

   public static EnumChatFormatting getValueByName(String friendlyName) {
      return friendlyName == null ? null : nameMapping.get(sanitize(friendlyName));
   }

   public static EnumChatFormatting byColorIndex(int n) {
      if (n < 0) {
         return RESET;
      }

      for (EnumChatFormatting enumChatFormatting : values()) {
         if (enumChatFormatting.getColorIndex() == n) {
            return enumChatFormatting;
         }
      }

      return null;
   }

   public static Collection<String> getValidValues(boolean flag, boolean flag2) {
      ArrayList newArrayList = Lists.newArrayList();

      for (EnumChatFormatting enumChatFormatting : values()) {
         if ((!enumChatFormatting.isColor() || flag) && (!enumChatFormatting.isFancyStyling() || flag2)) {
            newArrayList.add(enumChatFormatting.getFriendlyName());
         }
      }

      return newArrayList;
   }

   static {
      for (EnumChatFormatting enumChatFormatting : values()) {
         nameMapping.put(sanitize(enumChatFormatting.name), enumChatFormatting);
      }
   }

   public static String formatText(String str) {
      Matcher matcher = formattingCodePattern.matcher(str);
      String str2 = "";

      while (matcher.find()) {
         str2 = matcher.group();
      }

      return str2;
   }
}
