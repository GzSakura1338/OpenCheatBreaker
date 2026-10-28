package net.minecraft.util;

public class StatCollector {
    public static StringTranslate localizedName = StringTranslate.getInstance();
    public static StringTranslate fallbackTranslator = new StringTranslate();

    public static String translateToLocal(String str) {
        return localizedName.translateKey(str);
    }

    public static String translateToLocalFormatted(String str, Object... objs) {
        return localizedName.translateKeyFormat(str, objs);
    }

    public static String translateToFallback(String str) {
        return fallbackTranslator.translateKey(str);
    }

    public static boolean canTranslate(String str) {
        return localizedName.isKeyTranslated(str);
    }

    public static long getLastTranslationUpdateTimeInMilliseconds() {
        return localizedName.getLastUpdateTimeInMilliseconds();
    }
}
