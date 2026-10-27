package com.cheatbreaker.client.minimap;

import java.util.ArrayList;

public class CustomMobs {
   public static ArrayList<CustomMob> customMobList = new ArrayList<>();

   public static void handleString(String str, boolean flag) {
      CustomMob findCustomMob2 = findCustomMob(str);
      if (findCustomMob2 != null) {
         findCustomMob2.enabled = flag;
      } else {
         customMobList.add(new CustomMob(str, flag));
      }
   }

   public static CustomMob findCustomMob(String str) {
      for (CustomMob customMob : customMobList) {
         if (customMob.name.equals(str)) {
            return customMob;
         }
      }

      return null;
   }

   public static void handleString(String str, boolean flag, boolean flag2) {
      CustomMob findCustomMob2 = findCustomMob(str);
      if (findCustomMob2 != null) {
         findCustomMob2.hostile = flag;
         findCustomMob2.neutral = flag2;
      } else {
         customMobList.add(new CustomMob(str, flag, flag2));
      }
   }
}
