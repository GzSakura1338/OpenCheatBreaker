package net.optifine.shaders.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.optifine.util.StrUtils;

public class ShaderOptionSwitchConst extends ShaderOptionSwitch {
   public static Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*bool\\s*([A-Za-z0-9_]+)\\s*=\\s*(true|false)\\s*;\\s*(//.*)?$");

   public ShaderOptionSwitchConst(String str, String str2, String str3, String str4) {
      super(str, str2, str3, str4);
   }

   @Override
   public String getSourceLine() {
      return "const bool " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
   }

   public static ShaderOption parseOption(String str, String str2) {
      Matcher matcher = PATTERN_CONST.matcher(str);
      if (!matcher.matches()) {
         return null;
      } else {
         String group = matcher.group(1);
         String group2 = matcher.group(2);
         String group3 = matcher.group(3);
         if (group != null && group.length() > 0) {
            str2 = StrUtils.removePrefix(str2, "/shaders/");
            ShaderOptionSwitchConst shaderOptionSwitchConst = new ShaderOptionSwitchConst(group, group3, group2, str2);
            shaderOptionSwitchConst.setVisible(false);
            return shaderOptionSwitchConst;
         } else {
            return null;
         }
      }
   }

   @Override
   public boolean matchesLine(String str) {
      Matcher matcher = PATTERN_CONST.matcher(str);
      if (!matcher.matches()) {
         return false;
      }

      String group = matcher.group(1);
      return group.matches(this.getName());
   }

   @Override
   public boolean checkUsed() {
      return false;
   }
}
