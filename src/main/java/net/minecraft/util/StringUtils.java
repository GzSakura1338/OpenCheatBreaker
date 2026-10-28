package net.minecraft.util;

import java.util.regex.Pattern;

public class StringUtils {
    public static Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    public static String ticksToElapsedTime(int n) {
        int n2 = n / 20;
        int n3 = n2 / 60;
        n2 %= 60;
        return n2 < 10 ? n3 + ":0" + n2 : n3 + ":" + n2;
    }

    public static String stripControlCodes(String str) {
        return patternControlCode.matcher(str).replaceAll("");
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str);
    }
}
