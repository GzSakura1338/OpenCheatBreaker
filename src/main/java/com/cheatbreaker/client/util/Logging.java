package com.cheatbreaker.client.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logging {
   public static Logger logger = LogManager.getLogger("CheatBreaker");

   public static void debug(Object obj, Object... objs) {
      log(LogLevel.DEBUG, null, obj, objs);
   }

   public static void warnTagged(String str, Object obj, Object... objs) {
      log(LogLevel.WARN, str, obj, objs);
   }

   public static void warn(Object obj, Object... objs) {
      log(LogLevel.WARN, null, obj, objs);
   }

   public static void infoTagged(String str, Object obj, Object... objs) {
      log(LogLevel.INFO, str, obj, objs);
   }

   public static void debugTagged(String str, Object obj, Object... objs) {
      log(LogLevel.DEBUG, str, obj, objs);
   }

   public static void errorTagged(String str, Object obj, Object... objs) {
      log(LogLevel.ERROR, str, obj, objs);
   }

   public Logging() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static void info(Object obj, Object... objs) {
      log(LogLevel.INFO, null, obj, objs);
   }

   public static void error(Object obj, Object... objs) {
      log(LogLevel.ERROR, null, obj, objs);
   }

   public static void log(LogLevel logLevel, String str, Object obj, Object... objs) {
      String str2 = str == null ? "[CB]" : "[CB (" + str + ")]";
      String str3 = str2 + " " + String.format(obj.toString(), objs);
      switch (logLevel) {
         case DEBUG:
            logger.debug(str3);
            break;
         case INFO:
            logger.info(str3);
            break;
         case WARN:
            logger.warn(str3);
            break;
         case ERROR:
            logger.error(str3);
      }
   }
}
