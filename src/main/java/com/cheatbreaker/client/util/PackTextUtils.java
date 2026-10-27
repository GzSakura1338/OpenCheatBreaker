package com.cheatbreaker.client.util;

import net.minecraft.client.Minecraft;

public class PackTextUtils {
   public static Minecraft mc = Minecraft.getMinecraft();

   public static String createStringBuilder(String str, int n) {
      if (str.endsWith(".zip")) {
         str = str.substring(0, str.length() - 4);
      }

      if (mc.fontRendererObj.getStringWidth(str) > n) {
         str = mc.fontRendererObj.trimStringToWidth(str, n - mc.fontRendererObj.getStringWidth("...")) + "...";
      }

      return str;
   }
}
