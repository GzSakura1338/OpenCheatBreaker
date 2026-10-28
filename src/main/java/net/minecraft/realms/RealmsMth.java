package net.minecraft.realms;

import java.util.Random;
import net.minecraft.util.MathHelper;

public class RealmsMth {
    public static double nextDouble(Random random, double d, double d2) {
        return MathHelper.getRandomDoubleInRange(random, d, d2);
    }

    public static int clamp(int n, int n2, int n3) {
        return MathHelper.clamp_int(n, n2, n3);
    }

    public static boolean isEmpty(String str) {
        return MathHelper.stringNullOrLengthZero(str);
    }

    public static long lfloor(double d) {
        return MathHelper.floor_double_long(d);
    }

    public static float sqrt(double d) {
        return MathHelper.sqrt_double(d);
    }

    public static int getInt(String str, int n) {
        return MathHelper.parseIntWithDefault(str, n);
    }

    public static double getDouble(String str, double d) {
        return MathHelper.parseDoubleWithDefault(str, d);
    }

    public static int getInt(String str, int n, int n2) {
        return MathHelper.parseIntWithDefaultAndMax(str, n, n2);
    }

    public static double asbMax(double d, double d2) {
        return MathHelper.abs_max(d, d2);
    }

    public static float nextFloat(Random random, float f, float f2) {
        return MathHelper.randomFloatClamp(random, f, f2);
    }

    public static double wrapDegrees(double d) {
        return MathHelper.wrapAngleTo180_double(d);
    }

    public static float clamp(float f, float f2, float f3) {
        return MathHelper.clamp_float(f, f2, f3);
    }

    public static double getDouble(String str, double d, double d2) {
        return MathHelper.parseDoubleWithDefaultAndMax(str, d, d2);
    }

    public static double average(long[] longs) {
        return MathHelper.average(longs);
    }

    public static int nextInt(Random random, int n, int n2) {
        return MathHelper.getRandomIntegerInRange(random, n, n2);
    }

    public static int roundUpToPowerOfTwo(int n) {
        return MathHelper.roundUpToPowerOfTwo(n);
    }

    public static float sqrt_float(float f) {
        return MathHelper.sqrt_float(f);
    }

    public static float sin(float f) {
        return MathHelper.sin(f);
    }

    public static int ceiling_float_int(float f) {
        return MathHelper.ceiling_float_int(f);
    }

    public static int absFloor(double d) {
        return MathHelper.absFloor(d);
    }

    public static float abs(float f) {
        return MathHelper.abs(f);
    }

    public static int calculateLogBaseTwo(int n) {
        return MathHelper.calculateLogBaseTwo(n);
    }

    public static int floor_double(double d) {
        return MathHelper.floor_double(d);
    }

    public static float cos(float f) {
        return MathHelper.cos(f);
    }

    public static int abs_int(int n) {
        return MathHelper.abs_int(n);
    }

    public static int ceiling_double_int(double d) {
        return MathHelper.ceiling_double_int(d);
    }

    public static double denormalizeClamp(double d, double d2, double d3) {
        return MathHelper.denormalizeClamp(d, d2, d3);
    }

    public static int bucketInt(int n, int n2) {
        return MathHelper.bucketInt(n, n2);
    }

    public static double clamp_double(double d, double d2, double d3) {
        return MathHelper.clamp_double(d, d2, d3);
    }

    public static int truncateDoubleToInt(double d) {
        return MathHelper.truncateDoubleToInt(d);
    }

    public static float wrapAngleTo180_float(float f) {
        return MathHelper.wrapAngleTo180_float(f);
    }

    public static int roundUp(int n, int n2) {
        return MathHelper.roundUp(n, n2);
    }

    public static int floor_float(float f) {
        return MathHelper.floor_float(f);
    }
}
