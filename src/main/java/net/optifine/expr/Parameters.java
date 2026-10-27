package net.optifine.expr;

public class Parameters implements IParameters {
   public ExpressionType[] parameterTypes;

   public Parameters(ExpressionType[] expressionTypes) {
      this.parameterTypes = expressionTypes;
   }

   @Override
   public ExpressionType[] getParameterTypes(IExpression[] expressions) {
      return this.parameterTypes;
   }
}
