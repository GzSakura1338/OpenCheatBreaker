package com.cheatbreaker.client.cosmetic;

import java.util.HashMap;
import java.util.Map;

public enum CosmeticType {
   WINGS("dragon_wings"),
   CAPE("cape"),
   HAT("hat"),
   EMOTE("emote"),
   ICON("icon"),
   SUBICON("subicon");

   public String text;
   public static Map<String, CosmeticType> stringToCosmeticTypeMap = new HashMap<>();

   public static CosmeticType get(String str) {
      return stringToCosmeticTypeMap.get(str);
   }

   public String getText() {
      return this.text;
   }

   CosmeticType(String str) {
      this.text = str;
   }

   static {
      for (CosmeticType cosmeticType : values()) {
         stringToCosmeticTypeMap.put(cosmeticType.text, cosmeticType);
      }
   }
}
