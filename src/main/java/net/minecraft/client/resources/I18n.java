package net.minecraft.client.resources;

import java.util.Map;

public class I18n {
    public static Locale i18nLocale;

    public static void setLocale(Locale locale) {
        i18nLocale = locale;
    }

    public static String format(String str, Object... objs) {
        return i18nLocale.formatMessage(str, objs);
    }

    public static Map getLocaleProperties() {
        return i18nLocale.translations;
    }
}
