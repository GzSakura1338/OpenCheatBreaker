package net.optifine.shaders.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.optifine.Config;
import net.optifine.expr.ExpressionFloatArrayCached;
import net.optifine.expr.ExpressionFloatCached;
import net.optifine.expr.ExpressionParser;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionBool;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.IExpressionFloatArray;
import net.optifine.expr.ParseException;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Program;
import net.optifine.shaders.SMCLog;
import net.optifine.shaders.ShaderUtils;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.uniform.CustomUniform;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderExpressionResolver;
import net.optifine.shaders.uniform.UniformType;
import net.optifine.util.StrUtils;

public class ShaderPackParser {
   public static Pattern PATTERN_VERSION = Pattern.compile("^\\s*#version\\s+.*$");
   public static Pattern PATTERN_INCLUDE = Pattern.compile("^\\s*#include\\s+\"([A-Za-z0-9_/\\.]+)\".*$");
   public static Set<String> setConstNames = makeSetConstNames();
   public static Map<String, Integer> mapAlphaFuncs = makeMapAlphaFuncs();
   public static Map<String, Integer> mapBlendFactors = makeMapBlendFactors();

   public static ShaderOption[] parseShaderPackOptions(IShaderPack shaderPack, String[] strs, List<Integer> list) {
      if (shaderPack == null) {
         return new ShaderOption[0];
      }

      HashMap map = new HashMap();
      collectShaderOptions(shaderPack, "/shaders", strs, map);

      for (int n : list) {
         String str = "/shaders/world" + n;
         collectShaderOptions(shaderPack, str, strs, map);
      }

      Collection values = map.values();
      ShaderOption[] toArray = (ShaderOption[])values.toArray(new ShaderOption[values.size()]);
      Comparator comparator = new Comparator<ShaderOption>() {
         public int compare(ShaderOption shaderOption, ShaderOption shaderOption2) {
            return shaderOption.getName().compareToIgnoreCase(shaderOption2.getName());
         }
      };
      Arrays.sort(toArray, comparator);
      return toArray;
   }

   public static void collectShaderOptions(IShaderPack shaderPack, String str, String[] strs, Map<String, ShaderOption> map) {
      for (int i = 0; i < strs.length; i++) {
         String str2 = strs[i];
         if (!str2.equals("")) {
            String str3 = str + "/" + str2 + ".vsh";
            String str4 = str + "/" + str2 + ".fsh";
            collectShaderOptions(shaderPack, str3, map);
            collectShaderOptions(shaderPack, str4, map);
         }
      }
   }

   public static void collectShaderOptions(IShaderPack shaderPack, String str, Map<String, ShaderOption> map) {
      String[] lines = getLines(shaderPack, str);

      for (int i = 0; i < lines.length; i++) {
         String str2 = lines[i];
         ShaderOption shaderOption = getShaderOption(str2, str);
         if (shaderOption != null && !shaderOption.getName().startsWith(ShaderMacros.getPrefixMacro()) && (!shaderOption.checkUsed() || isOptionUsed(shaderOption, lines))) {
            String name = shaderOption.getName();
            ShaderOption shaderOption2 = (ShaderOption)map.get(name);
            if (shaderOption2 != null) {
               if (!Config.equals(shaderOption2.getValueDefault(), shaderOption.getValueDefault())) {
                  Config.warn("Ambiguous shader option: " + shaderOption.getName());
                  Config.warn(" - in " + Config.arrayToString(shaderOption2.getPaths()) + ": " + shaderOption2.getValueDefault());
                  Config.warn(" - in " + Config.arrayToString(shaderOption.getPaths()) + ": " + shaderOption.getValueDefault());
                  shaderOption2.setEnabled(false);
               }

               if (shaderOption2.getDescription() == null || shaderOption2.getDescription().length() <= 0) {
                  shaderOption2.setDescription(shaderOption.getDescription());
               }

               shaderOption2.addPaths(shaderOption.getPaths());
            } else {
               map.put(name, shaderOption);
            }
         }
      }
   }

   public static boolean isOptionUsed(ShaderOption shaderOption, String[] strs) {
      for (int i = 0; i < strs.length; i++) {
         String str = strs[i];
         if (shaderOption.isUsedInLine(str)) {
            return true;
         }
      }

      return false;
   }

