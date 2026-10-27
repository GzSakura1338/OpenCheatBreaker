package net.optifine.expr;

import net.optifine.shaders.uniform.Smoother;

public class FunctionFloat implements IExpressionFloat {
   public int smoothId = -1;
   public FunctionType type;
   public IExpression[] arguments;

   public FunctionFloat(FunctionType functionType, IExpression[] expressions) {
      this.type = functionType;
      this.arguments = expressions;
   }

   @Override
   public float eval() {
      IExpression[] arguments = this.arguments;
      switch (this.type) {
         case SMOOTH:
            IExpression expression = arguments[0];
            if (!(expression instanceof ConstantFloat)) {
               float evalFloat2 = evalFloat(arguments, 0);
               float f = arguments.length > 1 ? evalFloat(arguments, 1) : 1.0F;
               float f2 = arguments.length > 2 ? evalFloat(arguments, 2) : f;
               if (this.smoothId < 0) {
                  this.smoothId = Smoother.getNextId();
               }

               return Smoother.getSmoothValue(this.smoothId, evalFloat2, f, f2);
            }
         default:
            return this.type.evalFloat(this.arguments);
      }
   }

   public static float evalFloat(IExpression[] expressions, int n) {
      IExpressionFloat expressionFloat = (IExpressionFloat)expressions[n];
      return expressionFloat.eval();
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   @Override
   public String toString() {
      return "" + this.type + "()";
   }
}
