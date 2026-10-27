package net.minecraft.util;

import java.util.regex.Pattern;

public class StringUtils {
   public static Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

   public static String ticksToElapsedTime(int ticks) {
      int n = ticks / 20;
      int n2 = n / 60;
      n %= 60;
      return n < 10 ? n2 + ":0" + n : n2 + ":" + n;
   }

   public static String stripControlCodes(String text) {
      return patternControlCode.matcher(text).replaceAll("");
   }

   public static boolean isNullOrEmpty(String string) {
      return org.apache.commons.lang3.StringUtils.isEmpty(string);
   }
}
