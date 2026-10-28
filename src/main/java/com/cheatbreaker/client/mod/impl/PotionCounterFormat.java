package com.cheatbreaker.client.mod.impl;

public class PotionCounterFormat {
    public static String pluralize(float f, String str) {
        return pluralize(f, str, false);
    }

    public static String pluralize(float f, String str, boolean flag) {
        return !(f < 1.0F) && !(f > 1.0F) ? str : str + (flag ? "S" : "s");
    }
}