   public static String[] getLines(IShaderPack shaderPack, String str) {
      try {
         ArrayList list = new ArrayList();
         String loadFile2 = loadFile(str, shaderPack, 0, list, 0);
         if (loadFile2 == null) {
            return new String[0];
         }

         ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(loadFile2.getBytes());
         return Config.readLines(byteArrayInputStream);
      } catch (IOException e) {
         Config.dbg(e.getClass().getName() + ": " + e.getMessage());
         return new String[0];
      }
   }

   public static ShaderOption getShaderOption(String str, String str2) {
      ShaderOption shaderOption = null;
      if (shaderOption == null) {
         shaderOption = ShaderOptionSwitch.parseOption(str, str2);
      }

      if (shaderOption == null) {
         shaderOption = ShaderOptionVariable.parseOption(str, str2);
      }

      if (shaderOption != null) {
         return shaderOption;
      }

      if (shaderOption == null) {
         shaderOption = ShaderOptionSwitchConst.parseOption(str, str2);
      }

      if (shaderOption == null) {
         shaderOption = ShaderOptionVariableConst.parseOption(str, str2);
      }

      return shaderOption != null && setConstNames.contains(shaderOption.getName()) ? shaderOption : null;
   }

   public static Set<String> makeSetConstNames() {
      HashSet set = new HashSet();
      set.add("shadowMapResolution");
      set.add("shadowMapFov");
      set.add("shadowDistance");
      set.add("shadowDistanceRenderMul");
      set.add("shadowIntervalSize");
      set.add("generateShadowMipmap");
      set.add("generateShadowColorMipmap");
      set.add("shadowHardwareFiltering");
      set.add("shadowHardwareFiltering0");
      set.add("shadowHardwareFiltering1");
      set.add("shadowtex0Mipmap");
      set.add("shadowtexMipmap");
      set.add("shadowtex1Mipmap");
      set.add("shadowcolor0Mipmap");
      set.add("shadowColor0Mipmap");
      set.add("shadowcolor1Mipmap");
      set.add("shadowColor1Mipmap");
      set.add("shadowtex0Nearest");
      set.add("shadowtexNearest");
      set.add("shadow0MinMagNearest");
      set.add("shadowtex1Nearest");
      set.add("shadow1MinMagNearest");
      set.add("shadowcolor0Nearest");
      set.add("shadowColor0Nearest");
      set.add("shadowColor0MinMagNearest");
      set.add("shadowcolor1Nearest");
      set.add("shadowColor1Nearest");
      set.add("shadowColor1MinMagNearest");
      set.add("wetnessHalflife");
      set.add("drynessHalflife");
      set.add("eyeBrightnessHalflife");
      set.add("centerDepthHalflife");
      set.add("sunPathRotation");
      set.add("ambientOcclusionLevel");
      set.add("superSamplingLevel");
      set.add("noiseTextureResolution");
      return set;
   }

   public static ShaderProfile[] parseProfiles(Properties properties, ShaderOption[] shaderOptions) {
      String str = "profile.";
      ArrayList list = new ArrayList();

      for (Object obj : properties.keySet()) {
         String str2 = (String)obj;
         if (str2.startsWith(str)) {
            String substring = str2.substring(str.length());
            properties.getProperty(str2);
            HashSet set = new HashSet();
            ShaderProfile parseProfile2 = parseProfile(substring, properties, set, shaderOptions);
            if (parseProfile2 != null) {
               list.add(parseProfile2);
            }
         }
      }

      return list.size() <= 0 ? null : (ShaderProfile[])list.toArray(new ShaderProfile[list.size()]);
   }

   public static Map<String, IExpressionBool> parseProgramConditions(Properties properties, ShaderOption[] shaderOptions) {
      String str = "program.";
      Pattern compile = Pattern.compile("program\\.([^.]+)\\.enabled");
      HashMap map = new HashMap();

      for (Object obj : properties.keySet()) {
         String str2 = (String)obj;
         Matcher matcher = compile.matcher(str2);
         if (matcher.matches()) {
            String group = matcher.group(1);
            String trim = properties.getProperty(str2).trim();
            IExpressionBool parseOptionExpression2 = parseOptionExpression(trim, shaderOptions);
            if (parseOptionExpression2 == null) {
               SMCLog.severe("Error parsing program condition: " + str2);
            } else {
               map.put(group, parseOptionExpression2);
            }
         }
      }

      return map;
   }

