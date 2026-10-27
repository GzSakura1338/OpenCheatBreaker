package net.optifine.expr;

public class ConstantFloat implements IExpressionFloat {
   public float value;

   public ConstantFloat(float f) {
      this.value = f;
   }

   @Override
   public float eval() {
      return this.value;
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   @Override
   public String toString() {
      return "" + this.value;
   }
}
