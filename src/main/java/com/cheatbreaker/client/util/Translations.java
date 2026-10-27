package com.cheatbreaker.client.util;

import java.text.Collator;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class Translations {
   public static Collator getCollator() {
      String str = "en_US";

      try {
         str = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
      } catch (NullPointerException e) {
      }

      String[] split = str.split("_");
      Locale locale = new Locale(split[0], split.length > 1 ? split[1] : "");
      return Collator.getInstance(locale);
   }

   public static String translate(String str) {
      return I18n.format(str);
   }
}
