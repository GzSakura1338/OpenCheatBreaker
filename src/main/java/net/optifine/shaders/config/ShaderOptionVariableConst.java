package net.optifine.shaders.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.optifine.util.StrUtils;

public class ShaderOptionVariableConst extends ShaderOptionVariable {
   public String type = null;
   public static Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*(float|int)\\s*([A-Za-z0-9_]+)\\s*=\\s*(-?[0-9\\.]+f?F?)\\s*;\\s*(//.*)?$");

   public ShaderOptionVariableConst(String str, String str2, String str3, String str4, String[] strs, String str5) {
      super(str, str3, str4, strs, str5);
      this.type = str2;
   }

   @Override
   public String getSourceLine() {
      return "const " + this.type + " " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
   }

   @Override
   public boolean matchesLine(String str) {
      Matcher matcher = PATTERN_CONST.matcher(str);
      if (!matcher.matches()) {
         return false;
      }

      String group = matcher.group(2);
      return group.matches(this.getName());
   }

   public static ShaderOption parseOption(String str, String str2) {
      Matcher matcher = PATTERN_CONST.matcher(str);
      if (!matcher.matches()) {
         return null;
      }

      String group = matcher.group(1);
      String group2 = matcher.group(2);
      String group3 = matcher.group(3);
      String group4 = matcher.group(4);
      String segment = StrUtils.getSegment(group4, "[", "]");
      if (segment != null && segment.length() > 0) {
         group4 = group4.replace(segment, "").trim();
      }

      String[] parseValues2 = parseValues(group3, segment);
      if (group2 != null && group2.length() > 0) {
         str2 = StrUtils.removePrefix(str2, "/shaders/");
         return new ShaderOptionVariableConst(group2, group, group4, group3, parseValues2, str2);
      } else {
         return null;
      }
   }
}
