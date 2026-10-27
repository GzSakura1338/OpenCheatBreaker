package net.optifine.expr;

public class ExpressionFloatArrayCached implements IExpressionFloatArray, IExpressionCached {
   public IExpressionFloatArray expression;
   public boolean cached;
   public float[] value;

   public ExpressionFloatArrayCached(IExpressionFloatArray expressionFloatArray) {
      this.expression = expressionFloatArray;
   }

   @Override
   public float[] eval() {
      if (!this.cached) {
         this.value = this.expression.eval();
         this.cached = true;
      }

      return this.value;
   }

   @Override
   public void reset() {
      this.cached = false;
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   @Override
   public String toString() {
      return "cached(" + this.expression + ")";
   }
}
