package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorImproved extends NoiseGenerator {
    public int[] permutations = new int[512];
    public double xCoord;
    public double yCoord;
    public double zCoord;
    public static double[] doubleArray = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
    public static double[] doubleArray2 = new double[]{1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0};
    public static double[] doubleArray3 = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
    public static double[] doubleArray4 = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
    public static double[] doubleArray5 = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};

    public NoiseGeneratorImproved() {
        this(new Random());
    }

    public NoiseGeneratorImproved(Random random) {
        this.xCoord = random.nextDouble() * 256.0;
        this.yCoord = random.nextDouble() * 256.0;
        this.zCoord = random.nextDouble() * 256.0;
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

    public double lerp(double d, double d2, double d3) {
        return d2 + d * (d3 - d2);
    }

    public double computeDouble(int n, double d, double d2) {
        int n2 = n & 15;
        return doubleArray4[n2] * d + doubleArray5[n2] * d2;
    }

    public double grad(int n, double d, double d2, double d3) {
        int n2 = n & 15;
        return doubleArray[n2] * d + doubleArray2[n2] * d2 + doubleArray3[n2] * d3;
    }

    public void populateNoiseArray(
        double[] doubles, double d, double d2, double d3, int n, int n2, int n3, double d4, double d5, double d6, double d7
    ) {
        if (n2 == 1) {
            boolean flag = false;
            boolean flag2 = false;
            boolean flag3 = false;
            boolean flag4 = false;
            double d8 = 0.0;
            double d9 = 0.0;
            int n4 = 0;
            double d10 = 1.0 / d7;

            for (int i = 0; i < n; i++) {
                double d11 = d + i * d4 + this.xCoord;
                int n5 = (int)d11;
                if (d11 < n5) {
                    n5--;
                }

                int n6 = n5 & 0xFF;
                d11 -= n5;
                double d12 = d11 * d11 * d11 * (d11 * (d11 * 6.0 - 15.0) + 10.0);

                for (int j = 0; j < n3; j++) {
                    double d13 = d3 + j * d6 + this.zCoord;
                    int n7 = (int)d13;
                    if (d13 < n7) {
                        n7--;
                    }

                    int n8 = n7 & 0xFF;
                    d13 -= n7;
                    double d14 = d13 * d13 * d13 * (d13 * (d13 * 6.0 - 15.0) + 10.0);
                    int n9 = this.permutations[n6] + 0;
                    int n10 = this.permutations[n9] + n8;
                    int n11 = this.permutations[n6 + 1] + 0;
                    int n12 = this.permutations[n11] + n8;
                    d8 = this.lerp(
                        d12, this.computeDouble(this.permutations[n10], d11, d13), this.grad(this.permutations[n12], d11 - 1.0, 0.0, d13)
                    );
                    d9 = this.lerp(
                        d12,
                        this.grad(this.permutations[n10 + 1], d11, 0.0, d13 - 1.0),
                        this.grad(this.permutations[n12 + 1], d11 - 1.0, 0.0, d13 - 1.0)
                    );
                    double lerp = this.lerp(d14, d8, d9);
                    int n13 = n4++;
                    doubles[n13] += lerp * d10;
                }
            }
        } else {
            int n4 = 0;
            double d8 = 1.0 / d7;
            int n5 = -1;
            boolean flag = false;
            boolean flag2 = false;
            boolean flag3 = false;
            boolean flag4 = false;
            boolean flag5 = false;
            boolean flag6 = false;
            double d9 = 0.0;
            double d10 = 0.0;
            double d11 = 0.0;
            double d12 = 0.0;

            for (int i = 0; i < n; i++) {
                double d13 = d + i * d4 + this.xCoord;
                int n6 = (int)d13;
                if (d13 < n6) {
                    n6--;
                }

                int n7 = n6 & 0xFF;
                d13 -= n6;
                double d14 = d13 * d13 * d13 * (d13 * (d13 * 6.0 - 15.0) + 10.0);

                for (int j = 0; j < n3; j++) {
                    double d15 = d3 + j * d6 + this.zCoord;
                    int n8 = (int)d15;
                    if (d15 < n8) {
                        n8--;
                    }

                    int n9 = n8 & 0xFF;
                    d15 -= n8;
                    double d16 = d15 * d15 * d15 * (d15 * (d15 * 6.0 - 15.0) + 10.0);

                    for (int k = 0; k < n2; k++) {
                        double d17 = d2 + k * d5 + this.yCoord;
                        int n10 = (int)d17;
                        if (d17 < n10) {
                            n10--;
                        }

                        int n11 = n10 & 0xFF;
                        d17 -= n10;
                        double d18 = d17 * d17 * d17 * (d17 * (d17 * 6.0 - 15.0) + 10.0);
                        if (k == 0 || n11 != n5) {
                            n5 = n11;
                            int n12 = this.permutations[n7] + n11;
                            int n13 = this.permutations[n12] + n9;
                            int n14 = this.permutations[n12 + 1] + n9;
                            int n15 = this.permutations[n7 + 1] + n11;
                            int n16 = this.permutations[n15] + n9;
                            int n17 = this.permutations[n15 + 1] + n9;
                            d9 = this.lerp(
                                d14, this.grad(this.permutations[n13], d13, d17, d15), this.grad(this.permutations[n16], d13 - 1.0, d17, d15)
                            );
                            d10 = this.lerp(
                                d14,
                                this.grad(this.permutations[n14], d13, d17 - 1.0, d15),
                                this.grad(this.permutations[n17], d13 - 1.0, d17 - 1.0, d15)
                            );
                            d11 = this.lerp(
                                d14,
                                this.grad(this.permutations[n13 + 1], d13, d17, d15 - 1.0),
                                this.grad(this.permutations[n16 + 1], d13 - 1.0, d17, d15 - 1.0)
                            );
                            d12 = this.lerp(
                                d14,
                                this.grad(this.permutations[n14 + 1], d13, d17 - 1.0, d15 - 1.0),
                                this.grad(this.permutations[n17 + 1], d13 - 1.0, d17 - 1.0, d15 - 1.0)
                            );
                        }

                        double lerp = this.lerp(d18, d9, d10);
                        double lerp2 = this.lerp(d18, d11, d12);
                        double lerp3 = this.lerp(d16, lerp, lerp2);
                        int n12 = n4++;
                        doubles[n12] += lerp3 * d8;
                    }
                }
            }
        }
    }
}
