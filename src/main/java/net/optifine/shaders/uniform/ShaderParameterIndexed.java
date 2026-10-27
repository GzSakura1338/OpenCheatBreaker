package net.optifine.shaders.uniform;

import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpressionFloat;

public class ShaderParameterIndexed implements IExpressionFloat {
   public ShaderParameterFloat type;
   public int index1;
   public int index2;

   public ShaderParameterIndexed(ShaderParameterFloat shaderParameterFloat) {
      this(shaderParameterFloat, 0, 0);
   }

   public ShaderParameterIndexed(ShaderParameterFloat shaderParameterFloat, int n) {
      this(shaderParameterFloat, n, 0);
   }

   public ShaderParameterIndexed(ShaderParameterFloat shaderParameterFloat, int n, int n2) {
      this.type = shaderParameterFloat;
      this.index1 = n;
      this.index2 = n2;
   }

   @Override
   public float eval() {
      return this.type.eval(this.index1, this.index2);
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   @Override
   public String toString() {
      return this.type.getIndexNames1() == null
         ? "" + this.type
         : (
            this.type.getIndexNames2() == null
               ? "" + this.type + "." + this.type.getIndexNames1()[this.index1]
               : "" + this.type + "." + this.type.getIndexNames1()[this.index1] + "." + this.type.getIndexNames2()[this.index2]
         );
   }
}
