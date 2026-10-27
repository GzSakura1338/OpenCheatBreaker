package net.optifine.expr;

public class ExpressionFloatCached implements IExpressionFloat, IExpressionCached {
   public IExpressionFloat expression;
   public boolean cached;
   public float value;

   public ExpressionFloatCached(IExpressionFloat expressionFloat) {
      this.expression = expressionFloat;
   }

   @Override
   public float eval() {
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
