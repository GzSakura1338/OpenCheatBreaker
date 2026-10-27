package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale {
   public static Splitter splitter = Splitter.on('=').limit(2);
   public static Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   public Map<String, String> properties = Maps.newHashMap();
   public boolean unicode;

   public synchronized void loadLocaleDataFiles(IResourceManager resourceManager, List<String> languageList) {
      this.properties.clear();

      for (String str : languageList) {
         String format = String.format("lang/%s.lang", str);

         for (String str2 : resourceManager.getResourceDomains()) {
            try {
               this.loadLocaleData(resourceManager.getAllResources(new ResourceLocation(str2, format)));
            } catch (IOException e) {
            }
         }
      }

      this.checkUnicode();
   }

   public boolean isUnicode() {
      return this.unicode;
   }

   public void checkUnicode() {
      this.unicode = false;
      int n = 0;
      int n2 = 0;

      for (String str : this.properties.values()) {
         int n3 = str.length();
         n2 += n3;

         for (int i = 0; i < n3; i++) {
            if (str.charAt(i) >= 256) {
               n++;
            }
         }
      }

      float f = (float)n / n2;
      this.unicode = f > 0.1;
   }

   public void loadLocaleData(List<IResource> resourcesList) throws IOException {
      for (IResource resource : resourcesList) {
         InputStream inputStream = resource.getInputStream();

         try {
            this.loadLocaleData(inputStream);
         } finally {
            IOUtils.closeQuietly(inputStream);
         }
      }
   }

   public void loadLocaleData(InputStream inputStreamIn) throws IOException {
      for (String str : IOUtils.readLines(inputStreamIn, Charsets.UTF_8)) {
         if (!str.isEmpty() && str.charAt(0) != '#') {
            String[] toArray = (String[])Iterables.toArray(splitter.split(str), String.class);
            if (toArray != null && toArray.length == 2) {
               String str2 = toArray[0];
               String replaceAll = pattern.matcher(toArray[1]).replaceAll("%$1s");
               this.properties.put(str2, replaceAll);
            }
         }
      }
   }

   public String translateKeyPrivate(String translateKey) {
      String str = this.properties.get(translateKey);
      return str == null ? translateKey : str;
   }

   public String formatMessage(String translateKey, Object[] parameters) {
      String translateKeyPrivate = this.translateKeyPrivate(translateKey);

      try {
         return String.format(translateKeyPrivate, parameters);
      } catch (IllegalFormatException e) {
         return "Format error: " + translateKeyPrivate;
      }
   }
}
