package com.cheatbreaker.client.animation;

public class Keyframe {
    public float leftHandleX;
    public InterpolationType interpolationType = InterpolationType.LINEAR;
    public float leftHandleY;
    public float rightHandleX;
    public float rightHandleY;
    public float value;
    public int frame;

    public Keyframe(int n, float f, String str, float f2, float f3, float f4, float f5) {
        this(n, f, str);
        this.leftHandleX = f2;
        this.leftHandleY = f3;
        this.rightHandleX = f4;
        this.rightHandleY = f5;
    }

    public static float lerp(float f, float f2, float f3) {
        return f + (f2 - f) * f3;
    }

    public float interpolate(float f, Keyframe keyframe) {
        return this.interpolationType.interpolate(this, f, keyframe);
    }

    public static InterpolationType parseInterpolation(String str) {
        if (str.equals("CONSTANT")) {
            return InterpolationType.CONSTANT;
        } else {
            return str.equals("BEZIER") ? InterpolationType.BEZIER : InterpolationType.LINEAR;
        }
    }

    public static Keyframe fromTokens(String[] strs) {
        if (strs.length == 8) {
            float parseFloat = Float.parseFloat(strs[4]);
            float parseFloat2 = Float.parseFloat(strs[5]);
            float parseFloat3 = Float.parseFloat(strs[6]);
            float parseFloat4 = Float.parseFloat(strs[7]);
            return new Keyframe(Integer.parseInt(strs[1]), Float.parseFloat(strs[2]), strs[3], parseFloat, parseFloat2, parseFloat3, parseFloat4);
        } else if (strs.length == 4) {
            return new Keyframe(Integer.parseInt(strs[1]), Float.parseFloat(strs[2]), strs[3]);
        } else {
            return strs.length == 3 ? new Keyframe(Integer.parseInt(strs[1]), Float.parseFloat(strs[2])) : null;
        }
    }

    public Keyframe(int n, float f) {
        this.frame = n;
        this.value = f;
    }

    public Keyframe(int n, float f, String str) {
        this(n, f);
        this.interpolationType = parseInterpolation(str);
    }
}
