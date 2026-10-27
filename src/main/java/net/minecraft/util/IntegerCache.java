package net.minecraft.util;

public class IntegerCache {
   public static Integer[] CACHE = new Integer[65535];

   public static Integer getInteger(int value) {
      return value >= 0 && value < CACHE.length ? CACHE[value] : new Integer(value);
   }

   static {
      int n = 0;

      for (int length = CACHE.length; n < length; n++) {
         CACHE[n] = n;
      }
   }
}
