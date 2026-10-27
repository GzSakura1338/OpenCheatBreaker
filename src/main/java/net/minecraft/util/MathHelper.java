package net.minecraft.util;

import java.util.Random;
import java.util.UUID;
import net.optifine.util.MathUtils;

public class MathHelper {
   public static float SQRT_2 = sqrt_float(2.0F);
   public static final int SIN_BITS = 12;
   public static final int SIN_MASK = 4095;
   public static final int SIN_COUNT = 4096;
   public static final int SIN_COUNT_D4 = 1024;
   public static float floatValue = MathUtils.roundToFloat(Math.PI);
   public static float PI2 = MathUtils.roundToFloat(Math.PI * 2);
   public static float PId2 = MathUtils.roundToFloat(Math.PI / 2);
   public static float radToIndex = MathUtils.roundToFloat(651.8986469044033);
   public static float deg2Rad = MathUtils.roundToFloat(Math.PI / 180.0);
   public static float[] SIN_TABLE_FAST = new float[4096];
   public static boolean fastMath = false;
   public static float[] SIN_TABLE = new float[65536];
   public static int[] multiplyDeBruijnBitPosition;
   public static double SMALLEST_FRACTION_FREE_DOUBLE;
   public static double[] ARCSINE_TABLE;
   public static double[] COSINE_TABLE;

   public static float sin(float f) {
      return fastMath ? SIN_TABLE_FAST[(int)(f * radToIndex) & 4095] : SIN_TABLE[(int)(f * 10430.378F) & 65535];
   }

   public static float cos(float value) {
      return fastMath ? SIN_TABLE_FAST[(int)(value * radToIndex + 1024.0F) & 4095] : SIN_TABLE[(int)(value * 10430.378F + 16384.0F) & 65535];
   }

   public static float sqrt_float(float value) {
      return (float)Math.sqrt(value);
   }

   public static float sqrt_double(double value) {
      return (float)Math.sqrt(value);
   }

   public static int floor_float(float value) {
      int value2 = (int)value;
      return value < value2 ? value2 - 1 : value2;
   }

   public static int truncateDoubleToInt(double value) {
      return (int)(value + 1024.0) - 1024;
   }

   public static int floor_double(double value) {
      int value2 = (int)value;
      return value < value2 ? value2 - 1 : value2;
   }

   public static long floor_double_long(double value) {
      long value2 = (long)value;
      return value < value2 ? value2 - 1L : value2;
   }

   public static int absFloor(double value) {
      return (int)(value >= 0.0 ? value : -value + 1.0);
   }

   public static float abs(float value) {
      return value >= 0.0F ? value : -value;
   }

   public static int abs_int(int value) {
      return value >= 0 ? value : -value;
   }

   public static int ceiling_float_int(float value) {
      int value2 = (int)value;
      return value > value2 ? value2 + 1 : value2;
   }

   public static int ceiling_double_int(double value) {
      int value2 = (int)value;
      return value > value2 ? value2 + 1 : value2;
   }

   public static int clamp_int(int num, int min, int max) {
      return num < min ? min : (num > max ? max : num);
   }

   public static float clamp_float(float num, float min, float max) {
      return num < min ? min : (num > max ? max : num);
   }

   public static double clamp_double(double num, double min, double max) {
      return num < min ? min : (num > max ? max : num);
   }

   public static double denormalizeClamp(double lowerBnd, double upperBnd, double slide) {
      return slide < 0.0 ? lowerBnd : (slide > 1.0 ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide);
   }

   public static double abs_max(double d, double d2) {
      if (d < 0.0) {
         d = -d;
      }

      if (d2 < 0.0) {
         d2 = -d2;
      }

      return d > d2 ? d : d2;
   }

   public static int bucketInt(int n, int n2) {
      return n < 0 ? -((-n - 1) / n2) - 1 : n / n2;
   }

   public static int getRandomIntegerInRange(Random random, int n, int n2) {
      return n >= n2 ? n : random.nextInt(n2 - n + 1) + n;
   }

   public static float randomFloatClamp(Random random, float f, float f2) {
      return f >= f2 ? f : random.nextFloat() * (f2 - f) + f;
   }

