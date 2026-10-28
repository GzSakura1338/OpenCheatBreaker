package net.optifine.entity.model.anim;

public class FunctionFloat implements IExpressionFloat {
    public FunctionType type;
    public IExpression[] arguments;

    public FunctionFloat(FunctionType functionType, IExpression[] expressions) {
        this.type = functionType;
        this.arguments = expressions;
    }

    @Override
    public float eval() {
        return this.type.evalFloat(this.arguments);
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
