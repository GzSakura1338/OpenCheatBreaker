package net.optifine.shaders.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.optifine.Config;
import net.optifine.Lang;
import net.optifine.util.StrUtils;

public class ShaderOptionSwitch extends ShaderOption {
   public static Pattern PATTERN_DEFINE = Pattern.compile("^\\s*(//)?\\s*#define\\s+([A-Za-z0-9_]+)\\s*(//.*)?$");
   public static Pattern PATTERN_IFDEF = Pattern.compile("^\\s*#if(n)?def\\s+([A-Za-z0-9_]+)(\\s*)?$");

   public ShaderOptionSwitch(String str, String str2, String str3, String str4) {
      super(str, str2, str3, new String[]{"false", "true"}, str3, str4);
   }

   @Override
   public String getSourceLine() {
      return isTrue(this.getValue()) ? "#define " + this.getName() + " // Shader option ON" : "//#define " + this.getName() + " // Shader option OFF";
   }

   @Override
   public String getValueText(String str) {
      String valueText = super.getValueText(str);
      return valueText != str ? valueText : (isTrue(str) ? Lang.getOn() : Lang.getOff());
   }

   @Override
   public String getValueColor(String str) {
      return isTrue(str) ? "§a" : "§c";
   }

   public static ShaderOption parseOption(String str, String str2) {
      Matcher matcher = PATTERN_DEFINE.matcher(str);
      if (!matcher.matches()) {
         return null;
      } else {
         String group = matcher.group(1);
         String group2 = matcher.group(2);
         String group3 = matcher.group(3);
         if (group2 != null && group2.length() > 0) {
            boolean flag = Config.equals(group, "//");
            boolean flag2 = !flag;
            str2 = StrUtils.removePrefix(str2, "/shaders/");
            return new ShaderOptionSwitch(group2, group3, String.valueOf(flag2), str2);
         } else {
            return null;
         }
      }
   }

   @Override
   public boolean matchesLine(String str) {
      Matcher matcher = PATTERN_DEFINE.matcher(str);
      if (!matcher.matches()) {
         return false;
      }

      String group = matcher.group(2);
      return group.matches(this.getName());
   }

   @Override
   public boolean checkUsed() {
      return true;
   }

   @Override
   public boolean isUsedInLine(String str) {
      Matcher matcher = PATTERN_IFDEF.matcher(str);
      if (matcher.matches()) {
         String group = matcher.group(2);
         if (group.equals(this.getName())) {
            return true;
         }
      }

      return false;
   }

   public static boolean isTrue(String str) {
      return Boolean.valueOf(str);
   }
}
