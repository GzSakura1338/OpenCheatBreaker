package net.optifine.shaders.uniform;

import java.util.ArrayList;
import java.util.Map;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionCached;

public class CustomUniforms {
   public CustomUniform[] uniforms;
   public IExpressionCached[] expressionsCached;

   public CustomUniforms(CustomUniform[] customUniforms, Map<String, IExpression> map) {
      this.uniforms = customUniforms;
      ArrayList list = new ArrayList();

      for (String str : map.keySet()) {
         IExpression expression = (IExpression)map.get(str);
         if (expression instanceof IExpressionCached) {
            IExpressionCached expressionCached = (IExpressionCached)expression;
            list.add(expressionCached);
         }
      }

      this.expressionsCached = (IExpressionCached[])list.toArray(new IExpressionCached[list.size()]);
   }

   public void setProgram(int n) {
      for (int i = 0; i < this.uniforms.length; i++) {
         CustomUniform customUniform = this.uniforms[i];
         customUniform.setProgram(n);
      }
   }

   public void update() {
      this.resetCache();

      for (int i = 0; i < this.uniforms.length; i++) {
         CustomUniform customUniform = this.uniforms[i];
         customUniform.update();
      }
   }

   public void resetCache() {
      for (int i = 0; i < this.expressionsCached.length; i++) {
         IExpressionCached expressionCached = this.expressionsCached[i];
         expressionCached.reset();
      }
   }

   public void reset() {
      for (int i = 0; i < this.uniforms.length; i++) {
         CustomUniform customUniform = this.uniforms[i];
         customUniform.reset();
      }
   }
}
