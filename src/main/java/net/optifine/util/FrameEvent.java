package net.optifine.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class FrameEvent {
   public static Map<String, Integer> mapEventFrames = new HashMap<>();

   public static boolean isActive(String str, int n) {
      synchronized (mapEventFrames) {
         int frameCount = Minecraft.getMinecraft().entityRenderer.frameCount;
         Integer integer = mapEventFrames.get(str);
         if (integer == null) {
            integer = new Integer(frameCount);
            mapEventFrames.put(str, integer);
         }

         int n2 = integer;
         if (frameCount > n2 && frameCount < n2 + n) {
            return false;
         }

         mapEventFrames.put(str, new Integer(frameCount));
         return true;
      }
   }
}
