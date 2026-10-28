package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class StringTranslate {
    public static Pattern numericVariablePattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    public static Splitter equalSignSplitter = Splitter.on('=').limit(2);
    public static StringTranslate instance = new StringTranslate();
    public Map<String, String> languageList = Maps.newHashMap();
    public long lastUpdateTimeInMilliseconds;

    public StringTranslate() {
        try {
            InputStream resourceAsStream = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");
            for (String str : IOUtils.readLines(resourceAsStream, Charsets.UTF_8)) {
                String[] strs;
                if (!str.isEmpty()
                    && str.charAt(0) != '#'
                    && (strs = (String[])Iterables.toArray(equalSignSplitter.split(str), String.class)) != null
                    && strs.length == 2) {
                    String str2 = strs[0];
                    String replaceAll = numericVariablePattern.matcher(strs[1]).replaceAll("%$1s");
                    this.languageList.put(str2, replaceAll);
                }
            }

            this.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
        } catch (UncheckedIOException e) {
        }
    }

    public static StringTranslate getInstance() {
        return instance;
    }

    public static synchronized void replaceWith(Map map) {
        instance.languageList.clear();
        instance.languageList.putAll(map);
        instance.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
    }

    public synchronized String translateKey(String str) {
        return this.tryTranslateKey(str);
    }

    public synchronized String translateKeyFormat(String str, Object... objs) {
        String tryTranslateKey = this.tryTranslateKey(str);

        try {
            return String.format(tryTranslateKey, objs);
        } catch (IllegalFormatException e) {
            return "Format error: " + tryTranslateKey;
        }
    }

    public String tryTranslateKey(String str) {
        String str2 = this.languageList.get(str);
        return str2 == null ? str : str2;
    }

    public synchronized boolean isKeyTranslated(String str) {
        return this.languageList.containsKey(str);
    }

    public long getLastUpdateTimeInMilliseconds() {
        return this.lastUpdateTimeInMilliseconds;
    }
}
