package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
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
            if (!str.isEmpty() && str.charAt(0) != '#') {
               String[] toArray = (String[])Iterables.toArray(equalSignSplitter.split(str), String.class);
               if (toArray != null && toArray.length == 2) {
                  String str2 = toArray[0];
                  String replaceAll = numericVariablePattern.matcher(toArray[1]).replaceAll("%$1s");
                  this.languageList.put(str2, replaceAll);
               }
            }
         }

         this.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
      } catch (UncheckedIOException e) {
      }
   }

   public static StringTranslate getInstance() {
      return instance;
   }

   public static synchronized void replaceWith(Map<String, String> map) {
      instance.languageList.clear();
      instance.languageList.putAll(map);
      instance.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
   }

   public synchronized String translateKey(String key) {
      return this.tryTranslateKey(key);
   }

   public synchronized String translateKeyFormat(String key, Object... format) {
      String tryTranslateKey = this.tryTranslateKey(key);

      try {
         return String.format(tryTranslateKey, format);
      } catch (IllegalFormatException e) {
         return "Format error: " + tryTranslateKey;
      }
   }

   public String tryTranslateKey(String key) {
      String str = this.languageList.get(key);
      return str == null ? key : str;
   }

   public synchronized boolean isKeyTranslated(String key) {
      return this.languageList.containsKey(key);
   }

   public long getLastUpdateTimeInMilliseconds() {
      return this.lastUpdateTimeInMilliseconds;
   }
}
