package net.optifine.shaders.config;

import java.util.Map;
import net.optifine.Config;
import net.optifine.expr.ConstantFloat;
import net.optifine.expr.FunctionBool;
import net.optifine.expr.FunctionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;

public class MacroExpressionResolver implements IExpressionResolver {
   public Map<String, String> mapMacroValues = null;

   public MacroExpressionResolver(Map<String, String> map) {
      this.mapMacroValues = map;
   }

   @Override
   public IExpression getExpression(String str) {
      String str2 = "defined_";
      if (str.startsWith(str2)) {
         String substring = str.substring(str2.length());
         return this.mapMacroValues.containsKey(substring)
            ? new FunctionBool(FunctionType.TRUE, (IExpression[])null)
            : new FunctionBool(FunctionType.FALSE, (IExpression[])null);
      }

      while (this.mapMacroValues.containsKey(str)) {
         String str3 = this.mapMacroValues.get(str);
         if (str3 == null || str3.equals(str)) {
            break;
         }

         str = str3;
      }

      int parseInt = Config.parseInt(str, Integer.MIN_VALUE);
      if (parseInt == Integer.MIN_VALUE) {
         Config.warn("Unknown macro value: " + str);
         return new ConstantFloat(0.0F);
      } else {
         return new ConstantFloat(parseInt);
      }
   }
}
