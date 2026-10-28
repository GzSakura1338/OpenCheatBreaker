package net.minecraft.util;

import java.util.Random;

public class MathHelper {
    public static final int SIN_BITS = 12;
    public static final int SIN_MASK = 4095;
    public static final int SIN_COUNT = 4096;
    public static final float CONSTANT_3_1415927 = (float) Math.PI;
    public static final float PId2 = (float) (Math.PI / 2);
    public static final float degFull = 360.0F;
    public static final float radToIndex = 651.8986F;
    public static final float degToIndex = 11.377778F;
    public static final float deg2Rad = (float) (Math.PI / 180.0);
    public static float[] SIN_TABLE_FAST = new float[4096];
    public static boolean fastMath = false;
    public static float[] SIN_TABLE = new float[65536];
    public static int[] multiplyDeBruijnBitPosition;
    public static final float CONSTANT_6_2831855 = (float) (Math.PI * 2);
    public static final float CONSTANT_6_2831855_2 = (float) (Math.PI * 2);

    public static float sin(float f) {
        return fastMath ? SIN_TABLE_FAST[(int)(f * 651.8986F) & 4095] : SIN_TABLE[(int)(f * 10430.378F) & 65535];
    }

    public static float cos(float f) {
        return fastMath ? SIN_TABLE_FAST[(int)((f + (float) (Math.PI / 2)) * 651.8986F) & 4095] : SIN_TABLE[(int)(f * 10430.378F + 16384.0F) & 65535];
    }

    public static float sqrt_float(float f) {
        return (float)Math.sqrt(f);
    }

    public static float sqrt_double(double d) {
        return (float)Math.sqrt(d);
    }

    public static int floor_float(float f) {
        int n = (int)f;
        return f < n ? n - 1 : n;
    }

    public static int truncateDoubleToInt(double d) {
        return (int)(d + 1024.0) - 1024;
    }

    public static int floor_double(double d) {
        int n = (int)d;
        return d < n ? n - 1 : n;
    }

    public static long floor_double_long(double d) {
        long l = (long)d;
        return d < l ? l - 1L : l;
    }

    public static int absFloor(double d) {
        return (int)(d >= 0.0 ? d : -d + 1.0);
    }

    public static float abs(float f) {
        return f >= 0.0F ? f : -f;
    }

    public static int abs_int(int n) {
        return n >= 0 ? n : -n;
    }

    public static int ceiling_float_int(float f) {
        int n = (int)f;
        return f > n ? n + 1 : n;
    }

    public static int ceiling_double_int(double d) {
        int n = (int)d;
        return d > n ? n + 1 : n;
    }

    public static int clamp_int(int n, int n2, int n3) {
        return n < n2 ? n2 : (n > n3 ? n3 : n);
    }

    public static float clamp_float(float f, float f2, float f3) {
        return f < f2 ? f2 : (f > f3 ? f3 : f);
    }

    public static double clamp_double(double d, double d2, double d3) {
        return d < d2 ? d2 : (d > d3 ? d3 : d);
    }

    public static double denormalizeClamp(double d, double d2, double d3) {
        return d3 < 0.0 ? d : (d3 > 1.0 ? d2 : d + (d2 - d) * d3);
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

    public static boolean stringNullOrLengthZero(String str) {
        return str == null || str.length() == 0;
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

    public static double average(long[] longs) {
        long l = 0L;
        long[] longs2 = longs;
        int length = longs.length;

        for (int i = 0; i < length; i++) {
            long l2 = longs2[i];
            l += l2;
        }

        return (double)l / longs.length;
    }

    public static float wrapAngleTo180_float(float f) {
        f %= 360.0F;
        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    public static double wrapAngleTo180_double(double d) {
        d %= 360.0;
        if (d >= 180.0) {
            d -= 360.0;
        }

        if (d < -180.0) {
            d += 360.0;
        }

        return d;
    }

    public static int parseIntWithDefault(String str, int n) {
        int n2 = n;

        try {
            n2 = Integer.parseInt(str);
        } catch (Throwable e) {
        }

        return n2;
    }

    public static int parseIntWithDefaultAndMax(String str, int n, int n2) {
        int n3 = n;

        try {
            n3 = Integer.parseInt(str);
        } catch (Throwable e) {
        }

        if (n3 < n2) {
            n3 = n2;
        }

        return n3;
    }

    public static double parseDoubleWithDefault(String str, double d) {
        double d2 = d;

        try {
            d2 = Double.parseDouble(str);
        } catch (Throwable e) {
        }

        return d2;
    }

    public static double parseDoubleWithDefaultAndMax(String str, double d, double d2) {
        double d3 = d;

        try {
            d3 = Double.parseDouble(str);
        } catch (Throwable e) {
        }

        if (d3 < d2) {
            d3 = d2;
        }

        return d3;
    }

    public static int roundUpToPowerOfTwo(int n) {
        int n2 = n - 1;
        n2 |= n2 >> 1;
        n2 |= n2 >> 2;
        n2 |= n2 >> 4;
        n2 |= n2 >> 8;
        n2 |= n2 >> 16;
        return n2 + 1;
    }

    public static boolean isPowerOfTwo(int n) {
        return n != 0 && (n & n - 1) == 0;
    }

    public static int calculateLogBaseTwoDeBruijn(int n) {
        n = isPowerOfTwo(n) ? n : roundUpToPowerOfTwo(n);
        return multiplyDeBruijnBitPosition[(int)(n * 125613361L >> 27) & 31];
    }

    public static int calculateLogBaseTwo(int n) {
        return calculateLogBaseTwoDeBruijn(n) - (isPowerOfTwo(n) ? 0 : 1);
    }

    public static int roundUp(int n, int n2) {
        if (n2 == 0) {
            return 0;
        }

        if (n < 0) {
            n2 *= -1;
        }

        int n3 = n % n2;
        return n3 == 0 ? n : n + n2 - n3;
    }

    static {
        for (int i = 0; i < 65536; i++) {
            SIN_TABLE[i] = (float)Math.sin(i * Math.PI * 2.0 / 65536.0);
        }

        multiplyDeBruijnBitPosition = new int[]{
            0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
        };

        for (int i = 0; i < 4096; i++) {
            SIN_TABLE_FAST[i] = (float)Math.sin((i + 0.5F) / 4096.0F * (float) (Math.PI * 2));
        }

        for (int i = 0; i < 360; i += 90) {
            SIN_TABLE_FAST[(int)(i * 11.377778F) & 4095] = (float)Math.sin(i * (float) (Math.PI / 180.0));
        }
    }

    public static int floorMod(int n, int n2) {
        return (n % n2 + n2) % n2;
    }
}
