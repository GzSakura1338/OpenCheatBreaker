package net.optifine.util;

import java.util.HashMap;
import java.util.Map;

public class TimedEvent {
   public static Map<String, Long> mapEventTimes = new HashMap<>();

   public static boolean isActive(String str, long l) {
      synchronized (mapEventTimes) {
         long currentTimeMillis = System.currentTimeMillis();
         Long longValue = mapEventTimes.get(str);
         if (longValue == null) {
            longValue = new Long(currentTimeMillis);
            mapEventTimes.put(str, longValue);
         }

         long l2 = longValue;
         if (currentTimeMillis < l2 + l) {
            return false;
         }

         mapEventTimes.put(str, new Long(currentTimeMillis));
         return true;
      }
   }
}
