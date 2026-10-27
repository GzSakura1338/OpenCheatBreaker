package net.optifine.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;

public class FontUtils {
   public static Properties readFontProperties(ResourceLocation resourceLocation) {
      String resourcePath = resourceLocation.getResourcePath();
      PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
      String str = ".png";
      if (!resourcePath.endsWith(str)) {
         return propertiesOrdered;
      }

      String str2 = resourcePath.substring(0, resourcePath.length() - str.length()) + ".properties";

      try {
         ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getResourceDomain(), str2);
         InputStream resourceStream = Config.getResourceStream(Config.getResourceManager(), resourceLocation2);
         if (resourceStream == null) {
            return propertiesOrdered;
         }

         Config.log("Loading " + str2);
         propertiesOrdered.load(resourceStream);
         resourceStream.close();
      } catch (FileNotFoundException e) {
      } catch (IOException e) {
         e.printStackTrace();
      }

      return propertiesOrdered;
   }

   public static void readCustomCharWidths(Properties properties, float[] floats) {
      for (Object obj : properties.keySet()) {
         String str = (String)obj;
         String str2 = "width.";
         if (str.startsWith(str2)) {
            String substring = str.substring(str2.length());
            int parseInt = Config.parseInt(substring, -1);
            if (parseInt >= 0 && parseInt < floats.length) {
               String property = properties.getProperty(str);
               float parseFloat = Config.parseFloat(property, -1.0F);
               if (parseFloat >= 0.0F) {
                  floats[parseInt] = parseFloat;
               }
            }
         }
      }
   }

   public static float readFloat(Properties properties, String str, float f) {
      String property = properties.getProperty(str);
      if (property == null) {
         return f;
      } else {
         float parseFloat = Config.parseFloat(property, Float.MIN_VALUE);
         if (parseFloat == Float.MIN_VALUE) {
            Config.warn("Invalid value for " + str + ": " + property);
            return f;
         } else {
            return parseFloat;
         }
      }
   }

   public static boolean readBoolean(Properties properties, String str, boolean flag) {
      String property = properties.getProperty(str);
      if (property == null) {
         return flag;
      } else {
         String trim = property.toLowerCase().trim();
         if (trim.equals("true") || trim.equals("on")) {
            return true;
         } else if (!trim.equals("false") && !trim.equals("off")) {
            Config.warn("Invalid value for " + str + ": " + property);
            return flag;
         } else {
            return false;
         }
      }
   }

   public static ResourceLocation getHdFontLocation(ResourceLocation resourceLocation) {
      if (!Config.isCustomFonts()) {
         return resourceLocation;
      }

      if (resourceLocation == null) {
         return resourceLocation;
      }

      if (!Config.isMinecraftThread()) {
         return resourceLocation;
      }

      String resourcePath = resourceLocation.getResourcePath();
      String str = "textures/";
      String str2 = "mcpatcher/";
      if (!resourcePath.startsWith(str)) {
         return resourceLocation;
      }

      resourcePath = resourcePath.substring(str.length());
      resourcePath = str2 + resourcePath;
      ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getResourceDomain(), resourcePath);
      return Config.hasResource(Config.getResourceManager(), resourceLocation2) ? resourceLocation2 : resourceLocation;
   }
}
