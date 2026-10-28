package net.minecraft.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum EnumChatFormatting {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    OBFUSCATED('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r');

    public static Map formattingCodeMapping = new HashMap();
    public static Map nameMapping = new HashMap();
    public char formattingCode;
    public boolean fancyStyling;
    public String controlString;
    public static Pattern pattern = Pattern.compile("(?i)§[0-9A-FK-OR]");
    public static Pattern pattern2 = Pattern.compile("[0-9A-FK-OR]§(?i)");
    public static Pattern formattingCodePattern = Pattern.compile("(?i)§[0-9A-FR]");

    EnumChatFormatting(char c) {
        this(c, false);
    }

    EnumChatFormatting(char c, boolean flag) {
        this.formattingCode = c;
        this.fancyStyling = flag;
        this.controlString = "§" + c;
    }

    public char getFormattingCode() {
        return this.formattingCode;
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

    public static String getTextWithoutFormattingCodes(String str) {
        return str == null ? null : pattern.matcher(str).replaceAll("");
    }

    public static EnumChatFormatting getValueByName(String str) {
        return str == null ? null : (EnumChatFormatting)nameMapping.get(str.toLowerCase());
    }

    public static Collection getValidValues(boolean flag, boolean flag2) {
        ArrayList list = new ArrayList();

        for (EnumChatFormatting enumChatFormatting : values()) {
            if ((!enumChatFormatting.isColor() || flag) && (!enumChatFormatting.isFancyStyling() || flag2)) {
                list.add(enumChatFormatting.getFriendlyName());
            }
        }

        return list;
    }

    static {
        for (EnumChatFormatting enumChatFormatting : values()) {
            formattingCodeMapping.put(enumChatFormatting.getFormattingCode(), enumChatFormatting);
            nameMapping.put(enumChatFormatting.getFriendlyName(), enumChatFormatting);
        }
    }

    public static String formatText(String str) {
        return str == null ? null : pattern.matcher(str).replaceAll("");
    }

    public static String formatText(Color color) {
        Matcher matcher = formattingCodePattern.matcher((CharSequence)color);
        String str = "";

        while (matcher.find()) {
            str = matcher.group();
        }

        return str;
    }

    public static String getLastFormatting(String str) {
        Matcher matcher = formattingCodePattern.matcher(str);
        String str2 = "";

        while (matcher.find()) {
            str2 = matcher.group();
        }

        return str2;
    }
}