   public static IExpressionBool parseOptionExpression(String str, ShaderOption[] shaderOptions) {
      try {
         ShaderOptionResolver shaderOptionResolver = new ShaderOptionResolver(shaderOptions);
         ExpressionParser expressionParser = new ExpressionParser(shaderOptionResolver);
         return expressionParser.parseBool(str);
      } catch (ParseException e) {
         SMCLog.warning(e.getClass().getName() + ": " + e.getMessage());
         return null;
      }
   }

   public static Set<String> parseOptionSliders(Properties properties, ShaderOption[] shaderOptions) {
      HashSet set = new HashSet();
      String property = properties.getProperty("sliders");
      if (property == null) {
         return set;
      }

      String[] tokenize = Config.tokenize(property, " ");

      for (int i = 0; i < tokenize.length; i++) {
         String str = tokenize[i];
         ShaderOption shaderOption = ShaderUtils.getShaderOption(str, shaderOptions);
         if (shaderOption == null) {
            Config.warn("Invalid shader option: " + str);
         } else {
            set.add(str);
         }
      }

      return set;
   }

   public static ShaderProfile parseProfile(String str, Properties properties, Set<String> set, ShaderOption[] shaderOptions) {
      String str2 = "profile.";
      String str3 = str2 + str;
      if (set.contains(str3)) {
         Config.warn("[Shaders] Profile already parsed: " + str);
         return null;
      }

      set.add(str);
      ShaderProfile shaderProfile = new ShaderProfile(str);
      String property = properties.getProperty(str3);
      String[] tokenize = Config.tokenize(property, " ");

      for (int i = 0; i < tokenize.length; i++) {
         String str4 = tokenize[i];
         if (str4.startsWith(str2)) {
            String substring = str4.substring(str2.length());
            ShaderProfile parseProfile2 = parseProfile(substring, properties, set, shaderOptions);
            if (shaderProfile != null) {
               shaderProfile.addOptionValues(parseProfile2);
               shaderProfile.addDisabledPrograms(parseProfile2.getDisabledPrograms());
            }
         } else {
            String[] tokenize2 = Config.tokenize(str4, ":=");
            if (tokenize2.length == 1) {
               String str5 = tokenize2[0];
               boolean flag = true;
               if (str5.startsWith("!")) {
                  flag = false;
                  str5 = str5.substring(1);
               }

               String str6 = "program.";
               if (str5.startsWith(str6)) {
                  String substring = str5.substring(str6.length());
                  if (!Shaders.isProgramPath(substring)) {
                     Config.warn("Invalid program: " + substring + " in profile: " + shaderProfile.getName());
                  } else if (flag) {
                     shaderProfile.removeDisabledProgram(substring);
                  } else {
                     shaderProfile.addDisabledProgram(substring);
                  }
               } else {
                  ShaderOption shaderOption = ShaderUtils.getShaderOption(str5, shaderOptions);
                  if (!(shaderOption instanceof ShaderOptionSwitch)) {
                     Config.warn("[Shaders] Invalid option: " + str5);
                  } else {
                     shaderProfile.addOptionValue(str5, String.valueOf(flag));
                     shaderOption.setVisible(true);
                  }
               }
            } else if (tokenize2.length != 2) {
               Config.warn("[Shaders] Invalid option value: " + str4);
            } else {
               String str5 = tokenize2[0];
               String str6 = tokenize2[1];
               ShaderOption shaderOption = ShaderUtils.getShaderOption(str5, shaderOptions);
               if (shaderOption == null) {
                  Config.warn("[Shaders] Invalid option: " + str4);
               } else if (!shaderOption.isValidValue(str6)) {
                  Config.warn("[Shaders] Invalid value: " + str4);
               } else {
                  shaderOption.setVisible(true);
                  shaderProfile.addOptionValue(str5, str6);
               }
            }
         }
      }

      return shaderProfile;
   }

   public static Map<String, ScreenShaderOptions> parseGuiScreens(Properties properties, ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions) {
      HashMap map = new HashMap();
      parseGuiScreen("screen", properties, map, shaderProfiles, shaderOptions);
      return map.isEmpty() ? null : map;
   }

