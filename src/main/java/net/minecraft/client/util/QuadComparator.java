package net.minecraft.client.util;

import java.util.Comparator;

public class QuadComparator implements Comparator {
    public int[] intArray;
    public float floatValue;
    public float floatValue2;
    public float floatValue3;

    public QuadComparator(int[] ints, float f, float f2, float f3) {
        this.intArray = ints;
        this.floatValue = f;
        this.floatValue2 = f2;
        this.floatValue3 = f3;
    }

    public int compare(Integer integer, Integer integer2) {
        float f = Float.intBitsToFloat(this.intArray[integer]) - this.floatValue;
        float f2 = Float.intBitsToFloat(this.intArray[integer + 1]) - this.floatValue2;
        float f3 = Float.intBitsToFloat(this.intArray[integer + 2]) - this.floatValue3;
        float f4 = Float.intBitsToFloat(this.intArray[integer + 8]) - this.floatValue;
        float f5 = Float.intBitsToFloat(this.intArray[integer + 9]) - this.floatValue2;
        float f6 = Float.intBitsToFloat(this.intArray[integer + 10]) - this.floatValue3;
        float f7 = Float.intBitsToFloat(this.intArray[integer + 16]) - this.floatValue;
        float f8 = Float.intBitsToFloat(this.intArray[integer + 17]) - this.floatValue2;
        float f9 = Float.intBitsToFloat(this.intArray[integer + 18]) - this.floatValue3;
        float f10 = Float.intBitsToFloat(this.intArray[integer + 24]) - this.floatValue;
        float f11 = Float.intBitsToFloat(this.intArray[integer + 25]) - this.floatValue2;
        float f12 = Float.intBitsToFloat(this.intArray[integer + 26]) - this.floatValue3;
        float f13 = Float.intBitsToFloat(this.intArray[integer2]) - this.floatValue;
        float f14 = Float.intBitsToFloat(this.intArray[integer2 + 1]) - this.floatValue2;
        float f15 = Float.intBitsToFloat(this.intArray[integer2 + 2]) - this.floatValue3;
        float f16 = Float.intBitsToFloat(this.intArray[integer2 + 8]) - this.floatValue;
        float f17 = Float.intBitsToFloat(this.intArray[integer2 + 9]) - this.floatValue2;
        float f18 = Float.intBitsToFloat(this.intArray[integer2 + 10]) - this.floatValue3;
        float f19 = Float.intBitsToFloat(this.intArray[integer2 + 16]) - this.floatValue;
        float f20 = Float.intBitsToFloat(this.intArray[integer2 + 17]) - this.floatValue2;
        float f21 = Float.intBitsToFloat(this.intArray[integer2 + 18]) - this.floatValue3;
        float f22 = Float.intBitsToFloat(this.intArray[integer2 + 24]) - this.floatValue;
        float f23 = Float.intBitsToFloat(this.intArray[integer2 + 25]) - this.floatValue2;
        float f24 = Float.intBitsToFloat(this.intArray[integer2 + 26]) - this.floatValue3;
        float f25 = (f + f4 + f7 + f10) * 0.25F;
        float f26 = (f2 + f5 + f8 + f11) * 0.25F;
        float f27 = (f3 + f6 + f9 + f12) * 0.25F;
        float f28 = (f13 + f16 + f19 + f22) * 0.25F;
        float f29 = (f14 + f17 + f20 + f23) * 0.25F;
        float f30 = (f15 + f18 + f21 + f24) * 0.25F;
        float f31 = f25 * f25 + f26 * f26 + f27 * f27;
        float f32 = f28 * f28 + f29 * f29 + f30 * f30;
        return Float.compare(f32, f31);
    }

    @Override
    public int compare(Object obj, Object obj2) {
        return this.compare((Integer)obj, (Integer)obj2);
    }
}
