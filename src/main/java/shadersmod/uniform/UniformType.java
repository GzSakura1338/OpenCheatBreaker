package shadersmod.uniform;

import net.optifine.entity.model.anim.ExpressionType;
import net.optifine.entity.model.anim.IExpression;
import net.optifine.entity.model.anim.IExpressionBool;
import net.optifine.entity.model.anim.IExpressionFloat;

public enum UniformType {
    BOOL,
    INT,
    FLOAT;

    public ShaderUniformBase makeShaderUniform(String str) {
        switch (this) {
            case BOOL:
                return new ShaderUniformInt(str);
            case INT:
                return new ShaderUniformInt(str);
            case FLOAT:
                return new ShaderUniformFloat(str);
            default:
                throw new RuntimeException("Unknown uniform type: " + this);
        }
    }

    public void updateUniform(IExpression expression, ShaderUniformBase shaderUniformBase) {
        switch (this) {
            case BOOL:
                this.updateUniformBool((IExpressionBool)expression, (ShaderUniformInt)shaderUniformBase);
                return;
            case INT:
                this.updateUniformInt((IExpressionFloat)expression, (ShaderUniformInt)shaderUniformBase);
                return;
            case FLOAT:
                this.updateUniformFloat((IExpressionFloat)expression, (ShaderUniformFloat)shaderUniformBase);
                return;
            default:
                throw new RuntimeException("Unknown uniform type: " + this);
        }
    }

    public void updateUniformBool(IExpressionBool expressionBool, ShaderUniformInt shaderUniformInt) {
        boolean eval = expressionBool.eval();
        int n = eval ? 1 : 0;
        shaderUniformInt.setValue(n);
    }

    public void updateUniformInt(IExpressionFloat expressionFloat, ShaderUniformInt shaderUniformInt) {
        int eval = (int)expressionFloat.eval();
        shaderUniformInt.setValue(eval);
    }

    public void updateUniformFloat(IExpressionFloat expressionFloat, ShaderUniformFloat shaderUniformFloat) {
        float eval = expressionFloat.eval();
        shaderUniformFloat.setValue(eval);
    }

    public boolean matchesExpressionType(ExpressionType expressionType) {
        switch (this) {
            case BOOL:
                return expressionType == ExpressionType.BOOL;
            case INT:
                return expressionType == ExpressionType.FLOAT;
            case FLOAT:
                return expressionType == ExpressionType.FLOAT;
            default:
                throw new RuntimeException("Unknown uniform type: " + this);
        }
    }

    public static UniformType parse(String str) {
        UniformType[] values2 = values();

        for (int i = 0; i < values2.length; i++) {
            UniformType uniformType = values2[i];
            if (uniformType.name().toLowerCase().equals(str)) {
                return uniformType;
            }
        }

        return null;
    }
}
