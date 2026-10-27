package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.MathUtils;
import net.optifine.util.PropertiesOrdered;

public class CustomPanorama {
   public static CustomPanoramaProperties customPanoramaProperties = null;
   public static Random random = new Random();

   public static CustomPanoramaProperties getCustomPanoramaProperties() {
      return customPanoramaProperties;
   }

   public static void update() {
      customPanoramaProperties = null;
      String[] panoramaFolders = getPanoramaFolders();
      if (panoramaFolders.length > 1) {
         Properties[] panoramaProperties = getPanoramaProperties(panoramaFolders);
         int[] weights = getWeights(panoramaProperties);
         int randomIndex = getRandomIndex(weights);
         String str = panoramaFolders[randomIndex];
         Properties properties = panoramaProperties[randomIndex];
         if (properties == null) {
            properties = panoramaProperties[0];
         }

         if (properties == null) {
            properties = new PropertiesOrdered();
         }

         CustomPanoramaProperties customPanoramaProperties2 = new CustomPanoramaProperties(str, properties);
         customPanoramaProperties = customPanoramaProperties2;
      }
   }

   public static String[] getPanoramaFolders() {
      ArrayList list = new ArrayList();
      list.add("textures/gui/title/background");

      for (int i = 0; i < 100; i++) {
         String str = "optifine/gui/background" + i;
         String str2 = str + "/panorama_0.png";
         ResourceLocation resourceLocation = new ResourceLocation(str2);
         if (Config.hasResource(resourceLocation)) {
            list.add(str);
         }
      }

      return (String[])list.toArray(new String[list.size()]);
   }

   public static Properties[] getPanoramaProperties(String[] strs) {
      Properties[] propertieses = new Properties[strs.length];

      for (int i = 0; i < strs.length; i++) {
         String str = strs[i];
         if (i == 0) {
            str = "optifine/gui";
         } else {
            Config.dbg("CustomPanorama: " + str);
         }

         ResourceLocation resourceLocation = new ResourceLocation(str + "/background.properties");

         try {
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            if (resourceStream != null) {
               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(resourceStream);
               Config.dbg("CustomPanorama: " + resourceLocation.getResourcePath());
               propertieses[i] = propertiesOrdered;
               resourceStream.close();
            }
         } catch (IOException e) {
         }
      }

      return propertieses;
   }

   public static int[] getWeights(Properties[] propertieses) {
      int[] ints = new int[propertieses.length];

      for (int i = 0; i < ints.length; i++) {
         Properties properties = propertieses[i];
         if (properties == null) {
            properties = propertieses[0];
         }

         if (properties == null) {
            ints[i] = 1;
         } else {
            String property = properties.getProperty("weight", (String)null);
            ints[i] = Config.parseInt(property, 1);
         }
      }

      return ints;
   }

   public static int getRandomIndex(int[] ints) {
      int sum = MathUtils.getSum(ints);
      int nextInt = random.nextInt(sum);
      int n = 0;

      for (int i = 0; i < ints.length; i++) {
         n += ints[i];
         if (n > nextInt) {
            return i;
         }
      }

      return ints.length - 1;
   }
}
