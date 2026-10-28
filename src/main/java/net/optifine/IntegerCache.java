package net.optifine;

public class IntegerCache {
    public static final int CACHE_SIZE = 65535;
    public static Integer[] cache = makeCache(65535);

    public static Integer[] makeCache(int n) {
        Integer[] integers = new Integer[n];

        for (int i = 0; i < n; i++) {
            integers[i] = new Integer(i);
        }

        return integers;
    }

    public static Integer valueOf(int n) {
        return n >= 0 && n < 65535 ? cache[n] : new Integer(n);
    }
}
