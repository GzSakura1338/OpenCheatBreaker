package net.optifine.entity.model.anim;

public class FunctionBool implements IExpressionBool {
    public FunctionType type;
    public IExpression[] arguments;

    public FunctionBool(FunctionType functionType, IExpression[] expressions) {
        this.type = functionType;
        this.arguments = expressions;
    }

    @Override
    public boolean eval() {
        return this.type.evalBool(this.arguments);
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.BOOL;
    }

    @Override
    public String toString() {
        return "" + this.type + "()";
    }
}
