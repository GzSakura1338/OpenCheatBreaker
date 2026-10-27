package net.optifine.util;

import net.minecraft.util.MathHelper;

public class MathUtils {
   public static final float CONSTANT_3_1415927 = (float) Math.PI;
   public static final float PI2 = (float) (Math.PI * 2);
   public static final float PId2 = (float) (Math.PI / 2);
   public static float[] ASIN_TABLE = new float[65536];

   public static float asin(float f) {
      return ASIN_TABLE[(int)((f + 1.0F) * 32767.5) & 65535];
   }

   public static float acos(float f) {
      return (float) (Math.PI / 2) - ASIN_TABLE[(int)((f + 1.0F) * 32767.5) & 65535];
   }

   public static int getAverage(int[] ints) {
      if (ints.length <= 0) {
         return 0;
      }

      int sum = getSum(ints);
      return sum / ints.length;
   }

   public static int getSum(int[] ints) {
      if (ints.length <= 0) {
         return 0;
      }

      int n = 0;

      for (int i = 0; i < ints.length; i++) {
         int n2 = ints[i];
         n += n2;
      }

      return n;
   }

   public static int roundDownToPowerOfTwo(int n) {
      int roundUpToPowerOfTwo = MathHelper.roundUpToPowerOfTwo(n);
      return n == roundUpToPowerOfTwo ? roundUpToPowerOfTwo : roundUpToPowerOfTwo / 2;
   }

   public static boolean equalsDelta(float f, float f2, float f3) {
      return Math.abs(f - f2) <= f3;
   }

   public static float toDeg(float f) {
      return f * 180.0F / MathHelper.floatValue;
   }

   public static float toRad(float f) {
      return f / 180.0F * MathHelper.floatValue;
   }

   public static float roundToFloat(double d) {
      return (float)(Math.round(d * 1.0E8) / 1.0E8);
   }

   static {
      for (int i = 0; i < 65536; i++) {
         ASIN_TABLE[i] = (float)Math.asin(i / 32767.5 - 1.0);
      }

      for (int i = -1; i < 2; i++) {
         ASIN_TABLE[(int)((i + 1.0) * 32767.5) & 65535] = (float)Math.asin(i);
      }
   }
}
