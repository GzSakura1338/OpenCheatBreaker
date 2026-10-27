package net.optifine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import net.minecraft.network.PacketThreadUtil;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CustomLoadingScreens {
   public static CustomLoadingScreen[] screens = null;
   public static int screensMinDimensionId = 0;

   public static CustomLoadingScreen getCustomLoadingScreen() {
      if (screens == null) {
         return null;
      }

      int lastDimensionId = PacketThreadUtil.lastDimensionId;
      int n = lastDimensionId - screensMinDimensionId;
      CustomLoadingScreen customLoadingScreen = null;
      if (n >= 0 && n < screens.length) {
         customLoadingScreen = screens[n];
      }

      return customLoadingScreen;
   }

   public static void update() {
      screens = null;
      screensMinDimensionId = 0;
      Pair parseScreens2 = parseScreens();
      screens = (CustomLoadingScreen[])parseScreens2.getLeft();
      screensMinDimensionId = (Integer)parseScreens2.getRight();
   }

   public static Pair<CustomLoadingScreen[], Integer> parseScreens() {
      String str = "optifine/gui/loading/background";
      String str2 = ".png";
      String[] collectFiles = ResUtils.collectFiles(str, str2);
      HashMap map = new HashMap();

      for (int i = 0; i < collectFiles.length; i++) {
         String str3 = collectFiles[i];
         String removePrefixSuffix = StrUtils.removePrefixSuffix(str3, str, str2);
         int parseInt = Config.parseInt(removePrefixSuffix, Integer.MIN_VALUE);
         if (parseInt == Integer.MIN_VALUE) {
            warn("Invalid dimension ID: " + removePrefixSuffix + ", path: " + str3);
         } else {
            map.put(parseInt, str3);
         }
      }

      Set keySet = map.keySet();
      Integer[] toArray = (Integer[])keySet.toArray(new Integer[keySet.size()]);
      Arrays.sort(toArray);
      if (toArray.length <= 0) {
         return new ImmutablePair(null, 0);
      }

      String str3 = "optifine/gui/loading/loading.properties";
      Properties readProperties = ResUtils.readProperties(str3, "CustomLoadingScreens");
      int n = toArray[0];
      int n2 = toArray[toArray.length - 1];
      int n3 = n2 - n + 1;
      CustomLoadingScreen[] customLoadingScreens = new CustomLoadingScreen[n3];

      for (int i = 0; i < toArray.length; i++) {
         Integer integer = toArray[i];
         String str4 = (String)map.get(integer);
         customLoadingScreens[integer - n] = CustomLoadingScreen.parseScreen(str4, integer, readProperties);
      }

      return new ImmutablePair(customLoadingScreens, n);
   }

   public static void warn(String str) {
      Config.warn("CustomLoadingScreen: " + str);
   }

   public static void dbg(String str) {
      Config.dbg("CustomLoadingScreen: " + str);
   }
}
