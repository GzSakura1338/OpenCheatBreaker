package net.minecraft.world.gen.layer;

import com.google.common.collect.Lists;
import java.util.List;

public class IntCache {
   public static int intCacheSize = 256;
   public static List<int[]> freeSmallArrays = Lists.newArrayList();
   public static List<int[]> inUseSmallArrays = Lists.newArrayList();
   public static List<int[]> freeLargeArrays = Lists.newArrayList();
   public static List<int[]> inUseLargeArrays = Lists.newArrayList();

   public static synchronized int[] getIntCache(int n) {
      if (n <= 256) {
         if (freeSmallArrays.isEmpty()) {
            int[] ints = new int[256];
            inUseSmallArrays.add(ints);
            return ints;
         } else {
            int[] ints = freeSmallArrays.remove(freeSmallArrays.size() - 1);
            inUseSmallArrays.add(ints);
            return ints;
         }
      } else if (n > intCacheSize) {
         intCacheSize = n;
         freeLargeArrays.clear();
         inUseLargeArrays.clear();
         int[] ints = new int[intCacheSize];
         inUseLargeArrays.add(ints);
         return ints;
      } else if (freeLargeArrays.isEmpty()) {
         int[] ints = new int[intCacheSize];
         inUseLargeArrays.add(ints);
         return ints;
      } else {
         int[] ints = freeLargeArrays.remove(freeLargeArrays.size() - 1);
         inUseLargeArrays.add(ints);
         return ints;
      }
   }

   public static synchronized void resetIntCache() {
      if (!freeLargeArrays.isEmpty()) {
         freeLargeArrays.remove(freeLargeArrays.size() - 1);
      }

      if (!freeSmallArrays.isEmpty()) {
         freeSmallArrays.remove(freeSmallArrays.size() - 1);
      }

      freeLargeArrays.addAll(inUseLargeArrays);
      freeSmallArrays.addAll(inUseSmallArrays);
      inUseLargeArrays.clear();
      inUseSmallArrays.clear();
   }

   public static synchronized String getCacheSizes() {
      return "cache: "
         + freeLargeArrays.size()
         + ", tcache: "
         + freeSmallArrays.size()
         + ", allocated: "
         + inUseLargeArrays.size()
         + ", tallocated: "
         + inUseSmallArrays.size();
   }
}
