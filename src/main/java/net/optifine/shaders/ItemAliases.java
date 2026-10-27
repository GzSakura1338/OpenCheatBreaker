package net.optifine.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.config.ConnectedParser;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;

public class ItemAliases {
   public static int[] itemAliases = null;
   public static boolean updateOnResourcesReloaded;
   public static final int NO_ALIAS = Integer.MIN_VALUE;

   public static int getItemAliasId(int n) {
      if (itemAliases == null) {
         return n;
      } else if (n >= 0 && n < itemAliases.length) {
         int n2 = itemAliases[n];
         return n2 == Integer.MIN_VALUE ? n : n2;
      } else {
         return n;
      }
   }

   public static void resourcesReloaded() {
      if (updateOnResourcesReloaded) {
         updateOnResourcesReloaded = false;
         update(Shaders.getShaderPack());
      }
   }

   public static void update(IShaderPack shaderPack) {
      reset();
      if (shaderPack != null) {
         if (Reflector.Loader_getActiveModList.exists() && Config.getResourceManager() == null) {
            Config.dbg("[Shaders] Delayed loading of item mappings after resources are loaded");
            updateOnResourcesReloaded = true;
         } else {
            ArrayList list = new ArrayList();
            String str = "/shaders/item.properties";
            InputStream resourceAsStream = shaderPack.getResourceAsStream(str);
            if (resourceAsStream != null) {
               loadItemAliases(resourceAsStream, str, list);
            }

            loadModItemAliases(list);
            if (list.size() > 0) {
               itemAliases = toArray(list);
            }
         }
      }
   }

   public static void loadModItemAliases(List<Integer> list) {
      String[] forgeModIds = ReflectorForge.getForgeModIds();

      for (int i = 0; i < forgeModIds.length; i++) {
         String str = forgeModIds[i];

         try {
            ResourceLocation resourceLocation = new ResourceLocation(str, "shaders/item.properties");
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            loadItemAliases(resourceStream, resourceLocation.toString(), list);
         } catch (IOException e) {
         }
      }
   }

   public static void loadItemAliases(InputStream inputStream, String str, List<Integer> list) {
      if (inputStream != null) {
         try {
            inputStream = MacroProcessor.process(inputStream, str);
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(inputStream);
            inputStream.close();
            Config.dbg("[Shaders] Parsing item mappings: " + str);
            ConnectedParser connectedParser = new ConnectedParser("Shaders");

            for (Object obj : propertiesOrdered.keySet()) {
               String str2 = (String)obj;
               String property = propertiesOrdered.getProperty(str2);
               String str3 = "item.";
               if (!str2.startsWith(str3)) {
                  Config.warn("[Shaders] Invalid item ID: " + str2);
               } else {
                  String removePrefix = StrUtils.removePrefix(str2, str3);
                  int parseInt = Config.parseInt(removePrefix, -1);
                  if (parseInt < 0) {
                     Config.warn("[Shaders] Invalid item alias ID: " + parseInt);
                  } else {
                     int[] parseItems = connectedParser.parseItems(property);
                     if (parseItems != null && parseItems.length >= 1) {
                        for (int i = 0; i < parseItems.length; i++) {
                           int n = parseItems[i];
                           addToList(list, n, parseInt);
                        }
                     } else {
                        Config.warn("[Shaders] Invalid item ID mapping: " + str2 + "=" + property);
                     }
                  }
               }
            }
         } catch (IOException e) {
            Config.warn("[Shaders] Error reading: " + str);
         }
      }
   }

   public static void addToList(List<Integer> list, int n, int n2) {
      while (list.size() <= n) {
         list.add(Integer.MIN_VALUE);
      }

      list.set(n, n2);
   }

   public static int[] toArray(List<Integer> list) {
      int[] ints = new int[list.size()];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = (Integer)list.get(i);
      }

      return ints;
   }

   public static void reset() {
      itemAliases = null;
   }
}
