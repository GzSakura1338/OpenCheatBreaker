package net.optifine;

public class NumUtils {
    public static float limit(float f, float f2, float f3) {
        return f < f2 ? f2 : (f > f3 ? f3 : f);
    }
}
