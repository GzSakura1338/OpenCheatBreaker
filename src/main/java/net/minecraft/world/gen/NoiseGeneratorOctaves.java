package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.MathHelper;

public class NoiseGeneratorOctaves extends NoiseGenerator {
    public int octaves;
    public NoiseGeneratorImproved[] generatorCollection;

    public NoiseGeneratorOctaves(Random random, int n) {
        this.octaves = n;
        this.generatorCollection = new NoiseGeneratorImproved[n];

        for (int i = 0; i < n; i++) {
            this.generatorCollection[i] = new NoiseGeneratorImproved(random);
        }
    }

    public double[] generateNoiseOctaves(double[] doubles, int n, int n2, int n3, int n4, int n5, int n6, double d, double d2, double d3) {
        if (doubles == null) {
            doubles = new double[n4 * n5 * n6];
        } else {
            for (int i = 0; i < doubles.length; i++) {
                doubles[i] = 0.0;
            }
        }

        double d4 = 1.0;

        for (int i = 0; i < this.octaves; i++) {
            double d5 = n * d4 * d;
            double d6 = n2 * d4 * d2;
            double d7 = n3 * d4 * d3;
            long l = MathHelper.floor_double_long(d5);
            long l2 = MathHelper.floor_double_long(d7);
            d5 -= l;
            d7 -= l2;
            l %= 16777216L;
            l2 %= 16777216L;
            d5 += l;
            d7 += l2;
            this.generatorCollection[i].populateNoiseArray(doubles, d5, d6, d7, n4, n5, n6, d * d4, d2 * d4, d3 * d4, d4);
            d4 /= 2.0;
        }

        return doubles;
    }

    public double[] generateNoiseOctaves(double[] doubles, int n, int n2, int n3, int n4, double d, double d2, double d3) {
        return this.generateNoiseOctaves(doubles, n, 10, n2, n3, 1, n4, d, 1.0, d2);
    }
}
