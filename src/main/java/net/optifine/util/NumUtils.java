package net.optifine.util;

public class NumUtils {
   public static float limit(float f, float f2, float f3) {
      return f < f2 ? f2 : (f > f3 ? f3 : f);
   }

   public static int mod(int n, int n2) {
      int n3 = n % n2;
      if (n3 < 0) {
         n3 += n2;
      }

      return n3;
   }
}
