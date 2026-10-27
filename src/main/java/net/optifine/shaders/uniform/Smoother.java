package net.optifine.shaders.uniform;

import java.util.HashMap;
import java.util.Map;
import net.optifine.util.CounterInt;
import net.optifine.util.SmoothFloat;

public class Smoother {
   public static Map<Integer, SmoothFloat> mapSmoothValues = new HashMap<>();
   public static CounterInt counterIds = new CounterInt(1);

   public static float getSmoothValue(int n, float f, float f2, float f3) {
      synchronized (mapSmoothValues) {
         Integer integer = n;
         SmoothFloat smoothFloat = mapSmoothValues.get(integer);
         if (smoothFloat == null) {
            smoothFloat = new SmoothFloat(f, f2, f3);
            mapSmoothValues.put(integer, smoothFloat);
         }

         return smoothFloat.getSmoothValue(f, f2, f3);
      }
   }

   public static int getNextId() {
      synchronized (counterIds) {
         return counterIds.nextValue();
      }
   }

   public static void resetValues() {
      synchronized (mapSmoothValues) {
         mapSmoothValues.clear();
      }
   }
}
