package net.optifine.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.optifine.Config;

public class EntityUtils {
   public static Map<Class, Integer> mapIdByClass = new HashMap<>();
   public static Map<String, Integer> mapIdByName = new HashMap<>();
   public static Map<String, Class> mapClassByName = new HashMap<>();

   public static int getEntityIdByClass(Entity entity) {
      return entity == null ? -1 : getEntityIdByClass(entity.getClass());
   }

   public static int getEntityIdByClass(Class clazz) {
      Integer integer = mapIdByClass.get(clazz);
      return integer == null ? -1 : integer;
   }

   public static int getEntityIdByName(String str) {
      Integer integer = mapIdByName.get(str);
      return integer == null ? -1 : integer;
   }

   public static Class getEntityClassByName(String str) {
      return mapClassByName.get(str);
   }

   static {
      for (int i = 0; i < 1000; i++) {
         Class classFromID = EntityList.getClassFromID(i);
         if (classFromID != null) {
            String stringFromID = EntityList.getStringFromID(i);
            if (stringFromID != null) {
               if (mapIdByClass.containsKey(classFromID)) {
                  Config.warn("Duplicate entity class: " + classFromID + ", id1: " + mapIdByClass.get(classFromID) + ", id2: " + i);
               }

               if (mapIdByName.containsKey(stringFromID)) {
                  Config.warn("Duplicate entity name: " + stringFromID + ", id1: " + mapIdByName.get(stringFromID) + ", id2: " + i);
               }

               if (mapClassByName.containsKey(stringFromID)) {
                  Config.warn("Duplicate entity name: " + stringFromID + ", class1: " + mapClassByName.get(stringFromID) + ", class2: " + classFromID);
               }

               mapIdByClass.put(classFromID, i);
               mapIdByName.put(stringFromID, i);
               mapClassByName.put(stringFromID, classFromID);
            }
         }
      }
   }
}
