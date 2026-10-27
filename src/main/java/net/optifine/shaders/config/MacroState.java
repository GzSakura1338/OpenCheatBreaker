package net.optifine.shaders.config;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.optifine.Config;
import net.optifine.expr.ExpressionParser;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionBool;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.ParseException;

public class MacroState {
   public boolean active = true;
   public Deque<Boolean> dequeState = new ArrayDeque<>();
   public Deque<Boolean> dequeResolved = new ArrayDeque<>();
   public Map<String, String> mapMacroValues = new HashMap<>();
   public static Pattern PATTERN_DIRECTIVE = Pattern.compile("\\s*#\\s*(\\w+)\\s*(.*)");
   public static Pattern PATTERN_DEFINED = Pattern.compile("defined\\s+(\\w+)");
   public static Pattern PATTERN_DEFINED_FUNC = Pattern.compile("defined\\s*\\(\\s*(\\w+)\\s*\\)");
   public static Pattern PATTERN_MACRO = Pattern.compile("(\\w+)");
   public static final String DEFINE = "define";
   public static final String UNDEF = "undef";
   public static final String IFDEF = "ifdef";
   public static final String IFNDEF = "ifndef";
   public static final String TEXT_IF = "if";
   public static final String ELSE = "else";
   public static final String ELIF = "elif";
   public static final String ENDIF = "endif";
   public static List<String> MACRO_NAMES = Arrays.asList("define", "undef", "ifdef", "ifndef", "if", "else", "elif", "endif");

   public boolean processLine(String str) {
      Matcher matcher = PATTERN_DIRECTIVE.matcher(str);
      if (!matcher.matches()) {
         return this.active;
      }

      String group = matcher.group(1);
      String group2 = matcher.group(2);
      int indexOf = group2.indexOf("//");
      if (indexOf >= 0) {
         group2 = group2.substring(0, indexOf);
      }

      boolean active = this.active;
      this.processMacro(group, group2);
      this.active = !this.dequeState.contains(Boolean.FALSE);
      return this.active || active;
   }

   public static boolean isMacroLine(String str) {
      Matcher matcher = PATTERN_DIRECTIVE.matcher(str);
      if (!matcher.matches()) {
         return false;
      }

      String group = matcher.group(1);
      return MACRO_NAMES.contains(group);
   }

   public void processMacro(String str, String str2) {
      StringTokenizer stringTokenizer = new StringTokenizer(str2, " \t");
      String str3 = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
      String str4 = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken("").trim() : "";
      if (str.equals("define")) {
         this.mapMacroValues.put(str3, str4);
      } else if (str.equals("undef")) {
         this.mapMacroValues.remove(str3);
      } else if (str.equals("ifdef")) {
         boolean containsKey = this.mapMacroValues.containsKey(str3);
         this.dequeState.add(containsKey);
         this.dequeResolved.add(containsKey);
      } else if (str.equals("ifndef")) {
         boolean flag = !this.mapMacroValues.containsKey(str3);
         this.dequeState.add(flag);
         this.dequeResolved.add(flag);
      } else if (str.equals("if")) {
         boolean eval = this.eval(str2);
         this.dequeState.add(eval);
         this.dequeResolved.add(eval);
      } else if (!this.dequeState.isEmpty()) {
         if (str.equals("elif")) {
            boolean removeLast = this.dequeState.removeLast();
            boolean removeLast2 = this.dequeResolved.removeLast();
            if (removeLast2) {
               this.dequeState.add(false);
               this.dequeResolved.add(removeLast2);
            } else {
               boolean eval = this.eval(str2);
               this.dequeState.add(eval);
               this.dequeResolved.add(eval);
            }
         } else if (str.equals("else")) {
            boolean removeLast = this.dequeState.removeLast();
            boolean removeLast2 = this.dequeResolved.removeLast();
            boolean flag = !removeLast2;
            this.dequeState.add(flag);
            this.dequeResolved.add(true);
         } else if (str.equals("endif")) {
            this.dequeState.removeLast();
            this.dequeResolved.removeLast();
         }
      }
   }

   public boolean eval(String str) {
      Matcher matcher = PATTERN_DEFINED.matcher(str);
      str = matcher.replaceAll("defined_$1");
      Matcher matcher2 = PATTERN_DEFINED_FUNC.matcher(str);
      str = matcher2.replaceAll("defined_$1");
      boolean flag = false;
      int n = 0;

      do {
         flag = false;
         Matcher matcher3 = PATTERN_MACRO.matcher(str);

         while (matcher3.find()) {
            String group = matcher3.group();
            if (group.length() > 0) {
               char charAt = group.charAt(0);
               if ((Character.isLetter(charAt) || charAt == '_') && this.mapMacroValues.containsKey(group)) {
                  String str2 = this.mapMacroValues.get(group);
                  if (str2 == null) {
                     str2 = "1";
                  }

                  int start = matcher3.start();
                  int end = matcher3.end();
                  str = str.substring(0, start) + " " + str2 + " " + str.substring(end);
                  flag = true;
                  n++;
                  break;
               }
            }
         }
      } while (flag && n < 100);

      if (n >= 100) {
         Config.warn("Too many iterations: " + n + ", when resolving: " + str);
         return true;
      }

      try {
         MacroExpressionResolver macroExpressionResolver = new MacroExpressionResolver(this.mapMacroValues);
         ExpressionParser expressionParser = new ExpressionParser(macroExpressionResolver);
         IExpression parse = expressionParser.parse(str);
         if (parse.getExpressionType() == ExpressionType.BOOL) {
            IExpressionBool expressionBool = (IExpressionBool)parse;
            return expressionBool.eval();
         } else if (parse.getExpressionType() == ExpressionType.FLOAT) {
            IExpressionFloat expressionFloat = (IExpressionFloat)parse;
            float eval = expressionFloat.eval();
            return eval != 0.0F;
         } else {
            throw new ParseException("Not a boolean or float expression: " + parse.getExpressionType());
         }
      } catch (ParseException e) {
         Config.warn("Invalid macro expression: " + str);
         Config.warn("Error: " + e.getMessage());
         return false;
      }
   }
}
