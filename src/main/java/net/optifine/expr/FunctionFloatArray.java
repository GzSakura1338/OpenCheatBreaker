package net.optifine.expr;

public class FunctionFloatArray implements IExpressionFloatArray {
   public FunctionType type;
   public IExpression[] arguments;

   public FunctionFloatArray(FunctionType functionType, IExpression[] expressions) {
      this.type = functionType;
      this.arguments = expressions;
   }

   @Override
   public float[] eval() {
      return this.type.evalFloatArray(this.arguments);
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT_ARRAY;
   }

   @Override
   public String toString() {
      return "" + this.type + "()";
   }
}
