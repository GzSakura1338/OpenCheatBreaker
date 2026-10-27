package com.cheatbreaker.client.mod;

public enum HudElementType {
   ARMOR,
   EFFECT,
   ITEM;

   public static HudElementType fromName(String str) {
      return Enum.valueOf(HudElementType.class, str);
   }
}
