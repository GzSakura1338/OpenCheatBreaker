package net.optifine;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Lang {
   public static Splitter splitter = Splitter.on('=').limit(2);
   public static Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

   public static void resourcesReloaded() {
      Map localeProperties = I18n.getLocaleProperties();
      ArrayList list = new ArrayList();
      String str = "optifine/lang/";
      String str2 = "en_US";
      String str3 = ".lang";
      list.add(str + str2 + str3);
      if (!Config.getGameSettings().language.equals(str2)) {
         list.add(str + Config.getGameSettings().language + str3);
      }

      String[] toArray = (String[])list.toArray(new String[list.size()]);
      loadResources(Config.getDefaultResourcePack(), toArray, localeProperties);
      IResourcePack[] resourcePacks = Config.getResourcePacks();

      for (int i = 0; i < resourcePacks.length; i++) {
         IResourcePack resourcePack = resourcePacks[i];
         loadResources(resourcePack, toArray, localeProperties);
      }
   }

   public static void loadResources(IResourcePack resourcePack, String[] strs, Map map) {
      try {
         for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            ResourceLocation resourceLocation = new ResourceLocation(str);
            if (resourcePack.resourceExists(resourceLocation)) {
               InputStream inputStream = resourcePack.getInputStream(resourceLocation);
               if (inputStream != null) {
                  loadLocaleData(inputStream, map);
               }
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void loadLocaleData(InputStream inputStream, Map map) throws IOException {
      Iterator iterator = IOUtils.readLines(inputStream, Charsets.UTF_8).iterator();
      inputStream.close();

      while (iterator.hasNext()) {
         String str = (String)iterator.next();
         if (!str.isEmpty() && str.charAt(0) != '#') {
            String[] toArray = (String[])Iterables.toArray(splitter.split(str), String.class);
            if (toArray != null && toArray.length == 2) {
               String str2 = toArray[0];
               String replaceAll = pattern.matcher(toArray[1]).replaceAll("%$1s");
               map.put(str2, replaceAll);
            }
         }
      }
   }

   public static String get(String str) {
      return I18n.format(str);
   }

   public static String get(String str, String str2) {
      String format = I18n.format(str);
      return format != null && !format.equals(str) ? format : str2;
   }

   public static String getOn() {
      return I18n.format("options.on");
   }

   public static String getOff() {
      return I18n.format("options.off");
   }

   public static String getFast() {
      return I18n.format("options.graphics.fast");
   }

   public static String getFancy() {
      return I18n.format("options.graphics.fancy");
   }

   public static String getDefault() {
      return I18n.format("generator.default");
   }
}
