package net.optifine.shaders.config;

import java.util.HashMap;
import java.util.Map;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;

public class ShaderOptionResolver implements IExpressionResolver {
   public Map<String, ExpressionShaderOptionSwitch> mapOptions = new HashMap<>();

   public ShaderOptionResolver(ShaderOption[] shaderOptions) {
      for (int i = 0; i < shaderOptions.length; i++) {
         ShaderOption shaderOption = shaderOptions[i];
         if (shaderOption instanceof ShaderOptionSwitch) {
            ShaderOptionSwitch shaderOptionSwitch = (ShaderOptionSwitch)shaderOption;
            ExpressionShaderOptionSwitch expressionShaderOptionSwitch = new ExpressionShaderOptionSwitch(shaderOptionSwitch);
            this.mapOptions.put(shaderOption.getName(), expressionShaderOptionSwitch);
         }
      }
   }

   @Override
   public IExpression getExpression(String str) {
      return this.mapOptions.get(str);
   }
}
