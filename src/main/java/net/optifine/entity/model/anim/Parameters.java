package net.optifine.entity.model.anim;

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