   public static double getRandomDoubleInRange(Random random, double d, double d2) {
      return d >= d2 ? d : random.nextDouble() * (d2 - d) + d;
   }

   public static double average(long[] values) {
      long l = 0L;

      for (long l2 : values) {
         l += l2;
      }

      return (double)l / values.length;
   }

   public static boolean epsilonEquals(float f, float f2) {
      return abs(f2 - f) < 1.0E-5F;
   }

   public static int normalizeAngle(int n, int n2) {
      return (n % n2 + n2) % n2;
   }

   public static float wrapAngleTo180_float(float value) {
      value %= 360.0F;
      if (value >= 180.0F) {
         value -= 360.0F;
      }

      if (value < -180.0F) {
         value += 360.0F;
      }

      return value;
   }

   public static double wrapAngleTo180_double(double value) {
      value %= 360.0;
      if (value >= 180.0) {
         value -= 360.0;
      }

      if (value < -180.0) {
         value += 360.0;
      }

      return value;
   }

   public static int parseIntWithDefault(String str, int n) {
      try {
         return Integer.parseInt(str);
      } catch (Throwable e) {
         return n;
      }
   }

   public static int parseIntWithDefaultAndMax(String str, int n, int n2) {
      return Math.max(n2, parseIntWithDefault(str, n));
   }

   public static double parseDoubleWithDefault(String str, double d) {
      try {
         return Double.parseDouble(str);
      } catch (Throwable e) {
         return d;
      }
   }

   public static double parseDoubleWithDefaultAndMax(String str, double d, double d2) {
      return Math.max(d2, parseDoubleWithDefault(str, d));
   }

   public static int roundUpToPowerOfTwo(int value) {
      int n = value - 1;
      n |= n >> 1;
      n |= n >> 2;
      n |= n >> 4;
      n |= n >> 8;
      n |= n >> 16;
      return n + 1;
   }

   public static boolean isPowerOfTwo(int value) {
      return value != 0 && (value & value - 1) == 0;
   }

   public static int calculateLogBaseTwoDeBruijn(int value) {
      value = isPowerOfTwo(value) ? value : roundUpToPowerOfTwo(value);
      return multiplyDeBruijnBitPosition[(int)(value * 125613361L >> 27) & 31];
   }

   public static int calculateLogBaseTwo(int value) {
      return calculateLogBaseTwoDeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
   }

   public static int roundUp(int n, int n2) {
      if (n2 == 0) {
         return 0;
      }

      if (n == 0) {
         return n2;
      }

      if (n < 0) {
         n2 *= -1;
      }

      int n3 = n % n2;
      return n3 == 0 ? n : n + n2 - n3;
   }

   public static int packRgb(float f, float f2, float f3) {
      return packRgb(floor_float(f * 255.0F), floor_float(f2 * 255.0F), floor_float(f3 * 255.0F));
   }

   public static int packRgb(int n, int n2, int n3) {
      int n4 = (n << 8) + n2;
      return (n4 << 8) + n3;
   }

   public static int multiplyColors(int n, int n2) {
      int n3 = (n & 0xFF0000) >> 16;
      int n4 = (n2 & 0xFF0000) >> 16;
      int n5 = (n & 0xFF00) >> 8;
      int n6 = (n2 & 0xFF00) >> 8;
      int n7 = (n & 0xFF) >> 0;
      int n8 = (n2 & 0xFF) >> 0;
      int n9 = (int)((float)n3 * n4 / 255.0F);
      int n10 = (int)((float)n5 * n6 / 255.0F);
      int n11 = (int)((float)n7 * n8 / 255.0F);
      return n & 0xFF000000 | n9 << 16 | n10 << 8 | n11;
   }

   public static double fractionalPart(double d) {
      return d - Math.floor(d);
   }

