package net.optifine.util;

public class MemoryMonitor {
   public static long startTimeMs = System.currentTimeMillis();
   public static long startMemory = getMemoryUsed();
   public static long lastTimeMs = startTimeMs;
   public static long lastMemory = startMemory;
   public static boolean gcEvent = false;
   public static int memBytesSec = 0;
   public static long longValue = 1048576L;

   public static void update() {
      long currentTimeMillis = System.currentTimeMillis();
      long memoryUsed = getMemoryUsed();
      gcEvent = memoryUsed < lastMemory;
      if (gcEvent) {
         long l = lastTimeMs - startTimeMs;
         long l2 = lastMemory - startMemory;
         double d = l / 1000.0;
         int n = (int)(l2 / d);
         if (n > 0) {
            memBytesSec = n;
         }

         startTimeMs = currentTimeMillis;
         startMemory = memoryUsed;
      }

      lastTimeMs = currentTimeMillis;
      lastMemory = memoryUsed;
   }

   public static long getMemoryUsed() {
      Runtime runtime = Runtime.getRuntime();
      return runtime.totalMemory() - runtime.freeMemory();
   }

   public static long getStartTimeMs() {
      return startTimeMs;
   }

   public static long getStartMemoryMb() {
      return startMemory / longValue;
   }

   public static boolean isGcEvent() {
      return gcEvent;
   }

   public static long getAllocationRateMb() {
      return memBytesSec / longValue;
   }
}
