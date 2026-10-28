package com.cheatbreaker.client.util;

public class FastTrig {
    public static final double CONSTANT_6_283185307179586 = Math.PI * 2;
    public static final double CONSTANT_1_5707963267948966 = Math.PI / 2;
    public static double[] cosTable = new double[8192];
    public static final int CONSTANT_8192 = 8192;
    public static final int CONSTANT_16384 = 16384;

    static {
        for (int i = 0; i < 8192; i++) {
            cosTable[i] = Math.cos(i / 8192.0F * Math.PI);
        }
    }

    public FastTrig() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static double sin(double d) {
        return cos(d - (Math.PI / 2));
    }

    public static double cos(double d) {
        int n = (int)(d / (Math.PI * 2) % 1.0 * 16384.0);
        if (n < 0) {
            n += 16384;
        }

        return n >= 8192 ? -cosTable[n - 8192] : cosTable[n];
    }
}
