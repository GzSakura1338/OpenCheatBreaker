package com.cheatbreaker.client.util;

import com.cheatbreaker.client.CheatBreaker;

public class MouseSensitivityConverter {
    public static double sensitivityToDistance(double d) {
        double d2 = 1000.0;
        double d3 = d2 / (d * 100.0);
        double intValue = CheatBreaker.getInstance().getKeybindSettings().mouseDpi.getIntValue();
        double d4 = d3 / intValue;
        return d4 * 2.54;
    }

    public static double distanceToSensitivity(double d) {
        double d2 = d / 2.54;
        double d3 = CheatBreaker.getInstance().getKeybindSettings().mouseDpi.getIntValue() * d2;
        double d4 = 1000.0;
        double d5 = d4 / d3;
        return Math.max(0.1, Math.min(1.0, d5));
    }
}
