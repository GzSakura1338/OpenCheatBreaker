package net.minecraft.util;

import java.util.Collection;
import java.util.Random;

public class WeightedRandom {
   public static int getTotalWeight(Collection<? extends WeightedRandom.Item> collection) {
      int n = 0;

      for (WeightedRandom.Item item : collection) {
         n += item.itemWeight;
      }

      return n;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random random, Collection<T> collection, int totalWeight) {
      if (totalWeight <= 0) {
         throw new IllegalArgumentException();
      }

      int nextInt = random.nextInt(totalWeight);
      return getRandomItem(collection, nextInt);
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Collection<T> collection, int weight) {
      for (WeightedRandom.Item item : collection) {
         weight -= item.itemWeight;
         if (weight < 0) {
            return (T)item;
         }
      }

      return null;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random random, Collection<T> collection) {
      return getRandomItem(random, collection, getTotalWeight(collection));
   }

   public static class Item {
      public int itemWeight;

      public Item(int itemWeightIn) {
         this.itemWeight = itemWeightIn;
      }
   }
}
