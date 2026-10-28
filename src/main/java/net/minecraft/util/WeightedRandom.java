package net.minecraft.util;

import java.util.Collection;
import java.util.Random;

public class WeightedRandom {
    public static int getTotalWeight(Collection collection) {
        int n = 0;

        for (WeightedRandom.Item item : (Iterable<WeightedRandom.Item>)collection) {
            n += item.itemWeight;
        }

        return n;
    }

    public static WeightedRandom.Item getRandomItem(Random random, Collection collection, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        int nextInt = random.nextInt(n);

        for (WeightedRandom.Item item : (Iterable<WeightedRandom.Item>)collection) {
            nextInt -= item.itemWeight;
            if (nextInt < 0) {
                return item;
            }
        }

        return null;
    }

    public static WeightedRandom.Item getRandomItem(Random random, Collection collection) {
        return getRandomItem(random, collection, getTotalWeight(collection));
    }

    public static int getTotalWeight(WeightedRandom.Item[] items) {
        int n = 0;
        WeightedRandom.Item[] items2 = items;
        int length = items.length;

        for (int i = 0; i < length; i++) {
            WeightedRandom.Item item = items2[i];
            n += item.itemWeight;
        }

        return n;
    }

    public static WeightedRandom.Item getRandomItem(Random random, WeightedRandom.Item[] items, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        int nextInt = random.nextInt(n);
        WeightedRandom.Item[] items2 = items;
        int length = items.length;

        for (int i = 0; i < length; i++) {
            WeightedRandom.Item item = items2[i];
            nextInt -= item.itemWeight;
            if (nextInt < 0) {
                return item;
            }
        }

        return null;
    }

    public static WeightedRandom.Item getRandomItem(Random random, WeightedRandom.Item[] items) {
        return getRandomItem(random, items, getTotalWeight(items));
    }

    public static class Item {
        public int itemWeight;

        public Item(int n) {
            this.itemWeight = n;
        }
    }
}
