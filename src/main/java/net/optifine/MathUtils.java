package net.optifine;

import net.minecraft.util.MathHelper;

public class MathUtils {
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
        return f * 180.0F / (float) Math.PI;
    }

    public static float toRad(float f) {
        return f / 180.0F * (float) Math.PI;
    }
}