   public static boolean parseGuiScreen(String str, Properties properties, Map<String, ScreenShaderOptions> map, ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions) {
      String property = properties.getProperty(str);
      if (property == null) {
         return false;
      }

      ArrayList list = new ArrayList();
      HashSet set = new HashSet();
      String[] tokenize = Config.tokenize(property, " ");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         if (str2.equals("<empty>")) {
            list.add((ShaderOption)null);
         } else if (set.contains(str2)) {
            Config.warn("[Shaders] Duplicate option: " + str2 + ", key: " + str);
         } else {
            set.add(str2);
            if (str2.equals("<profile>")) {
               if (shaderProfiles == null) {
                  Config.warn("[Shaders] Option profile can not be used, no profiles defined: " + str2 + ", key: " + str);
               } else {
                  ShaderOptionProfile shaderOptionProfile = new ShaderOptionProfile(shaderProfiles, shaderOptions);
                  list.add(shaderOptionProfile);
               }
            } else if (str2.equals("*")) {
               ShaderOptionRest shaderOptionRest = new ShaderOptionRest("<rest>");
               list.add(shaderOptionRest);
            } else if (str2.startsWith("[") && str2.endsWith("]")) {
               String removePrefixSuffix = StrUtils.removePrefixSuffix(str2, "[", "]");
               if (!removePrefixSuffix.matches("^[a-zA-Z0-9_]+$")) {
                  Config.warn("[Shaders] Invalid screen: " + str2 + ", key: " + str);
               } else if (!parseGuiScreen("screen." + removePrefixSuffix, properties, map, shaderProfiles, shaderOptions)) {
                  Config.warn("[Shaders] Invalid screen: " + str2 + ", key: " + str);
               } else {
                  ShaderOptionScreen shaderOptionScreen = new ShaderOptionScreen(removePrefixSuffix);
                  list.add(shaderOptionScreen);
               }
            } else {
               ShaderOption shaderOption = ShaderUtils.getShaderOption(str2, shaderOptions);
               if (shaderOption == null) {
                  Config.warn("[Shaders] Invalid option: " + str2 + ", key: " + str);
                  list.add((ShaderOption)null);
               } else {
                  shaderOption.setVisible(true);
                  list.add(shaderOption);
               }
            }
         }
      }

