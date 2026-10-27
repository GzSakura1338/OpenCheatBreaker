package net.optifine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
   public static Logger LOGGER = LogManager.getLogger();
   public static boolean logDetail = System.getProperty("log.detail", "false").equals("true");

   public static void detail(String str) {
      if (logDetail) {
         LOGGER.info("[OptiFine] " + str);
      }
   }

   public static void dbg(String str) {
      LOGGER.info("[OptiFine] " + str);
   }

   public static void warn(String str) {
      LOGGER.warn("[OptiFine] " + str);
   }

   public static void warn(String str, Throwable throwable) {
      LOGGER.warn("[OptiFine] " + str, throwable);
   }

   public static void error(String str) {
      LOGGER.error("[OptiFine] " + str);
   }

   public static void error(String str, Throwable throwable) {
      LOGGER.error("[OptiFine] " + str, throwable);
   }

   public static void log(String str) {
      dbg(str);
   }
}
