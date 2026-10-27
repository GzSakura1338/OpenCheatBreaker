package net.optifine.shaders.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderParser {
   public static Pattern PATTERN_UNIFORM = Pattern.compile("\\s*uniform\\s+\\w+\\s+(\\w+).*");
   public static Pattern PATTERN_ATTRIBUTE = Pattern.compile("\\s*attribute\\s+\\w+\\s+(\\w+).*");
   public static Pattern PATTERN_CONST_INT = Pattern.compile("\\s*const\\s+int\\s+(\\w+)\\s*=\\s*([-+.\\w]+)\\s*;.*");
   public static Pattern PATTERN_CONST_FLOAT = Pattern.compile("\\s*const\\s+float\\s+(\\w+)\\s*=\\s*([-+.\\w]+)\\s*;.*");
   public static Pattern PATTERN_CONST_VEC4 = Pattern.compile("\\s*const\\s+vec4\\s+(\\w+)\\s*=\\s*(.+)\\s*;.*");
   public static Pattern PATTERN_CONST_BOOL = Pattern.compile("\\s*const\\s+bool\\s+(\\w+)\\s*=\\s*(\\w+)\\s*;.*");
   public static Pattern PATTERN_PROPERTY = Pattern.compile("\\s*(/\\*|//)?\\s*([A-Z]+):\\s*(\\w+)\\s*(\\*/.*|\\s*)");
   public static Pattern PATTERN_EXTENSION = Pattern.compile("\\s*#\\s*extension\\s+(\\w+)\\s*:\\s*(\\w+).*");
   public static Pattern PATTERN_DEFERRED_FSH = Pattern.compile(".*deferred[0-9]*\\.fsh");
   public static Pattern PATTERN_COMPOSITE_FSH = Pattern.compile(".*composite[0-9]*\\.fsh");
   public static Pattern PATTERN_FINAL_FSH = Pattern.compile(".*final\\.fsh");
   public static Pattern PATTERN_DRAW_BUFFERS = Pattern.compile("[0-7N]*");

   public static ShaderLine parseLine(String str) {
      Matcher matcher = PATTERN_UNIFORM.matcher(str);
      if (matcher.matches()) {
         return new ShaderLine(1, matcher.group(1), "", str);
      }

      Matcher matcher2 = PATTERN_ATTRIBUTE.matcher(str);
      if (matcher2.matches()) {
         return new ShaderLine(2, matcher2.group(1), "", str);
      }

      Matcher matcher3 = PATTERN_PROPERTY.matcher(str);
      if (matcher3.matches()) {
         return new ShaderLine(6, matcher3.group(2), matcher3.group(3), str);
      }

      Matcher matcher4 = PATTERN_CONST_INT.matcher(str);
      if (matcher4.matches()) {
         return new ShaderLine(3, matcher4.group(1), matcher4.group(2), str);
      }

      Matcher matcher5 = PATTERN_CONST_FLOAT.matcher(str);
      if (matcher5.matches()) {
         return new ShaderLine(4, matcher5.group(1), matcher5.group(2), str);
      }

      Matcher matcher6 = PATTERN_CONST_BOOL.matcher(str);
      if (matcher6.matches()) {
         return new ShaderLine(5, matcher6.group(1), matcher6.group(2), str);
      }

      Matcher matcher7 = PATTERN_EXTENSION.matcher(str);
      if (matcher7.matches()) {
         return new ShaderLine(7, matcher7.group(1), matcher7.group(2), str);
      }

      Matcher matcher8 = PATTERN_CONST_VEC4.matcher(str);
      return matcher8.matches() ? new ShaderLine(8, matcher8.group(1), matcher8.group(2), str) : null;
   }

   public static int getIndex(String str, String str2, int n, int n2) {
      if (str.length() != str2.length() + 1) {
         return -1;
      }

      if (!str.startsWith(str2)) {
         return -1;
      }

      int n3 = str.charAt(str2.length()) - '0';
      return n3 >= n && n3 <= n2 ? n3 : -1;
   }

   public static int getShadowDepthIndex(String str) {
      return str.equals("shadow") ? 0 : (str.equals("watershadow") ? 1 : getIndex(str, "shadowtex", 0, 1));
   }

   public static int getShadowColorIndex(String str) {
      return str.equals("shadowcolor") ? 0 : getIndex(str, "shadowcolor", 0, 1);
   }

   public static int getDepthIndex(String str) {
      return getIndex(str, "depthtex", 0, 2);
   }

   public static int getColorIndex(String str) {
      int index = getIndex(str, "gaux", 1, 4);
      return index > 0 ? index + 3 : getIndex(str, "colortex", 4, 7);
   }

   public static boolean isDeferred(String str) {
      return PATTERN_DEFERRED_FSH.matcher(str).matches();
   }

   public static boolean isComposite(String str) {
      return PATTERN_COMPOSITE_FSH.matcher(str).matches();
   }

   public static boolean isFinal(String str) {
      return PATTERN_FINAL_FSH.matcher(str).matches();
   }

   public static boolean isValidDrawBuffers(String str) {
      return PATTERN_DRAW_BUFFERS.matcher(str).matches();
   }
}