      ShaderOption[] toArray = (ShaderOption[])list.toArray(new ShaderOption[list.size()]);
      String property2 = properties.getProperty(str + ".columns");
      int parseInt = Config.parseInt(property2, 2);
      ScreenShaderOptions screenShaderOptions = new ScreenShaderOptions(str, toArray, parseInt);
      map.put(str, screenShaderOptions);
      return true;
   }

   public static BufferedReader resolveIncludes(BufferedReader bufferedReader, String str, IShaderPack shaderPack, int n, List<String> list, int n2) throws IOException {
      String str2 = "/";
      int lastIndexOf = str.lastIndexOf("/");
      if (lastIndexOf >= 0) {
         str2 = str.substring(0, lastIndexOf);
      }

      CharArrayWriter charArrayWriter = new CharArrayWriter();
      int n3 = -1;
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      int n4 = 1;

      while (true) {
         String readLine = bufferedReader.readLine();
         if (readLine == null) {
            char[] toCharArray = charArrayWriter.toCharArray();
            if (n3 >= 0 && linkedHashSet.size() > 0) {
               StringBuilder sb = new StringBuilder();

               for (ShaderMacro shaderMacro : (Iterable<ShaderMacro>)linkedHashSet) {
                  sb.append("#define ");
                  sb.append(shaderMacro.getName());
                  sb.append(" ");
                  sb.append(shaderMacro.getValue());
                  sb.append("\n");
               }

               String str3 = sb.toString();
               StringBuilder sb2 = new StringBuilder(new String(toCharArray));
               sb2.insert(n3, str3);
               String str4 = sb2.toString();
               toCharArray = str4.toCharArray();
            }

            CharArrayReader charArrayReader = new CharArrayReader(toCharArray);
            return new BufferedReader(charArrayReader);
         }

         if (n3 < 0) {
            Matcher matcher = PATTERN_VERSION.matcher(readLine);
            if (matcher.matches()) {
               String str3 = ShaderMacros.getFixedMacroLines() + ShaderMacros.getOptionMacroLines();
               String str4 = readLine + "\n" + str3;
               String str5 = "#line " + (n4 + 1) + " " + n;
               readLine = str4 + str5;
               n3 = charArrayWriter.size() + str4.length();
            }
         }

         Matcher matcher = PATTERN_INCLUDE.matcher(readLine);
         if (matcher.matches()) {
            String group = matcher.group(1);
            boolean startsWith = group.startsWith("/");
            String str3 = startsWith ? "/shaders" + group : str2 + "/" + group;
            if (!list.contains(str3)) {
               list.add(str3);
            }

            int n5 = list.indexOf(str3) + 1;
            readLine = loadFile(str3, shaderPack, n5, list, n2);
            if (readLine == null) {
               throw new IOException("Included file not found: " + str);
            }

            if (readLine.endsWith("\n")) {
               readLine = readLine.substring(0, readLine.length() - 1);
            }

            String str4 = "#line 1 " + n5 + "\n";
            if (readLine.startsWith("#version ")) {
               str4 = "";
            }

            readLine = str4 + readLine + "\n#line " + (n4 + 1) + " " + n;
         }

         if (n3 >= 0 && readLine.contains(ShaderMacros.getPrefixMacro())) {
            ShaderMacro[] findMacros2 = findMacros(readLine, ShaderMacros.getExtensions());

            for (int i = 0; i < findMacros2.length; i++) {
               ShaderMacro shaderMacro = findMacros2[i];
               linkedHashSet.add(shaderMacro);
            }
         }

         charArrayWriter.write(readLine);
         charArrayWriter.write("\n");
         n4++;
      }
   }

   public static ShaderMacro[] findMacros(String str, ShaderMacro[] shaderMacros) {
      ArrayList list = new ArrayList();

      for (int i = 0; i < shaderMacros.length; i++) {
         ShaderMacro shaderMacro = shaderMacros[i];
         if (str.contains(shaderMacro.getName())) {
            list.add(shaderMacro);
         }
      }

      return (ShaderMacro[])list.toArray(new ShaderMacro[list.size()]);
   }

   public static String loadFile(String str, IShaderPack shaderPack, int n, List<String> list, int n2) throws IOException {
      if (n2 >= 10) {
         throw new IOException("#include depth exceeded: " + n2 + ", file: " + str);
      }

      n2++;
      InputStream resourceAsStream = shaderPack.getResourceAsStream(str);
      if (resourceAsStream == null) {
         return null;
      }

      InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "ASCII");
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      bufferedReader = resolveIncludes(bufferedReader, str, shaderPack, n, list, n2);
      CharArrayWriter charArrayWriter = new CharArrayWriter();

      while (true) {
         String readLine = bufferedReader.readLine();
         if (readLine == null) {
            return charArrayWriter.toString();
         }

         charArrayWriter.write(readLine);
         charArrayWriter.write("\n");
      }
   }

   public static CustomUniforms parseCustomUniforms(Properties properties) {
      String str = "uniform";
      String str2 = "variable";
      String str3 = str + ".";
      String str4 = str2 + ".";
      HashMap map = new HashMap();
      ArrayList list = new ArrayList();

      for (Object obj : properties.keySet()) {
         String str5 = (String)obj;
         String[] tokenize = Config.tokenize(str5, ".");
         if (tokenize.length == 3) {
            String str6 = tokenize[0];
            String str7 = tokenize[1];
            String str8 = tokenize[2];
            String trim = properties.getProperty(str5).trim();
            if (map.containsKey(str8)) {
               SMCLog.warning("Expression already defined: " + str8);
            } else if (str6.equals(str) || str6.equals(str2)) {
               SMCLog.info("Custom " + str6 + ": " + str8);
               CustomUniform parseCustomUniform2 = parseCustomUniform(str6, str8, str7, trim, map);
               if (parseCustomUniform2 != null) {
                  map.put(str8, parseCustomUniform2.getExpression());
                  if (!str6.equals(str2)) {
                     list.add(parseCustomUniform2);
                  }
               }
            }
         }
      }

      if (list.size() <= 0) {
         return null;
      }

      CustomUniform[] toArray = (CustomUniform[])list.toArray(new CustomUniform[list.size()]);
      return new CustomUniforms(toArray, map);
   }

   public static CustomUniform parseCustomUniform(String str, String str2, String str3, String str4, Map<String, IExpression> map) {
      try {
         UniformType parse = UniformType.parse(str3);
         if (parse == null) {
            SMCLog.warning("Unknown " + str + " type: " + parse);
            return null;
         } else {
            ShaderExpressionResolver shaderExpressionResolver = new ShaderExpressionResolver(map);
            ExpressionParser expressionParser = new ExpressionParser(shaderExpressionResolver);
            IExpression parse2 = expressionParser.parse(str4);
            ExpressionType expressionType = parse2.getExpressionType();
            if (!parse.matchesExpressionType(expressionType)) {
               SMCLog.warning("Expression type does not match " + str + " type, expression: " + expressionType + ", " + str + ": " + parse + " " + str2);
               return null;
            } else {
               parse2 = makeExpressionCached(parse2);
               return new CustomUniform(str2, parse, parse2);
            }
         }
      } catch (ParseException e) {
         SMCLog.warning(e.getClass().getName() + ": " + e.getMessage());
         return null;
      }
   }

   public static IExpression makeExpressionCached(IExpression expression) {
      return expression instanceof IExpressionFloat
         ? new ExpressionFloatCached((IExpressionFloat)expression)
         : (expression instanceof IExpressionFloatArray ? new ExpressionFloatArrayCached((IExpressionFloatArray)expression) : expression);
   }

   public static void parseAlphaStates(Properties properties) {
      for (Object obj : properties.keySet()) {
         String str = (String)obj;
         String[] tokenize = Config.tokenize(str, ".");
         if (tokenize.length == 2) {
            String str2 = tokenize[0];
            String str3 = tokenize[1];
            if (str2.equals("alphaTest")) {
               Program program = Shaders.getProgram(str3);
               if (program == null) {
                  SMCLog.severe("Invalid program name: " + str3);
               } else {
                  String trim = properties.getProperty(str).trim();
                  GlAlphaState parseAlphaState2 = parseAlphaState(trim);
                  if (parseAlphaState2 != null) {
                     program.setAlphaState(parseAlphaState2);
                  }
               }
            }
         }
      }
   }

   public static GlAlphaState parseAlphaState(String str) {
      String[] tokenize = Config.tokenize(str, " ");
      if (tokenize.length == 1) {
         String str2 = tokenize[0];
         if (str2.equals("off") || str2.equals("false")) {
            return new GlAlphaState(false);
         }
      } else if (tokenize.length == 2) {
         String str2 = tokenize[0];
         String str3 = tokenize[1];
         Integer integer = mapAlphaFuncs.get(str2);
         float parseFloat = Config.parseFloat(str3, -1.0F);
         if (integer != null && parseFloat >= 0.0F) {
            return new GlAlphaState(true, integer, parseFloat);
         }
      }

      SMCLog.severe("Invalid alpha test: " + str);
      return null;
   }

   public static void parseBlendStates(Properties properties) {
      for (Object obj : properties.keySet()) {
         String str = (String)obj;
         String[] tokenize = Config.tokenize(str, ".");
         if (tokenize.length == 2) {
            String str2 = tokenize[0];
            String str3 = tokenize[1];
            if (str2.equals("blend")) {
               Program program = Shaders.getProgram(str3);
               if (program == null) {
                  SMCLog.severe("Invalid program name: " + str3);
               } else {
                  String trim = properties.getProperty(str).trim();
                  GlBlendState parseBlendState2 = parseBlendState(trim);
                  if (parseBlendState2 != null) {
                     program.setBlendState(parseBlendState2);
                  }
               }
            }
         }
      }
   }

   public static GlBlendState parseBlendState(String str) {
      String[] tokenize = Config.tokenize(str, " ");
      if (tokenize.length == 1) {
         String str2 = tokenize[0];
         if (str2.equals("off") || str2.equals("false")) {
            return new GlBlendState(false);
         }
      } else if (tokenize.length == 2 || tokenize.length == 4) {
         String str2 = tokenize[0];
         String str3 = tokenize[1];
         String str4 = str2;
         String str5 = str3;
         if (tokenize.length == 4) {
            str4 = tokenize[2];
            str5 = tokenize[3];
         }

         Integer integer = mapBlendFactors.get(str2);
         Integer integer2 = mapBlendFactors.get(str3);
         Integer integer3 = mapBlendFactors.get(str4);
         Integer integer4 = mapBlendFactors.get(str5);
         if (integer != null && integer2 != null && integer3 != null && integer4 != null) {
            return new GlBlendState(true, integer, integer2, integer3, integer4);
         }
      }

      SMCLog.severe("Invalid blend mode: " + str);
      return null;
   }

   public static void parseRenderScales(Properties properties) {
      for (Object obj : properties.keySet()) {
         String str = (String)obj;
         String[] tokenize = Config.tokenize(str, ".");
         if (tokenize.length == 2) {
            String str2 = tokenize[0];
            String str3 = tokenize[1];
            if (str2.equals("scale")) {
               Program program = Shaders.getProgram(str3);
               if (program == null) {
                  SMCLog.severe("Invalid program name: " + str3);
               } else {
                  String trim = properties.getProperty(str).trim();
                  RenderScale parseRenderScale2 = parseRenderScale(trim);
                  if (parseRenderScale2 != null) {
                     program.setRenderScale(parseRenderScale2);
                  }
               }
            }
         }
      }
   }

   public static RenderScale parseRenderScale(String str) {
      String[] tokenize = Config.tokenize(str, " ");
      float parseFloat = Config.parseFloat(tokenize[0], -1.0F);
      float f = 0.0F;
      float f2 = 0.0F;
      if (tokenize.length > 1) {
         if (tokenize.length != 3) {
            SMCLog.severe("Invalid render scale: " + str);
            return null;
         }

         f = Config.parseFloat(tokenize[1], -1.0F);
         f2 = Config.parseFloat(tokenize[2], -1.0F);
      }

      if (Config.between(parseFloat, 0.0F, 1.0F) && Config.between(f, 0.0F, 1.0F) && Config.between(f2, 0.0F, 1.0F)) {
         return new RenderScale(parseFloat, f, f2);
      }

      SMCLog.severe("Invalid render scale: " + str);
      return null;
   }

   public static void parseBuffersFlip(Properties properties) {
      for (Object obj : properties.keySet()) {
         String str = (String)obj;
         String[] tokenize = Config.tokenize(str, ".");
         if (tokenize.length == 3) {
            String str2 = tokenize[0];
            String str3 = tokenize[1];
            String str4 = tokenize[2];
            if (str2.equals("flip")) {
               Program program = Shaders.getProgram(str3);
               if (program == null) {
                  SMCLog.severe("Invalid program name: " + str3);
               } else {
                  Boolean[] buffersFlip = program.getBuffersFlip();
                  int bufferIndexFromString = Shaders.getBufferIndexFromString(str4);
                  if (bufferIndexFromString >= 0 && bufferIndexFromString < buffersFlip.length) {
                     String trim = properties.getProperty(str).trim();
                     Boolean parseBoolean = Config.parseBoolean(trim, (Boolean)null);
                     if (parseBoolean == null) {
                        SMCLog.severe("Invalid boolean value: " + trim);
                     } else {
                        buffersFlip[bufferIndexFromString] = parseBoolean;
                     }
                  } else {
                     SMCLog.severe("Invalid buffer name: " + str4);
                  }
               }
            }
         }
      }
   }

   public static Map<String, Integer> makeMapAlphaFuncs() {
      HashMap map = new HashMap();
      map.put("NEVER", new Integer(512));
      map.put("LESS", new Integer(513));
      map.put("EQUAL", new Integer(514));
      map.put("LEQUAL", new Integer(515));
      map.put("GREATER", new Integer(516));
      map.put("NOTEQUAL", new Integer(517));
      map.put("GEQUAL", new Integer(518));
      map.put("ALWAYS", new Integer(519));
      return Collections.unmodifiableMap(map);
   }

   public static Map<String, Integer> makeMapBlendFactors() {
      HashMap map = new HashMap();
      map.put("ZERO", new Integer(0));
      map.put("ONE", new Integer(1));
      map.put("SRC_COLOR", new Integer(768));
      map.put("ONE_MINUS_SRC_COLOR", new Integer(769));
      map.put("DST_COLOR", new Integer(774));
      map.put("ONE_MINUS_DST_COLOR", new Integer(775));
      map.put("SRC_ALPHA", new Integer(770));
      map.put("ONE_MINUS_SRC_ALPHA", new Integer(771));
      map.put("DST_ALPHA", new Integer(772));
      map.put("ONE_MINUS_DST_ALPHA", new Integer(773));
      map.put("CONSTANT_COLOR", new Integer(32769));
      map.put("ONE_MINUS_CONSTANT_COLOR", new Integer(32770));
      map.put("CONSTANT_ALPHA", new Integer(32771));
      map.put("ONE_MINUS_CONSTANT_ALPHA", new Integer(32772));
      map.put("SRC_ALPHA_SATURATE", new Integer(776));
      return Collections.unmodifiableMap(map);
   }
}
