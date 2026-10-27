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

public class EntityAliases {
   public static int[] entityAliases = null;
   public static boolean updateOnResourcesReloaded;

   public static int getEntityAliasId(int n) {
      if (entityAliases == null) {
         return -1;
      } else {
         return n >= 0 && n < entityAliases.length ? entityAliases[n] : -1;
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
            Config.dbg("[Shaders] Delayed loading of entity mappings after resources are loaded");
            updateOnResourcesReloaded = true;
         } else {
            ArrayList list = new ArrayList();
            String str = "/shaders/entity.properties";
            InputStream resourceAsStream = shaderPack.getResourceAsStream(str);
            if (resourceAsStream != null) {
               loadEntityAliases(resourceAsStream, str, list);
            }

            loadModEntityAliases(list);
            if (list.size() > 0) {
               entityAliases = toArray(list);
            }
         }
      }
   }

   public static void loadModEntityAliases(List<Integer> list) {
      String[] forgeModIds = ReflectorForge.getForgeModIds();

      for (int i = 0; i < forgeModIds.length; i++) {
         String str = forgeModIds[i];

         try {
            ResourceLocation resourceLocation = new ResourceLocation(str, "shaders/entity.properties");
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            loadEntityAliases(resourceStream, resourceLocation.toString(), list);
         } catch (IOException e) {
         }
      }
   }

   public static void loadEntityAliases(InputStream inputStream, String str, List<Integer> list) {
      if (inputStream != null) {
         try {
            inputStream = MacroProcessor.process(inputStream, str);
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(inputStream);
            inputStream.close();
            Config.dbg("[Shaders] Parsing entity mappings: " + str);
            ConnectedParser connectedParser = new ConnectedParser("Shaders");

            for (Object obj : propertiesOrdered.keySet()) {
               String str2 = (String)obj;
               String property = propertiesOrdered.getProperty(str2);
               String str3 = "entity.";
               if (!str2.startsWith(str3)) {
                  Config.warn("[Shaders] Invalid entity ID: " + str2);
               } else {
                  String removePrefix = StrUtils.removePrefix(str2, str3);
                  int parseInt = Config.parseInt(removePrefix, -1);
                  if (parseInt < 0) {
                     Config.warn("[Shaders] Invalid entity alias ID: " + parseInt);
                  } else {
                     int[] parseEntities = connectedParser.parseEntities(property);
                     if (parseEntities != null && parseEntities.length >= 1) {
                        for (int i = 0; i < parseEntities.length; i++) {
                           int n = parseEntities[i];
                           addToList(list, n, parseInt);
                        }
                     } else {
                        Config.warn("[Shaders] Invalid entity ID mapping: " + str2 + "=" + property);
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
         list.add(-1);
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
      entityAliases = null;
   }
}
