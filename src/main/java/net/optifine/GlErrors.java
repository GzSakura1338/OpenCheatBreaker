package net.optifine;

public class GlErrors {
   public static boolean frameStarted = false;
   public static long timeCheckStartMs = -1L;
   public static int countErrors = 0;
   public static int countErrorsSuppressed = 0;
   public static boolean suppressed = false;
   public static boolean oneErrorEnabled = false;
   public static final long CHECK_INTERVAL_MS = 3000L;
   public static final int CHECK_ERROR_MAX = 10;

   public static void frameStart() {
      frameStarted = true;
      if (timeCheckStartMs < 0L) {
         timeCheckStartMs = System.currentTimeMillis();
      }

      if (System.currentTimeMillis() > timeCheckStartMs + 3000L) {
         if (countErrorsSuppressed > 0) {
            Config.error("Suppressed " + countErrors + " OpenGL errors");
         }

         suppressed = countErrors > 10;
         timeCheckStartMs = System.currentTimeMillis();
         countErrors = 0;
         countErrorsSuppressed = 0;
         oneErrorEnabled = true;
      }
   }

   public static boolean isEnabled(int n) {
      if (!frameStarted) {
         return true;
      }

      countErrors++;
      if (oneErrorEnabled) {
         oneErrorEnabled = false;
         return true;
      }

      if (suppressed) {
         countErrorsSuppressed++;
      }

      return !suppressed;
   }
}
