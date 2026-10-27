package com.cheatbreaker.client.mod.impl;

public class PotionCounterFormat {
   public static String formatText(float f, String str) {
      return createStringBuilder(f, str, false);
   }

   public static String createStringBuilder(float f, String str, boolean flag) {
      return !(f < 1.0F) && !(f > 1.0F) ? str : str + (flag ? "S" : "s");
   }
}