   public static long getPositionRandom(Vec3i pos) {
      return getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ());
   }

   public static long getCoordinateRandom(int x, int y, int z) {
      long l = x * 3129871 ^ z * 116129781L ^ y;
      return l * l * 42317861L + l * 11L;
   }

   public static UUID getRandomUuid(Random rand) {
      long l = rand.nextLong() & -61441L | 16384L;
      long l2 = rand.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
      return new UUID(l, l2);
   }

   public static double minusDiv(double d, double d2, double d3) {
      return (d - d2) / (d3 - d2);
   }

   public static double atan2(double d, double d2) {
      double d3 = d2 * d2 + d * d;
      if (Double.isNaN(d3)) {
         return Double.NaN;
      }

      boolean flag = d < 0.0;
      if (flag) {
         d = -d;
      }

      boolean flag2 = d2 < 0.0;
      if (flag2) {
         d2 = -d2;
      }

      boolean flag3 = d > d2;
      if (flag3) {
         double d4 = d2;
         d2 = d;
         d = d4;
      }

      double fastInverseSqrt2 = fastInverseSqrt(d3);
      d2 *= fastInverseSqrt2;
      d *= fastInverseSqrt2;
      double d4 = SMALLEST_FRACTION_FREE_DOUBLE + d;
      int doubleToRawLongBits = (int)Double.doubleToRawLongBits(d4);
      double d5 = ARCSINE_TABLE[doubleToRawLongBits];
      double d6 = COSINE_TABLE[doubleToRawLongBits];
      double d7 = d4 - SMALLEST_FRACTION_FREE_DOUBLE;
      double d8 = d * d6 - d2 * d7;
      double d9 = (6.0 + d8 * d8) * d8 * 0.16666666666666666;
      double d10 = d5 + d9;
      if (flag3) {
         d10 = (Math.PI / 2) - d10;
      }

      if (flag2) {
         d10 = Math.PI - d10;
      }

      if (flag) {
         d10 = -d10;
      }

      return d10;
   }

   public static double fastInverseSqrt(double d) {
      double d2 = 0.5 * d;
      long doubleToRawLongBits = Double.doubleToRawLongBits(d);
      doubleToRawLongBits = 6910469410427058090L - (doubleToRawLongBits >> 1);
      d = Double.longBitsToDouble(doubleToRawLongBits);
      return d * (1.5 - d2 * d * d);
   }

   public static int hsvToRGB(float f, float f2, float f3) {
      int n = (int)(f * 6.0F) % 6;
      float f4 = f * 6.0F - n;
      float f5 = f3 * (1.0F - f2);
      float f6 = f3 * (1.0F - f4 * f2);
      float f7 = f3 * (1.0F - (1.0F - f4) * f2);
      float f8;
      float f9;
      float f10;
      switch (n) {
         case 0:
            f8 = f3;
            f9 = f7;
            f10 = f5;
            break;
         case 1:
            f8 = f6;
            f9 = f3;
            f10 = f5;
            break;
         case 2:
            f8 = f5;
            f9 = f3;
            f10 = f7;
            break;
         case 3:
            f8 = f5;
            f9 = f6;
            f10 = f3;
            break;
         case 4:
            f8 = f7;
            f9 = f5;
            f10 = f3;
            break;
         case 5:
            f8 = f3;
            f9 = f5;
            f10 = f6;
            break;
         default:
            throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + f + ", " + f2 + ", " + f3);
      }

      int n2 = clamp_int((int)(f8 * 255.0F), 0, 255);
      int n3 = clamp_int((int)(f9 * 255.0F), 0, 255);
      int n4 = clamp_int((int)(f10 * 255.0F), 0, 255);
      return n2 << 16 | n3 << 8 | n4;
   }

   static {
      for (int i = 0; i < 65536; i++) {
         SIN_TABLE[i] = (float)Math.sin(i * Math.PI * 2.0 / 65536.0);
      }

      for (int i = 0; i < SIN_TABLE_FAST.length; i++) {
         SIN_TABLE_FAST[i] = MathUtils.roundToFloat(Math.sin(i * Math.PI * 2.0 / 4096.0));
      }

      multiplyDeBruijnBitPosition = new int[]{
         0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
      };
      SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
      ARCSINE_TABLE = new double[257];
      COSINE_TABLE = new double[257];

      for (int i = 0; i < 257; i++) {
         double d = i / 256.0;
         double asin = Math.asin(d);
         COSINE_TABLE[i] = Math.cos(asin);
         ARCSINE_TABLE[i] = asin;
      }
   }
}
