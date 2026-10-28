package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorSimplex {
    public static int[][] intMatrix = new int[][]{
        {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };
    public static double SQRT3 = Math.sqrt(3.0);
    public int[] permutations = new int[512];
    public double doubleValue;
    public double doubleValue2;
    public double doubleValue3;
    public static double doubleValue4 = 0.5 * (SQRT3 - 1.0);
    public static double doubleValue5 = (3.0 - SQRT3) / 6.0;

    public NoiseGeneratorSimplex() {
        this(new Random());
    }

    public NoiseGeneratorSimplex(Random random) {
        this.doubleValue = random.nextDouble() * 256.0;
        this.doubleValue2 = random.nextDouble() * 256.0;
        this.doubleValue3 = random.nextDouble() * 256.0;
        int n = 0;

        while (n < 256) {
            this.permutations[n] = n++;
        }

        for (int i = 0; i < 256; i++) {
            int n2 = random.nextInt(256 - i) + i;
            int n3 = this.permutations[i];
            this.permutations[i] = this.permutations[n2];
            this.permutations[n2] = n3;
            this.permutations[i + 256] = this.permutations[i];
        }
    }

    public static int computeInt(double d) {
        return d > 0.0 ? (int)d : (int)d - 1;
    }

    public static double computeDouble(int[] ints, double d, double d2) {
        return ints[0] * d + ints[1] * d2;
    }

    public double sample(double d, double d2) {
        double d3 = 0.5 * (SQRT3 - 1.0);
        double d4 = (d + d2) * d3;
        int computeInt2 = computeInt(d + d4);
        int computeInt3 = computeInt(d2 + d4);
        double d5 = (3.0 - SQRT3) / 6.0;
        double d6 = (computeInt2 + computeInt3) * d5;
        double d7 = computeInt2 - d6;
        double d8 = computeInt3 - d6;
        double d9 = d - d7;
        double d10 = d2 - d8;
        byte b;
        byte b2;
        if (d9 > d10) {
            b = 1;
            b2 = 0;
        } else {
            b = 0;
            b2 = 1;
        }

        double d11 = d9 - b + d5;
        double d12 = d10 - b2 + d5;
        double d13 = d9 - 1.0 + 2.0 * d5;
        double d14 = d10 - 1.0 + 2.0 * d5;
        int n = computeInt2 & 0xFF;
        int n2 = computeInt3 & 0xFF;
        int n3 = this.permutations[n + this.permutations[n2]] % 12;
        int n4 = this.permutations[n + b + this.permutations[n2 + b2]] % 12;
        int n5 = this.permutations[n + 1 + this.permutations[n2 + 1]] % 12;
        double d15 = 0.5 - d9 * d9 - d10 * d10;
        double d16;
        if (d15 < 0.0) {
            d16 = 0.0;
        } else {
            d15 *= d15;
            d16 = d15 * d15 * computeDouble(intMatrix[n3], d9, d10);
        }

        double d17 = 0.5 - d11 * d11 - d12 * d12;
        double d18;
        if (d17 < 0.0) {
            d18 = 0.0;
        } else {
            d17 *= d17;
            d18 = d17 * d17 * computeDouble(intMatrix[n4], d11, d12);
        }

        double d19 = 0.5 - d13 * d13 - d14 * d14;
        double d20;
        if (d19 < 0.0) {
            d20 = 0.0;
        } else {
            d19 *= d19;
            d20 = d19 * d19 * computeDouble(intMatrix[n5], d13, d14);
        }

        return 70.0 * (d16 + d18 + d20);
    }

    public void process(double[] doubles, double d, double d2, int n, int n2, double d3, double d4, double d5) {
        int n3 = 0;

        for (int i = 0; i < n2; i++) {
            double d6 = (d2 + i) * d4 + this.doubleValue2;

            for (int j = 0; j < n; j++) {
                double d7 = (d + j) * d3 + this.doubleValue;
                double d8 = (d7 + d6) * doubleValue4;
                int computeInt2 = computeInt(d7 + d8);
                int computeInt3 = computeInt(d6 + d8);
                double d9 = (computeInt2 + computeInt3) * doubleValue5;
                double d10 = computeInt2 - d9;
                double d11 = computeInt3 - d9;
                double d12 = d7 - d10;
                double d13 = d6 - d11;
                byte b;
                byte b2;
                if (d12 > d13) {
                    b = 1;
                    b2 = 0;
                } else {
                    b = 0;
                    b2 = 1;
                }

                double d14 = d12 - b + doubleValue5;
                double d15 = d13 - b2 + doubleValue5;
                double d16 = d12 - 1.0 + 2.0 * doubleValue5;
                double d17 = d13 - 1.0 + 2.0 * doubleValue5;
                int n4 = computeInt2 & 0xFF;
                int n5 = computeInt3 & 0xFF;
                int n6 = this.permutations[n4 + this.permutations[n5]] % 12;
                int n7 = this.permutations[n4 + b + this.permutations[n5 + b2]] % 12;
                int n8 = this.permutations[n4 + 1 + this.permutations[n5 + 1]] % 12;
                double d18 = 0.5 - d12 * d12 - d13 * d13;
                double d19;
                if (d18 < 0.0) {
                    d19 = 0.0;
                } else {
                    d18 *= d18;
                    d19 = d18 * d18 * computeDouble(intMatrix[n6], d12, d13);
                }

                double d20 = 0.5 - d14 * d14 - d15 * d15;
                double d21;
                if (d20 < 0.0) {
                    d21 = 0.0;
                } else {
                    d20 *= d20;
                    d21 = d20 * d20 * computeDouble(intMatrix[n7], d14, d15);
                }

                double d22 = 0.5 - d16 * d16 - d17 * d17;
                double d23;
                if (d22 < 0.0) {
                    d23 = 0.0;
                } else {
                    d22 *= d22;
                    d23 = d22 * d22 * computeDouble(intMatrix[n8], d16, d17);
                }

                int n9 = n3++;
                doubles[n9] += 70.0 * (d19 + d21 + d23) * d5;
            }
        }
    }
}
